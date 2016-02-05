package cc.sferalabs.sfera.drivers.sonos.events;

import cc.sferalabs.sfera.events.BooleanEvent;
import cc.sferalabs.sfera.events.Node;

public class SonosConnectedEvent extends BooleanEvent implements SonosEvent {

	public SonosConnectedEvent(Node source, boolean value) {
		super(source, "connected", value);
	}

}
