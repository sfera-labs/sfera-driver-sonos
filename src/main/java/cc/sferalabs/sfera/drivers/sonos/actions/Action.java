/*-
 * +======================================================================+
 * Sonos
 * ---
 * Copyright (C) 2016 Sfera Labs S.r.l.
 * ---
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * -======================================================================-
 */

package cc.sferalabs.sfera.drivers.sonos.actions;

import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class Action {

	private final String action;
	private final String endpoint;
	private final String service;
	private final String parameters;

	/**
	 * 
	 * @param action
	 *            the action
	 * @param endpoint
	 *            the endpoint
	 * @param service
	 *            the uPnP service name
	 * @param parameters
	 *            the parameters
	 */
	public Action(String action, String endpoint, String service, String parameters) {
		this.action = action;
		this.endpoint = endpoint;
		this.service = service;
		this.parameters = parameters;
	}

	/**
	 * 
	 * @param action
	 *            the action
	 * @param endpoint
	 *            the endpoint
	 * @param service
	 *            the uPnP service name
	 * @param parameters
	 *            the parameters map
	 */
	public Action(String action, String endpoint, String service, Map<String, String> parameters) {
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
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * 
	 * @return the endpoint name
	 */
	public String getEndpoint() {
		return this.endpoint;
	}

	/**
	 * 
	 * @return the uPnP service name
	 */
	public String getService() {
		return this.service;
	}

	/**
	 * 
	 * @return the parameters
	 */
	public String getParameters() {
		return this.parameters;
	}
}
