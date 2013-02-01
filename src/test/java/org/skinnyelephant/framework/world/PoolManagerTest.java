package org.skinnyelephant.framework.world;

import org.junit.Test;
import org.skinnyelephant.framework.annotations.Component;
import org.skinnyelephant.framework.systems.EntitySystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 13.1.2
 * Time: 13:08
 *
 * @author Kristaps Kohs
 */
public class PoolManagerTest {
    @Test
    public void testPooledEntities() {
        Core core = new Core();
        core.initialize();

        core.addSystem(new Testsystem());
      Entity e =  core.createPooledEntity(TestCompOne.class);
        TestCompOne compOne = e.getComponent(TestCompOne.class);
        core.process(0);
        core.removeEntity(e.getEntityId());
        core.process(0);
        Entity e2 =  core.createPooledEntity(TestCompOne.class);
        Entity e3 =  core.createPooledEntity(TestCompOne.class, TestCompTwo.class);
        core.process(0);
        core.removeEntity(e3.getEntityId());
        core.process(0);
         core.getPoolManager().setRemovalPeriod(20);
        List<Entity> entities = new ArrayList<Entity>();
        for(int i  = 0 ; i < 64 ;i++  ) {
           entities.add(core.createPooledEntity(TestCompOne.class));
        }

        for(Entity entity : entities) {
            core.removeEntity(entity.getEntityId());
        }

        core.process(10000);
        core.process(10000);
        core.process(10000);

    }

    @Component
    public static class TestCompOne {

    }

    public class Testsystem extends EntitySystem  {

        @Override
        public void initialize() {
            addUsedComponent(TestCompTwo.class);
            addUsedComponent(TestCompOne.class);
        }

        @Override
        public void processEntity(Entity entity) {
           entity.getEntityId();
        }

        @Override
        public void dispose() {

        }
    }


    @Component
    public static class TestCompTwo {

    }


}
