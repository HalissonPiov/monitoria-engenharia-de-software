
public class CloudClientImpl implements CloudClient {

	@Override
	public int publish(String deviceId, String appTopic, byte[] payload, int qos, boolean retain, int priority)
			throws KuraException {
		return 200;
	}
}
