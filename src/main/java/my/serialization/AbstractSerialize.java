package my.serialization;

public abstract class AbstractSerialize {

    public abstract byte[] serialize();

    public abstract int size();

    public abstract AbstractSerialize fromByteArray(byte[] data);

}
