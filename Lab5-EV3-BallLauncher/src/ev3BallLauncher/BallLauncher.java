package ev3BallLauncher;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class BallLauncher {
	
	// launch motor
	private static EV3LargeRegulatedMotor launchMotor;
	
	// speed constants
	private static final int ROTATE_SPEED2 = 2000, MOTOR_ACCELERATION2 = 8000; // fast speed for farther target
	private static final int ROTATE_SPEED1 = 1500, MOTOR_ACCELERATION1 = 8000; // slower speed for closer target
	private static final int ROTATE_SPEED3 = 100, MOTOR_ACCELERATION3 = 100; // slow speed to revert back to origin
	
	public BallLauncher( EV3LargeRegulatedMotor launchMotor ) {
		this.launchMotor = launchMotor;
	}
	
	/**
	 * A method to shoot the ball at our middle target
	 * 
	 */
	public void shootAtMiddleTarget() {
		launchMotor.setSpeed(ROTATE_SPEED1);
		launchMotor.setAcceleration(MOTOR_ACCELERATION1);
		launchMotor.rotate(-135);
		moveLauncherBackMiddle();
	}
	
	/**
	 * A method to shoot the ball at one of the side targets
	 * 
	 */
	public void shootAtSideTarget() {
		launchMotor.setSpeed(ROTATE_SPEED2);
		launchMotor.setAcceleration(MOTOR_ACCELERATION2);
		launchMotor.rotate(-160);
		moveLauncherBackSide();
	}
	
	/**
	 * A method to move our launcher back to its original position for middle target
	 */
	private void moveLauncherBackMiddle() {
		launchMotor.setSpeed(-ROTATE_SPEED3);
		launchMotor.setAcceleration(-MOTOR_ACCELERATION3);
		launchMotor.rotate(135);
	}
	
	/**
	 * A method to move our launcher back to its original position for side targets
	 */
	private void moveLauncherBackSide() {
		launchMotor.setSpeed(-ROTATE_SPEED3);
		launchMotor.setAcceleration(-MOTOR_ACCELERATION3);
		launchMotor.rotate(160);
	}


}
