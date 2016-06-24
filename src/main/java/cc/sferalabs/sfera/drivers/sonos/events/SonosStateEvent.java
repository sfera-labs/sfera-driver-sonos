package cc.sferalabs.sfera.drivers.sonos.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.StringEvent;

/**
 * Event triggered when the player changes playing state.
 * 
 * @sfera.event_id state
 * @sfera.event_val "playing" the player is playing
 * @sfera.event_val "paused_playback" the player is paused
 * @sfera.event_val "transitioning" the player is transitioning between tracks
 * @sfera.event_val "stopped" the player is stopped
 * 
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class SonosStateEvent extends StringEvent implements SonosEvent {

	public SonosStateEvent(Node source, String value) {
		super(source, "state", value == null ? null : value.toLowerCase());
	}

}
