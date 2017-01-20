//group 22
//Matthew Rodin 260623844
//Tiffany Wang 260684152
package ballistics;


import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;




public class Lab5 {
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	public static void main(String[] args){
		Launcher launch = new Launcher(leftMotor, rightMotor);
		//make out ball lanch at every press
		System.out.println("please press any key");
		while(true){
			Button.waitForAnyPress();
			launch.run();
		}
	}
}
