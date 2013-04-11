package dk.statsbiblioteket.medieplatform.ticketsystem;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AuthorizationTest {
    @Test
    @Ignore
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

        Assert.assertEquals(resources.size(), authorizedResources.size());
    }
}
