package dk.statsbiblioteket.medieplatform.ticketsystem;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * The backend of the ticket system. The ticket system is implemented as a client to memcached. The tickets are
 * serialized to json and stored/retrieved from the memcache.
 */
public class TicketSystem {


    /**
     * The client to the memcached database
     */
    private final MemcachedClient memCachedTickets;

    /**
     * The authorization object, used to verify that the user is allowed to access the requested resources
     */
    private final Authorization authorization;

    /**
     * The lifetime of a ticket, in SECONDS!!!
     */
    private final int timeToLive;

    /**
     * The json interface, to map objects to and from json.
     */
    private final ObjectMapper mapper;

    /**
     * Create a new ticket system backend. Principly, you could have multiple of these
     * @param memCachedTickets the client to the memcached server
     * @param timeToLive the lifetime of the tickets, in SECONDS!!!
     * @param authorization the authorization object
     */
    public TicketSystem(MemcachedClient memCachedTickets, int timeToLive, Authorization authorization) {
        this.memCachedTickets = memCachedTickets;
        this.timeToLive = timeToLive;
        this.mapper = new ObjectMapper();
        this.authorization = authorization;
    }


    /**
     * Get the ticket from the system
     * @param id the id of the ticket
     * @return the ticket, or null of the ticket is not found
     */
    public Ticket getTicketFromID(String id){
        String ticketString = (String) memCachedTickets.get(id);
        try {
            if (ticketString != null){
                return mapper.readValue(ticketString,Ticket.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            //Jackson reading from string, should never happen
            return null;
        }
    }

    /**
     * Issue a ticket for the given resources, if the user is allowed to access these.
     * The ticket will be issued for the subset of the resources that the user is allowed
     * @param resources the resources the user want to see
     * @param type the type of the resources
     * @param ipAddress the identifier of the user, often the IP
     * @param userAttributes The user attributes, a map of strings to lists of string values
     * @return a ticket for the resources the user can see
     */
    public Ticket issueTicket(List<String> resources,
                              String type,
                              String ipAddress,
                              Map<String, List<String>> userAttributes) {

        List<String> authorizedResources = authorization.authorizeUser(userAttributes, type, resources);
        Ticket ticket = new Ticket(type, ipAddress, authorizedResources, userAttributes);
        if (!ticket.getResources().isEmpty()) {//No need to preserve the ticket, if it does not allow access to anything
            try {
                String ticketString = mapper.writeValueAsString(ticket);
                int backoffExp = 0;
                OperationFuture<Boolean> added = memCachedTickets.add(ticket.getId(), timeToLive, ticketString);

                while ( ! (added.isDone() && backoffExp < 10)){
                    try {
                        Thread.sleep((long) Math.pow(2,backoffExp++));
                    } catch (InterruptedException e) {

                    }
                }
            } catch (IOException e) {
                //Jackson reading from string, should never happen
                //ignore
            }
        }
        return ticket;
    }
}
