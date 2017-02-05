/*
 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */
package ev3Localization;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
	final static int FAST = 200, SLOW = 100, ACCELERATION = 500;
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	public Navigation(Odometer odo) {
		this.odometer = odo;

		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/*
	 * Functions to set the motor speeds jointly
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/*
	 * Float the two motors jointly
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position
	 */
	public void travelTo(double x, double y) {
	
		double deltaX = x - odometer.getX();
		double deltaY = y - odometer.getY();
		
		// turn to the minimum angle
		turnTo(odometer.getAng(), true);
		
		// calculate the distance to next point
		double distance  = Math.hypot(deltaX, deltaY);
		
		// move to the next point
		leftMotor.setSpeed(FAST);
		rightMotor.setSpeed(FAST);
		leftMotor.rotate(convertDistance(odometer.getWheelRadius(),distance), true);
		rightMotor.rotate(convertDistance(odometer.getWheelRadius(), distance), false);

		leftMotor.stop(true);
		rightMotor.stop(true);
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

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getAng();

		while (Math.abs(error) > DEG_ERR) {

			error = angle - this.odometer.getAng();

			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}
	
	/*
	 * Go forward a set distance in cm
	 */
	public void goForward(double distance) {
		leftMotor.setSpeed(FAST);
		rightMotor.setSpeed(FAST);
		leftMotor.rotate(convertDistance(odometer.getWheelRadius(),distance), true);
		rightMotor.rotate(convertDistance(odometer.getWheelRadius(), distance), false);

		leftMotor.stop(true);
		rightMotor.stop(true);
	}
	
	/*
	 * Go backward a set distance in cm
	 */
	public void goBackward(double distance) {
		leftMotor.setSpeed(-FAST);
		rightMotor.setSpeed(-FAST);
		leftMotor.rotate(convertDistance(odometer.getWheelRadius(),distance), true);
		rightMotor.rotate(convertDistance(odometer.getWheelRadius(), distance), false);

		leftMotor.stop(true);
		rightMotor.stop(true);
	}
	
	/**
	 * A method to stop our motors
	 */
	public void stop() {
		this.leftMotor.stop();
		this.rightMotor.stop();
	}
	
}
