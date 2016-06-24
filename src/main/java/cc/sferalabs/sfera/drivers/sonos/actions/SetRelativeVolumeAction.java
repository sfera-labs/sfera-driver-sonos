package cc.sferalabs.sfera.drivers.sonos.actions;

/**
 *
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class SetRelativeVolumeAction extends RenderingControlAction {

	private static final String ACTION = "SetRelativeVolume";
	private static final String PARAMETERS_HEAD = "<InstanceID>0</InstanceID><Channel>Master</Channel><Adjustment>";
	private static final String PARAMETERS_TRAIL = "</Adjustment>";

	public SetRelativeVolumeAction(int val) {
		super(ACTION, PARAMETERS_HEAD + val + PARAMETERS_TRAIL);
	}

}
