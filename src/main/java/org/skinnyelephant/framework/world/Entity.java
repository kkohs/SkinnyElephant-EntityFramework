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


import java.util.HashMap;
import java.util.Map;

/**
 * Class representing entity in this framework.
 *
 * @author Kristaps Kohs
 */
public final class Entity implements Disposable {
    /** Bitmap of used components. */
    private long componentsIds;
    /** Unique Entity ID. */
    private long eID;
    /** Entity reference. */
    private String reference;
    /** Reference to framework {@link Core} */
    private Core core;
    /** Map of used components. */
    private Map<Class<?>, Object> components;
    /**Flag indicating whether entity should be pooled.*/
    private boolean isPooled;

    /**
     * Constructor for creating entity with string reference.
     *
     * @param reference entity reference.
     * @param core     framework {@link Core}
     */
    protected Entity(String reference, Core core) {
        this.reference = reference;
        this.components = new HashMap<Class<?>, Object>();
        this.core = core;
    }

    /**
     * Constructor for creating entity without string reference.
     *
     * @param core framework {@link Core}
     */
    public Entity(final Core core) {
        this(null, core);
    }

    /**
     * Method for adding component to {@link Entity}.
     *
     * @param component Component to be added.
     * @param <T>       Class annotated with {@link org.skinnyelephant.framework.annotations.Component}
     * @return this entity.
     */
    public <T> Entity addComponent(T component) {
        components.put(component.getClass(), component);
        core.getEntityManager().removeFromCache(this);
        componentsIds |= core.getComponentManager().getComponentId(component.getClass());
        core.getEntityManager().addToCache(this);
        return this;
    }

    /**
     * Method for retrieving component based on given component class.
     *
     * @param type Component class.
     * @param <T>  Class annotated with {@link org.skinnyelephant.framework.annotations.Component}
     * @return Component or null if component does not exist.
     */
    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<?> type) {
        Object component = components.get(type);
        if (component != null) {
            return (T) components.get(type);
        } else {
            return null;
        }
    }

    public void removeComponent(Class<?> type) {
        Object component = components.get(type);
        if (component != null) {
            core.getEntityManager().removeFromCache(this);
            componentsIds ^= core.getComponentManager().getComponentId(component.getClass());
            core.getEntityManager().addToCache(this);
        }
    }

    /**
     * Getter for entity reference.
     *
     * @return entity reference.
     */
    public String getReference() {
        return reference;
    }

    /**
     * Getter for framework {@link Core} linked to this entity.
     *
     * @return framework {@link Core}
     */
    public Core getCore() {
        return core;
    }

    /**
     * Getter for bitmap of components.
     *
     * @return
     */
    public long getComponentsIds() {
        return componentsIds;
    }

    /**
     * Getter for this entity id.
     *
     * @return id
     */
    public long getEntityId() {
        return eID;
    }

    /**
     * Setter for this entity id.
     *
     * @param eID id
     */
    public void setEntityId(long eID) {
        this.eID = eID;
    }

    public boolean isPooled() {
        return isPooled;
    }

    public void setPooled(boolean pooled) {
        isPooled = pooled;
    }

    @Override
    public void dispose() {
        for (Object o : components.values()) {
            if (o instanceof Disposable) {
                ((Disposable) o).dispose();
            }
        }
    }
}
