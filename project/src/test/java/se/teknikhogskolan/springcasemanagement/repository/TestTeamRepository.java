package se.teknikhogskolan.springcasemanagement.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hsqldb.types.Collation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.repository.CrudRepository;

import se.teknikhogskolan.springcasemanagement.model.Team;

public final class TestTeamRepository {

    private final String projectPackage = "se.teknikhogskolan.springcasemanagement";
    private Team team;
    private List<Team> teamsInDb;

    @Before
    public void setUp() {
        this.team = new Team("Test");
        teamsInDb = new ArrayList<>();
    }

    @Test
    public void canSaveTeam() {
        executeVoid(teamRepository -> teamRepository.save(team));
        deleteTeam(team);
    }

    @Test
    public void canGetTeamById() {
        Team teamFromDb = execute(teamRepository -> {
            team = teamRepository.save(team);
            return teamRepository.findOne(team.getId());
        });

        assertEquals(team, teamFromDb);
        deleteTeam(team);
    }

    @Test
    public void canGetTeamByName() {
        String name = "New name";
        Team teamFromDb = execute(teamRepository -> {
            team.setName(name);
            team = teamRepository.save(team);
            return teamRepository.findByName(name);
        });

        assertEquals(team, teamFromDb);
        deleteTeam(team);
    }

    @Test
    public void canGetAllTeams() {
        executeVoid(teamRepository -> {
            for (int i = 0; i < 5; i++) {
                teamsInDb.add(teamRepository.save(new Team("test" + i)));
            }
        });
        Iterable<Team> teamsFromDb = executeMultiple(CrudRepository::findAll);

        Collection<Team> result = new ArrayList<>();
        teamsFromDb.forEach(t -> result.add(t));

        teamsInDb.forEach(t -> {
            if (!result.contains(t)) fail();
        });
        deleteTeams(teamsInDb);
    }

    private Team execute(Function<TeamRepository, Team> operation) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.scan(projectPackage);
            context.refresh();
            TeamRepository teamRepository = context.getBean(TeamRepository.class);
            return operation.apply(teamRepository);
        }
    }

    private void executeVoid(Consumer<TeamRepository> operation) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.scan(projectPackage);
            context.refresh();
            TeamRepository teamRepository = context.getBean(TeamRepository.class);
            operation.accept(teamRepository);
        }
    }

    private Iterable<Team> executeMultiple(Function<TeamRepository, Iterable<Team>> operation) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.scan(projectPackage);
            context.refresh();
            TeamRepository teamRepository = context.getBean(TeamRepository.class);
            return operation.apply(teamRepository);
        }
    }

    private void deleteTeam(Team team) {
        executeVoid(teamRepository -> teamRepository.delete(team));
    }

    private void deleteTeams(List<Team> teams) {
        executeVoid(teamRepository -> teamRepository.delete(teams));
    }
}