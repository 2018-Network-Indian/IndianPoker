package indianClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class GreenRoomFrame extends JFrame{
	private BufferedImage img;
	private JLayeredPane lp;
	private JLabel makeroom;
	private File bgm;
	private Clip clip;
	private Vector data;
	private JTable table;
	private JButton btnRoomCreate;
	private JButton refresh;
	private static Connect conn;
	
	public GreenRoomFrame() {		
		super("Indian Poker");
		this.conn = new Connect();
		bgm = new File("rsc/list.wav");
		try {
		clip = AudioSystem.getClip();
		//clip.open(AudioSystem.getAudioInputStream(bgm));
		clip.start();
		clip.loop(3);
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("??");
		data = conn.getRoomList();
		
		showScreen();		
	}
	
	void showScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocation(screenSize.width/2 - this.getSize().width/2 - 550, screenSize.height/2 - 350 - this.getSize().height/2);
		this.setSize(1200, 700);
		this.setResizable(false);
		this.setLayout(null);
		this.setUndecorated(true);
		
		lp = new JLayeredPane();
		lp.setBounds(0,0,1200,700);
		lp.setLayout(null);
		
		try {
			img = ImageIO.read(new File("rsc/greenroom.png"));
		} catch (IOException e) {
		}
		BackImg backimg = new BackImg();
		backimg.setBounds(0, 0, 1200, 700);
		
		Vector<String> col = new Vector<String>();
		col.add("�� ��ȣ");
		col.add("�� �̸�");
		col.add("���� ");
		col.add("�ο� ");
		
		DefaultTableModel model = new DefaultTableModel(data,col) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(model);
		jTableSet();
		table.setBounds(147,30,850,700);
		table.addMouseListener(new MouseClick());;
		lp.add(table);
		refresh = new JButton(new ImageIcon("rsc/refresh.png"));
		refresh.setBounds(900, -15, 80, 70);	
		refresh.setBorderPainted(false);
		
		refresh.addActionListener(new ClickListener());
		refresh.setBackground(Color.BLACK);
		lp.add(refresh);
		
		btnRoomCreate = new JButton(new ImageIcon("rsc/roomCreates.png"));		
		btnRoomCreate.setBounds(1000,650, 30, 30);
		btnRoomCreate.setFocusPainted(false);
		btnRoomCreate.setContentAreaFilled(false);
		btnRoomCreate.setBorderPainted(false);
		btnRoomCreate.addActionListener(new ClickListener());
		lp.add(btnRoomCreate);
		
		makeroom = new JLabel("�� �����");
		makeroom.setBounds(1070, 645, 100, 70);
		makeroom.setFont(new Font("serif", Font.BOLD, 15));
		makeroom.setForeground(Color. CYAN);
		lp.add(makeroom);
		
		lp.add(backimg);
		this.add(lp);
		this.setVisible(true);
	}
	
	public void jTableSet() {
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		DefaultTableCellRenderer celAlignCenter = new DefaultTableCellRenderer();
		
		for(int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(10);
			table.getColumnModel().getColumn(i).setCellRenderer(celAlignCenter);
		}
		table.setBackground(Color.lightGray);
		table.setOpaque(false);
		table.setRowHeight(35);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setForeground(Color.BLUE);
	}

	private class BackImg extends JPanel{
		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}
	}
	
	private class ClickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			/*if(Connect.roomCreate();) {				 
				clip.close();
				lp.removeAll();
				dispose();
				GameTable go = new GameTable();
			}else if(e.getSource() == refresh){
				clip.close();
				lp.removeAll();
				dispose();
				GreenRoom re = new GreenRoom();
			} */
			if(e.getSource() == btnRoomCreate) {
				System.out.println("CREATE");
				conn.roomCreate(); 
				clip.close();
				lp.removeAll();
				dispose();
				GameTableFrame go = new GameTableFrame();
			}else if(e.getSource() == refresh){
				System.out.println("GRENNROOM");
				clip.close();
				lp.removeAll();
				dispose();
				conn.refreshRoom();
				GreenRoomFrame re = new GreenRoomFrame();
				
			}
			
		}
	}
	
	private class MouseClick implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			int index = table.getSelectedRow();
			System.out.println("Ŭ���̺�Ʈ ��" + index);
			if(index > -1) {
				int roomid = (int) table.getValueAt(index, 0);
				if(roomid != 0) {
					boolean retval = conn.joinRoom(roomid);	
					clip.close();
					lp.removeAll();
					dispose();
					if(retval == true) {
						GameTableFrame go = new GameTableFrame();
					}
					else {
						GreenRoomFrame re = new GreenRoomFrame();
					}
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}	
	}
}
