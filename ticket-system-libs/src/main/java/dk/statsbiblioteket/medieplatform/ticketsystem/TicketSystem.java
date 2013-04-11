package dk.statsbiblioteket.medieplatform.ticketsystem;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * TODO
 * Created by IntelliJ IDEA.
 * User: abr + mar
 * Date: Oct 7, 2010
 * Time: 3:19:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TicketSystem {


    private final MemcachedClient memCachedTickets;
    private final Authorization authorization;
    private final int timeToLive;
    private final ObjectMapper mapper;

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

    public Ticket issueTicket(List<String> resources,
                              String type,
                              String userIdentifier,
                              Map<String, List<String>> userAttributes) {

        List<String> authorizedResources = authorization.authorizeUser(userAttributes, type, resources);
        Ticket ticket = new Ticket(type, userIdentifier, authorizedResources, userAttributes);
        if (!ticket.getResources().isEmpty()) {//No need to preserve the ticket, if it does not allow access to anything
            try {
                String ticketString = mapper.writeValueAsString(ticket);
                OperationFuture<Boolean> added = memCachedTickets.add(ticket.getId(), timeToLive, ticketString);
                while (!added.isDone()){
                    try {
                        //expo backoff
                        Thread.sleep(10);
                    } catch (InterruptedException e) {

                    }
                    //TODO timeout

                }
            } catch (IOException e) {
                //Jackson reading from string, should never happen
                //ignore
            }
        }
        return ticket;
    }
}
