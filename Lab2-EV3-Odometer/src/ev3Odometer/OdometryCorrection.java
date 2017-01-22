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
	private double xprev = 0; private double yprev = 0; private double tprev=0;
	private double xnow,ynow,tnow; 
	private double square_length= 30.48;
	private double deltaX, deltaY,deltaT;
		
	// constructor
	public OdometryCorrection(Odometer odometer, EV3ColorSensor lightSensor) {
		this.odometer = odometer;
		this.lightSensor = lightSensor;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		xnow=odometer.getX();
		ynow=odometer.getY();
		tnow=odometer.getTheta();
		deltaX=xnow-xprev;
		deltaY=ynow-yprev;
		deltaT=tnow-tprev;
		
		while (true) {
			correctionStart = System.currentTimeMillis();
			// put your correction code here
			
			// correct our values
			//corrects theta when not turning and XY when turning
			correctValues();
			//corrects the distances X and Y using data from the lightsensor
			SensorCorrect(lightSensor);
			
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
	 * A method to correct our values
	 */
	//corrects theta values when going straight and XY values when turning
	private void correctValues() {
		//if our robot is not rotating then it must be moving forward at a constant angle
		if ( isRobotRotating() ) {
			//robot is rotating, values of x and y should stay the same
			xnow=xprev;
			ynow=yprev;
			odometer.setX(xnow);
			odometer.setY(ynow);
		} else {
			// correct it to one of the 4 possible angles for when robot is not rotating, 
			if ( tnow > -10 && tnow < 10 ) {
				
				tnow=0;
				odometer.setTheta(0);
			} 
			if ( tnow > 80 && tnow < 100 ) {
				
				tnow=90;
				odometer.setTheta(90);
			} 
			if ( tnow > 170 && tnow < 190 ) {
				
				tnow=180;
				odometer.setTheta(180);
			}
			if ( tnow > 260 && tnow < 280 ) {
				
				tnow=270;
				odometer.setTheta(270);
			}
		}
		tnow+=tprev;
	}
	
	/**
	 * A method to determine whether robot is rotating
	 * @return
	 */
	private boolean isRobotRotating() {
		// If absolute changes for both x and y are very small, robot must be rotating
		if ( (Math.abs(xnow-xprev) < 0.1) && (Math.abs(ynow-yprev) < 0.1 )) {
			return true;
		}else{
		return false;
		}
	}
	
	private void SensorCorrect(EV3ColorSensor lightsensor) {
		int data=lightsensor.getColorID();
		if(data==13){
			Sound.beep();
			
		}
	}
	
}