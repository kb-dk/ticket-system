package dk.statsbiblioteket.doms.authchecker;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 1, 2010
 * Time: 11:17:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserDBClient {


    public Map<String, Set<String>> getRolesFor(String username, String password)
    throws InvalidCredentialsException {
        Set<String> set = new HashSet<String>();
        set.add("administrator");
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        map.put("fedoraRole",set);
        return map;
    }
}
