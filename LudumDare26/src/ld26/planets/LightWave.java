package ld26.planets;

public class LightWave {

	boolean[][] wave = new boolean[Map.WIDTH][Map.HEIGHT];	//position of lightWave
	boolean[][] colMap = new boolean[Map.WIDTH][Map.HEIGHT];	//used for collision detection
	int[][] brightness = new int[Map.WIDTH][Map.HEIGHT];		//brightness of wave
	int age;
	int originX;
	int originY;

	public LightWave(int x, int y)
	{
		age = 0;
		originX = x;
		originY = y;
		wave[x][y] = true;
	}

	public void update()
	{
		//update colMap
		//this.detectCollision();		//exclude player from Collision
		this.updateMap();
		this.updateBrightness();
		age++;
		for (int i=this.originX-this.age; i<this.originX+this.age; i++)
		{
			for (int j=this.originY-this.age; j<this.originY+this.age; j++)
			{
				wave[i][j] = true;
			}
		}
	}

	public void detectCollision()
	{
		for (int i=this.originX-this.age; i<this.originX+this.age; i++)
		{
			for (int j=this.originY-this.age; j<this.originY+this.age; j++)
			{
				if (wave[i][j] && colMap[i][j])
				{
					wave[i][j] = false;
				}
			}
		}
	}

	private void updateMap()
	{
		for (int i=1; i<Map.WIDTH-1; i++)
		{
			for (int j=1; j<Map.HEIGHT-1; j++)
			{
				if (wave[i][j])
				{
					wave[i][j] = false;
					//primary axis: horizontal
					if (Math.abs(i-this.originX)>Math.abs(j-this.originY))
					{
						if (i-this.originX>0)
						{
							wave[i+1][j] = true;
						}
						else
						{
							wave[i-1][j] = true;
						}
					}
					//primary axis: vertical
					else
					{
						if (j-this.originY>0)
						{
							wave[i][j+1] = true;
						}
						else
						{
							wave[i][j-1] = true;
						}
					}
				}
			}
		}
	}

	private void updateBrightness()
	{
		for (int i=0; i<Map.WIDTH; i++)
		{
			for (int j=0; j<Map.HEIGHT; j++)
			{
				if (this.wave[i][j])
				{
					this.brightness[i][j] += 10;		//+10 in vacuum
					if (this.colMap[i][j])
					{
						this.brightness[i][j] += 120;	//+120 on planets
					}
				}
				if (!this.wave[i][j] && this.brightness[i][j]>0)
				{
					this.brightness[i][j]--;			//diminishing by 1 on each pass
				}
			}
		}

	}

	public static void main(String args[])
	{
		LightWave w = new LightWave(10,10);
		for (int t=0; t<10; t++)
		{
			System.out.println("\n Wave @ iteration #" + w.age);
			w.update();
			for (int i=0; i<Map.WIDTH; i++)
			{
				for (int j=0; j<Map.HEIGHT; j++)
				{
					if (w.wave[i][j])
					{
						System.out.print(1);
					}
					else
					{
						System.out.print(0);
					}
				}
				System.out.println();
			}
		}
	}
}
