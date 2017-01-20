//Lab 3
//Tiffany Wang 260684152
//Matthew Rodin 260623844
//Group 22
package navigation;

import lejos.hardware.lcd.TextLCD;
import lejos.hardware.ev3.LocalEV3;


public class wallReaderDisplay extends Thread{ 
	private WallFollower p; 
	public final static TextLCD t = LocalEV3.get().getTextLCD();
	public wallReaderDisplay (WallFollower p){
		this.p = p;
	}
	
	public void run(){
		while(true){
			t.drawString("D:                  " + p.readUSDistance(), 0, 3);
		}
	}
}
