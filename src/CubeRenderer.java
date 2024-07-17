
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import rendering.Angle;
import rendering.Matrix4;
import rendering.Triangle;
import rendering.Vector3;

import rendering.Bajs.Move;

public class CubeRenderer extends JPanel implements MouseMotionListener, MouseListener, KeyListener{
	private final List<Triangle> triangles;
	private final SmallCube[][][] cubes;
	private Vector3 position;
	private Vector3 angle;
	
	private BufferedImage colorBuffer;
	private float[][] depthBuffer;
		
	private Queue<Move> moves;
	private float rot = 0;
	private Move current = null;
	
	private Point startPoint = null;
	
	private int width, height;

	public CubeRenderer(int width, int height, RubikCube cube){
		this.width = width;
		this.height = height;
		triangles = new ArrayList<>(3 * 3 * 6 * 2);
		cubes = new SmallCube[3][3][3];
		
		setCube(cube);	
		removeInsides();
		moves = new LinkedList<>();
		
		position = new Vector3(0,0,-3.9f);
		angle = new Vector3(0,0,0);


		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setFocusable(true);
	}
	
	public CubeRenderer(int width, int height) {
		this(width, height, new RubikCube(3));
		
	}
	public void queueMove(Move ... moves){
		this.moves.addAll(Arrays.asList(moves));
	}
	public void setCube(RubikCube cube){
		int[] rgb = null;
		int width = 0;
		int height = 0;
		try {
			BufferedImage image = ImageIO.read(new File("../res/cube.png"));
			width = image.getWidth();
			height = image.getHeight();
			rgb = new int[width * height];
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {
					rgb[y * width + x] = image.getRGB(x, y);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		RubikSide left = cube.getLeft();
		RubikSide right = cube.getRight();
		RubikSide top = cube.getTop();
		RubikSide bottom = cube.getBottom();
		RubikSide back = cube.getBack();
		RubikSide front = cube.getFront();

		Triangle[][] colors = new Triangle[6][];
		for(int i = 0; i < 6; i++){
			colors[i] = getColor(i);
		}

		this.triangles.clear();
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				for(int z = 0; z < 3; z++){
					int sideColors[] = {top.get(z, x), bottom.get(2-z, x), left.get(2-y, z), right.get(2-y, z), front.get(2-y, x), back.get(2-y, x)};

					List<Triangle> triangles = getCube(new Vector3(x-1,y-1,z-1));
					for(int i = 0; i < 6; i++){
						triangles.get(i * 2).setTexture(colors[sideColors[i] - 1][0], rgb, width, height);
						triangles.get(i * 2 + 1).setTexture(colors[sideColors[i] - 1][1], rgb, width, height);
					}
					this.cubes[x][y][z] = new SmallCube(triangles);
					this.triangles.addAll(triangles);
				}
			}
		}
		removeInsides();
	}
	private void removeInsides(){
		int color = 0;
		for(int x = 0;  x < 3; x++) {
			for(int z = 0; z < 3; z++) {
				cubes[x][0][z].triangles.get(0).setTexture(color);
				cubes[x][0][z].triangles.get(1).setTexture(color);
				
				cubes[x][2][z].triangles.get(2).setTexture(color);
				cubes[x][2][z].triangles.get(3).setTexture(color);
				
				cubes[x][1][z].triangles.get(0).setTexture(color);
				cubes[x][1][z].triangles.get(1).setTexture(color);
				cubes[x][1][z].triangles.get(2).setTexture(color);
				cubes[x][1][z].triangles.get(3).setTexture(color);
			}
		}
		for(int y = 0;  y < 3; y++) {
			for(int z = 0; z < 3; z++) {
				cubes[2][y][z].triangles.get(4).setTexture(color);
				cubes[2][y][z].triangles.get(5).setTexture(color);
				
				cubes[0][y][z].triangles.get(6).setTexture(color);
				cubes[0][y][z].triangles.get(7).setTexture(color);
				
				cubes[1][y][z].triangles.get(4).setTexture(color);
				cubes[1][y][z].triangles.get(5).setTexture(color);
				cubes[1][y][z].triangles.get(6).setTexture(color);
				cubes[1][y][z].triangles.get(7).setTexture(color);
			}
		}
		for(int x = 0;  x < 3; x++) {
			for(int y = 0; y < 3; y++) {
				cubes[x][y][0].triangles.get(8).setTexture(color);
				cubes[x][y][0].triangles.get(9).setTexture(color);
				
				cubes[x][y][2].triangles.get(10).setTexture(color);
				cubes[x][y][2].triangles.get(11).setTexture(color);
				
				cubes[x][y][1].triangles.get(8).setTexture(color);
				cubes[x][y][1].triangles.get(9).setTexture(color);
				cubes[x][y][1].triangles.get(10).setTexture(color);
				cubes[x][y][1].triangles.get(11).setTexture(color);
				
				
			}
		}
	}
	private void rotate(Move move, Angle angle) {
		if(move.prime) {
			angle = angle.neg();
		}
		switch(move.side) {
			case R:
				transform(2,2,0,2,0,2, Matrix4.rotationMatrixX(angle.neg()));
				break;
			case L:
				transform(0,0,0,2,0,2, Matrix4.rotationMatrixX(angle));
				break;
			case U:
				transform(0,2,2,2,0,2, Matrix4.rotationMatrixY(angle.neg()));
				break;
			case D:
				transform(0,2,0,0,0,2, Matrix4.rotationMatrixY(angle));
				break;
			case B:
				transform(0,2,0,2,0,0, Matrix4.rotationMatrixZ(angle));
				break;
			case F:
				transform(0,2,0,2,2,2, Matrix4.rotationMatrixZ(angle.neg()));
				break;
				
		}
	}
	private void rotateCubes(Move move) {
		int n = 1;
		if(move.prime) {
			n = 3;
		}
		for(int i = 0; i < n; i++) {
			switch(move.side) {
				case R:
					rotateCubesX(2);
					break;
				case L:
					rotateCubesX(0);
					rotateCubesX(0);
					rotateCubesX(0);
					break;
				case U:
					rotateCubesY(2);
					break;
				case D:
					rotateCubesY(0);
					rotateCubesY(0);
					rotateCubesY(0);
					break;
				case F:
					rotateCubesZ(2);
					rotateCubesZ(2);
					rotateCubesZ(2);
					break;	
				case B:
					rotateCubesZ(0);
					break;
			}
		}

	}
	private void rotateCubesX(int x) {
		SmallCube last1 = cubes[x][0][1];
		SmallCube last2 = cubes[x][0][2];
		for(double angle = 0; angle < Math.PI * 2; angle += Math.PI / 2) {
			
			int z1 = ((int)Math.cos(angle) + 1);
			int y1 = ((int)Math.sin(angle) + 1);
			SmallCube temp1 = cubes[x][y1][z1];
			cubes[x][y1][z1] = last1;
			last1 = temp1;
			
			
			int z2 = (int)(Math.cos(angle + Math.PI / 4) * 1.9 + 1);
			int y2 = (int)(Math.sin(angle + Math.PI / 4) * 1.9 + 1);
			SmallCube temp2 = cubes[x][y2][z2];
			cubes[x][y2][z2] = last2;
			last2 = temp2;
			
		}
	}
	private void rotateCubesY(int y) {
		SmallCube last1 = cubes[1][y][0];
		SmallCube last2 = cubes[2][y][0];
		for(double angle = 0; angle < Math.PI * 2; angle += Math.PI / 2) {
			
			int x1 = ((int)Math.cos(angle) + 1);
			int z1 = ((int)Math.sin(angle) + 1);
			SmallCube temp1 = cubes[x1][y][z1];
			cubes[x1][y][z1] = last1;
			last1 = temp1;
			
			
			
			int x2 = (int)(Math.cos(angle + Math.PI / 4) * 1.9 + 1);
			int z2 = (int)(Math.sin(angle + Math.PI / 4) * 1.9 + 1);
			SmallCube temp2 = cubes[x2][y][z2];
			cubes[x2][y][z2] = last2;
			last2 = temp2;	
		}
	}
	private void rotateCubesZ(int z) {
		SmallCube last1 = cubes[1][0][z];
		SmallCube last2 = cubes[2][0][z];
		for(double angle = 0; angle < Math.PI * 2; angle += Math.PI / 2) {
			
			int x1 = ((int)Math.cos(angle) + 1);
			int y1 = ((int)Math.sin(angle) + 1);
			SmallCube temp1 = cubes[x1][y1][z];
			cubes[x1][y1][z] = last1;
			last1 = temp1;
			
			
			
			int x2 = (int)(Math.cos(angle + Math.PI / 4) * 1.9 + 1);
			int y2 = (int)(Math.sin(angle + Math.PI / 4) * 1.9 + 1);
			SmallCube temp2 = cubes[x2][y2][z];
			cubes[x2][y2][z] = last2;
			last2 = temp2;	
		}
	}
	private void transform(int xmin, int xmax, int ymin, int ymax, int zmin, int zmax, Matrix4 transformation) {
		for(int x = xmin; x <= xmax; x++) {
			for(int y = ymin; y <= ymax; y++) {
				for(int z = zmin; z <= zmax; z++) {
					cubes[x][y][z].transform(transformation);
				}
			}
		}

	}
	
	public void update(float time) {
		//angle += Math.PI / 10* time;
		//position = position.add(new Vector(0,0,1*time));
		if(current == null && !moves.isEmpty()) {
			current = moves.poll();
			rot = 0;
		}
		if(current != null) {
			float dist = 360 * time;
			if(rot + dist >= 90) {
				rotate(current, Angle.newAngleDegrees(90 - rot));
				rotateCubes(current);
				current = null;
				rot = 0;
			}else {
				rotate(current, Angle.newAngleDegrees(dist));
				rot += dist;
			}
		}


		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		colorBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		depthBuffer = new float[width][height];
		
		for(int i = 0; i < depthBuffer.length; i++) {
			Arrays.fill(depthBuffer[i], -Float.MAX_VALUE);

		}

		Matrix4 worldTransform = Matrix4.transformMatrix(position);
		Matrix4 rotation = Matrix4.rotationMatrixY(Angle.newAngleRadians(angle.y));
		rotation = Matrix4.rotationMatrixX(Angle.newAngleRadians(angle.x)).dot(rotation);
		rotation = Matrix4.rotationMatrixZ(Angle.newAngleRadians(angle.z)).dot(rotation);
		Matrix4 projection = Matrix4.perspectiveProjection(Angle.newAngleDegrees(90));
		
		Matrix4 transform = projection.dot(worldTransform.dot(rotation));
		int i = 0;
		for(Triangle t : triangles) {
			drawTriangle(t, transform);
		}
		
		g.drawImage(colorBuffer, 0, 0, getWidth(), getHeight(), null);
		
	}
	
	private void drawTriangle(Triangle triangle, Matrix4 transform) {
		Vector3[] corners = triangle.corners.clone();
		for(int i = 0; i < corners.length; i++) {
			Vector3 p = transform.dot(corners[i]);
			float x = p.x / -p.z * width / 2.0f + width / 2.0f;
			float y = -p.y / -p.z * height / 2.0f + height / 2.0f;
			corners[i] = new Vector3(x, y, p.z);
		}
		triangle.setRendered(corners.clone());
		Arrays.sort(corners, Comparator.comparingDouble(v -> v.y));
		if((int)corners[1].y == (int)corners[2].y) {
			fillBottomFlatTriangle(corners[0], corners[1], corners[2], triangle);
		}
		else if((int)corners[0].y == (int)corners[1].y) {
			fillTopFlatTriangle(corners[0], corners[1], corners[2], triangle);
		}else {
			float d = (corners[1].y - corners[0].y)/ (corners[2].y - corners[0].y);
			float dx = (corners[2].x - corners[0].x) * d;
			float dz = (corners[2].z - corners[0].z) * d;
			Vector3 v4 = new Vector3(corners[0].x + dx, corners[1].y, corners[0].z + dz);
			fillBottomFlatTriangle(corners[0], corners[1], v4, triangle);
			fillTopFlatTriangle(corners[1], v4, corners[2], triangle);
		}
	}
	
	private void fillBottomFlatTriangle(Vector3 v1, Vector3 v2, Vector3 v3, Triangle triangle) {	
		
		float invsSlopex1 = (v2.x - v1.x) / ((int)v2.y - (int)v1.y);
		float invsSlopex2 = (v3.x - v1.x) / ((int)v3.y - (int)v1.y);
		
		float invsSlopez1 = (v2.z - v1.z) / ((int)v2.y - (int)v1.y);
		float invsSlopez2 = (v3.z - v1.z) / ((int)v3.y - (int)v1.y);
		
		float currx1 = v1.x;
		float currx2 = v1.x;
		
		float currz1 = v1.z;
		float currz2 = v1.z;
		
		float depth = (v1.z + v2.z + v3.z) / 3;
		
		for(int y = (int)v1.y; y <= v2.y; y++) {
			Vector3 v11 = new Vector3(currx1, y, currz1);
			Vector3 v22 = new Vector3(currx2, y, currz2);
			
			drawLine(v11, v22, triangle, depth);
			
			currx1 += invsSlopex1;
			currx2 += invsSlopex2;
			
			currz1 += invsSlopez1;
			currz2 += invsSlopez2;
		}
	}
	private void fillTopFlatTriangle(Vector3 v1, Vector3 v2, Vector3 v3, Triangle triangle) {
		float invsSlopex1 = (v3.x - v1.x) / ((int)v3.y - (int)v1.y);
		float invsSlopex2 = (v3.x - v2.x) / ((int)v3.y - (int)v2.y);
		
		float invsSlopez1 = (v3.z - v1.z) / ((int)v3.y - (int)v1.y);
		float invsSlopez2 = (v3.z - v2.z) / ((int)v3.y - (int)v2.y);
		
		float currx1 = v3.x;
		float currx2 = v3.x;
		
		float currz1 = v3.z;
		float currz2 = v3.z;
		
		float depth = (v1.z + v2.z + v3.z) / 3;

		
		for(int y = (int)v3.y; y >= v1.y; y--) {
			Vector3 v11 = new Vector3(currx1, y, currz1);
			Vector3 v22 = new Vector3(currx2, y, currz2);
			drawLine(v11, v22, triangle, depth);
			
			currx1 -= invsSlopex1;
			currx2 -= invsSlopex2;
			
			currz1 -= invsSlopez1;
			currz2 -= invsSlopez2;
		}
	}
	private void drawLine(Vector3 v1, Vector3 v2, Triangle triangle, float depth) {
		if((int)v1.x == (int)v2.x) {
			float dy = v2.y - v1.y;
			float dz = v2.z - v1.z;
			float kz = dz / dy;

			int x = (int)v1.x;
			for(int y = (int)v1.y; y <= v2.y; y++) {
				float z = v1.z + (y - (int)v1.y) * kz;

				if(depthBuffer[x][y] < z) {
					int color = triangle.getColor(x, y);
					if((color & 0xFF << 24) != 0) {
						colorBuffer.setRGB(x, y, color);
						depthBuffer[x][y] = z;
					}

				}
			}
		}else {
			if(v1.x > v2.x) {
				Vector3 temp = v1;
				v1 = v2;
				v2 = temp;
			}
			float dx = v2.x - v1.x;
			float dy = v2.y - v1.y;
			float dz = v2.z - v1.z;
			float k = dy / dx;
			float kz = dz / dx;
			for(int x = (int)v1.x; x <= v2.x; x++) {
				int y = (int)(v1.y + (x - (int)v1.x) * k);
				float z = v1.z + (x - (int)v1.x) * kz;
				int color = triangle.getColor(x, y);
				if(depthBuffer[x][y] < z && (color & (0xFF << 24)) != 0) {
					colorBuffer.setRGB(x, y, color);
					depthBuffer[x][y] = z;

				}
			}
		}
	}

	private static List<Triangle> getCube(Vector3 offset){
		float w = 0.5f;
		float d = 0f;
		int[] rgb;
		int width;
		int height;
		try {
			BufferedImage image = ImageIO.read(new File("../res/cube.png"));
			width = image.getWidth();
			height = image.getHeight();
			rgb = new int[width * height];
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {
					rgb[y * width + x] = image.getRGB(x, y);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Triangle[] triangles =  new Triangle[] {
			//Top
			new Triangle(new Vector3(-w, w+d, -w), new Vector3(w, w+d, w), new Vector3(w, w+d, -w)).setTexture(new Vector3(0,0), new Vector3(1/3.0f, 1/2.0f), new Vector3(0, 1/2.0f), rgb, width, height),
			new Triangle(new Vector3(-w, w+d, -w), new Vector3(w, w+d, w), new Vector3(-w,w+d,w)).setTexture(new Vector3(0,0), new Vector3(1/3.0f, 1/2.0f), new Vector3(1/3.0f,0), rgb, width, height),	
			
			//Bottom
			new Triangle(new Vector3(-w, -w+d, -w), new Vector3(w, -w+d, w), new Vector3(w, -w+d, -w)).setTexture(new Vector3(2/3.0f,1/2.0f), new Vector3(1, 1), new Vector3(2/3.0f, 1), rgb, width, height),
			new Triangle(new Vector3(-w, -w+d, -w), new Vector3(w, -w+d, w), new Vector3(-w,-w+d,w)).setTexture(new Vector3(2/3.0f,1/2.0f), new Vector3(1, 1), new Vector3(1,1/2.0f), rgb, width, height),
			
			//Left
			new Triangle(new Vector3(-w-d, w, -w), new Vector3(-w-d, -w, w), new Vector3(-w-d, -w, -w)).setTexture(new Vector3(0, 1/2.0f), new Vector3(1/3.0f, 1), new Vector3(0, 1), rgb, width, height),
			new Triangle(new Vector3(-w-d, w, -w), new Vector3(-w-d, -w, w), new Vector3(-w-d, w, w)).setTexture(new Vector3(0, 1/2.0f), new Vector3(1/3.0f, 1), new Vector3(1/3.0f, 1/2.0f), rgb, width, height),	
			
			//Right
			new Triangle(new Vector3(w-d, w, -w), new Vector3(w-d, -w, w), new Vector3(w-d, -w, -w)).setTexture(new Vector3(2/3.0f, 0), new Vector3(1, 1/2.0f), new Vector3(2/3.0f, 1/2.0f), rgb, width, height),
			new Triangle(new Vector3(w-d, w, -w), new Vector3(w-d, -w, w), new Vector3(w-d, w, w)).setTexture(new Vector3(2/3.0f, 0), new Vector3(1, 1/2.0f), new Vector3(1, 0), rgb, width, height),	
			
			//Front
			new Triangle(new Vector3(-w, w, w+d), new Vector3(w, -w, w+d), new Vector3(-w, -w, w+d)).setTexture(new Vector3(1/3.0f, 1/2.0f), new Vector3(2/3.0f, 1), new Vector3(1/3.0f, 1), rgb, width, height),
			new Triangle(new Vector3(-w, w, w+d), new Vector3(w, -w, w+d), new Vector3(w, w, w+d)).setTexture(new Vector3(1/3.0f, 1/2.0f), new Vector3(2/3.0f, 1), new Vector3(2/3.0f, 1/2.0f), rgb, width, height),
			
			//Back
			new Triangle(new Vector3(-w, w, -w+d), new Vector3(w, -w, -w+d), new Vector3(-w, -w, -w+d)).setTexture(new Vector3(1/3.0f, 0), new Vector3(2/3.0f, 1/2.0f), new Vector3(1/3.0f, 1/2.0f), rgb, width, height),
			new Triangle(new Vector3(-w, w, -w+d), new Vector3(w, -w, -w+d), new Vector3(w, w, -w+d)).setTexture(new Vector3(1/3.0f, 0), new Vector3(2/3.0f, 1/2.0f), new Vector3(2/3.0f, 0f), rgb, width, height),
		};
		
		for(int i = 0; i < triangles.length; i++) {
			Triangle t = triangles[i];
			for(int j = 0; j < t.corners.length; j++) {
				t.corners[j] = t.corners[j].add(offset);				
			}
		}
		return Arrays.asList(triangles);
	}
	private static Triangle[] getColor(int color){
		int x = color % 3;
		int y = color / 3;
		float x1 = x * 1/3.0f;
		float x2 = (x+1) * 1/3.0f;
		float y1 = y * 1/2.0f;
		float y2 = (y+1) * 1/2.0f;
		Triangle t1 = new Triangle(new Vector3(x1, y1), new Vector3(x2, y2), new Vector3(x1, y2));
		Triangle t2 = new Triangle(new Vector3(x1, y1), new Vector3(x2, y2), new Vector3(x2, y1));
		return new Triangle[]{t1,t2};
	}
	private static List<Triangle> leftSide(Vector3 pos, int[] rgb, int width, int height){
		float w = 0.5f;
		float d = 0;
		return Arrays.asList (
			new Triangle(new Vector3(-w-d, w, -w), new Vector3(-w-d, -w, w), new Vector3(-w-d, -w, -w)).setTexture(new Vector3(0, 1/2.0f), new Vector3(1/3.0f, 1), new Vector3(0, 1), rgb, width, height),
			new Triangle(new Vector3(-w-d, w, -w), new Vector3(-w-d, -w, w), new Vector3(-w-d, w, w)).setTexture(new Vector3(0, 1/2.0f), new Vector3(1/3.0f, 1), new Vector3(1/3.0f, 1/2.0f), rgb, width, height)	
		);
	}
	private static List<Triangle> rightSide(Vector3 pos, int[] rgb, int width, int height){
		float w = 0.5f;
		float d = 0;
		return Arrays.asList (
			new Triangle(new Vector3(w-d, w, -w), new Vector3(w-d, -w, w), new Vector3(w-d, -w, -w)).setTexture(new Vector3(2/3.0f, 0), new Vector3(1, 1/2.0f), new Vector3(2/3.0f, 1/2.0f), rgb, width, height),
			new Triangle(new Vector3(w-d, w, -w), new Vector3(w-d, -w, w), new Vector3(w-d, w, w)).setTexture(new Vector3(2/3.0f, 0), new Vector3(1, 1/2.0f), new Vector3(1, 0), rgb, width, height)	
		);
	}
	private static List<Triangle> topSide(Vector3 pos, int[] rgb, int width, int height){
		float w = 0.5f;
		float d = 0;
		return Arrays.asList (
			new Triangle(new Vector3(-w, w+d, -w), new Vector3(w, w+d, w), new Vector3(w, w+d, -w)).setTexture(new Vector3(0,0), new Vector3(1/3.0f, 1/2.0f), new Vector3(0, 1/2.0f), rgb, width, height),
			new Triangle(new Vector3(-w, w+d, -w), new Vector3(w, w+d, w), new Vector3(-w,w+d,w)).setTexture(new Vector3(0,0), new Vector3(1/3.0f, 1/2.0f), new Vector3(1/3.0f,0), rgb, width, height)	
		);
	}
	private static List<Triangle> bottomSide(Vector3 pos, int[] rgb, int width, int height){
		float w = 0.5f;
		float d = 0;
		return Arrays.asList (
			new Triangle(new Vector3(-w, -w+d, -w), new Vector3(w, -w+d, w), new Vector3(w, -w+d, -w)).setTexture(new Vector3(2/3.0f,1/2.0f), new Vector3(1, 1), new Vector3(2/3.0f, 1), rgb, width, height),
			new Triangle(new Vector3(-w, -w+d, -w), new Vector3(w, -w+d, w), new Vector3(-w,-w+d,w)).setTexture(new Vector3(2/3.0f,1/2.0f), new Vector3(1, 1), new Vector3(1,1/2.0f), rgb, width, height)	
		);
	}
	private static List<Triangle> backSide(Vector3 pos, int[] rgb, int width, int height){
		float w = 0.5f;
		float d = 0;
		return Arrays.asList (
			new Triangle(new Vector3(-w, w, -w+d), new Vector3(w, -w, -w+d), new Vector3(-w, -w, -w+d)).setTexture(new Vector3(1/3.0f, 0), new Vector3(2/3.0f, 1/2.0f), new Vector3(1/3.0f, 1/2.0f), rgb, width, height),
			new Triangle(new Vector3(-w, w, -w+d), new Vector3(w, -w, -w+d), new Vector3(w, w, -w+d)).setTexture(new Vector3(1/3.0f, 0), new Vector3(2/3.0f, 1/2.0f), new Vector3(2/3.0f, 0f), rgb, width, height)	
		);
	}
	private static List<Triangle> frontSide(Vector3 pos, int[] rgb, int width, int height){
		float w = 0.5f;
		float d = 0;
		return Arrays.asList (
			new Triangle(new Vector3(-w, w, w+d), new Vector3(w, -w, w+d), new Vector3(-w, -w, w+d)).setTexture(new Vector3(1/3.0f, 1/2.0f), new Vector3(2/3.0f, 1), new Vector3(1/3.0f, 1), rgb, width, height),
			new Triangle(new Vector3(-w, w, w+d), new Vector3(w, -w, w+d), new Vector3(w, w, w+d)).setTexture(new Vector3(1/3.0f, 1/2.0f), new Vector3(2/3.0f, 1), new Vector3(2/3.0f, 1/2.0f), rgb, width, height)	
		);
	}
	
	private class SmallCube{
		List<Triangle> triangles;
		
		private SmallCube(List<Triangle> triangles) {
			this.triangles = triangles;
		}
		private void transform(Matrix4 matrix) {
			ListIterator<Triangle> it = triangles.listIterator();
			for(Triangle t : triangles) {
				for(int i = 0; i < 3; i++) {
					t.corners[i] = matrix.dot(t.corners[i]);
				}
			}
		}
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		double diffX = e.getPoint().x - startPoint.x;
		double diffY = e.getPoint().y - startPoint.y;
		
		Vector3 d = new Vector3((float)diffY / 200, (float)diffX / 200, 0);
		angle = angle.add(d);
		startPoint = e.getPoint();
	}
	@Override
	public void mousePressed(MouseEvent e) {
		startPoint = e.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("key");
		switch(e.getKeyCode()) {
			case KeyEvent.VK_J:
				moves.add(Move.U);
				break;
			case KeyEvent.VK_K:
				moves.add(Move.R_);
				break;
			case KeyEvent.VK_L:
				moves.add(Move.D_);
				break;
			case KeyEvent.VK_I:
				moves.add(Move.R);
				break;
			case KeyEvent.VK_F:
				moves.add(Move.U_);
				break;
			case KeyEvent.VK_D:
				moves.add(Move.L);
				break;
			case KeyEvent.VK_S:
				moves.add(Move.D);
				break;
			case KeyEvent.VK_E:
				moves.add(Move.L_);
				break;
			case KeyEvent.VK_H:
				moves.add(Move.F);
				break;
			case KeyEvent.VK_G:
				moves.add(Move.F_);
				break;
			case KeyEvent.VK_O:
				moves.add(Move.B_);
				break;
			case KeyEvent.VK_W:
				moves.add(Move.B);
				break;
			
		}
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}

