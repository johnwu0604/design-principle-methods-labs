package ev3BallLauncher;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
	
	// vehicle objects
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	// variables used for determining position
	private double x, y, theta, track, wheel_circum;
	private int currentLeftMotorTachoCount, currentRightMotorTachoCount,
				prevLeftMotorTachoCount, prevRightMotorTachoCount;
	
	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer(EV3LargeRegulatedMotor leftMotor,EV3LargeRegulatedMotor rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.x = 0.0;
		this.y = 0.0;
		this.theta = 0.0;
		this.currentLeftMotorTachoCount = 0;
		this.currentRightMotorTachoCount = 0;
		this.prevLeftMotorTachoCount = 0;
		this.prevRightMotorTachoCount = 0;
		this.track = Lab5.TRACK;
		this.wheel_circum = Math.PI*2*Lab5.RADIUS;
		lock = new Object();
	}

	/**
	 * Our main method
	 */
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			
			// Get current tachometer values
			currentLeftMotorTachoCount = leftMotor.getTachoCount();
			currentRightMotorTachoCount = rightMotor.getTachoCount();
			
			// Compare it with the previous value to get the change
			int leftDeltaTacho = currentLeftMotorTachoCount - prevLeftMotorTachoCount;
			int rightDeltaTacho = currentRightMotorTachoCount - prevRightMotorTachoCount;
			
			// Use our change in rotation values to calculate displacement of each wheel
			double leftMotorDisplacement = wheel_circum*leftDeltaTacho/360;
			double rightMotorDisplacement = wheel_circum*rightDeltaTacho/360;
			
			// angle at which our vehicle changed
			double thetaChange = ( leftMotorDisplacement - rightMotorDisplacement )/track;
			// change in distance of our vehicle
			double displacement = ( leftMotorDisplacement + rightMotorDisplacement )/2;
			
			prevLeftMotorTachoCount = currentLeftMotorTachoCount;
			prevRightMotorTachoCount = currentRightMotorTachoCount;
			

			synchronized (lock) {
				/**
				 * Don't use the variables x, y, or theta anywhere but here!
				 * Only update the values of x, y, and theta in this block. 
				 * Do not perform complex math
				 * 
				 */
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

	/**
	 * A method to get the position of our vehicle
	 * 
	 * @param position
	 * @param update
	 */
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				if(theta>=0) {
	  				position[2] = ( theta * 360 / ( 2 * Math.PI ) ) % 360;
	 			} else {
	 			    position[2] = (( theta * 360 / ( 2 * Math.PI ) ) % 360)+360;
	 			}
		}
	}

	/**
	 * A method to get the x-coordinate of the our vehicle position
	 * 
	 * @return
	 */
	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	/**
	 * A method to get the y-coordinate of the our vehicle position
	 * 
	 * @return
	 */
	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	/**
	 * A method to get the theta of the our vehicle position
	 * 
	 * @return
	 */
	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	/**
	 * A method to set the position of our vehicle
	 * 
	 * @param position
	 * @param update
	 */
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

	/**
	 * A method to set the x-coordinate of our vehicle
	 * 
	 * @param x
	 */
	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	/**
	 * A method to set the y-coordinate of our vehicle
	 * 
	 * @param y
	 */
	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	/**
	 * A method to set the theta position of our vehicle
	 * 
	 * @param theta
	 */
	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}

	/**
	 * @return the currentLeftMotorTachoCount
	 */
	public int getCurrentLeftMotorTachoCount() {
		return currentLeftMotorTachoCount;
	}

	/**
	 * @param currentLeftMotorTachoCount the currentLeftMotorTachoCount to set
	 */
	public void setCurrentLeftMotorTachoCount(int currentLeftMotorTachoCount) {
		synchronized (lock) {
			this.currentLeftMotorTachoCount = currentLeftMotorTachoCount;	
		}
	}

	/**
	 * @return the currentRightMotorTachoCount
	 */
	public int getCurrentRightMotorTachoCount() {
		return currentRightMotorTachoCount;
	}

	/**
	 * @param currentRightMotorTachoCount the currentRightMotorTachoCount to set
	 */
	public void setCurrentRightMotorTachoCount(int currentRightMotorTachoCount) {
		synchronized (lock) {
			this.currentRightMotorTachoCount = currentRightMotorTachoCount;	
		}
	}
}