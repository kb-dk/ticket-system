package dk.statsbiblioteket.medieplatform.ticketsystem;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 4/3/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Authorization {

    public Authorization(String service) {
    }


    /**
     * @param uuid the list of UUIDs that we want to examine
     * @param userAttributes the user attributes
     * @param type the type of the resources
     * @return a List of UUIDs that the user is allowed to see
     */
    public List<String> authorizeUser(List<String> userAttributes,
                                      String type,
                                      String... uuid){
        return null;
    }
}
