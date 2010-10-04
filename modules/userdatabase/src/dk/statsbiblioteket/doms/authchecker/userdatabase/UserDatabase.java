package dk.statsbiblioteket.doms.authchecker.userdatabase;

import dk.statsbiblioteket.doms.authchecker.user.User;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 1, 2010
 * Time: 10:27:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserDatabase {

    private static Map<String,UserTimestamp> userDB;

    private static Date lastClean;

    static {
        userDB = new HashMap<String,UserTimestamp>();
        lastClean = new Date();
    }


    public static void addUser(User user){
        userDB.put(user.getUsername(),new UserTimestamp(user, new Date()));
        cleanup();
    }

    public static User getUser(String username){
        cleanup();
        UserTimestamp user = userDB.get(username);
        user.setLastAccessed(new Date());
        return user;

    }

    private static void cleanup(){
    }
}
