package ld26.planets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ld26.tools.Timer;

public class Canvas extends JPanel implements KeyListener{

	static final int WIDTH = 720;
	static final int HEIGHT = 440;

	Map lnkMap;
	Player lnkPlayer;
	char input;
	public boolean welcomeScreen, endingScreen, potatoScreen, hasWon, restart;

	static Timer timer;
	static Timer debugTimer;
	int millis;

	public int[][] brightness;	//determines brightness at x,y.
	public Color[][] color;		//stores the color of the object at x,y, even if it's dark
	public Color[][] canvas;		//stores the color at x,y after applying the corresponding brightness	

	public Canvas()
	{
		//Instantiating arrays
		brightness = new int[Map.WIDTH][Map.HEIGHT];
		color = new Color[Map.WIDTH][Map.HEIGHT];
		canvas = new Color[Map.WIDTH][Map.HEIGHT];
		for (int i=0; i<Map.WIDTH; i++)
		{
			for (int j=0; j<Map.HEIGHT; j++)
			{
				brightness[i][j] = 0;
				color[i][j] = Color.BLACK;
				canvas[i][j] = Color.BLACK;
			}
		}

		timer = new Timer();
		debugTimer = new Timer();

		this.setDoubleBuffered(true);
		this.setFocusable(true);
		addKeyListener(this);

		JFrame frame = new JFrame();
		frame.setSize(Canvas.WIDTH, Canvas.HEIGHT);
		//frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setVisible(true);

		welcomeScreen = true;
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (welcomeScreen)
		{
			this.paintWelcome(g);
		}
		else if (!welcomeScreen && !endingScreen)
		{
			if (this.lnkMap!=null)
			{
				this.paintTask(g);
			}
		}
		if (endingScreen)
		{
			this.paintEnding(g);
		}
		repaint();
	}

	private void paintWelcome(Graphics g)
	{
		String str0 = "Hello! Welcome to \"Planets\"!";
		String str1 = "You are a lonely, golden planet, who must survive against the rest of the celestial objects.";
		String str2 = "But beware! Bigger objects icorporate smaller bodies!";
		String str3 = "Move with WASD. Press SPACE to begin. Good luck!";
		String str4 = "\n\n\n\nMade by Pabo for the Ludum Dare 26 competition on 28/29 April 2013.";
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, Canvas.WIDTH, Canvas.HEIGHT);
		g.setColor(Color.BLACK);
		g.setFont(new Font("BOLD", 0, 18));
		g.drawString(str0, 10, Map.HEIGHT/10);
		g.drawString(str1, 10, Map.HEIGHT/10+20);
		g.drawString(str2, 10, Map.HEIGHT/10+40);
		g.drawString(str3, 10, Map.HEIGHT/10+60);
		g.drawString(str4, 10, Map.HEIGHT/10+80);
	}

	private void paintEnding(Graphics g)
	{
		String str0 = "Congratulations, you beat all the other planets!.";
		String str1 = "Hmm, it's pretty lonely now, isn't it?";
		String str2 = "Anyways, press SPACE to get your reward!";

		String str3 = "GAME OVER";
		String str4 = "Start again? [SPACE]";
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, Canvas.WIDTH, Canvas.HEIGHT);
		g.setColor(Color.BLACK);
		g.setFont(new Font("BOLD", 0, 18));
		if (hasWon)
		{
			g.drawString(str0, 10, Map.HEIGHT/10);
			g.drawString(str1, 10, Map.HEIGHT/10+20);
			//g.drawString(str2, 10, Map.HEIGHT/10+40);			
		}
		else
		{
			g.setFont(new Font("BOLD", 0, 20));
			g.drawString(str3, 300, 200);
			//g.drawString(str4, 10, Map.HEIGHT/10+20);
			if (restart)
			{
				//endingScreen = false;
			}
		}
	}

	private void paintTask(Graphics g)
	{
		millis = (int)timer.getElapsedTimeMil();
		System.out.println("Framerate: " + millis + "ms\n");
		this.lnkMap.updateMap();
		System.out.println("Map update: " + debugTimer.getElapsedTimeMil());
		drawBackground(g);
		System.out.println("Draw background: " + debugTimer.getElapsedTimeMil());
		//calcBrightness(g);
		calcFauxBrightness(g);
		System.out.println("Faux Brightness: " + debugTimer.getElapsedTimeMil());
		calcColor(g);
		//calcCanvas(g);
		System.out.println("Color calc: " + debugTimer.getElapsedTimeMil());
		drawCanvas(g);
		System.out.println("Drawing canvas: " + debugTimer.getElapsedTimeMil());
	}

	private void drawBackground(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Map.WIDTH, Map.HEIGHT);
	}

	//causes only lit parts to be drawn
	private void calcFauxBrightness(Graphics g)
	{
		for (int i=0; i<Map.WIDTH; i++)
		{
			for (int j=0; j<Map.HEIGHT; j++)
			{
				if (this.color[i][j]!=Color.BLACK)
					//if (this.lnkPlayer.footprint[i][j])
				{					
					this.brightness[i][j] = 1;
				}
				else
				{
					this.brightness[i][j] = 0;
				}

			}
		}
	}

	private void calcBrightness(Graphics g)
	{
		for (int i=0; i<Canvas.WIDTH; i++)
		{
			for (int j=0; j<Canvas.HEIGHT; j++)
			{
				//diminishing light
				if (brightness[i][j]>0)
				{
					brightness[i][j]--;
				}
				//lightWave on collision
				if (this.lnkMap.lightWave[i][j]==true
						&& this.lnkMap.world[i][j]==true)
				{
					this.lnkMap.lightWave[i][j]=false;
					brightness[i][j] += 120;
				}
				//lightWave in vacuum
				else if (this.lnkMap.lightWave[i][j]==true)
				{
					brightness[i][j] += 10;
				}
			}
		}
	}

	private void calcColor(Graphics g)
	{
		for (int k=0; k<Map.WIDTH; k++)
		{
			for (int l=0; l<Map.HEIGHT; l++)
			{						
				this.color[k][l] = Color.BLACK;
			}
		}
		for (int i=0; i<this.lnkMap.bodies.size(); i++)
		{
			CelestialBody cb = this.lnkMap.bodies.get(i);
			Color c = cb.color;
			int intRadius = (int)cb.radius;
			int x1 = cb.x - intRadius;
			int x2 = cb.x + intRadius+1;
			int y1 = cb.y - intRadius;
			int y2 = cb.y + intRadius+1;
			boolean[][] tmpArray = cb.footprint;
			for (int k=x1; k<x2; k++)
			{
				for (int l=y1; l<y2; l++)
				{
					if (tmpArray[k-cb.x1][l-cb.y1])
					{
						this.color[k][l] = c;
					}
				}
			}
		}
	}

	private void calcCanvas(Graphics g)
	{
		for (int i=0; i<Map.WIDTH; i++)
		{
			for (int j=0; j<Map.HEIGHT; j++)
			{
				//TODO: calculate final color at i,j, derived from color and brightness arrays
			}
		}
	}

	private void drawCanvas(Graphics g)
	{
		for (int i=0; i<Map.WIDTH; i++)
		{
			for (int j=0; j<Map.HEIGHT; j++)
			{
				if (this.brightness[i][j]>0)
				{
					g.setColor(this.color[i][j]);	//should change this later on from this.color to this.canvas - I'm just testing now
					g.drawLine(i, j, i, j);
				}
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		input = event.getKeyChar();
		switch (input){		
		case 'w':
		case 'W':
			lnkPlayer.northward = true;
			break;
		case 'a':
		case 'A':
			lnkPlayer.eastward = true;
			break;
		case 's':
		case 'S':
			lnkPlayer.southward = true;
			break;
		case 'd':
		case 'D':
			lnkPlayer.westward = true;
			break;
		case ' ':
			if (welcomeScreen)
			{
				this.welcomeScreen = false;			
			}
			else if (endingScreen)
			{
				this.restart = true;
			}			
		default:
			break;
		}

	}

	@Override
	public void keyReleased(KeyEvent event) {
		input = event.getKeyChar();
		switch (input){		
		case 'w':
		case 'W':
			lnkPlayer.northward = false;
			break;
		case 'a':
		case 'A':
			lnkPlayer.eastward = false;
			break;
		case 's':
		case 'S':
			lnkPlayer.southward = false;
			break;
		case 'd':
		case 'D':
			lnkPlayer.westward = false;
			break;
		}

	}

	@Override
	public void keyTyped(KeyEvent event) {
		// TODO Auto-generated method stub
	}
}