package io.github.agileek.cfp;

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;

public class AuthenticationConfigFactory implements ConfigFactory {

    public Config build() {
        final Google2Client oidcClient = new Google2Client("838220759655-i3jhcru3c1feei5bjtriks1o7ass0p0v.apps.googleusercontent.com", "FAD9dVdJB4dOOQSh9uoWMLRg");
        oidcClient.setAuthorizationGenerator(profile -> profile.addRole("ROLE_ADMIN"));

        final FacebookClient facebookClient = new FacebookClient("145278422258960", "be21409ba8f39b5dae2a7de525484da8");
        final TwitterClient twitterClient = new TwitterClient("CoxUiYwQOSFDReZYdjigBA", "2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs");

        final Clients clients = new Clients("http://localhost:4567/callback", oidcClient, facebookClient, twitterClient);

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
        config.setHttpActionAdapter(new DefaultHttpActionAdapter());
        return config;
    }
}