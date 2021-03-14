import java.util.HashMap;
import java.util.List;

public class DataCube {


    String[] dimensions;        //there will be 3 dimentions for this example
    int[][][] dataCube;         //array with 3 dimentions


    public DataCube(String[] dimensions, HashMap<String, HashMap<String, List<Integer>>> invertedIndex){
        this.dimensions = dimensions;

        computeCube(invertedIndex);
    }

    private void computeCube(HashMap<String, HashMap<String, List<Integer>>> invertedIndex) {
        int[] sizes = new int[3];   //stores the size of each dimension

        //obtains the size of each dimention. The relation between the sizes array and the string array is index-based
        for(int i = 0; i<3; i++){
            sizes[i] = invertedIndex.get(dimensions[i]).size();
        }

        //allocates space to the data cube
        dataCube = new int[sizes[0]][sizes[1]][sizes[2]];

        //adds the data into the data cube


    }
}
