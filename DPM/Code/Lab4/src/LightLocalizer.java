//Tiffany Wang 260684152
//Matthew Rodin 260623844
//Group 22

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import java.util.Arrays; 

public class LightLocalizer {
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;
	private Navigation navi;
	private final int ROTATION_SPEED = 50;
	private final int threshold = 1;
	private final int distanceWheelSensor = 7;
	
	public LightLocalizer(Odometer odo, SampleProvider colorSensor, float[] colorData, Navigation navi) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.navi = navi;
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
//		1.move forward until it sees a line
//		move backwards 2cm (or 1cm or 3cm)
//		move around in a full circle (360 degrees) —> use a line counter to make sure you have 4 lines counted 
//		rotate 45 degrees
//		rotate clockwise until you see a line. save angle A
//		rotate counter clockwise until you see 2nd line. save angle B
//		x = -track*cos[(A+(360-B))/2]
//		rotate to -45
//		rotate clockwise until you see 2nd line line. save angle C
//		rotate counter clockwise until you see 2nd line. save angle D.
//		y = -tack*cos[(C+(360-D))/2]
//		Now you have coordinates (x,y) which should be negative. Naviagte to (0,0)
		
		boolean line = false;
		fetchSample();
		navi.turnTo(35, true);
		navi.stopping();
		double color = fetchSample();
		while(!line){

			navi.goForward();
			color = fetchSample();
			if(color < 0.35) line = true;
		}
		
		navi.stopping();
		
		navi.goForward(6);
		navi.stopping();

		double lineArray[] = new double[4];
		
		// you want to rotate the robot until you log the angles of the 4 lines 
		int counterLine = 0; 
		navi.rotate(ROTATION_SPEED);
		while(counterLine < 4){
			color = fetchSample();
			
			//you don't want the sensor to log the four lines at once so you input a boolean. 
			if(color < 0.35 && line){
				continue;
			}
			
			
			// if the color is no longer black, and there is a line then you know that the robot has go passed that line 
			//it is ready to sense the next one
			if(color > 0.35 && line){
				line = false;
			}
			
			//if there was no line and the robot senses black, then it's a new line
			if(color < 0.35 && !line){
				lineArray[counterLine] = odo.getAng();

				counterLine++;
				line = true;

			}
			if(color < 0.35 ){
				lineArray[counterLine] = odo.getAng();
				Sound.buzz();
				counterLine ++;
			}
			
		}
		
		navi.turnTo(0, true);
		navi.stopping();
		System.out.println("\n\n\n\n");
		for(int i  = 0; i < counterLine ; i++){
			System.out.println((int)lineArray[i] + "\n");
		}
		
		//calculate the current position with trig
		double xnow, ynow; 
		xnow =  distanceWheelSensor * Math.cos(Math.toRadians(( lineArray[3] - lineArray[1])/2));
		ynow = - distanceWheelSensor * Math.cos(Math.toRadians((- lineArray[2] + lineArray[0]) /2));
		System.out.println("xnow: " + (int)xnow + "\nynow: " + (int)ynow);
		
		
		//you update the current position with the ones you have calculated.
		odo.setX(xnow);
		odo.setY(ynow);
		
		//travel to the origine and turn to 90 (0degrees)
		navi.travelTo(0, 0);
		navi.turnTo(90, true);
		navi.stopping();
		
		odo.setPosition(new double [] {0.0, 0.0, 90.0}, new boolean [] {true, true, true});
		
	}
	
	
	public double fetchSample(){
		float[] sampleRed = {0};
		colorSensor.fetchSample(sampleRed, 0);
		return sampleRed[0];
	}

}
