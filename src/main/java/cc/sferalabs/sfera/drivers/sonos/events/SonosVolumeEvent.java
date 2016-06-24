package cc.sferalabs.sfera.drivers.sonos.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.NumberEvent;

/**
 * Event triggered when the volume of the player changes.
 * 
 * @sfera.event_id volume
 * @sfera.event_val 0-100 the current volume level
 * 
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class SonosVolumeEvent extends NumberEvent implements SonosEvent {

	public SonosVolumeEvent(Node source, String value) {
		super(source, "volume", value == null ? null : Integer.parseInt(value));
	}

}
