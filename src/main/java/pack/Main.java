package pack;

import constants.Constants;
import my.serialization.TestWithPrimitive;

public class Main {
    public static void main(String[] args) throws Exception{
        Constants.initConstant();

        TestWithPrimitive t= new TestWithPrimitive();

        t.setAge(23);
        t.setGold(10000L);

        t.setHeight(1.75D);

        t.setId(1);

        t.serialize();
    }
}
