package ev3Translation;

public interface UltrasonicController {
	
	public void processUSData(int distance);
	
	public int readUSDistance();
}
