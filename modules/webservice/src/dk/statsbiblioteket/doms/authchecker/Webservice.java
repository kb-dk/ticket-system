package dk.statsbiblioteket.doms.authchecker;

import dk.statsbiblioteket.doms.authchecker.userdatabase.UserDatabase;
import dk.statsbiblioteket.doms.authchecker.user.User;
import dk.statsbiblioteket.doms.authchecker.user.Roles;
import dk.statsbiblioteket.doms.authchecker.fedoraconnector.FedoraConnector;
import dk.statsbiblioteket.doms.authchecker.exceptions.*;
import dk.statsbiblioteket.doms.webservices.ConfigCollection;

import javax.ws.rs.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Sep 30, 2010
 * Time: 2:54:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/")
public class Webservice {



    public Webservice() {
    }


    @GET
    @Path("is/{url}/allowedFor/{user}/WithTheseRoles")
    @Produces("text/plain")
    public String isAllowedForThisUserWithTheseRoles(
            @PathParam("url")
            String url,
            @PathParam("user")
            String user,
            @QueryParam("role")
            List<String> roles) throws
                                FedoraException,
                                URLNotFoundException,
                                InvalidCredentialsException,
                                ResourceNotFoundException {
        /*for (String role : roles) {
            System.out.println("Given this role "+role);
        }
*/


        /*Generate user information*/
        String password = mkpass(user);

        /*Store user information in temp database*/

        Roles fedoraroles = new Roles("fedoraRole", roles);
        UserDatabase.addUser(new User(user,password,fedoraroles));

        /*Check auth against Fedora*/

        String fedoralocation = ConfigCollection.getProperties().getProperty(
                "dk.statsbiblioteket.doms.authchecker.fedoralocation");

        FedoraConnector fedora = new FedoraConnector(fedoralocation);
        String pid = fedora.getObjectWithThisURL(url, user, password);
        fedora.getDatastreamProfile(pid,"CONTENTS",user,password);

        return password;
    }

    private String mkpass(String user) {
        //TODO
        return user+"1";
    }



    @GET
    @Path("getRolesForUser/{user}/withPassword/{password}")
    @Produces("text/xml")
    public User getRolesForUser(
            @PathParam("user") String username,
            @PathParam("password") String password)
            throws UserNotFoundException {
        User user = UserDatabase.getUser(username);
        if (user.getPassword().equals(password)){
            return user;
        } else{
            throw new UserNotFoundException("Username '"+username+"' with password '"+password+"' not found in user database");
        }

    }
}
