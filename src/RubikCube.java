import java.util.Arrays;

public class RubikCube {
    private int size;
    private RubikSide top;
    private RubikSide right;
    private RubikSide left;
    private RubikSide back;
    private RubikSide front;
    private RubikSide bottom;

    public RubikCube (int size){
        this.size = size;
        this.front = new RubikSide(size, 1);
        this.right = new RubikSide(size, 2);
        this.left = new RubikSide(size, 4);
        this.top = new RubikSide(size, 6);
        this.back = new RubikSide(size, 3);
        this.bottom = new RubikSide(size, 5);
    }

    public void turnRowACW(int row){
        int[] frontRow = front.getRow(row);
        front.setRow(row, left.getRow(row));
        left.setRow(row, back.getRow(row));
        back.setRow(row, right.getRow(row));
        right.setRow(row, frontRow);
        if(row == 0){
            top.rotateFaceACW();
        }else if (row == size-1){
            bottom.rotateFaceACW();
        }
    }
    
   public void turnRowCW(int row){
        int[] frontRow = front.getRow(row);
        front.setRow(row, right.getRow(row));
        right.setRow(row, back.getRow(row));
        back.setRow(row, left.getRow(row));
        left.setRow(row, frontRow);
        if(row == 0){
            top.rotateFaceCW();
        }else if (row == size-1){
            bottom.rotateFaceACW();
        }
    }

    public void turnColumnCW(int col){
        int[] frontCol = front.getCol(col);
        front.setCol(col, bottom.getCol(col));
        bottom.setCol(col, back.getCol(size -col-1));
        back.setCol(size-col-1, top.getCol(col));
        top.setCol(col, frontCol);
        if(col == 0){
            left.rotateFaceACW();
        }else if (col == size-1){
            right.rotateFaceCW();
        }
    }
    public void turnColumnACW(int col){
        int[] frontCol = front.getCol(col);
        front.setCol(col, top.getCol(col));
        top.setCol(col, back.getCol(size -col-1));
        back.setCol(size-col-1, bottom.getCol(col));
        bottom.setCol(col, frontCol);
        if(col == 0){
            left.rotateFaceCW();
        }else if (col == size-1){
            right.rotateFaceACW();
        }
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
    public RubikSide getTop(){
        return (RubikSide)this.top.clone();
    }
    public RubikSide getRight(){
        return (RubikSide)this.right.clone();
    }
    public RubikSide getLeft(){
        return (RubikSide)this.left.clone();
    }
    public RubikSide getBack(){
        return (RubikSide)this.back.clone();
    }
      public RubikSide getFront(){
        return (RubikSide)this.front.clone();
    }
      public RubikSide getBottom(){
        return (RubikSide)this.bottom.clone();
    }
}
