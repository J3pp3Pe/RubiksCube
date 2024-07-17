package rendering;

public class Matrix4 {
	private float[][] matrix;
	
	public Matrix4() {
		matrix = new float[4][4];
	}
	public Matrix4(float[][] matrix) {
		if(matrix.length != 4) {
			throw new IllegalArgumentException("Invalid number of rows. Must be 4 rows");
		}
		for(int i = 0; i < matrix.length; i++) {
			if(matrix[i].length != 4) {
				throw new IllegalArgumentException("Invalid number of columns. Must be 4 columns");

			}
		}
		this.matrix = matrix;
	}
	public Matrix4(Matrix4 matrix) {
		this.matrix = new float[4][4];
		for(int i = 0; i < this.matrix.length; i++) {
			this.matrix[i] = matrix.matrix[i].clone();
		}
	}
	
	public Matrix4 dot(Matrix4 m2) {
		float[][] result = new float[this.matrix.length][m2.matrix[0].length];
		for(int row = 0; row < result.length; row++) {
			for(int column = 0; column < result[row].length; column++) {
				result[row][column] = dotPos(this, m2, row, column);
			}
		}
		return new Matrix4(result);
	}
	public Vector3 dot(Vector3 v) {
		return new Vector3(
			matrix[0][0] * v.x + matrix[0][1] * v.y + matrix[0][2] * v.z + matrix[0][3],
			matrix[1][0] * v.x + matrix[1][1] * v.y + matrix[1][2] * v.z + matrix[1][3],
			matrix[2][0] * v.x + matrix[2][1] * v.y + matrix[2][2] * v.z + matrix[2][3]
		);		
	}
	public Vector3 dot2(Vector3 v) {
		return new Vector3(
			matrix[0][0] * v.x + matrix[0][1] * v.y + matrix[0][2],
			matrix[1][0] * v.x + matrix[1][1] * v.y + matrix[1][2],
			0
		);		
	}
	private float dotPos(Matrix4 m1, Matrix4 m2, int row, int column) {
		float res = 0;
		for(int i = 0; i < m1.matrix[row].length; i++) {
			res += m1.matrix[row][i] * m2.matrix[i][column];
		}
		return res;
	}
	
	public Matrix4 add(Matrix4 m2) {
		if(this.matrix.length != m2.matrix.length || this.matrix[0].length != m2.matrix[0].length) {
			throw new IllegalArgumentException("Unable to add matrixes of different sizes!");
		}
		float[][] result = new float[this.matrix.length][this.matrix[0].length];
		for(int row = 0; row < result.length; row++) {
			for(int column = 0; column < result[row].length; column++) {
				result[row][column] = this.matrix[row][column] + m2.matrix[row][column];
			}
		}
		return new Matrix4(result);
	}
	public Matrix4 subtract(Matrix4 m2) {
		if(this.matrix.length != m2.matrix.length || this.matrix[0].length != m2.matrix[0].length) {
			throw new IllegalArgumentException("Unable to subtract matrixes of different sizes!");
		}
		float[][] result = new float[this.matrix.length][this.matrix[0].length];
		for(int row = 0; row < result.length; row++) {
			for(int column = 0; column < result[row].length; column++) {
				result[row][column] = this.matrix[row][column] - m2.matrix[row][column];
			}
		}
		return new Matrix4(result);
	}
	public Matrix4 multiply(float val) {
		float[][] result = new float[this.matrix.length][this.matrix[0].length];
		for(int row = 0; row < result.length; row++) {
			for(int column = 0; column < result[row].length; column++) {
				result[row][column] = this.matrix[row][column] * val;
			}
		}
		return new Matrix4(result);
	}
	
	public static Matrix4 perspectiveProjection(Angle fieldOfView) {
		float[][] f = {
				{1 / (float)Math.tan(fieldOfView.radians/2), 0, 0, 0},
				{0,1 / (float)Math.tan(fieldOfView.radians/2), 0, 0},
				{0,0,1,0},
				{0,0,0,1}
		};
		return new Matrix4(f);
	}
	public static Matrix4 rotationMatrixZ (Angle angle) {
		float[][] f = {
				{(float)Math.cos(angle.radians), -(float)Math.sin(angle.radians), 0, 0},
				{(float)Math.sin(angle.radians), (float)Math.cos(angle.radians), 0, 0},
				{0,0,1,0},
				{0,0,0,1}
		};
		return new Matrix4(f);
	}
	public static Matrix4 rotationMatrixY (Angle angle) {
		float[][] f = {
				{(float)Math.cos(angle.radians), 0,(float)Math.sin(angle.radians), 0},
				{0,1, 0, 0},
				{-(float)Math.sin(angle.radians),0,(float)Math.cos(angle.radians),0},
				{0,0,0,1}
		};
		return new Matrix4(f);
	}
	public static Matrix4 rotationMatrixX (Angle angle) {
		float[][] f = {
				{1,0,0, 0},
				{0,(float)Math.cos(angle.radians),-(float)Math.sin(angle.radians),0},
				{0,(float)Math.sin(angle.radians),(float)Math.cos(angle.radians),0},
				{0,0,0,1}
		};
		return new Matrix4(f);
	}
	public static Matrix4 transformMatrix(Vector3 v) {
		float[][] f = new float[4][4];
		for(int row = 0; row < f.length; row++) {
			f[row][row] = 1;
		}
		f[0][3] = v.x;
		f[1][3] = v.y;
		f[2][3] = v.z;
		
		return new Matrix4(f);
	}
	
	public float get(int row, int column) {
		return matrix[row][column];
	}
	public void set(int row, int column, float value) {
		matrix[row][column] = value;
	}
	public int height() {
		return matrix.length;
	}
	public int width() {
		return matrix[0].length;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("{");
		for(int row = 0; row < this.matrix.length; row++) {
			result.append("{");
			for(int column = 0; column < this.matrix[row].length; column++) {
				result.append(this.matrix[row][column]);
				if(column != this.matrix[row].length - 1) {
					result.append(", ");
				}
			}
		
			result.append("}");
		}
		result.append("}");
		return result.toString();
	}
	@Override
	public boolean equals(Object o) {
		if(o == this) {
			return true;
		}
		if(o.getClass() != this.getClass()) {
			return false;
		}
		Matrix4 m2 = (Matrix4)o;
		if(this.matrix.length != m2.matrix.length || this.matrix[0].length != m2.matrix[0].length) {
			return false;
		}
		for(int row = 0; row < this.matrix.length; row++) {
			for(int column = 0; column < this.matrix[row].length; column++) {
				if(this.matrix[row][column] != m2.matrix[row][column]) {
					return false;
				}
			}
		}
		return true;
	}

}
