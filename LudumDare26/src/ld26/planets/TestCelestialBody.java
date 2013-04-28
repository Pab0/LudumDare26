package ld26.planets;

import java.awt.Color;

public class TestCelestialBody {

	public static void main(String[] args) {
		CelestialBody cb = new CelestialBody(50, 10, 10, Color.BLACK);
		System.out.println(cb.radius);
		boolean[][] array = cb.calcFootprint();
		System.out.println(cb.calcMass(cb.radius));		
	}
}