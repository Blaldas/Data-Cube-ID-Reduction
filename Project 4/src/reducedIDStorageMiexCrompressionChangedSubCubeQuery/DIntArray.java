package reducedIDStorageMiexCrompressionChangedSubCubeQuery;

import it.unimi.dsi.fastutil.ints.IntArrays;

public class DIntArray {

    int[] reducedPos1, reducedPos2, noReductionArray;
    int sizeReduced, sizeNonReduced;

    public DIntArray() {
        reducedPos1 = new int[0];
        reducedPos2 = new int[0];
        noReductionArray = new int[0];

        sizeReduced = 0;
        sizeNonReduced = 0;
    }

    public void reduceMaximumMemory() {
        int[] b1 = new int[sizeReduced];
        int[] b2 = new int[sizeReduced];

        for(int i = sizeReduced; i > 0; b1[--i] = reducedPos1[i], b2[i] = reducedPos2[i]){;}
        reducedPos1 = b1;
        reducedPos2 = b2;

        int[] a = new int[sizeNonReduced];
        for(int i = sizeNonReduced; i > 0; a[--i] = noReductionArray[i]){;}
        noReductionArray = a;

    }

    public void addTid(int newTid) {
        //Acrescenta-se à compressão
        if (sizeReduced > 0 && reducedPos2[sizeReduced - 1] + 1 == newTid) {
            reducedPos2[sizeReduced - 1] = newTid;
        }
        //nova compressão -> 3 elementos seguidos com tids seguidos
        else if (sizeNonReduced >= 2 && noReductionArray[sizeNonReduced - 1] + 1 == newTid && noReductionArray[sizeNonReduced - 2] + 2 == newTid) {
            //se nao tiver mais espaço realoca
            if (sizeReduced == reducedPos1.length)
                increaseReducedArrays();
            //coloca valores e aumenta ponteiro
            reducedPos1[sizeReduced] = noReductionArray[sizeNonReduced - 2];
            reducedPos2[sizeReduced++] = newTid;
            //remove valores do array sem redução
            // usar de houver problemas....:
            // noReductionArray[sizeNonReduced-1] = 0;
            // noReductionArray[sizeNonReduced-2] = 0;
            sizeNonReduced -= 2;
        }
        //não acrescenta a redução existente nem cria novo redução:
        else {
            //se nao tiver mais espaço realoca
            if (sizeNonReduced == noReductionArray.length)
                increaseNonReducedArray();
            //adiciona novo valor
            noReductionArray[sizeNonReduced++] = newTid;
        }
    }

    public void addTidInterval(int v1, int v2) {
        if (sizeReduced == reducedPos1.length)
            increaseReducedArrays();
        reducedPos1[sizeReduced] = v1;
        reducedPos2[sizeReduced++] = v2;

    }

    public void increaseReducedArrays() {
        int[] a = new int[reducedPos1.length == 0 ? 1 : 2 * reducedPos1.length];
        int[] b = new int[reducedPos1.length == 0 ? 1 : 2 * reducedPos1.length];

        for (int i = 0; i < sizeReduced; i++) {
            a[i] = reducedPos1[i];
            b[i] = reducedPos2[i];
        }

        reducedPos1 = a;
        reducedPos2 = b;
    }

    public void increaseNonReducedArray() {
        int[] a = new int[noReductionArray.length == 0 ? 1 : 2 * noReductionArray.length];
        for (int i = 0; i < sizeNonReduced; i++) {
            a[i] = noReductionArray[i];
        }

        noReductionArray = a;
    }

    /**
     * @return an tid ordered 2d Matrix
     */
    public int[][] get2dMatrix() {
        int[][] returnable = new int[sizeReduced + sizeNonReduced][1];

        /*
        for (int i = 0; i < sizeNonReduced; i++) {
            returnable[i][0] = noReductionArray[i];
        }

        for (int i = 0; i < sizeReduced; i++) {
            returnable[i] = new int[2];
            returnable[i][0] = reducedPos1[i];
            returnable[i][1] = reducedPos2[i];
        }
         */
        int noReduction = 0, reduction = 0;
        int c = 0;

        while (noReduction < sizeNonReduced && reduction < sizeReduced) {
            if (reducedPos1[reduction] < noReductionArray[noReduction]) {
                returnable[c] = new int[2];
                returnable[c][0] = reducedPos1[reduction];
                returnable[c][1] = reducedPos2[reduction++];
            } else
                returnable[c][0] = noReductionArray[noReduction++];
            c++;
        }

        if (noReduction != sizeNonReduced) {
            while (noReduction < sizeNonReduced) {
                returnable[c][0] = noReductionArray[noReduction++];
                c++;
            }
        }else{
            while(reduction < sizeReduced) {
                returnable[c] = new int[2];
                returnable[c][0] = reducedPos1[reduction];
                returnable[c][1] = reducedPos2[reduction++];
                c++;
            }
        }

        return returnable;
    }

    /**
     *
     * @return an int array with all the tids.
     *  De-compresses the DIntArray Class to an array
     */
    public int[] getAsArray() {
        int[] secundary = new int[2 * sizeReduced + sizeNonReduced];
        int pos = 0;
        for (int i = 0; i < sizeReduced; i++) {
            for (int n = reducedPos1[i]; n <= reducedPos2[i]; n++) {
                if (pos == secundary.length) {
                    int[] b = new int[2 * pos];
                    System.arraycopy(secundary, 0, b, 0, secundary.length);
                    secundary = b;
                }
                secundary[pos++] = n;
            }
        }
        if (secundary.length - pos < sizeNonReduced) {
            int[] a = new int[secundary.length + (sizeNonReduced - secundary.length - pos + 1)];
            for (int i = 0; i < pos; i++) {
                a[i] = secundary[i];
            }
            secundary = a;
        }

        for (int i = 0; i < sizeNonReduced; i++)
            secundary[pos++] = noReductionArray[i];

        int[] returnable = new int[pos];
        for (int i = pos; i > 0; returnable[--i] = secundary[i]) {
        }

        return returnable;
    }

    /**
     * @return biggest TID stored or -1, if no TID is stored
     */
    public int getBigestTid() {

        if (sizeReduced > 0) {
            if (sizeNonReduced > 0)
                //I do not use math.min in order to avoid lose any kind of processing power
                return reducedPos2[sizeReduced - 1] > noReductionArray[sizeNonReduced - 1] ? reducedPos2[sizeReduced - 1] : noReductionArray[sizeNonReduced - 1];
            return reducedPos2[sizeReduced - 1];
        } else if (sizeNonReduced > 0)
            return noReductionArray[sizeNonReduced - 1];
        return -1;
    }

    /**
     * @param tid the tuple to be found
     * @return true if found, false if not found
     */
    public boolean hasTid(int tid) {
        //matrix sem redução
        if (IntArrays.binarySearch(noReductionArray, 0, sizeNonReduced, tid) >= 0)
            return true;

        //matrix com redução
        //attempt to binary search
        int start = 0;
        int end = sizeReduced - 1;
        int mid;
        while (start <= end) {
            mid = (end + start) / 2;
            if (reducedPos1[mid] == tid || (reducedPos1[mid] < tid && reducedPos2[mid] != -1 && reducedPos2[mid] >= tid)) {
                return true;
            } else if (reducedPos1[mid] > tid)
                end = mid - 1;
            else
                start = mid + 1;
        }
        return false;
    }


    public int countStoredTids() {
        int count = 0;
        for (int i = 0; i < sizeReduced; i++)
            count += (reducedPos2[i] - reducedPos1[i]);
        count += sizeReduced;
        count += sizeNonReduced;
        return count;
    }

    /**
     *  I know this may look like a joke to you, but if every value gets added right, "it just works" xD
     */
    public void clearSpace() {
        sizeReduced = 0;
        sizeNonReduced = 0;
    }

    /**
     *
     * @return the total arrays size
     *
     * used to check if the class is empty or not. Just like countStoredTids(), but better.
     */
    public int intersetionCount() {
        return  sizeReduced +  sizeNonReduced;
    }
/*

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
    /*
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
*/

}
