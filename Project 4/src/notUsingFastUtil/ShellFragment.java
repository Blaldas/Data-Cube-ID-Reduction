package notUsingFastUtil;


import java.util.Arrays;
import java.util.Date;

public class ShellFragment {

    int[][] matrix;
    int[] size;
    int lower;
    int upper;


    ShellFragment( int lower, int upper){
        this.lower = lower;
        this.upper = upper;
        this.size = new int[upper - lower + 1];
        this.matrix = new int[upper - lower + 1][0];

    }

    public void addTuple(int tid, int value){
        //verifica o tamanho
        if(size[value-lower] == matrix[value-lower].length)
        {
            int[] a = new int[size[value-lower] == 0 ? 1 : 2* size[value-lower]];
            for(int i = size[value-lower]; i>0; a[--i] = matrix[value-lower][i]){}

            matrix[value-lower] = a;
        }

        matrix[value-lower][size[value-lower]++] = tid;
    }


    public void proneShellFragment(){
        int[] b;
        for(int v = 0; v < matrix.length; v++){
            b = new int[size[v]];
            for(int i = size[v]; i>0; b[--i] = matrix[v][i]){}
            matrix[v] = b;

        }
    }




    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0.
     * Care that the array of a found value may be zero as well, so it's not a flag
     */
    public int[] getTidsListFromValue(int value) {
        if (value > upper || value < lower)                         //se os valores nao estiverem nos intervalos
            return new int[0];                                          //devolve array a zero

        return matrix[value - lower];
    }
    public int[] getTidsListFromValueWithoutPronage(int value) {
        if (value > upper || value < lower)                         //se os valores nao estiverem nos intervalos
            return new int[0];                                          //devolve array a zero

        return Arrays.copyOfRange(matrix[value - lower], 0, size[value-lower]);
    }


    /**
     * @return all the values being stored
     */
    public int[] getAllValues() {
        int[] returnable = new int[matrix.length];                  //aloca array com tamanho de todos os valores
        for (int i = 0; i < returnable.length; i++)                 //para cada uma das poisções do array
            returnable[i] = lower + i;                                  //coloca o valor devido
        return returnable;                                          //devolve array com valores
    }


    /**
     * @return returns an array with all the tids
     * <p>This function should actually be ignored, the method getBiggestTid() should be used instead
     */
    public int[] getAllTids() {
        int b = getBiggestTid();                                //obtem o maior tid
        int[] returnable = new int[b + 1];                      //aloca array com o tamanho do maior tid+1

        for (int i = 0; i < returnable.length; i++)             //para cada uma das posições
            returnable[i] = i;                                  //coloca o seu index

        return returnable;
    }

    /**
     * @return the biggest tid stored int the shellfragment
     */
    public int getBiggestTid() {
        int max = -1;                                   //var max stores the biggest tid
        int n;
        for (int i = 0; i < matrix.length; i++) {                       //for each value
            if (size[i] > 0 && max < (n = matrix[i][size[i] - 1]))          //if the number ofd arrays stored is greater than zero and its las value is bigger than max
                max = n;                                                        //stores the greayer value in max
        }
        return max;                                             //returns the biggest value
    }

}
