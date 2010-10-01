package dk.statsbiblioteket.doms.authchecker.userdatabase;

import dk.statsbiblioteket.doms.authchecker.userdatabase.user.User;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 1, 2010
 * Time: 10:27:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserDatabase {

    private static Map<String,User> userDB;

    static {
        userDB = new HashMap<String,User>();
    }


    public static void addUser(User user){
        userDB.put(user.getUsername(),user);
        cleanup();
    }

    public static User getUser(String username){
        cleanup();
        return userDB.get(username);
    }

    private static void cleanup(){
        
    }
}
