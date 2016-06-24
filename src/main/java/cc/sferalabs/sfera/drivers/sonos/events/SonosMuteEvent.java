package cc.sferalabs.sfera.drivers.sonos.events;

import cc.sferalabs.sfera.events.BooleanEvent;
import cc.sferalabs.sfera.events.Node;

/**
 * Event triggered when the player is muted or un-muted.
 * 
 * @sfera.event_id mute
 * @sfera.event_val true muted
 * @sfera.event_val false un-muted
 * 
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class SonosMuteEvent extends BooleanEvent implements SonosEvent {

	public SonosMuteEvent(Node source, String value) {
		super(source, "mute", value == null ? null : value.equals("1"));
	}

}
