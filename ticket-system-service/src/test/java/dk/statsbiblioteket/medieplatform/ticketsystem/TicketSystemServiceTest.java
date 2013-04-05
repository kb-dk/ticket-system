package dk.statsbiblioteket.medieplatform.ticketsystem;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.server.impl.application.WebApplicationContext;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;
import junit.framework.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

public class TicketSystemServiceTest {

    @Test
    public void testIssueTicket() throws BackendException, URISyntaxException {
        ConfigCollection.getProperties().setProperty(
                "dk.statsbiblioteket.ticket-system.auth-checker",
                "http://devel06:9612/licensemodule/services/");
        WebApplicationImpl webApplication = new WebApplicationImpl();
        ContainerRequest request = new ContainerRequest(webApplication, null, new URI(""), new URI("?a=c"), new InBoundHeaders(), null);
        WebApplicationContext context = new WebApplicationContext(webApplication, request, null);

        TicketSystemService ticketSystemService = new TicketSystemService();
        Map<String,String> ticketMap = ticketSystemService.issueTicketQueryParams(Arrays.asList("uuid:123456-7890", "uuid:234567-8901"), "Streame", "userIdentifier", context);
        System.out.println(ticketMap);
        Assert.assertNotNull(ticketMap);
    }
}

