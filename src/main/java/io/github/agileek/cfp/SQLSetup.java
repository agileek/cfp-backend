package io.github.agileek.cfp;

import com.google.common.annotations.VisibleForTesting;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLTemplates;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

public final class SQLSetup {
    @VisibleForTesting
    final HikariDataSource dataSource = new HikariDataSource();
    @VisibleForTesting
    final SQLTemplates dialect = new MySQLTemplates();

    public SQLSetup(String url, String user, String password) {
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        dataSource.getDataSourceClassName();

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }

}
