package dk.statsbiblioteket.doms.authchecker;

import dk.statsbiblioteket.doms.authchecker.exceptions.*;
import dk.statsbiblioteket.doms.authchecker.fedoraconnector.FedoraConnector;
import dk.statsbiblioteket.doms.authchecker.user.Roles;
import dk.statsbiblioteket.doms.authchecker.user.User;
import dk.statsbiblioteket.doms.authchecker.userdatabase.UserDatabase;
import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 4/3/13
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
@Path("/adminUsers/")
public class GuiAdminUsers {

    private static UserDatabase adminUsers;

    private static final Object lock = new Object();


    private static final String ADMINUSER_TTL_PROP ="dk.statsbiblioteket.doms.authchecker.adminusers.timeToLive";
    private Log log = LogFactory.getLog(Authchecker.class);


    public GuiAdminUsers() throws BackendException {
        log.trace("Created a new authchecker webservice object");
        synchronized (lock){
            if (adminUsers == null){
                long ttl;
                try {
                    String ttlString = ConfigCollection.getProperties()
                            .getProperty(ADMINUSER_TTL_PROP,""+48*60*60*1000);
                    log.trace("Read '"+ADMINUSER_TTL_PROP+"' property as '"+ttlString+"'");
                    ttl = Long.parseLong(ttlString);
                } catch (NumberFormatException e) {
                    log.warn("Could not parse the  '"+ ADMINUSER_TTL_PROP
                             +"' as a long, using default 2 days timetolive",e);
                    ttl = 48*60*60*1000;
                }
                adminUsers = new UserDatabase(ttl);
            }
        }
    }


    @POST
    @Path("/createUser/{user}/WithTheseRoles")
    @Produces({MediaType.TEXT_XML})
    public User createAdminUserWithTheseRoles(
            @PathParam("user") String username,
            @Context UriInfo ui) throws
            FedoraException,
            URLNotFoundException,
            InvalidCredentialsException,
            ResourceNotFoundException,
            MissingArgumentException {


        log.trace("Entered createUserWithTheseRoles with the params"
                  + "'user='"+ username +"'");
        MultivaluedMap<String, String> roles = ui.getQueryParameters();

        /*Generate user information*/
        String password = mkpass();

        log.trace("Password created for user='"+ username +"': '"+password+"'");
        /*Store user information in temp database*/

        List<Roles> fedoraroles = new ArrayList<Roles>();
        for (Map.Entry<String, List<String>> stringListEntry : roles.entrySet()) {
            if (stringListEntry.getKey().equals("fedoraRole")){
                continue;
            }
            if (stringListEntry.getKey().equals("url")){
                continue;
            }
            fedoraroles.add(new Roles(stringListEntry.getKey(),stringListEntry.getValue()));
        }
        User user = adminUsers.addUser(username, password,fedoraroles);

        log.trace("User='"+ username +" added to the database");

        log.trace("Success, returning user='"+ username +"' password");
        return user;
    }

    private String mkpass() {
        return UUID.randomUUID().toString();
    }


    @GET
    @Path("getRolesForUser/{user}/withPassword/{password}")
    @Produces({MediaType.TEXT_XML})
    public User getRolesForUser(
            @PathParam("user") String username,
            @PathParam("password") String password)
            throws BackendException {
        log.trace("Entered getRolesForUser with the params user='"+username+"' and"
                + "password='"+password+"'");
        log.trace("Getting user='"+username+"' from the database");
        User user = adminUsers.getUser(username);

        if (user != null && user.getPassword().equals(password)){
            log.trace("User='"+username+"' found and password matches");
            return user;
        }  else {
            log.debug("User='"+username+"' not found, or password does not match");
            throw new UserNotFoundException("Username '"+username+"' with password '"+password+"' not found in user database");
        }

    }


}
