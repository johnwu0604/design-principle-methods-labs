//Tiffany Wang 260684152
//Matthew Rodin 260623844
//Group 22

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static int ROTATION_SPEED = 70;
	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private static final double track = 15;
	private double fallingA1, fallingA2, risingA1, risingA2;
	private double tacholeftnow, tacholeftlast = 0, tachorightnow, tachorightlast = 0;
	private final double WHEEL_RADIUS = 2.1;
	private double theta = 0 ;
	private Navigation navi;
	private final int wallDistance = 30;
	
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType, Navigation navi) {
		this.navi = navi;
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
	}

	public void doLocalization() {
		double [] pos = new double [3];
		double angleA = 0, angleB = 0;
		boolean wall = false;
		float distance = getFilteredData();
		double travelToPosition, angleAverage;
		if (locType == LocalizationType.FALLING_EDGE) {
			
			
			
			
			
			
			
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			
			// update the odometer position (example to follow:)
			wall = true;
			navi.rotate(ROTATION_SPEED); //turn clockwise
			//rotate the robot until it sees no wall
			while(wall){
				distance = getFilteredData();
				if(distance >= wallDistance) {
					wall = false;
				}
			}
			//if you see a wall, that is point a, then switch directions
			
			//look for angle a
			//keep rotating until the robot sees a wall, then latch the angle
			while(!wall){
				distance = getFilteredData();
				if(distance < wallDistance){
					wall = true;
				}
			}
			
			angleA = odo.getAng(); //log angle a
			navi.stopping(); //stop the motors
			navi.rotate(-ROTATION_SPEED);//rotate counter clockwise
		
			//switch direction and wait until it sees no wall
			while(wall){
				distance = getFilteredData();
				if(distance >= wallDistance) {
					wall = false;
				}
			}
			
			// turn until he sees a wall
			while(!wall){
				distance = getFilteredData();
				if(distance < wallDistance){
					wall = true;
				}
			}
			
			
			angleB  = odo.getAng(); //log angle b
		
			//calculation of the average angle
			angleAverage = (angleA + angleB)/2;
			
			//calculation of the angle needed to travel to 0 degrees
			travelToPosition = angleAverage -135;
			if(travelToPosition < 0) travelToPosition += 360;
//			else if(travelToPosition >) travelToPosition -= 180;
			navi.turnTo(travelToPosition, true);
			navi.stopping(); //stop the motors
			System.out.println("\n\n\n\n\n");
			
			// we want to set our robot to the 90 degrees 
			// in our code from the previous labs, the y-axis was 0 degrees, we want to keep it the same way. 
			System.out.println("Angle A: "+ (int)angleA + "\n"  + "Angle B: " + (int)angleB + "\n" + "TravelToAngle: " +(int)travelToPosition);
			odo.setPosition(new double [] {0.0, 0.0, 90.0}, new boolean [] {true, true, true});
		} else {
//			/*
//			 * The robot should turn until it sees the wall, then look for the
//			 * "rising edges:" the points where it no longer sees the wall.
//			 * This is very similar to the FALLING_EDGE routine, but the robot
//			 * will face toward the wall for most of it.
//			 */

			navi.rotate(-ROTATION_SPEED); // counter-clockwise
			//robot turns until it sees the wall
			while(!wall){
				distance = getFilteredData();
				if(distance < wallDistance){
					wall = true;
					
				}
				
			}
			// the robot looks for the "rising edges" (i.e. the points where it no longer sees the wall)
			while(wall){
				distance = getFilteredData();
				if(distance > wallDistance && wall){
					wall = false;
				}
			}
			
			navi.stopping(); //stop the motors
			
			//log angle a
			angleA = odo.getAng();
			
			navi.rotate(ROTATION_SPEED); //turn clockwise
			while(!wall){
				distance = getFilteredData();
				if(distance < wallDistance){
					wall = true;
				}
			}
			
			//turn until it sees no wall 
			while(wall){
				distance = getFilteredData();
				if(distance > wallDistance && wall) wall = false;
			}
			
			navi.stopping(); //stop the motors
			//log angle b
			angleB = odo.getAng();
			
			//calculation of the average angle
			angleAverage = (angleA + angleB)/2;
			
			//calculation of the angle needed to travel to 0 degrees
			travelToPosition = angleAverage - 315;
			if(travelToPosition < 0) travelToPosition += 360;
//			else if(travelToPosition >) travelToPosition -= 180;
			navi.turnTo(travelToPosition, true);
			navi.stopping(); //stop the motors
			System.out.println("\n\n\n\n\n");
			// we want to set our robot to the 90 degrees 
			// in our code from the previous labs, the y-axis was 0 degrees, we want to keep it the same way. 
			System.out.println("Angle A: "+ (int)angleA + "\n"  + "Angle B: " + (int)angleB + "\n" + "TravelToAngle: " +(int)travelToPosition);
			odo.setPosition(new double [] {0.0, 0.0, 90.0}, new boolean [] {true, true, true});
		}
	}
	
	// method to fetch the data from the us sensor
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0] * 100;
				
			if(distance > 50){
				distance = 50; 
			}
		return distance;
	}
}