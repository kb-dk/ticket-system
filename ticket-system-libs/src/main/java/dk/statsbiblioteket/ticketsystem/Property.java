package dk.statsbiblioteket.ticketsystem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/25/11
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "property", propOrder = {
        "name",
        "value"
})
public class Property {

    @XmlElement(nillable = false, required = true)
    private String name;
    @XmlElement(nillable = false, required = true)
    private String value;


    public Property() {
    }

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
