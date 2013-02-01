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

package org.skinnyelephant.framework.core;

import org.reflections.Reflections;
import org.skinnyelephant.framework.annotations.Component;
import org.skinnyelephant.framework.systems.EntitySystem;

import java.util.*;

/**
 * Base framework class containing manages,systems input processors.
 *
 * @author Kristaps Kohs
 */
public final class Core implements Disposable {
    /**
     * List of systems in this core.
     */
    private final List<EntitySystem> systems;
    /**
     * Map containing all managers of this core.
     */
    private final Map<Class<? extends Manager>, Manager> managers = new HashMap<Class<? extends Manager>, Manager>();
    /**
     * Flag indicating if core has been initialized.
     */
    private boolean initialized;
    /**
     * Manager responsible for managing components.
     */
    private ComponentManager componentManager;
    /**
     * Manager responsible for managing entities.
     */
    private EntityManager entityManager;
    /**
     * Manager responsible for pooling entities
     */
    private PoolManager poolManager;

    /**
     * Constructor for creating framework core.
     */
    public Core() {
        this.systems = new ArrayList<EntitySystem>();
        this.componentManager = new ComponentManager();
        this.entityManager = new EntityManager(this);
        this.poolManager = new PoolManager(this);
    }

    /**
     * <p>Initializes core.</p>
     * <p>In this method all packages are scanned for classes containing annotation {@link Component} and registered into core.</p>
     * <p>Also {@link EntityManager} and {@link org.skinnyelephant.framework.core.ComponentManager#initialize()}  method is called.</p>
     */
    public final void initialize() {
        if (initialized) {
            throw new IllegalStateException("Core has been initialized already");
        }
        entityManager.initialize();
        componentManager.initialize();
        poolManager.initialize();
        Reflections reflection = new Reflections("");
        Set<Class<?>> components = reflection.getTypesAnnotatedWith(Component.class);

        for (Class<?> component : components) {
            componentManager.getComponentId(component);
        }

        initialized = true;
    }

    /**
     * <p>Main {@link Core} processing method.</p>
     * <p>Iterates through all {@link EntitySystem} and retrieves required entities and passes them to system processing.</p>
     *
     * @deprecated use {@link Core#process(float)} instead for periodic system processing support.
     */
    @Deprecated
    public final void process() {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        for (EntitySystem system : systems) {
            system.processSystem();
            if (!system.isPassive()) {
                system.processEntities(entityManager.getEntitiesForSystem(system));
            }
        }
    }

    /**
     * <p>Main {@link Core} processing method.</p>
     * <p>Iterates through all {@link EntitySystem} and retrieves required entities and passes them to system processing.</p>
     *
     * @param delta delta time.
     */
    public final void process(final float delta) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }

        for (EntitySystem system : systems) {
            if (!system.isPeriodic()) {
                system.processSystem();
                if (!system.isPassive()) {
                    system.processEntities(entityManager.getEntitiesForSystem(system));
                }
            } else {
                if (system.isProcessingRequired(delta)) {
                    system.processSystem();
                    if (!system.isPassive()) {
                        system.processEntities(entityManager.getEntitiesForSystem(system));
                    }
                }
            }
        }

        poolManager.cleanUpPool(delta);
    }

    /**
     * Method for adding {@link EntitySystem} to {@link Core}, and also calls {@link org.skinnyelephant.framework.systems.EntitySystem#initialize()} method.
     *
     * @param system {@link EntitySystem} to add to {@link Core}.
     */
    public final void addSystem(final EntitySystem system) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        system.setCore(this);
        system.initialize();
        systems.add(system);
    }

    /**
     * Delegate method for retrieving component id from {@link ComponentManager} .
     *
     * @param comp Class of component to retrieve id for.
     * @return Component Id.
     */
    public final long getComponentId(final Class<?> comp) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        return componentManager.getComponentId(comp);
    }

    /**
     * Creates {@link Entity} without reference and registers it to {@link Core}
     *
     * @return Created entity.
     */
    public final Entity createEntity() {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        return createEntity(null);
    }

    /**
     * Creates {@link Entity} with reference and registers it to {@link Core}
     *
     * @param reference Entity reference
     * @return Created entity.
     */
    public final Entity createEntity(final String reference) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        Entity e = new Entity(reference, this);
        entityManager.addEntity(e);
        return e;
    }

    public final Entity createPooledEntity(Class<?>... components) {
        Entity e = poolManager.createPooledEntity(components);
        if (entityManager.getEntity(e.getEntityId()) == null) {
            entityManager.addEntity(e);
        }
        return e;
    }

    /**
     * Removes {@link Entity} with given reference from  {@link Core}
     *
     * @param reference Entity reference
     * @return Created entity.
     */
    public final Entity removeEntity(final String reference) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        Entity e = entityManager.getEntity(reference);
        if (e.isPooled()) {
            poolManager.destroyPooledEntity(e);
        }
        entityManager.removeEntity(e);
        return e;
    }

    /**
     * Removes {@link Entity} with given id from  {@link Core}
     *
     * @param id Entity id
     * @return Created entity.
     */
    public final Entity removeEntity(final long id) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        Entity e = entityManager.getEntity(id);
        if (e.isPooled()) {
            poolManager.destroyPooledEntity(e);
        }
        entityManager.removeEntity(e);
        return e;
    }

    /**
     * Getter for {@link EntityManager}
     *
     * @return This worlds {@link EntityManager}
     */
    public final EntityManager getEntityManager() {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        return entityManager;
    }

    /**
     * Setter for {@link EntityManager}
     *
     * @param manager {@link EntityManager} to set.
     */
    public final void setEntityManager(final EntityManager manager) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        this.entityManager = manager;
    }

    public PoolManager getPoolManager() {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        return poolManager;
    }

    public void setPoolManager(PoolManager poolManager) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        this.poolManager = poolManager;
    }

    /**
     * Getter for this worlds {@link EntitySystem}'s
     *
     * @return This worlds {@link EntitySystem}'s
     */
    public final List<EntitySystem> getSystems() {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        return systems;
    }

    /**
     * Getter for {@link ComponentManager}
     *
     * @return This worlds {@link ComponentManager}
     */
    public final ComponentManager getComponentManager() {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        return componentManager;
    }

    /**
     * Setter for {@link ComponentManager}
     *
     * @param manager {@link ComponentManager} to set.
     */
    public final void setComponentManager(final ComponentManager manager) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        this.componentManager = manager;
    }

    /**
     * Method for removing {@link EntitySystem} from this core. Also calls {@link EntitySystem} destroy() method.
     *
     * @param system to remove.
     */
    public final void removeSystem(final EntitySystem system) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        systems.remove(system);
        system.dispose();
    }

    /**
     * Method for adding {@link Manager} to this core.
     *
     * @param manager {@link Manager}  to add.
     * @param <T>     class that implements {@link Manager}.
     */
    public final <T extends Manager> void addManager(final T manager) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        manager.initialize();
        managers.put(manager.getClass(), manager);
    }

    /**
     * Method for getting {@link Manager} based on given type.
     *
     * @param type Manager class.
     * @return Manager
     */
    @SuppressWarnings("unchecked")
    public final <T extends Manager> T getManager(final Class<? extends Manager> type) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        return (T) managers.get(type);
    }

    /**
     * Method for removing manager from this core.
     *
     * @param type Manager class to remove.
     */
    public final void removeManager(final Class<? extends Manager> type) {
        if (!initialized) {
            throw new IllegalStateException("Core has not been initialized!");
        }
        Manager m = managers.get(type);
        m.dispose();
        managers.remove(type);
    }

    @Override
    public final void dispose() {
        if (!initialized) {
            throw new IllegalStateException("Core is not initialized.");
        }
        entityManager.dispose();
        componentManager.dispose();
        poolManager.dispose();

        for (Manager manager : managers.values()) {
            manager.dispose();
        }

        for (EntitySystem system : systems) {
            system.dispose();
        }
        systems.clear();
        managers.clear();
        initialized = false;
    }
}
