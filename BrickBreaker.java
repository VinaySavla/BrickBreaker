import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics2D;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.JFrame;

import java.util.Random;


class Gameplay extends JPanel implements KeyListener, ActionListener 
{
	private boolean play = false; //to initiate and stop the game
	private int score = 0; // to calculate the score
	
	private int totalBricks = 48; //number of bricks
	
	private Timer timer; //Runs Action Events
	private int delay=8; //for ball speed
	
	private int paddleX = 310; //Mark Position of Paddle
	
	private int ballposX = 120; //To mark X position of ball
	private int ballposY = 350; //To mark Y position og ball
	private int ballDirX = -1; //update ball's X position during play
	private int ballDirY = -2; //update ball"s Y position during play
	
	private MapGenerator map; // to call MapGenerator class to fetch brick details
	
	public Gameplay() //starting setup of the game is defined in this method
	{		
		map = new MapGenerator(4, 12);
		addKeyListener(this); //To take key events from keyboard. This is for calling game play again.
		setFocusable(true); //Focuses on Keyboard and Mouse input so that it runs well
		setFocusTraversalKeysEnabled(false); //to control focus on tab and shift+tab button
        timer = new Timer(delay,this); //To Manage Virtual Game Time
		timer.start(); // To start Virtual Game time and taking Action from Input Devices
	}
	
	public void paint(Graphics g) //to color all components and display score
	{    		
		// background
		g.setColor(Color.black);
		g.fillRect(1, 1, 692, 592);
		
		// drawing map
		map.draw((Graphics2D) g);
		
		// borders
		g.setColor(Color.yellow);
		g.fillRect(0, 0, 3, 592);
		g.fillRect(0, 0, 692, 3);
		g.fillRect(691, 0, 3, 592);
		
		// the scores 		
		g.setColor(Color.white);
		g.setFont(new Font("serif",Font.BOLD, 25));
		g.drawString(""+score, 590,30);
		
		// the paddle
		g.setColor(Color.yellow);
		g.fillRect(paddleX, 550, 100, 8);
		
		// the ball
		g.setColor(Color.red);
		g.fillOval(ballposX, ballposY, 20, 20);
	
		// when you won the game
		if(totalBricks <= 0)
		{
			 play = false;
             ballDirX = 0;
     		 ballDirY = 0;
             g.setColor(Color.RED);
             g.setFont(new Font("serif",Font.BOLD, 30));
             g.drawString("You Won", 260,300);
             
             g.setColor(Color.RED);
             g.setFont(new Font("serif",Font.BOLD, 20));           
             g.drawString("Press (Enter) to Restart", 230,350);  
		}
		
		// when you lose the game
		if(ballposY > 570)
        {
			 play = false;
             ballDirX = 0;
     		 ballDirY = 0;
             g.setColor(Color.RED);
             g.setFont(new Font("serif",Font.BOLD, 30));
             g.drawString("Game Over, Scores: "+score, 190,300);
             
             g.setColor(Color.RED);
             g.setFont(new Font("serif",Font.BOLD, 20));           
             g.drawString("Press (Enter) to Restart", 230,350);        
        }
		
		g.dispose(); // to dispose context and release system resources
	}	

	public void keyPressed(KeyEvent e) 
	{
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		{        
			if(paddleX >= 600) //checks if the paddle is at extreme right
			{
				paddleX = 600;
			}
			else
			{
				moveRight(); //moves paddle to right
			}
        }
		
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			if(paddleX < 10)  //Checks if paddle is at extreme left
			{
				paddleX = 10;
			}
			else
			{
				moveLeft(); //moves paddle to left
			}
        }		
		if (e.getKeyCode() == KeyEvent.VK_ENTER) //to take enter button as input
		{          
			if(!play) //to start game again if game has ended
			{
				play = true;
				ballposX = 120;
				ballposY = 350;
				ballDirX = -1;
				ballDirY = -2;
				paddleX = 310;
				score = 0;
				totalBricks = 48;
				map = new MapGenerator(4, 12);
				
				repaint();
			}
        }		
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	public void moveRight() //methos to move paddle to its right
	{
		play = true;
		paddleX+=20;	
	}
	
	public void moveLeft() // method to move paddle to its left
	{
		play = true;
		paddleX-=20;	 	
	}
	
	public void actionPerformed(ActionEvent e) //start game
	{
		timer.start();
		if(play)
		{			
			if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(paddleX, 550, 30, 8))) //if ball touches left side paddle
			{
				ballDirY = -ballDirY;
				ballDirX = -2;
			}
			else if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(paddleX + 70, 550, 30, 8)))  //if ball touches left side paddle
			{
				ballDirY = -ballDirY;
				ballDirX = ballDirX + 1;
			}
			else if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(paddleX + 30, 550, 40, 8)))  //if ball touches middle paddle
			{
				ballDirY = -ballDirY;
			}
			
			// check map collision with the ball		
			A: for(int i = 0; i<map.map.length; i++)
			{
				for(int j =0; j<map.map[0].length; j++)
				{				
					if(map.map[i][j] > 0)
					{
						//scores++;
						int brickX = j * map.brickWidth + 80;
						int brickY = i * map.brickHeight + 50;
						int brickWidth = map.brickWidth;
						int brickHeight = map.brickHeight;
						
						Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);					
						Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
						Rectangle brickRect = rect;
						
						if(ballRect.intersects(brickRect))
						{					
							map.setBrickValue(0, i, j);
							score+=5;	
							totalBricks--;
							
							// when ball hit right or left of brick
							if(ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width)	
							{
								ballDirX = -ballDirX;
							}
							// when ball hits top or bottom of brick
							else
							{
								ballDirY = -ballDirY;				
							}
							
							break A;
						}
					}
				}
			}
			
			ballposX += ballDirX; //Moving ball in x direction
			ballposY += ballDirY; //Moving ball in Y direction
			
			if(ballposX < 0) //Left Wall Rebound
			{
				ballDirX = -ballDirX;
			}
			if(ballposY < 0) //Upper Wall Rebound
			{
				ballDirY = -ballDirY;
			}
			if(ballposX > 670)  //Right Wall Rebound
			{
				ballDirX = -ballDirX;
			}		
			
			repaint();		
		}
	}
}

class MapGenerator 
{
	public int map[][];
	public int brickWidth;
	public int brickHeight;
	
	public Color colors[][];
	public MapGenerator (int row, int col)
	{
		Random random = new Random();
		map = new int[row][col];
		colors = new Color[row][col];
		for(int i = 0; i<map.length; i++)
		{
			for(int j =0; j<map[0].length; j++)
			{
				map[i][j] = 1;
				colors[i][j] = new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
			}			
		}
		
		brickWidth = 540/col;
		brickHeight = 150/row;
	}
	
	public void draw(Graphics2D g)
	{
		for(int i = 0; i<map.length; i++)
		{
			for(int j =0; j<map[0].length; j++)
			{
				if(map[i][j] > 0)
				{
					g.setColor(colors[i][j]);
					g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
					
					// this is just to show separate brick, game can still run without it
					g.setStroke(new BasicStroke(3));
					g.setColor(Color.black);
					g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);				
				}
			}
		}
	}
	
	public void setBrickValue(int value, int row, int col)
	{
		map[row][col] = value;
	}
}



class BrickBreaker {
	public static void main(String args[]) {
		JFrame obj=new JFrame();
		Gameplay gamePlay = new Gameplay();
		
		obj.setBounds(10, 10, 700, 600); //For Windows Size
		obj.setTitle("Breakout Ball"); //Title of the Game
		obj.setResizable(false); //One method for ensuring that a graphical interface looks the way you intend is to prevent the user from re-sizing it, using this method of JFrame:
		obj.setVisible(true); //To make Frame Appeare on the screen
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //To close the Window
		obj.add(gamePlay); // To Fetch Gameplay Class 
        obj.setVisible(true); //To make Gameplay Frame Appeare on the Screen
		
	}

}
