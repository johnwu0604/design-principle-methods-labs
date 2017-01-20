//Lab 3
//Tiffany Wang 260684152
//Matthew Rodin 260623844
//Group 22
package navigation;
import lejos.robotics.SampleProvider;
import navigation.UltrasonicController;

//
//  Control of the wall follower is applied periodically by the 
//  UltrasonicPoller thread.  The while loop at the bottom executes
//  in a loop.  Assuming that the us.fetchSample, and cont.processUSData
//  methods operate in about 20mS, and that the thread sleeps for
//  50 mS at the end of each loop, then one cycle through the loop
//  is approximately 70 mS.  This corresponds to a sampling rate
//  of 1/70mS or about 14 Hz.
//


public class UltrasonicPoller extends Thread{
	public SampleProvider us;
	public WallFollower cont;
	public float[] usData;
	private int distance;
	
	public UltrasonicPoller(SampleProvider us, float[] usData, WallFollower p) {
		this.us = us;
		this.cont = p;
		this.usData = usData;
		
	}

//  Sensors now return floats using a uniform protocol.
//  Need to convert US result to an integer [0,255]
	
	public void run() {
		while(true){
//			System.out.println("k");
			us.fetchSample(usData,0);							// acquire data
			distance=(int)(usData[0]*100.0);					// extract from buffer, cast to int
			cont.processUSData(distance);
		}
		
	}
	
	public WallFollower getP(){
		return cont;
	}
	
	public double getD(){
		return distance;
	}
}
