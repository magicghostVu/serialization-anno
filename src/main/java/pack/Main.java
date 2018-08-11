package pack;

import com.google.gson.Gson;
import constants.Constants;
import my.serialization.TestWithPrimitive;
import my.serialization.enumstatus.MariedStatus;

import java.nio.charset.Charset;

public class Main {
    public static void main(String[] args) throws Exception {
        Constants.initConstant();

        TestWithPrimitive t = new TestWithPrimitive();

        t.setAge(1000000);
        t.setGold(1000000000L);

        t.setHeight(1.753847594D);

        t.setId(1);

        t.setName("Vũ Hồng Phú");

        t.setMariedStatus(MariedStatus.SINGLE);

        byte[] arr = t.serialize();


        TestWithPrimitive t2 = new TestWithPrimitive();
        t2.fromByteArray(arr);


        //String filePath = System.getProperty("user.dir") + "/filetest/tmp.dat";

        /*FileOutputStream stream = new FileOutputStream(filePath);
        FileChannel chanelWrite = stream.getChannel();


        ByteBuffer bfWrite = ByteBuffer.wrap(arr);
        chanelWrite.write(bfWrite);

        bfWrite.clear();

        chanelWrite.close();

        stream.close();*/


        //t.getClass().getFields();

        System.out.println("arr size is " + arr.length);


        String jsonRepresent= (new Gson()).toJson(t2);

        byte[] arr2= jsonRepresent.getBytes(Charset.forName("utf-8"));

        System.out.println("json length is "+ arr2.length);
    }
}
