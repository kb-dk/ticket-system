package dk.statsbiblioteket.medieplatform.ticketsystem;


import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ticket", propOrder = {
        "id",
        "ipAddress",
        "userAttributes",
        "type",
        "resources",
        "properties"
})
@XmlRootElement
public class Ticket{
    /**
     * The ticket id
     * */
    @XmlElement(nillable = false, required = true)
    private String id;

    /*The presentation type that this ticket allows access to
    * */
    @XmlElement(nillable = false, required = true)
    private String type;

    /*The user identifier, probably his IP address. The ticket should only be valid for the user with this identifier*/
    @XmlElement(nillable = false, required = true)
    private String ipAddress;

    /*The resources that this ticket provides access to*/
    @XmlElement(nillable = false, required = true)
    private List<String> resources;

    /* The user attributes. Mainly preserved for logging purposes*/
    @XmlElement(nillable = false, required = true)
    private Map<String, List<String>> userAttributes;

    /*Additionel properties to have in the ticket*/
    @XmlElement(nillable = false, required = false)
    List<Property> properties;

    public Ticket() {
    }

    public Ticket(String type, String ipAddress, List<String> resources, Map<String, List<String>> userAttributes) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.ipAddress = ipAddress;
        this.resources = resources;
        this.userAttributes = userAttributes;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", resources=" + resources +
                ", userAttributes=" + userAttributes +
                ", properties=" + properties +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public List<String> getResources() {
        return resources;
    }

    public Map<String, List<String>> getUserAttributes() {
        return userAttributes;
    }

    public List<Property> getProperties() {
        return properties;
    }
}