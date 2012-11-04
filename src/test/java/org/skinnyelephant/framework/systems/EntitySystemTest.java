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
