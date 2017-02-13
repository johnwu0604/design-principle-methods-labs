package ev3BallLauncher;

import ev3BallLauncher.Lab5;
import ev3BallLauncher.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigator {
	 
	// vehicle objects
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	// vehicle constants
    private final double RADIUS, TRACK;
	
	// speed constants
	private static final int FORWARD_SPEED = 250, ROTATE_SPEED = 100, MOTOR_ACCELERATION = 200;

	public Navigator(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Odometer odometer) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odometer = odometer;
		this.RADIUS = Lab5.RADIUS;
		this.TRACK = Lab5.TRACK;
	}
	
	/**
	 * A method to turn our vehicle to a certain angle
	 * 
	 * @param theta
	 */
	public void turnTo(double theta) {
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
	 * Calculates the minimum angle to turn to
	 * 
	 * @param x
	 * @param y
	 * 
	 * @return double minAngle
	 */
	public double calculateMinAngle(double deltaX, double deltaY) {
		// calculate the minimum angle
		return Math.atan2( deltaX, deltaY) - odometer.getTheta();
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
	
}
