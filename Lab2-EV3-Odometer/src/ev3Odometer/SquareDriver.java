/*
 * SquareDriver.java
 */
package ev3Odometer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class SquareDriver {
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 100;
	private static final double SQUARE_LENGTH = 30.48;
	private static final double PATH= 2*SQUARE_LENGTH;
	//left radius too big turns too much, final rotated counterclockwise from initial
	public static final double LEFT_WHEEL_RADIUS = 2.090;
	public static final double RIGHT_WHEEL_RADIUS = 2.093;
	public static final double TRACK = 15.8;

	public static void drive(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		
		// reset the motors
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(1500);
		}

		// wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected that
			// the odometer will be interrupted by another thread
		}

		for (int i = 0; i < 4; i++) {
			// drive forward two tiles
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);

			leftMotor.rotate(convertDistance(LEFT_WHEEL_RADIUS, PATH), true);
			rightMotor.rotate(convertDistance(RIGHT_WHEEL_RADIUS, PATH), false);

			// turn 90 degrees clockwise
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);

			leftMotor.rotate(convertAngle(LEFT_WHEEL_RADIUS, TRACK, 90.0), true);
			rightMotor.rotate(-convertAngle(RIGHT_WHEEL_RADIUS, TRACK, 90.0), false);
		}
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double TRACK, double angle) {
		return convertDistance(radius, Math.PI * TRACK * angle / 360.0);
	}
}