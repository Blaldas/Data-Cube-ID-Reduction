package ReducedIdStorageWithClassAsDoubleArray;

public class ShellFragment {

    DIntArray[] matrix;
    int[] size;
    int lower;
    int upper;

    ShellFragment(int[][] rawData, int column, int lower, int upper) {
        this.lower = lower;
        this.upper = upper;


        matrix = new DIntArray[upper - lower + 1];      //aloca array de DoubleIntArray com o tamanho necessário
        size = new int[upper - lower + 1];              //aloca array de sizes com tamnho necessário

        fillMatrix(rawData, column);

    }


    private void fillMatrix(int[][] rawData, int column) {


        for (int i = 0; i < rawData.length; i++) {          //para cada uma das tuples

            if (size[rawData[i][column] - lower] == 0 || size[rawData[i][column] - lower] == matrix[rawData[i][column] - lower].getLength()) {                //se não houver espaço para guardar tid no valor a ser looped

                DIntArray b = new DIntArray(size[rawData[i][column] - lower] == 0 ?                                                           //se o tamanho for zero
                        1 : (int) (size[rawData[i][column] - lower] * calculateGrowingRatio(rawData[i][column] - lower, rawData.length)) <= size[rawData[i][column] - lower] ?
                        size[rawData[i][column] - lower] + 1 : (int) (size[rawData[i][column] - lower] * calculateGrowingRatio(rawData[i][column] - lower, rawData.length)));

                if (size[rawData[i][column] - lower] != 0) {
                    b.SetArray1(matrix[rawData[i][column] - lower].array1);
                    b.SetArray2(matrix[rawData[i][column] - lower].array2);
                }

                matrix[rawData[i][column] - lower] = b;                                                                             //coloca a apontar para o novo array
            }

            //caso seja o primeiro OU não seja incremenyto
            if (size[rawData[i][column] - lower] == 0 || (i - getLastValue(rawData[i][column])) != 1) {
                matrix[rawData[i][column] - lower].array1[size[rawData[i][column] - lower]] = i;
                size[rawData[i][column] - lower]++;
            } else {                  //caso seja incremento da última posição -> acrescenta ou troca valor no array 2
                matrix[rawData[i][column] - lower].array2[size[rawData[i][column] - lower] - 1] = i;
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
        if (matrix[val - lower].array2[size[val - lower] - 1] != -1)
            return matrix[val - lower].array2[size[val - lower] - 1];
        return matrix[val - lower].array1[size[val - lower] - 1];
    }


    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0. Care that the array of a found value may be zero as well, so it's not a flag
     */
    public int[][] getTidsListFromValue(int value) {
        if (value > upper || value < lower)
            return new int[0][0];
        return matrix[value - lower].get2dMatrix(size[value - lower]);      //talvez seja melhor nao usar isto, enviuar o array e que eles testem até ao null
    }

    /**
     * @param tid id of the tuple to be seached
     * @return the value of such tuple, or lower-1 if not found.
     */
    public int getValueFromTid(int tid) {
        for (int i = 0; i < matrix.length; i++) {                   //para cada uma das linhas
            for (int v = 0; v < size[i]; v++) {                                 //para cada coluna das linhas
                if (matrix[i].array1[v] == tid)                                            //se tiver o id pretendiso
                    return lower + i;                                           //devolve logo o valor
                else if (matrix[i].array2[v] != -1 && matrix[i].array1[v] <= tid && matrix[i].array2[v] >= tid)       //se tiver tamanho 2 e o id estiver entre os valores
                    return lower + i;                                           //devolve logo o valor
                else if (matrix[i].array1[v] > tid)                                        //se ids forem superiores - eficiencia
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

        for (int i = 0; i < returnable.length; returnable[i] = lower + i++) {
        }
        return returnable;
    }


    public int getBiggestTid() {
        int max = -1;                                                                           //coloca um valor inicial nunca returnavel em max

        for (int i = 0; i < size.length; i++)                                   //para cada dimensão
            if (size[i] != 0)                                                       //se o tamanho da dimensão for maior que zero
                if (matrix[i].array2[size[i] - 1] > max)                                    //se a segunda posição for maior que zero
                    max = matrix[i].array2[size[i] - 1];                                        //max guarda a ultima posição
                else if (matrix[i].array1[size[i] - 1] > max)                               //senão, se a primeira posição for maior que zero
                    max = matrix[i].array1[size[i] - 1];                                        //max guarda a ultima posição
        return max;                                                             //devolve max
    }

    public int[][] copyMatrix(int[][] matrix, int length) {
        int a[][] = new int[length][];
        for (int i = 0; i < length; a[i] = matrix[i++]) {
        }

        return a;

    }

    public int[] getAllTids() {
        int b = getBiggestTid();
        int[] returnable = new int[b + 1];

        for (int i = 0; i < returnable.length; i++)
            returnable[i] = i;

        return returnable;
    }
}
