package io.github.agileek.cfp;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.querydsl.sql.dml.SQLInsertClause;
import io.github.agileek.cfp.api.Proposal;
import io.github.agileek.cfp.database.model.QProposal;
import io.github.agileek.cfp.database.model.bean.BProposal;
import java.sql.Connection;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Rule;
import org.junit.Test;

public class AppTest {
    @Rule
    public SparkServerRule withSparkServer = new SparkServerRule();

    @Test
    public void shouldGetTheHelloWorld() throws Exception {
        Request request = withSparkServer.withRequestBuilder("hello").build();

        Response response = new OkHttpClient().newCall(request).execute();
        assertThat(response.body().string()).isEqualTo("Hello World");
    }

    @Test
    public void shouldGetTheProposals() throws Exception {
        Request request = withSparkServer.withRequestBuilder("proposal").build();

        Response response = new OkHttpClient().newCall(request).execute();
        assertThat(getProposals(response)).isEmpty();
    }


    @Test
    public void shouldGetTheProposals_withRealValue() throws Exception {
        try (Connection connection = withSparkServer.getConnection()) {
            BProposal bProposal = new BProposal();
            bProposal.setContent("Content");
            new SQLInsertClause(connection, withSparkServer.getDialect(), QProposal.proposal).populate(bProposal).execute();
        }
        Request request = withSparkServer.withRequestBuilder("proposal").build();

        Response response = new OkHttpClient().newCall(request).execute();
        assertThat(getProposals(response)).hasSize(1).containsExactly(new Proposal("Content"));
    }

    private List<Proposal> getProposals(Response response) {
        Gson gson = new Gson();
        return gson.fromJson(response.body().charStream(), new TypeToken<List<Proposal>>() {
        }.getType());
    }
}