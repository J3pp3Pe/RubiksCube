import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Side {
    private int size;
    private int[][] values;

    public Side (int size, int color){
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

    public void rotateFace(){
        int[] row1 = getRow(0);
        int[] row2 = getRow(size-1);
        int[] col1 = getCol(0);
        int[] col2 = getCol(size-1);
        setRow(0, col2);
        setRow(size-1, col1);
        setCol(0, reverse(row1));
        setCol(size-1, reverse(row2));
    }
}

