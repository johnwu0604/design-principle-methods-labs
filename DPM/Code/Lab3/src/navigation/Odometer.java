//Lab 3
//Tiffany Wang 260684152
//Matthew Rodin 260623844
//Group 22
package navigation;
/*
//Tiffany Wang 260684152
//Matthew Rodin	260623844
//Group 22

* Odometer.java
 */



import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;
	
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	
	private double lastTachoRight = 0;
	private double lastTachoLeft = 0;
	private double currentTachoRight ;
	private double currentTachoLeft ;
	
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 13.5;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		//theta = -0.7376;
		lock = new Object();
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}
	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis(); //returns the current time in milliseconds 
			
			//retrieving last tachometer values for left and right motors
			this.currentTachoLeft = leftMotor.getTachoCount();

			this.currentTachoRight = rightMotor.getTachoCount();

			
			// Calculating the change in rotation of each wheel
			double deltaTachoLeft = (currentTachoLeft - lastTachoLeft);
			double deltaTachoRight = (currentTachoRight - lastTachoRight);
			
			//calculate left and right motor distances
			double distanceLeft = deltaTachoLeft*Math.PI/180*WHEEL_RADIUS ;
			double distanceRight = deltaTachoRight*(Math.PI/180)*WHEEL_RADIUS ;
			
			//Update for next cycle
			this.lastTachoLeft = currentTachoLeft;
			this.lastTachoRight = currentTachoRight;
			
			//calculate the angle change of the robot
			double thetaChange = (distanceLeft - distanceRight )/TRACK * 0.93;
			
			//calculate displacement of robot
			double displacement = (distanceRight+distanceLeft)/2;

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				//theta = -0.7376;
				
				theta += thetaChange;
	
				
				x += displacement*Math.sin(theta);
				y += displacement*Math.cos(theta);
				
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = (theta*180 / Math.PI)%360;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}