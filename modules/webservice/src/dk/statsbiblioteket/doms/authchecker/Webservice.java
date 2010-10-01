package dk.statsbiblioteket.doms.authchecker;

import dk.statsbiblioteket.doms.authchecker.userdatabase.UserDatabase;
import dk.statsbiblioteket.doms.authchecker.userdatabase.user.User;
import dk.statsbiblioteket.doms.authchecker.userdatabase.user.Roles;

import javax.ws.rs.*;
import java.util.List;
import java.util.LinkedList;

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
            List<String> roles){
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




        return String.valueOf(true);
    }

    private String mkpass(String user) {
        return user+"1";
    }


    @GET
    @Path("usage")
    @Produces("text/plain")
    public String usage(){
        return "Usage";
    }


    @GET
    @Path("getRoles")
    @Produces("text/xml")
    public User getRolesForUser(String username, String password)
            throws UserNotFoundException {
        User user = UserDatabase.getUser(username);
        if (user.getPassword().equals(password)){
            return user;
        } else{
            throw new UserNotFoundException("Username '"+username+"' with password '"+password+"' not found in user database");
        }

    }
}
