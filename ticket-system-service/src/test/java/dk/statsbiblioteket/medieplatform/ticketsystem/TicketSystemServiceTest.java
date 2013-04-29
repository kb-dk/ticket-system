package dk.statsbiblioteket.medieplatform.ticketsystem;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.server.impl.application.WebApplicationContext;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

public class TicketSystemServiceTest {

    @Test
    @Ignore("online test")
    public void testIssueTicket() throws BackendException, URISyntaxException {
        ConfigCollection.getProperties().setProperty(
                "dk.statsbiblioteket.ticket-system.auth-checker",
                "http://devel06:9612/licensemodule/services/");
        ConfigCollection.getProperties().setProperty("dk.statsbiblioteket.ticket-system.memcacheServer","localhost");
        ConfigCollection.getProperties().setProperty("dk.statsbiblioteket.ticket-system.memcachePort","11211");

        WebApplicationImpl webApplication = new WebApplicationImpl();
        ContainerRequest request = new ContainerRequest(webApplication, null, new URI(""), new URI("?ip_role_mapper.SBIPRoleMapper=SB_PUB"), new InBoundHeaders(), null);
        WebApplicationContext context = new WebApplicationContext(webApplication, request, null);

        TicketSystemService ticketSystemService = new TicketSystemService();
        Map<String,String> ticketMap = ticketSystemService.issueTicketQueryParams(
                Arrays.asList("doms_reklamefilm:uuid:35a1aa76-97a1-4f1b-b5aa-ad2a246eeeec"),
                "Stream",
                "ipAddress", context);
        System.out.println(ticketMap);
        Assert.assertNotNull(ticketMap);
        Assert.assertTrue(ticketMap.size() > 0);

        for (Map.Entry<String, String> stringStringEntry : ticketMap.entrySet()) {
            Ticket ticket = ticketSystemService.resolveTicket(stringStringEntry.getValue());
            System.out.println(ticket);
        }


    }
}

