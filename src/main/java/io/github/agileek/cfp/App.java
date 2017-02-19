package io.github.agileek.cfp;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.querydsl.sql.SQLQuery;
import io.github.agileek.cfp.api.Proposal;
import io.github.agileek.cfp.database.model.QProposal;
import io.github.agileek.cfp.database.model.bean.BProposal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import static spark.Spark.get;

public final class App {

    @VisibleForTesting
    SQLSetup sqlSetup;

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.configure();
    }

    public void configure() throws SQLException, ClassNotFoundException {
        String url = System.getProperty("JDBC_URL", "jdbc:mysql://localhost:3306/cfp");
        String user = System.getProperty("JDBC_USER", "cfp");
        String password = System.getProperty("JDBC_PASSWORD", "cfp");

        sqlSetup = new SQLSetup(url, user, password);
        Gson gson = new Gson();
        get("/hello", (req, res) -> "Hello World");
        get("/proposal", ((request, response) -> {
            try (Connection connection = sqlSetup.dataSource.getConnection()) {
                SQLQuery<Void> query = new SQLQuery<>(connection, sqlSetup.dialect);
                List<BProposal> contents = query.select(QProposal.proposal)
                        .from(QProposal.proposal)
                        .fetch();
                return contents.stream().map(bProposal -> new Proposal(bProposal.getSubject(), bProposal.getContent())).collect(Collectors.toList());
            }
        }), gson::toJson);
    }
}