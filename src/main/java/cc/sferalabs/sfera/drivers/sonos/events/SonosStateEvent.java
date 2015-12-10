package cc.sferalabs.sfera.drivers.sonos.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.StringEvent;

public class SonosStateEvent extends StringEvent implements SonosEvent {

	public SonosStateEvent(Node source, String value) {
		super(source, "state", value == null ? null : value.toLowerCase());
	}

}
