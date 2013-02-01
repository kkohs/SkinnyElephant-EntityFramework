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
    private final int absoluteMax;
    private int maxEntitiesInPool;
    private int minEntitiesInPool;
    private int frequency;
    private int oneStepRemoval;
    /**
     * Array of pooled entities.
     */
    private LinkedList<Entity> entities;

    public EntityPool(int absoluteMax) {
        entities = new LinkedList<Entity>();
        minEntitiesInPool = 8;
        maxEntitiesInPool = 64;
        oneStepRemoval = 1;
        frequency = 1;
        this.absoluteMax = absoluteMax;
    }

    /**
     * Returns entity from pool or null if entity does not exist.
     *
     * @return entity
     */
    public Entity get() {
        if (entities.size() <= 0) {
            frequency++;
            return null;
        }
        frequency++;
        if (minEntitiesInPool / frequency > 0) {
            minEntitiesInPool *= 2;
            if(minEntitiesInPool > absoluteMax) {
                minEntitiesInPool = absoluteMax;
            }
        }
        return entities.removeLast();
    }

    /**
     * Returns entity back to pool when its no longer used.
     *
     * @param e to return to pool.
     */
    public void put(Entity e) {
        frequency--;
        if(frequency < 1) frequency = 1;
        if(entities.size() >= maxEntitiesInPool) return;

        float stats = maxEntitiesInPool / frequency;
        if(stats < 1 && (maxEntitiesInPool * 2 <= absoluteMax)) {
            maxEntitiesInPool *= 2;
        } else if(stats > 10) {
            maxEntitiesInPool *= .5;
        }
        entities.add(e);
    }

    /**
     * Releases (N) entities from this pool, if pool has contains less entities than {@link EntityPool#minEntitiesInPool} no entities are removed.
     */
    public void releaseFromPool() {
        oneStepRemoval = minEntitiesInPool/ frequency;
        if (entities.size() <= minEntitiesInPool + oneStepRemoval) {
            minEntitiesInPool -= minEntitiesInPool / 2;
            if(maxEntitiesInPool / entities.size() > 2) {
                maxEntitiesInPool *= .5;
            }
            if(minEntitiesInPool < 4) minEntitiesInPool = 4;
            return;
        }

        int removed = 0;
        while (removed < oneStepRemoval) {
            entities.removeFirst();
            removed++;
        }
        if(frequency < 1) frequency = 1;

    }
}
