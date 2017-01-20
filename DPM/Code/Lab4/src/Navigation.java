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


//Tiffany Wang 260684152
//Matthew Rodin 260623844
//Group 22



import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
	final static int FAST = 200, SLOW = 50, ACCELERATION = 4000;
	final static double DEG_ERR = 4.0, CM_ERR = 1.0;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor; 
	private final int FORWARD_SPEED  = 100;
	private final double radius = 2.1;

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
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
	 * constantly updating it's heading
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			this.setSpeeds(FAST, FAST);
		}
		this.setSpeeds(0, 0);
	}

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getAng();

		do{

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
		}while (Math.abs(error) > DEG_ERR);
		stop = true;
		if (stop) {
			this.setSpeeds(0, 0);
		}
	}
	
	//set the robot to rotate continuously 
	public void rotate(int ROTATION_SPEED){
		if(ROTATION_SPEED > 0){
			leftMotor.setSpeed(ROTATION_SPEED+1);
			rightMotor.setSpeed(ROTATION_SPEED);
			leftMotor.forward();
			rightMotor.backward();
		}else{
			leftMotor.setSpeed(ROTATION_SPEED+1);
			rightMotor.setSpeed(ROTATION_SPEED);
			leftMotor.backward();
			rightMotor.forward();
		}
	}
	
	/*
	 * Go foward a set distance in cm
	 */

	//set the robot to go forward for a certain distance 
	public void goForward(double distance){
		this.leftMotor.setSpeed(FORWARD_SPEED);
		this.rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(radius, distance), true);
		rightMotor.rotate(convertDistance(radius, distance), false);
	}
	
	
	//set the robot to go forward continuously 
	public void goForward(){
		this.leftMotor.setSpeed(FORWARD_SPEED);
		this.rightMotor.setSpeed(FORWARD_SPEED);
		this.leftMotor.forward();
		this.rightMotor.forward();
	}
	
	
	//stops the motors
	public void stopping(){
		this.setSpeeds(0, 0);
		leftMotor.stop(true);
		rightMotor.stop(false);
	}
	
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}