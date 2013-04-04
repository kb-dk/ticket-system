package dk.statsbiblioteket.medieplatform.ticketsystem;


import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ticket", propOrder = {
        "ID",
        "userIdentifier",
        "userAttributes",
        "type",
        "resources",
        "properties"
})
@XmlRootElement
public class Ticket{
    @XmlElement(nillable = false, required = true)
    private String ID;

    @XmlElement(nillable = false, required = true)
    private String type;

    @XmlElement(nillable = false, required = true)
    private String userIdentifier;

    @XmlElement(nillable = false, required = true)
    private List<String> resources;

    @XmlElement(nillable = false, required = true)
    private Map<String, List<String>> userAttributes;

    @XmlElement(nillable = false, required = false)
    List<Property> properties;

    public Ticket() {
    }

    public Ticket(String type, String userIdentifier, List<String> resources, Map<String, List<String>> userAttributes) {
        this.ID = UUID.randomUUID().toString();
        this.type = type;
        this.userIdentifier = userIdentifier;
        this.resources = resources;
        this.userAttributes = userAttributes;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ID='" + ID + '\'' +
                ", type='" + type + '\'' +
                ", userIdentifier='" + userIdentifier + '\'' +
                ", resources=" + resources +
                ", userAttributes=" + userAttributes +
                ", properties=" + properties +
                '}';
    }

    public String getID() {
        return ID;
    }

    public String getType() {
        return type;
    }

    public String getUserIdentifier() {
        return userIdentifier;
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