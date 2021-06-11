package notUsingFastUtil;


import reducedIDStorageMiexCrompressionChangedSubCubeQuery.DIntArray;

import java.util.Arrays;

public class DataCube {

    ShellFragment[] shellFragmentList;
    int lower;


    /**
     * @param sizes      Max value of each dimension
     * @param lowerValue o menor valor do dataset (default = 1)
     *                   Chamada uma vez para criar o objeto cubo
     */
    public DataCube(int[] sizes, int lowerValue) {
        shellFragmentList = new ShellFragment[sizes.length];
        this.lower = lowerValue;
        for (int i = 0; i < shellFragmentList.length; i++) {
            shellFragmentList[i] = new ShellFragment( lowerValue, sizes[i]);
        }
    }

    /**
     * @param tid    this tuple id
     * @param values Array of values to each dimension.
     */
    public void addTuple(int tid, int[] values) {


        for (int i = 0; i < shellFragmentList.length; i++)
            shellFragmentList[i].addTuple(tid, values[i]);
    }


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
            if (query[i] != -88 && query[i] != -99) {
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

        int[] returnable = tidsList[0];
        if (instanciated > 0) {
            for (int i = 1; i < instanciated; i++) {
                returnable = intersect(returnable, tidsList[i]);
                if (returnable.length == 0)
                    return returnable;
                tidsList[i] = null; //chama para o garabge colector
            }

            return returnable;
        }

        return shellFragmentList[0].getAllTids();
    }


    public int[] pointQuerySeach(ShellFragment[] subCube, int[] query) {
        if (query.length != subCube.length)
            return null;

        int instanciated = 0;
        int[][] tidsList = new int[subCube.length][];                 //stores values of instanciated
        for (int i = 0; i < query.length; i++) {                                        //obtem todas as listas de values
            if (query[i] != -88 && query[i] != -99) {
                int[] returned = subCube[i].getTidsListFromValue(query[i]);
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
            //System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        }

        int[] d = tidsList[0];
        if (instanciated > 0) {
            for (int i = 1; i < instanciated; i++) {

                d = intersect(d, tidsList[i]);

                if (d.length == 0)
                    return d;
            }

            return d;
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
            return ;
        }

        int[] tidArray = this.pointQuerySeach(values);            //obtem TIDs resultante
        if (tidArray == null || tidArray.length == 0) {
            System.out.println("no values found");
            return ;
        }


      ShellFragment[] subCube = new ShellFragment[shellFragmentList.length];
        for (int i = 0; i < subCube.length; i++) {
            subCube[i] = new ShellFragment( shellFragmentList[i].lower, shellFragmentList[i].upper);
        }

        //para cada tid resultante
        for (int i = 0; i < tidArray.length; i++) {
            //para cada dimensão
            for (int j = 0; j < shellFragmentList.length; j++) {
                subCube[j].addTuple(i, shellFragmentList[j].getValueFromTid(tidArray[i]));
            }
        }

        System.out.println("num tids: " +tidArray.length);

      /*        The pronage is not done because it will only result in error when reading memory consumption
                Due to the temporary nature of subcubes, there is no need to have such measures

        for (int j = 0; j < subCube.length; j++) {
            subCube[j].proneShellFragment();
        }
       */

        //System.gc(); //Does not affect anything, such as tidArray, values or subCube, it only cleans secondary trash.

        //System.out.println();
        //showQueryDataCube(values, subCube);        // a nova função que mostra as coisas
    }

    /**
     * @param qValues       the query
     * @param subCube       the resulted subCube
     */
    private void showQueryDataCube(int[] qValues, ShellFragment[] subCube) {

        int[] query = new int[subCube.length];               //stores all the values as a query.
        int[] counter = new int[subCube.length];             //counter to the query values
        for (int c : counter)
            c = 0;

        int[][] values = getAllDifferentValues(qValues);      //guarda todos os valores diferentes para cada dimensão


        double total = 1;                              //guarda o numero de conbinações difrerentes
        for (int[] d : values) {
            total *= (d.length);
        }

        int rounds = 0;
        do {
            for (int i = 0; i < counter.length; i++)     //da os valores as queries
                query[i] = values[i][counter[i]];


            //pesquisa e mostra com valores do query
            getNumeroDeTuplesComCaracteristicas(query, subCube);// faz pesquisa sobre esses valores

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
    private int[][] getAllDifferentValues(int[] queryValues) {
        int[][] result = new int[queryValues.length][1];

        for (int i = 0; i < queryValues.length; i++) {               //para cada uma das dimensões
            if (queryValues[i] == -99) {
                result[i] = new int[shellFragmentList[i].matrix.length + 1];
                result[i][0] = -88;
                for (int j = result[i].length; j > 1; result[i][--j] = j) {
                }
            } else {
                result[i][0] = queryValues[i];
            }
        }
        return result;
    }


    private void getNumeroDeTuplesComCaracteristicas(int[] query, ShellFragment[] subCube) {
        /*int count = 0;
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
         */

        int count = pointQueryCounter(subCube, query);

        StringBuilder str = new StringBuilder();                            //obtem os dados e mostra
        for (int i : query)
            if (i != -88)
                str.append((i)).append(" ");
            else
                str.append("* ");
        str.append(": ").append(count);
        System.out.println(str);

    }

    public int pointQueryCounter(ShellFragment[] subCube, int[] query) {
        int[] mat = pointQuerySeach(subCube, query);
        if (mat == null)
            return -1;

        return mat.length;
    }


    /**
     * @param tid tuple id to seach on
     * @return an array with the dimensional values of such tuple.
     */
    private int[] getDimensions(int tid, int[] query) {
        int[] returnable = new int[shellFragmentList.length];

        for (int i = 0; i < query.length; i++) {//query.length == shellfragmentelist.length
            if (query[i] == -99 || query[i] == -88)      //se estiver instanciado tem de ter estes valores
                returnable[i] = shellFragmentList[i].getValueFromTid(tid);
            else {                                       //se não tiver instanciado tem de se procurar
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

    public void proneShellfragments() {
        for (ShellFragment s : shellFragmentList)
            s.proneShellFragment();
    }
}
