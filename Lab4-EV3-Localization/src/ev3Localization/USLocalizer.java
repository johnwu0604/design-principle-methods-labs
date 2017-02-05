package ev3Localization;

import lejos.robotics.SampleProvider;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static int ROTATION_SPEED = 80;
	private static final double DISTANCE_WALL = 30, NOISE_MARGIN = 3;

	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private Navigation navigation;
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.navigation = new Navigation(odo);
	}
	
	public void doLocalization() {
		
		double angleA, angleB, angleNorth;
		
		if (locType == LocalizationType.FALLING_EDGE) {
			// get angles A and B for falling edge
			angleA = getAngleAFallingEdge();
			angleB = getAngleBFallingEdge();
			
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			// get angles A and B for rising edge
			angleA = getAngleARisingEdge();
			angleB = getAngleBRisingEdge();
		}
		// calculate our north angle
		angleNorth = getAngleNorth( angleA, angleB );
		
		odo.setPosition(new double [] {0.0, 0.0, odo.getAng()+angleNorth}, new boolean [] {false, false, true});
		navigation.turnTo(0,true);
	}
	
	/**
	 * A method which filters our data for the distance
	 * 
	 * @return
	 */
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0]*100;	
		return distance > 100 ? 100 : distance;
	}
	
	/**
	 * A method to retrieve the angle A of the localization on the falling edge
	 * Angle A - Angle where sensor first detects the east oriented (0 degrees) wall
	 * 
	 * @return double angleA
	 */
	private double getAngleAFallingEdge() {
		// rotate our robot clockwise until it doesn't see a wall anymore, then keep rotating until a wall is detected
		while ( getFilteredData() < DISTANCE_WALL + NOISE_MARGIN ) {
			navigation.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);
		}
		while ( getFilteredData() > DISTANCE_WALL ) {
			navigation.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);
		}
		// stop the motors and return the angle
		navigation.stop();
		return odo.getAng();
	}
	
	/**
	 * A method to retrieve the angle B of the localization on the falling edge
	 * Angle B - Angle where sensor first detects the north oriented (90 degrees) wall
	 * 
	 * @return double angleA
	 */
	private double getAngleBFallingEdge() {
		// rotate vehicle counterclockwise until no wall is detected, then keep rotating until a wall is detected
		while ( getFilteredData() < DISTANCE_WALL + NOISE_MARGIN ) {
			navigation.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
		}
		while ( getFilteredData() > DISTANCE_WALL ) {
			navigation.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
		}
		// stop the motors and return the angle
		navigation.stop();
		return odo.getAng();
	}
	
	/**
	 * A method to retrieve the angle A of the localization on the rising edge
	 * Angle A - Angle where sensor first detects the east oriented (0 degrees) wall
	 * 
	 * @return double angleA
	 */
	private double getAngleARisingEdge() {
		// rotate our robot counterclockwise until it sees a wall, then keep rotating until it doesn't see the wall anymore
		while ( getFilteredData() > DISTANCE_WALL - NOISE_MARGIN ) {
			navigation.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
		}
		while ( getFilteredData() < DISTANCE_WALL ) {
			navigation.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
		}
		// stop the motors and return the angle
		navigation.stop();
		return odo.getAng();
	}
	
	/**
	 * A method to retrieve the angle B of the localization on the rising edge
	 * Angle B - Angle where sensor first detects the north oriented (90 degrees) wall
	 * 
	 * @return double angleA
	 */
	private double getAngleBRisingEdge() {
		// rotate vehicle clockwise a wall is detected, then keep rotating until it doesn't see the wall anymore
		while ( getFilteredData() > DISTANCE_WALL - NOISE_MARGIN ) {
			navigation.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);
		}
		while ( getFilteredData() < DISTANCE_WALL ) {
			navigation.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);
		}
		// stop the motors and return the angle
		navigation.stop();
		return odo.getAng();
	}
	
	/**
	 * A method to determine the angle of the north orientation
	 * 
	 * @param double angleA
	 * @param double angleB
	 * @return double angleNorth
	 */
	private double getAngleNorth( double angleA, double angleB ) {
		double angleNorth = 0;
		if(angleA > angleB){
			angleNorth = 225 - (angleA + angleB)/2.0;
		} else {
			angleNorth = 45 - (angleA + angleB)/2.0;
		}
		return angleNorth;
	}

}
