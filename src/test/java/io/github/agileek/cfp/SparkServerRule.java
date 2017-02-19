package io.github.agileek.cfp;

import com.querydsl.sql.SQLTemplates;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.sql.Connection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.HttpUrl;
import okhttp3.Request;
import static org.awaitility.Awaitility.await;
import org.junit.rules.ExternalResource;
import spark.Spark;

public class SparkServerRule extends ExternalResource {

    private App tested;
    private final AtomicInteger port = new AtomicInteger();

    private static boolean isPortAvailable(int port) {
        try {
            try (
                    ServerSocket ss = new ServerSocket(port);
                    DatagramSocket ds = new DatagramSocket(port)
            ) {
                ss.setReuseAddress(false);
                ds.setReuseAddress(false);
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public Request.Builder withRequestBuilder(String pathSegment) {
        return new Request.Builder().url(withBaseUrl().addPathSegment(pathSegment).build());
    }

    public HttpUrl.Builder withBaseUrl() {
        return new HttpUrl.Builder()
                .scheme("http")
                .host("localhost")
                .port(port.get());
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        tested = new App();
        Spark.port(getFirstAvailablePort());
        System.setProperty("JDBC_URL", "jdbc:h2:mem:test;MODE=MYSQL;DATABASE_TO_UPPER=false;INIT=CREATE SCHEMA IF NOT EXISTS CFP");
        System.setProperty("JDBC_USER", "cfp");
        System.setProperty("JDBC_PASSWORD", "");
        System.setProperty("JDBC_DRIVER", "org.h2.Driver");
        tested.configure();
        Spark.awaitInitialization();
    }

    private int getFirstAvailablePort() throws Exception {
        int port;
        do {
            port = ThreadLocalRandom.current().nextInt(1024, 50000);
            isPortAvailable(port);
        } while (!isPortAvailable(port));
        this.port.set(port);
        return port;
    }

    @Override
    protected void after() {
        super.after();
        Spark.stop();
        await().atMost(1, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS).until(() -> isPortAvailable(port.get()));

    }

    public Connection getConnection() throws Exception {
        return tested.dataSource.getConnection();
    }

    public SQLTemplates getDialect() {
        return tested.dialect;
    }
}
