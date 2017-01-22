/* 
 * OdometryCorrection.java
 */
package ev3Odometer;

import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private EV3ColorSensor lightSensor;
	private SampleProvider csColor;

	// constructor
	public OdometryCorrection(Odometer odometer, EV3ColorSensor lightSensor) {
		this.odometer = odometer;
		this.lightSensor = lightSensor;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		

		while (true) {
			correctionStart = System.currentTimeMillis();
			
			// put your correction code here
			
			// correct our values
			correctTheta();
			correctXY();
			

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
	
	/**
	 * A method to correct our theta value
	 */
	private void correctTheta() {
		//if our robot is not rotating then it must be moving forward at a constant angle
		if ( !isRobotRotating() ) {
			// correct it to one of the 4 possible angles for when robot is not rotating, 
			if ( odometer.getTheta() > -10 && odometer.getTheta() < 10 ) {
				odometer.setTheta(0);
			} 
			if ( odometer.getTheta() > 80 && odometer.getTheta() < 100 ) {
				odometer.setTheta(90);
			} 
			if ( odometer.getTheta() > 170 && odometer.getTheta() < 190 ) {
				odometer.setTheta(180);
			}
			if ( odometer.getTheta() > 260 && odometer.getTheta() < 280 ) {
				odometer.setTheta(270);
			}
		}
		
	}
	
	private void correctXY() {
		switch(lightSensor.getColorID()) {
		case(13): Sound.beep();
		break;
		}
	}
	
	
	/**
	 * A method to determine whether robot is rotating
	 * @return
	 */
	private boolean isRobotRotating() {
		// If absolute changes for both x and y are very small, robot must be rotating
		if ( odometer.getX() < Math.abs(0.1) && odometer.getY() < Math.abs(0.1) ) {
			return true;
		}
		return false;
	}
	
}