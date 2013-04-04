package dk.statsbiblioteket.ticketsystem;

import dk.statsbiblioteket.util.caching.TimeSensitiveCache;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: abr + mar
 * Date: Oct 7, 2010
 * Time: 3:19:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TicketSystem {


    private TimeSensitiveCache<String, Ticket> tickets;

    public TicketSystem(long timeToLive) {
        tickets = new TimeSensitiveCache<String, Ticket>(timeToLive, false);//30 sec
    }



    /**
     * Get the ticket from the system
     * @param id the id of the ticket
     * @return the ticket, or null of the ticket is not found
     */
    public Ticket getTicketFromID(String id){
        return tickets.get(id);
    }

    @Deprecated
    public Ticket issueTicket(String username,
                              String url,
                              MultivaluedMap<String,String> inProps){

        List<Property> properties = new ArrayList<Property>();
        for (Map.Entry<String, List<String>> listEntry : inProps.entrySet()) {
            for (String value : listEntry.getValue()) {
                properties.add(new Property(listEntry.getKey(),value));
            }
        }
        String id = generateID(username,url);
        Ticket ticket = new Ticket(id, new ContentResource(url,null), username,properties,null);
        tickets.put(id,ticket);
        return ticket;
    }



    private String generateID(String username, String url) {
        return UUID.randomUUID().toString();
    }

    public Map<String,Ticket> issueTickets(List<String> uuids,
                                           List<String> userAttributes,
                                           String type){
        return null;
    }


}
