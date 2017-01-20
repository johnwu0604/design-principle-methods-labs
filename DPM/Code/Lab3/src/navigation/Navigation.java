//Lab 3
//Tiffany Wang 260684152
//Matthew Rodin 260623844
//Group 22
package navigation;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import navigation.Odometer;
import navigation.UltrasonicPoller;
import navigation.WallFollower;

public class Navigation extends Thread {
	private static final Object lock = new Object();
	private static final int FORWARD_STRAIGHT = 175; 
	private static final int ROTATE_SPEED = 75;
	private static double currentT = 0;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	static Odometer odometer;
	private static final double leftRadius = 2.1;
	private static final double rightRadius =2.1;
	private static final double width = 15;
	private double xnow;
	private double ynow;
	private double tnow;
	public static boolean navigationCalled = false; 
	public static boolean turnCalled = false ;
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 16;
	public static boolean isSensed = false; 
	public UltrasonicPoller usPoller; 
	public WallFollower p;
	public SampleProvider usDistance;
	public float[] usData;
	private Thread forward;
	private final double threshold = 0.5;


	
//
//	public Navigation(Odometer odometer, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor){
//		this.leftMotor = leftMotor;
//		this.rightMotor = rightMotor;
//		this.odometer = odometer;
//		
//	}
	
	public Navigation(Odometer odometer2, final EV3LargeRegulatedMotor leftMotor, final EV3LargeRegulatedMotor rightMotor){
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		odometer = odometer2;
		// we want the robot to move forward in a thread, for it should always be happening, except under certain conditions 
		// We don't want the robot to move forward when it is turn (or else it would not be accurate) 
		// we also want the robot to stop moving once it reaches the destination. 
		

	}
	

	public void travelTo(double x, double y){
		double theta;
		navigationCalled = true;
		//once the robot turns, it can start moving forward
		boolean done = false;
		while(true){	
			if (done) break;
			while(!isSensed){
			// we do not want this loop to work when the robot is following the wall 
			if (Math.abs(x - xnow) < threshold && Math.abs(y - ynow) < threshold){
				//once the robot approaches the destination with a threshold of 0.5, it quits the while loops 
				Sound.beep();
				leftMotor.stop(true);
				rightMotor.stop(false);	
				done = true;
				break;
			}
			
			
			//if(navigationCalled){
				ynow = odometer.getY();
				xnow = odometer.getX();
				tnow = odometer.getTheta();
				//we calculate the angle of the robot under difference circumstances. 
				if (y-ynow == 0 && x-xnow > 0) {
					theta = Math.PI/2;
				} else if (y-ynow == 0 && x-xnow < 0) {
					theta = -Math.PI/2;
				} else if (x-xnow == 0 && y-ynow > 0) {
					theta = 0;
				} else if (x-xnow == 0 && y-ynow < 0) {
					theta = Math.PI;
				} else if((y-ynow) < 0 && x-xnow < 0){
					theta = Math.atan((x - xnow)/(y-ynow)) - Math.PI;
				} else if((y-ynow) < 0 && x-xnow > 0){
					theta = Math.atan((x - xnow)/(y-ynow)) + Math.PI;
				} else{
					theta = Math.atan((x - xnow)/(y-ynow));
				}
				if(Math.abs(tnow - theta) > 1*Math.PI / 180){
					turnTo(Math.toDegrees(theta));
				} else {
					//if the angle is off, the robot would correct it, else it would move forward.
					leftMotor.setSpeed(FORWARD_STRAIGHT);
					rightMotor.setSpeed(FORWARD_STRAIGHT);
					leftMotor.forward();
					rightMotor.forward();
				}
			}
			//}
		}// we want the robot to move forward at the same time as it reads its position 
		// one it reaches its destination, it exists the loop. 
		navigationCalled = false;
		
	}
//	This method causes the robot to travel to the absolute field location (x, y).
//	This method should continuously call turnTo(double theta) and then
//	set the motor speed to forward(straight). This will make sure that your
//	heading is updated until you reach your exact goal. (This method will poll
//	the odometer for information)
	
	
	public void turnTo(double theta){
		turnCalled = true;
		currentT = odometer.getTheta();
		//we want the robot to make the smallest turn 
		double deltaT = (theta - Math.toDegrees(currentT));
		//we want the angle to be in between -180 and 180 degrees
		if (deltaT > 180){
			deltaT -= 360;
		} else if (deltaT < -180) {
			deltaT += 360;
		}
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		//we make the robot rotate
		leftMotor.rotate(convertAngle(leftRadius, width, deltaT), true);
		rightMotor.rotate(-convertAngle(rightRadius, width,deltaT), false);
			
			
		turnCalled = false;
		
	}
	public static boolean isNavigating(){
		return turnCalled || navigationCalled ;
//	This method returns true if another thread has called travelTo() or
//	turnTo() and the method has yet to return; false otherwise.
	}
	
 
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	//set method used in the WallFollower 
	public static void setNavigationCalled(boolean called){
		navigationCalled = called;
	}
	
}
