/*
 * //Tiffany Wang 260684152
* //Matthew Rodin	260623844 
* //Group 22
 

* 
* OdometryCorrection.java
 */
package ev3Odometer;

import lejos.ev3.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.hardware.*;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10; 
	private static final double distSensorWheel = 6.5; // difference between sensor and wheels
	private Odometer odometer;
	private double ynow=0;
	private double xnow=0;
	private double ylast = 0;
	private double xlast = 0;
	private double tnow;
	private double tlast = 0;
	private double xlinelast = 0;; 
	private double deltaX;
	private double ylinelast = 0;
	private double deltaY;
	private double tlinelast = 0;
	private double deltaT;
	private EV3ColorSensor lightSensor;
	private final double square = 30.48;
	private int countery = 0;
	private int counterx = 0;
	private int counterT = 0;
	// constructor
	public OdometryCorrection(Odometer odometer, EV3ColorSensor lightSensor) {
		this.odometer = odometer;
		this.lightSensor = lightSensor;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		float[] sampleRed = {0};
		
		
		while (true) {
			correctionStart = System.currentTimeMillis();
			lightSensor.getRedMode().fetchSample(sampleRed, 0);
			// first get the readings of x, y and theta (degrees)
			xnow = this.odometer.getX();
			ynow = this.odometer.getY();
			tnow = this.odometer.getTheta()*180 / Math.PI;
			
			
			//if the change in x and y are both minimal, it means that the robot is rotating. 
			//so there should be no change in x and y.
			if(Math.abs(xlast - xnow) < 0.1 && Math.abs(ylast - ynow) < 0.1){
				xnow = xlast;
				ynow = ylast;
				this.odometer.setX(xnow);
				this.odometer.setY(ynow);
			}else{
				//if the change in x and y are significant we want to make sure that the value of Theta is good. 
				// we correct theta by giving it a error margin of 10 degrees. 
				if((tnow > -5 && tnow < 15) && (tnow < -2 || tnow > 2)){
					tnow = 0;
					this.odometer.setTheta(0);
				}else if((tnow > 150 && tnow < 190) && (tnow < 178 || tnow > 182)) {
					tnow = 180;
					this.odometer.setTheta(Math.PI);
				}if((tnow > 70 && tnow < 110) && (tnow < 88 || tnow > 92)){
					tnow = 90;
					this.odometer.setTheta(Math.PI/2.0);
				}else if((tnow > 250 && tnow < 295) && (tnow < 268 || tnow > 272)) {
					tnow = 270;
					this.odometer.setTheta(1.5*Math.PI);
				}
			}
			
			//correct the Theta 
			tnow = tlast + (tnow-tlast)*90/82;
			// in the setting, the left wheel turns slightly more than the right wheel 
			// therefore, when calculating theta, the value would be bigger 
			// we correct it everytime. 
			this.odometer.setTheta(tnow/180 * Math.PI); 
			

			if((tnow > 340 && tnow < 15) && (tnow < 355 || tnow > 5)) {
				tnow = 360;
				this.odometer.setTheta(2.0*Math.PI);
			}// at the end we want to correct the angle to 360. 
			
			//when the robot is moving on the y axis or on the x axis 
			//(each direction corresponds to a specific angle) 
			// we make sure that the odometer the correct value of x and y
			
			//we correct the displacement of x and y with a coefficient
			if((tnow > -3 && tnow < 15) || (tnow > 150 && tnow < 190)){
				if ((tnow - tlast) < 5){
					this.odometer.setX(xlast); 
				}
				ynow = ylast + (ynow - ylast)*1.07;
				this.odometer.setY(ynow);
				ylast = ynow;
			}else if((tnow > 70 && tnow < 110) || (tnow > 250 && tnow < 295)){
				if ((tnow - tlast) < 5){
					this.odometer.setY(ylast);
				}
				xnow = xlast + (xnow - xlast)*1.05;
				this.odometer.setX(xnow);
				xlast = xnow;
			}
			
			// each line reading is a reference of the distance traveled by the robot
			if (sampleRed[0]*100 < 35){
				Sound.beep();
				//we calculate the change in x , y  and theta from one reading to another
				deltaX = xnow - xlinelast;  
				deltaY = ynow - ylinelast;
				deltaT = tnow - tlinelast;
				
				if(deltaT > 70){
					counterx = 0;
					countery = 0; 
				}
				
				//when the angle is 0 or 180, the robot is traveling on the y axis, thus X is constant. 
				// we correct the y coordinate by half a square or a square 
				// we only want the subtract the distSensor once. 
				//the correction depends on the direction of the robot. 
				if((tnow > -5 && tnow < 20) || (tnow > 160 && tnow < 190)){
			
					
					if (tnow > -5 && tnow < 20){
						if(countery == 0){
							ynow = ylinelast + square / 2.0 - distSensorWheel; 
							this.odometer.setY(ynow);
							countery++;
						}else{ 
							this.odometer.setY(ynow);
							ynow = ylinelast + square;
						}
					}else{
						if(countery == 0){
							ynow = ylinelast - square/2.0 + distSensorWheel;
							this.odometer.setY(ynow);
							countery++;
						}else{
							ynow = ylinelast - square;
							this.odometer.setY(ynow);	
						}
					}
					//when the angle is 90 or 270, the robot is traveling on the X axis, thus y is constant. 
					// we correct the x coordinate by half a square or a square 
				}else if ((tnow > 75 && tnow < 110) || (tnow > 255 && tnow < 290)){					
					// we only want to subtract half a square once
					if (tnow > 85 && tnow < 100){
						if(counterx == 0){
							xnow = xlinelast + (square / 2.0) - distSensorWheel; 
							this.odometer.setX(xnow);
							counterx++;
						}else{ 
							xnow = xlinelast + square;
							this.odometer.setX(xnow);
						}
					}else{
						if(counterx == 0){
							xnow = xlinelast - square/2.0 + distSensorWheel;
							this.odometer.setX(xnow);
							counterx++;
						}else{
							xnow = xlinelast - square;
							this.odometer.setX(xnow);	
						}
					}
				}
				//once the robot makes a rotation, we want to reset the counter in order to correct the distance traveled 
				//by distSensorWheel.  
			
				
				ylinelast = ynow; 
				xlinelast = xnow;
				tlinelast = tnow;
			}
			
			
			tlast = tnow;

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
	
}