package dk.statsbiblioteket.medieplatform.ticketsystem;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import dk.statsbiblioteket.medieplatform.ticketsystem.authorization.AuthorizationRequest;
import dk.statsbiblioteket.medieplatform.ticketsystem.authorization.AuthorizationResponse;
import dk.statsbiblioteket.medieplatform.ticketsystem.authorization.UserAttribute;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 4/3/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Authorization {
    public static final Client client = Client.create();

    private final WebResource webResource;

    public Authorization(String service) {
        webResource = client.resource(service);
    }


    /**
     * @param resources the list of UUIDs that we want to examine
     * @param userAttributes the user attributes
     * @param type the type of the resources
     * @return a List of UUIDs that the user is allowed to see
     */
    public List<String> authorizeUser(Map<String, List<String>> userAttributes,
                                      String type,
                                      List<String> resources){


        AuthorizationRequest authorizationRequest = new AuthorizationRequest(resources, type, transform(userAttributes));


        AuthorizationResponse authorizationResponse = webResource.path("/checkAccessForIds")
                .type(MediaType.TEXT_XML)
                .post(AuthorizationResponse.class, authorizationRequest);

//        try {
//            JAXBContext jaxbContext = JAXBContext.newInstance(AuthorizationRequest.class, AuthorizationResponse.class);
//            Marshaller marshaller = jaxbContext.createMarshaller();
//            marshaller.marshal(authorizationRequest,System.out);
//             marshaller.marshal(authorizationResponse,System.out);
//        } catch (JAXBException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//        System.out.println(authorizationResponse.getQuery());

        return authorizationResponse.getResources();
    }

    public List<UserAttribute> transform(Map<String, List<String>> userAttributes) {
        ArrayList<UserAttribute> result = new ArrayList<UserAttribute>();

        for (Map.Entry<String, List<String>> entry : userAttributes.entrySet()) {
            result.add(new UserAttribute(entry.getKey(), entry.getValue()));
        }

        return result;
    }
}
