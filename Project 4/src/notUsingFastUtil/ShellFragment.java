package notUsingFastUtil;

import it.unimi.dsi.fastutil.ints.IntArrays;

import java.util.Arrays;
import java.util.Date;

public class ShellFragment {

    int[][] matrix;
    int[] size;
    int lower;
    int upper;


    ShellFragment(int[][] rawData, int column, int lower, int upper) {
        this.lower = lower;                                 //guarda lower
        this.upper = upper;                                 //guarda upper

        matrix = new int[upper - lower + 1][0];             //aloca o número de linhas necssárias para a matrix -> uma linha == 1 valor
        size = new int[upper - lower + 1];                  //aloca um contador para cada linha da matrix
        //fillMatrixB(rawData, column);                                //chama método que coloca os valores na matrix
        fillMatrixA(rawData, column);
    }

    private void fillMatrixA(int[][] rawData, int column) {
        int counter[] = new int[matrix.length];
        //obtem o tamanho de todas as dimensões
        for (int[] rawDatum : rawData) {
            counter[rawDatum[column] - lower]++;
        }
        //aloca os tamanhos expressamente necessários
        for (int i = 0; i < matrix.length; i++)
            matrix[i] = new int[counter[i]];

        for (int i = 0; i < rawData.length; i++) {
            matrix[rawData[i][column] - lower][size[rawData[i][column] - lower]] = i;                                           //coloca o novo valor no final do array
            size[rawData[i][column] - lower]++;
        }

    }

    private void fillMatrixB(int[][] rawData, int column) {

        for (int i = 0; i < rawData.length; i++) {                                                                      //para cada uma dos tuples

            if (size[rawData[i][column] - lower] == matrix[rawData[i][column] - lower].length) {//se o tamanho máximo do array for igual ao tamanho
                int[] b = new int[size[rawData[i][column] - lower] == 0 ?                                                           //se o tamanho for zero
                        1 : (int) (size[rawData[i][column] - lower] * calculateGrowingRatio(rawData[i][column] - lower, rawData.length)) <= size[rawData[i][column] - lower] ?
                        size[rawData[i][column] - lower] + 1 : (int) (size[rawData[i][column] - lower] * calculateGrowingRatio(rawData[i][column] - lower, rawData.length))];//coloca tamanhoa a 2, senão chama função que indica o ratio de crescimento

                for (int n = size[rawData[i][column] - lower]; n-- != 0; b[n] = matrix[rawData[i][column] - lower][n]) {
                }    //copia os valores do anyigo array para o novo
                matrix[rawData[i][column] - lower] = b;                                                                             //coloca a apontar para o novo array
            }

            matrix[rawData[i][column] - lower][size[rawData[i][column] - lower]] = i;                                           //coloca o novo valor no final do array
            size[rawData[i][column] - lower]++;                                                                         //aumenta o devido counter
        }

        int[][] oldArray = new int[1][1];
        int[][] newArray = new int[oldArray.length + 1][];
        for (int n = oldArray.length; n-- != 0; newArray[n] = oldArray[n]) {}

    }


    /**
     * @param i      the index of the array
     * @param length the total data lenght
     * @return a multipler between ]1.1,2]
     */
    private float calculateGrowingRatio(int i, int length) {
        float r = 1.1f + (1f - ((float) size[i] / (float) length) * 2);     //formula simples para obter o ratio de crescimento
        if (r <= 1f)                                                          //restrição a 1.1
            return 1.1f;
        return r;
    }

    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0.
     * Care that the array of a found value may be zero as well, so it's not a flag
     */
    public int[] getTidsListFromValue(int value) {
        if (value > upper || value < lower)                         //se os valores nao estiverem nos intervalos
            return new int[0];                                          //devolve array a zero

        /*int[] returnable = new int[matrix[value - lower].length];
        // for(int i =0; i < returnable.length; i++)
            returnable[i] = matrix[value - lower][i];

        return returnable;       //devolver os valores

         */
        return matrix[value - lower];
    }

    /**
     * @param tid id of the tuple to be seached
     * @return the value of such tuple, or lower-1 if not found.
     */
    public int getValueFromTid(int tid) {
        for (int i = 0; i < matrix.length; i++) {                                     //para cada um dos valores (arrays de tids)
            int pos = IntArrays.binarySearch(matrix[i], 0, size[i], tid);                 //faz pesquisa binária
            if (pos >= 0)                                                //se a pesquisa binária der resultado positivo (o resultado é a posição)
                return lower + i;                                                  //devole valor da posição
        }
        return lower - 1;                               //devove valor menor que o minimo
    }


    public int getBigestValue() {
        return upper;
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

    public int getNumberUnnusedInts() {
        int total = 0;

        for (int i = 0; i < size.length; i++)
            total += matrix[i].length - size[i];

        return total;
    }

    public int getNumberUsedInts() {
        int total = 0;

        for (int i = 0; i < size.length; i++)
            total += size[i];

        return total;
    }
}
