package dk.statsbiblioteket.doms.authchecker.userdatabase;

import dk.statsbiblioteket.doms.authchecker.TimeSensitiveCache;
import dk.statsbiblioteket.doms.authchecker.user.Roles;
import dk.statsbiblioteket.doms.authchecker.user.User;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 1, 2010
 * Time: 10:27:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserDatabase{
    private  TimeSensitiveCache<User> cache;

    public UserDatabase(long timeToLive) {
        cache = new TimeSensitiveCache<User>(timeToLive);
    }

    public  User getUser(String username){
        return cache.getElement(username);
    }

    public User addUser(String username, String password, Roles fedoraroles) {
        String id = username;
        User user = new User(username,password,id,fedoraroles);
        cache.addElement(user);
        return user;
    }

    public User addUser(String username, String password, List<Roles> fedoraroles) {
        String id = username;
        User user = new User(username,password,id,fedoraroles);
        cache.addElement(user);
        return user;
    }

}
