
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class Tank {	//创建出Tank自己的类，方便之后添加tank的属性和方法，面向对象的思维
	int id;

	private static final int XSPEED = 6;	//把坦克的行驶速度设置为静态变量
	private static final int YSPEED = 6;
	
	private static final int TWIDTH= 30;	//把坦克大小设置为常量
	private static final int THEIGHT = 30;
	
	//enum Direction {L, LU, U, RU, R, RD, D, LD, STOP};	//用枚举类型定义了8个方向
	Direction dir = Direction.STOP;		//初始化一个Tank的属性：方向。初始化为STOP状态
	private Direction ptDir = Direction.D;		//炮筒的方向


	private boolean bL=false;	//设置了4个临时变量，布尔类型，区别按键是否按下
	private boolean bU=false;
	private boolean bR=false;
	private boolean bD=false;
	
	int x, y;	//坦克出生的时候的坐标
	private int oldX = x, oldY = y;
	boolean bGood;	//布尔量，用来区分敌我
	
	

	
	public int score = 0;
	public int hScore = 0;

	private boolean live = true;	//用一个布尔值来确定这个坦克是否还活着
	private int life = 100;

	private int spFireNum = 10;

	TankClient tc = null;	//***生命TankClient类的引用，才能使用TankClient类的成员，m子弹
	
	BloodBar bb = new BloodBar();	//血条
	
	private static Random r = new Random();	//产生一个随机数
	public int step = r.nextInt(12) + 3;	//随机步数
	public int step2 = r.nextInt(12) + 3;	//随机步数
	

	private static Toolkit tk = Toolkit.getDefaultToolkit();	//拿到操作系统的工具箱
	
	private static Image[] tankImages = null;	//建立一个空的，底下用static的方法从硬盘拿图片，和explode类的对比
	
	private static Map<String, Image> imgs = new HashMap<String, Image>();	//转一下，免得错
	
	static { 
		tankImages = new Image[]{

			tk.getImage(Tank.class.getClassLoader().getResource("images/tanksL.gif")),
			//工具拿到图片，拿到类装载器，拿到源，拿到图片
			tk.getImage(Tank.class.getClassLoader().getResource("images/tanksLU.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tanksU.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tanksRU.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tanksR.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tanksRD.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tanksD.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tanksLD.gif")),
			
			tk.getImage(Tank.class.getClassLoader().getResource("images/myTankL.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/myTankLU.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/myTankU.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/myTankRU.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/myTankR.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/myTankRD.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/myTankD.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/myTankLD.gif"))
		
		};
		
		imgs.put("L", tankImages[0]);
		imgs.put("LU", tankImages[1]);
		imgs.put("U", tankImages[2]);
		imgs.put("RU", tankImages[3]);
		imgs.put("R", tankImages[4]);
		imgs.put("RD", tankImages[5]);
		imgs.put("D", tankImages[6]);
		imgs.put("LD", tankImages[7]);
		
		imgs.put("mL", tankImages[8]);
		imgs.put("mLU", tankImages[9]);
		imgs.put("mU", tankImages[10]);
		imgs.put("mRU", tankImages[11]);
		imgs.put("mR", tankImages[12]);
		imgs.put("mRD", tankImages[13]);
		imgs.put("mD", tankImages[14]);
		imgs.put("mLD", tankImages[15]);
		
		
	}
	
	
	public Tank(int x, int y, boolean bGood) {	//构造方法
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.bGood = bGood;
		
	}
	

	
	public Tank(int x, int y, boolean bGood,  Direction dir, Direction ptDir,  TankClient tc) {	//重载构造方法，接收TankClient的引用
		this(x, y, bGood);		//调用父类构造方法
		this.ptDir = ptDir;
		this.tc = tc;	//初始化tc
	}
	
	public void draw(Graphics g){	//方法，坦克画自己
		if(!live){		//在画之前看看坦克还活着吗？死了就不画了，估计之后的敌人坦克的ArrayList中删除坦克也要在这里操作
			tc.tanks.remove(this);
			//this.addPoint(10);
			return;
		}
		
		Color c = g.getColor();
		/*if(bGood){
			g.setColor(Color.PINK);	//画出自己的坦克，红颜色代表
		}else{
			g.setColor(Color.BLUE);
		}*/
		g.setColor(Color.WHITE);
		g.drawString(id + "P", x + 5, y - 15);
		bb.draw(g);		//主战坦克画出血条
		//g.fillOval(x, y, TWIDTH, THEIGHT);	//用实心圆代表坦克
		g.setColor(c);
		

		
		move();			//在画图中调用了move方法，让坦克自动行走，我们只控制方向
		
		//之下画炮筒
		if(bGood){
			switch(ptDir){		
			case L:
				g.drawImage(imgs.get("mL"), x, y, null);
				break;
			case LU:
				g.drawImage(imgs.get("mLU"), x, y, null);
				break;
			case U:
				g.drawImage(imgs.get("mU"), x, y, null);
				break;
			case RU:
				g.drawImage(imgs.get("mRU"), x, y, null);
				break;
			case R:
				g.drawImage(imgs.get("mR"), x, y, null);
				break;
			case RD:
				g.drawImage(imgs.get("mRD"), x, y, null);
				break;
			case D:
				g.drawImage(imgs.get("mD"), x, y, null);
				break;
			case LD:
				g.drawImage(imgs.get("mLD"), x, y, null);
				break;
			
			}
		}else{
			switch(ptDir){		
			case L:
				g.drawImage(imgs.get("L"), x, y, null);
				break;
			case LU:
				g.drawImage(imgs.get("LU"), x, y, null);
				break;
			case U:
				g.drawImage(imgs.get("U"), x, y, null);
				break;
			case RU:
				g.drawImage(imgs.get("RU"), x, y, null);
				break;
			case R:
				g.drawImage(imgs.get("R"), x, y, null);
				break;
			case RD:
				g.drawImage(imgs.get("RD"), x, y, null);
				break;
			case D:
				g.drawImage(imgs.get("D"), x, y, null);
				break;
			case LD:
				g.drawImage(imgs.get("LD"), x, y, null);
				break;
			}
		}
	}
	
	public void keyPressed(KeyEvent e){	//方法，坦克自己动
		int keyCode = e.getKeyCode();		//获得按键的虚拟码，等下和key event里面的比
		
		switch (keyCode) {		//亲测，和if语句没有什么区别
		case KeyEvent.VK_F2 :
			if(!this.live){
				x = 900;
				y = 700;
				this.live = true;
				this.life = 100;
				this.spFireNum = 10;

				tc.score = 0;
			}
			break;
		case KeyEvent.VK_F1:
			for(int i=0; i<8; i++){
				Tank tk = new Tank(r.nextInt(1000), r.nextInt(800), false, Direction.D, Direction.D, tc);
				if(!tk.crashTanks(tc.tanks) && !tk.crashWall(tc.w1) && !tk.crashWall(tc.w2)){

					tc.tanks.add(tk);

				}
				TankNewMsg msg = new TankNewMsg(tk);
			
				tc.nc.send(msg);
			}
			break;
		case KeyEvent.VK_UP:
			bU = true;	
			break;
		case KeyEvent.VK_DOWN:
			bD = true;
			break;
		case KeyEvent.VK_LEFT:
			bL = true;
			break;
		case KeyEvent.VK_RIGHT:
			bR = true;
			break;
		
		}				
		lacateDirection();	//调用设置坦克方向的方法
	}
	
	public void keyReleased(KeyEvent e){	//检测按键松手，松手的时候把他设置为false
		int keyCode = e.getKeyCode();		//获得按键的虚拟码，等下和key event里面的比

		switch (keyCode) {
		case KeyEvent.VK_UP:
			bU = false;	
			break;
		case KeyEvent.VK_DOWN:
			bD = false;
			break;
		case KeyEvent.VK_LEFT:
			bL = false;
			break;
		case KeyEvent.VK_RIGHT:
			bR = false;
			break;
		case KeyEvent.VK_F:		//*****当被按下F键时，触发事件
			fire();		//fire（）方法返回值是一个missile，正好传给tc.m
			break;
		case KeyEvent.VK_R:
			superFire();
			break;
		}
		
		lacateDirection();	//调用设置坦克方向的方法
	}
	
	private void lacateDirection(){			//根据按键产生的布尔值设置坦克的方向
		
		Direction oldDir = this.dir;
		
		if(bL && !bU && !bR && !bD) dir = Direction.L;		
		else if(bL && bU && !bR && !bD) dir = Direction.LU;		
		else if(!bL && bU && !bR && !bD) dir = Direction.U;
		else if(!bL && bU && bR && !bD) dir = Direction.RU;
		else if(!bL && !bU && bR && !bD) dir = Direction.R;
		else if(!bL && !bU && bR && bD) dir = Direction.RD;
		else if(!bL && !bU && !bR && bD) dir = Direction.D;
		else if(bL && !bU && !bR && bD) dir = Direction.LD;
		else if(!bL && !bU && !bR && !bD) dir = Direction.STOP;
		
		if(dir != oldDir) {
			TankMoveMsg msg = new TankMoveMsg(id, x, y, dir, ptDir);
			tc.nc.send(msg);
		}
		
	}
	
	private void move(){		//让坦克移动的方法，move
		oldX = x;
		oldY = y;
		switch(dir){		//根据坦克的方向来移动坦克
		case L:
			x -= YSPEED;
			break;
		case LU:
			y -= XSPEED;
			x -= YSPEED;
			break;
		case U:
			y -= XSPEED;
			break;
		case RU:
			y -= XSPEED;
			x += YSPEED;
			break;
		case R:
			x += YSPEED;
			break;
		case RD:
			y += XSPEED;
			x += YSPEED;
			break;
		case D:
			y += XSPEED;
			break;
		case LD:
			y += XSPEED;
			x -= YSPEED;
			break;
		case STOP:
			break;
		}
		/*
		if(x <= 3) x=3;		//给坦克定了一个边界，跑不出去啦，边角也解决了。
		if(y <= 24) y=24;
		if(x >= 767) x=767;
		if(y >= 567) y=567;  
		*/
		//重新写一个坦克的边界，比较官方的写法，不过比上面量出来的方法有点不精确，会被边框影响。。。。
		if(x<0) x=0;
		if(y<24) y=24;
		if(x + Tank.TWIDTH > TankClient.GAME_WIDTH) x = TankClient.GAME_WIDTH - Tank.TWIDTH;
		if(y + Tank.TWIDTH > TankClient.GAME_HEIGHT) y = TankClient.GAME_HEIGHT - Tank.THEIGHT;
		
		if(this.dir != Direction.STOP){
			this.ptDir = this.dir;
		}
/*		
		if(!bGood){
			Direction[] dirs = Direction.values();	
			if(step == 0){	//step是一个随机数，走完这么多步之后再换一个方向
				step = r.nextInt(12) + 3;
				int rn = r.nextInt(dirs.length);
				dir = dirs[rn];
			}
			step --;
		}
		
		if(!bGood){	//step2是一个随机数，随机开火
			if(step2 == 0){	
				step2 = r.nextInt(12) + 3;
				fire();
			}
			step2 --;
		}
		
		*/
		
	}
	
	//“坦克打出一发子弹”，所以，“打”是坦克的方法，打完了，返回来一颗“子弹”。
	public Missile fire(){			//添加一个坦克的方法：坦克开火
		if(!live) return null;
		//int x = this.x + Tank.TWIDTH/2 - Missile.MWIDTH/2;	//让子弹从坦克中间发出
		//int y = this.y + Tank.THEIGHT/2 - Missile.MHEIGHT/2;
		int x = this.x + 17;	//让子弹从坦克中间发出
		int y = this.y + 17;
		Missile f= new Missile(id, x, y, bGood,ptDir,this.tc);	//new出一个子弹对象
		tc.missiles.add(f);
		
		MissileNewMsg msg = new MissileNewMsg(f);
		tc.nc.send(msg);
		
		return f;
	}
	
	public Missile fire(Direction dir){			//添加一个坦克的方法：坦克开火
		if(!live) return null;
		int x = this.x + Tank.TWIDTH/2 - Missile.MWIDTH/2;	//让子弹从坦克中间发出
		int y = this.y + Tank.THEIGHT/2 - Missile.MHEIGHT/2;
		//int x = this.x + 30;	//让子弹从坦克中间发出
		//int y = this.y + 30;
		Missile f= new Missile(id, x, y, bGood,dir,this.tc);	//new出一个子弹对象
		tc.missiles.add(f);
		
		return f;
	}
	
	public void superFire(){
		if(spFireNum > 0){
			Direction[] dirs = Direction.values();
			for(int i=0; i<8; i++){
				fire(dirs[i]);
			}
			this.spFireNum --;
		}
	}
	
	public Rectangle getRect(){			//用到一个辅助类，用来拿到我们坦克的方框，子弹类也需要这个方法
		return new Rectangle(x, y, tankImages[0].getWidth(null), tankImages[0].getHeight(null));
		
	}

	private void stay(){
		x = oldX;
		y = oldY;
	}
	
	public boolean crashWall(Wall w){		//添加坦克撞墙上的处理方法
		if(this.getRect().intersects(w.getRect()) && this.live){
			this.stay();
			return true;
		}
		return false;
	}
	
	public boolean crashTanks(java.util.List<Tank> tanks){	//坦克不能重叠处理
		for(int i=0; i<tanks.size(); i++){
			Tank t = tanks.get(i);
			if(this != t){
				if(this.live && t.isLive() && this.getRect().intersects(t.getRect()) ){
					stay();
					return true;
				}
			}
		}
		return false;
	}

	public boolean eatB(Replenishment r){		//吃一个血块
		if(this.isbGood()){
			if(this.isLive() && r.isLive() && this.getRect().intersects(r.getRect())){
				r.setLive(false);
				if(this.life == 100){
					tc.score += 50;
					if(tc.score > tc.hScore){
						tc.hScore = tc.score;
					}
				}
				if(this.life == 80){
					this.setLife(this.life + 20);
				} else if(this.life < 100){
					this.setLife(this.life + 40);
				}
				return true;
			}
			
		}
		return false;
	}
	
	public boolean eatBB(List<Replenishment> rps){	//吃一个list的血块

			for(int i=0; i<rps.size(); i++){
				Replenishment r = rps.get(i);	//调用上一个方法
				this.eatB(r);
				}

		return false;
	}
	
	
	
	public boolean eatSP(SpFireBullet sp){	
		if(this.isbGood()){
			if(this.isLive() && sp.isLive() && this.getRect().intersects(sp.getRect())){
				sp.setLive(false);
				this.spFireNum += 10;
				return true;
			}
		}
		return false;
	}
	
	public boolean eatSPF(List<SpFireBullet> spfs){

			for(int i=0; i<spfs.size(); i++){
				SpFireBullet sp = spfs.get(i);	
				this.eatSP(sp);
				}

		return false;
	}
	
	
	
	
	private class BloodBar{				//血条，用内部类
		public void draw(Graphics g){	//有自己的draw方法
			Color c = g.getColor();
			g.setColor(Color.DARK_GRAY);
			g.drawRect(x, y - 11, 36, 8);
			if(life == 100){g.setColor(Color.GREEN);}
			if(life == 80){g.setColor(Color.CYAN);}
			if(life == 60){g.setColor(Color.YELLOW);}
			if(life == 40){g.setColor(Color.ORANGE);}
			if(life == 20){g.setColor(Color.RED);}
			g.fillRect(x+1, y - 10, 36*life/100-1, 7);
			g.setColor(c);
			
		}
		
	}
	
	/**
	 * 一下是为私有成员变量建立的函数********************************************
	 * @return
	 */
	
	public Direction getPtDir() {
		return ptDir;
	}



	public void setPtDir(Direction ptDir) {
		this.ptDir = ptDir;
	}
	
	public boolean isbGood() {
		return bGood;
	}
	
	public boolean isLive() {	//live是私有的，用两个方法来取出和设置
		return live;
	}

	public void setLive(boolean live) {		//live是私有的，用两个方法来取出和设置
		this.live = live;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getSpFireNum() {
		return spFireNum;
	}

	public void setSpFireNum(int spFireNum) {
		this.spFireNum = spFireNum;
	}
}














