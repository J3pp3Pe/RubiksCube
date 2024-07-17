import javax.swing.JFrame;

import static rendering.Bajs.Move.*;

enum Side{
    RIGHT, LEFT, FRONT, BACK, TOP, BOTTOM
}
public class App extends JFrame{

    private final CubeRenderer renderer;

    public App(){
        RubikCube cube1 = new RubikCube(3);
        renderer = new CubeRenderer(1000, 1000);
        renderer.setCube(cube1);

        for(int i = 0; i < 100; i++){
            renderer.queueMove(R, U, R_, U_);
            renderer.queueMove(L_, U_, L, U);
            renderer.queueMove(U, R, U_, R_);
            renderer.queueMove(U_, L_, U, L);

        }
        

		setContentPane(renderer);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500,500);
		setVisible(true);
        setTitle("C==3");
		
		long lastTime = 0;
		long time = System.currentTimeMillis();
		while(true) {
			lastTime = time;
			time = System.currentTimeMillis();
			float dt = (time - lastTime) / 1000.0f;
            update(dt);
		}
    }
    public void update(float dt){
        renderer.update(dt);
    }
    public static void main(String[] args) throws Exception {
        

         RubikCube cube1 = new RubikCube(3);
        // Side side1 = new Side(3, 4);
        

        cube1.printCube();
        cube1.turnColumnACW(0);
        System.out.println();
        cube1.printCube();

        new App();
    }
}

