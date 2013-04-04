package dk.statsbiblioteket.medieplatform.ticketsystem;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.server.impl.application.WebApplicationContext;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import junit.framework.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class TicketSystemServiceTest {

    @Test
    public void testIssueTicket() throws BackendException, URISyntaxException {
        WebApplicationImpl webApplication = new WebApplicationImpl();
        ContainerRequest request = new ContainerRequest(webApplication, null, new URI(""), new URI(""), new InBoundHeaders(), null);
        WebApplicationContext context = new WebApplicationContext(webApplication, request, null);

        TicketSystemService ticketSystemService = new TicketSystemService();
        Map<String,String> ticketMap = ticketSystemService.issueTicketQueryParams("id", "type", "userIdentifier", context);
        Assert.assertNotNull(ticketMap);
    }
}

