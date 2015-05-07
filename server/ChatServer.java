
/**
 * Caesar Cipher Server Service :) For CS 232, project 5
 * 
 * @author Brian Cole
 */
public class ChatServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting server");
		int myPortNumber = Integer.parseInt(args[0]);
//		int myPortNumber = 1234;
		System.out.println("Port #" + myPortNumber);
		MainServerLoop myMainLoop = new MainServerLoop(myPortNumber);
		myMainLoop.run();
	}
}
