package rendering;

public class Vector3{
	public final float x,y,z;
	
	public Vector3(int size) {
		x = 0;
		y = 0;
		z = 0;
	}
	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3(float x, float y) {
		this.x = x;
		this.y = y;
		this.z = 0;
	}
	public Vector3 add(Vector3 v) {
		return new Vector3(this.x + v.x, this.y + v.y, this.z + v.z);
	}
	public Vector3 subtract(Vector3 v) {
		return new Vector3(this.x - v.x, this.y - v.y, this.z - v.z);
	}
	public Vector3 multiply(float val) {
		return new Vector3(this.x * val, this.y * val, this.z * val);
	}
	public float length() {
		return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
	public Vector3 round() {
		return new Vector3(this.x, Math.round(this.y), this.z);
	}

}
