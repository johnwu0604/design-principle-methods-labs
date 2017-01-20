package myPackage;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

//
// This is the Timed Wall Follower from the NXT updated to run on EV3 hardware.
// Differences between the two implementations are indicated in the comments.
//

public class TimedWF implements TimerListener{

// Class Constants
	
	public static final int SINTERVAL=100;		// A 10Hz sampling rate
	public static final double PROPCONST=1.0;	// Proportionality constant
	public static final int WALLDIST=30; 		// Distance to wall * 1.4 (cm)
	public static final int FWDSPEED=200;		// Forward speed (deg/sec)
	public static final int MAXCORRECTION=50;	// Bound on correction to prevent stalling
	public static final long SLEEPINT=500;		// Display update 2Hz
	public static final int ERRORTOL=1;	        // Error tolerance (cm)
	public static final int MAXDIST=200;        // Max value of valid distance
	public static final int ID_ESCAPE=32;		// Value returned when ESCAPE key pressed
	
// Class Variables
	public static int wallDist=0;			    // Measured distance to wall
	public static int distError=0;			    // Error
	public static int leftSpeed=FWDSPEED;	    // Vehicle speed 
	public static int rightSpeed=FWDSPEED;

// Objects instanced once by this class

	static TextLCD t = LocalEV3.get().getTextLCD();
	static RegulatedMotor leftMotor = Motor.A;
	static RegulatedMotor rightMotor = Motor.D;
	
// leJOS EV3 uses a new scheme for doing sensor I/O

// 1. Get a port instance for each sensor used 

	static Port portUS = LocalEV3.get().getPort("S1");
	static Port portTouch = LocalEV3.get().getPort("S2");
	
// 2. Get an instance for each sensor
	
	static SensorModes myUS = new EV3UltrasonicSensor(portUS);
	static SensorModes myTouch = new EV3TouchSensor(portTouch);
	
// 3. Get an instance of each sensor in measurement mode
	
	static SampleProvider myDistance = myUS.getMode("Distance");
	static SampleProvider myTouchStatus = myTouch.getMode(0);
	
// 4. Allocate buffers for data return
	
	static float[] sampleUS = new float[myDistance.sampleSize()];
	static float[] sampleTouch = new float[myTouchStatus.sampleSize()];
	
// Class entry point (main) starts here
	
	public static void main(String[] args) throws InterruptedException {
		
		boolean noexit;
		int status;
		
// Set up the display area
		
		t.clear();
		t.drawString("Wall Follower", 0, 0);
		t.drawString("Distance:", 0, 4);
		t.drawString("L Speed:", 0, 5);
		t.drawString("R Speed:", 0, 6);
		t.drawString("Error:", 0, 7);
		
// Set up timer interrupts
		
		Timer myTimer = new Timer(SINTERVAL, new TimedWF());
		
// Start the cart rolling forward at nominal speed
		
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
		leftMotor.forward();
		rightMotor.forward();
		distError=0;
		
// Enable the exception handler
		
		myTimer.start();
		
// There are two threads in operation, Main and the timer exception
// handler.  Main continuously updates the display and checks for
// an abort from the user.
//
	
		noexit=true;

		while(noexit) {
			status=Button.readButtons();			   // Check for press on console
			myTouchStatus.fetchSample(sampleTouch,0);  // Or a collision detected by the touch sensor
			if ((status==Button.ID_ENTER)||(sampleTouch[0]==1)) {
				System.exit(0);
			}
			
// Update status on LCD
			
			t.drawInt(wallDist,5,11,4);				// Display key parameters on LCD
			t.drawInt(leftSpeed,4,11,5);
			t.drawInt(rightSpeed,4,11,6);
			t.drawInt(distError,4,11,7);
			
			Thread.sleep(SLEEPINT);					// Have a short nap
		}
				
	}
	

//
// The servo (control) loop is implemented in the timer handler (listener).  Version 0.90 of
// leJOS EV3 has a bug in the servo code.  A motion command has to follow setSpeed in order
// for the new set point to register.  Hopefully this will get fixed in later versions.
// 

	public void timedOut() {
		
		int diff;

		myDistance.fetchSample(sampleUS,0);			// Read latest sample in buffer 
		wallDist=(int)(sampleUS[0]*100.0);			// Convert from MKS to CGS; truncate to int 
		if (wallDist <= MAXDIST)
			distError = WALLDIST-wallDist;			// Compute error term 
		
		// Controller Actions 
		
		if (Math.abs(distError) <= ERRORTOL) {		// Case 1: Error in bounds, no correction 
			leftSpeed=FWDSPEED;
			rightSpeed=FWDSPEED;
			leftMotor.setSpeed(leftSpeed);			// If correction was being applied on last 
			rightMotor.setSpeed(rightSpeed);		// update, clear it 
			leftMotor.forward();					// Hack - leJOS bug 
			rightMotor.forward();
		}
		
		else if (distError > 0) {					// Case 2: positive error, move away from wall 
			diff=calcProp(distError);				// Get correction value and apply 
			leftSpeed=FWDSPEED+diff;
			rightSpeed=FWDSPEED-diff;
			leftMotor.setSpeed(leftSpeed);
			rightMotor.setSpeed(rightSpeed);
			leftMotor.forward();					// Hack - leJOS bug 
			rightMotor.forward();
		}
		
		else if (distError < 0) {					// Case 3: negative error, move towards wall 
			diff=calcProp(distError);				// Get correction value and apply 
			leftSpeed=FWDSPEED-diff;
			rightSpeed=FWDSPEED+diff;
			leftMotor.setSpeed(leftSpeed);
			rightMotor.setSpeed(rightSpeed);
			leftMotor.forward();					// Hack - leJOS bug 
			rightMotor.forward();			
		}
	}
	
//
// This method is used to implement your particular control law.  The default
// here is to alter motor speed by an amount proportional to the error.  There
// is some clamping to stay within speed limits.
 
	
	int calcProp (int diff) {
		
		int correction;

// PROPORTIONAL:  Correction is proportional to magnitude of error 

		if (diff < 0) diff=-diff;
		correction = (int)(PROPCONST *(double)diff);
		if (correction >= FWDSPEED) correction = MAXCORRECTION;
		
		return correction;	

	}
}
