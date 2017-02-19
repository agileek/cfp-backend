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
        final Google2Client oidcClient = new Google2Client("343992089165-sp0l1km383i8cbm2j5nn20kbk5dk8hor.apps.googleusercontent.com", "uR3D8ej1kIRPbqAFaxIE3HWh");
        oidcClient.setAuthorizationGenerator(profile -> profile.addRole("ROLE_ADMIN"));

        final FacebookClient facebookClient = new FacebookClient("145278422258960", "be21409ba8f39b5dae2a7de525484da8");
        final TwitterClient twitterClient = new TwitterClient("CoxUiYwQOSFDReZYdjigBA", "2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs");

        final Clients clients = new Clients("http://localhost:8080/callback", oidcClient, facebookClient, twitterClient);

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
        config.setHttpActionAdapter(new DefaultHttpActionAdapter());
        return config;
    }
}