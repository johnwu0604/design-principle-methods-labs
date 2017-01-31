package ev3Translation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class NavigatorUS extends Thread implements UltrasonicController {

	// vehicle variables
	private static Odometer odometer;
	private static EV3LargeRegulatedMotor leftMotor, rightMotor, sensorMotor;
	private final double RADIUS, TRACK;
	
	// navigation variables
	private static final int FORWARD_SPEED = 250, ROTATE_SPEED = 100;
	private static final int MOTOR_ACCELERATION=400;
	private static boolean isNavigating = true;
	
	 
	// variables to store sensor data
	private int distance, filterControl;
	
	// wall follower variables
	private static final int motorLow = 100, motorHigh = 200, bandCenter = 12, bandwidth = 3, FILTER_OUT = 20;

	// obstacle avoidance variables
	private double initialAngleAtBlock;
	private static boolean hasBlockPassed = false;
	
	double destinationX;
	double destinationY;

	public NavigatorUS(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,  EV3LargeRegulatedMotor sensorMotor,
			Odometer odometer) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.sensorMotor = sensorMotor;
		this.odometer = odometer;
		this.RADIUS = Lab3.RADIUS;
		this.TRACK = Lab3.TRACK;
	}
	
	/**
	 * Reads our sensor distance
	 */
	public int readUSDistance() {
		return this.distance;
	}
	
	/**
	 * Processes our sensor data and acts accordingly based on its readings
	 */
	@Override
	public void processUSData(int distance) {
		
		// filter out invalid samples of data
		filterData(distance);
		
		// will be true if we are in the process of getting around obstacle
		if ( !isNavigating() && !hasBlockPassed ) {
			// this means we have passed our object, continue to destination
			if (initialAngleAtBlock - odometer.getTheta() >= Math.PI/2) {
				hasBlockPassed = true;
				leftMotor.stop(true);
				rightMotor.stop(true);
				//reset motors
				for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
					motor.stop();
					motor.setAcceleration(MOTOR_ACCELERATION);
				}
				travelTo(destinationX,destinationY);
				return;
			}
			// otherwise execute our wall follow logic
			excecuteWallFollow();
		} else {
			if ( distance < 18 ) {
				leftMotor.stop(true);
				rightMotor.stop(true);
				initialAngleAtBlock = odometer.getTheta();
				prepareForWallFollower();
			}
		}
		
		
	}
	
	/**
	 * Our main run method
	 * 
	 */
	public void run() {
		//reset motors
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(MOTOR_ACCELERATION);
		}
		// travel to coordinates
		travelTo(0, 60);
		travelTo(60, 0);
	}
	
	/**
	 * Determine how much the motor must rotate for vehicle to reach a certain distance
	 * 
	 * @param radius
	 * @param distance
	 * @return
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * Determine the angle our motors need to rotate in order for vehicle to turn a certain angle 
	 * 
	 * @param radius
	 * @param TRACK
	 * @param angle
	 * @return
	 */
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
		this.destinationX=x;
		this.destinationY=y;
		isNavigating = true;
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
		isNavigating = false;
	}
	
	/**
	 * A method to turn our vehicle to a certain angle
	 * 
	 * @param theta
	 */
	private void turnTo(double theta) {
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
				
		if(theta < 0) { // if angle is negative, turn counter clockwise
			leftMotor.rotate(-convertAngle(RADIUS, TRACK, (theta*180)/Math.PI), true);
			rightMotor.rotate(convertAngle(RADIUS, TRACK, (theta*180)/Math.PI), false);
		} 
		else { // angle is positive, turn clockwise
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
		return isNavigating; 
	}
	
	
	/**
	 * A method to filter out invalid samples of data
	 */
	private void filterData(int distance) {
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
	}
	
	/**
	 * A method to that implements our wall following logic
	 */
	private void excecuteWallFollow() {
		// calculate our offset from the bandCenter
		int error = distance - bandCenter - 5; // -5 for distance from sensor to side of vehicle
		
		// Keep moving forward if vehicle is within threshold value
		if ( Math.abs(error) < this.bandwidth ) {
			steerStraight();
		} 
		// We are too close to the wall, steer vehicle to the right
		else if ( error < 0 ) {
			steerRight(); 
		} 
		// We are too far away from the wall
		else { 
			if ( error > 100 ) {
				// It is just sensing something very far away, keep going straight
				steerStraight(); 
			} else {
				// We are too far from the wall, steer left
				steerLeft();
			}
		}
	}
	
	
	/**
	 * A method to turn our vehicle
	 */
	private void prepareForWallFollower() {
		isNavigating = false;
		turnTo(Math.PI/2); // turn our angle 90 degrees
		sensorMotor.setSpeed(100);					
		sensorMotor.rotate(-110); // turn our sensor toward the wall
	}
	
	/**
	 * Method to steer the vehicle in a straight forward direction
	 */
	public void steerStraight() {
		leftMotor.setSpeed(motorHigh);			
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	/**
	 * Method to steer the vehicle right
	 */
	public void steerRight() {
		leftMotor.setSpeed(motorHigh);			
		rightMotor.setSpeed(motorLow);
		leftMotor.forward();
		rightMotor.forward();
	}
	
    /**
     * Method to steer the vehicle left
     */
	public void steerLeft() {
		leftMotor.setSpeed(motorLow);			
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
	}

	
}