package dk.statsbiblioteket.doms.authchecker;

/**
 * Interface to implement to use the TimeSensitiveCache.
 *
 * Objects must implement these two methods, to ensure they are correctly used by
 * the system
 * @see dk.statsbiblioteket.doms.authchecker.TimeSensitiveCache
 */
public interface Cacheble {

    /**
     * Get the time when this element was created. To be used to determine when
     * to remove the element from the cache.
     * @return the creation time, in standard Date format.
     */
    public long getCreationTime();


    /**
     * The unique id of this element. Elements are identified by this id, so
     * overlaps will create overwrites.
     * @return the unique id
     */
    public String getID();
}
