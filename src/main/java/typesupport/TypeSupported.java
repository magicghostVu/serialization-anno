package typesupport;

import anno.field.extended_field.EnumField;
import anno.field.extended_field.ListField;
import anno.field.extended_field.MapField;
import anno.field.extended_field.ObjectField;
import anno.field.primitive.*;

import java.lang.annotation.Annotation;

// các kiểu sẽ hỗ trợ kèm theo các annotation đại diện cho kiểu đó
public enum TypeSupported {
    BYTE(ByteField.class),
    INTEGER(IntField.class),
    DOUBLE(DoubleField.class),
    LONG(LongField.class),
    FLOAT(FloatField.class),
    CHARACTER(CharField.class),
    BOOLEAN(BooleanField.class),
    SHORT(ShortField.class),
    LIST(ListField.class),
    MAP(MapField.class),
    ENUM(EnumField.class),
    OBJECT(ObjectField.class);

    private Class<? extends Annotation> classCorrespond;


    TypeSupported(Class<? extends Annotation> classCorrespond) {
        this.classCorrespond = classCorrespond;
    }

    public Class<? extends Annotation> getClassCorrespond() {
        return classCorrespond;
    }
}
