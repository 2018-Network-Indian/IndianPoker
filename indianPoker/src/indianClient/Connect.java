package indianClient;

import java.awt.Robot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.JOptionPane;

public class Connect {
	// final static String IP = "219.252.216.105";
	final static String IP = "127.0.0.1";
	final static int PORT = 9977;
	static Socket socket;
	static OutputStream out;
	static DataOutputStream dout;
	static InputStream in;
	static DataInputStream din;
	static ObjectInputStream oin;
	static ObjectOutputStream oout;

	static boolean flag = false;

	public static void serverConn(String id, String pwd) {
		try {
			socket = new Socket(IP, PORT);

			out = socket.getOutputStream();
			dout = new DataOutputStream(out);

			in = socket.getInputStream();
			din = new DataInputStream(in);

			dout.writeUTF(id);
			dout.writeUTF(pwd);
			String ok = din.readUTF();
			if (ok.equals("OK"))
				flag = true;
			System.out.println(flag);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "������ ������ �� �����ϴ�.", "���ӿ���", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Vector getRoomList() {
		Vector data = new Vector();

		try {
			
			int count = din.readInt();
			System.out.println("##" + count);

			for (int i = 0; i < count; i++) {
				int id = din.readInt();
				String roomName = din.readUTF();
				String owner = din.readUTF();
				int num = din.readInt();
				Vector row = new Vector();
				row.add(id);
				row.add(roomName);
				row.add(owner);
				row.add(num);
				data.add(row);
				System.out.println(id + " " + roomName + " " + owner + " " + num);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	public static boolean refreshRoom() {
		String send = "Refresh";
		try {
			dout.writeUTF(send);			
			String retval = din.readUTF();
			if (retval.equals("RFS"))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	public static boolean roomCreate() {
		String name = JOptionPane.showInputDialog("���̸��� �Է��ϼ��� : ");
		System.out.println(name);
		if (name == null)
			return false;
		String send = "RoomCreate";
		try {
			dout.writeUTF(send);
			dout.writeUTF(name);
			String retval = din.readUTF();
			if (retval.equals("RCS"))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean joinRoom(int roomid) {
		String send = "RoomJoin";
		try {
			dout.writeUTF(send);
			dout.writeInt(roomid);
			String retval = din.readUTF();
			if (retval.equals("RJS"))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean gameStart(GameTableFrame game) {
		try {
			Robot r = new Robot();
			String wait = din.readUTF();
			while (wait.equals("wait")) {
				r.delay(2000);
				wait = din.readUTF();
				System.out.println(wait);
			}
			game.changeMsg("������ �����մϴ�.");
			new Thread(new Chat(game)).start();
			
			String gameInfo = din.readUTF();
			game.getUserName(gameInfo);
			System.out.println(gameInfo);
			r.delay(2500);

			game.changeMsg("���� �����մϴ�.");
			int p1Card, p2Card;

			p1Card = din.readInt();
			p2Card = din.readInt();

			r.delay(2500);

			// �����ֱ�
			game.setP1Card(p1Card);
			game.setP2Card(p2Card);
			if (game.isOwner()) {
				if (p1Card > p2Card) {
					game.changeMsg("����� ���Դϴ�.");
					r.delay(2500);
					game.setEnable();
				} else {
					game.changeMsg("������ ���Դϴ�.");
					r.delay(2500);
					game.setDisable();
				}
			} else {
				if (p1Card > p2Card) {
					game.changeMsg("������ ���Դϴ�.");
					r.delay(2500);
					game.setDisable();
				} else {
					game.changeMsg("����� ���Դϴ�.");
					r.delay(2500);
					game.setEnable();
				}
			}

			// ���� ���μ���
			String endsignal = din.readUTF();
			while (!endsignal.equals("Game Over")) {
				System.out.println(endsignal);
				game.initCardView();
				int otherGarnet = din.readInt(); // ��� ���� ������Ʈ
				game.updateGarnet(otherGarnet);
				game.updateBetSum(din.readInt());
				game.updateBettingvalue(din.readInt());
				game.setDie(false);

				int otherCard = din.readInt();// ���ī�� �ޱ�
				System.out.println("this turn other card : " + otherCard);
				game.setOtherCard(otherCard);

				String betsignal = din.readUTF();
				do {
					System.out.println(betsignal);
					if (betsignal.equals("Your turn")) {
						game.setEnable();
						// �������̺��� Ŭ�� �̺�Ʈ �߻� - ���� - �� - ����
						String sendop = "";
						game.setMyop("");
						do {
							sendop = game.getMyOp();
							r.delay(1500);
						} while (sendop.equals(""));
						dout.writeUTF(sendop);

						if (sendop.equals("betting")) {
							game.setBetvalue(din.readInt());
							int n = Integer.parseInt(game.getBettingString());
							dout.writeInt(n);
							System.out.println("���� ���� " + n + " " + game.getBettingString());
						} else if (sendop.equals("call")) {
							// �������� ó���� ���� ��� �޾ƾ��� ���ð� �ѹ��õȰ� ������
							game.updateBettingvalue(din.readInt());
							game.updateBetSum(din.readInt());
							game.updateMyGarnet(din.readInt());
						} else if (sendop.equals("die")) {// ������ �й�
							game.setDie(true);
						}
						System.out.println("����" + sendop);
					} else if (betsignal.equals("Wait turn")) {
						game.setDisable();
						r.delay(3500);
						String otherOp = din.readUTF();
						game.updateBettingvalue(din.readInt()); // ���� ���ݼ�
						game.updateGarnet(din.readInt()); // ���� ����
						game.updateBetSum(din.readInt());

						System.out.println("��밡 ������ �� " + game.getBetvalue());

						if (otherOp.equals("betting"))
							game.setEnable();

					}
					betsignal = din.readUTF();
				} while (!betsignal.equals("Betting end"));
				// ��� ó��
				r.delay(2000);
				int myCard = din.readInt();
				game.setMyCard(myCard); // ��ī�带 �޾Ƽ� ������
				System.out.println(myCard + " ' " + otherCard + " . " + game.isOwner());

				if (!game.getDie()) {
					if (myCard > otherCard) {
						game.changeMsg("�¸�!");
						r.delay(1500);
						game.setEnable();
						JOptionPane.showMessageDialog(null, "�̰���ϴ�!");
					} else if (otherCard > myCard) {
						game.changeMsg("�й�!");
						r.delay(1500);
						game.setDisable();
						JOptionPane.showMessageDialog(null, "�����ϴ�.");
					} else {
						game.changeMsg("���º�!");
						JOptionPane.showMessageDialog(null, "�����ϴ�.");
					}
				}
				else {
					game.changeMsg("����!");
					r.delay(1500);
					game.setDisable();
					JOptionPane.showMessageDialog(null, "�����߽��ϴ�.");				
				}

				r.delay(2000);
				int myGarnet = din.readInt();
				game.updateMyGarnet(myGarnet);
				// ��밡���� �������� ������Ʈ��

				// ��������
				endsignal = din.readUTF();
			}
			game.changeMsg("������ ����Ǿ����ϴ�.");
			System.out.println("���� ��");
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean getFlag() {
		return flag;
	}
}
