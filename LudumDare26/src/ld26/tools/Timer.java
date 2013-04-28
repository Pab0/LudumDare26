package ld26.tools;

public class Timer {

	public long nanoTime;
	
	public Timer()
	{
		this.nanoTime = System.nanoTime();
	}
	
	public long getElapsedTime()
	{
		long elapsedTime;
		elapsedTime = System.nanoTime() - this.nanoTime;
		this.nanoTime = System.nanoTime();
		return elapsedTime;
	}
	
	public float getElapsedTimeMil()
	{
		float milliSec = -1;
		milliSec = ((float)this.getElapsedTime())/1000000;
		return milliSec;
	}
}