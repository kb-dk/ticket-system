package dk.statsbiblioteket.doms.authchecker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 7, 2010
 * Time: 3:51:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class  TimeSensitiveCache<T extends Cacheble> {

    private Log log = LogFactory.getLog(TimeSensitiveCache.class);

    private LinkedHashMap<String,T> elements;

    private Date lastClean;

    private long timeToLive;

    public TimeSensitiveCache(long timeToLive) {
        log.trace("Creating a new TimeSensitiveCache with timeToLive='"+timeToLive+"'");
        this.timeToLive = timeToLive;
        elements = new LinkedHashMap<String,T>();
        lastClean = new Date();
    }


    /**
     * Get the element with the associated ID. If the element is not found, return
     * null
     * @param id the element id
     * @return the element, or null if not found
     */
    public synchronized T getElement(String id){
        log.trace("Entered getElement with id='"+id+"'");
        cleanup();
        return elements.get(id);
    }


    /**
     * Insert the element in the cache.
     * @param element the element to insert.
     */
    public synchronized void addElement(T element){
        log.trace("Entered addElement with element '"
                  +element.toString()+"' with id='"+element.getID()+"'");
        cleanup();
        String id = element.getID();
        elements.put(id,element);
    }

    private synchronized void cleanup(){
        log.trace("Entered cleanup");
        if (!isToOld(lastClean.getTime())){
            log.trace("Did a recent cleanup, aborting");
            return;
        }

        lastClean = new Date();
        if (elements.isEmpty()){
            log.trace("No elements in cache, aborting");
            return;
        }

        Iterator<T> iterator = elements.values().iterator();
        T element;
        while (iterator.hasNext()){
            element = iterator.next();
            if (isToOld(element.getCreationTime())){
                log.trace("Removing element='"+element.getID()
                          +"' as it got to old");
                iterator.remove();
            } else {
                break;
            }
        }
    }

    private  synchronized boolean isToOld(long event) {
        Date now = new Date();
        if (new Date(event+timeToLive).after(now)){
            return false;
        } else {
            return true;
        }
    }


}
