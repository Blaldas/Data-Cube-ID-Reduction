package reducedIDStorageMiexCrompressionChangedSubCubeQuery;

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
        proneShellFragment();
    }

    /**
     * @param upper biggest value to store
     * @param lower lowest value yo store
     *              <p>
     *              used to create subCubes!
     */
    ShellFragment(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
        matrix = new DIntArray[upper - lower + 1];
    }

    /**
     * @param tid      the tid to add - what to add
     * @param tidValue the valye to add - where to add
     */
    public void addTuple(int tid, int tidValue) {
        if (matrix[tidValue - lower] == null) {
            matrix[tidValue - lower] = new DIntArray();
        }
        matrix[tidValue - lower].addTid(tid);
    }

    public void proneShellFragment() {
        for (DIntArray d : matrix) {
            if (d == null)
                continue;
            d.proneDIntArray();
        }
    }

    private void fillMatrix(int[][] rawData, int column) {


        for (int i = 0; i < rawData.length; i++) {          //para cada uma das tuples

            if (matrix[rawData[i][column] - lower] == null)
                matrix[rawData[i][column] - lower] = new DIntArray();

            matrix[rawData[i][column] - lower].addTid(i);
        }

    }


    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0. Care that the array of a found value may be zero as well, so it's not a flag
     */
    public DIntArray getTidsListFromValue(int value) {
        if (value > upper || value < lower || matrix[value - lower] == null)
            return new DIntArray();
        return matrix[value - lower];
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

        //para cada dimensão
        for (DIntArray dIntArray : matrix) {
            if (dIntArray == null)
                continue;
            int a = dIntArray.getBigestTid();
            if (a > max)
                max = a;
        }
        return max;                                                             //devolve max
    }

    public int[][] copyMatrix(int[][] matrix, int length) {
        int[][] a = new int[length][];
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
