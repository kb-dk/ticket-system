package dk.statsbiblioteket.doms.authchecker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fcrepo.server.security.jaas.auth.UserPrincipal;

import javax.security.auth.spi.LoginModule;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.*;
import java.util.*;
import java.io.IOException;

import dk.statsbiblioteket.doms.authchecker.exceptions.InvalidCredentialsException;


/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 1, 2010
 * Time: 10:50:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class TempDatabaseModule implements LoginModule {

    private static final Log logger =
            LogFactory.getLog(TempDatabaseModule.class);


    private Subject subject = null;

    private CallbackHandler handler = null;

    // private Map<String, ?> sharedState = null;
    private Map<String, ?> options = null;


    private String username = null;

    private UserPrincipal principal = null;

    private Map<String, Set<String>> attributes = null;

    private boolean debug = false;

    private boolean successLogin = false;

    private UserDBClient client;

    public void initialize(Subject subject,
                           CallbackHandler callbackHandler,
                           Map<String, ?> sharedState,
                           Map<String, ?> options) {
        this.subject = subject;
        this.handler = callbackHandler;
        // this.sharedState = sharedState;
        this.options = options;
        String wsLocation = (String) this.options.get("webserviceLocation");
        client = new UserDBClient(wsLocation);


        String debugOption = (String) this.options.get("debug");
        if (debugOption != null && "true".equalsIgnoreCase(debugOption)) {
            debug = true;
        }

        attributes = new HashMap<String, Set<String>>();

    }

    public boolean login() throws LoginException {

        // The only 2 callback types that are supported.
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("username");
        callbacks[1] = new PasswordCallback("password", false);

        String password = null;
        try {
            // sets the username and password from the callback handler
            handler.handle(callbacks);
            username = ((NameCallback) callbacks[0]).getName();
            char[] passwordCharArray =
                    ((PasswordCallback) callbacks[1]).getPassword();
            password = new String(passwordCharArray);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new LoginException("IOException occured: " + ioe.getMessage());
        } catch (UnsupportedCallbackException ucbe) {
            ucbe.printStackTrace();
            throw new LoginException("UnsupportedCallbackException encountered: "
                                     + ucbe.getMessage());
        }

        successLogin = authenticate(username, password);

        return successLogin;

    }

    private boolean authenticate(String username, String password) {

        //Primary work of this method is to get the roles of the user

        try {
            Map<String,Set<String>> roles = client.getRolesFor(username, password);
            principal = new UserPrincipal(username);
            attributes = roles;
            return true;
        } catch (InvalidCredentialsException e) {
            logger.debug(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public boolean commit() throws LoginException {
        if (!successLogin) {
            return false;
        }

        try {
            subject.getPrincipals().add(principal);
            subject.getPublicCredentials().add(attributes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    public boolean abort() throws LoginException {
        try {
            clear();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    public boolean logout() throws LoginException {
        try {
            clear();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    private void clear() {
        subject.getPrincipals().clear();
        subject.getPublicCredentials().clear();
        subject.getPrivateCredentials().clear();
        principal = null;
        username = null;
    }
}
