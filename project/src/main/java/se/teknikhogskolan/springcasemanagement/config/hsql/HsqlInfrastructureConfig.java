package se.teknikhogskolan.springcasemanagement.config.hsql;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import se.teknikhogskolan.springcasemanagement.config.JpaConfig;

@Configuration
@ComponentScan(basePackages = {"se.teknikhogskolan.springcasemanagement"})
@EnableJpaRepositories("se.teknikhogskolan.springcasemanagement.repository")
@EnableTransactionManagement
@EnableJpaAuditing
public class HsqlInfrastructureConfig extends JpaConfig {

    @Bean
    @Override
    public DataSource dataSource() {
        
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        config.setJdbcUrl("jdbc:hsqldb:mem:TestSelf");
        
        return new HikariDataSource(config);
    }

    @Bean
    @Override
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.HSQL);
        adapter.setGenerateDdl(true);
        return adapter;
    }
}