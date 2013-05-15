package ld26.tools;

public class TestTimer {
	
	public static void main(String[] args)
	{
		Timer timer = new Timer();
		for (int i=0; i<2000000; i++)
		{
			System.out.println(i + ": " + timer.getElapsedTimeMil());
		}
	}
}