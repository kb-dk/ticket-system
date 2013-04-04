package dk.statsbiblioteket.medieplatform.ticketsystem;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 4/3/13
 * Time: 1:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContentResource {

    //This should probably be the UUID
    private final String identifier;

    private final String type;

    public ContentResource(String identifier, String type) {
        this.identifier = identifier;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }
}
