package reducedIDStorageMiexCrompressionChangedSubCubeQuery;


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
    public DataCube(int[] maxValue, int lowerValue){
        shellFragmentList = new ShellFragment[maxValue.length - 1]; //o primerio é o númeo de tuplas
        this.lower = lowerValue;

        for (int i = 0; i < shellFragmentList.length; i++) {
            shellFragmentList[i] = new ShellFragment( maxValue[i + 1], lowerValue);
        }
    }

    public void addTuple(int tid, int[] tupleValues){
        for(int i = 0; i < shellFragmentList.length; i++){
            shellFragmentList[i].addTid(tid, tupleValues[i]);
        }
    }


    /**
     * @param query the query beinmg made
     * @return all the tids obtained from such query
     */
    public int pointQueryCounter(int[] query) {
        DIntArray mat = pointQuerySeach(query);
        if (mat == null)
            return -1;
        return mat.countStoredTids();

    }


    public int pointQueryCounter(ShellFragment[] subCube, int[] query, DIntArray d) {
        DIntArray mat = pointQuerySeach(subCube, query, d);
        if (mat == null)
            return -1;
        int t = mat.countStoredTids();
        mat = null;

        return t;
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

        DIntArray result = null;
        for (int i = 0; i < query.length; i++) {
            if (query[i] != '*' && query[i] != '?') {
                DIntArray secundary = shellFragmentList[i].getTidsListFromValue(query[i]);
                if (secundary.countStoredTids() == 0)       //se o valor colocado nao der resultados
                    return secundary;
                if (result == null)          //se o array final estiver vazio
                    result = secundary;
                else {
                    result = intersect(result, secundary);
                    // System.out.println(Arrays.deepToString(result.get2dMatrix()));
                    if (result.countStoredTids() == 0) {
                        return result;
                    }
                }
            }

            if (result == null) { //caso não exista nenhuma instanciação
                result = new DIntArray();
                //result.addValues(0, shellFragmentList[0].getBiggestTid());
                result.reducedPos1 = new int[1];
                result.reducedPos2 = new int[1];
                result.reducedPos2[0] = shellFragmentList[0].getBiggestTid();
                ++result.sizeReduced;
            }
        }

        return result;
    }

    public DIntArray pointQuerySeach(ShellFragment[] subCube, int[] query,  DIntArray result) {
        if (query.length != subCube.length)
            return null;

        result.clearSpace();

        for (int i = 0; i < query.length; i++) {
            if (query[i] != '*' && query[i] != '?') {
                DIntArray secundary = subCube[i].getTidsListFromValue(query[i]);
                if (secundary.countStoredTids() == 0)       //se o valor colocado nao der resultados
                    return secundary;
                if (result.countStoredTids() == 0)          //se o array final estiver vazio
                    result = secundary;
                else {
                    result = intersect(result, secundary);
                    // System.out.println(Arrays.deepToString(result.get2dMatrix()));
                    if (result.countStoredTids() == 0) {
                        return result;
                    }
                }
            }

            if ( result.countStoredTids() == 0) { //caso não exista nenhuma instanciação
                result = new DIntArray();
                //result.addValues(0, shellFragmentList[0].getBiggestTid());
                result.reducedPos1 = new int[1];
                result.reducedPos2 = new int[1];
                result.reducedPos2[0] = subCube[0].getBiggestTid();
                ++result.sizeReduced;
            }
        }

        return result;
    }

    private int[] getArrayFromMatrix(DIntArray matrix) {
        if (matrix == null)
            return null;
        return matrix.getAsArray();
    }


    /**
     * @param DIntArrayA array of tids
     * @param DIntArrayB array of tids
     * @return the array with the tids existing in both arrays received
     */
    private static DIntArray intersect(DIntArray DIntArrayA, DIntArray DIntArrayB) {

        DIntArray c = new DIntArray();
        int aiNONreduced = 0, aiReduced = 0;
        int biNONreduced = 0, biReduced = 0;

        //obtem menor entre A.reduced1[aiReduced] e A.nonreduced[aiNONreduced]
        //obtem menor entre B.reduced1[biReduced] e B.nonreduced[biNONreduced]
        //faz interceção entre os dois valores obtidos
        //Adiciona resultado dessa intercepção

        while ((aiNONreduced < DIntArrayA.sizeNonReduced || aiReduced < DIntArrayA.sizeReduced) && (biNONreduced < DIntArrayB.sizeNonReduced || biReduced < DIntArrayB.sizeReduced)) {
            //obtem menor valor A
            if (aiNONreduced == DIntArrayA.sizeNonReduced) { //caso não exista não comprimido-> pega-se o comprimido seguinte
                //A -> 2
                //obtem menor valor B
                if (biNONreduced == DIntArrayB.sizeNonReduced) { //caso não exista não-comprimido-> pega-se o comprimido seguinte
                    //A -> 2 e B-> 2
                    if (DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.reducedPos2[aiReduced] >= DIntArrayB.reducedPos1[biReduced]) {         //[b0 , - ]
                        if (DIntArrayB.reducedPos1[biReduced] == DIntArrayA.reducedPos2[aiReduced])     //caso o primeiro esteja no final do útimo
                            c.addTid(DIntArrayB.reducedPos1[biReduced]);
                        else
                            c.addTidInterval(DIntArrayB.reducedPos1[biReduced], DIntArrayA.reducedPos2[aiReduced] > DIntArrayB.reducedPos2[biReduced] ? DIntArrayB.reducedPos2[biReduced] : DIntArrayA.reducedPos2[aiReduced]);
                    } else if (DIntArrayA.reducedPos1[aiReduced] >= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.reducedPos2[biReduced]) {
                        if (DIntArrayB.reducedPos2[biReduced] == DIntArrayA.reducedPos1[aiReduced])     //caso o primeiro esteja no final do útimo
                            c.addTid(DIntArrayA.reducedPos1[aiReduced]);
                        else
                            c.addTidInterval(DIntArrayA.reducedPos1[aiReduced], DIntArrayA.reducedPos2[aiReduced] > DIntArrayB.reducedPos2[biReduced] ? DIntArrayB.reducedPos2[biReduced] : DIntArrayA.reducedPos2[aiReduced]);
                    }
                    if (DIntArrayA.reducedPos2[aiReduced] < DIntArrayB.reducedPos2[biReduced])
                        ++aiReduced;
                    else
                        ++biReduced;

                } else if (biReduced == DIntArrayB.sizeReduced) {
                    //A -> 2, B -> 1
                    if (DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.noReductionArray[biNONreduced] && DIntArrayA.reducedPos2[aiReduced] >= DIntArrayB.noReductionArray[biNONreduced]) {     //B1 entre A1 e A2
                        c.addTid(DIntArrayB.noReductionArray[biNONreduced]);
                        ++biNONreduced;
                    } else if (DIntArrayA.reducedPos2[aiReduced] < DIntArrayB.noReductionArray[biNONreduced])                                           //A1 menor que B1
                        ++aiReduced;
                    else                                                                                //A1 maior que B1
                        ++biNONreduced;

                } else {  //caso ambos existam -> pegam-se o menor valor;
                    if (DIntArrayB.noReductionArray[biNONreduced] < DIntArrayB.reducedPos1[biReduced]) {      //caso sem redução seja menor -> obtyem-se sem redução
                        //A -> 2, B -> 1
                        if (DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.noReductionArray[biNONreduced] && DIntArrayA.reducedPos2[aiReduced] >= DIntArrayB.noReductionArray[biNONreduced]) {     //B1 entre A1 e A2
                            c.addTid(DIntArrayB.noReductionArray[biNONreduced]);
                            ++biNONreduced;
                        } else if (DIntArrayA.reducedPos2[aiReduced] < DIntArrayB.noReductionArray[biNONreduced])                                           //A1 menor que B1
                            ++aiReduced;
                        else                                                                                //A1 maior que B1
                            ++biNONreduced;
                    } else {
                        //A -> 2 e B-> 2
                        if (DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.reducedPos2[aiReduced] >= DIntArrayB.reducedPos1[biReduced]) {         //[b0 , - ]
                            if (DIntArrayB.reducedPos1[biReduced] == DIntArrayA.reducedPos2[aiReduced])     //caso o primeiro esteja no final do útimo
                                c.addTid(DIntArrayB.reducedPos1[biReduced]);
                            else
                                c.addTidInterval(DIntArrayB.reducedPos1[biReduced], DIntArrayA.reducedPos2[aiReduced] > DIntArrayB.reducedPos2[biReduced] ? DIntArrayB.reducedPos2[biReduced] : DIntArrayA.reducedPos2[aiReduced]);
                        } else if (DIntArrayA.reducedPos1[aiReduced] >= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.reducedPos2[biReduced]) {
                            if (DIntArrayB.reducedPos2[biReduced] == DIntArrayA.reducedPos1[aiReduced])     //caso o primeiro esteja no final do útimo
                                c.addTid(DIntArrayA.reducedPos1[aiReduced]);
                            else
                                c.addTidInterval(DIntArrayA.reducedPos1[aiReduced], DIntArrayA.reducedPos2[aiReduced] > DIntArrayB.reducedPos2[biReduced] ? DIntArrayB.reducedPos2[biReduced] : DIntArrayA.reducedPos2[aiReduced]);
                        }
                        if (DIntArrayA.reducedPos2[aiReduced] < DIntArrayB.reducedPos2[biReduced])
                            ++aiReduced;
                        else
                            ++biReduced;

                    }
                }
            } else if (aiReduced == DIntArrayA.sizeReduced) {  //caso não exista comprimido -> pega-se no não-comprimido seguinte
                //A -> 1
                if (biNONreduced == DIntArrayB.sizeNonReduced) { //caso não exista não comprimido-> pega-se o comprimido seguinte
                    //A-> 1, B -> 2
                    if (DIntArrayA.noReductionArray[aiNONreduced] >= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.noReductionArray[aiNONreduced] <= DIntArrayB.reducedPos2[biReduced]) {     //A1 entre B1 e B2
                        c.addTid(DIntArrayA.noReductionArray[aiNONreduced++]);
                    } else if (DIntArrayA.noReductionArray[aiNONreduced] < DIntArrayB.reducedPos2[biReduced])                                           //A1 menor que B2
                        ++aiNONreduced;                                                                                       //avança com pontyeiro A
                    else                                                                                //A1 maior que  B2
                        ++biReduced;

                } else if (biReduced == DIntArrayB.sizeReduced) {  //caso não exista comprimido -> pega-se no não-comprimido seguinte
                    //A-> 1, B -> 1
                    if (DIntArrayA.noReductionArray[aiNONreduced] == DIntArrayB.noReductionArray[biNONreduced]) {       //sao iguais
                        c.addTid(DIntArrayA.noReductionArray[aiNONreduced]);                             //adiciona valor 1
                        ++aiNONreduced;                                                           //avança com pontyeiro A
                        ++biNONreduced;                                                           //avança com ponteiro B
                    } else if (DIntArrayA.noReductionArray[aiNONreduced] < DIntArrayB.noReductionArray[biNONreduced])       //A menor que B
                        ++aiNONreduced;                                                    //avança com pontyeiro A
                    else                                                    //B menor que A
                        ++biNONreduced;

                } else {  //caso ambos em B existam -> pegam-se o menor valor;
                    if (DIntArrayB.noReductionArray[biNONreduced] < DIntArrayB.reducedPos1[biReduced]) {      //caso sem redução seja menor -> obtyem-se sem redução
                        //A-> 1, B -> 1
                        if (DIntArrayA.noReductionArray[aiNONreduced] == DIntArrayB.noReductionArray[biNONreduced]) {       //sao iguais
                            c.addTid(DIntArrayA.noReductionArray[aiNONreduced]);                             //adiciona valor 1
                            ++aiNONreduced;                                                           //avança com pontyeiro A
                            ++biNONreduced;                                                           //avança com ponteiro B
                        } else if (DIntArrayA.noReductionArray[aiNONreduced] < DIntArrayB.noReductionArray[biNONreduced])       //A menor que B
                            ++aiNONreduced;                                                    //avança com pontyeiro A
                        else                                                    //B menor que A
                            ++biNONreduced;

                    } else {
                        //A-> 1, B -> 2
                        if (DIntArrayA.noReductionArray[aiNONreduced] >= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.noReductionArray[aiNONreduced] <= DIntArrayB.reducedPos2[biReduced]) {     //A1 entre B1 e B2
                            c.addTid(DIntArrayA.noReductionArray[aiNONreduced++]);
                        } else if (DIntArrayA.noReductionArray[aiNONreduced] < DIntArrayB.reducedPos2[biReduced])                                           //A1 menor que B2
                            ++aiNONreduced;                                                                                       //avança com pontyeiro A
                        else                                                                                //A1 maior que  B2
                            ++biReduced;
                    }
                }
            } else {  //caso ambos existam -> pegam-se o menor valor;
                if (DIntArrayA.noReductionArray[aiNONreduced] < DIntArrayA.reducedPos1[aiReduced]) {      //caso sem redução seja menor -> obtyem-se sem redução
                    // A -> 1
                    if (biNONreduced == DIntArrayB.sizeNonReduced) { //caso não exista não comprimido-> pega-se o comprimido seguinte
                        //A-> 1, B -> 2
                        if (DIntArrayA.noReductionArray[aiNONreduced] >= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.noReductionArray[aiNONreduced] <= DIntArrayB.reducedPos2[biReduced]) {     //A1 entre B1 e B2
                            c.addTid(DIntArrayA.noReductionArray[aiNONreduced++]);
                        } else if (DIntArrayA.noReductionArray[aiNONreduced] < DIntArrayB.reducedPos2[biReduced])                                           //A1 menor que B2
                            ++aiNONreduced;                                                                                       //avança com pontyeiro A
                        else                                                                                //A1 maior que  B2
                            ++biReduced;

                    } else if (biReduced == DIntArrayB.sizeReduced) {  //caso não exista comprimido -> pega-se no não-comprimido seguinte
                        //A-> 1, B -> 1
                        if (DIntArrayA.noReductionArray[aiNONreduced] == DIntArrayB.noReductionArray[biNONreduced]) {       //sao iguais
                            c.addTid(DIntArrayA.noReductionArray[aiNONreduced]);                             //adiciona valor 1
                            ++aiNONreduced;                                                           //avança com pontyeiro A
                            ++biNONreduced;                                                           //avança com ponteiro B
                        } else if (DIntArrayA.noReductionArray[aiNONreduced] < DIntArrayB.noReductionArray[biNONreduced])       //A menor que B
                            ++aiNONreduced;                                                    //avança com pontyeiro A
                        else                                                    //B menor que A
                            ++biNONreduced;

                    } else {  //caso ambos em B existam -> pegam-se o menor valor;
                        if (DIntArrayB.noReductionArray[biNONreduced] < DIntArrayB.reducedPos1[biReduced]) {      //caso sem redução seja menor -> obtyem-se sem redução
                            //A-> 1, B -> 1
                            if (DIntArrayA.noReductionArray[aiNONreduced] == DIntArrayB.noReductionArray[biNONreduced]) {       //sao iguais
                                c.addTid(DIntArrayA.noReductionArray[aiNONreduced]);                             //adiciona valor 1
                                ++aiNONreduced;                                                           //avança com pontyeiro A
                                ++biNONreduced;                                                           //avança com ponteiro B
                            } else if (DIntArrayA.noReductionArray[aiNONreduced] < DIntArrayB.noReductionArray[biNONreduced])       //A menor que B
                                ++aiNONreduced;                                                    //avança com pontyeiro A
                            else                                                    //B menor que A
                                ++biNONreduced;

                        } else {
                            //A-> 1, B -> 2
                            if (DIntArrayA.noReductionArray[aiNONreduced] >= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.noReductionArray[aiNONreduced] <= DIntArrayB.reducedPos2[biReduced]) {     //A1 entre B1 e B2
                                c.addTid(DIntArrayA.noReductionArray[aiNONreduced++]);
                            } else if (DIntArrayA.noReductionArray[aiNONreduced] < DIntArrayB.reducedPos2[biReduced])                                           //A1 menor que B2
                                ++aiNONreduced;                                                                                       //avança com pontyeiro A
                            else                                                                                //A1 maior que  B2
                                ++biReduced;
                        }
                    }
                } else {
                    //A -> 2
                    if (biNONreduced == DIntArrayB.sizeNonReduced) { //caso não exista não-comprimido-> pega-se o comprimido seguinte
                        //A -> 2 e B-> 2
                        if (DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.reducedPos2[aiReduced] >= DIntArrayB.reducedPos1[biReduced]) {         //[b0 , - ]
                            if (DIntArrayB.reducedPos1[biReduced] == DIntArrayA.reducedPos2[aiReduced])     //caso o primeiro esteja no final do útimo
                                c.addTid(DIntArrayB.reducedPos1[biReduced]);
                            else
                                c.addTidInterval(DIntArrayB.reducedPos1[biReduced], DIntArrayA.reducedPos2[aiReduced] > DIntArrayB.reducedPos2[biReduced] ? DIntArrayB.reducedPos2[biReduced] : DIntArrayA.reducedPos2[aiReduced]);
                        } else if (DIntArrayA.reducedPos1[aiReduced] >= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.reducedPos2[biReduced]) {
                            if (DIntArrayB.reducedPos2[biReduced] == DIntArrayA.reducedPos1[aiReduced])     //caso o primeiro esteja no final do útimo
                                c.addTid(DIntArrayA.reducedPos1[aiReduced]);
                            else
                                c.addTidInterval(DIntArrayA.reducedPos1[aiReduced], DIntArrayA.reducedPos2[aiReduced] > DIntArrayB.reducedPos2[biReduced] ? DIntArrayB.reducedPos2[biReduced] : DIntArrayA.reducedPos2[aiReduced]);
                        }
                        if (DIntArrayA.reducedPos2[aiReduced] < DIntArrayB.reducedPos2[biReduced])
                            ++aiReduced;
                        else
                            ++biReduced;

                    } else if (biReduced == DIntArrayB.sizeReduced) {
                        //A -> 2, B -> 1
                        if (DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.noReductionArray[biNONreduced] && DIntArrayA.reducedPos2[aiReduced] >= DIntArrayB.noReductionArray[biNONreduced]) {     //B1 entre A1 e A2
                            c.addTid(DIntArrayB.noReductionArray[biNONreduced]);
                            ++biNONreduced;
                        } else if (DIntArrayA.reducedPos2[aiReduced] < DIntArrayB.noReductionArray[biNONreduced])                                           //A1 menor que B1
                            ++aiReduced;
                        else                                                                                //A1 maior que B1
                            ++biNONreduced;

                    } else {  //caso ambos existam -> pegam-se o menor valor;
                        if (DIntArrayB.noReductionArray[biNONreduced] < DIntArrayB.reducedPos1[biReduced]) {      //caso sem redução seja menor -> obtyem-se sem redução
                            //A -> 2, B -> 1
                            if (DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.noReductionArray[biNONreduced] && DIntArrayA.reducedPos2[aiReduced] >= DIntArrayB.noReductionArray[biNONreduced]) {     //B1 entre A1 e A2
                                c.addTid(DIntArrayB.noReductionArray[biNONreduced]);
                                ++biNONreduced;
                            } else if (DIntArrayA.reducedPos2[aiReduced] < DIntArrayB.noReductionArray[biNONreduced])                                           //A1 menor que B1
                                ++aiReduced;
                            else                                                                                //A1 maior que B1
                                ++biNONreduced;
                        } else {
                            //A -> 2 e B-> 2
                            if (DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.reducedPos2[aiReduced] >= DIntArrayB.reducedPos1[biReduced]) {         //[b0 , - ]
                                if (DIntArrayB.reducedPos1[biReduced] == DIntArrayA.reducedPos2[aiReduced])     //caso o primeiro esteja no final do útimo
                                    c.addTid(DIntArrayB.reducedPos1[biReduced]);
                                else
                                    c.addTidInterval(DIntArrayB.reducedPos1[biReduced], DIntArrayA.reducedPos2[aiReduced] > DIntArrayB.reducedPos2[biReduced] ? DIntArrayB.reducedPos2[biReduced] : DIntArrayA.reducedPos2[aiReduced]);
                            } else if (DIntArrayA.reducedPos1[aiReduced] >= DIntArrayB.reducedPos1[biReduced] && DIntArrayA.reducedPos1[aiReduced] <= DIntArrayB.reducedPos2[biReduced]) {
                                if (DIntArrayB.reducedPos2[biReduced] == DIntArrayA.reducedPos1[aiReduced])     //caso o primeiro esteja no final do útimo
                                    c.addTid(DIntArrayA.reducedPos1[aiReduced]);
                                else
                                    c.addTidInterval(DIntArrayA.reducedPos1[aiReduced], DIntArrayA.reducedPos2[aiReduced] > DIntArrayB.reducedPos2[biReduced] ? DIntArrayB.reducedPos2[biReduced] : DIntArrayA.reducedPos2[aiReduced]);
                            }
                            if (DIntArrayA.reducedPos2[aiReduced] < DIntArrayB.reducedPos2[biReduced])
                                ++aiReduced;
                            else
                                ++biReduced;

                        }
                    }
                }
            }
        }

        return c;
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
        //FAZ O SUBCUBE------------------------
        //cria os shellFragments
        ShellFragment[] subCube = new ShellFragment[shellFragmentList.length];
        for (int i = 0; i < subCube.length; i++) {
            subCube[i] = new ShellFragment(shellFragmentList[i].upper, shellFragmentList[i].lower);
        }

        //para cada tid resultante
        for (int i = 0; i < tidArray.length; i++) {
            //para cada dimensão
            for (int j = 0; j < shellFragmentList.length; j++) {
                subCube[j].addTid(i, shellFragmentList[j].getValueFromTid(tidArray[i]));
            }
        }
        showQueryDataCube(values, subCube);        // a nova função que mostra as coisas

    }


    /**
     * @param qValues the query
     * @param subCube the subCube created
     */
    private void showQueryDataCube(int[] qValues, ShellFragment[] subCube) {

        int[] query = new int[subCube.length];               //stores all the values as a query.
        int[] counter = new int[subCube.length];             //counter to the query values


        int[][] values = getAllDifferentValues(qValues);      //guarda todos os valores diferentes para cada dimensão para se poder loopar neles

        //numero de prints que se vai fazer
        int total = 1;                              //guarda o numero de conbinações difrerentes
        for (int[] d : values) {
            total *= (d.length);
        }

        //System.gc();
        if (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() > Main.maxMemory)
            Main.maxMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        //entra no loop de mostrar resultados
        DIntArray d = new DIntArray();
        int rounds = 0;
        do {
            for (int i = 0; i < counter.length; i++)     //da os valores as queries
                query[i] = values[i][counter[i]];

            //pesquisa com valores do query e mostra valores
            getNumeroDeTuplesComCaracteristicas(query, subCube, d);// faz pesquisa sobre esses valores

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

    private int[][] getAllDifferentValues(int[] queryValues) {
        int[][] result = new int[queryValues.length][1];        //aloca com tamanho minimo inicial 1

        for (int i = 0; i < queryValues.length; i++) {               //para cada uma das dimensões
            if (queryValues[i] == '?') {
                result[i] = new int[shellFragmentList[i].matrix.length + 1];
                result[i][0] = '*';
                for (int j = result[i].length; j > 1; result[i][--j] = j) {
                }
            } else {
                result[i][0] = queryValues[i];
            }
        }
        return result;
    }

    private void getNumeroDeTuplesComCaracteristicas(int[] query, ShellFragment[] subCube, DIntArray d) {
        //faz point query no subCubo
        int count = pointQueryCounter(subCube, query, d);

        StringBuilder str = new StringBuilder();                            //obtem os dados e mostra
        for (int i : query)
            if (i != '*')
                str.append((i)).append(" ");
            else
                str.append("* ");
        str.append(": ").append(count);
        System.out.println(str);

    }

}
