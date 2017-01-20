//Lab 3
//Tiffany Wang 260684152
//Matthew Rodin 260623844
//Group 22

package navigation;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import navigation.Navigation;
import navigation.Odometer;
import navigation.OdometryDisplay;
import navigation.UltrasonicController;
import navigation.UltrasonicPoller;
import navigation.WallFollower;
import navigation.wallReaderDisplay;


public class Lab3{
	
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3ColorSensor lightSensor = new EV3ColorSensor(LocalEV3.get().getPort("S1"));
	static final Port usPort = LocalEV3.get().getPort("S2");
	private static final int bandCenter = 30;			// Offset from the wall (cm)
	private static final int bandWidth = 3;
	private static TextLCD t = LocalEV3.get().getTextLCD();
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 16;
	public static UltrasonicPoller usPoller; 
	

	public static void main(String[] args) {
		int buttonChoice;
		Odometer odometer = new Odometer(leftMotor, rightMotor);
//		final Navigation navi = new Navigation(odometer, leftMotor, rightMotor);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
		
		@SuppressWarnings("resource")							    // Because we don't bother to close this resource
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);		// usSensor is the instance
		SampleProvider usDistance = usSensor.getMode("Distance");	// usDistance provides samples from this instance
		float[] usData = new float[usDistance.sampleSize()];		// usData is the buffer in which data are returned
		WallFollower p = new WallFollower(leftMotor,  rightMotor, bandCenter, bandWidth);
		usPoller = new UltrasonicPoller(usDistance, usData, p);
		final wallReaderDisplay reader = new wallReaderDisplay(p);
		final Navigation navigation = new Navigation(odometer, leftMotor,  rightMotor);
		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString("  Part | Part   ", 0, 2);
			t.drawString("   ONE | TWO    ", 0, 3);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_RIGHT) {
			// initiate controller objects
			odometer.start();
			odometryDisplay.start();
			reader.start();
			usPoller.start();
			(new Thread() {
				public void run() {
					navigation.travelTo(0, 60);
					navigation.travelTo(60, 0);
							
				}
			}).start();
			
		} else {
			 
			odometer.start();
			odometryDisplay.start();

			
			(new Thread() {
				public void run() {
					navigation.travelTo(60, 30);
					navigation.travelTo(30, 30);
					navigation.travelTo(30, 60);
					navigation.travelTo(60, 0);			
				}
			}).start();
		}
		
			
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	
}