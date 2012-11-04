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
import org.skinnyelephant.framework.world.Disposable;
import org.skinnyelephant.framework.world.Entity;
import org.skinnyelephant.framework.world.World;

/**
 * Base abstract class for entity systems.
 *
 * @author Kristaps Kohs
 */
public abstract class EntitySystem implements Disposable {
    /** Bitmap of used components by this system. */
    private long usedComponents;
    /** Flag indicating if system is passive. */
    private boolean passive;
    /** Flag indicating if system processing is periodic */
    private boolean periodic;
    /** Period in which system is required to be processed. */
    private final float period;
    /** Accumulated delta time since last processing. */
    private float accumulatedDelta;
    /** Reference tho framework {@link World}. */
    protected World world;


    /**
     * Constructor for creating active entity system.
     *
     * @param world framework {@link World}
     */
    public EntitySystem() {
        this(false);
    }

    /**
     * Constructor for creating active/passive entity system.
     *
     * @param world   framework {@link World}
     * @param passive flag weather or not system is passive.
     */
    public EntitySystem(boolean passive) {
        this(passive, false, 0);
    }

    /**
     * Constructor for creating active/passive entity system.
     *
     * @param world    framework {@link World}
     * @param periodic flag indicating if system is periodic.
     * @param period   system processing period.
     */
    public EntitySystem(boolean periodic, float period) {
        this(false, periodic, period);
    }

    /**
     * Constructor for creating active/passive and or periodic or not entity system.
     *
     * @param world    framework {@link World}
     * @param passive  flag weather or not system is passive.
     * @param periodic flag indicating if system is periodic.
     * @param period   system processing period.
     */
    public EntitySystem(boolean passive, boolean periodic, float period) {
        this.passive = passive;
        this.periodic = periodic;
        this.period = period;
    }

    /** Abstract method for initializing system. */
    public abstract void initialize();

    /**
     * Method for processing given {@link Entity}.
     *
     * @param entity entity to process
     */
    public abstract void processEntity(final Entity entity);

    /**
     * Method for processing Immutable set of entities passed by framework {@link World}.
     *
     * @param entities set of entities to process.
     */
    public void processEntities(final ImmutableSet<Entity> entities) {
        for (Entity e : entities) {
            processEntity(e);
        }
    }

    /** Method for processing system. */
    public void processSystem() {
    }

    /**
     * Method for adding given Component type to be used by this system.
     *
     * @param comp component type.
     */
    protected void addUsedComponent(final Class<?> comp) {
        usedComponents |= world.getComponentId(comp);
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

    public final void setWorld(World world) {
        this.world = world;
    }
}
