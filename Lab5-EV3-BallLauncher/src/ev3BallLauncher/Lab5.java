package ev3BallLauncher;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Lab5 {
	
	private static final EV3LargeRegulatedMotor launchMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	// Characteristics of our vehicle
	public static final double TRACK = 15.8;
	public static final double RADIUS = 2.093;
	
	private static final double SQUARE_LENGTH = 30.48;
	
	private static int buttonChoice;
	
	public static void main(String[] args) {
		
		@SuppressWarnings("resource")							    // Because we don't bother to close this resource

		// instantiate our objects
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		Navigator navigator = new Navigator(leftMotor, rightMotor, odometer);
		BallLauncher ballLauncher = new BallLauncher(launchMotor);
		
		// start our odometer
		odometer.start();

		buttonChoice = waitForButtonChoice(t);
		
		while ( buttonChoice != Button.ID_DOWN ) {
			
			if (buttonChoice == Button.ID_LEFT) {
				t.drawString("                     ", 0, 0);
				t.drawString("       Shooting      ", 0, 1);
				t.drawString("          At         ", 0, 2);
				t.drawString("         Left        ", 0, 3);
				t.drawString("        Target       ", 0, 4);
				t.drawString("                     ", 0, 5);
				// turn robot towards left target, and shoot 
				turnTowardCoordinates( -SQUARE_LENGTH, 3*SQUARE_LENGTH, odometer, navigator );
				ballLauncher.shootAtSideTarget();
				buttonChoice = waitForButtonChoice(t);
				
			} 
			
			else if (buttonChoice == Button.ID_UP) {
				t.drawString("                     ", 0, 0);
				t.drawString("       Shooting      ", 0, 1);
				t.drawString("          At         ", 0, 2);
				t.drawString("        Middle       ", 0, 3);
				t.drawString("        Target       ", 0, 4);
				t.drawString("                     ", 0, 5);
				// turn robot towards middle target, and shoot 
				turnTowardCoordinates( 0, 3*SQUARE_LENGTH, odometer, navigator );
				ballLauncher.shootAtMiddleTarget();
				buttonChoice = waitForButtonChoice(t);
			} 
			
			else if (buttonChoice == Button.ID_RIGHT) {
				t.drawString("                     ", 0, 0);
				t.drawString("       Shooting      ", 0, 1);
				t.drawString("          At         ", 0, 2);
				t.drawString("        Right        ", 0, 3);
				t.drawString("        Target       ", 0, 4);
				t.drawString("                     ", 0, 5);
				// turn robot towards right target, and shoot 
				turnTowardCoordinates( SQUARE_LENGTH, 3*SQUARE_LENGTH, odometer, navigator );
				ballLauncher.shootAtSideTarget();
				buttonChoice = waitForButtonChoice(t);
			}
			
		}
		
		System.exit(0);
	
	}
	
	/**
	 * A method that shows the home screen and waits until a button is pressed
	 * 
	 * @param TextLCD t
	 * @return int buttonChoice
	 */
	private static int waitForButtonChoice(TextLCD t) {
		
		buttonChoice = 0;
		
		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("          ^          ", 0, 0);
			t.drawString("       Forward       ", 0, 1);
			t.drawString(" < Left   |  Right > ", 0, 2);
			t.drawString("        Quit         ", 0, 3);
			t.drawString("          v          ", 0, 4);
			
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT && buttonChoice != Button.ID_UP
				&& buttonChoice != Button.ID_DOWN );
		
		t.clear();
		return buttonChoice;
	}
	
	/**
	 * A method that turns the vehicle towards a given x and y coordinate
	 * 
	 * @param double x
	 * @param double y
	 */
	private static void turnTowardCoordinates(double x, double y, Odometer odometer, Navigator navigator) {
		double deltaX = x - odometer.getX();
		double deltaY = y - odometer.getY();
		navigator.turnTo( navigator.calculateMinAngle(deltaX, deltaY) );
	}
}
