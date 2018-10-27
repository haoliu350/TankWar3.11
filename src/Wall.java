import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class Wall {

	int x, y;
	int width , height;
	//Color c;
	String kind;
	TankClient tc;		//还是把TankClient的引用拿到
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	
	private static Image[] wallImages = null;
	
	private static Map<String, Image> imgs = new HashMap<String, Image>();
	
	static { 
		wallImages = new Image[]{

			tk.getImage(Missile.class.getClassLoader().getResource("images/grass.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/wall.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/steel.gif"))
		};
		
		imgs.put("G", wallImages[0]);
		imgs.put("W", wallImages[1]);
		imgs.put("S", wallImages[2]);
	}
	
	public Wall(int x, int y, int width, int height, String kind) {	
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.kind = kind;
		
		
	}
	
	public Wall(int x, int y, int width, int height, String kind, TankClient tc) {
		this(x,y, width, height, kind);
		this.tc = tc;
	}
	
	

	
	public void draw(Graphics g){	//画墙的方法
		/*
		Color c = g.getColor();
		g.setColor(this.c);
		g.fillRect(x, y, width, height);
		g.setColor(c);
		
		

			for(int i=0; i <= width; i++ ){
				for(int j=0; i<= height; j++){
					g.drawImage(imgs.get(kind), x + 40*i, y + 40*j, null);
					
				}
			}
*/
		g.drawImage(imgs.get(kind), x, y, null);
			
	}
	
	public Rectangle getRect(){			//碰撞问题
		return new Rectangle(x, y, imgs.get(kind).getWidth(null),imgs.get(kind).getHeight(null) );	
	}
}
