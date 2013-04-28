package ld26.tools;

public class TestTimer {
	
	public static void main(String[] args)
	{
		Timer timer = new Timer();
		for (int i=0; i<20; i++)
		{
			System.out.println(i + ": " + timer.getElapsedTimeMil());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}