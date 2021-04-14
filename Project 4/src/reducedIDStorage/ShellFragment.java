package reducedIDStorage;

import java.util.Arrays;

public class ShellFragment {

    int[][][] matrix;
    int[] size;
    int lower;
    int upper;

    ShellFragment(int[][] rawData, int column, int lower, int upper) {
        this.lower = lower;
        this.upper = upper;


        matrix = new int[upper - lower + 1][0][0];
        size = new int[upper - lower + 1];
        Arrays.fill(size, 0);

        fillMatrix(rawData, column);

    }


    private void fillMatrix(int[][] rawData, int column) {


        for (int i = 0; i < rawData.length; i++) {
            if (size[rawData[i][column] - lower] == matrix[rawData[i][column] - lower].length) {
                int[][] b = new int[size[rawData[i][column] - lower] == 0 ?                                                           //se o tamanho for zero
                        1 : (int) (size[rawData[i][column] - lower] * calculateGrowingRatio(rawData[i][column] - lower, rawData.length)) <= size[rawData[i][column] - lower] ?
                        size[rawData[i][column] - lower] + 1 : (int) (size[rawData[i][column] - lower] * calculateGrowingRatio(rawData[i][column] - lower, rawData.length))][];//coloca tamanhoa a 2, senão chama função que indica o ratio de crescimento

                for (int n = size[rawData[i][column] - lower]; n-- != 0; b[n] = matrix[rawData[i][column] - lower][n]) {
                }    //copia os valores do anyigo array para o novo -> verificvar que funciona em 3d, uma vez que nao copia os valores, apenas os redistribui
                matrix[rawData[i][column] - lower] = b;                                                                             //coloca a apontar para o novo array
            }

            //caso seja o primeiro OU não seja incremenyto
            if (size[rawData[i][column] - lower] == 0 || i - matrix[rawData[i][column] - lower][size[rawData[i][column] - lower - 1]][matrix[rawData[i][column] - lower][size[rawData[i][column] - lower] - 1].length - 1] != 0) {
                matrix[rawData[i][column] - lower][size[rawData[i][column] - lower]] = new int[1];
                matrix[rawData[i][column] - lower][size[rawData[i][column] - lower]][0] = i;
            }//caso seja incremento da última posição
            else {
                if (matrix[rawData[i][column] - lower][size[rawData[i][column] - lower - 1]].length == 1) {
                    int b[] = new int[2];
                    b[0] = matrix[rawData[i][column] - lower][size[rawData[i][column] - lower - 1]][0];
                    b[1] = i;
                    matrix[rawData[i][column] - lower][size[rawData[i][column] - lower - 1]] = b;
                } else
                    matrix[rawData[i][column] - lower][size[rawData[i][column] - lower - 1]][1] = i;
            }

        }
    }


    /**
     * @param i      the index of the array
     * @param length the total data lenght
     * @return a multipler between ]1,2]
     */
    private float calculateGrowingRatio(int i, int length) {
        float r = 1.1f + (1f - ((float) size[i] / (float) length) * 2);     //formula simples para obter o ratio de crescimento
        if (r > 2)                                                              //restrição a 2
            return 2;
        else if (r < 1)                                                          //restrição a 1.1
            return 1.1f;
        return r;
    }

    //returns the last value stored for value i
    private int getLastValue(int val) {
        return matrix[val - lower][size[val - lower] - 1][matrix[val - lower][size[val - lower] - 1].length - 1];
    }


    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0. Care that the array of a found value may be zero as well, so it's not a flag
     */
    public int[][] getTidsListFromValue(int value) {
        if (value > upper || value < lower)
            return new int[0][0];
        return matrix[value - lower];
    }

    /**
     * @param tid id of the tuple to be seached
     * @return the value of such tuple, or lower-1 if not found.
     */
    public int getValueFromTid(int tid) {
        for (int i = 0; i < matrix.length; i++) {                   //para cada uma das linhas
            for (int[] v : matrix[i]) {                                 //para cada coluna das linhas
                if (v[0] == tid)                                            //se tiver o id pretendiso
                    return lower + i;                                           //devolve logo o valor
                else if (v.length == 2 && v[0] >= tid && v[1] <= tid)       //se tiver tamanho 2 e o id estiver entre os valores
                    return lower + i;                                           //devolve logo o valor
                else if (v[0] > tid)                                        //se ids forem superiores - eficiencia
                    break;                                                  //faz break;
            }
        }
        return lower - 1;
    }


    public int getBigestValue() {
        return upper;
    }

    /**
     * @return all the values being stored
     */
    public int[] getAllValues() {
        int[] returnable = new int[matrix.length];

        for (int i = 0; i < returnable.length; returnable[i++] = lower + i) {
        }
        return returnable;
    }


    public int[] getAllTids() {
        int b = getBiggestTid();
        int[] returnable = new int[b + 1];

        for (int i = 0; i < returnable.length; i++)
            returnable[i] = i;

        return returnable;
    }

    public int getBiggestTid() {
        int max = -1;                                                                           //coloca um valor inicial nunca returnavel em max

        for (int i = 0; i < size.length; i++)                                                  //para cada dimensão
            if (size[i] != 0 && matrix[i][size[i] - 1][matrix[i][size[i] - 1].length - 1] > max)    //se o tamanho da dimensão for maior que zero e a ultima posição for maior que max
                max = matrix[i][size[i] - 1][matrix[i][size[i] - 1].length - 1];                        //max guarda a ultima posição

        return max;                                                                             //devolve max
    }


}
