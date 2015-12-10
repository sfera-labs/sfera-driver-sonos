package cc.sferalabs.sfera.drivers.sonos.actions;

public class SetVolumeAction extends RenderingControlAction {

	private static final String ACTION = "SetVolume";
	private static final String PARAMETERS_HEAD = "<InstanceID>0</InstanceID><Channel>Master</Channel><DesiredVolume>";
	private static final String PARAMETERS_TRAIL = "</DesiredVolume>";

	/**
	 * 
	 * @param val
	 */
	public SetVolumeAction(int val) {
		super(ACTION, PARAMETERS_HEAD + val + PARAMETERS_TRAIL);
	}

}
