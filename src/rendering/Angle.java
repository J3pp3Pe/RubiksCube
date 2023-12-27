package rendering;

public class Angle {
	public final float degrees;
	public final float radians;
	
	private Angle(float degrees){
		this.degrees = degrees;
		this.radians = (float)Math.toRadians(degrees);
	};
	
	public static Angle newAngleDegrees(float angle) {
		return new Angle(angle);
	}
	public static Angle newAngleRadians(float angle) {
		return new Angle((float)Math.toDegrees(angle));
	}
	
	public Angle neg() {
		return newAngleDegrees(-degrees);
	}

}
