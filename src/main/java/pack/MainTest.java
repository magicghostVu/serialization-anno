package pack;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MainTest {
    public static void main(String[] args) {

        ByteBuffer byteBuffer= ByteBuffer.allocate(10);


        byteBuffer.putLong(8L);

        byteBuffer.putShort((short) 2);

        // với bytebuffer mới được put thì phải flip lại mới đọc được data
        byteBuffer.flip();

        long l1= byteBuffer.getLong();

        System.out.println("l1 is "+ l1);

        short s1=byteBuffer.getShort();

        System.out.println("s1 is "+ s1);

        // thử với  byteArray

        //hàm này sử sụng lúc nào cũng được
        byte[] arr= byteBuffer.array();


        System.out.println(Arrays.toString(arr));

        // nếu sử dụng wrap thì buffer đã sẵn sàng để đọc ngay
        ByteBuffer byteBuffer1= ByteBuffer.wrap(arr);

        l1= byteBuffer1.getLong();

        s1= byteBuffer1.getShort();

        System.out.println();



    }
}
