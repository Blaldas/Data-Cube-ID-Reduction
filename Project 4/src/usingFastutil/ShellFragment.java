package usingFastutil;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.ints.IntArraySet;

public class ShellFragment {

    IntArraySet[] matrix;
    int lower;
    int upper;

    ShellFragment(int[] rawData, int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
        matrix = new IntArraySet[upper - lower + 1];
        for (int i =0; i < matrix.length; i++){
            matrix[i] = new IntArraySet(rawData.length);
        }

        fillMatrix(rawData);
    }

    private void fillMatrix(int[] rawData) {
        System.out.println("klmdsvknodlmvs");

        for (int i = 0; i < rawData.length; i++)                                                                   //para cada uma dos tuples
        {
            System.out.println(i);
            matrix[0].add(i);
        }
        System.out.println("klmdsvknodlmvs");
        System.exit(0);
    }

    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0. Care that the array of a found value may be zero as well, so it's not a flag
     */
    public int[] getTidsListFromValue(int value) {
        if (value > upper || value < lower)
            return new int[0];

        return matrix[value - lower].toIntArray();
    }

    /**
     * @param tid id of the tuple to be seached
     * @return the value of such tuple, or lower-1 if not found.
     */
    public int getValueFromTid(int tid) {
        for (int i = 0; i < matrix.length; i++) {
            if(matrix[i].contains(tid))
                return lower+i;
        }
        return lower-1;
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


    /**
     *
     * @return a list with all the Tids stored
     *
     * The algorithm gets the biggest tids it can find and fills an array with all the natual values <= the value found
     */
    public int[] getAllTids() {
        int b = getBiggestTid();
        int[] returnable = new int[b + 1];

        for (int i = 0; i < returnable.length; i++)
            returnable[i] = i;

        return returnable;

    }

    /**
     *
     * @return the biggest ID stored int all the values
     */
    public int getBiggestTid() {
        int max = -1;

        for (IntArraySet v : matrix) {
            if(v.size() > 0){
                int m = v.toIntArray()[v.size()-1];
                if (max < m)
                    max = m;
            }
        }
        return max;
    }
}
