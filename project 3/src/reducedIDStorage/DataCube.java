package reducedIDStorage;

import java.util.Arrays;

public class DataCube {

    ShellFragment[] shellFragmentList;
    int lower;

    public DataCube(int[][] rawData, int[] maxValue, int lowerValue) {

        shellFragmentList = new ShellFragment[rawData[0].length];
        this.lower = lowerValue;

        for (int i = 0; i < rawData[0].length; i++) {
            int[] dimensionalValues = new int[rawData.length];

            for (int d = 0; d < rawData.length; d++)
                dimensionalValues[d] = rawData[d][i];

            shellFragmentList[i] = new ShellFragment(dimensionalValues, lowerValue, maxValue[i + 1], maxValue[0]);
            //shellFragmentList[i] = new ShellFragment(dimensionalValues, lowerValue, maxValue[i + 1], maxValue[0], i);
            //shellFragmentList[i] = new ShellFragment(dimensionalValues, lowerValue, maxValue[i + 1]);
            System.out.println("Dimension number " + (i + 1) + " created");
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
            int[] valuesList = shellFragmentList[i].getAllValues();
            for (int n = 0; n < valuesList.length; n++) {
                str = new StringBuilder();
                str.append(valuesList[n]).append("\t\t").append(Arrays.toString(shellFragmentList[i].getTidsListFromValue(valuesList[n])));
                System.out.println(str);
            }
        }
    }

    /**
     * @param query the query being made
     * @return array with the tuple ids, null if query.length != shellFragmentList.length
     */
    public int[] pointQuerySeach(int[] query) {
        if (query.length != shellFragmentList.length)
            return null;

        int[] retornable = new int[0];
        for (int i = 0; i < query.length; i++) {
            if (query[i] != '*' && query[i] != '?') {
                int[] secundary = shellFragmentList[i].getTidsListFromValue(query[i]);      //obtem lista de tids
                if (secundary.length == 0)                                      //se a lista for vazia, devolve null
                return secundary;

                if (retornable.length == 0)
                    retornable = secundary;
                else {
                    retornable = intersect(retornable, secundary);
                    if (retornable.length == 0)
                        return retornable;
                }
            }
        }
        //caso todas tenham valor '?' ou '*'
        if (retornable.length == 0)
            return shellFragmentList[0].getAllTids();
        return retornable;

    }

    //can have its perforce improved

    /**
     * @param first  array of tids
     * @param second array of tids
     * @return the array with the tids existing in both arrays received
     */
    private int[] intersect(int[] first, int[] second) {
        int[] retornable;
        int[] secundary = first.length < second.length ? new int[first.length] : new int[second.length];
        int counter = 0;

        for (int a : first){                        //para cada um dos valores do array 1
            for (int b : second) {                      //para cada um dos valores do array 2
                if (a == b) {                               //se ambos valores forem iguais
                    secundary[counter] = a;                     //valor A é adicionado
                    counter++;
                    break;
                }
            }
        }
        retornable = new int[counter];
        System.arraycopy(secundary, 0, retornable, 0, counter);

        return retornable;
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

        int[] tidArrat = this.pointQuerySeach(values);            //obtem TIDs resultante

        if (tidArrat == null || tidArrat.length == 0)
        {
            System.out.println("no values found");
            return;
        }


        int[][] subCubeValues = new int[tidArrat.length][];                             //aloca memoria array de valores

        for (int i = 0; i < subCubeValues.length; i++) {                                     //para cada um dos IDs de tuples que respeita o pedido
            subCubeValues[i] = getDimensions(tidArrat[i]);                              //obtem-se os seus valores e coloca-se no array de representação de objetos
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
    private int[] getDimensions(int tid) {
        int[] returnable = new int[shellFragmentList.length];

        for (int i = 0; i < shellFragmentList.length; i++)
            returnable[i] = shellFragmentList[i].getValueFromTid(tid);

        return returnable;
    }
}
