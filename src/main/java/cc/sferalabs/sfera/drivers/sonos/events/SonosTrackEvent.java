package cc.sferalabs.sfera.drivers.sonos.events;

import java.util.HashMap;
import java.util.Map;

import cc.sferalabs.sfera.events.BaseEvent;
import cc.sferalabs.sfera.events.Node;

public class SonosTrackEvent extends BaseEvent implements SonosEvent {

	private final Map<String, String> metadata;

	/**
	 * 
	 * @param source
	 * @param number
	 * @param metadata
	 */
	public SonosTrackEvent(Node source, String number,
			Map<String, String> metadata) {
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

	@Override
	public Map<String, String> getValue() {
		return metadata;
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
	 * 
	 * @return
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
	 * @return
	 */
	public String getTitle() {
		return getMetadata("title");
	}

	/**
	 * 
	 * @return
	 */
	public String getCreator() {
		return getMetadata("creator");
	}

	/**
	 * 
	 * @return
	 */
	public String getAlbum() {
		return getMetadata("album");
	}

	/**
	 * 
	 * @return
	 */
	public String getAlbumArtURI() {
		return getMetadata("albumarturi");
	}
}
