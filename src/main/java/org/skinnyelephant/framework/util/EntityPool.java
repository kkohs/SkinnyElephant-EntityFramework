package org.skinnyelephant.framework.util;

import org.skinnyelephant.framework.world.Entity;

import java.util.LinkedList;

/**
 * Class for pooling entities with same compoennts.
 * Date: 13.1.2
 * Time: 12:45
 *
 * @author Kristaps Kohs
 */
public class EntityPool {

    private static final int MIN_ENTITIES_IN_POOL = 32;
    private static final int ONE_STEP_REMOVAL = 8;
    /**
     * Array of pooled entities.
     */
    private LinkedList<Entity> entities;

    public EntityPool() {
        entities = new LinkedList<Entity>();
    }

    /**
     * Returns entity from pool or null if entity does not exist.
     * @return  entity
     */
    public Entity get() {
        if(entities.size() <= 0) return null;
        return entities.removeFirst();
    }


    /**
     * Returns entity back to pool when its no longer used.
     * @param e to return to pool.
     */
    public void put(Entity e) {
        entities.add(e);
    }

    /**
     * Releases (N) entities from this pool, if pool has contains less entities than {@link EntityPool#MIN_ENTITIES_IN_POOL} no entities are removed.
     */
    public void releaseFromPool() {
        if(entities.size() < MIN_ENTITIES_IN_POOL + ONE_STEP_REMOVAL)return;

       int removed = 0;
        while (removed < ONE_STEP_REMOVAL) {
            entities.removeLast();
            removed ++;
        }
    }
}
