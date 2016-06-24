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
public class AddURIToQueueAction extends AVTransportAction {

	private static final String ACTION = "AddURIToQueue";
	private static final String PARAMETERS_0 = "<InstanceID>0</InstanceID><EnqueuedURI>";
	private static final String PARAMETERS_1 = "</EnqueuedURI><EnqueuedURIMetaData>";
	private static final String PARAMETERS_2 = "</EnqueuedURIMetaData><DesiredFirstTrackNumberEnqueued>";
	private static final String PARAMETERS_3 = "</DesiredFirstTrackNumberEnqueued><EnqueueAsNext>";
	private static final String PARAMETERS_4 = "</EnqueueAsNext>";

	public AddURIToQueueAction(String uri, String metadata, int trackNumber, boolean asNext) {
		super(ACTION, PARAMETERS_0 + uri + PARAMETERS_1 + metadata + PARAMETERS_2 + trackNumber
				+ PARAMETERS_3 + (asNext ? "1" : "0") + PARAMETERS_4);
	}
}
