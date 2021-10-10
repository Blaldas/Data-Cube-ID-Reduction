

package UpdateStructure;


import java.util.Arrays;

public class DataCube {

    ShellFragment[] shellFragmentList;
    int lower;

    //int[][] modifiedTuples;
    //int sizeModifiedArray;


    public DataCube(int[] maxValue, int lowerValue) {
        shellFragmentList = new ShellFragment[maxValue.length]; //o primerio é o númeo de tuplas
        this.lower = lowerValue;

        for (int i = 0; i < shellFragmentList.length; i++) {
            shellFragmentList[i] = new ShellFragment(lowerValue, maxValue[i]);
        }
    }

    public void addTuple(int tid, int[] tupleValues) {
        for (int i = 0; i < shellFragmentList.length; i++) {
            shellFragmentList[i].addTuple(tid, tupleValues[i]);
        }
    }

    public void reduceMaximumMemory() {
        for (ShellFragment s : shellFragmentList)
            s.reduceMaximumMemory();
    }

    /**
     * @param query the query beinmg made
     * @return all the tids obtained from such query
     */
    public int pointQueryCounter(int[] query) {
        DIntArray mat = pointQuerySeach(query);
        if (mat == null)
            return -1;

        System.out.println(mat.toString());

        return mat.countStoredTids();

    }


    public int pointQueryCounterSubCube(ShellFragment[] subCube, int[] query) {
        DIntArray mat = pointQuerySeachSubCube(subCube, query);
        if (mat == null)
            return -1;
        return mat.countStoredTids();
    }


    /**
     * @param query the query being made
     * @return array with the tuple ids, null if query.length != shellFragmentList.length
     */
    public DIntArray pointQuerySeach(int[] query) {

        if (query.length != shellFragmentList.length)
            return null;

        int instanciated = 0;
        DIntArray[] tidsList = new DIntArray[shellFragmentList.length];
        for (int i = 0; i < query.length; i++) {
            if (query[i] != -88 && query[i] != -99) {
                DIntArray secundary = shellFragmentList[i].getTidsListFromValue(query[i]);
                if (secundary.countStoredTids() == 0)       //se o valor colocado nao der resultados
                    return secundary;
                if (instanciated == 0)          //se ainda nada tiver sido instanciado
                    tidsList[0] = secundary;
                else {
                    for (int n = instanciated - 1; n >= 0; n--) {
                        if (tidsList[n].intersetionCount() > secundary.intersetionCount()) {
                            tidsList[n + 1] = tidsList[n];
                            if (n == 0)
                                tidsList[0] = secundary;
                        } else {
                            tidsList[n + 1] = secundary;
                            break;
                        }
                    }

                }
                instanciated++;
            }
        }
        DIntArray result;
        if (instanciated > 0) {
            result = tidsList[0];
            for (int i = 1; i < instanciated; i++) {

                result = intersect(result, tidsList[i]);

                if (result.intersetionCount() == 0)
                    return result;
            }

        } else {
            result = new DIntArray();
            //result.addValues(0, shellFragmentList[0].getBiggestTid());
            result.reducedPos1 = new int[1];
            result.reducedPos2 = new int[1];
            result.reducedPos2[0] = shellFragmentList[0].getBiggestTid();
            ++result.sizeReduced;
        }

        return result;
    }

    public DIntArray pointQuerySeachSubCube(ShellFragment[] subCube, int[] query) {
        if (query.length != subCube.length)
            return null;

        int instanciated = 0;
        DIntArray[] tidsList = new DIntArray[subCube.length];
        for (int i = 0; i < query.length; i++) {
            if (query[i] != -88) {
                DIntArray secundary = subCube[i].getTidsListFromValue(query[i]);
                if (secundary.intersetionCount() == 0)       //se o valor colocado nao der resultados
                    return secundary;
                if (instanciated == 0)          //se ainda nada tiver sido instanciado
                    tidsList[0] = secundary;
                else {
                    for (int n = instanciated - 1; n >= 0; n--) {
                        if (tidsList[n].intersetionCount() > secundary.intersetionCount()) {
                            tidsList[n + 1] = tidsList[n];
                            if (n == 0)
                                tidsList[0] = secundary;
                        } else {
                            tidsList[n + 1] = secundary;
                            break;
                        }
                    }

                }
                instanciated++;
            }
        }
        DIntArray result;
        if (instanciated > 0) {
            result = tidsList[0];
            for (int i = 1; i < instanciated; i++) {

                result = intersect(result, tidsList[i]);

                if (result.intersetionCount() == 0)
                    return result;
            }
            return result;
        } else {
            result = new DIntArray();
            //result.addValues(0, shellFragmentList[0].getBiggestTid());
            result.reducedPos1 = new int[1];
            result.reducedPos2 = new int[1];
            result.reducedPos2[0] = subCube[0].getBiggestTid();
            ++result.sizeReduced;
        }

        return result;
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

        //int[] tidArray = this.pointQueryAdapter(values);            //obtem TIDs resultante
        int[] tidArray = pointQuerySeach(values).getAsArray();
        if (tidArray.length == 0) { //its never null cause its verified above
            System.out.println("no values found");
            return;
        }

        //mostra resposta a query inicial:
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == -99 || values[i] == -88)
                str.append('*').append(" ");
            else
                str.append(values[i]).append(" ");
        }
        str.append(": ").append(tidArray.length);
        System.out.println(str);

        System.out.println("A recriar sub dataset");
        //para cada tid resultante
        int numInqiridas = 0;
        for (int i : values)
            if (i == -99)
                numInqiridas++;

        int[] mapeamentoDimInq = new int[numInqiridas];
        numInqiridas = 0;
        for (int i = 0; i < values.length; i++)
            if (values[i] == -99)
                mapeamentoDimInq[numInqiridas++] = i;


        int subdataset[][] = new int[tidArray.length][numInqiridas];//cada linha é uma tupla, cada coluna é uma dimensão;

        //para cada dimensão, obtem os tids, interceta com os tids do subCubo e adiciona os valores:
        for (int d = 0; d < mapeamentoDimInq.length; d++) {     //para cada uma das dimensões inquiridas
            for (int i = 0; i < shellFragmentList[mapeamentoDimInq[d]].matrix.length; i++) {      //para cada valor da dimensão
                DIntArray val;
                if (shellFragmentList[mapeamentoDimInq[d]].matrix[i] != null)
                    val = shellFragmentList[mapeamentoDimInq[d]].matrix[i];                     //obtem lista de tids com esse valor
                else
                    continue;

                //note-se: val tem tamanho exato.
                //faz interceção: val com tidArray
                int ti = 0;
                int ci = 0;
                int di = 0;
                while (ti < tidArray.length && (ci < val.sizeReduced || di < val.sizeNonReduced)) {   //interceção e adiciona
                    if (ci == val.sizeReduced) {
                        if (tidArray[ti] == val.noReductionArray[di]) {
                            subdataset[ti++][d] = i + lower;    //lower igual a 1 para todas as diemnsões!
                            ++di;
                        } else if (tidArray[ti] < val.noReductionArray[di])
                            ++ti;
                        else
                            ++di;
                    } else if (di == val.sizeNonReduced) {
                        if (tidArray[ti] >= val.reducedPos1[ci] && tidArray[ti] <= val.reducedPos2[ci]) {
                            subdataset[ti++][d] = i + lower;    //lower igual a 1 para todas as diemnsões!
                        } else if (tidArray[ti] < val.reducedPos2[ci])
                            ++ti;
                        else
                            ++ci;
                    } else {
                        if (val.reducedPos1[ci] < val.noReductionArray[di]) { //reduzido menor
                            if (tidArray[ti] >= val.reducedPos1[ci] && tidArray[ti] <= val.reducedPos2[ci]) {
                                subdataset[ti++][d] = i + lower;    //lower igual a 1 para todas as diemnsões!
                            } else if (tidArray[ti] < val.reducedPos2[ci])
                                ++ti;
                            else
                                ++ci;
                        } else {              //nao reduzido menor
                            if (tidArray[ti] == val.noReductionArray[di]) {
                                subdataset[ti++][d] = i + lower;    //lower igual a 1 para todas as diemnsões!
                                ++di;

                            } else if (tidArray[ti] < val.noReductionArray[di])
                                ++ti;
                            else
                                ++di;
                        }
                    }
                }
            }
        }

        //para cada dimensão, altera os valores de forma a ter os valores atualizados
        //para cada uma das dimensões inquiridas
        int mi;
        int si;
        for (int i = 0; i < numInqiridas; i++) {
            mi = 0;
            si = 0;
               while(mi < shellFragmentList[mapeamentoDimInq[i]].sizeModified && si < tidArray.length){
                   //caso o tid esteja modificado, atuaiza o seu valor
                   if(shellFragmentList[mapeamentoDimInq[i]].tidModified[mi] == tidArray[si]){
                       subdataset[tidArray[si]][i] = shellFragmentList[mapeamentoDimInq[i]].valueModified[mi];
                       ++mi;
                       ++si;
                   }
                   else if (shellFragmentList[mapeamentoDimInq[i]].tidModified[mi] < tidArray[si])
                       ++mi;
                   else
                       ++si;
               }
        }


        //System.out.println(Arrays.deepToString(subdataset));

        //System.exit(0);


        //FAZ O SUBCUBE------------------------
        System.out.println("A refazer cubo");
        //cria os shellFragments
        ShellFragment[] subCube = new ShellFragment[numInqiridas];
        for (int i = 0; i < subCube.length; i++) {
            subCube[i] = new ShellFragment(shellFragmentList[mapeamentoDimInq[i]].lower, shellFragmentList[mapeamentoDimInq[i]].getBiggestValue());
        }

        for (int i = 0; i < subdataset.length; i++)
            for (int d = 0; d < subCube.length; d++)
                subCube[d].addTuple(i, subdataset[i][d]);

        System.out.println("Subcubo acabado");

        //System.gc();


        //////////////////////////////////////////////////////////////////////////////////////////
        //USED TO SHOW EVERY SINGLE COMBINATION AVAILABLE.
        //showQueryDataCube(values, subCube);        // a nova função que mostra as coisas
        showQueryDataCube(values, mapeamentoDimInq, subCube);
    }

    private void showQueryDataCube(int[] qValues, int[] mapeamentoDimInq, ShellFragment[] subCube) {

        int[] query = new int[subCube.length];               //stores all the values as a query.
        int[] counter = new int[subCube.length];             //counter to the query values


        int[][] values = getAllDifferentValues(qValues);      //guarda todos os valores diferentes para cada dimensão para se poder loopar neles
        //numero de prints que se vai fazer
        int total = 1;                              //guarda o numero de conbinações difrerentes
        for (int[] d : values) {
            total *= (d.length);
        }

        System.gc();
        int[][] arrayQueriesEResultados = new int[total][qValues.length + 1];

        //entra no loop de mostrar resultados
        int rounds = 0;
        do {
            for (int i = 0; i < counter.length; i++)     //da os valores as queries
                query[i] = values[mapeamentoDimInq[i]][counter[i]];

            //copia query original
            for (int i = 0; i < qValues.length; i++)
                arrayQueriesEResultados[rounds][i] = qValues[i];
            //modifica dimensões inquiridas para a query
            for (int i = 0; i < mapeamentoDimInq.length; i++)
                arrayQueriesEResultados[rounds][mapeamentoDimInq[i]] = query[i];

            //pesquisa com valores do query e mostra valores
            arrayQueriesEResultados[rounds][qValues.length] = pointQueryCounterSubCube(subCube, query);// faz pesquisa sobre esses valores

            //gere os counters
            for (int i = 0; i < mapeamentoDimInq.length; i++) {              //para cada um dos counter
                if (counter[i] < values[mapeamentoDimInq[i]].length - 1) {
                    counter[i]++;
                    break;
                } else        //if( counter[i] == '*')
                    counter[i] = 0;
            }
            rounds++;
        } while (rounds < total);

        // System.out.println(Arrays.deepToString(arrayQueriesEResultados));

        System.out.println("[");
        StringBuilder str = new StringBuilder();
        for (int[] q : arrayQueriesEResultados) {
            str.setLength(0);
            str.append("[");
            for (int i = 0; i < q.length - 1; i++) {
                if (q[i] == -88)
                    str.append('*').append(" ");
                else if (q[i] == -99)
                    str.append('?').append(" ");
                else
                    str.append(q[i]).append(" ");
                str.append(" ; ");
            }
            str.append(q[q.length - 1]);
            str.append("]");
            System.out.println(str);
        }
        System.out.println("]");


        System.out.println(total + " lines written");
    }

    private int[][] getAllDifferentValues(int[] queryValues) {
        int[][] result = new int[queryValues.length][1];        //aloca com tamanho minimo inicial 1
        //System.out.println(result.length);
        for (int i = 0; i < queryValues.length; i++) {               //para cada uma das dimensões
            if (queryValues[i] == -99) {
                result[i] = new int[shellFragmentList[i].getCardinality() + 1];
                result[i][0] = -88;
                for (int j = result[i].length; j > 1; result[i][--j] = j) {
                }
            } else {
                result[i][0] = queryValues[i];
            }
        }
        return result;
    }

    /**
     * @param values tuple values
     */
    public void addNewTuple(int[] values) {
        if (values.length != shellFragmentList.length) {
            System.out.println("Number of dimensions invalid. Introduced " + values.length + " to data cube with " + shellFragmentList.length + " dimensions!");
            return;
        }

        for (int i = 0; i < shellFragmentList.length; i++)
            if (values[i] < 1) {
                System.out.println("value <1, therefore tuple not added");
                return;
            }


        int newTupleTid = shellFragmentList[0].getBiggestTid();
        newTupleTid++;

        for (int i = 0; i < shellFragmentList.length; i++) {
            shellFragmentList[i].addNewTuple(newTupleTid, values[i]);
        }

    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < shellFragmentList.length; i++) {
            str.append("\n\nDimension").append(i + 1).append("\n");
            str.append(shellFragmentList[i].toString());
        }

        return str.toString();
    }

    public void modifyTuple(int tid, int[] values) {

        if (tid < 0 || tid > shellFragmentList[0].getBiggestTid()) {
            System.out.println("Tuple ID is not stored in the datacube");
            return;
        }

        if (values.length != shellFragmentList.length) {
            System.out.println("Number of dimensions invalid. Introduced " + values.length + " to data cube with " + shellFragmentList.length + " dimensions!");
            return;
        }

        for (int val : values)
            if (val < lower) {
                System.out.println("The attribute " + val + " is not supported due to the program's design");
                return;
            }

        for (int i = 0; i < shellFragmentList.length; i++)
            shellFragmentList[i].modifyTuple(tid, values[i]);
    }
}
