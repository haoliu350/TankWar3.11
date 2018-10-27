
import java.awt.*;
import java.util.Random;

/**
 * 
 * @author Hao LIU
 * 加血块的类
 *
 */

public class SpFireBullet {
	int x, y, w = 20, h = 30;
	int oldX = x; int oldY = y;
	TankClient tc;
	Tank tk;
	private boolean live = true;
	
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	private static final int XSPEED = 2;	
	private static final int YSPEED = 2;
	//enum Direction {L, LU, U, RU, R, RD, D, LD, STOP};	
	private Direction dir = Direction.STOP;	
	int step = r.nextInt(12) + 3;
	private static Random r = new Random();	
	
	
	
	public SpFireBullet(int x, int y) {		
		this.x = x;
		this.y = y;

	}
	
	public SpFireBullet(int x, int y, TankClient tc) {		
		this.x = x;
		this.y = y;
		this.tc = tc;
	}

	public void draw(Graphics g){
		if(!live) {		
			tc.spfs.remove(this);
			return;
		}
		Color c = g.getColor();
		
		g.setColor(Color.WHITE);
		g.drawRect(x, y, w, h);
		g.setColor(Color.CYAN);
		g.drawRect(x-1, y-1, w+2, h+2);
		g.fillRect(x, y + h/4 -2, w, 5);
		g.fillRect(x, y + h/2 -2, w, 5);
		g.fillRect(x, y + 3*h/4 -2, w, 5);
		
		g.setColor(c);
		move();
	}
	
	private void move(){		
		oldX = x;
		oldY = y;
		
		Direction[] dirs = Direction.values();	
		if(step == 0){	
			step = r.nextInt(12)+1;
			int rn = r.nextInt(dirs.length);
			dir = dirs[rn];
		}
		step --;

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
		case STOP:
			break;
		}

		if(x <= 3) x=3;		
		if(y <= 24) y=24;
		if(x >= 767) x=767;
		if(y >= 567) y=567;  
	}

	public Rectangle getRect(){			//碰撞问题
		return new Rectangle(x, y, w, h);	
	}
	
	private void stay(){
		x = oldX;
		y = oldY;
	}
	
	public boolean BulletCrashWall(Wall w){		
		if(this.getRect().intersects(w.getRect()) && this.live){
			this.stay();
			return true;
		}
		return false;
	}
	
}
