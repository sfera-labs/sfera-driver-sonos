package cc.sferalabs.sfera.drivers.sonos.actions;

public class RenderingControlAction extends Action {

	private static final String ENDPOINT = "/MediaRenderer/RenderingControl/Control";
	private static final String SERVICE = "urn:schemas-upnp-org:service:RenderingControl:1";

	public static final RenderingControlAction SET_MUTE_TRUE = new RenderingControlAction(
			"SetMute",
			"<InstanceID>0</InstanceID><Channel>Master</Channel><DesiredMute>1</DesiredMute>");

	public static final RenderingControlAction SET_MUTE_FALSE = new RenderingControlAction(
			"SetMute",
			"<InstanceID>0</InstanceID><Channel>Master</Channel><DesiredMute>0</DesiredMute>");

	public static final RenderingControlAction GET_MUTE = new RenderingControlAction(
			"GetMute", "<InstanceID>0</InstanceID><Channel>Master</Channel>");
	
	public static final RenderingControlAction GET_VOLUME = new RenderingControlAction(
			"GetVolume", "<InstanceID>0</InstanceID><Channel>Master</Channel>");

	/**
	 * 
	 * @param action
	 * @param parameters
	 */
	public RenderingControlAction(String action, String parameters) {
		super(action, ENDPOINT, SERVICE, parameters);
	}

}
