public interface TelemetryService {
    void recordTelemetry(String topicName, int msgId);
}