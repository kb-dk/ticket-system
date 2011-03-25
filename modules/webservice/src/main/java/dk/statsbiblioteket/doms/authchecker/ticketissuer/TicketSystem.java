package dk.statsbiblioteket.doms.authchecker.ticketissuer;

import dk.statsbiblioteket.util.caching.TimeSensitiveCache;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: abr + mar
 * Date: Oct 7, 2010
 * Time: 3:19:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TicketSystem {


    private TimeSensitiveCache<String, Ticket> tickets;

    private static final Random random = new Random();

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


    /**
     * Issue a new ticket for the given username and url
     * @param username the username to associate
     * @param url the url to associate
     * @return a new ticket with a unique id
     */
    public Ticket issueTicket(String username, String url){
        String id = generateID(username,url);
        Ticket ticket = new Ticket(id, url, username);
        tickets.put(id,ticket);
        return ticket;
    }

    public Ticket issueTicket(String username, String url, String... values){

        List<Property> properties = new ArrayList<Property>(values.length);
        for (int i = 0; i < values.length; i+=1) {
            String[] bits = values[i].split("\\@", 2);
            if (bits.length != 2){
                continue;
            }
            String name = bits[0];
            String value = bits[1];
            properties.add(new Property(name,value));
        }

        String id = generateID(username,url);
        Ticket ticket = new Ticket(id, url, username,properties);
        tickets.put(id,ticket);
        return ticket;
    }



    private String generateID(String username, String url) {
        return UUID.randomUUID().toString();
       //return username+"@"+url+"@"+random.nextInt();
    }


}
