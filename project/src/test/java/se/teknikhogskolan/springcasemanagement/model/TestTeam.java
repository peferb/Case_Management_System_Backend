package se.teknikhogskolan.springcasemanagement.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public final class TestTeam {

    private Team team;

    @Before
    public void setUp() throws Exception {
        team = new Team("name");
    }

    @Test
    public void sameTeamShouldBeEqual() {
        assertEquals(team, team);
    }

    @Test
    public void differentNamesShouldNotBeEqual() {
        Team newTeam = new Team("diff name");
        assertNotEquals(team, newTeam);
    }

    @Test
    public void sameNameShouldHaveSameHashCode() {
        Team newTeam = new Team(team.getName());
        assertEquals(team.hashCode(), newTeam.hashCode());
    }

    @Test
    public void differentObjectTypeShouldNotBeEqual() {
        String teamString = team.getName();
        assertNotEquals(teamString, team);
    }
}