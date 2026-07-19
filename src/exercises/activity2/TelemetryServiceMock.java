public class TelemetryServiceMock implements TelemetryService {
    private boolean called;
    private boolean rpcCalled;
    private boolean attributesCalled;

    @Override
    public void recordTelemetry(String topicName, int msgId) {
        this.called = true;
    }

    @Override
    public void processRpc(String topicName, int msgId) {
        this.rpcCalled = true;
    }

    @Override
    public void processAttributes(String topicName, int msgId) {
        this.attributesCalled = true;
    }

    public boolean wasCalled() {
        return called;
    }

    public boolean wasRpcCalled() {
        return rpcCalled;
    }

    public boolean wasAttributesCalled() {
        return attributesCalled;
    }
}