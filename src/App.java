
enum Side{
    RIGHT, LEFT, FRONT, BACK, TOP, BOTTOM
}
public class App {
    public static void main(String[] args) throws Exception {
        

         RubikCube cube1 = new RubikCube(3);
        // Side side1 = new Side(3, 4);
        

        cube1.printCube();
        cube1.turnRowCW(0);
        System.out.println();
        cube1.printCube();
    }
}

