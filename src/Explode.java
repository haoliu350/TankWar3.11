import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.*;


public class Explode {
	int x, y;
	private boolean live = true;
	private TankClient tc;
	
	//int[] diameter = {3, 7, 14, 24, 33, 36, 30, 25, 12, 4};	//��ը�Ĵ�С�������ʾ
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	
	private static Image[] images = { 
		//��explode����һ��������������������class������ĸ���
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
	
	public void draw(Graphics g){	//��ը���Լ��ķ���
		if(!init){
			
			for (int j = 0; j < images.length; j++) {
				g.drawImage(images[j], -100, -100, null);
			}
			init = true;
		}
		
		
		if(!live){
			tc.explodes.remove(this);	//�ӵ����˾ʹ�list��ȥ��
			return;
		}
		
		if(step == images.length){	//basement���ж�
			live = false;
			step = 0;
			return;
		}
		/*
		Color c = g.getColor();
		g.setColor(Color.ORANGE);
		//��Բ��ʾ��ը
		g.fillOval(x-diameter[step]/2, y-diameter[step]/2, diameter[step], diameter[step]);
		g.setColor(c);
		*/
		g.drawImage(images[step], x, y, null);
		step ++;
	}
	
}
