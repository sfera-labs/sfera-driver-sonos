package cc.sferalabs.sfera.drivers.sonos.actions;

/**
 *
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class AVTransportAction extends Action {

	private static final String ENDPOINT = "/MediaRenderer/AVTransport/Control";
	private static final String SERVICE = "urn:schemas-upnp-org:service:AVTransport:1";

	public static final AVTransportAction PLAY = new AVTransportAction("Play",
			"<InstanceID>0</InstanceID><Speed>1</Speed>");

	public static final AVTransportAction PAUSE = new AVTransportAction("Pause",
			"<InstanceID>0</InstanceID>");

	public static final AVTransportAction STOP = new AVTransportAction("Stop",
			"<InstanceID>0</InstanceID>");

	public static final AVTransportAction NEXT = new AVTransportAction("Next",
			"<InstanceID>0</InstanceID>");

	public static final AVTransportAction PREVIOUS = new AVTransportAction("Previous",
			"<InstanceID>0</InstanceID>");

	public static final AVTransportAction GET_POSITION_INFO = new AVTransportAction(
			"GetPositionInfo", "<InstanceID>0</InstanceID>");

	public static final AVTransportAction GET_TRANSPORT_INFO = new AVTransportAction(
			"GetTransportInfo", "<InstanceID>0</InstanceID>");

	public AVTransportAction(String action, String parameters) {
		super(action, ENDPOINT, SERVICE, parameters);
	}
}
