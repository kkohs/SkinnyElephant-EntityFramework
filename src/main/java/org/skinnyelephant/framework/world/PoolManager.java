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
     * Created new Entity Pool manager.
     * @param frameWorkCore frameworks core.
     */
    public PoolManager(Core frameWorkCore) {
        this.frameWorkCore = frameWorkCore;
    }

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
            if(e == null) {
                return createEntityWithComponents(components);
            } else {
                return e;
            }
        } else {
            pooledEntities.put(componentIds, new EntityPool());
            return createEntityWithComponents(components);
        }
    }

    public void destroyPooledEntity(Entity entity) {
        if (!initialized) {
            throw new IllegalStateException("Manager not initialized");
        }
        final long componentIds = entity.getComponentsIds();
        if(pooledEntities.containsKey(componentIds)) {
            pooledEntities.get(componentIds).put(entity);
        } else {
            pooledEntities.put(componentIds,new EntityPool());
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
                throw new IllegalStateException("Failed to create pooled component",e1 );
            }
        }
        return e;
    }

    @Override
    public void dispose() {
        if (!initialized) {
            throw new IllegalStateException("Manager not initialized");
        }
          pooledEntities.clear();
    }

}
