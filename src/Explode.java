import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.*;


public class Explode {
	int x, y;
	private boolean live = true;
	private TankClient tc;
	
	//int[] diameter = {3, 7, 14, 24, 33, 36, 30, 25, 12, 4};	//爆炸的大小，数组标示
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	
	private static Image[] images = { 
		//把explode当做一个对象，这个对象的类型是class，反射的概念
		tk.getImage(Explode.class.getClassLoader().getResource("images/0.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/1.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/2.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/3.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/4.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/5.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/6.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/7.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/8.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/9.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/10.gif"))
	};
	
	private static boolean init = false;
	
	int step = 0;
	
	public Explode(int x, int y, TankClient tc){
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	
	public void draw(Graphics g){	//爆炸画自己的方法
		if(!init){
			
			for (int j = 0; j < images.length; j++) {
				g.drawImage(images[j], -100, -100, null);
			}
			init = true;
		}
		
		
		if(!live){
			tc.explodes.remove(this);	//子弹死了就从list中去掉
			return;
		}
		
		if(step == images.length){	//basement的判断
			live = false;
			step = 0;
			return;
		}
		/*
		Color c = g.getColor();
		g.setColor(Color.ORANGE);
		//画圆表示爆炸
		g.fillOval(x-diameter[step]/2, y-diameter[step]/2, diameter[step], diameter[step]);
		g.setColor(c);
		*/
		g.drawImage(images[step], x, y, null);
		step ++;
	}
	
}
