//Group 22
//Matthew Rodin (260623844)
//Tiffany Wang (260684152)

package wallFollower;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 100, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;
	int counter = 0;
	
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight);					// Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		
		// rudimentary filter - toss out invalid samples corresponding to null signal.
		// (n.b. this was not included in the Bang-bang controller, but easily could have).
		//
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} else if (distance == 255){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		
		float error = Math.abs(distance - bandCenter); // we want the error to be positive 
		if(error <= bandwidth){ // the robot does not turn right / left when the error is within the bandwidth
			this.rightMotor.setSpeed(motorStraight);
			this.leftMotor.setSpeed(motorStraight); 
		}else{
			float adjustedSpeed = error * 6; // the adjustedSpeed is proportional to the error 
			if(distance > bandCenter){
				if(distance < 75){
					if((motorStraight + adjustedSpeed) > 240){ // we do not want the motor to go too fast
															// therefore we are limiting the speed to 240
						this.rightMotor.setSpeed(240); 
					}else{
						this.rightMotor.setSpeed(motorStraight + adjustedSpeed);
					}
				
					if((motorStraight - adjustedSpeed) <= 50){
						this.leftMotor.setSpeed(50);  // we do not want the motor to go too slow, so we set a minimum speed 
					} else { 
						this.leftMotor.setSpeed(motorStraight - adjustedSpeed);  // else, the speed of the leftMotor is adjusted with the adjustedSpeed 
																				// adjustment proportional to the error. 
					}
				}else{
					this.rightMotor.setSpeed(135); // if(distance > 75) == true means that the robot has to potentially do a U-Turn 
													// However, this could also mean that there is a gap on the "wall" 
													// Therefore, we set the speed slower so it can detect the end of the gap (if there is one)
													// before it does a whole U-turn 
					this.leftMotor.setSpeed(95); 
						
				}
			}else if (distance < bandCenter){
				this.leftMotor.setSpeed(motorStraight + adjustedSpeed);
				
				if((motorStraight - adjustedSpeed) <= 50){  // we are setting the minimal speed to 50
					this.rightMotor.setSpeed(50); 
					} else { 
						this.rightMotor.setSpeed(motorStraight - adjustedSpeed); 
					}
				}
		this.leftMotor.forward(); // after setting up the speed of the wheels, we put the motors to work. 
		this.rightMotor.forward(); 
		}
	}

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
