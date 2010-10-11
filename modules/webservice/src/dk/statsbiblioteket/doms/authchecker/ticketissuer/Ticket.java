package dk.statsbiblioteket.doms.authchecker.ticketissuer;

import dk.statsbiblioteket.doms.authchecker.Cacheble;

import javax.xml.bind.annotation.*;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ticket", propOrder = {
        "ID",
        "url",
        "username"
})
@XmlRootElement
public class Ticket implements Cacheble{


    private String ID;

    private String url;

    private String username;

    @XmlTransient
    private long creationTime = new Date().getTime();

    public Ticket() {
    }

    public Ticket(String ID, String url, String username) {
        this.ID = ID;
        this.url = url;
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getID() {
        return ID;
    }
}
