package indianServer;

import java.awt.AWTException;
import java.awt.Robot;
import java.net.Socket;

public class GameUser {
	private String myId;
	private GameRoom myRoom;
	private Socket mySocket;
	private String myGameName;
	private int myGarnet, myCard;
	private boolean myTurn, die;
	private int port;
	//public GameUser() {}
	public GameUser(String id, Socket sock) {
		myId = id;
		mySocket = sock;
		myRoom = null;
		myGameName = null;
		myGarnet = 20;
		myTurn = false;
		die = false;
	}
	
	public void joinRoom(GameRoom room) {
		myRoom = room;
		myGameName = room.getName();
	}
	
	public void detachRoom(GameRoom room) {
		myRoom = null;
		myGameName = null;
	}
	public void setmyRoom(GameRoom room) {
		this.myRoom = room;
	}

	public void waitGame(Accept acc) {
		int delayTime = 4000;

		Robot r = null;
		try {
			r = new Robot();
		} catch (AWTException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		myRoom = acc.getMyRoom();

		while (myRoom.getCurrentUser() < 2) {
			r.delay(delayTime);
			acc.sendMsg("wait");
		}
		try {		
			acc.sendMsg("Game Start~");
			GameProc go = new GameProc(this);
		} catch (NullPointerException e) {
			// e.getCause();
			// System.out.println("¿©±â·Î;;");
			acc.sendMsg("Game Start");
			this.setmyRoom(myRoom);
			GameProc go = new GameProc(this);
		}

	}
	
	public GameRoom getRoom() {
		return myRoom;
	}
	
	public String getId() {
		return myId;
	}
	
	public Socket getSocket() {
		return mySocket;
	}
	
	public String getGameName() {
		return myGameName;
	}
	
	public void setGameName(String str) {
		myGameName = str;
	}
	
	public int getGarnet() {
		return myGarnet;
	}
	
	public void incGarnet(int n) {
		myGarnet += n;
	}
	
	public void decGarnet(int n) {
		myGarnet -= n;
	}
	
	public void reSetGarnet() {
		myGarnet = 20;
	}
	
	public void setMyTurn(boolean turn) {
		myTurn = turn;
	}
	
	public boolean getMyTurn() {
		return myTurn;
	}
	
	public void setMyCard(int x) {
		myCard = x;
	}
	
	public int getMyCard() {
		return myCard;
	}
	
	public void setDie(boolean d) {
		die = d;
	}
	
	public boolean getDie() {
		return die;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getPort() {
		return port;
	}
	@Override
	public String toString() {
		return "ID : " + myId ;
	}
}
