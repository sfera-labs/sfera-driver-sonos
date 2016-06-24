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
public class SetRelativeVolumeAction extends RenderingControlAction {

	private static final String ACTION = "SetRelativeVolume";
	private static final String PARAMETERS_HEAD = "<InstanceID>0</InstanceID><Channel>Master</Channel><Adjustment>";
	private static final String PARAMETERS_TRAIL = "</Adjustment>";

	public SetRelativeVolumeAction(int val) {
		super(ACTION, PARAMETERS_HEAD + val + PARAMETERS_TRAIL);
	}

}
