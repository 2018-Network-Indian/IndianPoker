package indianServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	final static int PORT = 9977;
	
	
	public static void main(String args[]) throws IOException {
		
			ServerSocket socket = new ServerSocket(PORT);
			while(true) {
				Socket csock = socket.accept();
				Accept exe = new Accept(csock);
			
				new Thread(exe).start();
			} 
	}
}