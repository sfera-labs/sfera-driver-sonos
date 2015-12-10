package cc.sferalabs.sfera.drivers.sonos.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.NumberEvent;

public class SonosVolumeEvent extends NumberEvent implements SonosEvent {

	public SonosVolumeEvent(Node source, String value) {
		super(source, "volume", value == null ? null : Integer.parseInt(value));
	}

}
