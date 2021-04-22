package ReducedIdStorageWithClassAsDoubleArrayV2;

import it.unimi.dsi.fastutil.ints.IntArrays;

import java.lang.reflect.Array;
import java.util.Arrays;

public class DIntArray {

    int[] array1, array2;
    int size = 0;

    public DIntArray(int arrayLength) {
        array1 = new int[arrayLength];
        array2 = new int[0];

    }


    /**
     * @return array1 lenght
     */
    public int getLength() {
        return array1.length;
    }

    /**
     * @param array1 int array of values
     *               <p>
     *               stores an array as array1
     */
    public void SetArray1(int[] array1) {
        System.arraycopy(array1, 0, this.array1, 0, array1.length);
    }

    /**
     * @param array2 int array of values
     *               <p>
     *               stores an array as array2
     */
    public void SetArray2(int[] array2, int size) {
        this.array2 = array2;
        this.size = size;
    }

    /**
     * @param value the value beng added to array2
     */
    public void addToArray2(int value) {

        if (size == array2.length) {
            int[] b = new int[array2.length == 0 ? 1 : 2 * array2.length];
            System.arraycopy(array2, 0, b, 0, array2.length);
            b[size] = value;
            array2 = b;

        } else {

            array2[size] = value;

        }

        size++;

    }


    /**
     * @param length lenght of the array to copy -> this is used because the shellfragmenmt stores the real array size
     *               <p>
     *               returns 2d matrix based on this values
     */
    public int[][] get2dMatrix(int length) {
        int[][] a = new int[length][1];

        for (int i = 0; i < length; i++) {
            a[i][0] = array1[i];
        }

        //adiciona a segunda dimensão
        int ai = a.length - 1;
        int bi = array2.length - 1;
        while (bi >= 0 && ai >= 0) {       //para cada uma das posições com segunda dimensão
            if (a[ai][0] < array2[bi]) {
                int[] b = new int[2];                           //cria array com tamanmho 2
                b[0] = a[ai][0];                //coloca na primeira dimensão o que esta na primeira dimens~ºao do array que já existia
                b[1] = array2[bi];                               //coloca na segund dimensão o valor
                a[ai] = b;                      //coloca o a apontar para o array que se criou com tamanho 2
                bi--;
            }
            ai--;
        }
        return a;
    }


    /**
     * @param v the index position, it works as the second position of array 1
     * @return if V has a second position returns the tid stored, otherwise returns -1
     */
    public int array2indexContains(int v) {

        for (int j : array2) {
            if (array1[v] < j && (v == array1.length - 1 || array1[v + 1] > j))
                return j;
        }
        return -1;
    }

    /**
     * rebuilds array2 and indexConnections in order to improve speed
     */
    public void redoArray2() {
        int[] b = new int[size];
        System.arraycopy(array2, 0, b, 0, size);
        array2 = b;
    }


}
