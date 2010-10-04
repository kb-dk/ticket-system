package dk.statsbiblioteket.doms.authchecker.userdatabase;

import dk.statsbiblioteket.doms.authchecker.user.User;
import dk.statsbiblioteket.doms.authchecker.user.Roles;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 4, 2010
 * Time: 12:06:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserTimestamp extends User{

    private User user;

        private Date lastAccessed;

    public UserTimestamp(User user, Date date) {
        this.user = user;
    }



    public Date getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public void setUsername(String value) {
        user.setUsername(value);
    }

    public List<Roles> getAttributes() {
        return user.getAttributes();
    }

    public String getPassword() {
        return user.getPassword();
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }
}
