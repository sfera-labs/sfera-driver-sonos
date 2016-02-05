package cc.sferalabs.sfera.drivers.sonos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import cc.sferalabs.sfera.core.Configuration;
import cc.sferalabs.sfera.core.SystemNode;
import cc.sferalabs.sfera.drivers.Driver;
import cc.sferalabs.sfera.drivers.sonos.actions.AVTransportAction;
import cc.sferalabs.sfera.drivers.sonos.actions.Action;
import cc.sferalabs.sfera.drivers.sonos.actions.AddURIToQueueAction;
import cc.sferalabs.sfera.drivers.sonos.actions.RenderingControlAction;
import cc.sferalabs.sfera.drivers.sonos.actions.Response;
import cc.sferalabs.sfera.drivers.sonos.actions.SetAVTransportURIAction;
import cc.sferalabs.sfera.drivers.sonos.actions.SetRelativeVolumeAction;
import cc.sferalabs.sfera.drivers.sonos.actions.SetVolumeAction;
import cc.sferalabs.sfera.drivers.sonos.events.SonosConnectedEvent;
import cc.sferalabs.sfera.drivers.sonos.events.SonosMuteEvent;
import cc.sferalabs.sfera.drivers.sonos.events.SonosStateEvent;
import cc.sferalabs.sfera.drivers.sonos.events.SonosStatusEvent;
import cc.sferalabs.sfera.drivers.sonos.events.SonosTrackEvent;
import cc.sferalabs.sfera.drivers.sonos.events.SonosVolumeEvent;
import cc.sferalabs.sfera.events.Bus;

public class Sonos extends Driver {

	private static final String BROADCAST_ADDR = "239.255.255.250";
	private static final int DISCOVER_PORT = 1900;
	private static final String SEARCH_TARGET = "urn:schemas-upnp-org:device:ZonePlayer:1";
	private final static String DISCOVER_MESSAGE = "M-SEARCH * HTTP/1.1\r\n" + "HOST: "
			+ BROADCAST_ADDR + ":" + DISCOVER_PORT + "\r\n" + "ST: " + SEARCH_TARGET + "\r\n"
			+ "MAN: \"ssdp:discover\"\r\n" + "MX: 2\r\n" + "\r\n";
	private static final int RESPONSE_TIMEOUT = 5000;

	private final static String UPnP_BODY_HEADER = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" "
			+ "s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body>";
	private final static String UPnP_BODY_TRAILER = "</s:Body></s:Envelope>";

	private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
	private static final int EVENTS_LOCAL_PORT = 1077;
	private static final long RESUBSCRIBE_ADVANCE = 600000;
	private static final int SUBSCRIBE_TIMEOUT_SECONDS = 3600;
	private static final int POLL_TIME = 30000;
	private int actualSubscribeTimeoutSeconds = SUBSCRIBE_TIMEOUT_SECONDS;

	String localHost;
	private String baseUrl;
	private String roomName;
	private long lastSubscribe;
	private ServerSocket eventsSocket;
	private Map<String, String> subscriptionSids;

	/**
	 * 
	 * @param id
	 */
	public Sonos(String id) {
		super(id);
	}

	@Override
	protected boolean onInit(Configuration config) throws InterruptedException {
		localHost = config.get("localhost", null);
		if (localHost == null) {
			try {
				localHost = SystemNode.getSiteLocalAddress().getHostAddress();
			} catch (Exception e) {
			}
			if (localHost == null) {
				log.error("No valid localhost found");
				return false;
			}
		}
		log.info("Using localhost: " + localHost);
		String room = config.get("room", null);
		if (room == null) {
			log.info("Looking for a device");
		} else {
			log.info("Looking for device '{}'", room);
		}
		try {
			discover(room);
		} catch (IOException e) {
			log.error("Discovery error", e);
			return false;
		}
		log.info("Connected to: {} ({})", roomName, baseUrl);
		try {
			getState();
		} catch (Exception e) {
			log.error("Get state error", e);
			return false;
		}
		try {
			eventsSocket = new ServerSocket(EVENTS_LOCAL_PORT);
			eventsSocket.setSoTimeout(POLL_TIME);
		} catch (IOException e) {
			log.error("Error creating events socket", e);
			return false;
		}
		subscriptionSids = new HashMap<>();
		Bus.postIfChanged(new SonosConnectedEvent(this, true));
		return true;
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void getState() throws Exception {
		Response r = post(AVTransportAction.GET_TRANSPORT_INFO);
		Bus.postIfChanged(new SonosStateEvent(this, r.params.get("CurrentTransportState")));
		Bus.postIfChanged(new SonosStatusEvent(this, r.params.get("CurrentTransportStatus")));

		r = post(AVTransportAction.GET_POSITION_INFO);
		Map<String, String> metadata = getTrackMetadata(r.params.get("TrackMetaData"));
		Bus.postIfChanged(new SonosTrackEvent(this, r.params.get("Track"), metadata));

		r = post(RenderingControlAction.GET_MUTE);
		Bus.postIfChanged(new SonosMuteEvent(this, r.params.get("CurrentMute")));
		r = post(RenderingControlAction.GET_VOLUME);
		Bus.postIfChanged(new SonosVolumeEvent(this, r.params.get("CurrentVolume")));
	}

	/**
	 * 
	 * @param xml
	 * @return
	 */
	private Map<String, String> getTrackMetadata(String xml) {
		Map<String, String> metadata = new HashMap<>();
		if (xml != null && !xml.isEmpty() && !xml.equalsIgnoreCase("NOT_IMPLEMENTED")) {
			XMLEventReader eventReader = null;
			try (StringReader sr = new StringReader(xml)) {
				eventReader = XML_INPUT_FACTORY.createXMLEventReader(sr);
				while (eventReader.hasNext()) {
					XMLEvent event = eventReader.nextEvent();
					if (event.isStartElement()) {
						StartElement elem = event.asStartElement();
						String name = elem.getName().getLocalPart();
						if (name.equals("title") || name.equals("creator") || name.equals("album")
								|| name.equals("albumArtURI")) {
							StringBuilder val = new StringBuilder();
							event = eventReader.nextEvent();
							while (event.isCharacters()) {
								val.append(event.asCharacters().getData());
								event = eventReader.nextEvent();
							}
							metadata.put(name.toLowerCase(), val.toString());
						}
					} else if (event.isEndElement()) {
						EndElement elem = event.asEndElement();
						if (elem.getName().getPrefix().equals("u")) {
							break;
						}
					}
				}
			} catch (Exception e) {
				log.warn("Error getting track metadata", e);
			} finally {
				if (eventReader != null) {
					try {
						eventReader.close();
					} catch (Exception e) {
					}
				}
			}
		}

		return metadata;
	}

	/**
	 * 
	 * @param roomName
	 * @throws IOException
	 */
	private void discover(String roomName) throws IOException {
		try (DatagramSocket sock = new DatagramSocket()) {
			byte[] bytes = DISCOVER_MESSAGE.getBytes(StandardCharsets.UTF_8);
			DatagramPacket discoverPacket = new DatagramPacket(bytes, bytes.length);
			discoverPacket.setAddress(InetAddress.getByName(BROADCAST_ADDR));
			discoverPacket.setPort(DISCOVER_PORT);

			log.debug("Sending discovery message");
			sock.send(discoverPacket);
			sock.setSoTimeout(RESPONSE_TIMEOUT);

			while (true) {
				byte[] buff = new byte[1536];
				DatagramPacket respPacket = new DatagramPacket(buff, buff.length);
				sock.receive(respPacket);
				log.debug("Got response from: {}", respPacket.getAddress());
				String location = getLocation(respPacket);
				log.debug("Location: {}", location);
				if (location != null && location.length() > 8) {
					String name = getRoomName(location);
					log.debug("Name: {}", name);
					if (name != null && (roomName == null || roomName.equalsIgnoreCase(name))) {
						this.roomName = name;
						this.baseUrl = location.substring(0, location.indexOf('/', 8));
						return;
					}
				}
			}
		}
	}

	/**
	 * @param location
	 * @return
	 */
	private String getRoomName(String location) {
		HttpURLConnection connection = null;
		XMLEventReader eventReader = null;
		InputStream in = null;
		try {
			connection = (HttpURLConnection) (new URL(location)).openConnection();
			in = connection.getInputStream();
			eventReader = XML_INPUT_FACTORY.createXMLEventReader(in, StandardCharsets.UTF_8.name());
			String roomName = null;
			boolean hasMediaRenderer = false;
			boolean inDevice = false;
			boolean inDeviceList = false;
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					String tag = startElement.getName().getLocalPart();
					if (inDevice) {
						if (roomName == null && tag.equalsIgnoreCase("roomName")) {
							if (eventReader.hasNext()) {
								event = eventReader.nextEvent();
								if (event.isCharacters()) {
									roomName = event.asCharacters().getData();
								}
							}
						}
						if (inDeviceList) {
							if (tag.equalsIgnoreCase("deviceType")) {
								if (eventReader.hasNext()) {
									event = eventReader.nextEvent();
									if (event.isCharacters() && event.asCharacters().getData()
											.contains(":MediaRenderer:")) {
										hasMediaRenderer = true;
										break;
									}
								}
							}
						} else {
							if (tag.equalsIgnoreCase("deviceList")) {
								inDeviceList = true;
							}
						}
					} else {
						if (tag.equalsIgnoreCase("device")) {
							inDevice = true;
						}
					}

				} else if (inDevice && event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					String tag = endElement.getName().getLocalPart();
					if (tag.equalsIgnoreCase("deviceList")) {
						inDeviceList = false;
					}
				}
			}

			if (hasMediaRenderer) {
				return roomName;
			}
		} catch (Exception e) {
			log.debug("Error parsing description XML", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
			if (eventReader != null) {
				try {
					eventReader.close();
				} catch (Exception e) {
				}
			}
			if (connection != null) {
				connection.disconnect();
			}
		}

		return null;
	}

	/**
	 * @param packet
	 * @return
	 */
	private String getLocation(DatagramPacket packet) {
		String data = new String(packet.getData(), StandardCharsets.UTF_8);
		String location = null;
		String st = null;
		String[] lines = data.split("\n");
		for (String line : lines) {
			String lineLC = line.trim().toLowerCase();
			if (lineLC.startsWith("location:")) {
				try {
					location = line.substring(line.indexOf(':') + 1).trim();
				} catch (Exception e) {
				}
			} else if (lineLC.startsWith("st:")) {
				try {
					st = line.substring(line.indexOf(':') + 1).trim();
				} catch (Exception e) {
				}
			}
		}

		if (location == null || st == null) {
			return null;
		}

		if (st.contains(":ZonePlayer:")) {
			return location;
		}

		return null;
	}

	@Override
	protected boolean loop() throws InterruptedException {
		Socket sock = null;
		try {
			if (lastSubscribe < System.currentTimeMillis() - (actualSubscribeTimeoutSeconds * 1000)
					+ RESUBSCRIBE_ADVANCE) {
				subscribe("/MediaRenderer/AVTransport/Event");
				subscribe("/MediaRenderer/RenderingControl/Event");
				lastSubscribe = System.currentTimeMillis();
			}

			try {
				sock = eventsSocket.accept();
				try (BufferedWriter bw = new BufferedWriter(
						new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_8))) {
					bw.write("HTTP/1.1 200 OK\r\n");
					bw.write("\r\n");
					bw.flush();
				}
			} catch (SocketTimeoutException ste) {
			}

			getState();

		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			log.error("Error in loop", e);
			return false;
		} finally {
			if (sock != null) {
				try {
					sock.close();
				} catch (Exception e) {
				}
			}
		}

		return true;
	}

	/**
	 * 
	 * @param publisher
	 * @throws Exception
	 */
	private void subscribe(String publisher) throws Exception {
		int slash = baseUrl.lastIndexOf('/');
		int colon = baseUrl.lastIndexOf(':');
		String host = baseUrl.substring(slash + 1, colon);
		int port = Integer.parseInt(baseUrl.substring(colon + 1));
		String callbackUri = "http://" + localHost + ":" + EVENTS_LOCAL_PORT + publisher;
		log.debug("Subscribing: {}", callbackUri);
		Socket sock = null;
		try {
			sock = new Socket(host, port);
			sock.setSoTimeout(RESPONSE_TIMEOUT);
			try (BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_8));
					BufferedReader br = new BufferedReader(
							new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8))) {
				bw.write("SUBSCRIBE " + publisher + " HTTP/1.1\r\n");
				bw.write("HOST: " + localHost + "\r\n");
				String sid = subscriptionSids.get(publisher);
				if (sid == null) {
					bw.write("CALLBACK: <" + callbackUri + ">\r\n");
					bw.write("NT: upnp:event\r\n");
				} else {
					bw.write(sid + "\r\n");
				}
				bw.write("TIMEOUT: Second-" + SUBSCRIBE_TIMEOUT_SECONDS + "\r\n");
				bw.write("\r\n");
				bw.flush();

				sid = null;
				String timeout = null;
				String line = br.readLine();
				if (!line.equalsIgnoreCase("HTTP/1.1 200 OK")) {
					throw new Exception("Response error: " + line);
				}
				while ((line = br.readLine()) != null) {
					if (line.startsWith("SID:")) {
						sid = line;
					} else if (line.startsWith("TIMEOUT:")) {
						timeout = line;
					}
				}
				subscriptionSids.put(publisher, sid);
				if (timeout != null) {
					timeout = timeout.substring(timeout.indexOf('-') + 1);
					int toInt = Integer.parseInt(timeout);
					if (toInt < actualSubscribeTimeoutSeconds) {
						actualSubscribeTimeoutSeconds = toInt;
					}
				}

			}

		} finally {
			if (sock != null) {
				try {
					sock.close();
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	protected void onQuit() {
		Bus.postIfChanged(new SonosConnectedEvent(this, false));
		baseUrl = null;
		lastSubscribe = 0;
		if (eventsSocket != null) {
			try {
				eventsSocket.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * @param action
	 * @param service
	 * @param parameters
	 * @return
	 */
	public Response action(String action, String service, Map<String, String> parameters) {
		String endpoint = "/MediaRenderer/" + service + "/Control";
		service = "urn:schemas-upnp-org:service:" + service + ":1";
		try {
			return post(new Action(action, endpoint, service, parameters));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean play() {
		return send(AVTransportAction.PLAY);
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	public boolean play(String uri) {
		return play(uri, "");
	}

	/**
	 * 
	 * @param uri
	 * @param metadata
	 * @return
	 */
	public boolean play(String uri, String metadata) {
		boolean ok = send(new SetAVTransportURIAction(uri, metadata));
		if (!play()) {
			ok = false;
		}
		return ok;
	}

	/**
	 * 
	 * @return
	 */
	public boolean pause() {
		return send(AVTransportAction.PAUSE);
	}

	/**
	 * 
	 * @return
	 */
	public boolean stop() {
		return send(AVTransportAction.STOP);
	}

	/**
	 * 
	 * @return
	 */
	public boolean next() {
		return send(AVTransportAction.NEXT);
	}

	/**
	 * 
	 * @return
	 */
	public boolean previous() {
		return send(AVTransportAction.PREVIOUS);
	}

	/**
	 * 
	 * @param val
	 * @return
	 */
	public boolean setVolume(int val) {
		return send(new SetVolumeAction(val));
	}

	/**
	 * 
	 * @param val
	 * @return
	 */
	public boolean setRelativeVolume(int val) {
		return send(new SetRelativeVolumeAction(val));
	}

	/**
	 * 
	 * @param val
	 * @return
	 */
	public boolean setMute(boolean val) {
		Action a = val ? RenderingControlAction.SET_MUTE_TRUE
				: RenderingControlAction.SET_MUTE_FALSE;
		return send(a);
	}

	/**
	 * 
	 * @param uri
	 * @param metadata
	 * @param trackNumber
	 * @param asNext
	 * @return
	 */
	public boolean addToQueue(String uri, String metadata, int trackNumber, boolean asNext) {
		return send(new AddURIToQueueAction(uri, metadata, trackNumber, asNext));
	}

	/**
	 * 
	 * @param uri
	 * @param trackNumber
	 * @return
	 */
	public boolean addToQueue(String uri, int trackNumber) {
		return addToQueue(uri, "", trackNumber, false);
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	public boolean addToQueue(String uri) {
		return addToQueue(uri, 0);
	}

	/**
	 * 
	 * @param action
	 * @return
	 */
	private boolean send(Action action) {
		try {
			return post(action).ok;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @param action
	 * @return
	 * @throws Exception
	 */
	private Response post(Action action) throws Exception {
		if (baseUrl == null) {
			log.error("Driver not initialized. Cannot perform action: {}", action.getAction());
			throw new Exception("Driver not initialized");
		}
		log.debug("Action: {}", action.getAction());
		HttpURLConnection connection = null;
		try {
			StringBuilder body = new StringBuilder(UPnP_BODY_HEADER);
			body.append("<u:").append(action.getAction()).append(" ");
			body.append("xmlns:u=\"").append(action.getService()).append("\">");
			body.append(action.getParameters());
			body.append("</u:").append(action.getAction()).append(">");
			body.append(UPnP_BODY_TRAILER);

			byte[] data = body.toString().getBytes(StandardCharsets.UTF_8);

			URL url = new URL(baseUrl + action.getEndpoint());
			log.debug("Post request to: {}", url);
			log.debug("Post request body: {}", body);

			connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setReadTimeout(RESPONSE_TIMEOUT);
			connection.setConnectTimeout(RESPONSE_TIMEOUT);
			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setRequestProperty("SOAPAction",
					"\"" + action.getService() + "#" + action.getAction() + "\"");
			connection.setRequestProperty("Connection", "Close");
			connection.setRequestProperty("Content-Length", Integer.toString(data.length));
			connection.setDoOutput(true);

			connection.getOutputStream().write(data);

			log.debug("Post request sent");

			int respCode = connection.getResponseCode();
			boolean ok = respCode == HttpURLConnection.HTTP_OK;
			Map<String, String> prms = null;
			String errorMessage = null;
			if (ok) {
				prms = getResponseParams(connection);

			} else {
				log.warn("Action response error. Action: {}", action.getAction());
				try (InputStream in = connection.getErrorStream();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(in, StandardCharsets.UTF_8))) {
					errorMessage = br.lines().collect(Collectors.joining());
				} catch (Exception e) {
					log.debug("Error reading error message", e);
				}
			}

			Response resp = new Response(ok, respCode, prms, errorMessage);

			log.debug("Response code: {}", resp.code);
			if (ok) {
				log.debug("Response params: {}", resp.params);
			} else {
				log.debug("Response error message: {}", resp.errorMessage);
			}

			return resp;

		} catch (Exception e) {
			throw new Exception("Error processing action: " + action.getAction(), e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * 
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> getResponseParams(HttpURLConnection connection) throws Exception {
		Map<String, String> prms = new HashMap<>();
		XMLEventReader eventReader = null;
		try (InputStream in = connection.getInputStream()) {
			eventReader = XML_INPUT_FACTORY.createXMLEventReader(in, StandardCharsets.UTF_8.name());
			boolean put = false;
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					StartElement elem = event.asStartElement();
					if (put) {
						StringBuilder val = new StringBuilder();
						event = eventReader.nextEvent();
						while (event.isCharacters()) {
							val.append(event.asCharacters().getData());
							event = eventReader.nextEvent();
						}
						String key = elem.getName().getLocalPart();
						prms.put(key, val.toString());
					} else if (elem.getName().getPrefix().equals("u")) {
						put = true;
					}

				} else if (event.isEndElement()) {
					EndElement elem = event.asEndElement();
					if (elem.getName().getPrefix().equals("u")) {
						break;
					}
				}
			}

			return prms;

		} finally {
			if (eventReader != null) {
				try {
					eventReader.close();
				} catch (Exception e) {
				}
			}
		}
	}

}
