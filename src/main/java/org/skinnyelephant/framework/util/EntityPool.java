package org.skinnyelephant.framework.util;

import org.skinnyelephant.framework.world.Entity;

/**
 * Class for pooling entities with same compoennts.
 * Date: 13.1.2
 * Time: 12:45
 *
 * @author Kristaps Kohs
 */
public class EntityPool {
    /**
     * Array of pooled entities.
     */
    private Entity[] entities;
    /**
     * Index of current entity to return.
     */
    private int currentEntity;

    public EntityPool() {
        entities = new Entity[32];
    }

    /**
     * Returns entity from pool or null if entity does not exist.
     * @return  entity
     */
    public Entity get() {
        if(currentEntity < 0) return null;
        return entities[currentEntity--];
    }


    /**
     * Returns entity back to pool when its no longer used.
     * @param e to return to pool.
     */
    public void put(Entity e) {
        if(entities.length >= currentEntity) {
            final int newLength = entities.length * 2;
            final Entity[] newArray = new Entity[newLength];
            System.arraycopy(entities,0,newArray,0,entities.length);
            entities = newArray;
            entities[++currentEntity] = e;
        }  else {
            entities[++currentEntity] = e;
        }
    }
}
