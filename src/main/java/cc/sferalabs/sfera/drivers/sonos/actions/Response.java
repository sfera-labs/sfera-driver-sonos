package cc.sferalabs.sfera.drivers.sonos.actions;

import java.util.Map;

/**
 *
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class Response {

	public final boolean ok;
	public final int code;
	public final Map<String, String> params;
	public final String errorMessage;

	public Response(boolean ok, int code, Map<String, String> params, String errorMessage) {
		this.ok = ok;
		this.code = code;
		this.params = params;
		this.errorMessage = errorMessage;
	}

}
