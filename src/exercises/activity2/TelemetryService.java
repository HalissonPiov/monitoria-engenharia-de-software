public interface TelemetryService {
    void recordTelemetry(String topicName, int msgId);
    void processRpc(String topicName, int msgId);
    void processAttributes(String topicName, int msgId);
}