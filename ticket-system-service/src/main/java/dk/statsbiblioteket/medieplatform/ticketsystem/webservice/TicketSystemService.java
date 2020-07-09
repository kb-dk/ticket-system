package dk.statsbiblioteket.medieplatform.ticketsystem;

import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/tickets/")
public class TicketSystemService {

    private static final String MEMCACHE_SERVER = "dk.statsbiblioteket.medieplatform.ticketsystem.memcacheServer";
    private static final String MEMCACHE_PORT = "dk.statsbiblioteket.medieplatform.ticketsystem.memcachePort";
    private static TicketSystem tickets;

    private static final Object lock = new Object();

    private static final String TICKET_TTL_PROP = "dk.statsbiblioteket.medieplatform.ticketsystem.timeToLive";
    private static final String TICKET_AUTH_SERVICE = "dk.statsbiblioteket.medieplatform.ticketsystem.auth-checker";

    private final Log log = LogFactory.getLog(TicketSystemService.class);



    public TicketSystemService() throws BackendException {
        log.trace("Created a new TicketSystem webservice object");
        synchronized (lock){
            if (tickets == null) {
                int ttl;
                try {
                    String ttlString = ConfigCollection.getProperties()
                            .getProperty(TICKET_TTL_PROP, "" + 30);
                    log.trace("Read '" + TICKET_TTL_PROP + "' property as '" + ttlString + "'");
                    ttl = Integer.parseInt(ttlString);
                } catch (NumberFormatException e) {
                    log.warn("Could not parse the  '" + TICKET_TTL_PROP
                            + "' as a long, using default 30 sec timetolive", e);
                    ttl = 30;
                }

                String authService = ConfigCollection.getProperties().getProperty(TICKET_AUTH_SERVICE);
                Authorization authorization = new Authorization(authService);


                String memcacheServer = ConfigCollection.getProperties().getProperty(MEMCACHE_SERVER);
                int memcachePort = Integer.parseInt(ConfigCollection.getProperties().getProperty(MEMCACHE_PORT));


                //TODO how is reconnect handled?
                MemcachedClient memCachedTickets;
                try {
                    memCachedTickets = new MemcachedClient(new BinaryConnectionFactory(),
                            Arrays.asList(new InetSocketAddress(memcacheServer, memcachePort)));
                } catch (IOException e) {
                    throw new Error("Failed to connect to cache, ticket system fails to start", e);
                }


                tickets = new TicketSystem(memCachedTickets, ttl,authorization);
            }
        }
    }


    /*Issuing of tickets*/

    @GET
    @Path("issueTicket")
    @Produces({MediaType.APPLICATION_JSON})
    public Map<String, String> issueTicketGet(
            @QueryParam("id") List<String> id,
            @QueryParam("type") String type,
            @QueryParam("ipAddress") String ipAddress,
            @Context UriInfo uriInfo
    ) throws MissingArgumentException {
        return issueTicketQueryParams(id, type, ipAddress, uriInfo);
    }



    @POST
    @Path("issueTicket")
    @Produces({MediaType.APPLICATION_JSON})
    public Map<String, String> issueTicketQueryParams(
            @QueryParam("id") List<String> resources,
            @QueryParam("type") String type,
            @QueryParam("ipAddress") String ipAddress,
            @Context UriInfo uriInfo
    ) throws MissingArgumentException {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        queryParams.remove("id");
        queryParams.remove("type");
        queryParams.remove("ipAddress");

        if (resources == null){
            throw new MissingArgumentException("id is missing");
        }

        if (type == null){
            throw new MissingArgumentException("type is missing");
        }

        if (ipAddress == null){
            throw new MissingArgumentException("ipAddress is missing");
        }

        Map<String, List<String>> userAttributes = new HashMap<String, List<String>>();

        for (String key : queryParams.keySet()) {
            List<String> values = queryParams.get(key);
            if (values != null && values.size() > 0) {
                userAttributes.put(key, values);
            }
        }

        HashMap<String, String> ticketMap = new HashMap<String, String>();

        Ticket ticket = tickets.issueTicket(resources, type, ipAddress, userAttributes);
        for (String resource : ticket.getResources()) {
            ticketMap.put(resource, ticket.getId());
        }
        log.debug("Issued ticket: " + ticket);

        return ticketMap;
    }






    /*Resolving of tickets*/


    @GET
    @Path("resolveTicket")
    @Produces({MediaType.APPLICATION_JSON})
    public Ticket resolveTicket(
            @QueryParam("ticket")
            String ticketID)
            throws TicketNotFoundException {
        log.trace("Entered resolveTicket with param ID='"+ticketID+"'");
        Ticket ticket = tickets.getTicketFromID(ticketID);
        if (ticket == null){
            throw new TicketNotFoundException("The ticket ID '"+ticketID+"' was not found in the system");
        }
        log.trace("Found ticket='"+ticket.getId()+"'");
        return ticket;
    }

    @GET
    @Path("resolveTicket/{ticket}")
    @Produces({MediaType.APPLICATION_JSON})
    public Ticket resolveTicketAlt(
            @PathParam("ticket")
            String ticketID)
            throws TicketNotFoundException {
        return resolveTicket(ticketID);
    }




}
