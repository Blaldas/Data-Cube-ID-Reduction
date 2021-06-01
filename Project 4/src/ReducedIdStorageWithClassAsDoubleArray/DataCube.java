package ReducedIdStorageWithClassAsDoubleArray;

import notUsingFastUtil.Main;

import java.util.Arrays;
import java.util.Date;


public class DataCube {

    ShellFragment[] shellFragmentList;
    int lower;

    public DataCube(int[][] rawData, int[] maxValue, int lowerValue) {

        shellFragmentList = new ShellFragment[rawData[0].length];
        this.lower = lowerValue;

        for (int i = 0; i < rawData[0].length; i++) {
            shellFragmentList[i] = new ShellFragment(rawData, i, lowerValue, maxValue[i + 1]);
            //System.gc();
            //System.out.println(i);
        }
    }


    /**
     * prints all the values and the number of tuples they store, for every single diemension
     */
   /* public void showAllDimensions() {
        for (int i = 0; i < shellFragmentList.length; i++) {
            StringBuilder str;
            System.out.println("Dimension " + (i + 1));
            System.out.println("Value\tTuple IDs");
            for (int n = 0; n <= shellFragmentList[i].getBigestValue(); n++) {
                str = new StringBuilder();
                str.append(n).append("\t\t");
                int[][] tidsForValue = shellFragmentList[i].getTidsListFromValue(n);
                for (int[] arr : tidsForValue)
                    str.append(Arrays.toString(arr)).append(" ");
                System.out.println(str);
            }
        }
    }

    */
    public int pointQueryCounter(int[] query) {
        DIntArray mat = pointQuerySeach(query);
        if (mat == null)
            return -1;
        int counter = 0;


        for (int i = 0; i < mat.size; i++) {
            if (mat.array2[i] != -1)
                counter += mat.array2[i] - mat.array1[i];
            counter++;
        }

        return counter;
    }

    public int[] pointQueryAdapter(int[] query) {
        return getArrayFromMatrix(pointQuerySeach(query));

    }

    /**
     * @param query the query being made
     * @return array with the tuple ids, null if query.length != shellFragmentList.length
     */
    public DIntArray pointQuerySeach(int[] query) {
        if (query.length != shellFragmentList.length)
            return null;

        DIntArray result = new DIntArray(0);
        for (int i = 0; i < query.length; i++) {
            if (query[i] != '*' && query[i] != '?') {
                DIntArray secundary = shellFragmentList[i].getTidsListFromValue(query[i]);
                if (secundary.size == 0)
                    return secundary;
                if (result.size == 0)
                    result = secundary;
                else {
                    result = intersect(result, secundary);
                   // System.out.println(Arrays.deepToString(result.get2dMatrix(result.size)));

                    if (result.size == 0)
                        return result;
                }
            }
            if (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() > ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory)
                ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            if (result.size == 0) { //caso não exista nenhuma instanciação
                result = new DIntArray(1);
                result.addValues(0, shellFragmentList[0].getBiggestTid());
            }
        }

        return result;
    }

    private int[] getArrayFromMatrix(DIntArray matrix) {
        if (matrix == null)
            return null;
        else if (matrix.size == 0)
            return new int[0];

        int size = 0;
        int[] returnable = new int[matrix.size];
        for (int i = 0; i < matrix.size; i++) {
            if (size == returnable.length) {
                int[] b = new int[2 * size];
                System.arraycopy(returnable, 0, b, 0, returnable.length);
                returnable = b;
            }
            if (matrix.array2[i] == -1) {
                returnable[size++] = matrix.array1[i];
            } else {
                for (int n = matrix.array1[i]; n <= matrix.array2[i]; n++) {
                    if (size == returnable.length) {
                        int[] b = new int[2 * size];
                        System.arraycopy(returnable, 0, b, 0, returnable.length);
                        returnable = b;
                    }
                    returnable[size++] = n;
                }
            }
        }
        if (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() > ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory)
            ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        return Arrays.copyOfRange(returnable, 0, size);
    }


    /**
     * @param arrayA array of tids
     * @param arrayB array of tids
     * @return the array with the tids existing in both arrays received
     */
    private static DIntArray intersect(DIntArray arrayA, DIntArray arrayB) {

        DIntArray c = new DIntArray(2 * arrayA.size > arrayB.size ? arrayA.size : arrayB.size);
        int ai = 0, bi = 0;


        while (ai < arrayA.size && bi < arrayB.size) {
            if (arrayA.array2[ai] == -1) {                //A tem tamanho 1:
                if (arrayB.array2[bi] == -1) {               //B tem tamanho 1
                    if (arrayA.array1[ai] == arrayB.array1[bi]) {       //sao iguais
                        c.addValues(arrayA.array1[ai], -1);                             //adiciona valor 1
                        ++ai;                                                           //avança com pontyeiro A
                        ++bi;                                                           //avança com ponteiro B
                    } else if (arrayA.array1[ai] < arrayB.array1[bi])       //A menor que B
                        ++ai;                                                    //avança com pontyeiro A
                    else                                                    //B menor que A
                        ++bi;                                                   //avança com ponteiro B
                } else {                    //B tem tamanho 2
                    if (arrayA.array1[ai] >= arrayB.array1[bi] && arrayA.array1[ai] <= arrayB.array2[bi]) {     //A1 entre B1 e B2
                        c.addValues(arrayA.array1[ai], -1);
                        ++ai;
                    } else if (arrayA.array1[ai] < arrayB.array2[bi])                                           //A1 menor que B2
                        ++ai;                                                                                       //avança com pontyeiro A
                    else                                                                                //A1 maior que  B2
                        ++bi;                                                                                //avança com ponteiro B
                }
            } else {                    //A tem tamanho 2
                if (arrayB.array2[bi] == -1) {   //B tem tamanho 1
                    if (arrayA.array1[ai] <= arrayB.array1[bi] && arrayA.array2[ai] >= arrayB.array1[bi]) {     //B1 entre A1 e A2
                        c.addValues(arrayB.array1[bi], -1);
                        ++bi;
                    } else if (arrayA.array2[ai] < arrayB.array1[bi])                                           //A1 menor que B1
                        ++ai;
                    else                                                                                //A1 maior que B1
                        ++bi;
                } else {        //A e B têm tamanho 2
                    if (arrayA.array1[ai] <= arrayB.array1[bi] && arrayA.array2[ai] >= arrayB.array1[bi]) {         //[b0 , - ]
                        if (arrayB.array1[bi] == arrayA.array2[ai])     //caso o primeiro esteja no final do útimo
                            c.addValues(arrayB.array1[bi], -1);
                        else
                            c.addValues(arrayB.array1[bi], arrayA.array2[ai] > arrayB.array2[bi] ? arrayB.array2[bi] : arrayA.array2[ai]);
                    } else if (arrayA.array1[ai] >= arrayB.array1[bi] && arrayA.array1[ai] <= arrayB.array2[bi]) {
                        if (arrayB.array2[bi] == arrayA.array1[ai])     //caso o primeiro esteja no final do útimo
                            c.addValues(arrayA.array1[ai], -1);
                        else
                            c.addValues(arrayA.array1[ai], arrayA.array2[ai] > arrayB.array2[bi] ? arrayB.array2[bi] : arrayA.array2[ai]);
                    }
                    if (arrayA.array2[ai] < arrayB.array2[bi])
                        ++ai;
                    else
                        ++bi;
                }
            }
        }

        DIntArray returnable = new DIntArray(c.size);
        returnable.addArrays(c.array1, c.array2, c.size);
        c = null;
        return returnable;

    }


    public int getNumberShellFragments() {
        return shellFragmentList.length;
    }

    public int getNumberTuples() {
        return shellFragmentList[0].getBiggestTid() + 1;
    }

    /**
     * @param values the query
     */
    public void getSubCube(int[] values) {
        if (values.length != shellFragmentList.length) {
            System.out.println("wrong number of dimensions");
            return;
        }


        int[] tidArray = this.pointQueryAdapter(values);            //obtem TIDs resultante
        if (tidArray.length == 0) {
            System.out.println("no values found");
            return;
        }
        if (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() > ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory)
            ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        int[][] subCubeValues = new int[tidArray.length][];                      //aloca memoria array de valores

        for (int i = 0; i < subCubeValues.length; i++) {                                     //para cada um dos IDs de tuples que respeita o pedido
            subCubeValues[i] = getDimensions(tidArray[i], values);                              //obtem-se os seus valores e coloca-se no array de representação de objetos
        }
        if (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() > ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory)
            ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();


        showQueryDataCube(values, subCubeValues);        // a nova função que mostra as coisas

    }


    /**
     * @param qValues       the query
     * @param subCubeValues "subcube" values
     */
    private void showQueryDataCube(int[] qValues, int[][] subCubeValues) {

        int[] query = new int[subCubeValues[0].length];               //stores all the values as a query.
        int[] counter = new int[subCubeValues[0].length];             //counter to the query values
        for (int c : counter)
            c = 0;


        int[][] values = getAllDifferentValues(subCubeValues, qValues);      //guarda todos os valores diferentes para cada dimensão
        if (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() > ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory)
            ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();


        int total = 1;                              //guarda o numero de conbinações difrerentes
        for (int[] d : values) {
            total *= (d.length);
        }

        int rounds = 0;
        do {
            for (int i = 0; i < counter.length; i++)     //da os valores as queries
                query[i] = values[i][counter[i]];


            //pesquisa com valores do query
            getNumeroDeTuplesComCaracteristicas(query, subCubeValues);// faz pesquisa sobre esses valores

            //gere os counters
            for (int i = 0; i < counter.length; i++) {              //para cada um dos counter
                if (counter[i] < values[i].length - 1) {
                    counter[i]++;
                    break;
                } else        //if( counter[i] == '*')
                    counter[i] = 0;

            }

            rounds++;
            if (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() > ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory)
                ReducedIdStorageWithClassAsDoubleArray.Main.maxMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        } while (rounds < total);

        System.out.println(total + " lines written");
    }

    private int[][] getAllDifferentValues(int[][] subCubeValues, int[] queryValues) {
        int[][] result = new int[subCubeValues[0].length][0];

        for (int i = 0; i < queryValues.length; i++) {               //para cada uma das dimensões
            if (queryValues[i] == '?') {
                result[i] = new int[shellFragmentList[i].getBigestValue() - shellFragmentList[i].lower + 2];
                //for(int n = shellFragmentList[i].getBigestValue(); n > shellFragmentList[i].lower ; n++)
                //  result[i][n] = n;
                System.arraycopy(shellFragmentList[i].getAllValues(), 0, result[i], 1, shellFragmentList[i].getAllValues().length);
                result[i][0] = '*';
            } else {
                result[i] = new int[]{queryValues[i]};
            }
        }
        return result;
    }

    private void getNumeroDeTuplesComCaracteristicas(int[] query, int[][] values) {
        int count = 0;
        for (int[] tuple : values) {                                                //para cada uma das tuples
            boolean flagEqual = true;
            for (int i = 0; i < tuple.length; i++) {                                //para cada uma das diemnsões
                if (query[i] != '*' && tuple[i] != query[i]) {                           //se os valores da dimensão forem diferentes
                    flagEqual = false;                                                      //mete flag a false
                    break;
                }
            }
            if (flagEqual)                                                          //se for tudo igual
                count++;                                                                    //aumenta o contador
        }
        StringBuilder str = new StringBuilder();                            //obtem os dados e mostra
        for (int i : query)
            if (i != '*')
                str.append((i)).append(" ");
            else
                str.append("* ");
        str.append(": ").append(count);
        System.out.println(str);

    }


    /**
     * @param tid tuple id to seach on
     * @return an array with the dimensional values of such tuple.
     */
    private int[] getDimensions(int tid, int[] query) {
        int[] returnable = new int[shellFragmentList.length];


        for (int i = 0; i < query.length; i++) {//query.length == shellfragmentelist.length
            if (query[i] == '?' || query[i] == '*')      //se estiver instanciado tem de ter estes valores
                returnable[i] = shellFragmentList[i].getValueFromTid(tid);
            else {                                       //se não tiver instanciado tem de se procurar
                returnable[i] = query[i];
            }
        }

        return returnable;
    }
}
