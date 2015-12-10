package cc.sferalabs.sfera.drivers.sonos.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.StringEvent;

public class SonosStatusEvent extends StringEvent implements SonosEvent {

	public SonosStatusEvent(Node source, String value) {
		super(source, "status", value == null ? null : value.toLowerCase());
	}

}
