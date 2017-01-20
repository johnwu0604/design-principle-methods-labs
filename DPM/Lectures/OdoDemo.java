package odoDemo;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

//
// A simple odometry demonstration using the EV3 controller
//

public class OdoDemo implements TimerListener {

// Class Constants
	
public static final int SINTERVAL=50;	// Period of sampling f (mSec)
public static final int SLEEPINT=500;	// Period of display update (mSec)
public static final double WB=16.0;		// Wheelbase (cm)
public static final double WR=2.7;		// Wheel radius (cm)

// Class Variables

public static int lastTachoL;			// Tacho L at last sample
public static int lastTachoR;			// Tacho R at last sample 
public static int nowTachoL;			// Current tacho L
public static int nowTachoR;			// Current tacho R
public static double X;					// Current X position
public static double Y;					// Current Y position
public static double Theta;				// Current orientation

// Resources

static TextLCD t = LocalEV3.get().getTextLCD();		     							// LCD
static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);  // L motor
static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.D); // R motor

// Main entry point
	
	public static void main(String[] args) throws InterruptedException {
		
		boolean noexit;
		int status;
		
// Set up display area
		
		t.clear();
		t.drawString("Odometer Demo",0,0,false);
	    t.drawString("Current X  ",0,4,false);
	    t.drawString("Current Y  ",0,5,false);
	    t.drawString("Current T  ",0,6,false);
	    
// Set up timer interrupt.  Creates a thread which services the timer interrupt.  Here
// control will be transfered to OdoDemo.timedOut().
	    
	    Timer myTimer = new Timer(SINTERVAL,new OdoDemo());
	    
// Create thread to move the cart simultaneously with odometer operation
	    
	    doSquare cart = new doSquare(leftMotor, rightMotor);
	    
// Clear tacho counts and put motors in freewheel mode.  Then initialize tacho count
// variable to its current state.
	    
	    leftMotor.resetTachoCount();
	    rightMotor.resetTachoCount();
	    lastTachoL=leftMotor.getTachoCount();
	    lastTachoR=rightMotor.getTachoCount();
	    
// Enable timer interrupts (i.e. start the odometer)
	    
	    myTimer.start();
	    
// Start the cart moving
	    
	    cart.start();
	    	    
// Enter display loop.  Terminate on any button push.
	    
	    noexit=true;
	    
	    while(noexit) {
	    	status=Button.readButtons();					// Terminate on ENTER button
	    	if (status==Button.ID_ENTER) {
	    		System.exit(0);
	    	}
	    	t.drawInt((int)X,4,11,4);						// Current X estimate
	    	t.drawInt((int)Y,4,11,5);						// Current Y estimate
	    	t.drawInt((int)(Theta*57.2598),4,11,6);			// Current heading
	    	Thread.sleep(SLEEPINT);							// Put thread to sleep
	    }	    
	}
	
//
// The "odometer" is implemented in the timer listener (aka interrupt service routine).
// It follows the recipe described in the class notes.
//
	public void timedOut() {
		double distL, distR, deltaD, deltaT, dX, dY;
		
		nowTachoL = leftMotor.getTachoCount();      		// get tacho counts
		nowTachoR = rightMotor.getTachoCount();
		distL = 3.14159*WR*(nowTachoL-lastTachoL)/180;		// compute L and R wheel displacements
		distR = 3.14159*WR*(nowTachoR-lastTachoR)/180;
		lastTachoL=nowTachoL;								// save tacho counts for next iteration
		lastTachoR=nowTachoR;
		deltaD = 0.5*(distL+distR);							// compute vehicle displacement
		deltaT = (distL-distR)/WB;							// compute change in heading
		Theta += deltaT;									// update heading
	    dX = deltaD * Math.sin(Theta);						// compute X component of displacement
		dY = deltaD * Math.cos(Theta);						// compute Y component of displacement
		X = X + dX;											// update estimates of X and Y position
		Y = Y + dY;	
	}
	
}
