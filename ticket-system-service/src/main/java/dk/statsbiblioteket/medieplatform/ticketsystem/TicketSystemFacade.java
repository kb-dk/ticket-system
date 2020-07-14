package dk.statsbiblioteket.medieplatform.ticketsystem;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.util.YAML;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;

public class TicketSystemFacade {
    private final Logger log = LoggerFactory.getLogger(TicketSystemFacade.class);
    private static TicketSystemFacade instance = null;
    private TicketSystem ticketSystem;
    
    public static synchronized void initialize(YAML serviceConfig) {
        instance = new TicketSystemFacade(serviceConfig);
    }
    
    public static synchronized TicketSystemFacade getInstance() {
        if(instance == null) {
            throw new IllegalStateException("TicketSystemFacade has not been initialized");
        } else {
            return instance;
        }
    }
    
    private TicketSystemFacade(YAML serviceConfig) {
        int ttl = serviceConfig.getInteger("config.ticketsystem.timetolive", 30);

        String authService = serviceConfig.getString("config.ticketsystem.auth-checker");
        Authorization authorization = new Authorization(authService);

        String memcacheServer = serviceConfig.getString("config.ticketsystem.memcache.server"); 
        int memcachePort = serviceConfig.getInteger("config.ticketsystem.memcache.port"); 

        //TODO how is reconnect handled?
        MemcachedClient memCachedTickets;
        try {
            memCachedTickets = new MemcachedClient(new BinaryConnectionFactory(),
                    Arrays.asList(new InetSocketAddress(memcacheServer, memcachePort)));
        } catch (IOException e) {
            throw new Error("Failed to connect to cache, ticket system fails to start", e);
        }


        ticketSystem = new TicketSystem(memCachedTickets, ttl,authorization);
    }
    
    public Map<String, String> issueTicket(List<String> resources, String type, String ipAddress, 
            Map<String, List<String>> userAttributes) {

        HashMap<String, String> ticketMap = new HashMap<String, String>();

        Ticket ticket = ticketSystem.issueTicket(resources, type, ipAddress, userAttributes);
        for (String resource : ticket.getResources()) {
            ticketMap.put(resource, ticket.getId());
        }
        log.debug("Issued ticket: " + ticket);

        return ticketMap;
    }
    
    public Ticket resolveTicket(String ticketID) throws TicketNotFoundException {
        log.trace("Entered resolveTicket with param ID='{}'", ticketID);
        Ticket ticket = ticketSystem.getTicketFromID(ticketID);
        if (ticket == null){
            throw new TicketNotFoundException("The ticket ID '" + ticketID + "' was not found in the system");
        }
        log.trace("Found ticket='{}'", ticket.toString());
        return ticket;
    }
    
}
