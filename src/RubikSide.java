import java.util.Arrays;
import java.util.stream.IntStream;

public class RubikSide {
    private int size;
    private int[][] values;

    public RubikSide (int size, int color){
        this.size = size;
        int[] dimension = IntStream.generate(() -> color).limit(size).toArray();
        values = IntStream.range(0, size)
                .boxed()
                .map(i -> dimension.clone())
                .toArray(int[][]::new);

        
    }
    public String[] getString(){
        String[] result = new String[size];
        for(int i = 0; i < result.length; i++){
            result[i] = Arrays.toString(getRow(i));
        }
        
        return result;
    }

    public int[] getRow(int row){
        return values[row];
    }
    public int get(int row, int col){
        return values[row][col];
    }

    public int[] getCol(int col){
        return IntStream.range(0, size)
            .map(i -> values[i][col]).toArray();
    }

    public void setRow(int row, int[] content){
        values[row] = content.clone();
    }
    public void setCol(int col, int[] content){
        for(int i = 0; i < values.length; i++){
            values[i][col] = content[i];
        }
    }

    private static int[] reverse(int[] content){
        int[] res = new int[content.length];
        for(int i = 0; i < content.length; i++){
            res[i] = content[content.length-i-1];
        }

        return res;
    }

    public void rotateFaceACW(){
        int[] row1 = getRow(0);
        int[] row2 = getRow(size-1);
        int[] col1 = getCol(0);
        int[] col2 = getCol(size-1);
        setRow(0, col2);
        setRow(size-1, col1);
        setCol(0, reverse(row1));
        setCol(size-1, reverse(row2));
    }
    public void rotateFaceCW(){
        int[] row1 = getRow(0);
        int[] row2 = getRow(size-1);
        int[] col1 = getCol(0);
        int[] col2 = getCol(size-1);
        setRow(0, reverse(col1));
        setRow(size-1, reverse(col2));
        setCol(0, row2);
        setCol(size-1, row1);
    }
    @Override
    public Object clone(){
        RubikSide newSide = new RubikSide(this.size, 0);
        for(int i = 0; i < this.values.length; i++){
            for(int j = 0; j < this.values[i].length; j++){
                newSide.values[i][j] = this.values[i][j];
            }
        }
        return newSide;
    }


    
}

