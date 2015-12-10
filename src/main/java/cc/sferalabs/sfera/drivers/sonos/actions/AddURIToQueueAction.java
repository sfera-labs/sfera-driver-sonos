package cc.sferalabs.sfera.drivers.sonos.actions;

public class AddURIToQueueAction extends AVTransportAction {

	private static final String ACTION = "AddURIToQueue";
	private static final String PARAMETERS_0 = "<InstanceID>0</InstanceID><EnqueuedURI>";
	private static final String PARAMETERS_1 = "</EnqueuedURI><EnqueuedURIMetaData>";
	private static final String PARAMETERS_2 = "</EnqueuedURIMetaData><DesiredFirstTrackNumberEnqueued>";
	private static final String PARAMETERS_3 = "</DesiredFirstTrackNumberEnqueued><EnqueueAsNext>";
	private static final String PARAMETERS_4 = "</EnqueueAsNext>";

	/**
	 * 
	 * @param uri
	 * @param metadata
	 */
	public AddURIToQueueAction(String uri, String metadata, int trackNumber,
			boolean asNext) {
		super(ACTION, PARAMETERS_0 + uri + PARAMETERS_1 + metadata
				+ PARAMETERS_2 + trackNumber + PARAMETERS_3
				+ (asNext ? "1" : "0") + PARAMETERS_4);
	}
}
