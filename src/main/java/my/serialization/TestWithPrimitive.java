package my.serialization;

import anno.field.primitive.*;
import constants.Constants;
import constants.TypeFieldEnum;
import utils.MyPair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

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
    public byte[] serialize() throws Exception {

        //todo: test thử  xem sao
        // giả sử các field chỉ chứa
        Field[] allField = this.getClass().getDeclaredFields();


        TreeMap<Byte, MyPair<Annotation, Field>> mapIndexedField = new TreeMap<>();

        Arrays.stream(allField).filter(f -> {
            // lấy tất cả các anno của field ra
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
            // tại đây check số anno thuộc tập quy định, nếu vượt quá 1 thì throw Exception
            // mỗi field chỉ được phép có duy nhất một annotation thuộc các lớp đã quy định
            Annotation[] all = field.getAnnotations();

            // số field thuộc một trong các loại đã cho
            //int count = 0;

            List<Annotation> annoInType = Arrays.stream(all).filter(a -> {
                return Constants.classIsAnnType(a.annotationType());
            }).collect(Collectors.toList());

            if (annoInType.size() > 1) {
                String msg = String.format("number annotations of field %s is greater than one, so confusing, cannot do anything",
                        field.getName());
                throw new IllegalArgumentException(msg);
            }

            Annotation annotationTarget = annoInType.get(0);

            // lấy ra field id của anno

            //Field[] allFi


            // lấy ra typeEnum của trường hiện tại
            TypeFieldEnum typeOfAnno = Constants.getTypeFieldByAnnotationClass(annotationTarget.annotationType());

            //  xử lý theo từng case sẽ gặp
            switch (typeOfAnno) {
                case BooleanField: {
                    BooleanField annoBool = (BooleanField) annotationTarget;
                    mapIndexedField.put(annoBool.fieldId(), new MyPair<>(annoBool, field));
                    break;
                }
                case StringField: {
                    break;
                }
                case DoubleField: {

                    DoubleField doubleFieldAnno = (DoubleField) annotationTarget;

                    mapIndexedField.put(doubleFieldAnno.fieldId(), new MyPair<>(doubleFieldAnno, field));

                    break;
                }
                case ShortField: {

                    ShortField shortFieldAnno = (ShortField) annotationTarget;
                    mapIndexedField.put(shortFieldAnno.fieldId(), new MyPair<>(shortFieldAnno, field));

                    break;
                }
                case FloatField: {

                    FloatField floatFieldAnnotation = (FloatField) annotationTarget;
                    mapIndexedField.put(floatFieldAnnotation.fieldId(), new MyPair<>(floatFieldAnnotation, field));

                    break;
                }
                case LongField: {
                    LongField longFieldAnnotation = (LongField) annotationTarget;
                    mapIndexedField.put(longFieldAnnotation.fieldId(), new MyPair<>(longFieldAnnotation, field));
                    break;
                }
                case CharField: {
                    CharField charFieldAnnotation = (CharField) annotationTarget;
                    mapIndexedField.put(charFieldAnnotation.fieldId(), new MyPair<>(charFieldAnnotation, field));
                    break;
                }
                case ByteField: {

                    ByteField byteFieldAnno = (ByteField) annotationTarget;
                    mapIndexedField.put(byteFieldAnno.fieldId(), new MyPair<>(byteFieldAnno, field));
                    break;
                }
                case IntField: {

                    IntField intFieldAnnotation = (IntField) annotationTarget;

                    mapIndexedField.put(intFieldAnnotation.fieldId(), new MyPair<>(intFieldAnnotation, field));
                    break;
                }
                default: {
                    break;
                }
            }


        });

        int sizeMap = mapIndexedField.size();

        if (sizeMap > Byte.MAX_VALUE) {
            throw new IllegalArgumentException("too much field to serialize");
        }
        byte sizeUpdated = (byte) sizeMap;

        // tại đây tính size của object sau đó tạo một byte buffer để put dần data
        // 1 byte lưu cỡ của map, còn lại mỗi cặp sẽ là 1 byte và 1 short (byte lưu field id và short lưu kích thước của trường)
        int sizeHeader = sizeUpdated * (Byte.BYTES + Short.BYTES) + 1;

        int sizeData = 0;
        for (MyPair<Annotation, Field> p : mapIndexedField.values()) {
            short sizeForThisField = Constants.getSizeDataForPrimitiveField(p.getFirst());
            sizeData += sizeForThisField;
        }

        int sizeAll = sizeHeader + sizeData;

        ByteBuffer byteBuffer = ByteBuffer.allocate(sizeAll);


        //bắt đầu put data vào buffer
        // put size trước
        byteBuffer.put(sizeUpdated);

        // put data cho header

        // giả sử rằng chỉ có mỗi primitive field

        // cái set này nó tham chiếu đến bản thân cái map, phải sao chép ra cái khác
        // không được dùng trực tiếp
        TreeSet<Byte> ks = new TreeSet<>();

        // ngon hơn rồi
        ks.addAll(mapIndexedField.keySet());


        // các field được put theo thứ tự tăng dần, các block của data được put tương
        while (!ks.isEmpty()) {
            byte lowestFieldId = ks.first();
            MyPair<Annotation, Field> dataInfoField = mapIndexedField.get(lowestFieldId);
            // put field id
            byteBuffer.put(lowestFieldId);

            // put size của field
            byteBuffer.putShort(Constants.getSizeDataForPrimitiveField(dataInfoField.getFirst()));
            ks.remove(lowestFieldId);
        }


        System.out.println();
        // bắt đầu put data vào đây thứ tự put data theo thứ tự put của các key ở
        while (!mapIndexedField.isEmpty()) {
            byte lowestFieldId = mapIndexedField.firstKey();

            // xử lý data ở đây cuối vòng lặp thì remove key đó đi
            MyPair<Annotation, Field> dataInfo = mapIndexedField.get(lowestFieldId);
            dataInfo.getSecond().setAccessible(true);

            TypeFieldEnum t = Constants.getTypeFieldByAnnotationClass(dataInfo.getFirst().annotationType());

            // gỉả sử rằng ở đây các class đã được check hợp lệ với các annotation phía trên
            //hợp lệ về kiểu của các class trong field
            switch (t) {
                case IntField: {


                    int data = dataInfo.getSecond().getInt(this);
                    byteBuffer.putInt(data);


                    break;
                }
                case ByteField: {
                    byte data = dataInfo.getSecond().getByte(this);
                    byteBuffer.put(data);


                    break;
                }
                case CharField: {

                    char data = dataInfo.getSecond().getChar(this);
                    byteBuffer.putChar(data);

                    break;
                }
                case LongField: {

                    long data = dataInfo.getSecond().getLong(this);

                    byteBuffer.putLong(data);
                    break;
                }
                case FloatField: {

                    float data = dataInfo.getSecond().getFloat(this);
                    byteBuffer.putFloat(data);

                    break;
                }
                case ShortField: {
                    short data = dataInfo.getSecond().getShort(this);
                    byteBuffer.putShort(data);

                    break;
                }
                case DoubleField: {

                    double data = dataInfo.getSecond().getDouble(this);

                    byteBuffer.putDouble(data);

                    break;
                }
                case StringField: {

                    // todo: tạm thời bỏ qua
                    break;
                }
                case BooleanField: {

                    // ta quy ước true là 1, false là 0
                    boolean data = dataInfo.getSecond().getBoolean(this);
                    if (data) {
                        byteBuffer.put((byte) 1);
                    } else {
                        byteBuffer.put((byte) 0);
                    }

                    break;
                }
            }


            String log = String.format("field %s is processed", dataInfo.getSecond().getName());
            System.out.println(log);


            mapIndexedField.remove(lowestFieldId);
        }


        return new byte[0];
    }

    // khôi phục lại data
    @Override
    public void fromByteArray(byte[] data) {
        //eturn null;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getGold() {
        return gold;
    }

    public void setGold(long gold) {
        this.gold = gold;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
