package ev3Localization;

import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private Odometer odo;
	private EV3ColorSensor lightSensor;
	private Navigation navigation;
	private static int FORWARD_SPEED = 100;
	private static double SENSOR_DISTANCE = 7;
	
	public LightLocalizer(Odometer odo, EV3ColorSensor lightSensor) {
		this.odo = odo;
		this.lightSensor = lightSensor;
		this.navigation = new Navigation(odo);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
		// move to our estimated origin
		moveToOriginEstimate(); 

	}
	
	/**
	 * A method which moves our vehicle to the estimated origin after localizing to 0 degrees
	 */
	private void moveToOriginEstimate() {
		// turn towards corner, and move backwards until sensor reads a line
		navigation.turnTo(225, true);
		while ( !(lightSensor.getColorID() == 13) ){
			navigation.setSpeeds(-FORWARD_SPEED, -FORWARD_SPEED);
		}
		Sound.beep();
 		navigation.stop();
 		navigation.goForward(SENSOR_DISTANCE);
	}

}
