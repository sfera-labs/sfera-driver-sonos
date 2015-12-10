package cc.sferalabs.sfera.drivers.sonos.events;

import cc.sferalabs.sfera.events.BooleanEvent;
import cc.sferalabs.sfera.events.Node;

public class SonosMuteEvent extends BooleanEvent implements SonosEvent {

	public SonosMuteEvent(Node source, String value) {
		super(source, "mute", value == null ? null : value.equals("1"));
	}

}
