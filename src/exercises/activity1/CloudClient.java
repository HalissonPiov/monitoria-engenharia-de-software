public interface CloudClient {
    int publish(String deviceId, String appTopic, byte[] payload, int qos, boolean retain, int priority)
            throws KuraException;
}