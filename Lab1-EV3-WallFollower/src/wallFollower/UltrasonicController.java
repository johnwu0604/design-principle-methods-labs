package wallFollower;

public interface UltrasonicController {
	
	public void processUSData(int distance);
	
	public int readUSDistance();
}
