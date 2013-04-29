package ld26.planets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import ld26.tools.RandomColor;

public class Map {

	public static final int BODY_NUM = 15;
	public static final int WIDTH = 700;
	public static final int HEIGHT = 400;
	public static final int BORDER_WIDTH = 10;

	ArrayList<CelestialBody> bodies;
	Player player;
	boolean[][] world;		//used for collision
	LightWave lightwave;
	boolean[][] lightWave;	//the points that the light wave currently occupies
	int camX, camY;
	Random random;
	Canvas canvas;

	public static void createMap()
	{
		Map map = new Map();
		map.instantiateWorld();
		map.linkCanvas();
		map.updateMap();
	}

	public Map()
	{
		camX = 100;
		camY = 100;
		world = new boolean[Map.WIDTH][Map.HEIGHT];
		bodies = new ArrayList<CelestialBody>();
		random = new Random();
	}

	private void linkCanvas()
	{
		this.canvas = new Canvas();
		this.canvas.lnkMap = this;
		this.canvas.lnkPlayer = this.player;
	}

	private void instantiateWorld()
	{
		createBorder();
		createCelestialBodies();
		createPlayer();
	}

	private void createBorder()
	{
		//horizontal border
		for (int i=0; i<Map.WIDTH; i++)
		{
			for (int j=0; j<Map.BORDER_WIDTH; j++)
			{
				this.world[i][j] = true;
			}
			for (int j=Map.HEIGHT-Map.BORDER_WIDTH; j<Map.HEIGHT; j++)
			{
				this.world[i][j] = true;
			}
		}
		//vertical border
		for (int i=0; i<Map.BORDER_WIDTH; i++)
		{
			for (int j=0; j<Map.HEIGHT; j++)
			{
				this.world[i][j] = true;
			}
		}
		for (int i=Map.WIDTH-Map.BORDER_WIDTH; i<Map.WIDTH; i++)
		{
			for (int j=0; j<Map.HEIGHT; j++)
			{
				this.world[i][j] = true;
			}
		}
	}

	private void createPlayer()
	{
		float mass;
		float radius;
		int posX, posY;
		boolean[][] tmpArray = null;
		boolean collision = false;
		//checking if the space isn't already occupied
		do
		{
			mass = random.nextInt((int)(CelestialBody.MAX_MASS+CelestialBody.MIN_MASS))+CelestialBody.MIN_MASS;
			posX = random.nextInt(Map.WIDTH);
			posY = random.nextInt(Map.HEIGHT);
			radius = CelestialBody.calcRadius(mass);

			if (posX-radius>=0 && posX+radius<Map.WIDTH && posY-radius>=0 && posY+radius<Map.HEIGHT)
			{
				player = new Player(mass, posX, posY, Color.decode("0xCDAE00"));
				tmpArray = player.footprint;
				collision = false;
				for (int k=0; k<Map.WIDTH; k++)
				{
					for (int l=0; l<Map.HEIGHT; l++)
					{
						if (tmpArray[k][l] && world[k][l])
						{
							collision = true;
						}						
					}
				}
			}
			else 
			{
				collision=true;					
			}
		}while(collision);
		bodies.add(player);
		for (int m=0; m<Map.WIDTH; m++)
		{
			for (int n=0; n<Map.HEIGHT; n++)
			{
				this.world[m][n] = this.world[m][n] || tmpArray[m][n];
			}
		}
		System.out.println("Created golden Player "
				+ " @ " + posX + ", " + posY
				+ " with radius=" + player.radius);
	}

	private void createCelestialBodies()
	{
		float mass;
		int posX, posY;
		float radius;
		CelestialBody cb = null;
		Color colour;
		boolean[][] tmpArray = null;
		boolean collision = false;
		for (int i=0; i<Map.BODY_NUM; i++)
		{
			//checking if the space isn't already occupied
			do
			{
				mass = random.nextInt((int)(CelestialBody.MAX_MASS+CelestialBody.MIN_MASS))+CelestialBody.MIN_MASS;
				posX = random.nextInt(Map.WIDTH);
				posY = random.nextInt(Map.HEIGHT);
				colour = RandomColor.getColor();
				radius = CelestialBody.calcRadius(mass);

				if (posX-radius>=0 && posX+radius<Map.WIDTH && posY-radius>=0 && posY+radius<Map.HEIGHT)
				{
					cb = new CelestialBody(mass, posX, posY, colour);
					tmpArray = cb.footprint;
					collision = false;
					for (int k=0; k<Map.WIDTH; k++)
					{
						for (int l=0; l<Map.HEIGHT; l++)
						{
							if (tmpArray[k][l] && world[k][l])
							{
								collision = true;
							}						
						}	
					}
				}
				else 
				{
					collision=true;					
				}
			}while(collision);
			bodies.add(cb);
			for (int m=0; m<Map.WIDTH; m++)
			{
				for (int n=0; n<Map.HEIGHT; n++)
				{
					this.world[m][n] = this.world[m][n] || tmpArray[m][n];
				}
			}
			System.out.println("Created " + colour.toString()
					+" body #" + i 
					+ " @ " + posX + ", " + posY
					+ " with radius=" + cb.radius);
		}
	}

	void updateMap()
	{
		collisionCheck();
		updateBodies();
		//updateLightWave();
		//updateCam();
		//calcLight();
		updateWorld();
	}

	private void setBorder()
	{
		createBorder();
	}

	void updateBodies()
	{
		for (int i=0; i<bodies.size(); i++)
		{
			float secs = (float)(canvas.millis/1000.0f);
			CelestialBody cb = bodies.get(i);
			//calcForces();
			cb.calcAcc(secs);
			cb.calcVel(secs);
			cb.calcPos(secs);			
			cb.updateFootprint();
		}
	}

	void updateFootprints()
	{
		for (int i=0; i<bodies.size(); i++)
		{
			bodies.get(i).updateFootprint();
		}
	}

	void updateWorld()
	{
		//resets the World
		for (int k=0; k<Map.WIDTH; k++)
		{
			for (int l=0; l<Map.HEIGHT; l++)
			{
				this.world[k][l] = false;
			}
		}
		//sets Border
		setBorder();
		//repopulates the World
		for (int i=0; i<this.bodies.size(); i++)
		{
			CelestialBody cb = this.bodies.get(i);
			for (int k=0; k<Map.WIDTH; k++)
			{
				for (int l=0; l<Map.HEIGHT; l++)
				{
					if (cb.footprint[k][l])
					{
						this.world[k][l] = true;
					}
				}
			}
		}
	}

	//detecting collisions by checking common "true" points
	void collisionCheck()
	{
		//setting border
		boolean[][] borderArray = new boolean[Map.WIDTH][Map.HEIGHT];
		//horizontal border
		for (int i=0; i<Map.WIDTH; i++)
		{
			for (int j=0; j<Map.BORDER_WIDTH; j++)
			{
				borderArray[i][j] = true;
			}
			for (int j=Map.HEIGHT-Map.BORDER_WIDTH; j<Map.HEIGHT; j++)
			{
				borderArray[i][j] = true;
			}
		}
		//vertical border
		for (int i=0; i<Map.BORDER_WIDTH; i++)
		{
			for (int j=0; j<Map.HEIGHT; j++)
			{
				borderArray[i][j] = true;
			}
		}
		for (int i=Map.WIDTH-Map.BORDER_WIDTH; i<Map.WIDTH; i++)
		{
			for (int j=0; j<Map.HEIGHT; j++)
			{
				borderArray[i][j] = true;
			}
		}

		//instantiating tpmArray, empty is -1
		int[][] tmpArray = new int[Map.WIDTH][Map.HEIGHT];	//holds the index of the body occupying x,y
		for (int i=0; i<Map.WIDTH; i++)
		{
			for (int j=0; j<Map.HEIGHT; j++)
			{
				tmpArray[i][j] = -1;	
			}
		}

		//checking bodies
		int x=0;
		int y=0;
		int radiusdepth=0;	//holds the depth to which the two bodies are in each other
		int maxX=-1;	//min and max for the borders
		int minX=Map.WIDTH;
		int maxY=-1;
		int minY=Map.WIDTH;
		int index=0;
		boolean collision=false;
		boolean borderCollision=false;
		CelestialBody cb;
		CelestialBody cbOld = null;
		for (int b=0; b<this.bodies.size(); b++)
		{
			cb = this.bodies.get(b);
			for (int i=cb.x1; i<cb.x2; i++)
			{
				for (int j=cb.y1; j<cb.y2; j++)
				{
					if (cb.footprint[i][j])
					{
						if (borderArray[i][j])
						{
							System.out.println("Border-collision detected! @ " + i + ", " + j);
							if (i>maxX)
								maxX=i;
							else if (i<minX)
								minX=i;
							if (j>maxY)
								maxY=j;
							else if (j<minY)
								minY=j;							
							index = b;
							borderCollision = true;
						}
						if (tmpArray[i][j]!=-1)
						{
							System.out.println("Collision detected!");
							x = i;
							y = j;							
							cbOld = this.bodies.get(tmpArray[i][j]);

							//calculating radius
							float tmp=0;
							tmp = (float)(cbOld.radius - (Math.sqrt(Math.abs(cbOld.x-x)*Math.abs(cbOld.x-x) + Math.abs(cbOld.y-y)*Math.abs(cbOld.y-y))));
							if (tmp>radiusdepth)
							{
								radiusdepth = (int)tmp + 1;		//+1 to compensate for the rounding to int
								System.out.println(tmp);
							}

							index = b;
							collision = true;							
						}
						tmpArray[i][j] = b;
					}
				}
			}
		}
		if (borderCollision)
		{
			handleBorderCollision(this.bodies.get(index), maxX, minX, maxY, minY);
		}
		if (collision)
		{
			handleCollision(this.bodies.get(index), cbOld, radiusdepth);
		}
	}

	//handling a collision at world[x][y] between cbBig and cbSmall
	void handleCollision(CelestialBody cbBig, CelestialBody cbSmall, int radiusdepth)
	{
		//check who's bigger
		if (cbBig.mass < cbSmall.mass)
		{
			CelestialBody tmp;
			tmp = cbBig;
			cbBig = cbSmall;
			cbSmall = tmp;
		}
		//exchange mass according to the radius change = radiusdepth
		float exchangedMass = cbSmall.mass - cbSmall.calcMass(cbSmall.radius-radiusdepth);
		cbSmall.radius-=radiusdepth;
		cbSmall.mass -= exchangedMass;
		cbBig.mass += exchangedMass;
		cbBig.radius = cbBig.calcRadius(cbBig.mass);
		updateFootprints();
		if (cbSmall.mass<CelestialBody.MIN_MASS/2)
		{
			cbBig.mass += cbSmall.mass;
			cbSmall.mass = 0;
			cbBig.radius = cbBig.calcRadius(cbBig.mass);
			updateFootprints();
		}
		checkBodySize(cbSmall);
	}

	private void handleBorderCollision(CelestialBody cb, int maxX, int minX, int maxY, int minY)
	{
		//collision with left vertical border
		if (minX<Map.BORDER_WIDTH)
		{
			cb.realX = Map.BORDER_WIDTH+1+cb.radius;
			cb.vx = -cb.vx;
			cb.ax = -cb.ax;
		}
		//collision with right vertical border
		else if (maxX>Map.WIDTH-Map.BORDER_WIDTH)
		{
			cb.realX = Map.WIDTH-Map.BORDER_WIDTH-1-cb.radius;
			cb.vx = -cb.vx;
			cb.ax = -cb.ax;
		}
		//collision with top horizontal border
		if (minY<Map.BORDER_WIDTH)
		{
			cb.realY = Map.BORDER_WIDTH+1+cb.radius;
			cb.vy = -cb.vy;
			cb.ay = -cb.ay;
		}
		//collision with bottom horizontal borders
		else if(maxY>Map.HEIGHT-Map.BORDER_WIDTH)
		{
			cb.realY = Map.HEIGHT-Map.BORDER_WIDTH-1-cb.radius;
			cb.vy = -cb.vy;
			cb.ay = -cb.ay;
		}
		updateFootprints();
	}

	void checkBodySize(CelestialBody cb)
	{
		if (cb.mass<CelestialBody.MIN_MASS/2)
		{
			for (int b=0; b<this.bodies.size(); b++)
			{
				if (this.bodies.get(b).equals(cb))
				{
					removeBody(b);
				}
			}
		}
	}

	private void removeBody(int b)
	{
		if (this.bodies.get(b) instanceof Player)
		{
			canvas.endingScreen = true;
			canvas.hasWon = false;
		}
		if (this.bodies.size()==2)
		{
			canvas.endingScreen = true;
			canvas.hasWon = true;
		}
		this.bodies.remove(b);
		this.bodies.trimToSize();
		System.out.println("A celestial body was destroyed.");
	}

}