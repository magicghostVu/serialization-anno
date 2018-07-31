package constants;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Constants {

    private static Set<Class<? extends Annotation>> setAllAnnoForField;

    private static Map<Class<? extends Annotation>, TypeFieldEnum> mapTypeFieldByClass;

    public static void initConstant() {
        //setAllAnnoForField = new HashSet<>();
        Reflections reflections = new Reflections("anno.field");
        setAllAnnoForField = reflections.getSubTypesOf(Annotation.class);


        mapTypeFieldByClass = new HashMap<>();

        setAllAnnoForField.forEach(c -> {
            TypeFieldEnum t = TypeFieldEnum.valueOf(c.getSimpleName());
            mapTypeFieldByClass.put(c, t);
        });


    }

    public static boolean classIsAnnType(Class<? extends Annotation> c) {
        return setAllAnnoForField.contains(c);
    }

}
