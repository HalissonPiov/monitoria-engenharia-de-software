public class MqttTransportHandler {
    private final TelemetryService telemetryService;

    public MqttTransportHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    public void processDevicePublish(Object ctx, Object mqttMsg, String topicName, int msgId) {
        if ("v1/devices/me/telemetry".equals(topicName)) {
            telemetryService.recordTelemetry(topicName, msgId);
        }
    }
}