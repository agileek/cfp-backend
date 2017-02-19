package io.github.agileek.cfp;

import java.util.List;
import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;

public class CustomAuthorizer extends ProfileAuthorizer<CommonProfile> {

    @Override
    public boolean isAuthorized(final WebContext context, final List<CommonProfile> profiles) throws HttpAction {
        return isAnyAuthorized(context, profiles);
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context, final CommonProfile profile) {
        if (profile == null) {
            return false;
        }
        return "bitard.michael@gmail.com".equals(profile.getEmail());
    }
}
