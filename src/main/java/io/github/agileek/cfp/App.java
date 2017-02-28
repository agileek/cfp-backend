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
import java.util.UUID;
import java.util.stream.Collectors;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkWebContext;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.post;

public final class App {

    @VisibleForTesting
    SQLSetup sqlSetup;

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.applySecurity();
        app.configure();
    }

    @VisibleForTesting
    private void applySecurity() {
        final Config config = new AuthenticationConfigFactory().build();
        SecurityFilter loginFilter = new SecurityFilter(config, "Google2Client,FacebookClient,TwitterClient");
        final CallbackRoute callback = new CallbackRoute(config, null, true);
        before("/hello", loginFilter);
        before("/proposal/:proposal/vote", new SecurityFilter(config, "*", "vote"));
        get("/callback", callback);
        post("/callback", callback);
    }

    public void configure() throws SQLException, ClassNotFoundException {
        String url = System.getProperty("JDBC_URL", "jdbc:h2:file:./cfp;MODE=MYSQL;DATABASE_TO_UPPER=false;INIT=CREATE SCHEMA IF NOT EXISTS CFP");
        String user = System.getProperty("JDBC_USER", "cfp");
        String password = System.getProperty("JDBC_PASSWORD", "cfp");

        sqlSetup = new SQLSetup(url, user, password);
        Gson gson = new Gson();

        get("/hello", (req, res) -> "Hello World");

        get("/test", (req, res) -> {
            final SparkWebContext context = new SparkWebContext(req, res);
            final ProfileManager manager = new ProfileManager(context);
            return gson.toJson(manager.getAll(true));
        });
        get("/proposal", ((request, response) -> {
            try (Connection connection = sqlSetup.dataSource.getConnection()) {
                SQLQuery<Void> query = new SQLQuery<>(connection, sqlSetup.dialect);
                List<BProposal> contents = query.select(QProposal.proposal)
                        .from(QProposal.proposal)
                        .fetch();
                return contents.stream().map(bProposal -> new Proposal(UUID.fromString(bProposal.getId()), bProposal.getSubject(), bProposal.getContent())).collect(Collectors.toList());
            }
        }), gson::toJson);
    }
}