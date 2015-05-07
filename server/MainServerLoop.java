import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServerLoop {
	ServerSocket myServerSocket = null;
	ClientConnectionRunnable myCCR;
	int myPortNumber;

	MainServerLoop(int port) {
		myPortNumber = port;
	}

	void run() {
		try { // get a server-socket:
			myServerSocket = new ServerSocket(myPortNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (true) { // inf. loop
			Socket connectionSocket;
			try { // on connection, sic a thread on it
				connectionSocket = myServerSocket.accept();

				// new
				// CaesarCipherCodingConnectionRunnable(connectionSocket).run();
				// //not threaded

				new Thread(new ClientConnectionRunnable(
						connectionSocket)).start(); // threaded

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class ClientConnectionRunnable implements Runnable {

		private int myRotationAmount;
		private Socket mySocket;

		public ClientConnectionRunnable(Socket mySocket) {
			this.mySocket = mySocket;
		}

		@Override
		public void run() {
			System.out.println("In connection-handling runnable.");

			// set up connection
			PrintWriter out;
			BufferedReader in = null;
			try {
				out = new PrintWriter(mySocket.getOutputStream());
				in = new BufferedReader(new InputStreamReader(
						mySocket.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return; // TODO is this really what I want?
			}

			// get rotation amount
			try {
				myRotationAmount = 0;
				while (myRotationAmount == 0) {
					myRotationAmount = in.read();

				}
				System.out
						.println("Got rotation amount of " + myRotationAmount);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// main response loop
			while (true) {
				try {
					char myChar = (char) in.read();

					if (myChar == '￿') {
						System.out
								.println("Got termination; exiting this runnable");
						break;
					}

					System.out.println("Read character: " + myChar);
					if (myChar >= 'A' && myChar <= 'Z') {
						myChar = (char) ((((myChar - 'A') + myRotationAmount) % 26) + 'A');
					} else if (myChar >= 'a' && myChar <= 'z') {
						myChar = (char) ((((myChar - 'a') + myRotationAmount) % 26) + 'a');
					}
					System.out.println("Sending: " + myChar);
					out.print(myChar);
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
