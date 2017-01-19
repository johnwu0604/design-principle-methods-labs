// Lab2.java

package ev3Odometer;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;

public class Lab2 {
	
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	// Constants
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 15.8;

	public static void main(String[] args) {
		int buttonChoice;

		// some objects that need to be instantiated
		
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
		OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);

		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString(" Float | Drive  ", 0, 2);
			t.drawString("motors | in a   ", 0, 3);
			t.drawString("       | square ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			
			leftMotor.forward();
			leftMotor.flt();
			rightMotor.forward();
			rightMotor.flt();
			
			odometer.start();
			odometryDisplay.start();
			
		} else {
			// start the odometer, the odometry display and (possibly) the
			// odometry correction
			
			odometer.start();
			odometryDisplay.start();
			// odometryCorrection.start();

			// spawn a new Thread to avoid SquareDriver.drive() from blocking
			(new Thread() {
				public void run() {
					SquareDriver.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
				}
			}).start();
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}