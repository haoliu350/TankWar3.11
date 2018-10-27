/*
坦克大战
版本 3.11

功能：加入爆炸，把坦克的炮筒方向在TankNewMsg 和 TankMoveMsg中都发送，都同步一下
	修正IP的小bug
	暂时网络版写到这里。



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
 *坦克大战 版本
 *坦克大战的主窗口类
 */
public class TankClient extends Frame{	//从Frame类继承
	/*
	 * 游戏窗口的高度和宽度
	 */
	public static final int GAME_WIDTH = 1000;	//把屏幕大小定义为常量，方便改动
	public static final int GAME_HEIGHT=800;
	
	private static Random r = new Random();

	public int score = 0;
	public int hScore = 0;
	
	NetClient nc = new NetClient(this);
	
	ConnDialog dialog = new ConnDialog();
	
	
	
	Tank myTank = new Tank(900, 700, true, Direction.D, Direction.D, this);		//不在需要x和y了，创建对象的时候把他们传递进去就可以了
	//Wall w1 = new Wall(300,600, 5, 1, "W");
	//Wall w2 = new Wall(700,150, 1, 3, "S");
	//Wall w3 = new Wall(100, 200, 2, 4, "G");
	
	Wall w1 = new Wall(300,600, 1, 1, "W");
	Wall w2 = new Wall(700,150, 1, 1, "S");
	Wall w3 = new Wall(100, 200, 1, 1, "G");
	/*
	 * 除了主战坦克之外的4个容器
	 */
	List<Explode> explodes = new ArrayList<Explode>();
	List<Missile> missiles = new ArrayList<Missile>();	//用一个ArrayList容器装子弹，就可以多发了
	List<Tank>	tanks = new ArrayList<Tank>();
	List<Replenishment> rps = new ArrayList<Replenishment>();
	List<SpFireBullet> spfs = new ArrayList<SpFireBullet>();


	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image bGImage =  tk.getImage(TankClient.class.getClassLoader().getResource("images/background.gif"));

	
	Image offscreemImage = null;	//定义一张屏幕后面的图片，设为空
	/*
	 * (non-Javadoc)
	 * @see java.awt.Window#paint(java.awt.Graphics)
	 * 画窗口
	 */
	public void paint(Graphics g) {		//paint方法自动被调用
		
		Color c = g.getColor();
		g.drawImage(bGImage, 0, 0, null);//加了张背景图片
		g.setColor(Color.WHITE);
		g.drawString("EnemyTanks count: " + tanks.size(), 10, 40);
		g.drawString("MyTank Life:" + myTank.getLife(), 900, 765);
		g.drawString("Highest Score: " + this.hScore, 10, 50);
		g.drawString("Score: " + this.score, 10, 65);
		g.drawString("Super Fire: " + myTank.getSpFireNum() ,900, 780);
		g.setColor(c);
		
		w1.draw(g);
		w2.draw(g);
		
		//坦克少于等3辆的时候自动加入敌方坦克
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
		
		for(int i = 0; i < missiles.size(); i++){	//从容器里遍历拿出来，全画出来
			Missile m = missiles.get(i);
			m.hittanks(tanks);//调用新的打坦克的方法
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
		
		for(int i=0; i < explodes.size(); i++){	//可能存在许多爆炸，每一个都拿出来画出来
			Explode e = explodes.get(i);
			e.draw(g);
		}
		
		for(int i=0; i <tanks.size(); i++){	//画出所有坦克
			Tank tk = tanks.get(i);
			tk.crashTanks(tanks);
			
			tk.crashWall(w1);
			tk.crashWall(w2);
			tk.draw(g);
		}
		
		for(int i=0; i<rps.size(); i++){	//画出血块
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
		

		
		myTank.draw(g);//画作为坦克本身的一个方法，只用把画笔传过去就行了
		myTank.crashWall(w2);
		myTank.eatBB(rps);
		myTank.eatSPF(spfs);
		
		w3.draw(g);
	}
	
	/*
	 * 双缓冲的update方法(non-Javadoc)
	 * @see java.awt.Container#update(java.awt.Graphics)
	 */
	public void update(Graphics g) {	//双缓冲机制，重写update方法
		if(offscreemImage == null){
			offscreemImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);	//若没有创建，则创建出图片
		}
		Graphics gOffScreem = offscreemImage.getGraphics();		//拿到画笔
		Color c = gOffScreem.getColor();	//保存原画笔颜色
		gOffScreem.setColor(Color.GRAY);	
		gOffScreem.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);//画一个屏幕大小的方框，用来刷新，手动在paint之前用底色刷屏幕
		gOffScreem.setColor(c);		//还原画笔颜色
		paint(gOffScreem);		//调用paint方法，用的幕后画笔
		g.drawImage( offscreemImage, 0, 0, null);	//把幕后图片画出来
	}
	
//-------------------main-----------------------------------------------------
	/**
	 * main方法
	 * @param args
	 */
	public static void main(String[] args) {

		new TankClient().launchFrame();	
	}
//------------------end of main-----------------------------------------------
	
	public void launchFrame(){
		

		//调用配置文件中的值
		int initialTankCount = Integer.parseInt(PropertyMgr.getProperty("initialTankCount"));
/*
		  for(int i=0; i<initialTankCount; i++){		//在窗口建立的时候就添加10辆坦克
		 
			tanks.add(new Tank(90+50*i, 70, false, this));
		}
		
*/
		
		this.setLocation(100, 100);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("Tank War");	//加个窗口的标题名
		this.setResizable(false);		//窗口不能改变大小
		this.setBackground(Color.GRAY);	//设置背景颜色
		this.setVisible(true);			//显示窗口
		
		PaintThread pt = new PaintThread();		//启动新线程，在新线程中不停对这个窗口repaint
		new Thread(pt).start();		//线程启动
		
		addKeyListener(new MyKeyMonitor());		//键盘按键监听，来控制坦克
		
		this.addWindowListener(new WindowAdapter(){		//用匿名类，关闭窗口事件响应
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
	 * 新线程，控制时间，和刷新屏幕
	 *
	 */
	private class PaintThread implements Runnable{	//用来repaint的线程类

		public void run() {
			try {
				while(true){
					Thread.sleep(50);		//每次repaint都相隔50个毫秒
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
	 * 键盘检测
	 *
	 */
	class MyKeyMonitor extends KeyAdapter {		//内部类，响应键盘的上下左右，并对坦克的坐标修改

/*		public void keyPressed(KeyEvent e) {	//实现keyPressed方法，其他方法不用实现		
			myTank.keyPressed(e);	//只用把键盘按下，这件事传递给Tank类就行了	
		}*/
		
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);	//按键抬起的事件传给Tank类
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
