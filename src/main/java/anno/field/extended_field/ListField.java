package anno.field.extended_field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//nhớ xử lý trường hợp một element bị null
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface ListField {
    byte fieldId();
    Class elementClass();
    boolean classElementMayBeAbstract() default true;
}
