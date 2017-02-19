package io.github.agileek.cfp;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLTemplates;
import com.zaxxer.hikari.HikariDataSource;
import io.github.agileek.cfp.api.Proposal;
import io.github.agileek.cfp.database.model.QProposal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import org.flywaydb.core.Flyway;
import static spark.Spark.get;

public class App {

    @VisibleForTesting
    final HikariDataSource dataSource = new HikariDataSource();// SQL-dialect
    @VisibleForTesting
    final SQLTemplates dialect = new MySQLTemplates();

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.configure();
    }


    public void configure() throws SQLException, ClassNotFoundException {
        String url = System.getProperty("JDBC_URL", "jdbc:mysql://localhost:3306/cfp");
        String user = System.getProperty("JDBC_USER", "cfp");
        String password = System.getProperty("JDBC_PASSWORD", "cfp");

        dataSource.setJdbcUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();

        Gson gson = new Gson();
        get("/hello", (req, res) -> "Hello World");
        get("/proposal", ((request, response) -> {
            try (Connection connection = dataSource.getConnection()) {
                SQLQuery<Void> query = new SQLQuery<>(connection, dialect);
                List<String> contents = query.select(QProposal.proposal.content)
                        .from(QProposal.proposal)
                        .fetch();
                return contents.stream().map(Proposal::new).collect(Collectors.toList());
            }
        }), gson::toJson);
    }
}
