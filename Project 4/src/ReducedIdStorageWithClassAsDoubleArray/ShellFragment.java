package ReducedIdStorageWithClassAsDoubleArray;

public class ShellFragment {

    DIntArray[] matrix;
    //int[] size;
    int lower;
    int upper;

    ShellFragment(int[][] rawData, int column, int lower, int upper) {
        this.lower = lower;
        this.upper = upper;


        matrix = new DIntArray[upper - lower + 1];      //aloca array de DoubleIntArray com o tamanho necessário
        //size = new int[upper - lower + 1];              //aloca array de sizes com tamnho necessário

        fillMatrix(rawData, column);

    }


    private void fillMatrix(int[][] rawData, int column) {


        for (int i = 0; i < rawData.length; i++) {          //para cada uma das tuples

            if(matrix[rawData[i][column] - lower] == null)
                matrix[rawData[i][column] - lower] = new DIntArray(1);

            if (matrix[rawData[i][column] - lower].size == 0 || matrix[rawData[i][column] - lower].size == matrix[rawData[i][column] - lower].getLength()) {                //se não houver espaço para guardar tid no valor a ser looped

                DIntArray b = new DIntArray(matrix[rawData[i][column] - lower].size == 0 ?                                                           //se o tamanho for zero
                        1 : (int) (matrix[rawData[i][column] - lower].size * calculateGrowingRatio(rawData[i][column] - lower, rawData.length)) <= matrix[rawData[i][column] - lower].size ?
                        matrix[rawData[i][column] - lower].size + 1 : (int) (matrix[rawData[i][column] - lower].size * calculateGrowingRatio(rawData[i][column] - lower, rawData.length)));

                if (matrix[rawData[i][column] - lower].size != 0) {
                    b.size = matrix[rawData[i][column] - lower].size;
                    b.SetArray1(matrix[rawData[i][column] - lower].array1);
                    b.SetArray2(matrix[rawData[i][column] - lower].array2);
                }

                matrix[rawData[i][column] - lower] = b;                                                                             //coloca a apontar para o novo array
            }

            //caso seja o primeiro OU não seja incremenyto
            if (matrix[rawData[i][column] - lower].size == 0 || (i - getLastValue(rawData[i][column])) != 1) {
                matrix[rawData[i][column] - lower].array1[matrix[rawData[i][column] - lower].size] = i;
                matrix[rawData[i][column] - lower].size++;
            } else {                  //caso seja incremento da última posição -> acrescenta ou troca valor no array 2
                matrix[rawData[i][column] - lower].array2[matrix[rawData[i][column] - lower].size - 1] = i;
            }
        }
    }


    /**
     * @param i      the index of the array
     * @param length the total data lenght
     * @return a multipler between ]1,2]
     */
    private float calculateGrowingRatio(int i, int length) {
        float r = 1.1f + (1f - ((float) matrix[i].size / (float) length) * 2);     //formula simples para obter o ratio de crescimento
        if (r > 2)                                                              //restrição a 2
            return 2;
        else if (r < 1)                                                          //restrição a 1.1
            return 1.1f;
        return r;
    }

    //returns the last value stored for value i
    private int getLastValue(int val) {
        if (matrix[val - lower].array2[matrix[val - lower].size - 1] != -1)
            return matrix[val - lower].array2[matrix[val - lower].size - 1];
        return matrix[val - lower].array1[matrix[val - lower].size - 1];
    }


    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0. Care that the array of a found value may be zero as well, so it's not a flag
     */
    public DIntArray getTidsListFromValue(int value) {
        if (value > upper || value < lower || matrix[value - lower] == null)
            return new DIntArray(0);
        return matrix[value - lower];      //talvez seja melhor nao usar isto, enviuar o array e que eles testem até ao null
    }

    /**
     * @param tid id of the tuple to be seached
     * @return the value of such tuple, or lower-1 if not found.
     */
    public int getValueFromTid(int tid) {
        for (int i = 0; i < matrix.length; i++) {                   //para cada uma das linhas
            //attempt to binary search
            int start = 0;
            int end = matrix[i].size - 1;
            int mid;
            while (start <= end){
                mid = (end + start) / 2;
                if (matrix[i].array1[mid] == tid || (matrix[i].array1[mid] < tid && matrix[i].array2[mid] != -1 && matrix[i].array2[mid] >= tid))
                {
                    return lower + i;
                }
                else if (matrix[i].array1[mid] > tid)
                    end = mid - 1;
                else
                    start = mid + 1;

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

        for (int i = 0; i < matrix.length; i++)                                   //para cada dimensão
            if (matrix[i] != null && matrix[i].size != 0)                                                       //se o tamanho da dimensão for maior que zero
                if (matrix[i].array2[matrix[i].size - 1] > max)                                    //se a segunda posição for maior que zero
                    max = matrix[i].array2[matrix[i].size - 1];                                        //max guarda a ultima posição
                else if (matrix[i].array1[matrix[i].size - 1] > max)                               //senão, se a primeira posição for maior que zero
                    max = matrix[i].array1[matrix[i].size - 1];                                        //max guarda a ultima posição
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
