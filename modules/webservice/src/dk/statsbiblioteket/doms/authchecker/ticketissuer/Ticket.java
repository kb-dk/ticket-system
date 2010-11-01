package dk.statsbiblioteket.doms.authchecker.ticketissuer;


import javax.xml.bind.annotation.*;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ticket", propOrder = {
        "ID",
        "url",
        "username"
})
@XmlRootElement
public class Ticket{


    private String ID;

    private String url;

    private String username;

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

   
    public String getID() {
        return ID;
    }
}
