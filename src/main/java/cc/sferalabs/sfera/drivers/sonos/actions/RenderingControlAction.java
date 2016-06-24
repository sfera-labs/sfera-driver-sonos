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
public class RenderingControlAction extends Action {

	private static final String ENDPOINT = "/MediaRenderer/RenderingControl/Control";
	private static final String SERVICE = "urn:schemas-upnp-org:service:RenderingControl:1";

	public static final RenderingControlAction SET_MUTE_TRUE = new RenderingControlAction("SetMute",
			"<InstanceID>0</InstanceID><Channel>Master</Channel><DesiredMute>1</DesiredMute>");

	public static final RenderingControlAction SET_MUTE_FALSE = new RenderingControlAction(
			"SetMute",
			"<InstanceID>0</InstanceID><Channel>Master</Channel><DesiredMute>0</DesiredMute>");

	public static final RenderingControlAction GET_MUTE = new RenderingControlAction("GetMute",
			"<InstanceID>0</InstanceID><Channel>Master</Channel>");

	public static final RenderingControlAction GET_VOLUME = new RenderingControlAction("GetVolume",
			"<InstanceID>0</InstanceID><Channel>Master</Channel>");

	public RenderingControlAction(String action, String parameters) {
		super(action, ENDPOINT, SERVICE, parameters);
	}

}
