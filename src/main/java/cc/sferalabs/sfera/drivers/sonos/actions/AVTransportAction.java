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

/**
 *
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class AVTransportAction extends Action {

	private static final String ENDPOINT = "/MediaRenderer/AVTransport/Control";
	private static final String SERVICE = "urn:schemas-upnp-org:service:AVTransport:1";

	public static final AVTransportAction PLAY = new AVTransportAction("Play",
			"<InstanceID>0</InstanceID><Speed>1</Speed>");

	public static final AVTransportAction PAUSE = new AVTransportAction("Pause",
			"<InstanceID>0</InstanceID>");

	public static final AVTransportAction STOP = new AVTransportAction("Stop",
			"<InstanceID>0</InstanceID>");

	public static final AVTransportAction NEXT = new AVTransportAction("Next",
			"<InstanceID>0</InstanceID>");

	public static final AVTransportAction PREVIOUS = new AVTransportAction("Previous",
			"<InstanceID>0</InstanceID>");

	public static final AVTransportAction GET_POSITION_INFO = new AVTransportAction(
			"GetPositionInfo", "<InstanceID>0</InstanceID>");

	public static final AVTransportAction GET_TRANSPORT_INFO = new AVTransportAction(
			"GetTransportInfo", "<InstanceID>0</InstanceID>");

	public AVTransportAction(String action, String parameters) {
		super(action, ENDPOINT, SERVICE, parameters);
	}
}
