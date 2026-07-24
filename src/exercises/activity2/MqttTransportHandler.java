public class MqttTransportHandler {
    private final TelemetryService telemetryService;

    public MqttTransportHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    public void processDevicePublish(Object ctx, Object mqttMsg, String topicName, int msgId) {
        // Bloco condicional gigante sem responsabilidade única (simulação do código original)
        if ("v1/devices/me/attributes".equals(topicName)) {
            // Lógica simulada de conversão e processamento (PostAttributeMsg)
            System.out.println("Processando atributos...");
            telemetryService.processAttributes(topicName, msgId);
        } else if ("v1/devices/me/telemetry".equals(topicName)) {
            // Lógica simulada de conversão e processamento (PostTelemetryMsg)
            System.out.println("Processando telemetria...");
            telemetryService.recordTelemetry(topicName, msgId);
        } else if (topicName != null && topicName.startsWith("v1/devices/me/rpc/request/")) {
            // Lógica simulada de conversão e processamento (ToDeviceRpcResponseMsg)
            System.out.println("Processando RPC...");
            telemetryService.processRpc(topicName, msgId);
        }
        // ... O código original possui dezenas de outros "else if" sequenciais.
        // O estudante deve continuar o processo de refatoração para os demais blocos.
    }
}