package org.skinnyelephant.framework.util;

import java.util.LinkedList;

/**
 * Simple class for generating and reusing ids.
 *
 * @author Kristaps Kohs
 */
public class EntityIdGeneratorImpl implements EntityIdGenerator {
    /** List of already generated but not used ids. */
    private final LinkedList<Long> idList;
    /** Next id to be assigned. */
    private long nextId;

    /** Constructor for this generator. */
    public EntityIdGeneratorImpl() {
        idList = new LinkedList<Long>();
    }

    @Override
    public long getId() {
        if (!idList.isEmpty()) {
            return idList.removeLast();
        }
        return nextId++;
    }

    @Override
    public void removeId(final long id) {
        idList.add(id);
    }

    @Override
    public void reset() {
        idList.clear();
        nextId = 0;
    }
}
