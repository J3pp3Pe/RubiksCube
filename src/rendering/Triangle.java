package rendering;

import java.awt.image.BufferedImage;

public class Triangle {
	public final Vector3[] corners;
	private Vector3[] textureCorners;
	private Vector3[] renderedCorners;
	private Matrix4 worldToTexture;
	private int[] rgb;
	private int width, height;
	private int color;
	
	public Triangle(Vector3[] corners) {
		if(corners.length != 3) {
			throw new IllegalArgumentException ("A triangle can only have 3 corners, not " + corners.length);
		}
		this.corners = corners;
		color = 255 << 16;
	}
	public Triangle(Vector3 v1, Vector3 v2, Vector3 v3) {
		this(new Vector3[] {v1,v2,v3});
	}
	
	public Triangle setTexture(Vector3 v1, Vector3 v2, Vector3 v3, int[] image, int width, int height) {
		this.textureCorners = new Vector3[] {v1,v2,v3};
		this.rgb = image;
		this.width = width;
		this.height = height;
		return this;
	}
	public Triangle setTexture(Triangle t, int[] image, int width, int height){
		return this.setTexture(t.corners[0], t.corners[1], t.corners[2], image, width, height);
	}
	public Triangle setTexture(int color) {
		this.textureCorners = null;
		this.rgb = null;
		this.color = color;
		return this;
	}
	
	public void setRendered(Vector3[] rendered) {
		this.renderedCorners = rendered;
		updateMatrix();
	}
	
	private void updateMatrix() {
		if(this.textureCorners == null) {
			return;
		}
		Vector3 k = renderedCorners[0];
		Vector3 v = renderedCorners[1].subtract(renderedCorners[0]);
		Vector3 u = renderedCorners[2].subtract(renderedCorners[0]);
		
		Vector3 c = textureCorners[0];
		Vector3 b = textureCorners[1].subtract(textureCorners[0]);
		Vector3 a = textureCorners[2].subtract(textureCorners[0]);
		
		float[][] f = {
				{a.x * v.y - b.x * u.y, b.x * u.x - a.x * v.x, -k.y * (b.x * u.x - a.x * v.x) - k.x * (a.x * v.y - b.x * u.y) + c.x * (u.x * v.y - u.y * v.x), 0},
				{a.y * v.y - b.y * u.y, b.y * u.x - a.y * v.x, -k.y * (b.y * u.x - a.y * v.x) - k.x * (a.y* v.y - b.y*u.y) + c.y * (u.x * v.y - u.y * v.x), 0},
				{0        , 0       , 0, 0},
				{0        , 0        ,0 , 0}
		};
		for(int i = 0; i < 3; i++) {
			f[0][i] *= width;
			f[1][i] *= height;
		}
		Matrix4 transform = new Matrix4(f);
		worldToTexture = transform.multiply(1/(u.x * v.y - u.y* v.x));
	}
	
	public int getColor(float x, float y) {
		if(this.rgb == null) {
			return this.color;
		}
		Vector3 pos = worldToTexture.dot2(new Vector3(x,y,1));
		
		int newX = Math.max(0, Math.min((int)width - 1, (int)pos.x));
		int newY = Math.max(0, Math.min((int)height - 1, (int)pos.y));

		
		return rgb[newY * width + newX];
	}	

}
