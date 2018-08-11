package my.serialization;

import anno.field.extended_field.EnumField;
import anno.field.extended_field.StringField;
import anno.field.primitive.*;
import anno.field.utils.BufferUtils;
import anno.field.utils.Utils;
import constants.Constants;
import constants.TypeFieldEnum;
import my.serialization.enumstatus.MariedStatus;
import utils.MyPair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TestWithPrimitive extends AbstractSerialize {


    public TestWithPrimitive() {
        name = "";
    }


    @IntField(fieldId = 1)
    private int age;


    @LongField(fieldId = 2)
    private long gold;


    @IntField(fieldId = 3)
    private int id;


    @DoubleField(fieldId = 4)
    private double height;


    @StringField(fieldId = 5)
    private String name;


    @EnumField(fieldId = 6, enumClass = MariedStatus.class)
    private MariedStatus mariedStatus;


    @Override
    public byte[] serialize() throws Exception {


        //todo: nên verify metadata với data trước khi serialize


        //todo: test thử  xem sao
        // giả sử các field chỉ chứa
        // all field là các field của tất cả các class kể cả class cha, cho đến tận cùng khi gặp lớp object
        Field[] allField = Utils.getAllField(getClass());


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

            //  xử lý theo từng case sẽ gặp, đưa metadata vào trong map indexField
            switch (typeOfAnno) {
                case BooleanField: {
                    BooleanField annoBool = (BooleanField) annotationTarget;
                    mapIndexedField.put(annoBool.fieldId(), new MyPair<>(annoBool, field));
                    break;
                }
                case StringField: {

                    StringField stringFieldAnnotation = (StringField) annotationTarget;

                    mapIndexedField.put(stringFieldAnnotation.fieldId(), new MyPair<>(stringFieldAnnotation, field));

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
                case EnumField: {

                    EnumField enumFieldAnnotation = (EnumField) annotationTarget;

                    mapIndexedField.put(enumFieldAnnotation.fieldId(), new MyPair<>(enumFieldAnnotation, field));

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

            // với các field là primitive thì tính được luôn
            if (Constants.annotationIsForPrimitiveField(p.getFirst())) {
                short sizeForThisField = Constants.getSizeDataForPrimitiveField(p.getFirst());
                sizeData += sizeForThisField;
            }
            // các field không phải primitive sẽ được tính theo cách của từng trường hợp
            else {

                TypeFieldEnum t = Constants.getTypeFieldByAnnotationClass(p.getFirst().annotationType());

                // todo: xử lý tính size của các field extended ở đây
                switch (t) {

                    // với StringField
                    case StringField: {


                        String dataStr = (String) p.getSecond().get(this);

                        byte[] strDataByteArr = dataStr.getBytes(Charset.forName("UTF-8"));

                        sizeData += strDataByteArr.length + Short.BYTES; // 2 byte thêm vào để lưu cỡ của mảng data

                        break;
                    }

                    // với enum field, sẽ lưu như đối với String
                    case EnumField: {
                        Enum val = (Enum) p.getSecond().get(this);

                        String nameEnum = val.name();

                        byte[] strNameEumByteArr = nameEnum.getBytes(Charset.forName("utf-8"));

                        sizeData += strNameEumByteArr.length + Short.BYTES; // tương tự như đối với String, vì sẽ lưu enum dưới dang string


                        break;
                    }


                    default: {
                        break;
                    }
                }


            }


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

            if (Constants.annotationIsForPrimitiveField(dataInfoField.getFirst())) {
                byteBuffer.putShort(Constants.getSizeDataForPrimitiveField(dataInfoField.getFirst()));
            } else {

                TypeFieldEnum t = Constants.getTypeFieldByAnnotationClass(dataInfoField.getFirst().annotationType());

                dataInfoField.getSecond().setAccessible(true);

                switch (t) {
                    case StringField: {

                        String dataStr = (String) dataInfoField.getSecond().get(this);
                        byte[] strDataByteArr = dataStr.getBytes(Charset.forName("UTF-8"));
                        short sizeField = (short) (strDataByteArr.length + Short.BYTES);
                        byteBuffer.putShort(sizeField);
                        break;
                    }
                    case EnumField: {
                        String enumName = ((Enum) dataInfoField.getSecond().get(this)).name();
                        byte[] rawData = enumName.getBytes(Charset.forName("utf-8"));
                        short sizeField = (short) (rawData.length + Short.BYTES);
                        byteBuffer.putShort(sizeField);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
            ks.remove(lowestFieldId);
        }


        //System.out.println();
        // bắt đầu put data vào đây thứ tự put data theo thứ tự put tăng dần
        while (!mapIndexedField.isEmpty()) {
            byte lowestFieldId = mapIndexedField.firstKey();

            // xử lý data ở đây cuối vòng lặp xong thì remove key đó đi
            MyPair<Annotation, Field> dataInfo = mapIndexedField.get(lowestFieldId);
            dataInfo.getSecond().setAccessible(true);

            TypeFieldEnum t = Constants.getTypeFieldByAnnotationClass(dataInfo.getFirst().annotationType());

            // gỉả sử rằng ở đây các class đã được check hợp lệ với các annotation phía trên
            // hợp lệ về kiểu của các class trong field
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
                    String data = (String) dataInfo.getSecond().get(this);
                    BufferUtils.putString(data, byteBuffer);

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
                case EnumField: {

                    Enum valThisField = (Enum) dataInfo.getSecond().get(this);

                    String enumName = valThisField.name();

                    BufferUtils.putString(enumName, byteBuffer);


                    break;
                }
            }


            String log = String.format("field %s is processed", dataInfo.getSecond().getName());
            System.out.println(log);


            mapIndexedField.remove(lowestFieldId);
        }


        return byteBuffer.array();
    }

    // khôi phục lại data
    @Override
    public void fromByteArray(byte[] data) {

        //todo: nên verify metadata trước đi parse ngược lại data

        //
        ByteBuffer buffer = ByteBuffer.wrap(data);
        // ta giả định rằng hiện tại chỉ có các trường primitive -> các trường primitive đã được gắn giá trị măc định sau khi constructor chạy


        // với các field không có trong map, ta để giá trị mặc định

        //todo : đọc header
        byte sizeMapHeaderStored = buffer.get();
        TreeMap<Byte, Short> indexFieldToSizeField = new TreeMap<>();
        for (int i = 0; i < sizeMapHeaderStored; i++) {
            byte indexField = buffer.get();
            short dataSizeForThisField = buffer.getShort();
            indexFieldToSizeField.put(indexField, dataSizeForThisField);
        }
        //todo: tách các block data cho từng field
        // tạm thời ở đây ta coi như object chỉ có các field primitive

        //byte[] tmpArr;
        // phải đọc theo thứ tự đã put vào, ở đây luôn đọc theo thứ tự từ nhỏ đến lớn

        // vì ở đây chưa có các extend field nên chưa cần check rỗng
        TreeSet<Byte> setIndexFieldTmp = new TreeSet<>();
        Map<Byte, byte[]> mapIndexToBlockData = new HashMap<>();
        setIndexFieldTmp.addAll(indexFieldToSizeField.keySet());
        while (!setIndexFieldTmp.isEmpty()) {
            byte currentLowestIndex = setIndexFieldTmp.first();
            //todo: xử lý tách các block data ở đây
            short sizeThisField = indexFieldToSizeField.get(currentLowestIndex);
            byte[] dataThisField = new byte[sizeThisField];
            buffer.get(dataThisField);
            mapIndexToBlockData.put(currentLowestIndex, dataThisField);
            setIndexFieldTmp.remove(currentLowestIndex);
        }


        // lấy ra tất cả các field của class hiện tại và metaData
        Map<Byte, MyPair<Field, Annotation>> mapIndexFieldMetaData = new HashMap<>();

        Field[] allCurrentFields = Utils.getAllField(this.getClass());


        Predicate<Field> filterField = f -> {

            Annotation[] all = f.getAnnotations();
            if (all.length == 0) return false;

            int countForTargetAnnotation = 0;

            for (Annotation a : all) {
                if (Constants.classIsAnnType(a.annotationType())) {
                    countForTargetAnnotation++;
                }
            }

            if (countForTargetAnnotation > 1) {
                String msg = String.format("Field %s had more than one annotation in target, so confusing, exit now", f.getName());
                throw new IllegalArgumentException(msg);
            }


            return true;
        };

        Consumer<Field> processValidField = f -> {
            Annotation[] all = f.getAnnotations();
            List<Annotation> targetL = Arrays.stream(all)
                    .filter(a -> Constants.classIsAnnType(a.annotationType()))
                    .collect(Collectors.toList());
            Annotation target = targetL.get(0);
            String methodName = Constants.getMethodNameGetFieldId();
            try {
                // todo: viết cho tiện, có thể thay bằng switch case, và nên thay bằng switch case
                byte fieldId = (Byte) target.getClass().getMethod(methodName).invoke(target);
                mapIndexFieldMetaData.put(fieldId, new MyPair<>(f, target));
            } catch (Exception e) {
                //todo: ghi log
                //e.get
                e.printStackTrace();
            }
        };

        //ở đây đã lấy hết được meta data về các field hiện tại
        Arrays.stream(allCurrentFields).filter(filterField).forEach(processValidField);

        // xử lý lấy ngược lại data
        // ở đây đã có các block data cho từng field rồi
        BiConsumer<Byte, byte[]> processDataForExistField = (indexField, dataBlock) -> {
            MyPair<Field, Annotation> metaDataForField = mapIndexFieldMetaData.get(indexField);


            TypeFieldEnum t = Constants.getTypeFieldByAnnotationClass(metaDataForField.getSecond().annotationType());

            ByteBuffer tmpBuffer = ByteBuffer.wrap(dataBlock);
            Field fieldNeedRestoreData = metaDataForField.getFirst();
            fieldNeedRestoreData.setAccessible(true);

            try {
                switch (t) {
                    case BooleanField: {
                        boolean val = tmpBuffer.get() != 0;
                        fieldNeedRestoreData.set(this, val);
                        break;
                    }
                    case StringField: {
                        String str = BufferUtils.readString(tmpBuffer);
                        fieldNeedRestoreData.set(this, str);
                        break;
                    }
                    case DoubleField: {
                        double d = tmpBuffer.getDouble();
                        fieldNeedRestoreData.set(this, d);
                        break;
                    }
                    case ShortField: {
                        short s = tmpBuffer.getShort();
                        fieldNeedRestoreData.set(this, s);
                        break;
                    }
                    case FloatField: {

                        float f = tmpBuffer.getFloat();

                        fieldNeedRestoreData.set(this, f);

                        break;
                    }
                    case LongField: {
                        long l = tmpBuffer.getLong();

                        fieldNeedRestoreData.set(this, l);
                        break;
                    }
                    case CharField: {
                        char c = tmpBuffer.getChar();

                        fieldNeedRestoreData.set(this, c);

                        break;
                    }
                    case ByteField: {

                        byte b = tmpBuffer.get();

                        fieldNeedRestoreData.set(this, b);

                        break;
                    }
                    case IntField: {

                        int i = tmpBuffer.getInt();

                        fieldNeedRestoreData.set(this, i);
                        break;
                    }

                    case EnumField: {

                        String enumName = BufferUtils.readString(tmpBuffer);

                        EnumField enumFieldAnnotation = (EnumField) metaDataForField.getSecond();

                        Class<? extends Enum> classEnum = enumFieldAnnotation.enumClass();

                        Enum targetVal;
                        try {
                            targetVal = Enum.valueOf(classEnum, enumName);
                        } catch (Exception e) {
                            targetVal = null;
                        }
                        fieldNeedRestoreData.set(this, targetVal);


                        break;
                    }
                    default: {

                    }
                }
            } catch (Exception e) {
                //todo: ghi log
                e.printStackTrace();
            }
        };

        // sử dụng map blockdata để restore lại dữ liệu cho các field tương ứng
        // các field không có trong map block data(các trường mới được thêm vào sau này) sẽ được sử lý tạo mới sau
        mapIndexToBlockData.forEach(processDataForExistField);


        //System.out.println();


        //


        //return null;
    }


    /*public Map<Byte, MyPair<Annotation, Field>> getAllFieldAndAnnotation() {


        Field allFields[] = getClass().getFields();

        Predicate<Field> filterField = f -> {


            return true;
        };


        return null;
    }*/


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MariedStatus getMariedStatus() {
        return mariedStatus;
    }

    public void setMariedStatus(MariedStatus mariedStatus) {
        this.mariedStatus = mariedStatus;
    }
}
