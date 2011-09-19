package dk.statsbiblioteket.doms.authchecker.ticketissuer;


import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ticket", propOrder = {
        "ID",
        "resource",
        "username",
        "property"
})
@XmlRootElement
public class Ticket{

    @XmlElement(nillable = false, required = true)
    private String ID;

    @XmlElement(nillable = false, required = true)
    private String resource;

    @XmlElement(nillable = false, required = true)
    private String username;

    List<Property> property;

    public Ticket() {
    }

    public Ticket(String ID, String resource, String username) {
        this.ID = ID;
        this.resource = resource;
        this.username = username;
        property = new ArrayList<Property>();
    }

    public Ticket(String ID, String resource, String username, List<Property> property) {
        this.ID = ID;
        this.resource = resource;
        this.username = username;
        this.property = property;
    }

    public String getResource() {
        return resource;
    }

    public String getUsername() {
        return username;
    }

   
    public String getID() {
        return ID;
    }

    public List<Property> getProperty() {
        return property;
    }
}
