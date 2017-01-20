package odoDemo;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class doSquare extends Thread {
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	static final int SIDE = 5000;			// Size of square to trace in units of 0.1cm
	static final int FWDSPEED = 180;		// Straight line speed (deg/sec)
	static final int TRNSPEED = 90;			// Rotational speed (deg/sec)
	static final int DISTTODEG=21;			// 360/(2xPixRw)  Rw=2.8cm	  (20)
	static final int ORIENTTODEG=298;		// (Rc/Rw)*100     Rc=7.94cm  (283)

	public doSquare(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}
	
	public void run() {
		MoveDistFwd(SIDE,FWDSPEED);				// Draw a side
		Rotate(90,TRNSPEED);					// Rotate 90¡  (repeat 4 times)
		MoveDistFwd(SIDE,FWDSPEED);
		Rotate(90,TRNSPEED);
		MoveDistFwd(SIDE,FWDSPEED);
		Rotate(90,TRNSPEED);
		MoveDistFwd(SIDE,FWDSPEED);
		Rotate(90,TRNSPEED);			
	}

	public void MoveDistFwd(int distance, int speed) {
		int WRotationAngle;
		
	// Motor commands block by default (i.e. they return only when motion is complete).
	// To get both motors synchronized, use the non-blocking method for leftMotor
	// so that it returns immediately.  The blocking form is used for rightMotor so
	// that this method returns when motion is complete.
		
		WRotationAngle=distance*DISTTODEG/100;	// Convert linear distance to turns
		leftMotor.setSpeed(speed);				// Roll both motors forward
		rightMotor.setSpeed(speed);
		leftMotor.rotate(WRotationAngle,true);	// Rotate left motor - DO NOT BLOCK
		rightMotor.rotate(WRotationAngle);		// Rotate right motor
	}

	public void Rotate(int angle, int speed) {
		int CRotationAngle;
		
		CRotationAngle=angle*ORIENTTODEG/100;	// Convert rotation to equivalent turns
		leftMotor.setSpeed(speed);				// Turn at a different speed from forward
		rightMotor.setSpeed(speed);
		leftMotor.rotate(CRotationAngle,true);	// Left motor clockwise, non-blocking
		rightMotor.rotate(-CRotationAngle);;	// Right motor counter clockwise, blocking
	}
}
