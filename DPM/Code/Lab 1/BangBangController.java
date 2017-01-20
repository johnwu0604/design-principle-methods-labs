//Group 22
//Matthew Rodin (260623844)
//Tiffany Wang (260684152)

package wallFollower;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorHigh);				// Start robot moving forward
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		int error = Math.abs(distance - bandCenter); //finding the error
		if (error <= bandwidth){ //we neglect any error that is smaller or equal to the bandwidth (threshold)
			this.rightMotor.forward(); //continue straight
			this.leftMotor.forward(); // continue straight
		}
		else {
			if(distance > bandCenter){ //if distance is bigger than our desired distance 
				if (distance < 75){
					//this is for the gap + turn
					//move right motor more than the left to outward turn
					this.rightMotor.setSpeed(motorHigh+50);
					this.leftMotor.setSpeed(motorLow); 
				}else{
					this.rightMotor.setSpeed(135); //set right motor speed to 135
					this.leftMotor.setSpeed(95); //set left motor speed to 95 	
				}
				
			}else if (distance < bandCenter){ //if distance is smaller than the desired distance
				//make sure to move left motor faster than the right in order to move away form the wall
				this.rightMotor.setSpeed(motorLow);
				this.leftMotor.setSpeed(motorHigh+57); 
			}
			this.leftMotor.forward();
			this.rightMotor.forward(); 
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
