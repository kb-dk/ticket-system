package dk.statsbiblioteket.doms.authchecker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dk.statsbiblioteket.doms.authchecker.exceptions.BackendException;
import dk.statsbiblioteket.doms.authchecker.exceptions.FedoraException;
import dk.statsbiblioteket.doms.authchecker.exceptions.InvalidCredentialsException;
import dk.statsbiblioteket.doms.authchecker.exceptions.MissingArgumentException;
import dk.statsbiblioteket.doms.authchecker.exceptions.ResourceNotFoundException;
import dk.statsbiblioteket.doms.authchecker.exceptions.URLNotFoundException;
import dk.statsbiblioteket.doms.authchecker.exceptions.UserNotFoundException;
import dk.statsbiblioteket.doms.authchecker.fedoraconnector.FedoraConnector;
import dk.statsbiblioteket.doms.authchecker.user.Roles;
import dk.statsbiblioteket.doms.authchecker.user.User;
import dk.statsbiblioteket.doms.authchecker.userdatabase.UserDatabase;
import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tools for checking access to resources in DOMS.
 */
@Path("/")
public class Authchecker {

    private static UserDatabase users;

    private static UserDatabase adminUsers;

    private static final Object lock = new Object();

    private static final String USER_TTL_PROP ="dk.statsbiblioteket.doms.authchecker.users.timeToLive";
    private static final String ADMINUSER_TTL_PROP ="dk.statsbiblioteket.doms.authchecker.adminusers.timeToLive";
    private Log log = LogFactory.getLog(Authchecker.class);
    private String fedoralocation;
    private FedoraConnector fedora;

    public Authchecker() throws BackendException {
        log.trace("Created a new authchecker webservice object");
        synchronized (lock){
            if (users == null){
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
            if (users == null){
                long ttl;
                try {
                    String ttlString = ConfigCollection.getProperties()
                            .getProperty(USER_TTL_PROP,""+30*1000);
                    log.trace("Read '"+USER_TTL_PROP+"' property as '"+ttlString+"'");
                    ttl = Long.parseLong(ttlString);
                } catch (NumberFormatException e) {
                    log.warn("Could not parse the  '"+ USER_TTL_PROP
                             +"' as a long, using default 30 sec timetolive",e);
                    ttl = 30*1000;
                }
                users = new UserDatabase(ttl);
            }


            if (fedoralocation == null){
                fedoralocation = ConfigCollection.getProperties().getProperty(
                        "dk.statsbiblioteket.doms.authchecker.fedoralocation");
                if (fedoralocation == null){
                    throw new FedoraException("Could not determine fedora location");
                }

                fedora = new FedoraConnector(fedoralocation);
            }


        }
    }

    @POST
    @Path("createAdminUser/{user}/WithTheseRoles")
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


    @POST
    @Path("isURLallowedFor/{user}/WithTheseRoles")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.TEXT_XML})
    public User isUrlAllowedForThisUserWithTheseRoles(
            @PathParam("user") String username,
            @QueryParam("url") String resource,
            MultivaluedMap<String,String> roles) throws
                                                 FedoraException,
                                                 URLNotFoundException,
                                                 InvalidCredentialsException,
                                                 ResourceNotFoundException,
                                                 MissingArgumentException {

        log.trace("Entered isURLAllowedForThisUserWithTheseRoles with the params"
                  + "url='"+resource+"' and user='"+ username +"'");

        if (resource == null){
            throw new MissingArgumentException("url is missing");
        }

        /*Generate user information*/
        User user = createSessionUser(username, roles);

        /*Check auth against Fedora*/
        String pid = fedora.getObjectWithThisURL(resource, user.getUsername(), user.getPassword());
        log.trace("Found pid='"+pid+"' with the url '"+resource+"'");

        log.trace("Attempting to get datastream profile of pid '"+pid+"' with the username '"+ username
                  +"'");
        fedora.getDatastreamProfile(pid,"CONTENTS", user.getUsername(), user.getPassword());

        log.trace("Success, returning user='"+ username +"' password");
        return user;
    }


    @GET
    @Path("isURLallowedFor/{user}/WithTheseRoles")
    @Produces({MediaType.TEXT_XML})
    public User isUrlAllowedForThisUserWithTheseRoles(
            @PathParam("user") String username,
            @QueryParam("url") String resource,
            @Context UriInfo ui) throws
                                 FedoraException,
                                 URLNotFoundException,
                                 InvalidCredentialsException,
                                 ResourceNotFoundException, MissingArgumentException {
        MultivaluedMap<String,String> params = ui.getQueryParameters();
        return isUrlAllowedForThisUserWithTheseRoles(username,resource,params);
    }



    @GET
    @Path("isURLallowedWithTheseRoles")
    @Produces({MediaType.TEXT_XML})
    public User isUrlAllowedWithTheseRoles(
            @QueryParam("url") String resource,
            @Context UriInfo ui) throws
                                 FedoraException,
                                 URLNotFoundException,
                                 InvalidCredentialsException,
                                 ResourceNotFoundException, MissingArgumentException {
        return isUrlAllowedForThisUserWithTheseRoles(null,resource,ui);
    }

    @POST
    @Path("isURLallowedWithTheseRoles")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.TEXT_XML})
    public User isUrlAllowedWithTheseRoles(
            @QueryParam("url") String resource,
            MultivaluedMap<String,String> roles) throws
                                                 FedoraException,
                                                 URLNotFoundException,
                                                 InvalidCredentialsException,
                                                 ResourceNotFoundException, MissingArgumentException {
        return isUrlAllowedForThisUserWithTheseRoles(null,resource,roles);
    }

    /**
     * Given a username, a set of roles, and a pid, create a session for that user and check whether that user would be
     * allowed to access the given pid.
     * @param username User name to user for the user session. If null, a username will be created.
     * @param pid Pid to check access to. Must not be null.
     * @param roles Roles this user should have. If null, a user with no roles will be created.
     * @return A session user object, if the user is allowed access.
     * @throws FedoraException If Fedora could not be contacted to check access rights.
     * @throws InvalidCredentialsException If access is not allowed with these roles.
     * @throws ResourceNotFoundException If no object with this PID could be found.
     * @throws MissingArgumentException If parameter "pid" is null.
     */
    @POST
    @Path("isPIDallowedFor/{user}/WithTheseRoles")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.TEXT_XML})
    public User isPidAllowedForThisUserWithTheseRoles(@PathParam("user") String username, @QueryParam("pid") String pid,
                                                      MultivaluedMap<String, String> roles)
            throws FedoraException, InvalidCredentialsException, ResourceNotFoundException,
            MissingArgumentException {

        log.trace("Entered isPIDAllowedForThisUserWithTheseRoles with the params" + "pid='" + pid + "' and user='"
                          + username + "'");

        if (pid == null) {
            throw new MissingArgumentException("pid is missing");
        }

        /*Generate user information*/
        User user = createSessionUser(username, roles);

        /*Check auth against Fedora*/
        log.trace("Attempting to get datastream profile of pid '" + pid + "' with the username '" + username + "'");
        fedora.getDatastreamProfile(pid, "DC", user.getUsername(), user.getPassword());

        log.trace("Success, returning user='" + username);
        return user;
    }


    /**
     * Given a username, a set of roles, and a pid, create a session for that user and check whether that user would be
     * allowed to access the given pid.
     * @param username User name to user for the user session. If null, a username will be created.
     * @param pid Pid to check access to. Must not be null.
     * @param ui Roles this user should have, given as injected query parameters. Must not be null.
     * @return A session user object, if the user is allowed access.
     * @throws FedoraException If Fedora could not be contacted to check access rights.
     * @throws InvalidCredentialsException If access is not allowed with these roles.
     * @throws ResourceNotFoundException If no object with this PID could be found.
     * @throws MissingArgumentException If parameter "pid" is null.
     */
    @GET
    @Path("isPIDallowedFor/{user}/WithTheseRoles")
    @Produces({MediaType.TEXT_XML})
    public User isPidAllowedForThisUserWithTheseRoles(
            @PathParam("user") String username,
            @QueryParam("pid") String pid,
            @Context UriInfo ui) throws
                                 FedoraException,
                                 InvalidCredentialsException,
                                 ResourceNotFoundException, MissingArgumentException {
        MultivaluedMap<String,String> params = ui.getQueryParameters();
        return isPidAllowedForThisUserWithTheseRoles(username, pid, params);
    }


    /**
     * Given a set of roles and a pid, create a session for that user and check whether that user would be
     * allowed to access the given pid.
     * @param pid Pid to check access to. Must not be null.
     * @param ui Roles this user should have, given as injected query parameters. Must not be null.
     * @return A session user object, if the user is allowed access.
     * @throws FedoraException If Fedora could not be contacted to check access rights.
     * @throws InvalidCredentialsException If access is not allowed with these roles.
     * @throws ResourceNotFoundException If no object with this PID could be found.
     * @throws MissingArgumentException If parameter "pid" is null.
     */
    @GET
    @Path("isPIDallowedWithTheseRoles")
    @Produces({MediaType.TEXT_XML})
    public User isPidAllowedWithTheseRoles(
            @QueryParam("pid") String pid,
            @Context UriInfo ui) throws
                                 FedoraException,
                                 InvalidCredentialsException,
                                 ResourceNotFoundException, MissingArgumentException {
        return isPidAllowedForThisUserWithTheseRoles(null, pid, ui);
    }


    /**
     * Given a set of roles and a pid, create a session for that user and check whether that user would be
     * allowed to access the given pid.
     *
     * @param pid Pid to check access to. Must not be null.
     * @param roles Roles this user should have. If null, a user with no roles will be created.
     * @return A session user object, if the user is allowed access.
     * @throws FedoraException If Fedora could not be contacted to check access rights.
     * @throws InvalidCredentialsException If access is not allowed with these roles.
     * @throws ResourceNotFoundException If no object with this PID could be found.
     * @throws MissingArgumentException If parameter "pid" is null.
     */
    @POST
    @Path("isPIDallowedWithTheseRoles")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.TEXT_XML})
    public User isPidAllowedWithTheseRoles(
            @QueryParam("pid") String pid,
            MultivaluedMap<String,String> roles) throws
                                                 FedoraException,
                                                 InvalidCredentialsException,
                                                 ResourceNotFoundException, MissingArgumentException {
        return isPidAllowedForThisUserWithTheseRoles(null,pid, roles);
    }


    private String mkUsername(){
        return UUID.randomUUID().toString();
    }

    private String mkpass() {
        return UUID.randomUUID().toString();
    }

    /**
     * Add a session user to the Fedora session user database.
     *
     * @param username Username. If null, a username will be created.
     * @param roles Roles this user should have. If null, the user will have no roles.
     * @return A user object for the session user.
     */
    private User createSessionUser(String username, MultivaluedMap<String, String> roles) {
        String password = mkpass();
        log.trace("Password created for user='"+ username + "'");

        if (username == null) {
            username = mkUsername();
        }

        /*Store user information in temp database*/
        List<Roles> fedoraroles = new ArrayList<Roles>();
        if (roles != null) {
            for (Map.Entry<String, List<String>> stringListEntry : roles.entrySet()) {
                if (stringListEntry.getKey().equals("fedoraRole")){
                    continue;
                }
                if (stringListEntry.getKey().equals("pid")){
                    continue;
                }
                fedoraroles.add(new Roles(stringListEntry.getKey(),stringListEntry.getValue()));
            }
        }
        User user = users.addUser(username, password, fedoraroles);
        log.trace("User='"+ username +" added to the database");
        return user;
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
        User user = users.getUser(username);

        if (user != null && user.getPassword().equals(password)){
            log.trace("User='"+username+"' found and password matches");
            return user;
        } else{
            user = adminUsers.getUser(username);
            if (user != null && user.getPassword().equals(password)){
                log.trace("User='"+username+"' found and password matches");
                return user;
            } else {
                log.debug("User='"+username+"' not found, or password does not match");
                throw new UserNotFoundException("Username '"+username+"' with password '"+password+"' not found in user database");
            }
        }
    }

}
