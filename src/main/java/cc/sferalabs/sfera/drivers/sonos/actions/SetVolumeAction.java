package cc.sferalabs.sfera.drivers.sonos.actions;

/**
 *
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class SetVolumeAction extends RenderingControlAction {

	private static final String ACTION = "SetVolume";
	private static final String PARAMETERS_HEAD = "<InstanceID>0</InstanceID><Channel>Master</Channel><DesiredVolume>";
	private static final String PARAMETERS_TRAIL = "</DesiredVolume>";

	public SetVolumeAction(int val) {
		super(ACTION, PARAMETERS_HEAD + val + PARAMETERS_TRAIL);
	}

}
