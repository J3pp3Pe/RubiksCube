import java.util.Arrays;

public class Cube {
    private int size;
    private Side top;
    private Side right;
    private Side left;
    private Side back;
    private Side front;
    private Side bottom;

    public Cube (int size){
        this.size = size;
        this.front = new Side(size, 1);
        this.right = new Side(size, 2);
        this.left = new Side(size, 4);
        this.top = new Side(size, 6);
        this.back = new Side(size, 3);
        this.bottom = new Side(size, 5);
    }

    public void printCube(){
        String offset = offset();
        Arrays.stream(top.getString()).forEach(element -> System.out.print(offset + element + "\n"));
        //middle part of cube
        String[][] middle = new String[4][size];
        middle[0] = left.getString();
        middle[1] = front.getString();
        middle[2] = right.getString();
        middle[3] = back.getString();
        middle = transpose(middle);
        for(int i = 0; i < middle.length; i++){
            Arrays.stream(middle[i]).forEach(element -> System.out.print(element + " "));
            System.out.println();
        }

        Arrays.stream(bottom.getString()).forEach(element -> System.out.print(offset + element + "\n"));
    }

    private String offset(){
        String spaces = " ".repeat(size*3);
        StringBuilder sb = new StringBuilder();
        sb.append(spaces).append(" ");
        return sb.toString();
    }

    private static String[][] transpose(String[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;

        String[][] result = new String[columns][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[j][i] = matrix[i][j];
            }
        }

        return result;
    }
}
