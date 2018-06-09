package indianClient;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Chat implements Runnable {
	private GameTable control;
	private final String IP = "219.252.216.105";
	//private final String IP = "127.0.0.1";
	private final int CPORT = 9976;
	private Socket csock;
	private OutputStream out;
	private DataOutputStream dout;
	private InputStream in;
	private DataInputStream din;
	private Robot r;
	
	public Chat(GameTable table) {
		control = table;
		try {
			csock = new Socket(IP, CPORT);
			out = csock.getOutputStream();
			dout = new DataOutputStream(out);
			in = csock.getInputStream();
			din = new DataInputStream(in);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
		r = new Robot();
		String signal = din.readUTF();
			while(true) {
				if(control.getWord()) {
					String send = control.getSendMsg();
					control.setWord(false);
					dout.writeUTF(send);
				}
				else 
					dout.writeUTF("null");				
				String recv = din.readUTF();
				if(recv.equals("END"))
					break;
				if(!recv.equals("msgStart")) {
					control.addChatting(recv);
					din.readUTF();
				}	
				r.delay(500);
			}
		}catch(IOException e) {return;} catch (AWTException e) {return;}
	}

}
