package cc.sferalabs.sfera.drivers.sonos.events;

import java.util.HashMap;
import java.util.Map;

import cc.sferalabs.sfera.events.BaseEvent;
import cc.sferalabs.sfera.events.Node;

/**
 * Event triggered when the player changes track.
 * 
 * @sfera.event_id track
 * @sfera.event_val params_map the map containing the current track parameters
 * @sfera.event_val_simple num the current track number
 * 
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class SonosTrackEvent extends BaseEvent implements SonosEvent {

	private final Map<String, String> metadata;

	public SonosTrackEvent(Node source, String number, Map<String, String> metadata) {
		super(source, "track");
		if (number != null) {
			if (metadata == null) {
				metadata = new HashMap<>();
			}
			metadata.put("number", number);
		}
		if (metadata != null && metadata.isEmpty()) {
			metadata = null;
		}
		this.metadata = metadata;
	}

	/**
	 * @return the map containing the current track parameters
	 */
	@Override
	public Map<String, String> getValue() {
		return metadata;
	}

	/**
	 * @return the current track number
	 */
	@Override
	public Integer getSimpleValue() {
		return getNumber();
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private String getMetadata(String key) {
		if (metadata == null) {
			return null;
		}
		return metadata.get(key);
	}

	/**
	 * @return the current track number
	 */
	public Integer getNumber() {
		String n = getMetadata("number");
		if (n != null) {
			try {
				return Integer.parseInt(n);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	/**
	 * 
	 * @return the current track title
	 */
	public String getTitle() {
		return getMetadata("title");
	}

	/**
	 * 
	 * @return the current track creator
	 */
	public String getCreator() {
		return getMetadata("creator");
	}

	/**
	 * 
	 * @return the current track album
	 */
	public String getAlbum() {
		return getMetadata("album");
	}

	/**
	 * 
	 * @return the current track album image URI
	 */
	public String getAlbumArtURI() {
		return getMetadata("albumarturi");
	}
}
