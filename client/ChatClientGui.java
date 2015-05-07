import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BoxLayout;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ChatClientGui extends JFrame implements WindowListener {

	private JTextField serverIPField; //server to connect to
	private JSpinner serverPortField; //server port
	private JTextField userHandleField; //username
	private JButton connectButton;
	private JButton disconnectButton;
	private JButton getButton;

	private JTextArea chatDisplayField;
	private JTextField chatEntryField;

	private ClientConnection myConnection;

	private Thread getResponseFromServerThread;

	/**
	 * Sets up chat client window (loosely based on demos from CS108)
	 */
	public ChatClientGui() {
		myConnection = new ClientConnection();
		setTitle("Caesar Cipher Client");
		setLayout(new FlowLayout());

		serverIPField = new JTextField("127.0.0.1");
		this.add(serverIPField);
		serverPortField = new JSpinner();
		serverPortField.setValue(12345);
		add(serverPortField);

		userHandleField = new JTextField();
		userHandleField.setText("me");
		add(userHandleField);

		connectButton = new JButton();
		connectButton.setText("Connect");
		connectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				myConnection.initialize(serverIPField.getText(),
						(int) serverPortField.getValue());
				// rotationAmountField.getText());
				if (myConnection.connect()) {
					chatDisplayField.append("Connected to server\n");
					serverIPField.setEnabled(false);
					serverPortField.setEnabled(false);
					userHandleField.setEnabled(false);
					connectButton.setEnabled(false);
					disconnectButton.setEnabled(true);
					
					myConnection.send(userHandleField.getText() + " has connected."); //TODO should this be on the server end?
				} else {
					chatDisplayField
							.append("Failed to connect; check your settings (is there, in fact, a server there?)\n");
				}
				receiveFromServer(); // start thread to get responses from
										// server
			}
		});
		add(connectButton);

		disconnectButton = new JButton();
		disconnectButton.setText("Disconnect");
		disconnectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getResponseFromServerThread.interrupt(); // stop listening for
															// responses from
															// server
				if (myConnection.disconnect()) {
					chatDisplayField.append("Disconnected from server\n");
					serverIPField.setEnabled(true);
					serverPortField.setEnabled(true);
					userHandleField.setEnabled(true);
					connectButton.setEnabled(true);
					disconnectButton.setEnabled(false);
				} else {
					chatDisplayField.append("Failed to disconnect\n");
					// resume listening for messages from server
					receiveFromServer();
				}
			}
		});
		disconnectButton.setEnabled(false);
		add(disconnectButton);

		getButton = new JButton();
		getButton.setText("Receive");
		getButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				chatDisplayField.append(myConnection.receive() + "\n");

			}
		});
		getButton.setEnabled(true);
		// add(getButton);

		chatDisplayField = new JTextArea(15, 20);
		chatDisplayField.setEditable(false);
		chatDisplayField.setLineWrap(true);
		chatDisplayField.setWrapStyleWord(true);
		chatDisplayField.setText("Results should show up here\n");
		add(chatDisplayField);

		chatEntryField = new JTextField(20);
		chatEntryField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO should extract this part and add a "send" button as well
				if (myConnection.isConnected()) {
					myConnection.send(userHandleField.getText() + " says: " + chatEntryField.getText()); //TODO pref'd format?
					chatEntryField.setText("");
				}

			}
		});
		add(chatEntryField);

		addWindowListener(this);

		this.pack();
	}

	/**
	 * gets the show on the road by making our window show up
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ChatClientGui myController = new ChatClientGui();
		myController.pack();
		myController.setVisible(true);
	}

	/**
	 * spin up a thread to receive responses from the server
	 */
	public void receiveFromServer() {
		Runnable myRunnable = new Runnable() {

			@Override
			public void run() {
				String receivedText;
				while (myConnection.isConnected()) {
					receivedText = myConnection.receive();
					if (receivedText == "") {
						continue;
					}
					try {
						for (int i = 0; i < receivedText.length(); i++) {
							// System.out.println((int)(char)receivedText.charAt(i));
							if (receivedText.charAt(i) != '\0') {
								chatDisplayField.append(""
										+ receivedText.charAt(i));
							}
						}
					} catch (Exception e) {
						// do nothing
					}

					try {
						chatDisplayField.append("\n");
					} catch (Exception e) {
						// do nothing
					}
				}

			}
		};
		getResponseFromServerThread = new Thread(myRunnable);
		getResponseFromServerThread.start();
	}

	/**
	 * Makes the program actually stop when you close its window
	 */
	@Override
	public void windowClosing(WindowEvent arg0) {
		// System.out.println("Closing");
		if (myConnection.isConnected()) { // if we're connected, disconnect
			getResponseFromServerThread.interrupt(); // stop listening for
														// responses from server
			myConnection.disconnect();
		}
		System.exit(0);
	}

	// ---------------------------------------------------------------------------------------

	// would you believe all of these are here even though they do nothing, just
	// so I've implemented the whole interface?

	@Override
	public void windowClosed(WindowEvent e) {
		// stub
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// System.out.println("iconified");
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// System.out.println("opened");
	}

}
