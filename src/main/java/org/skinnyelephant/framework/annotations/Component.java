package org.skinnyelephant.framework.annotations;

import java.lang.annotation.*;

/**
 * </p> Annotation designating class to be a Component. </p>
 * <p>If component contains resources that are required to be released it should also implement {@link org.skinnyelephant.framework.world.Disposable}</p>
 */
@Documented
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Component {
}
