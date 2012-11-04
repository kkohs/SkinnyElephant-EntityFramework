package org.skinnyelephant.framework.util;

/** Entity ID generator interface. */
public interface EntityIdGenerator {
    /**
     * Method for getting unique id.
     *
     * @return id.
     */
    public long getId();

    /**
     * Method for removing id.
     *
     * @param id id to be removed.
     */
    public void removeId(long id);

    /** Method for resetting generator. */
    public void reset();
}
