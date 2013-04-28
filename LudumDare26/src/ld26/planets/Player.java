package ld26.planets;

import java.awt.Color;

public class Player extends CelestialBody{
	
	String name;
	
	public Player(float mass, int posX, int posY, Color color)
	{
		super(mass, posX, posY, color);
		this.name = "Test subject";
	}
}