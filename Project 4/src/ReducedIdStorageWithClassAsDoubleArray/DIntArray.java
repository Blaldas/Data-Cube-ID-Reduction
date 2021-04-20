package ReducedIdStorageWithClassAsDoubleArray;

import java.util.Arrays;

public class DIntArray {

    int[] array1, array2;

    public DIntArray(int arrayLength) {
        array1 = new int[arrayLength];
        array2 = new int[arrayLength];

        Arrays.fill(array2, -1);            //preenche o array 2 com valor -1

    }


    public int getLength() {
        return array1.length;
    }

    public void SetArray1(int[] array1) {
        for (int i = 0; i < array1.length; i++)
            this.array1[i] = array1[i];
    }

    public void SetArray2(int[] array2) {
        for (int i = 0; i < array2.length; i++)
            this.array2[i] = array2[i];
    }

    //returns 2d matrix based on this values
    public int[][] get2dMatrix(int length) {
        int[][] a = new int[length][];

        for (int i = 0; i < length; i++) {
            if (array2[i] == -1) {        //caso tenha tamanho 1
                a[i] = new int[1];
                a[i][0] = array1[i];
            } else {
                a[i] = new int[2];
                a[i][0] = array1[i];
                a[i][1] = array2[i];
            }
        }
        return a;
    }


}
