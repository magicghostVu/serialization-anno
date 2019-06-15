package anno.field.extended_field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface MapField {
    byte fieldId();

    // key class chỉ được là 8 kiểu primitive, thêm String và Enum tạm thời chưa hỗ trợ kiểu Serialize
    Class<? extends Object> keyClass();


    // value hỗ trợ full 11 kiểu
    Class<? extends Object> valueClass();

    // cờ đánh dấu là abstract hay không
    boolean valueClassMayBeAbstract() default true;

}
