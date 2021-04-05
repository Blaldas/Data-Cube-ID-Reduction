package normalIDStorage;

public class ShellFragment {

    int[][] matrix;
    int lower;
    int upper;

    ShellFragment(int[] rawData, int lower, int upper) {
        this.lower = lower;
        this.upper = upper;

        matrix = new int[upper - lower + 1][0];

        fillMatrix(rawData);
    }

    ShellFragment(int[] rawData, int lower, int upper, int size) {
        this.lower = lower;
        this.upper = upper;

        matrix = new int[upper - lower + 1][0];

        fillMatrix(rawData, size);
        System.gc();
    }

    ShellFragment(int[] rawData, int lower, int upper, int size, int num) {
        this.lower = lower;
        this.upper = upper;

        matrix = new int[upper - lower + 1][0];

        fillMatrix(rawData, size, num);
        System.gc();
    }


    private void fillMatrix(int[] rawData) {
        for (int i = 0; i < rawData.length; i++) {                                                                          //para cada uma dos tuples
            int[] secundary = new int[matrix[rawData[i] - lower].length + 1];                                                   //cria array secundário
            System.arraycopy(matrix[rawData[i] - lower], 0, secundary, 0, matrix[rawData[i] - lower].length);
            matrix[rawData[i] - lower] = secundary;
            matrix[rawData[i] - lower][matrix[rawData[i] - lower].length - 1] = i;
        }
    }

    private void fillMatrix(int[] rawData, int size) {
        int[] counter = new int[matrix.length];
        int[][] secundary = new int[matrix.length][0];
        for (int i = 0; i < secundary.length; i++)
            secundary[i] = new int[size / 8];

        for (int i = 0; i < rawData.length; i++) {                                                                          //para cada uma dos tuples
            if (counter[rawData[i] - lower] < secundary[rawData[i] - lower].length) {                                      //caso ainda haja espaço
                secundary[rawData[i] - lower][counter[rawData[i] - lower]] = i;                                                 //adiciona valor na posição
                counter[rawData[i] - lower]++;                                                                                      //aumenta counter
            } else {
                int[] terciary = new int[secundary[rawData[i] - lower].length + 1];                                                   //cria array secundário
                System.arraycopy(secundary[rawData[i] - lower], 0, terciary, 0, secundary[rawData[i] - lower].length);
                secundary[rawData[i] - lower] = terciary;
                secundary[rawData[i] - lower][secundary[rawData[i] - lower].length - 1] = i;
                counter[rawData[i] - lower]++;                                                                                      //aumenta counter
            }
        }

        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = new int[counter[i]];
            System.arraycopy(secundary[i], 0, matrix[i], 0, counter[i]);
        }
    }

    private void fillMatrix(int[] rawData, int size, int num) {
        int[] counter = new int[matrix.length];
        int[][] secundary = new int[matrix.length][0];

        for (int i = 0; i < secundary.length; i++)
            if (num > 9 && num < 54)
                secundary[i] = new int[size];
            else if (num <= 9)
                secundary[i] = new int[size / 8];
            else
                secundary[i] = new int[size / 2];

        for (int i = 0; i < rawData.length; i++) {                                                                          //para cada uma dos tuples
            if (counter[rawData[i] - lower] < secundary[rawData[i] - lower].length) {                                      //caso ainda haja espaço
                secundary[rawData[i] - lower][counter[rawData[i] - lower]] = i;                                                 //adiciona valor na posição
                counter[rawData[i] - lower]++;                                                                                      //aumenta counter
            } else {
                int[] terciary = new int[secundary[rawData[i] - lower].length + 1];                                                   //cria array secundário
                System.arraycopy(secundary[rawData[i] - lower], 0, terciary, 0, secundary[rawData[i] - lower].length);
                secundary[rawData[i] - lower] = terciary;
                secundary[rawData[i] - lower][secundary[rawData[i] - lower].length - 1] = i;
                counter[rawData[i] - lower]++;                                                                                      //aumenta counter
            }
        }

        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = new int[counter[i]];
            System.arraycopy(secundary[i], 0, matrix[i], 0, counter[i]);
        }
    }

    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0. Care that the array of a found value may be zero as well, so it's not a flag
     */
    public int[] getTidsListFromValue(int value) {
        if (value > upper || value < lower)
            return new int[0];
        return matrix[value - lower];
    }

    /**
     * @param tid id of the tuple to be seached
     * @return the value of such tuple, or -999 if not found.
     */
    public int getValueFromTid(int tid) {
        for (int i = 0; i < matrix.length; i++) {
            for (int v : matrix[i]) {
                if (v == tid)
                    return lower + i;
            }
        }
        return -999;
    }

    /**
     * @return all the values being stored
     */
    public int[] getAllValues() {
        int[] returnable = new int[matrix.length];

        for (int i = 0; i < returnable.length; i++)
            returnable[i] = lower + i;

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
        int max = -1;

        for (int[] d : matrix) {
            if (d.length > 0 && max < d[d.length - 1])
                max = d[d.length - 1];
        }
        return max;
    }
}
