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

package org.skinnyelephant.framework.systems;

import com.google.common.collect.ImmutableSet;
import org.skinnyelephant.framework.core.Core;
import org.skinnyelephant.framework.core.Disposable;
import org.skinnyelephant.framework.core.Entity;
import org.skinnyelephant.framework.core.Manager;

/**
 * Base abstract class for entity systems.
 *
 * @author Kristaps Kohs
 */
public abstract class EntitySystem implements Disposable {
    /**
     * Period in which system is required to be processed.
     */
    private final float period;
    /**
     * Reference tho framework {@link org.skinnyelephant.framework.core.Core}.
     */
    protected Core core;
    /**
     * Bitmap of used components by this system.
     */
    private long usedComponents;
    /**
     * Flag indicating if system is passive.
     */
    private boolean passive;
    /**
     * Flag indicating if system processing is periodic
     */
    private boolean periodic;
    /**
     * Accumulated delta time since last processing.
     */
    private float accumulatedDelta;

    /**
     * Constructor for creating active entity system.
     */
    public EntitySystem() {
        this(false);
    }

    /**
     * Constructor for creating active/passive entity system.
     *
     * @param passive flag weather or not system is passive.
     */
    public EntitySystem(boolean passive) {
        this(passive, false, 0);
    }

    /**
     * Constructor for creating active/passive entity system.
     *
     * @param periodic flag indicating if system is periodic.
     * @param period   system processing period.
     */
    public EntitySystem(boolean periodic, float period) {
        this(false, periodic, period);
    }

    /**
     * Constructor for creating active/passive and or periodic or not entity system.
     *
     * @param passive  flag weather or not system is passive.
     * @param periodic flag indicating if system is periodic.
     * @param period   system processing period.
     */
    public EntitySystem(boolean passive, boolean periodic, float period) {
        this.passive = passive;
        this.periodic = periodic;
        this.period = period;
    }

    /**
     * Abstract method for initializing system.
     */
    public abstract void initialize();

    /**
     * Method for processing given {@link Entity}.
     *
     * @param entity entity to process
     */
    public abstract void processEntity(final Entity entity);

    /**
     * Method for processing Immutable set of entities passed by framework {@link org.skinnyelephant.framework.core.Core}.
     *
     * @param entities set of entities to process.
     */
    public void processEntities(final ImmutableSet<Entity> entities) {
        for (Entity e : entities) {
            processEntity(e);
        }
    }

    /**
     * Method for processing system.
     */
    public void processSystem() {
    }

    /**
     * Method for adding given Component type to be used by this system.
     *
     * @param comp component type.
     */
    protected void addUsedComponent(final Class<?> comp) {
        usedComponents |= core.getComponentId(comp);
    }

    /**
     * Returns bitmap of used components.
     *
     * @return used component bitmap.
     */
    public long getUsedComponents() {
        return usedComponents;
    }

    /**
     * Getter for passive flag.
     *
     * @return flag.
     */
    public final boolean isPassive() {
        return passive;
    }

    /**
     * Getter for periodic flag.
     *
     * @return periodic flag.
     */
    public final boolean isPeriodic() {
        return periodic;
    }

    /**
     * Method for checking weather or not system requires processing based on given delta time.
     *
     * @param delta time.
     * @return true if processing is required.
     */
    public final boolean isProcessingRequired(final float delta) {
        if (!periodic || period < 1) return true;
        accumulatedDelta += delta;
        if (accumulatedDelta >= period) {
            accumulatedDelta = 0;
            return true;
        }
        return false;
    }

    private <T extends Manager> T getManager(final Class<? extends Manager> type) {
        return core.getManager(type);
    }

    private <T> T getComponent(final Entity e, final Class<?> type) {
        return e.getComponent(type);
    }

    public final void setCore(Core core) {
        this.core = core;
    }
}
