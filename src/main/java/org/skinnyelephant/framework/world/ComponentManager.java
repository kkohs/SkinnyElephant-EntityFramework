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
 * Class for managing components, generating and storing component Id's.
 *
 * @author Kristaps Kohs
 */
public class ComponentManager implements Manager {
    /** Flag if manager is initialized. */
    private boolean initialized;
    /** Map containing all component ids. */
    private Map<Class, Long> componentIds;

    /**
     * Method for getting component id based on its class or generating new if id is not present in framework.
     *
     * @param component component class.
     * @return component id.
     */
    protected final long getComponentId(Class<?> component) {
        if (!initialized) {
            throw new IllegalStateException("Manager not initialized");
        }
        if (component == null) {
            throw new NullPointerException("Component type cannot be null.");
        }
        Long id = componentIds.get(component);
        if (id == null) {
            id = (long) Math.pow(2, componentIds.size());
            componentIds.put(component, id);
        }
        return id;
    }

    @Override
    public void initialize() {
        if (initialized) {
            throw new IllegalStateException("Manager already initialized");
        }
        initialized = true;
        this.componentIds = new HashMap<Class, Long>();
    }

    @Override
    public void dispose() {
        if (!initialized) {
            throw new IllegalStateException("Manager not initialized");
        }
        componentIds.clear();
        initialized = false;
    }
}
