
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class Tank {	//������Tank�Լ����࣬����֮�����tank�����Ժͷ�������������˼ά
	int id;

	private static final int XSPEED = 6;	//��̹�˵���ʻ�ٶ�����Ϊ��̬����
	private static final int YSPEED = 6;
	
	private static final int TWIDTH= 30;	//��̹�˴�С����Ϊ����
	private static final int THEIGHT = 30;
	
	//enum Direction {L, LU, U, RU, R, RD, D, LD, STOP};	//��ö�����Ͷ�����8������
	Direction dir = Direction.STOP;		//��ʼ��һ��Tank�����ԣ����򡣳�ʼ��ΪSTOP״̬
	private Direction ptDir = Direction.D;		//��Ͳ�ķ���


	private boolean bL=false;	//������4����ʱ�������������ͣ����𰴼��Ƿ���
	private boolean bU=false;
	private boolean bR=false;
	private boolean bD=false;
	
	int x, y;	//̹�˳�����ʱ�������
	private int oldX = x, oldY = y;
	boolean bGood;	//���������������ֵ���
	
	

	
	public int score = 0;
	public int hScore = 0;

	private boolean live = true;	//��һ������ֵ��ȷ�����̹���Ƿ񻹻���
	private int life = 100;

	private int spFireNum = 10;

	TankClient tc = null;	//***����TankClient������ã�����ʹ��TankClient��ĳ�Ա��m�ӵ�
	
	BloodBar bb = new BloodBar();	//Ѫ��
	
	private static Random r = new Random();	//����һ�������
	public int step = r.nextInt(12) + 3;	//�������
	public int step2 = r.nextInt(12) + 3;	//�������
	

	private static Toolkit tk = Toolkit.getDefaultToolkit();	//�õ�����ϵͳ�Ĺ�����
	
	private static Image[] tankImages = null;	//����һ���յģ�������static�ķ�����Ӳ����ͼƬ����explode��ĶԱ�
	
	private static Map<String, Image> imgs = new HashMap<String, Image>();	//תһ�£���ô�
	
	static { 
		tankImages = new Image[]{

			tk.getImage(Tank.class.getClassLoader().getResource("images/tanksL.gif")),
			//�����õ�ͼƬ���õ���װ�������õ�Դ���õ�ͼƬ
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
	
	
	public Tank(int x, int y, boolean bGood) {	//���췽��
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.bGood = bGood;
		
	}
	

	
	public Tank(int x, int y, boolean bGood,  Direction dir, Direction ptDir,  TankClient tc) {	//���ع��췽��������TankClient������
		this(x, y, bGood);		//���ø��๹�췽��
		this.ptDir = ptDir;
		this.tc = tc;	//��ʼ��tc
	}
	
	public void draw(Graphics g){	//������̹�˻��Լ�
		if(!live){		//�ڻ�֮ǰ����̹�˻����������˾Ͳ����ˣ�����֮��ĵ���̹�˵�ArrayList��ɾ��̹��ҲҪ���������
			tc.tanks.remove(this);
			//this.addPoint(10);
			return;
		}
		
		Color c = g.getColor();
		/*if(bGood){
			g.setColor(Color.PINK);	//�����Լ���̹�ˣ�����ɫ����
		}else{
			g.setColor(Color.BLUE);
		}*/
		g.setColor(Color.WHITE);
		g.drawString(id + "P", x + 5, y - 15);
		bb.draw(g);		//��ս̹�˻���Ѫ��
		//g.fillOval(x, y, TWIDTH, THEIGHT);	//��ʵ��Բ����̹��
		g.setColor(c);
		

		
		move();			//�ڻ�ͼ�е�����move��������̹���Զ����ߣ�����ֻ���Ʒ���
		
		//֮�»���Ͳ
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
	
	public void keyPressed(KeyEvent e){	//������̹���Լ���
		int keyCode = e.getKeyCode();		//��ð����������룬���º�key event����ı�
		
		switch (keyCode) {		//�ײ⣬��if���û��ʲô����
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
		lacateDirection();	//��������̹�˷���ķ���
	}
	
	public void keyReleased(KeyEvent e){	//��ⰴ�����֣����ֵ�ʱ���������Ϊfalse
		int keyCode = e.getKeyCode();		//��ð����������룬���º�key event����ı�

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
		case KeyEvent.VK_F:		//*****��������F��ʱ�������¼�
			fire();		//fire������������ֵ��һ��missile�����ô���tc.m
			break;
		case KeyEvent.VK_R:
			superFire();
			break;
		}
		
		lacateDirection();	//��������̹�˷���ķ���
	}
	
	private void lacateDirection(){			//���ݰ��������Ĳ���ֵ����̹�˵ķ���
		
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
	
	private void move(){		//��̹���ƶ��ķ�����move
		oldX = x;
		oldY = y;
		switch(dir){		//����̹�˵ķ������ƶ�̹��
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
		if(x <= 3) x=3;		//��̹�˶���һ���߽磬�ܲ���ȥ�����߽�Ҳ����ˡ�
		if(y <= 24) y=24;
		if(x >= 767) x=767;
		if(y >= 567) y=567;  
		*/
		//����дһ��̹�˵ı߽磬�ȽϹٷ���д���������������������ķ����е㲻��ȷ���ᱻ�߿�Ӱ�졣������
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
			if(step == 0){	//step��һ���������������ô�ಽ֮���ٻ�һ������
				step = r.nextInt(12) + 3;
				int rn = r.nextInt(dirs.length);
				dir = dirs[rn];
			}
			step --;
		}
		
		if(!bGood){	//step2��һ����������������
			if(step2 == 0){	
				step2 = r.nextInt(12) + 3;
				fire();
			}
			step2 --;
		}
		
		*/
		
	}
	
	//��̹�˴��һ���ӵ��������ԣ�������̹�˵ķ����������ˣ�������һ�š��ӵ�����
	public Missile fire(){			//���һ��̹�˵ķ�����̹�˿���
		if(!live) return null;
		//int x = this.x + Tank.TWIDTH/2 - Missile.MWIDTH/2;	//���ӵ���̹���м䷢��
		//int y = this.y + Tank.THEIGHT/2 - Missile.MHEIGHT/2;
		int x = this.x + 17;	//���ӵ���̹���м䷢��
		int y = this.y + 17;
		Missile f= new Missile(id, x, y, bGood,ptDir,this.tc);	//new��һ���ӵ�����
		tc.missiles.add(f);
		
		MissileNewMsg msg = new MissileNewMsg(f);
		tc.nc.send(msg);
		
		return f;
	}
	
	public Missile fire(Direction dir){			//���һ��̹�˵ķ�����̹�˿���
		if(!live) return null;
		int x = this.x + Tank.TWIDTH/2 - Missile.MWIDTH/2;	//���ӵ���̹���м䷢��
		int y = this.y + Tank.THEIGHT/2 - Missile.MHEIGHT/2;
		//int x = this.x + 30;	//���ӵ���̹���м䷢��
		//int y = this.y + 30;
		Missile f= new Missile(id, x, y, bGood,dir,this.tc);	//new��һ���ӵ�����
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
	
	public Rectangle getRect(){			//�õ�һ�������࣬�����õ�����̹�˵ķ����ӵ���Ҳ��Ҫ�������
		return new Rectangle(x, y, tankImages[0].getWidth(null), tankImages[0].getHeight(null));
		
	}

	private void stay(){
		x = oldX;
		y = oldY;
	}
	
	public boolean crashWall(Wall w){		//���̹��ײǽ�ϵĴ�����
		if(this.getRect().intersects(w.getRect()) && this.live){
			this.stay();
			return true;
		}
		return false;
	}
	
	public boolean crashTanks(java.util.List<Tank> tanks){	//̹�˲����ص�����
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

	public boolean eatB(Replenishment r){		//��һ��Ѫ��
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
	
	public boolean eatBB(List<Replenishment> rps){	//��һ��list��Ѫ��

			for(int i=0; i<rps.size(); i++){
				Replenishment r = rps.get(i);	//������һ������
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
	
	
	
	
	private class BloodBar{				//Ѫ�������ڲ���
		public void draw(Graphics g){	//���Լ���draw����
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
	 * һ����Ϊ˽�г�Ա���������ĺ���********************************************
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
	
	public boolean isLive() {	//live��˽�еģ�������������ȡ��������
		return live;
	}

	public void setLive(boolean live) {		//live��˽�еģ�������������ȡ��������
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














