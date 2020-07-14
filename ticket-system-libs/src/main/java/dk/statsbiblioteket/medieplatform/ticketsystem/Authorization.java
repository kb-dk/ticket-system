package dk.statsbiblioteket.medieplatform.ticketsystem;

import dk.statsbiblioteket.medieplatform.ticketsystem.authorization.AuthorizationRequest;
import dk.statsbiblioteket.medieplatform.ticketsystem.authorization.AuthorizationResponse;
import dk.statsbiblioteket.medieplatform.ticketsystem.authorization.UserAttribute;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Authorization handles calling the license-Module webservice to determine which resources are allowed for the user
 */
public class Authorization {
    private String service;


    /**
     * Create a new authorization client
     * @param service the address of the license module webservice rest interface - http://devel06:9612/licensemodule/services/
     */
    public Authorization(String service) {
        this.service = service;
    }


    /**
     * Get the list, hopefully a real subset of the resources, that the user is allowed to access
     *  @param resources the list of UUIDs that we want to examine
     * @param userAttributes the user attributes
     * @param type the type of the resources
     * @return a List of UUIDs that the user is allowed to see
     */
    public List<String> authorizeUser(Map<String, List<String>> userAttributes,
                                      String type,
                                      List<String> resources){

        AuthorizationRequest authorizationRequest = new AuthorizationRequest(resources, type, transform(userAttributes));

        WebClient client = WebClient.create(service);
        AuthorizationResponse authorizationResponse = client.path("/checkAccessForIds")
                .type(MediaType.TEXT_XML)
                .post(authorizationRequest, AuthorizationResponse.class);

        return authorizationResponse.getResources();
    }

    /**
     * Transform the user attributes
     * @param userAttributes the user attributes
     * @return the user attributes
     */
    private List<UserAttribute> transform(Map<String, List<String>> userAttributes) {
        ArrayList<UserAttribute> result = new ArrayList<UserAttribute>();

        for (Map.Entry<String, List<String>> entry : userAttributes.entrySet()) {
            result.add(new UserAttribute(entry.getKey(), entry.getValue()));
        }

        return result;
    }
}
