/*
 * 
 * 
 * 
 */

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Missile {
	int x, y;
	private boolean live = true;	//定义了一个live，来确定这个子弹还活着没有
	boolean bGood ;	//让子弹区分好坏，所以不会有友军误伤
	
	private static int ID = 1;
	
	int tankID;
	int id;
	
	Direction dir;		//成员变量是坦克的方向
	
	private static final int XSPEED = 10;	//子弹的速度
	private static final int YSPEED = 10;
	
	public static final int MWIDTH= 10;		//子弹的大小
	public static final int MHEIGHT = 10;
	
	TankClient tc = null;
	private static Random r = new Random();
	
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	
	private static Image[] missileImages = null;
	
	private static Map<String, Image> imgs = new HashMap<String, Image>();
	
	static { 
		missileImages = new Image[]{

			tk.getImage(Missile.class.getClassLoader().getResource("images/missileL.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileLU.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileU.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileRU.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileR.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileRD.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileD.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileLD.gif"))
		};
		
		imgs.put("L", missileImages[0]);
		imgs.put("LU", missileImages[1]);
		imgs.put("U", missileImages[2]);
		imgs.put("RU", missileImages[3]);
		imgs.put("R", missileImages[4]);
		imgs.put("RD", missileImages[5]);
		imgs.put("D", missileImages[6]);
		imgs.put("LD", missileImages[7]);
		
	}
	
	
	
	
	
	public Missile(int tankID, int x, int y, Direction dir) {	//子弹的构造方法
		this.tankID = tankID;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.id = ID++;
	}
	
	//重载了一个构造方法，和tank类一样，接受多一个参数，tc，这样就拿到了TankClient的引用
	public Missile(int tankID, int x, int y, boolean bGood, Direction dir, TankClient tc){
	
		this(tankID, x, y, dir);
		this.bGood = bGood;
		this.tc = tc;
	}

	public void draw(Graphics g){		//画法，在TankClient中调用
		if(!live){						//画之前看看子弹还活着没，如果死了就把它从list中remove了，并且不画，结束
			tc.missiles.remove(this);
			return;
		}
		/*
		Color c = g.getColor();
		
		if(bGood){
			g.setColor(Color.MAGENTA);	//主战坦克打洋红色的子弹
		}else{
			g.setColor(Color.BLACK);	//地方坦克打黑色子弹
		}
		g.fillOval(x, y, MWIDTH, MHEIGHT);	
		g.setColor(c);
		*/
		move();	
		
		
		switch(dir){		
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
	
	private void move(){			//move方法，和坦克相同
		switch(dir){		
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
		}
		//如果子弹出界了，我们就把他从missiles的ArrayList中去掉//改到draw方法中去了
		if(x<0 || y<0 || x > TankClient.GAME_WIDTH || y > TankClient.GAME_HEIGHT){
			live = false;		 //如果子弹出界就死了，去除工作放到draw里面了
		}
	}
	
	public boolean isLive(){	//live是私有的，用两个方法来取出和设置
		return live;
	}
	
	public void setLive(boolean live) {		//live是私有的，用两个方法来取出和设置
		this.live = live;
	}
	
	
	public Rectangle getRect(){		//用到一个辅助类，用来拿到我们子弹的方框，坦克类也需要这个方法
		//return new Rectangle(x, y, imgs.get("dir").getWidth(null), imgs.get("dir").getHeight(null));	
		return new Rectangle(x, y, MWIDTH, MHEIGHT);	
	}
	
	//碰撞检测比较麻烦，调用已有方法，暂时不太清楚
	public boolean hit(Tank t){		//粗暴的判断 子弹的方框 和 坦克的方框 是不是碰撞了，返回布尔值
		if(this.getRect().intersects(t.getRect()) && this.live && t.isLive() && this.bGood != t.isbGood()) {
		
			t.setLife(t.getLife() - 20);
			if(t.getLife() <= 0){
				t.setLive(false);
			}
			
			this.setLive(false);
			Explode e = new Explode(t.getX(), t.getY(), tc);//碰撞了就产生爆炸，我传的是坦克的中心位子，让看起来像是坦克在爆炸
			tc.explodes.add(e);	//然后把爆炸加入list
			return true;
		}
		return false;
	}
	
	public boolean hittanks(List<Tank> tanks){	//在hit方法的基础上，写一个方法，让每一个子弹去打所有的坦克
		for(int i=0; i< tanks.size(); i++){
			if(this.hit(tanks.get(i))){
				if(r.nextInt(100) > 85) {
					Replenishment rp = new Replenishment(x,y,tc);
					tc.rps.add(rp);
				} else if(r.nextInt(100) > 88){
					SpFireBullet spf = new SpFireBullet(x,y,tc);
					tc.spfs.add(spf);
				}	
				return true;	//如果打中任何一个坦克就return true了
			}
		}
		return false;
	}
	
	public boolean hitWall(Wall w){		//子弹打墙上会消失
		if(this.getRect().intersects(w.getRect()) && this.live){
			this.setLive(false);
			return true;
		}
		return false;
	}

		
}



