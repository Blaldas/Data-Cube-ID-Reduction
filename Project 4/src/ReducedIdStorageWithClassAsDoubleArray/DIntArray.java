package ReducedIdStorageWithClassAsDoubleArray;

import java.util.Arrays;

public class DIntArray {

    int[] array1, array2;
    int size;

    public DIntArray(int arrayLength) {
        array1 = new int[arrayLength];
        array2 = new int[arrayLength];
        size = 0;

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

    /**
     * @param newArray1 array1 to add
     * @param newArray2 array2 to add
     *                  this method ADDs the arrays to the existing ones.
     */
    public void addArrays(int[] newArray1, int[] newArray2, int newArraysSize) {
        for (int i = 0; i < newArraysSize; i++) {
            if (array1.length == size) {
                int[] a = new int[size == 0 ? 1 : 2 * size];
                int[] b = new int[size == 0 ? 1 : 2 * size];

                System.arraycopy(array1, 0, a, 0, size);
                System.arraycopy(array2, 0, b, 0, size);

                array1 = a;
                array2 = b;
            }

            array1[size] = newArray1[i];
            array2[size++] = newArray2[i];
        }

    }

    //returns 2d matrix based on this values
    public int[][] get2dMatrix(int length) {
        int[][] a = new int[length][1];

        for (int i = 0; i < length; i++) {
            if (array2[i] == -1) {        //caso tenha tamanho 1
                //a[i] = new int[1];
                a[i][0] = array1[i];
            } else {
                a[i] = new int[2];
                a[i][0] = array1[i];
                a[i][1] = array2[i];
            }
        }
        return a;
    }


    public void addValues(int v1, int v2) {

        if (this.array1.length == size) {
            int[] a = new int[((int) (1.5 * size)) == size ? size + 1 : (int) (1.5 * size)];
            int[] b = new int[((int) (1.5 * size)) == size ? size + 1 : (int) (1.5 * size)];

            System.arraycopy(this.array1, 0, a, 0, this.size);
            System.arraycopy(this.array2, 0, b, 0, this.size);

            this.array1 = a;
            this.array2 = b;
        }

        array1[size] = v1;
        array2[size++] = v2;
    }

}
