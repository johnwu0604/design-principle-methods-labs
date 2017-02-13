package ev3BallLauncher;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class BallLauncher {
	
	// launch motor
	private static EV3LargeRegulatedMotor launchMotor;
	
	// speed constants
	private static final int ROTATE_SPEED1 = 200, MOTOR_ACCELERATION1 = 200;
	private static final int ROTATE_SPEED2 = 400, MOTOR_ACCELERATION2 = 400;
	
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
		launchMotor.rotate(90);
		moveLauncherBack();
	}
	
	/**
	 * A method to shoot the ball at one of the side targets
	 * 
	 */
	public void shootAtSideTarget() {
		launchMotor.setSpeed(ROTATE_SPEED2);
		launchMotor.setAcceleration(MOTOR_ACCELERATION2);
		launchMotor.rotate(90);
		moveLauncherBack();
	}
	
	/**
	 * A method to move our launcher back to its original position
	 */
	private void moveLauncherBack() {
		launchMotor.setSpeed(-ROTATE_SPEED1);
		launchMotor.setAcceleration(-MOTOR_ACCELERATION1);
		launchMotor.rotate(-90);
	}


}
