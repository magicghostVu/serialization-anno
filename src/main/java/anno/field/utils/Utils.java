package anno.field.utils;

import java.lang.reflect.Field;
import java.util.*;

public class Utils {

    public static Field[] getAllField(Class<? extends Object> classzz) {

        if (classzz.equals(Object.class)) {
            return new Field[0];
        }
        Set<Field> resTmp = new HashSet<>();
        Class<? extends Object> tmp = classzz;
        while (!tmp.equals(Object.class)) {
            Field[] allFieldOfThisClass = tmp.getDeclaredFields();
            resTmp.addAll(Arrays.asList(allFieldOfThisClass));
            tmp = tmp.getSuperclass();
        }
        Field[] res= new Field[resTmp.size()];
        return resTmp.toArray(res);
    }


}
