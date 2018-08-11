package anno.field.utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class BufferUtils {


    // hàm này sẽ biến đổi lên buffer
    public static void putString(String data, ByteBuffer byteBuffer) {
        //byteBuffer.putShort((short) data.length());
        byte[] dataArray = data.getBytes(Charset.forName("UTF-8"));
        byteBuffer.putShort((short) dataArray.length);
        byteBuffer.put(dataArray);
    }


    public static String readString(ByteBuffer byteBuffer) {
        short sizeData = byteBuffer.getShort();
        byte[] byteArr = new byte[sizeData];
        byteBuffer.get(byteArr);
        String res = new String(byteArr, Charset.forName("UTF-8"));
        return res;
    }

}
