/**
 * 坦克大战游戏
 * 1.画一个坦克,以左上角坐标为参照点，如果追求完美的话，应该以坦克中心为参照点，这样转向更逼真
 * 2.让我的坦克动起来
 * 3.让我的坦克可以发射子弹，可以发射多个子弹，同时最多5个
 * 4.如果子弹击中敌人坦克则敌人坦克消失,或爆炸
 * 5.敌人坦克也可以移动，在一定的范围内自由移动
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;//用到集合Vector
import javax.imageio.*;
import java.io.*;

public class MyTankGame extends JFrame{
	
	MyPanel mp=null;

	public static void main(String[] args) {
		
		MyTankGame mtg=new MyTankGame();
		
	}
	public MyTankGame() {
		
		mp=new MyPanel();
		
		//启动mp线程
		Thread t=new Thread(mp);
		t.start();
		
		this.add(mp);
		this.addKeyListener(mp);//KeyListener只能加到Frame上，加到Panel上无效
		
		this.setSize(412, 335);//??边框在x轴上占12个像素,y轴上占35个像素?
		//this.setResizable(false);
		this.setTitle("坦克大战");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		this.setVisible(true);
				
	}

}
class MyPanel extends JPanel implements KeyListener,Runnable{
	
	Hero hero=null;
	
	//定义敌人的坦克组
	Vector<Enemy> ems=new Vector<Enemy>();
	int emSize=3;
	
	//定义炸弹集合
	Vector<Bomb> bombs=new Vector<Bomb>();
	
	//定义三张照片，三张照片组成一颗炸弹
	Image image1=null;
	Image image2=null;
	Image image3=null;
	
	public MyPanel() {
		
		hero=new Hero(190,270);	
		
		//初始化敌人的坦克,同时也初始化敌人的第一颗子弹
		for(int i=0;i<emSize;i++) {
			//创建一辆敌人坦克
			Enemy em = new Enemy((i+1)*50,0);
			//加入Vector
		    ems.add(em);
		    em.setDirect(2);//初始朝下
		    //启动敌人坦克线程
		    Thread t=new Thread(em);
		    t.start();
		    
		    //创建敌人的第一颗子弹
		    Bullet bullet=new Bullet(em.x+10,em.y+30,2);//敌人坦克初始时的方向定为了2
		    em.bullets.add(bullet);
		    //启动敌人子弹
		    Thread t2=new Thread(bullet);
		    t2.start();
		}
		
		//给照片赋值,第一个爆炸不显示，用javax.imageio来实现
//		image1=Toolkit.getDefaultToolkit().getImage("images/bomb1.png");
//		image2=Toolkit.getDefaultToolkit().getImage("images/bomb2.png");
//		image3=Toolkit.getDefaultToolkit().getImage("images/bomb3.png");
		
		//完美解决炸弹显示效果
		try {
			image1=ImageIO.read(new File("images/bomb1.png"));
			image2=ImageIO.read(new File("images/bomb2.png"));
			image3=ImageIO.read(new File("images/bomb3.png"));
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}
	//从写paint函数
	public void paint(Graphics g) {
		
		super.paint(g);
		
		g.fillRect(0, 0, 400, 300);
		
		//画我的坦克
		if(hero.isLive) {
		
		    this.drawTank(hero.getX(),hero.getY(),g,hero.getDirect(),hero.getType());
		
		}
		
		//画我的子弹多发
		for(int i=0;i<hero.bullets.size();i++) {
		
			if(hero.bullets.get(i)!=null&&hero.bullets.get(i).isLive==true) {
			    g.draw3DRect(hero.bullets.get(i).x, hero.bullets.get(i).y, 1, 1, false);
			}
			//当子弹已死，在这里把死了的子弹从集合中删除，否则发射完5颗子弹后就再发不出来子弹了
			if(hero.bullets.get(i).isLive==false) {
				hero.bullets.remove(hero.bullets.get(i));//参数不直接写i因为有可能下标i正被替换正在用到
			}
		}
		
		//画出所有活着的炸弹
		for(int i=0;i<bombs.size();i++) {
			
			//取出一个炸弹
			Bomb bomb=bombs.get(i);
			
			if(bomb.isLive) {
			//因为MyPanel是个线程，在run()函数里设定每隔100毫秒从画一次，因此相当于是在循环里画
			//同时可能有好几个炸弹同时爆炸，因此不是一个画完再画下一个，而是同时分步进行
			    if(bomb.life>6) {
				    g.drawImage(image1, bomb.x, bomb.y, 30, 30, this);
			    }else if(bomb.life>3){
				    g.drawImage(image2, bomb.x, bomb.y, 30, 30, this);
			    }else {
				    g.drawImage(image3, bomb.x, bomb.y, 30, 30, this);
			    }
			
			    bomb.lifeDown();//调用lifeDown函数，减少一个life值
			
			    if(bomb.life==0) {
				    bombs.remove(bomb);
			    }
			}
			
		}
		
		//画敌人的坦克,不用emSize而用ems.size()是因为不确定敌人坦克数，有时打死一个剩两个
		//顺便画出敌人的所有子弹
		for(int i=0;i<ems.size();i++) {
			//取出坦克
			Enemy em=ems.get(i);
			
			if(em.isLive) {
				
			    this.drawTank(em.getX(), em.getY(), g, em.getDirect(), em.getType());
			    //画出它的所有子弹
			    for(int j=0;j<em.bullets.size();j++) {
			    	//取出子弹
			    	Bullet bullet=em.bullets.get(j);
			    	
			    	if(bullet.isLive) {
			    		g.draw3DRect(bullet.x, bullet.y, 1, 1, false);			    		
			    	}else{
			    		em.bullets.remove(bullet);
			    	}
			    				    	
			    }
			    
			}
		}
		
	}
	
	public void drawTank(int x, int y, Graphics g, int direct, int type) {
		
		switch(type) {
		
		case 0: 
			g.setColor(Color.YELLOW);
		    break;
		case 1: 
			g.setColor(Color.CYAN);
		    break;
		
		}
		switch(direct) {
		//0为朝上，1为朝右，2为朝下，3为朝左
		case 0:
			g.fill3DRect(x, y, 5, 30, false);
			g.fill3DRect(x+5, y+5, 10, 20, false);
			g.fill3DRect(x+15, y, 5, 30, false);
			g.fillOval(x+5, y+10, 10, 10);
			g.drawLine(x+10, y, x+10, y+15);
			break;
						
		case 1:
			g.fill3DRect(x, y, 30, 5, false);
			g.fill3DRect(x, y+15, 30, 5, false);
			g.fill3DRect(x+5, y+5, 20, 10, false);
			g.fillOval(x+10, y+5, 10, 10);
			g.drawLine(x+15, y+10, x+30, y+10);
			break;
			
		case 2:
			g.fill3DRect(x, y, 5, 30, false);
			g.fill3DRect(x+5, y+5, 10, 20, false);
			g.fill3DRect(x+15, y, 5, 30, false);
			g.fillOval(x+5, y+10, 10, 10);
			g.drawLine(x+10, y+15, x+10, y+30);
			break;
			
		case 3:
			g.fill3DRect(x, y, 30, 5, false);
			g.fill3DRect(x, y+15, 30, 5, false);
			g.fill3DRect(x+5, y+5, 20, 10, false);
			g.fillOval(x+10, y+5, 10, 10);
			g.drawLine(x, y+10, x+15, y+10);
			break;
			
		}
		
	}
	//在面板上定义子弹击中坦克的函数,以便于之后添加炸弹集合
	public void hitTank(Bullet bullet,Tank tank) {
		
		switch(tank.direct) {
		
		case 0:
		case 2:
			if(bullet.x>tank.x&&bullet.x<(tank.x+20)&&bullet.y>tank.y&&bullet.y<(tank.y+30)) {
				tank.isLive=false;
				bullet.isLive=false;
				Bomb bomb=new Bomb(tank.x,tank.y);
				bombs.add(bomb);//生成一个炸弹添加到炸弹集合里
				
			}
			break;
		case 1:
		case 3:
			if(bullet.x>tank.x&&bullet.x<(tank.x+30)&&bullet.y>tank.y&&bullet.y<(tank.y+20)) {
				tank.isLive=false;
				bullet.isLive=false;
				Bomb bomb=new Bomb(tank.x,tank.y);
				bombs.add(bomb);//生成一个炸弹添加到炸弹集合里
				
			}
			break;
		
		}
		
	}
	//判断我的子弹是否击中敌人坦克
	public void hitEnemyTank() {
		
		//因为并不知道每个子弹和坦克都在什么位置，所以要一直不断的判断是否有子弹击中坦克
		for(int i=0;i<hero.bullets.size();i++) {
			//取出子弹
			Bullet bullet=hero.bullets.get(i);
			
			//即使子弹isLive=false，它仍然会继续存在跑到边框，存在那里，所以要加上下面的判断
			//确认子弹还活着的情况下才去判断是否击中敌人坦克
			if(bullet.isLive) {
			
			    for(int j=0;j<ems.size();j++) {	
			    	//取出敌人坦克
			    	Enemy em=ems.get(j);
			    	//即使坦克isLive=false，它也仍然一直存在那里直到程序删除它，没有引用被系统清理
			    	//因此要加上下面的判断
			    	if(em.isLive) {
			    		
				        this.hitTank(bullet, em);
				        
			    	}
			    }
			}
		}
		
	}
	//判断敌人的子弹是否击中我
	public void hitMe() {
		
		for(int i=0;i<ems.size();i++) {
			
			Enemy em=ems.get(i);
			
			for(int j=0;j<em.bullets.size();j++) {
				
				Bullet b=em.bullets.get(j);
				
				this.hitTank(b, hero);
				
			}
			
		}
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyPressed(KeyEvent e) {
		
	//按键a代表向左移动，s代表向下移动，d代表向右移动，w代表向上移动
		if(e.getKeyCode()==KeyEvent.VK_A) {
			
			this.hero.setDirect(3);	
			this.hero.moveLeft();//把x,y值的改变写到我的坦克里（Hero里）增加可读性
			                     //相当于：this.hero.x-=this.hero.speed;
			
		}else if(e.getKeyCode()==KeyEvent.VK_S) {
			
			this.hero.setDirect(2);
			this.hero.moveDown();
			
		}else if(e.getKeyCode()==KeyEvent.VK_D) {
			
			this.hero.setDirect(1);
			this.hero.moveRight();
			
		}else if(e.getKeyCode()==KeyEvent.VK_W) {
			
			this.hero.setDirect(0);
			this.hero.moveUp();
			
		}
		if(e.getKeyCode()==KeyEvent.VK_J) {
			if(hero.bullets.size()<5) {//因为是先判断再执行shooting函数，
				                       //因此第一次判断size为0，最多连发5发子弹的话，应为<5.
			   this.hero.shooting();
			}
		}
		this.repaint();
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
	@Override
	public void run() {
		
		//每隔100毫秒从绘子弹
		//线程创建后要启动		
		while(true) {
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
            this.hitEnemyTank();//判断我的子弹是否击中敌人的坦克
			
            this.hitMe();//判断我是否被击中
			
			this.repaint();
		}
	}
	
}



