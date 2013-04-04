package dk.statsbiblioteket.medieplatform.ticketsystem;


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
    private ContentResource resource;
    //TODO should this be list?


    @XmlElement(nillable = false, required = true)
    private String username;


    /*TODO Serialization??*/
    private String[] userRoles;

    List<Property> property;

    public Ticket() {
    }

    public Ticket(String ID, ContentResource resource, String username, String... userRoles) {
        this.ID = ID;
        this.resource = resource;


        this.username = username;
        this.userRoles = userRoles;
        property = new ArrayList<Property>();
    }

    public Ticket(String ID, ContentResource resource, String username, List<Property> property, String... userRoles) {
        this.ID = ID;
        this.resource = resource;
        this.username = username;
        this.userRoles = userRoles;
        this.property = property;
    }

    public ContentResource getResource() {
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
