package anno.field.extended_field;

import my.serialization.AbstractSerialize;

public @interface ObjectField {
    byte fieldId();

    // có vẻ như trường này là không cần thiết, sẽ tự detect được kiểu của field
    Class<? extends AbstractSerialize> _class();
    boolean maybeAbstract() default true;
}
