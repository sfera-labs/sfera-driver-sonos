package cc.sferalabs.sfera.drivers.sonos.actions;

/**
 *
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class SetAVTransportURIAction extends AVTransportAction {

	private static final String ACTION = "SetAVTransportURI";
	private static final String PARAMETERS_0 = "<InstanceID>0</InstanceID><CurrentURI>";
	private static final String PARAMETERS_1 = "</CurrentURI><CurrentURIMetaData>";
	private static final String PARAMETERS_2 = "</CurrentURIMetaData>";

	public SetAVTransportURIAction(String uri, String metadata) {
		super(ACTION, PARAMETERS_0 + uri + PARAMETERS_1 + metadata + PARAMETERS_2);
	}
}
