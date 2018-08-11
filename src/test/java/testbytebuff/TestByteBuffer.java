package testbytebuff;

import anno.field.primitive.IntField;
import anno.field.utils.Utils;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class TestByteBuffer extends TestCase{


    public void testByteBuffer(){
        ByteBuffer byteBuffer= ByteBuffer.allocate(10);


        byteBuffer.putLong(8L);

        byteBuffer.putShort((short) 2);

        byteBuffer.flip();

        long l1= byteBuffer.getLong();

        assertEquals(8L, l1);

    }

    public void testGetAllFields(){
        /*Field[] allFields= Utils.getAllField(ClassA.class);

        assertEquals(1, allFields.length);


        Field[] allFieldB= Utils.getAllField(ClassB.class);

        assertEquals(2, allFieldB.length);*/

        int[] arr= new int[2];

        Integer[] arr2= new Integer[3];



        System.out.println(arr2.getClass().getName());



    }

}
