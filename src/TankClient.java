/*
̹�˴�ս
�汾 3.11

���ܣ����뱬ը����̹�˵���Ͳ������TankNewMsg �� TankMoveMsg�ж����ͣ���ͬ��һ��
	����IP��Сbug
	��ʱ�����д�����



*/

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * 
 * @author Hao Liu
 *̹�˴�ս �汾
 *̹�˴�ս����������
 */
public class TankClient extends Frame{	//��Frame��̳�
	/*
	 * ��Ϸ���ڵĸ߶ȺͿ��
	 */
	public static final int GAME_WIDTH = 1000;	//����Ļ��С����Ϊ����������Ķ�
	public static final int GAME_HEIGHT=800;
	
	private static Random r = new Random();

	public int score = 0;
	public int hScore = 0;
	
	NetClient nc = new NetClient(this);
	
	ConnDialog dialog = new ConnDialog();
	
	
	
	Tank myTank = new Tank(900, 700, true, Direction.D, Direction.D, this);		//������Ҫx��y�ˣ����������ʱ������Ǵ��ݽ�ȥ�Ϳ�����
	//Wall w1 = new Wall(300,600, 5, 1, "W");
	//Wall w2 = new Wall(700,150, 1, 3, "S");
	//Wall w3 = new Wall(100, 200, 2, 4, "G");
	
	Wall w1 = new Wall(300,600, 1, 1, "W");
	Wall w2 = new Wall(700,150, 1, 1, "S");
	Wall w3 = new Wall(100, 200, 1, 1, "G");
	/*
	 * ������ս̹��֮���4������
	 */
	List<Explode> explodes = new ArrayList<Explode>();
	List<Missile> missiles = new ArrayList<Missile>();	//��һ��ArrayList����װ�ӵ����Ϳ��Զ෢��
	List<Tank>	tanks = new ArrayList<Tank>();
	List<Replenishment> rps = new ArrayList<Replenishment>();
	List<SpFireBullet> spfs = new ArrayList<SpFireBullet>();


	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image bGImage =  tk.getImage(TankClient.class.getClassLoader().getResource("images/background.gif"));

	
	Image offscreemImage = null;	//����һ����Ļ�����ͼƬ����Ϊ��
	/*
	 * (non-Javadoc)
	 * @see java.awt.Window#paint(java.awt.Graphics)
	 * ������
	 */
	public void paint(Graphics g) {		//paint�����Զ�������
		
		Color c = g.getColor();
		g.drawImage(bGImage, 0, 0, null);//�����ű���ͼƬ
		g.setColor(Color.WHITE);
		g.drawString("EnemyTanks count: " + tanks.size(), 10, 40);
		g.drawString("MyTank Life:" + myTank.getLife(), 900, 765);
		g.drawString("Highest Score: " + this.hScore, 10, 50);
		g.drawString("Score: " + this.score, 10, 65);
		g.drawString("Super Fire: " + myTank.getSpFireNum() ,900, 780);
		g.setColor(c);
		
		w1.draw(g);
		w2.draw(g);
		
		//̹�����ڵ�3����ʱ���Զ�����з�̹��
/*
		if(tanks.size() <= 3) {
			for(int i=0; i<2; i++) {
				Tank tk = new Tank(r.nextInt(1000), r.nextInt(800), false, this);
				if(!tk.crashTanks(this.tanks) && !tk.crashWall(this.w1) && !tk.crashWall(this.w2)){
					tanks.add(tk);
				}
			}
		}
*/
		
		for(int i = 0; i < missiles.size(); i++){	//������������ó�����ȫ������
			Missile m = missiles.get(i);
			m.hittanks(tanks);//�����µĴ�̹�˵ķ���
			m.hitWall(w1);
			m.hitWall(w2);
			if(m.hit(myTank)){
				
				TankDeadMsg msg = new TankDeadMsg(myTank.id);
				nc.send(msg);
				MissileDeadMsg msg2 = new MissileDeadMsg(m.tankID, m.id);
				nc.send(msg2);
					
			}
			m.draw(g);
		}
		
		for(int i=0; i < explodes.size(); i++){	//���ܴ�����౬ը��ÿһ�����ó���������
			Explode e = explodes.get(i);
			e.draw(g);
		}
		
		for(int i=0; i <tanks.size(); i++){	//��������̹��
			Tank tk = tanks.get(i);
			tk.crashTanks(tanks);
			
			tk.crashWall(w1);
			tk.crashWall(w2);
			tk.draw(g);
		}
		
		for(int i=0; i<rps.size(); i++){	//����Ѫ��
			Replenishment rp = rps.get(i);
			rp.bloodCrashWall(w1);
			rp.bloodCrashWall(w2);
			rp.draw(g);
		}
		
		for(int i=0; i<spfs.size(); i++){	
			SpFireBullet spf = spfs.get(i);
			spf.BulletCrashWall(w1);
			spf.BulletCrashWall(w2);
			spf.draw(g);
		}
		

		
		myTank.draw(g);//����Ϊ̹�˱����һ��������ֻ�ðѻ��ʴ���ȥ������
		myTank.crashWall(w2);
		myTank.eatBB(rps);
		myTank.eatSPF(spfs);
		
		w3.draw(g);
	}
	
	/*
	 * ˫�����update����(non-Javadoc)
	 * @see java.awt.Container#update(java.awt.Graphics)
	 */
	public void update(Graphics g) {	//˫������ƣ���дupdate����
		if(offscreemImage == null){
			offscreemImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);	//��û�д������򴴽���ͼƬ
		}
		Graphics gOffScreem = offscreemImage.getGraphics();		//�õ�����
		Color c = gOffScreem.getColor();	//����ԭ������ɫ
		gOffScreem.setColor(Color.GRAY);	
		gOffScreem.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);//��һ����Ļ��С�ķ�������ˢ�£��ֶ���paint֮ǰ�õ�ɫˢ��Ļ
		gOffScreem.setColor(c);		//��ԭ������ɫ
		paint(gOffScreem);		//����paint�������õ�Ļ�󻭱�
		g.drawImage( offscreemImage, 0, 0, null);	//��Ļ��ͼƬ������
	}
	
//-------------------main-----------------------------------------------------
	/**
	 * main����
	 * @param args
	 */
	public static void main(String[] args) {

		new TankClient().launchFrame();	
	}
//------------------end of main-----------------------------------------------
	
	public void launchFrame(){
		

		//���������ļ��е�ֵ
		int initialTankCount = Integer.parseInt(PropertyMgr.getProperty("initialTankCount"));
/*
		  for(int i=0; i<initialTankCount; i++){		//�ڴ��ڽ�����ʱ������10��̹��
		 
			tanks.add(new Tank(90+50*i, 70, false, this));
		}
		
*/
		
		this.setLocation(100, 100);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("Tank War");	//�Ӹ����ڵı�����
		this.setResizable(false);		//���ڲ��ܸı��С
		this.setBackground(Color.GRAY);	//���ñ�����ɫ
		this.setVisible(true);			//��ʾ����
		
		PaintThread pt = new PaintThread();		//�������̣߳������߳��в�ͣ���������repaint
		new Thread(pt).start();		//�߳�����
		
		addKeyListener(new MyKeyMonitor());		//���̰���������������̹��
		
		this.addWindowListener(new WindowAdapter(){		//�������࣬�رմ����¼���Ӧ
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				System.exit(0);
			}
		});
		
		//nc.connect("127.0.0.1", TankServer.TCP_PORT);
	}
	
	
	/**
	 * 
	 * @author Hao
	 * ���̣߳�����ʱ�䣬��ˢ����Ļ
	 *
	 */
	private class PaintThread implements Runnable{	//����repaint���߳���

		public void run() {
			try {
				while(true){
					Thread.sleep(50);		//ÿ��repaint�����50������
					repaint();
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}

	
	/**
	 * 
	 * @author hao
	 * ���̼��
	 *
	 */
	class MyKeyMonitor extends KeyAdapter {		//�ڲ��࣬��Ӧ���̵��������ң�����̹�˵������޸�

/*		public void keyPressed(KeyEvent e) {	//ʵ��keyPressed������������������ʵ��		
			myTank.keyPressed(e);	//ֻ�ðѼ��̰��£�����´��ݸ�Tank�������	
		}*/
		
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);	//����̧����¼�����Tank��
		}
		
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode(); 
			if(key == KeyEvent.VK_F3) {
				dialog.setVisible(true);
			} else {
				myTank.keyPressed(e);
			}
		}

	}
	
	
	
	class ConnDialog extends Dialog {
		Button b = new Button("Connect");
		TextField tfIP = new TextField("127.0.0.1", 12);
		TextField tfPort = new TextField("" + TankServer.TCP_PORT, 4);
		TextField tfMyUDPPort = new TextField("2222", 4);
		public ConnDialog() {
			super(TankClient.this, true);
		
			this.setLayout(new FlowLayout());
			this.add(new Label("IP:"));
			this.add(tfIP);
			this.add(new Label("Port:"));
			this.add(tfPort);
			this.add(new Label("My UDP Port:"));
			this.add(tfMyUDPPort);
			this.add(b);
			this.setLocation(300, 300);
			this.pack();
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			});
			
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String IP = tfIP.getText().trim();
					int port = Integer.parseInt(tfPort.getText().trim());
					int myUDPPort = Integer.parseInt(tfMyUDPPort.getText().trim());
					nc.setUdpPort(myUDPPort);
					nc.connect(IP, port);
					setVisible(false);
				}
				
			});
		}

	}
	

}
