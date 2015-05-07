import java.io.BufferedReader;
//import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnection {

	private String serverName;
	private int serverPort;
	private boolean areWeConnected;

	private Socket mySocket;
	private BufferedReader fromServer;
	private DataOutputStream toServer;

	/**
	 * Default constructor does almost nothing; you have to initialize() the
	 * object
	 */
	public ClientConnection() {
		super();
		areWeConnected = false;
	}

	/**
	 * Initialize a connection from client to server
	 * 
	 * @param serverName
	 *            the server to connect with
	 * @param serverPort
	 *            port to connect on
	 * @param myName
	 *            username to present in chatroom
	 */
	public void initialize(String serverName, int serverPort) {
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.areWeConnected = false;
	}

	/**
	 * @return the server name
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param serverName
	 *            new server name
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * @return the port this connection points to
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * @param serverPort
	 *            new port to connect on
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * returns connection status
	 * 
	 * @return are we connected to server
	 */
	public boolean isConnected() {
		return areWeConnected;
	}

	/**
	 * connect to server
	 */
	public boolean connect() {
		mySocket = null;
		fromServer = null;
		toServer = null;
		try {
			mySocket = new Socket(serverName, serverPort);
			// fromServer = new DataInputStream(mySocket.getInputStream());
			fromServer = new BufferedReader(new InputStreamReader(
					mySocket.getInputStream()));
			toServer = new DataOutputStream(mySocket.getOutputStream());
		} catch (UnknownHostException e) {
			// e.printStackTrace();
			return false;
		} catch (IOException e) {
			// e.printStackTrace();
			return false;
		}
		areWeConnected = true;
		return true;
	}

	/**
	 * disconnect from server
	 */
	public boolean disconnect() {
		try {
			toServer.close();
			fromServer.close();
			mySocket.close();

		} catch (IOException e) {
			// e.printStackTrace();
			return false;
		}
		areWeConnected = false;
		return true;
	}

	/**
	 * sends a string to the server, plus a terminating newline
	 * 
	 * @param message
	 *            the string to send to the server
	 */
	public void send(String message) {
		try {
			toServer.writeChars(message + " \n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * sends an int to the server
	 * 
	 * @param number
	 *            The int to send
	 */
	public void send(int number) {
		try {
			toServer.writeInt(number);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * attempts to read a line from the server
	 * 
	 * @return if we received, then the line of text we got, else null
	 */
	public String receive() {
		try {
			return fromServer.readLine();
		} catch (IOException e) {
			// e.printStackTrace();
			return null;
		}
	}
}
