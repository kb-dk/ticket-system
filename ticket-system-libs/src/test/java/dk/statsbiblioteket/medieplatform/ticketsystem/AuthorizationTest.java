package dk.statsbiblioteket.medieplatform.ticketsystem;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthorizationTest {
    @Test
    @Disabled
    public void testAuthorizeUser() throws Exception {
        //Online test
        //make grizzly webservice
        Authorization authorization = new Authorization("http://devel06:9612/licensemodule/services/");

        ArrayList<String> resources = new ArrayList<String>();
        resources.add("doms_radioTVCollection:uuid:a5390b1e-69fb-47c7-b23e-7831eb59479d");
        resources.add("doms_reklamefilm:uuid:35a1aa76-97a1-4f1b-b5aa-ad2a246eeeec");

        String type = "Stream";
        HashMap<String, List<String>> userAttributes = new HashMap<String, List<String>>();
        userAttributes.put("SBIPRoleMapper", Arrays.asList("inhouse"));

        List<String> authorizedResources = authorization.authorizeUser(userAttributes, type, resources);

        System.out.println(authorizedResources);

        assertEquals(resources.size(), authorizedResources.size());
    }
}
