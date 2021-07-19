package UpdateStructure;

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

        for (int i = sizeReduced; i > 0; b1[--i] = reducedPos1[i], b2[i] = reducedPos2[i]) {
            ;
        }
        reducedPos1 = b1;
        reducedPos2 = b2;

        int[] a = new int[sizeNonReduced];
        for (int i = sizeNonReduced; i > 0; a[--i] = noReductionArray[i]) {
            ;
        }
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

    public void addNewTid(int newTid) {
        //Acrescenta-se à compressão
        if (sizeReduced > 0 && reducedPos2[sizeReduced - 1] + 1 == newTid) {
            reducedPos2[sizeReduced - 1] = newTid;
        }
        //nova compressão -> 3 elementos seguidos com tids seguidos
        else if (sizeNonReduced >= 2 && noReductionArray[sizeNonReduced - 1] + 1 == newTid && noReductionArray[sizeNonReduced - 2] + 2 == newTid) {
            //se nao tiver mais espaço realoca
            if (sizeReduced == reducedPos1.length)
                increaseReducedArraysby1();
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
                increaseNonReducedArrayby1();
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

    public void increaseReducedArraysby1() {
        int[] a = new int[reducedPos1.length + 1];
        int[] b = new int[reducedPos1.length + 1];

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

    public void increaseNonReducedArrayby1() {
        int[] a = new int[noReductionArray.length + 1];
        for (int i = 0; i < sizeNonReduced; i++) {
            a[i] = noReductionArray[i];
        }

        noReductionArray = a;
    }

    /**
     * @return an int array with all the tids.
     * De-compresses the DIntArray Class to an array. The returning array has is tids in order
     */
    public int[] getAsArray() {
        int[] secundary = new int[2 * sizeReduced + sizeNonReduced];
        int pos = 0;
        int ci = 0; //compressed arrays
        int di = 0; //decompressed arrays


        while (ci < sizeReduced || di < sizeNonReduced) {
            //Aumenta o tamanho se necessário
            if (pos == secundary.length) {
                int[] b = new int[2 * secundary.length];
                System.arraycopy(secundary, 0, b, 0, secundary.length);
                secundary = b;
            }

            if (ci == sizeReduced) {  //é o sem compressão até ao final
                while (di < sizeNonReduced) {
                    if (pos == secundary.length) {
                        int[] b = new int[2 * secundary.length];
                        System.arraycopy(secundary, 0, b, 0, secundary.length);
                        secundary = b;
                    }
                    secundary[pos++] = noReductionArray[di++];
                }
            } else if (di == sizeNonReduced) {
                while (ci < sizeReduced) {
                    //verifica o tamanho
                    if (pos == secundary.length) {
                        int[] b = new int[2 * secundary.length];
                        System.arraycopy(secundary, 0, b, 0, secundary.length);
                        secundary = b;
                    }
                    //para cada intervalor
                    for (int i = reducedPos1[ci]; i <= reducedPos2[ci]; i++) {
                        //adiciona o valor
                        secundary[pos++] = i;
                        //verifica o tamanho dentro do intervalo
                        if (pos == secundary.length) {
                            int[] b = new int[2 * secundary.length];
                            System.arraycopy(secundary, 0, b, 0, secundary.length);
                            secundary = b;
                        }
                    }
                    ++ci;   //adiciona ci após adicionar o intervalo

                }
            } else if (reducedPos1[ci] < noReductionArray[di]) {    //intervalo é o menor
                //adiciona o intervalo-> para cada valor dentro do intervalo
                for (int i = reducedPos1[ci]; i <= reducedPos2[ci]; i++) {
                    //adiciona o valor
                    secundary[pos++] = i;
                    //verifica o tamanho dentro do intervalo
                    if (pos == secundary.length) {
                        int[] b = new int[2 * secundary.length];
                        System.arraycopy(secundary, 0, b, 0, secundary.length);
                        secundary = b;
                    }
                }
                ++ci;   //adiciona ci após adicionar o intervalo
            } else {   //não intervalor é o menor
                secundary[pos++] = noReductionArray[di++];
            }
        }

        int[] returnable = new int[pos];
        for (int i = pos; i > 0; returnable[--i] = secundary[i]) {
        }
        //System.out.println(Arrays.toString(returnable));
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

    public int binarySearch(int[] sortedArray, int key, int low, int high) {
        int index = Integer.MAX_VALUE;

        while (low <= high) {
            int mid = low  + ((high - low) / 2);
            if (sortedArray[mid] < key) {
                low = mid + 1;
            } else if (sortedArray[mid] > key) {
                high = mid - 1;
            } else if (sortedArray[mid] == key) {
                index = mid;
                break;
            }
        }
        return index;
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
     * I know this may look like a joke to you, but if every value gets added right, "it just works" xD
     */
    public void clearSpace() {
        sizeReduced = 0;
        sizeNonReduced = 0;
    }

    /**
     * @return the total arrays size
     * <p>
     * used to check if the class is empty or not. Just like countStoredTids(), but better.
     */
    public int intersetionCount() {
        return sizeReduced + sizeNonReduced;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("no red: [ ");
        for(int i = 0; i < sizeNonReduced; i++){
            str.append(noReductionArray[i]).append(" ");
        }
        str.append("]\n");
        str.append("reduct: [ ");
        for (int i = 0; i < sizeReduced; i++) {
            str.append("[").append(reducedPos1[i]).append(", ").append(reducedPos2[i]).append("] ");
        }
        str.append("]");
        return str.toString();
    }
}
