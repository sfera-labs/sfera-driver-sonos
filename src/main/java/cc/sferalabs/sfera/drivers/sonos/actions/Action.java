package cc.sferalabs.sfera.drivers.sonos.actions;

import java.util.Map;
import java.util.Map.Entry;

public class Action {

	private final String action;
	private final String endpoint;
	private final String service;
	private final String parameters;

	/**
	 * 
	 * @param action
	 * @param endpoint
	 * @param service
	 * @param parameters
	 */
	public Action(String action, String endpoint, String service,
			String parameters) {
		this.action = action;
		this.endpoint = endpoint;
		this.service = service;
		this.parameters = parameters;
	}

	/**
	 * 
	 * @param action
	 * @param endpoint
	 * @param service
	 * @param parameters
	 */
	public Action(String action, String endpoint, String service,
			Map<String, String> parameters) {
		this(action, endpoint, service, paramsMapToString(parameters));
	}

	/**
	 * 
	 * @param parameters
	 * @return
	 */
	private static String paramsMapToString(Map<String, String> parameters) {
		StringBuilder prms = new StringBuilder();
		for (Entry<String, String> e : parameters.entrySet()) {
			prms.append('<').append(e.getKey()).append('>');
			prms.append(e.getValue());
			prms.append("</").append(e.getKey()).append('>');
		}
		return prms.toString();
	}

	/**
	 * 
	 * @return
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * 
	 * @return
	 */
	public String getEndpoint() {
		return this.endpoint;
	}

	/**
	 * 
	 * @return
	 */
	public String getService() {
		return this.service;
	}

	/**
	 * 
	 * @return
	 */
	public String getParameters() {
		return this.parameters;
	}
}
