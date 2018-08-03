package my.serialization;

public abstract class AbstractSerialize {

    public abstract byte[] serialize() throws Exception;

    //public abstract short size();

    public abstract void fromByteArray(byte[] data);

}
