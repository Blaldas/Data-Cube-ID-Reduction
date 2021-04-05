package legacy;

public class DataCube {
    ShellFragment[] shellFragmentsList;


    /**
     * @param arrayOfValues d1 d1 d1 d1     //as diensões estao ao longo de linhas
     *                      d2 d2 d2 d2
     *                      d3 d3 d3 d3
     */
    public DataCube(int[][] arrayOfValues, int[] sizes) {
        shellFragmentsList = new ShellFragment[arrayOfValues[0].length];    //aloca memória para todas a dimensões
        int count = 1;
        for (int i = 0; i < arrayOfValues[0].length; i++) {       //para cada uma das colunas (dimensões)

            int[] arr = new int[arrayOfValues.length];          //cria um array com tamanho das linhas
            for (int n = 0; n < arrayOfValues.length; n++)        //copia o vaor de cada coluna para novo array
            {
                arr[n] = arrayOfValues[n][i];
            }

            shellFragmentsList[i] = new ShellFragment(arr, sizes[0]);              //cria shell fragment
            System.out.println("Dimension number " + count + " created");
            count++;
            //System.gc();            //calls the garbage collector as an attempt to reduce memory usage

        }
    }

    public DataCube(int[][] arrayOfValues) {
        shellFragmentsList = new ShellFragment[arrayOfValues[0].length];    //aloca memória para todas a dimensões

        for (int i = 0; i < arrayOfValues[0].length; i++) {       //para cada uma das colunas (dimensões)
            int[] arr = new int[arrayOfValues.length];          //cria um array com tamanho das linhas
            for (int n = 0; n < arrayOfValues.length; n++)        //copia o vaor de cada coluna para novo array
            {
                arr[n] = arrayOfValues[n][i];
            }
            shellFragmentsList[i] = new ShellFragment(arr);              //cria shell fragment

        }

    }

    public StringBuilder showDimensions() {
        StringBuilder str = new StringBuilder();
        int dimension = 1;
        for (ShellFragment shellFragment : shellFragmentsList) {
            str.append("Dimension number ").append(dimension).append("\n");
            for (int value : shellFragment.getValues()) {
                str.append("\nvalue ").append(value).append("\n");
                str.append("TID List\t");
                for (int i : shellFragment.getTIDListFromValue(value))
                    str.append(i).append(" ");
            }
            str.append("\n");
            dimension++;
        }
        return str;
    }

    /**
     * @param instanciations array of instanciated dimensions. the array must not have a greater lenght than the number of dimensions
     * @return int array with the IDs of the tuples that have the instantiated characteristics or NULL if the instanciated array has a bigger
     * lenght than the number of dimensions.
     */
    public int[] searchMultipleDimensionsAtOnce(int[] instanciations) {
        if (instanciations.length != shellFragmentsList.length)
            return null;
        int[] finalList = new int[0];
        boolean instanciated = false;

        for (int i = 0; i < instanciations.length; i++) {
            if (instanciations[i] != '*' && instanciations[i] != '?') {
                if (!instanciated) {  //caso nunca tenha havido uma instanciação até esta dimensão
                    finalList = shellFragmentsList[i].getTIDListFromValue(instanciations[i]);        //coloca os valores como valores iniciais
                    instanciated = true;
                    if (finalList == null)           //caso não haja valores, nao vale a pena continuar
                        return new int[0];
                } else {
                    int[] arr = shellFragmentsList[i].getTIDListFromValue(instanciations[i]);
                    if (arr == null)
                        return new int[0];
                    finalList = intersections(finalList, arr);      //intercepta valores
                    if (finalList.length == 0)           //caso não haja valores, nao vale a pena continuar
                        return finalList;
                }
            }
        }
        if (!instanciated) //caso nenhuma dimensão tenha sido instanciada
            return shellFragmentsList[0].getAllTIDs();

        return finalList;


    }

    /**
     * @param finalList        array n1
     * @param tidListFromValue array n2
     * @return returns an array that is the result of an mathematical intersection betweem array n1 and array n2.
     */
    private int[] intersections(int[] finalList, int[] tidListFromValue) {
        int[] retornable = new int[0];
        int[] secundary;
        for (int a : finalList)
            for (int b : tidListFromValue)
                if (a == b) {
                    secundary = new int[retornable.length + 1];
                    System.arraycopy(retornable, 0, secundary, 0, retornable.length);
                    secundary[secundary.length - 1] = a;
                    retornable = secundary;
                    break;                                      //acredito ser um melhoramento, se nao for tira-se
                }
        return retornable;
    }


    /**
     * @param arrayOfValues array with the query values-> the query
     * @return An datacube with the tuples that respect the query values, or null, if there is tuples that have suck values
     */
    public DataCube getSubCube(int[] arrayOfValues) {
        if (arrayOfValues.length != shellFragmentsList.length) {
            System.out.println("wrong number of dimensions");
            return null;
        }

        int[] tidArrat = this.searchMultipleDimensionsAtOnce(arrayOfValues);            //obtem TIDs resultante
        if (tidArrat == null || tidArrat.length == 0)
            return null;
        int[][] subCubeValues = new int[tidArrat.length][];                             //aloca memoria array de valores

        for (int i = 0; i < subCubeValues.length; i++) {                                     //para cada um dos IDs de tuples que respeita o pedido
            subCubeValues[i] = getDimensions(tidArrat[i]);                              //obtem-se os seus valores e coloca-se no array de representação de objetos
        }

        showQueryDataCube(arrayOfValues, subCubeValues);        // a nova função que mostra as coisas
        return null;

        //return new DataCube(subCubeValues);                                             //retorna noivo cubo de dados

    }

    /**
     * @param queryValues   the query
     * @param subCubeValues the dimensional values of the subcube
     */
    private void showQueryDataCube(int[] queryValues, int[][] subCubeValues) {

        int[] query = new int[subCubeValues[0].length];               //stores all the values as a query.
        int[] counter = new int[subCubeValues[0].length];             //counter to the query values
        for (int c : counter)
            c = 0;

        int[][] values = getAllDifferentValues(subCubeValues, queryValues);      //guarda todos os valores diferentes para cada dimensão


        int total = 1;                              //guarda o numero de conbinações difrerentes
        for (int[] d : values)
            total *= (d.length);

        int rounds = 0;
        do {
            for (int i = 0; i < counter.length; i++)     //da os valores *as queries
                query[i] = values[i][counter[i]];


            //pesquisa com valores do query
            getNumeroDeTuplesComCaracteristicas(query, subCubeValues);// faz pesquisa sobre esses valores

            //gere os counters
            for (int i = counter.length-1; i >= 0; i--) {              //para cada um dos counter
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
     * @param subCubeValues matrix of dimensional values
     * @param queryValues the query to get the values from
     * @return all the diferent values for each dimension of the matrix, including '*'
     * <p>funciona bem
     */
    private int[][] getAllDifferentValues(int[][] subCubeValues, int[] queryValues) {
        int[][] result = new int[subCubeValues[0].length][0];


        for (int i = 0; i < queryValues.length; i++) {               //para cada uma das dimensões
            if (queryValues[i] == '?') {
                for (int n =0;  n < subCubeValues.length; n++) {                             //para cada um dos valores da lista original
                    boolean existe = false;                      //flag existe a false
                    for (int e : result[i]) {
                        if (e == subCubeValues[n][i]) {
                            existe = true;              //
                            break;
                        }
                    }

                    if (!existe) {
                        int[] sec = new int[result[i].length + 1];
                        System.arraycopy(result[i], 0, sec, 0, result[i].length);
                        sec[sec.length - 1] = subCubeValues[n][i];
                        result[i] = sec;
                    }
                }
                int[] sec = new int[result[i].length + 1];
                System.arraycopy(result[i], 0, sec, 0, result[i].length);
                sec[sec.length - 1] = '*';
                result[i] = sec;
            } else {
                result[i] = new int[]{queryValues[i]};
            }

        }

        return result;
    }

    /**
     * @param query  query received
     * @param values matrix of tuples
     *               <p>
     *               shows the number of tuples equal to the one received
     *               <p>
     *               funciona bem
     */
    private void getNumeroDeTuplesComCaracteristicas(int[] query, int[][] values) {

        int count = 0;
        for (int[] tuple : values) {                //para cada uma das tuples
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
        if(count == 0)
            return;
        StringBuilder str = new StringBuilder();                            //obtem os dados e mostra
        for (int i : query)
            if (i != '*')
                str.append(i).append(" ");
            else
                str.append("* ");
        str.append(":\t").append(count);
        System.out.println(str);

    }


    /**
     * @param id index value (used as ID) of the tuple/object
     * @return int[] array with the values of each dimension, if the index is not fdound, returns NULL
     */
    public int[] getDimensions(int id) {
        int[] result = new int[shellFragmentsList.length];                                  //aloca memoria para cada uma das diemns~ºoes

        for (int i = 0; i < shellFragmentsList.length; i++) {                                 //para cada uma das dimensões
            result[i] = shellFragmentsList[i].getValueFromID(id);                        //obtem valor da dimensão tendo em conta o index
            if (result[i] == -1)
                return null;
        }
        return result;
    }

    /**
     * @return number of shellfragments/ dimensions
     */
    public int getNumberShellFragments() {
        return shellFragmentsList.length;
    }

    public StringBuilder showIndividualTuples() {
        StringBuilder str = new StringBuilder();
        str.append("id:\t");
        for (int i = 0; i < shellFragmentsList.length; i++)
            str.append("D").append((i + 1)).append("\t");
        str.append("\n");

        for (int id : shellFragmentsList[0].getAllTIDs()) {
            str.append(id).append(":\t");
            for (ShellFragment shellFragment : shellFragmentsList) {
                str.append(shellFragment.getValueFromID(id)).append("\t");
            }
            str.append("\n");
        }
        return str;
    }

    public void showAllQueryPossibilities() {
        StringBuilder str = new StringBuilder();
        System.out.println(getShellFreagmentSize());

        //mostrar as dimensões
        for (int i = 0; i < shellFragmentsList.length; i++)
            str.append("D").append(i + 1).append("\t");
        str.append(":\t#TUPLES\n");

        System.out.println(str);

        int[] indexList = new int[shellFragmentsList.length];
        for (int i : indexList)
            i = 0;


        int[][] valuesList = new int[shellFragmentsList.length][];
        //valuesList stores all the shellfragments values by line
        for (int i = 0; i < shellFragmentsList.length; i++) {
            valuesList[i] = shellFragmentsList[i].getValues();
        }

        //stores the values of the dimensions useing indexList as the index for the valuesList
        int[] beigUsed = new int[shellFragmentsList.length];

        boolean end;
        do {
            str = new StringBuilder();

            //gives to beignUsed the value of the valuesList for the index stated in the indexList, for each dimension
            for (int i = 0; i < beigUsed.length; i++) {
                if (indexList[i] != '*')
                    beigUsed[i] = valuesList[i][indexList[i]];
                else
                    beigUsed[i] = indexList[i];
            }

            //seaches the values
            int[] tuple = searchMultipleDimensionsAtOnce(beigUsed);

            //adds the 'query' values to the string that is going to be shown
            for (int i : beigUsed) {
                if (i != '*')
                    str.append(i).append("\t");                         //adds the values of the tuples
                else
                    str.append('*').append("\t");                         //adds the values of the tuples
            }
            str.append(":\t").append(tuple.length).append("\n");    //adds the number of tuples with those values
            System.out.println(str);

            //cicle to verify if all the indexes are on the last position
            end = true;
            for (int i = 0; i < beigUsed.length; i++)
                if (indexList[i] != ('*')) {
                    end = false;
                    break;
                }
            if (end)            //if end is true then it should leave the do..while loop.
                break;

            //the "complicated" loop
            for (int i = indexList.length - 1; i >= 0; i--) {           //starts from the right to the left
                if (indexList[i] < valuesList[i].length - 1)            //caso ainda estaja no loop, até chegar à penultima posição (na penultima posição, aumenta para a ultima, onde depois passa para o else seguinte)
                {
                    indexList[i]++;
                    break;
                } else if (indexList[i] == valuesList[i].length - 1) {
                    indexList[i] = '*';
                    break;
                } else if (indexList[i] == '*') {
                    indexList[i] = 0;
                } else {
                    System.out.println("Erro");
                    System.exit(-999);
                }
            }


        } while (true);     //yhe break condition is inside the loop,m  no need to have it here
    }

    public int getShellFreagmentSize() {
        return shellFragmentsList[0].values.length;
    }

    public int getNumberTuples() {
        return shellFragmentsList[0].getAllTIDs().length;
    }

    public void showBiggestValue() {
        for(int i =0 ; i < shellFragmentsList.length; i++){
            System.out.println(shellFragmentsList[i].getValues().length);


        }
        System.out.println("num dims\t" + shellFragmentsList.length);
        System.out.println("num tuples\t" + getNumberTuples());

    }
}
