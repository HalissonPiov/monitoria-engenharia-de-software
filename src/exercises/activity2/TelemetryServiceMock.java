public class TelemetryServiceMock implements TelemetryService {
    private boolean called;

    @Override
    public void recordTelemetry(String topicName, int msgId) {
        this.called = true;
    }

    public boolean wasCalled() {
        return called;
    }
}