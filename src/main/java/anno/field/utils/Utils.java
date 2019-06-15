package anno.field.utils;

import constants.Constants;
import my.serialization.AbstractSerialize;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        Field[] res = new Field[resTmp.size()];
        return resTmp.toArray(res); //
    }

    // lấy ra tất cả các field có annotation hợp lệ
    public static Set<Field> getAllValidField(Class<? extends AbstractSerialize> target) {
        Field[] allFields = getAllField(target);
        Predicate<Field> filterField = f -> {
            Annotation[] all = f.getAnnotations();
            Predicate<Annotation> fa = _fs ->
                Constants.classIsAnnType(_fs.annotationType());
            long numAnnotations_target = Arrays.stream(all).filter(fa).count();
            return numAnnotations_target == 1L;
        };
        return Arrays.stream(allFields).filter(filterField).collect(Collectors.toSet());
    }


}
