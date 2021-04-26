package notUsingFastUtil;

import it.unimi.dsi.fastutil.ints.IntArrays;

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
        }
    }


    /**
     * prints all the values and the number of tuples they store, for every single diemension
     */
    public void showAllDimensions() {
        for (int i = 0; i < shellFragmentList.length; i++) {
            StringBuilder str = new StringBuilder();
            System.out.println("Dimension " + (i + 1));
            System.out.println("Value\tNumberTuples");
            for (int n = shellFragmentList[i].lower; n <= shellFragmentList[i].upper; n++) {
                str.append(n).append("\t").append(Arrays.toString(shellFragmentList[i].getTidsListFromValue(n)));
                str.append("\n");
            }
            System.out.println(str);
            System.out.println(shellFragmentList[i].matrix[0].length);
        }
    }

    /**
     * @param query the query being made
     * @return array with the tuple ids, null if query.length != shellFragmentList.length
     */
    public int[] pointQuerySeach(int[] query) {
        if (query.length != shellFragmentList.length)
            return null;

        int instanciated = 0;
        int[][] tidsList = new int[shellFragmentList.length][];                 //stores values of instanciated
        for (int i = 0; i < query.length; i++) {                                        //obtem todas as listas de values
            if (query[i] != '*' && query[i] != '?') {
                int[] returned = shellFragmentList[i].getTidsListFromValue(query[i]);
                if (returned.length == 0)                                      //se a lista for vazia, devolve lista com tamanho 0
                    return new int[0];
                else if (instanciated == 0)
                    tidsList[0] = returned;      //obtem lista de tids
                else {

                    for (int n = instanciated - 1; n >= 0; n--) {
                        if (tidsList[n].length > returned.length) {
                            tidsList[n + 1] = tidsList[n];
                            if (n == 0)
                                tidsList[0] = returned;
                        } else {
                            tidsList[n + 1] = returned;
                            break;
                        }

                    }
                }
                //tidsList[instanciated] = returned;
                instanciated++;
            }
        }

        if (instanciated > 0) {
            for (int i = 1; i < instanciated; i++) {
                tidsList[0] = intersect(tidsList[0], tidsList[i]);
                if (tidsList[0].length == 0)
                    return tidsList[0];
            }
            return tidsList[0];
        }
        return shellFragmentList[0].getAllTids();
    }

    /**
     * @param arrayA
     * @param arrayB
     * @return chamar conuntos com o menor numero de tuples possivel
     */
    private static int[] intersect(int[] arrayA, int[] arrayB) {
        int[] c = new int[Math.min(arrayA.length, arrayB.length)];
        int ai = 0, bi = 0, ci = 0;

        while (ai < arrayA.length && bi < arrayB.length) {
            if (arrayA[ai] == arrayB[bi]) {
                if (ci == 0 || arrayA[ai] != c[ci - 1]) {
                    //if (arrayA[ai] != 0) {  Esta verificação foi removida porque os arrays enviados para aqui têm o tamanho estritamente necessário
                    //porém, esta linha estava a ignorar o tid 0 e a gastar tempo precioso
                    //Caso possa receber arrays com tamanho maior que o necessário, a linha pode ser usada como:
                    //if (arrayA[ai] != 0 && ai != 0)
                    c[ci++] = arrayA[ai];
                    //}
                }
                ai++;
                bi++;
            } else if (arrayA[ai] > arrayB[bi]) {
                bi++;
            } else if (arrayA[ai] < arrayB[bi]) {
                ai++;
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

        int[] tidArray = this.pointQuerySeach(values);            //obtem TIDs resultante
        if (tidArray == null || tidArray.length == 0) {
            System.out.println("no values found");
            return;
        }


        int[][] subCubeValues = new int[tidArray.length][];                      //aloca memoria array de valores

        for (int i = 0; i < subCubeValues.length; i++) {                                     //para cada um dos IDs de tuples que respeita o pedido
            subCubeValues[i] = getDimensions(tidArray[i], values);                              //obtem-se os seus valores e coloca-se no array de representação de objetos
        }
        showQueryDataCube(values, subCubeValues);        // a nova função que mostra as coisas

    }

    /**
     * @param qValues       the query
     * @param subCubeValues array with all the tuple dimensional values
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


    /**
     * @param queryValues the query
     * @return a matrix with all the values to be looped, in each dimension.
     */
    private int[][] getAllDifferentValues(int[][] subCubeValues, int[] queryValues) {
        int[][] result = new int[subCubeValues[0].length][0];

        for (int i = 0; i < queryValues.length; i++) {               //para cada uma das dimensões
            if (queryValues[i] == '?') {
                result[i] = new int[shellFragmentList[i].getAllValues().length + 1];
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
    private int[] getDimensions(int tid, int[] query) {
        int[] returnable = new int[shellFragmentList.length];

        for(int i =0; i < query.length; i++){//query.length == shellfragmentelist.length
            if(query[i]  == '?' || query[i] == '*')      //se estiver instanciado tem de ter estes valores
                returnable[i] = shellFragmentList[i].getValueFromTid(tid);
            else{                                       //se não tiver instanciado tem de se procurar
                returnable[i] = query[i];
            }
        }

        return returnable;
    }

    public int getNumberUnnusedInts() {
        int total = 0;
        for (ShellFragment d : shellFragmentList)
            total += d.getNumberUnnusedInts();
        return total;
    }

    public int getNumberUsedInts() {
        int total = 0;
        for (ShellFragment d : shellFragmentList)
            total += d.getNumberUsedInts();
        return total;
    }

    public int getBiggestID() {
        return shellFragmentList[0].getBiggestTid();
    }
}
