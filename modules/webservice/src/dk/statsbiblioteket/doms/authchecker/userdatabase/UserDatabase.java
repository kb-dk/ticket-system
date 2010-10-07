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

    private static Map<String,User> userDB;

    private static Map<String, Date> usernamesToDates;

    private static Deque<String> accessOrder;

    private static Date lastClean;

    private static Date age;

    static {
        userDB = new HashMap<String,User>();
        accessOrder = new LinkedList<String>();
        usernamesToDates = new HashMap<String,Date>();
        lastClean = new Date();
        age = new Date(1000*60*10);//10 min
    }


    public synchronized static void addUser(User user){
        String name = user.getUsername();
        userDB.put(name,user);
        usernamesToDates.put(name,new Date());
        accessOrder.offerFirst(name);
        cleanup();
    }

    public synchronized static User getUser(String username){
        User user = userDB.get(username);
        if (user != null){
            usernamesToDates.put(username,new Date());
            accessOrder.removeLastOccurrence(username); //TODO O(n)
            accessOrder.offerFirst(username);
        }
        cleanup();
        return user;

    }

    private synchronized static void cleanup(){
        if (!isToOld(lastClean)){
            return;
        }

        lastClean = new Date();

        //Get the oldest accessed username
        String lastUsername = accessOrder.getLast();

        //Check the date on that username
        Date dateOfLast = usernamesToDates.get(lastUsername);

        //If too old, remove, and redo
        if (isToOld(dateOfLast)){
            userDB.remove(lastUsername);
            usernamesToDates.remove(lastUsername);
            accessOrder.removeLast();
            cleanup();
        }
    }

    private synchronized static boolean isToOld(Date event) {
        Date now = new Date();
        if (event.getTime()+age.getTime()>=now.getTime()){
            return true;
        } else {
            return false;
        }
    }
}
