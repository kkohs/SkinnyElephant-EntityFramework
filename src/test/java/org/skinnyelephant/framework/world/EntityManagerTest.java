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

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.skinnyelephant.framework.annotations.Component;
import org.skinnyelephant.framework.systems.EntitySystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/** @author Kristaps Kohs */
public class EntityManagerTest {
    @Test
    public void testAddEntity() throws Exception {
        World world = new World();
        world.initialize();
        Entity e = new Entity(world);
        Entity e2 = new Entity("player", world);
        EntityManager manager = world.getEntityManager();

        manager.addEntity(e);
        manager.addEntity(e2);

        Entity referencedEntity = manager.getEntity("player");
        Entity registeredEntity = manager.getEntity(e.getEntityId());
        Entity registeredEntity2 = manager.getEntity(e2.getEntityId());

        assertEquals(e, registeredEntity);
        assertEquals(e2, registeredEntity2);
        assertEquals(e2, referencedEntity);
    }

    @Test
    public void testGetEntitiesForSystem() throws Exception {
        World world = new World();
        world.initialize();
        Entity e = new Entity(world);
        Entity e2 = new Entity(world);
        EntityManager manager = world.getEntityManager();
        manager.addEntity(e);
        manager.addEntity(e2);
        e.addComponent(new TestComponent());
        e2.addComponent(new TestComponent());
        e2.addComponent(new TestComponent2());
        EntitySystem system = new TestSystem(world);
        EntitySystem system2 = new TestSystem2(world);
        world.addSystem(system);
        world.addSystem(system2);

        ImmutableSet<Entity> set = manager.getEntitiesForSystem(system);
        ImmutableSet<Entity> set2 = manager.getEntitiesForSystem(system2);
        assertNotNull(set.size());
        assertEquals(set.size(), 2);
//        assertEquals(e, set.iterator().next());

        assertNotNull(set2.size());
        assertEquals(set2.size(), 1);

    }

    @Test
    public void testPerformance() {
        World world = new World();
        world.initialize();
        EntitySystem system = new TestSystem(world);
        world.addSystem(new TestSystem2(world));
        world.addSystem(system);

        for (int i = 0; i < 1000; i++) {
            world.createEntity().addComponent(new TestComponent());
        }
        for (int i = 0; i < 5000; i++) {
            world.createEntity().addComponent(new TestComponent()).addComponent(new TestComponent2());
        }
        long t = System.currentTimeMillis();
        world.process();

        System.out.println("Process time " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        world.process();

        System.out.println("Process time " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        world.createEntity().addComponent(new TestComponent());
        for (int i = 0; i < 1500; i++) {
            world.createEntity().addComponent(new TestComponent());

            world.createEntity().addComponent(new TestComponent()).addComponent(new TestComponent2());
        }
        world.process();
        System.out.println("Added new entities, Process time " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        for (int i = 0; i < 1500; i++) {
            world.removeEntity((long) i);
        }
        world.process();

        System.out.println("Process time " + (System.currentTimeMillis() - t));

        t = System.currentTimeMillis();

        world.dispose();
        System.out.println("Dispose time " + (System.currentTimeMillis() - t));

    }

    @Component
    private static class TestComponent {

    }

    @Component
    private static class TestComponent2 {

    }

    private static class TestSystem extends EntitySystem {

        public TestSystem(World world) {
            super(world);
        }

        @Override
        public void initialize() {
            addUsedComponent(TestComponent.class);
        }

        @Override
        public void processEntity(final Entity entity) {

        }

        @Override
        public void processEntities(final ImmutableSet<Entity> entities) {
            System.out.println(this.getClass() + " Size " + entities.size());
            super.processEntities(entities);
        }

        @Override
        public void dispose() {

        }
    }

    private static class TestSystem2 extends EntitySystem {

        public TestSystem2(World world) {
            super(world);
        }

        @Override
        public void initialize() {
            addUsedComponent(TestComponent.class);
            addUsedComponent(TestComponent2.class);
        }

        @Override
        public void processEntity(final Entity entity) {

        }

        @Override
        public void processEntities(final ImmutableSet<Entity> entities) {
            System.out.println(this.getClass() + " Size " + entities.size());
            super.processEntities(entities);
        }

        @Override
        public void dispose() {

        }
    }
}
