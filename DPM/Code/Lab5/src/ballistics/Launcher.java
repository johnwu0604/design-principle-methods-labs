//group 22
//Matthew Rodin 260623844
//Tiffany Wang 260684152

package ballistics;

import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class Launcher {
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
//	private final int distance = 120;
	private final static int maxSpeed = 3100; 
	private final static int maxAcceleration = 3000;
	private final static int maxAngle = 110;
	private final static int slowSpeed = 80;
	
	public Launcher(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor){
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		
	}
	
	public void run(){ 
		//shoot the ball at a certain acceleration 
		this.leftMotor.setAcceleration(maxAcceleration);
		this.rightMotor.setAcceleration(maxAcceleration);
		this.leftMotor.setSpeed(maxSpeed);
		this.rightMotor.setSpeed(maxSpeed);
		//make the motors rotate at the same time
		this.leftMotor.rotateTo(maxAngle, true);
		this.rightMotor.rotateTo(maxAngle, false);
		//stop the arm
		stopping();
		stopping();
		stopping();
		stopping();
		
		
		//bring the arm back to its original position for the next shot.
		this.leftMotor.setSpeed(slowSpeed);
		this.rightMotor.setSpeed(slowSpeed);
		this.leftMotor.rotateTo(0, true);
		this.rightMotor.rotateTo(0, false);
		
	}
	
	public void stopping(){
		this.rightMotor.setSpeed(0);
		this.leftMotor.setSpeed(0);
		this.rightMotor.stop();
		this.rightMotor.stop();
	}
	
	
}
