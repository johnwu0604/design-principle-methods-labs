package wallFollower;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor, upperMotor;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			EV3LargeRegulatedMotor upperMotor, int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.upperMotor = upperMotor;
		
		// Set all motors to zero speed to allow time for sensors to initialize
		upperMotor.setSpeed(0);
		upperMotor.forward();
		
		leftMotor.setSpeed(0);				
		rightMotor.setSpeed(0);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		if (!leftMotor.isMoving() && !rightMotor.isMoving()) {
			steerStraight();
		}
		
		if (!upperMotor.isMoving()){
			if(upperMotor.getLimitAngle() == 110){
				upperMotor.setSpeed(500);
				upperMotor.rotate(-110);
			}else{
				upperMotor.setSpeed(500);
				upperMotor.rotate(110);
			}
		}
		
		int error = this.distance - this.bandCenter;
		
		if ( Math.abs(error) < this.bandwidth ) {
			steerStraight();
		} 
		else if ( error < 0 ) {
			if ( error < -10 ) {
				turnRight();
			} else {
				steerRight();
			}
		} else {
			if ( error > 235 ) {
				steerStraight();
			} else {
				steerLeft();
			}
		}
		
		
		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
	
	public void steerStraight() {
		leftMotor.setSpeed(motorHigh);			
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	public void turnRight() {
		leftMotor.setSpeed(motorLow);			
		rightMotor.setSpeed(0);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	public void steerRight() {
		leftMotor.setSpeed(motorHigh);			
		rightMotor.setSpeed(motorLow);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	public void steerLeft() {
		leftMotor.setSpeed(motorLow);			
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
	}
}
