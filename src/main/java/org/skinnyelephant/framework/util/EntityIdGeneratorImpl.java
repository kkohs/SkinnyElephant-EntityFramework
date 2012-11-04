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

package org.skinnyelephant.framework.util;

import java.util.LinkedList;

/**
 * Simple class for generating and reusing ids.
 *
 * @author Kristaps Kohs
 */
public class EntityIdGeneratorImpl implements EntityIdGenerator {
    /** List of already generated but not used ids. */
    private final LinkedList<Long> idList;
    /** Next id to be assigned. */
    private long nextId;

    /** Constructor for this generator. */
    public EntityIdGeneratorImpl() {
        idList = new LinkedList<Long>();
    }

    @Override
    public long getId() {
        if (!idList.isEmpty()) {
            return idList.removeLast();
        }
        return nextId++;
    }

    @Override
    public void removeId(final long id) {
        idList.add(id);
    }

    @Override
    public void reset() {
        idList.clear();
        nextId = 0;
    }
}
