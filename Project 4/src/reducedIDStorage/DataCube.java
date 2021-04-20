package reducedIDStorage;

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
    public void showAllDimensions() {
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

    public int pointQueryCounter(int[] query) {
        int[][] mat = pointQuerySeach(query);
        if (mat == null)
            return -1;
        int counter = 0;

        for (int arr[] : mat) {
            if (arr.length == 2)
                counter += arr[1] - arr[0];
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
    public int[][] pointQuerySeach(int[] query) {
        if (query.length != shellFragmentList.length)
            return null;

        int[][] result = new int[0][];
        for (int i = 0; i < query.length; i++) {
            if (query[i] != '*' && query[i] != '?') {
                int[][] secundary = shellFragmentList[i].getTidsListFromValue(query[i]);      //obtem lista de tids
                if (secundary.length == 0)                                      //se a lista for vazia, devolve null
                    return new int[0][0];

                if (result.length == 0)
                    result = secundary;
                else {
                    //Date startDate = new Date(), endDate;
                    result = intersect(result, secundary);
                    /*
                    endDate = new Date();
                    long numSeconds = ((endDate.getTime() - startDate.getTime()));
                    System.out.println("intersect\t" + numSeconds);             //tempo
                     */
                    if (result.length == 0)
                        return new int[0][0];
                }
            }
        }
        //caso todas tenham valor '?' ou '*'
        if (result.length == 0) {
            result = new int[1][2];
            result[0][0] = 0;
            result[0][1] = shellFragmentList[0].getBiggestTid();
        }
        return result;
    }

    private int[] getArrayFromMatrix(int[][] matrix) {
        if (matrix == null)
            return null;
        else if (matrix.length == 0)
            return new int[0];

        int size = 0;
        int[] returnable = new int[matrix.length];

        for (int[] arr : matrix) {
            if (size == returnable.length) {
                int[] b = new int[2 * size];
                System.arraycopy(returnable, 0, b, 0, returnable.length);
                returnable = b;
            }

            if (arr.length == 1) {
                returnable[size++] = arr[0];
            } else {
                for (int i = arr[0]; i <= arr[1]; i++) {
                    if (size == returnable.length) {
                        int[] b = new int[2 * size];
                        System.arraycopy(returnable, 0, b, 0, returnable.length);
                        returnable = b;
                    }
                    returnable[size++] = i;
                }
            }
        }

        return Arrays.copyOfRange(returnable, 0, size);
    }


    /**
     * @param arrayA array of tids
     * @param arrayB array of tids
     * @return the array with the tids existing in both arrays received
     */
    private static int[][] intersect(int[][] arrayA, int[][] arrayB) {
        int[][] c = new int[Math.max(arrayA.length, arrayB.length)][];
        int ai = 0, bi = 0, ci = 0;


        while (ai < arrayA.length && bi < arrayB.length) {

            switch (arrayA[ai].length) {
                case 1:
                    switch (arrayB[bi].length) {
                        case 1:                                     //os 2 tamanho 1
                            if (arrayA[ai][0] == arrayB[bi][0]) {
                                c[ci] = new int[1];
                                c[ci++][0] = arrayA[ai][0];
                                ai++;
                                bi++;
                            } else if (arrayA[ai][0] < arrayB[bi][0])
                                ai++;
                            else
                                bi++;

                            break;
                        case 2:
                            //A 1, b 2
                            if (arrayA[ai][0] >= arrayB[bi][0] && arrayA[ai][0] <= arrayB[bi][1]) {
                                c[ci] = new int[1];
                                c[ci++][0] = arrayA[ai][0];
                                ai++;
                            } else if (arrayA[ai][0] < arrayB[bi][1])
                                ai++;
                            else
                                bi++;
                            break;
                    }
                    break;
                case 2:
                    switch (arrayB[bi].length) {
                        case 1:                             //a 2, b 1

                            if (arrayA[ai][0] <= arrayB[bi][0] && arrayA[ai][1] >= arrayB[bi][0]) {
                                c[ci] = new int[1];
                                c[ci++][0] = arrayB[bi][0];
                                bi++;
                            } else if (arrayA[ai][1] < arrayB[bi][0])
                                ai++;
                            else
                                bi++;
                            break;
                        case 2:                             //a 2, b 2

                            if (arrayA[ai][0] <= arrayB[bi][0] && arrayA[ai][1] >= arrayB[bi][0]) {         //[b0 , - ]
                                c[ci] = new int[2];
                                c[ci][0] = arrayB[bi][0];
                                c[ci++][1] = Math.min(arrayB[bi][1], arrayA[ai][1]);
                            } else if (arrayA[ai][0] >= arrayB[bi][0] && arrayA[ai][0] <= arrayB[bi][1]) {
                                c[ci] = new int[2];
                                c[ci][0] = arrayA[ai][0];
                                c[ci++][1] = Math.min(arrayB[bi][1], arrayA[ai][1]);
                            }
                            if (arrayA[ai][1] < arrayB[bi][1])
                                ai++;
                            else
                                bi++;
                            break;
                    }
            }
        }

        return Arrays.copyOfRange(c, 0, ci);
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

        int[][] subCubeValues = new int[tidArray.length][];                      //aloca memoria array de valores

        for (int i = 0; i < subCubeValues.length; i++) {                                     //para cada um dos IDs de tuples que respeita o pedido
            subCubeValues[i] = getDimensions(tidArray[i]);                              //obtem-se os seus valores e coloca-se no array de representação de objetos
        }
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
                if (tuple[i] != query[i] && query[i] != '*') {                          //se os valores da dimensão forem diferentes
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
                str.append((i)).append("\t");
            else
                str.append("*\t");
        str.append(":\t").append(count);
        System.out.println(str);

    }


    /**
     * @param tid tuple id to seach on
     * @return an array with the dimensional values of such tuple.
     */
    private int[] getDimensions(int tid) {
        int[] returnable = new int[shellFragmentList.length];

        for (int i = 0; i < shellFragmentList.length; i++)
            returnable[i] = shellFragmentList[i].getValueFromTid(tid);

        return returnable;
    }
}
