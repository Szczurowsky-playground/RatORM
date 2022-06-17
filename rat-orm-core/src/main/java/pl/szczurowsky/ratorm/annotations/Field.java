package pl.szczurowsky.ratorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
    String name() default "";

    boolean isPrimaryKey() default false;

    boolean isForeignKey() default false;
}