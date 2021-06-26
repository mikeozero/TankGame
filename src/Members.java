import java.util.Vector;

class Tank{
	
	int x=0;
	int y=0;
	int direct=0;//0为朝上，1为朝右，2为朝下，3为朝左
	int type=0;//0为我的坦克，1为敌人的坦克
	int speed=1;//坦克的速度，这样就不用写成x++或者y++（如果坦克速度变化了就不好扩展程序了）。
	
	boolean isLive=true;

	public Tank(int x,int y) {
		
		this.x=x;
		this.y=y;				
		
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getDirect() {
		return direct;
	}

	public void setDirect(int direct) {
		this.direct = direct;
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
		
}

class Hero extends Tank{
	
	Vector<Bullet> bullets=new Vector<Bullet>();//变成集合就可以实现多发子弹并存
	
	Bullet bullet=null;
	
	public Hero(int x, int y) {
		
		super(x, y);
		this.setType(0);
		
	}
	//把坦克的移动写到我的坦克这里增加程序可读性，敌人坦克不受我控制
	public void moveUp() {
		//给自己的坦克设置边框
		if(y>0) {
		    y-=speed;
		}
	}
	public void moveRight() {
		if(x<370) {
		    x+=speed;
		}
	}
	public void moveDown() {
		if(y<270) {
		    y+=speed;
		}
	}
	public void moveLeft() {
		if(x>0) {
		    x-=speed;
		}
	}
	
	public void shooting() {
		
		switch(this.direct) {
		
		case 0:
			bullet=new Bullet(x+10,y,0);
			bullets.add(bullet);
			break;
		case 1:
			bullet=new Bullet(x+30,y+10,1);
			bullets.add(bullet);
			break;
		case 2:
			bullet=new Bullet(x+10,y+30,2);
			bullets.add(bullet);
			break;
		case 3:
			bullet=new Bullet(x,y+10,3);
			bullets.add(bullet);
			break;						
		}
		//启动子弹线程
		Thread t=new Thread(bullet);
		t.start();
		
	}
	
}

class Enemy extends Tank implements Runnable{
	
	//boolean isLive=true;
	
	int time=0;//给添加子弹的行为设置时间，别太快否则多颗子弹会画在同一个点上。
	
	Vector<Bullet> bullets=new Vector<Bullet>();//敌人子弹集合
	
	public Enemy(int x, int y) {
		
		super(x,y);
		this.setType(1);
		
	}

	@Override
	public void run() {
		
		while(true) {
		
		    switch(this.direct) {
		
		    case 0:
			    for(int i=0;i<30;i++) {
			    	//给敌人坦克设置边框
			    	if(y>0) {
				        y-=speed;
			    	}
				    try {
					    Thread.sleep(50);//30次每次50毫秒相当于每变化一次方向需要1.5秒，两次就3秒
				    }catch(Exception e) {
				    	e.printStackTrace();
				    }
			    }
			    break;
		    case 1:
		    	for(int i=0;i<30;i++) {
		    		if(x<370) {
				        x+=speed;
		    		}
				    try {
					    Thread.sleep(50);
				    }catch(Exception e) {
				    	e.printStackTrace();
				    }
			    }
		    	break;
		    case 2:
		    	for(int i=0;i<30;i++) {
		    		if(y<270) {
				        y+=speed;
		    		}
				    try {
					    Thread.sleep(50);
				    }catch(Exception e) {
				    	e.printStackTrace();
				    }
			    }
		    	break;
		    case 3:
		    	for(int i=0;i<30;i++) {
		    		if(x>0) {
				        x-=speed;
		    		}
				    try {
					    Thread.sleep(50);
				    }catch(Exception e) {
				    	e.printStackTrace();
				    }
			    }
		    	break;			
		    }
		    
		    time++;
		    
		    //每隔3秒添加一颗子弹，最多同时有5颗子弹
		    if(time%2==0) {
		    	
		    	if(isLive) {
		    		
		    		if(bullets.size()<5) {
		    			//创建子弹
		    			Bullet b=null;
		    			
		    			switch(direct) {		    			
		    			case 0:
		    				b=new Bullet(x+10,y,0);
		    				bullets.add(b);
		    				break;
		    			case 1:
		    				b=new Bullet(x+30,y+10,1);
		    				bullets.add(b);
		    				break;
		    			case 2:
		    				b=new Bullet(x+10,y+30,2);
		    				bullets.add(b);
		    				break;
		    			case 3:
		    				b=new Bullet(x,y+10,3);
		    				bullets.add(b);
		    				break;
		    			}
		    			//启动子弹
		    			Thread t=new Thread(b);
		    			t.start();
		    			
		    		}
		    	}
		    }
		    
		    //让敌人坦克随机产生一个新的方向
		    this.direct=(int)(Math.random()*4);
		    
		    //如果坦克已死则退出
		    if(this.isLive==false) {
		    	break;
		    }
		    
		}
		
	}
	
}

class Bullet implements Runnable{
	
	int x;
	int y;
	int direct;
	int speed=1;
	
	boolean isLive=true;//是否还活着
	
	public Bullet(int x,int y,int direct) {
		
		this.x=x;
		this.y=y;
		this.direct=direct;
		
	}

	@Override
	public void run() {
		
		while(true) {
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
			switch(direct) {
			
			case 0:
				y-=speed;
				break;//break switch语句
			case 1:
				x+=speed;
				break;
			case 2:
				y+=speed;
				break;
			case 3:
				x-=speed;
				break;
			
			}
			//子弹何时死亡
			//先判断子弹是否碰到边缘
			if(x<0||x>400||y<0||y>300) {
				this.isLive=false;
				break;//break while循环
			}
			//System.out.println("子弹坐标x="+x+"子弹坐标y="+y);
		}
		
	}
		
}
class Bomb{
	
	int x,y;
	int life=9;
	boolean isLive=true;
	
	public Bomb(int x,int y) {
		
		this.x=x;
		this.y=y;
		
	}
	public void lifeDown() {
		
		if(this.life>0) {
			life--;
		}else {
			this.isLive=false;
		}
	}
}
