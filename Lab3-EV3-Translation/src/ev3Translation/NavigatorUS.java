package ev3Translation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class NavigatorUS extends Thread implements UltrasonicController {

	private static Odometer odometer;
	private static EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	private final double RADIUS, TRACK;
	
	private int distance;
	private int filterControl;
	
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 100;
	private static final double SQUARE_LENGTH = 30.48;
	private static final int FILTER_OUT = 20;

	public NavigatorUS(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Odometer odometer) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odometer = odometer;
		this.RADIUS = Lab3.RADIUS;
		this.TRACK = Lab3.TRACK;
	}
	
	public int readUSDistance() {
		return this.distance;
	}
	
	@Override
	public void processUSData(int distance) {
		// rudimentary filter - toss out invalid samples corresponding to null
		// signal.
		// (n.b. this was not included in the Bang-bang controller, but easily
		// could have).
		//
		if (distance >= 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance >= 255) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;
		} else {
			// distance went below 255: reset filter and leave
			// distance alone.
			filterControl = 0;
			this.distance = distance;
		}
		
		/**
		 * TODO: Getting around wall
		 * 
		 * Potential Method 1 (Easier)
		 * 
		 * 1) Turn 90 degrees, move a constant distance
		 * 2) Turn back 90 degrees, move a constant distance
		 * 3) Turn 90 degrees again, move a constant distance
		 * 4) Continue with path to destination
		 * 
		 * Potential Method 2 (Harder)
		 * 
		 * 1) When wall approaches, turn vehicle 90 degrees and turn sensor to face the wall.
		 * 2) Keep driving forward until sensor reads that there is no more wall in the way.
		 * 3) Turn 90 degrees back and repeat.
		 * 4) Then drive directly towards the wall.
		 */
		
		
	}
	
	public void run() {
		//reset motors
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(200);
		}
		// travel to coordinates
		travelTo(0, 60);
		travelTo(60, 0);
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double TRACK, double angle) {
		return convertDistance(radius, Math.PI * TRACK * angle / 360.0);
	}
	
	/**
	 * A method to drive our vehicle to a certain cartesian coordinate
	 * 
	 * @param x X-Coordinate
	 * @param y Y-Coordinate
	 */
	private void travelTo(double x, double y) {
		double deltaX = x - odometer.getX();
		double deltaY = y - odometer.getY();
		
		// calculate the minimum angle
		double minAngle = Math.atan2( deltaX, deltaY) - odometer.getTheta();
					
		// turn to the minimum angle
		turnTo(minAngle);
		
		// calculate the distance to next point
		double distance  = Math.hypot(deltaX, deltaY);
		
		// move to the next point
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(RADIUS,distance), true);
		rightMotor.rotate(convertDistance(RADIUS, distance), false);

		leftMotor.stop(true);
		rightMotor.stop(true);
	}
	
	/**
	 * A method to turn our vehicle to a certain angle
	 * 
	 * @param theta
	 */
	private void turnTo(double theta) {
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		if(theta < 0) { // if angle is negative, turn to the left
			leftMotor.rotate(-convertAngle(RADIUS, TRACK, -(theta*180)/Math.PI), true);
			rightMotor.rotate(convertAngle(RADIUS, TRACK, -(theta*180)/Math.PI), false);
		} 
		else { // angle is positive, turn to the right
			leftMotor.rotate(convertAngle(RADIUS, TRACK, (theta*180)/Math.PI), true);
			rightMotor.rotate(-convertAngle(RADIUS, TRACK, (theta*180)/Math.PI), false);
		}
		
	}
	
	/**
	 * A method to determine whether another thread has called travelTo and turnTo methods or not
	 * 
	 * @return
	 */
	private boolean isNavigating() {
		return false; // TODO
	}

	
}