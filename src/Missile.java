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
	private boolean live = true;	//������һ��live����ȷ������ӵ�������û��
	boolean bGood ;	//���ӵ����ֺû������Բ������Ѿ�����
	
	private static int ID = 1;
	
	int tankID;
	int id;
	
	Direction dir;		//��Ա������̹�˵ķ���
	
	private static final int XSPEED = 10;	//�ӵ����ٶ�
	private static final int YSPEED = 10;
	
	public static final int MWIDTH= 10;		//�ӵ��Ĵ�С
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
	
	
	
	
	
	public Missile(int tankID, int x, int y, Direction dir) {	//�ӵ��Ĺ��췽��
		this.tankID = tankID;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.id = ID++;
	}
	
	//������һ�����췽������tank��һ�������ܶ�һ��������tc���������õ���TankClient������
	public Missile(int tankID, int x, int y, boolean bGood, Direction dir, TankClient tc){
	
		this(tankID, x, y, dir);
		this.bGood = bGood;
		this.tc = tc;
	}

	public void draw(Graphics g){		//��������TankClient�е���
		if(!live){						//��֮ǰ�����ӵ�������û��������˾Ͱ�����list��remove�ˣ����Ҳ���������
			tc.missiles.remove(this);
			return;
		}
		/*
		Color c = g.getColor();
		
		if(bGood){
			g.setColor(Color.MAGENTA);	//��ս̹�˴����ɫ���ӵ�
		}else{
			g.setColor(Color.BLACK);	//�ط�̹�˴��ɫ�ӵ�
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
	
	private void move(){			//move��������̹����ͬ
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
		//����ӵ������ˣ����ǾͰ�����missiles��ArrayList��ȥ��//�ĵ�draw������ȥ��
		if(x<0 || y<0 || x > TankClient.GAME_WIDTH || y > TankClient.GAME_HEIGHT){
			live = false;		 //����ӵ���������ˣ�ȥ�������ŵ�draw������
		}
	}
	
	public boolean isLive(){	//live��˽�еģ�������������ȡ��������
		return live;
	}
	
	public void setLive(boolean live) {		//live��˽�еģ�������������ȡ��������
		this.live = live;
	}
	
	
	public Rectangle getRect(){		//�õ�һ�������࣬�����õ������ӵ��ķ���̹����Ҳ��Ҫ�������
		//return new Rectangle(x, y, imgs.get("dir").getWidth(null), imgs.get("dir").getHeight(null));	
		return new Rectangle(x, y, MWIDTH, MHEIGHT);	
	}
	
	//��ײ���Ƚ��鷳���������з�������ʱ��̫���
	public boolean hit(Tank t){		//�ֱ����ж� �ӵ��ķ��� �� ̹�˵ķ��� �ǲ�����ײ�ˣ����ز���ֵ
		if(this.getRect().intersects(t.getRect()) && this.live && t.isLive() && this.bGood != t.isbGood()) {
		
			t.setLife(t.getLife() - 20);
			if(t.getLife() <= 0){
				t.setLive(false);
			}
			
			this.setLive(false);
			Explode e = new Explode(t.getX(), t.getY(), tc);//��ײ�˾Ͳ�����ը���Ҵ�����̹�˵�����λ�ӣ��ÿ���������̹���ڱ�ը
			tc.explodes.add(e);	//Ȼ��ѱ�ը����list
			return true;
		}
		return false;
	}
	
	public boolean hittanks(List<Tank> tanks){	//��hit�����Ļ����ϣ�дһ����������ÿһ���ӵ�ȥ�����е�̹��
		for(int i=0; i< tanks.size(); i++){
			if(this.hit(tanks.get(i))){
				if(r.nextInt(100) > 85) {
					Replenishment rp = new Replenishment(x,y,tc);
					tc.rps.add(rp);
				} else if(r.nextInt(100) > 88){
					SpFireBullet spf = new SpFireBullet(x,y,tc);
					tc.spfs.add(spf);
				}	
				return true;	//��������κ�һ��̹�˾�return true��
			}
		}
		return false;
	}
	
	public boolean hitWall(Wall w){		//�ӵ���ǽ�ϻ���ʧ
		if(this.getRect().intersects(w.getRect()) && this.live){
			this.setLive(false);
			return true;
		}
		return false;
	}

		
}



