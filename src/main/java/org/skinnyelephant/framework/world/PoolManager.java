/*
 * Copyright 2012  Kristaps Kohs<kristaps.kohs@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.skinnyelephant.framework.world;

import org.skinnyelephant.framework.util.EntityPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for pooling entities to be reused later to avoid allocating new memory for creating new entities that are used often..
 * Date: 13.1.2
 * Time: 12:37
 *
 * @author Kristaps Kohs
 */
public class PoolManager implements Manager {
    /**
     * Core class.
     */
    private Core frameWorkCore;
    /**
     * Map containing Entity pool with specific id.
     */
    private Map<Long, EntityPool> pooledEntities;
    /**
     * Flag indicating if manager has been initialized.
     */
    private boolean initialized;
    /**
     * Period in which pool should be cleaned.
     */
    private float removalPeriod;
    /**
     * Current time.
     */
    private float accumulatedDelta;

    /**
     * Created new Entity Pool manager.
     *
     * @param frameWorkCore frameworks core.
     */
    public PoolManager(Core frameWorkCore) {
        this.frameWorkCore = frameWorkCore;
    }

    private int singleMaxPoolSize = 512;

    @Override
    public void initialize() {
        if (initialized) {
            throw new IllegalStateException("Manager already initialized!");
        }
        pooledEntities = new HashMap<Long, EntityPool>();
        initialized = true;
    }

    protected Entity createPooledEntity(Class<?>... components) {
        if (!initialized) {
            throw new IllegalStateException("Manager not initialized");
        }
        long componentIds = 0;
        for (Class<?> component : components) {
            componentIds |= frameWorkCore.getComponentId(component);
        }

        if (pooledEntities.containsKey(componentIds)) {
            Entity e = pooledEntities.get(componentIds).get();
            if (e == null) {
                return createEntityWithComponents(components);
            } else {
                return e;
            }
        } else {
            pooledEntities.put(componentIds, new EntityPool(singleMaxPoolSize));
            return createEntityWithComponents(components);
        }
    }

    public void destroyPooledEntity(Entity entity) {
        if (!initialized) {
            throw new IllegalStateException("Manager not initialized");
        }
        final long componentIds = entity.getComponentsIds();
        if (pooledEntities.containsKey(componentIds)) {
            pooledEntities.get(componentIds).put(entity);
        } else {
            pooledEntities.put(componentIds, new EntityPool(singleMaxPoolSize));
            pooledEntities.get(componentIds).put(entity);
        }
    }

    private Entity createEntityWithComponents(Class<?>... components) {
        if (!initialized) {
            throw new IllegalStateException("Manager not initialized");
        }
        Entity e = frameWorkCore.createEntity();
        e.setPooled(true);
        for (Class<?> component : components) {
            try {
                e.addComponent(component.newInstance());
            } catch (Exception e1) {
                throw new IllegalStateException("Failed to create pooled component", e1);
            }
        }
        return e;
    }

    /**
     * Cleans up entity pool if period delta > {@link PoolManager#removalPeriod}.
     *
     * @param delta time passed in milliseconds.
     */
    public void cleanUpPool(final float delta) {
        if (!initialized) {
            throw new IllegalStateException("Manager not initialized");
        }
        if (removalPeriod <= 0) {
            return;
        }
        if ((accumulatedDelta += (delta * .001f)) >= removalPeriod) {
            for (EntityPool entityPool : pooledEntities.values()) {
                entityPool.releaseFromPool();
            }
            accumulatedDelta = 0;
        }
    }

    /**
     * Sets removal period in seconds.
     *
     * @param removalPeriod period in seconds
     */
    public void setRemovalPeriod(float removalPeriod) {
        this.removalPeriod = removalPeriod;
    }

    @Override
    public void dispose() {
        if (!initialized) {
            throw new IllegalStateException("Manager not initialized");
        }
        pooledEntities.clear();
    }

    public int getSingleMaxPoolSize() {
        return singleMaxPoolSize;
    }

    public void setSingleMaxPoolSize(int singleMaxPoolSize) {
        this.singleMaxPoolSize = singleMaxPoolSize;
    }
}
