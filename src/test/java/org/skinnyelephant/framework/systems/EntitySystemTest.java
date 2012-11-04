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

import org.junit.Test;
import org.skinnyelephant.framework.world.Entity;
import org.skinnyelephant.framework.world.World;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/** @author Kristaps Kohs */
public class EntitySystemTest {
    @Test
    public void testIsProcessingRequired() throws Exception {
        World world = new World();
        world.initialize();
        MockSystem system = new MockSystem(world, false, true, 50);
        world.addSystem(system);


        for (int i = 0; i < 100; i++) {
            if (i == 49 || i == 99) {
                assertTrue(system.isProcessingRequired(1));
            } else {
                assertFalse(system.isProcessingRequired(1));
            }
        }
    }

    private class MockSystem extends EntitySystem {

        public MockSystem(World world, boolean passive, boolean periodic, float period) {
            super(world, passive, periodic, period);
        }

        @Override
        public void initialize() {

        }

        @Override
        public void processEntity(final Entity entity) {

        }

        @Override
        public void dispose() {

        }
    }
}
