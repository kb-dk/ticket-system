package dk.statsbiblioteket.medieplatform.ticketsystem.webservice;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import dk.statsbiblioteket.medieplatform.ticketsystem.FaultBarrier;

public class TicketSystemServiceApplication extends Application {

    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.asList(
            JacksonJsonProvider.class,
            TicketSystemService.class,
            FaultBarrier.class)
            );
    }

    
}
