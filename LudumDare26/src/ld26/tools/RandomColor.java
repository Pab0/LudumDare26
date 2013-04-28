package ld26.tools;

import java.awt.Color;
import java.util.Random;

public class RandomColor {

	static Color color;
	public static Color getColor()
	{
		Random random = new Random();
		int r = random.nextInt(5);
		switch(r){
		case 0:
			color = Color.CYAN;
			break;
		case 1:
			color = Color.BLUE;
			break;
		case 2:
			color = Color.DARK_GRAY;
			break;
		case 3:
			color = Color.GRAY;
			break;
		case 4:
			color = Color.LIGHT_GRAY;
			break;
		case 5:
			color = Color.ORANGE;
			break;
		default:
			color = Color.MAGENTA;
		}
		return color;
	}
}
