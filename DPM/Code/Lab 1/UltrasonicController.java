//Group 22
//Matthew Rodin (260623844)
//Tiffany Wang (260684152)

package wallFollower;

public interface UltrasonicController {
	
	public void processUSData(int distance);
	
	public int readUSDistance();
}
