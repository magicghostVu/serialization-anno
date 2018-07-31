package my.serialization;

import anno.field.primitive.DoubleField;
import anno.field.primitive.IntField;
import anno.field.primitive.LongField;
import constants.Constants;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.TreeMap;

public class TestWithPrimitive extends AbstractSerialize {


    public TestWithPrimitive() {
    }


    @IntField(fieldId = 1)
    private int age;


    @LongField(fieldId = 2)
    private long gold;


    @IntField(fieldId = 3)
    private int id;


    @DoubleField(fieldId = 4)
    private double height;


    @Override
    public byte[] serialize() {

        //todo: test thử  xem sao
        // giả sử các field chỉ chứa
        Field[] allField = this.getClass().getDeclaredFields();


        TreeMap<Integer, Field> mapIndexedField = new TreeMap<>();

        Arrays.stream(allField).filter(f -> {
            Annotation[] all = f.getAnnotations();
            //nếu không có anno nào bỏ qua
            if (all.length == 0) return false;
            // nếu có bất cứ cái nào thuộc các class ann đã quy định thì giữ
            for (Annotation a : all) {
                Class<? extends Annotation> cc = a.annotationType();
                if (Constants.classIsAnnType(cc)) return true;
            }

            return false;
        }).forEach(field -> {
            // tại đây check số anno thuộc tập quy định, nếu vượt quá 1 thì throw
            // Exception
            Annotation[] all = field.getAnnotations();

            int count = 0;

            for (Annotation a : all) {
                if (Constants.classIsAnnType(a.annotationType())) {
                    count++;
                }
            }

            // đã vượt quá 1
            if (count > 1) {
                String msg = String.format("number anno of field %s is greater than one, so confusing, cannot do anything",
                        field.getName());
                throw new IllegalArgumentException(msg);
            }





        });


        return new byte[0];
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public AbstractSerialize fromByteArray(byte[] data) {
        return null;
    }
}
