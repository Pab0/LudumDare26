package ld26.planets;

import java.awt.Color;


/*
 * To keep it simple, all the bodies have the same density. Thus, the mass alone dictates the radius
 */
public class CelestialBody {

	public static final float MAX_MASS = 100000;
	public static final float MIN_MASS = 30;
	public static float MAX_RADIUS;
	public static final int BODY_NUM = 5;

	int x,y;		//x,y are at the center of the body.
	int x1, x2, y1, y2;
	float realX,realY;
	float vx, vy, ax, ay;
	boolean northward = false;
	boolean southward = false;
	boolean eastward = false;
	boolean westward = false;
	float mass;
	float radius;
	Color color;
	boolean[][] footprint = new boolean[Map.WIDTH][Map.HEIGHT];

	public CelestialBody(float mass, int posX, int posY, Color color)
	{
		this.x = posX;
		this.y = posY;
		this.realX = posX;
		this.realY = posY;
		this.vx = 0;
		this.vy = 0;
		this.ax = 0;
		this.ay = 0;
		this.mass = mass;
		this.radius = calcRadius(mass);
		this.color = color;
		this.footprint = this.calcFootprint();
		MAX_RADIUS = this.calcRadius(MAX_MASS);
	}

	static float calcRadius(float mass)
	{
		//density=1 -> volume = mass, and since V=(4/3)*π*r^3:
		float r;
		r = (float)Math.pow((float)(((float)3/4)/Math.PI)*mass, 1.0/3);
		return r;
	}

	float calcMass(float radius)
	{
		float m;
		m = (float)(((float)4/3)*Math.PI*radius*radius*radius);
		return m;
	}

	//returns an array of Map dimensions, with "true" inside the circle and "false" outside of it
	public boolean[][] calcFootprint()
	{
		boolean[][] fp= new boolean[Map.WIDTH][Map.HEIGHT];
		this.x1 = this.x - (int)this.radius;
		this.x2 = this.x + (int)this.radius+1;
		this.y1 = this.y - (int)this.radius;
		this.y2 = this.y + (int)this.radius+1;
		//scanning only the rectangle that engulfs the circle				
		for (int i=x1; i<x2; i++)
		{
			for (int j=y1; j<y2; j++)
			{
				int x,y;
				x = i-this.x;
				y = j-this.y;
				if (x*x + y*y > this.radius*this.radius)
				{
					fp[i][j] = false;
				}
				else 
				{
					fp[i][j] = true;
				}
			}
		}
		return fp;
	}

	public void updateFootprint()
	{
		this.footprint = calcFootprint();
	}

	void calcAcc(float secs)
	{
		float accelerationAccelerator = 1000f;
		if (northward)
		{
			this.ay = -accelerationAccelerator;
		}		
		else if (southward)
		{
			this.ay = accelerationAccelerator;
		}
		else
		{
			this.ay = 0;
		}
		if (eastward)	
		{
			this.ax = -accelerationAccelerator;
		}
		else if (westward)
		{
			this.ax = accelerationAccelerator;
		}
		else
		{
			this.ax = 0;
		}

		//regulate acceleration
		float maxAcc = 500;
		if (this.ax>maxAcc)
		{
			this.ax = maxAcc;
		}
		else if (this.ax<-maxAcc)
		{
			this.ax = -maxAcc;
		}
		if (this.ay>maxAcc)
		{
			this.ay = maxAcc;
		}
		else if (this.ay<-maxAcc)
		{
			this.ay = -maxAcc;
		}
	}

	void calcVel(float secs)
	{
		//calculate new speed
		this.vx += this.ax*secs;
		this.vy += this.ay*secs;


		//friction
		float friction = 2f*secs;
		this.vx -= this.vx*(float)(friction);
		this.vy -= this.vy*(float)(friction);

		//regulate speed
		float maxSpeed = 2*this.radius;
		if (this.vx>maxSpeed)
		{
			this.vx = maxSpeed;
		}
		else if (this.vx<-maxSpeed)
		{
			this.vx = -maxSpeed;
		}
		if (this.vy>maxSpeed)
		{
			this.vy = maxSpeed;
		}
		else if (this.vy<-maxSpeed)
		{
			this.vy = -maxSpeed;
		}
	}

	void calcPos(float secs)
	{
		realX += vx*secs;
		realY += vy*secs;
		x = (int)realX;
		y = (int)realY;
	}
}