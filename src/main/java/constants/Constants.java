package constants;

import org.reflections.Reflections;
import typesupport.TypeSupported;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Constants {

    private static Set<Class<? extends Annotation>> setAllClassAnnotationForField;

    private static Map<Class<? extends Annotation>, TypeFieldEnum> mapTypeFieldByClass;

    private static Map<TypeFieldEnum, Short> mapSizeDataForPrimitive;

    private static final String methodNameGetFieldId = "fieldId";

    private static Set<Class> setClassPrimitive;

    private static Map<Class<? extends Annotation>, TypeSupported> mapAnnotationToTypeSupport;

    //private static


    public static void initConstant() {
        String packageNameContainsAllAnnotation = "anno.field";

        Reflections reflections = new Reflections(packageNameContainsAllAnnotation);
        setAllClassAnnotationForField = reflections.getSubTypesOf(Annotation.class);


        mapTypeFieldByClass = new HashMap<>();

        setAllClassAnnotationForField.forEach(c -> {
            TypeFieldEnum t = TypeFieldEnum.valueOf(c.getSimpleName());
            mapTypeFieldByClass.put(c, t);
        });

        mapSizeDataForPrimitive = new HashMap<>();

        for (TypeFieldEnum t : TypeFieldEnum.values()) {
            switch (t) {
                case IntField: {
                    mapSizeDataForPrimitive.put(t, (short) Integer.BYTES);
                    break;
                }
                case ByteField: {
                    mapSizeDataForPrimitive.put(t, (short) Byte.BYTES);
                    break;
                }
                case CharField: {
                    mapSizeDataForPrimitive.put(t, (short) Character.BYTES);
                    break;
                }
                case LongField: {
                    mapSizeDataForPrimitive.put(t, (short) Long.BYTES);
                    break;
                }
                case FloatField: {

                    mapSizeDataForPrimitive.put(t, (short) Float.BYTES);
                    break;
                }
                case ShortField: {
                    mapSizeDataForPrimitive.put(t, (short) Short.BYTES);
                    break;
                }
                case DoubleField: {
                    mapSizeDataForPrimitive.put(t, (short) Double.BYTES);
                    break;
                }
                case StringField: {
                    break;
                }
                case BooleanField: {
                    mapSizeDataForPrimitive.put(t, (short) Byte.BYTES);

                    break;
                }
                default: {
                    break;
                }
            }
        }


        // khởi tạo map các kiểu sẽ hỗ trợ
        for (TypeSupported t : TypeSupported.values()) {
            mapAnnotationToTypeSupport.put(t.getClassCorrespond(), t);
        }



    }


    public static boolean classIsAnnType(Class<? extends Annotation> c) {
        return setAllClassAnnotationForField.contains(c);
    }

    public static TypeFieldEnum getTypeFieldByAnnotationClass(Class<? extends Annotation> c) {
        return mapTypeFieldByClass.get(c);
    }

    public static boolean annotationIsForPrimitiveField(Annotation annotation) {
        TypeFieldEnum t = getTypeFieldByAnnotationClass(annotation.annotationType());
        return mapSizeDataForPrimitive.containsKey(t);
    }

    public static short getSizeDataForPrimitiveField(Annotation annotation) {
        if (!annotationIsForPrimitiveField(annotation)) {
            throw new IllegalArgumentException("this annotation is not for primitive field");
        }
        TypeFieldEnum t = mapTypeFieldByClass.get(annotation.annotationType());
        return mapSizeDataForPrimitive.get(t);
    }

    public static String getMethodNameGetFieldId() {
        return methodNameGetFieldId;
    }
}
