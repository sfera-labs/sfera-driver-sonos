package cc.sferalabs.sfera.drivers.sonos.actions;

public class SetRelativeVolumeAction extends RenderingControlAction {

	private static final String ACTION = "SetRelativeVolume";
	private static final String PARAMETERS_HEAD = "<InstanceID>0</InstanceID><Channel>Master</Channel><Adjustment>";
	private static final String PARAMETERS_TRAIL = "</Adjustment>";

	/**
	 * 
	 * @param val
	 */
	public SetRelativeVolumeAction(int val) {
		super(ACTION, PARAMETERS_HEAD + val + PARAMETERS_TRAIL);
	}

}
