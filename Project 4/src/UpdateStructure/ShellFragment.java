package UpdateStructure;

import java.util.Arrays;

public class ShellFragment {

    DIntArray[] matrix;
    int lower;
    int upper;

    int[] tidModified;
    int[] valueModified;
    int sizeModified;

    /**
     * @param upper biggest value to store
     * @param lower lowest value yo store
     *              <p>
     *              used to create subCubes!
     */
    ShellFragment(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
        matrix = new DIntArray[upper - lower + 1];
        tidModified = new int[0];
        valueModified = new int[0];
        sizeModified = 0;
    }

    /**
     * @param tid      the tid to add - what to add
     * @param tidValue the valye to add - where to add
     */
    public void addTuple(int tid, int tidValue) {
        if (matrix[tidValue - lower] == null) {
            matrix[tidValue - lower] = new DIntArray();
        }
        matrix[tidValue - lower].addTid(tid);
    }

    public void reduceMaximumMemory() {
        for (DIntArray d : matrix) {
            if (d == null)
                continue;
            d.reduceMaximumMemory();
        }
    }

    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0. Care that the array of a found value may be zero as well, so it's not a flag
     */
    public DIntArray getTidsListFromValue(int value) {
        if (value > upper || value < lower || matrix[value - lower] == null) {
            DIntArray d = new DIntArray();
            for (int i = 0; i < sizeModified; i++)
                if (valueModified[i] == value)
                    d.addNewTid(tidModified[i]);
            return d;
        }

        //Copia tudo para um  novo DIntArray
        DIntArray d = new DIntArray();
        d.sizeNonReduced = matrix[value - lower].sizeNonReduced;
        d.sizeReduced = matrix[value - lower].sizeReduced;
        d.reducedPos1 = matrix[value - lower].reducedPos1.clone();
        d.reducedPos2 = matrix[value - lower].reducedPos2.clone();
        d.noReductionArray = matrix[value - lower].noReductionArray.clone();

        return updateArrayByModifiedAttributes(d, value);
    }

    //se o tid for igual remove
    //se não for igual verifica se podia ser igual
    //aumenta os ponteiros devidamente
    private DIntArray updateArrayByModifiedAttributes(DIntArray d, int value) {
        int mi = 0;
        int ci = 0;
        int di = 0;

        while (mi < sizeModified && (ci < d.sizeReduced || di < d.sizeNonReduced)) {
            if (ci == d.sizeReduced) {
                //sem compressão
                //se o tid estiver na lista de modificados já sabe que tem de retirar
                if (tidModified[mi] == d.noReductionArray[di]) {
                    //pucha os tids seguintes para a esquerda
                    for (int i = di; i < d.noReductionArray.length - 2; d.noReductionArray[i] = d.noReductionArray[++i]) {
                    }
                    //diminui quantidade
                    --d.sizeNonReduced;
                    ++mi;
                } else {   //tids diferentes, verifica se podia ser parte da lista

                    //se o valor for o mesmo, é parte da lista e tem que colocar ordenadamente
                    if (value == valueModified[mi]) {
                        //verifica se tem espaço para adicionar um novo tid
                        if (d.sizeNonReduced == d.noReductionArray.length)
                            d.increaseNonReducedArrayby1(); //se nao tiver espaço, acrescenta 1

                        //adicionar à lista:
                        boolean foundFlag = false;  //flag que indica se colocou no meio ou não

                        //procura posição para ordenar pelo meio
                        for (int i = di; i < d.sizeNonReduced; i++) {
                            //quando encontra a poição, puxa tudo para a direita e adiciona
                            if (d.noReductionArray[i] > tidModified[mi]) {
                                //pucha para a direita a partir de i (incluindo)
                                for (int j = d.sizeNonReduced; j > i; d.noReductionArray[j] = d.noReductionArray[--j]) {
                                }
                                //adiciona o tid na posição
                                d.noReductionArray[i] = tidModified[mi];
                                di = i + 1; //di passa a ser a posição seguinte da posição colocada
                                ++d.sizeNonReduced; //aumenta em 1 o sizeNonReducted
                                foundFlag = true;   //indica que encontrou posição na flag
                                break;  //sai do loop
                            }
                        }
                        //se não tiver encontrado a posição para colocar pelo meio, coloca no final
                        if (!foundFlag) {
                            d.noReductionArray[d.sizeNonReduced++] = tidModified[mi];
                        }
                        ++mi;   //incrementa contador de modificados

                        // se nao for para colocar no array, apenas incrementa valor
                    } else if (tidModified[mi] < d.noReductionArray[di])
                        ++mi;
                    else
                        ++di;
                }


            } else if (di == d.sizeNonReduced) {
                //compressão
                //se estiver no meio
                if (tidModified[mi] >= d.reducedPos1[ci] && tidModified[mi] <= d.reducedPos2[ci]) {
                    //caso seja o minimo
                    if (tidModified[mi] == d.reducedPos1[ci]) {
                        ++d.reducedPos1[ci];
                        ++mi;
                    }
                    //caso seja máximo
                    else if (tidModified[mi] == d.reducedPos2[ci]) {
                        --d.reducedPos2[ci];
                        ++mi;
                    }
                    //caso esteja pelo meio
                    else {
                        //garante espaço para adicionar intervalo
                        if (d.reducedPos1.length == d.sizeReduced) {
                            d.increaseReducedArraysby1();
                        }
                        //puxa para a direita todos os seguintes à posição atual
                        for (int i = d.sizeReduced; i > ci + 1; i--) {
                            d.reducedPos1[i] = d.reducedPos1[i - 1];
                            d.reducedPos2[i] = d.reducedPos2[i - 1];
                        }
                        //aumenta contdor do array e coloca o intervalo.
                        //Note-se que isti permite [1; 1], mas é lidar
                        ++d.sizeReduced;
                        d.reducedPos2[ci + 1] = d.reducedPos2[ci];
                        d.reducedPos2[ci] = tidModified[mi] - 1;
                        d.reducedPos1[ci + 1] = tidModified[mi] + 1;
                    }

                } else {
                    //tids diferentes, verifica se podia ser parte da lista

                    //se o valor for o mesmo, é parte da lista e tem que colocar ordenadamente
                    if (value == valueModified[mi]) {
                        //verifica se tem espaço para adicionar um novo tid
                        if (d.sizeNonReduced == d.noReductionArray.length)
                            d.increaseNonReducedArrayby1(); //se nao tiver espaço, acrescenta 1

                        //adicionar à lista:
                        boolean foundFlag = false;  //flag que indica se colocou no meio ou não

                        //procura posição para ordenar pelo meio
                        for (int i = di; i < d.sizeNonReduced; i++) {
                            //quando encontra a poição, puxa tudo para a direita e adiciona
                            if (d.noReductionArray[i] > tidModified[mi]) {
                                //pucha para a direita a partir de i (incluindo)
                                for (int j = d.sizeNonReduced; j > i; d.noReductionArray[j] = d.noReductionArray[--j]) {
                                }
                                //adiciona o tid na posição
                                d.noReductionArray[i] = tidModified[mi];
                                di = i + 1; //di passa a ser a posição seguinte da posição colocada
                                ++d.sizeNonReduced; //aumenta em 1 o sizeNonReducted
                                foundFlag = true;   //indica que encontrou posição na flag
                                break;  //sai do loop
                            }
                        }
                        //se não tiver encontrado a posição para colocar pelo meio, coloca no final
                        if (!foundFlag) {
                            d.noReductionArray[d.sizeNonReduced++] = tidModified[mi];
                        }
                        ++mi;   //incrementa contador de modificados

                        // se nao for para colocar no array, apenas incrementa valor
                    } else if (tidModified[mi] < d.noReductionArray[di])
                        ++mi;
                    else
                        ++di;
                }
            } else {
                if (d.noReductionArray[di] < d.reducedPos1[ci]) {
                    //sem compressão

                    //sem compressão
                    //se o tid estiver na lista de modificados já sabe que tem de retirar
                    if (tidModified[mi] == d.noReductionArray[di]) {
                        //pucha os tids seguintes para a esquerda
                        for (int i = di; i < d.noReductionArray.length - 2; d.noReductionArray[i] = d.noReductionArray[++i]) {
                        }
                        //diminui quantidade
                        --d.sizeNonReduced;
                        ++mi;
                    } else {   //tids diferentes, verifica se podia ser parte da lista

                        //se o valor for o mesmo, é parte da lista e tem que colocar ordenadamente
                        if (value == valueModified[mi]) {
                            //verifica se tem espaço para adicionar um novo tid
                            if (d.sizeNonReduced == d.noReductionArray.length)
                                d.increaseNonReducedArrayby1(); //se nao tiver espaço, acrescenta 1

                            //adicionar à lista:
                            boolean foundFlag = false;  //flag que indica se colocou no meio ou não

                            //procura posição para ordenar pelo meio
                            for (int i = di; i < d.sizeNonReduced; i++) {
                                //quando encontra a poição, puxa tudo para a direita e adiciona
                                if (d.noReductionArray[i] > tidModified[mi]) {
                                    //pucha para a direita a partir de i (incluindo)
                                    for (int j = d.sizeNonReduced; j > i; d.noReductionArray[j] = d.noReductionArray[--j]) {
                                    }
                                    //adiciona o tid na posição
                                    d.noReductionArray[i] = tidModified[mi];
                                    di = i + 1; //di passa a ser a posição seguinte da posição colocada
                                    ++d.sizeNonReduced; //aumenta em 1 o sizeNonReducted
                                    foundFlag = true;   //indica que encontrou posição na flag
                                    break;  //sai do loop
                                }
                            }
                            //se não tiver encontrado a posição para colocar pelo meio, coloca no final
                            if (!foundFlag) {
                                d.noReductionArray[d.sizeNonReduced++] = tidModified[mi];
                            }
                            ++mi;   //incrementa contador de modificados

                            // se nao for para colocar no array, apenas incrementa valor
                        } else if (tidModified[mi] < d.noReductionArray[di])
                            ++mi;
                        else
                            ++di;
                    }
                } else {
                    //compressão

                    //se estiver no meio
                    if (tidModified[mi] >= d.reducedPos1[ci] && tidModified[mi] <= d.reducedPos2[ci]) {
                        //caso seja o minimo
                        if (tidModified[mi] == d.reducedPos1[ci]) {
                            ++d.reducedPos1[ci];
                            ++mi;
                        }
                        //caso seja máximo
                        else if (tidModified[mi] == d.reducedPos2[ci]) {
                            --d.reducedPos2[ci];
                            ++mi;
                        }
                        //caso esteja pelo meio
                        else {
                            //garante espaço para adicionar intervalo
                            if (d.reducedPos1.length == d.sizeReduced) {
                                d.increaseReducedArraysby1();
                            }
                            //puxa para a direita todos os seguintes à posição atual
                            for (int i = d.sizeReduced; i > ci + 1; i--) {
                                d.reducedPos1[i] = d.reducedPos1[i - 1];
                                d.reducedPos2[i] = d.reducedPos2[i - 1];
                            }
                            //aumenta contdor do array e coloca o intervalo.
                            //Note-se que isti permite [1; 1], mas é lidar
                            ++d.sizeReduced;
                            d.reducedPos2[ci + 1] = d.reducedPos2[ci];
                            d.reducedPos2[ci] = tidModified[mi] - 1;
                            d.reducedPos1[ci + 1] = tidModified[mi] + 1;
                        }

                    } else {
                        //tids diferentes, verifica se podia ser parte da lista

                        //se o valor for o mesmo, é parte da lista e tem que colocar ordenadamente
                        if (value == valueModified[mi]) {
                            //verifica se tem espaço para adicionar um novo tid
                            if (d.sizeNonReduced == d.noReductionArray.length)
                                d.increaseNonReducedArrayby1(); //se nao tiver espaço, acrescenta 1

                            //adicionar à lista:
                            boolean foundFlag = false;  //flag que indica se colocou no meio ou não

                            //procura posição para ordenar pelo meio
                            for (int i = di; i < d.sizeNonReduced; i++) {
                                //quando encontra a poição, puxa tudo para a direita e adiciona
                                if (d.noReductionArray[i] > tidModified[mi]) {
                                    //pucha para a direita a partir de i (incluindo)
                                    for (int j = d.sizeNonReduced; j > i; d.noReductionArray[j] = d.noReductionArray[--j]) {
                                    }
                                    //adiciona o tid na posição
                                    d.noReductionArray[i] = tidModified[mi];
                                    di = i + 1; //di passa a ser a posição seguinte da posição colocada
                                    ++d.sizeNonReduced; //aumenta em 1 o sizeNonReducted
                                    foundFlag = true;   //indica que encontrou posição na flag
                                    break;  //sai do loop
                                }
                            }
                            //se não tiver encontrado a posição para colocar pelo meio, coloca no final
                            if (!foundFlag) {
                                d.noReductionArray[d.sizeNonReduced++] = tidModified[mi];
                            }
                            ++mi;   //incrementa contador de modificados

                            // se nao for para colocar no array, apenas incrementa valor
                        } else if (tidModified[mi] < d.noReductionArray[di])
                            ++mi;
                        else
                            ++di;
                    }
                }
            }
        }

        return d;
    }

    /**
     * @param tid id of the tuple to be seached
     * @return the value of such tuple, or lower-1 if not found.
     */
    public int getValueFromTid(int tid) {
        for (int i = 0; i < matrix.length; i++) {                   //para cada uma das linhas
            if (matrix[i] != null && matrix[i].hasTid(tid))
                return i + lower;
        }
        return lower - 1;
    }

    /**
     * @return all the values being stored
     */
    public int[] getAllValues() {
        int[] returnable = new int[matrix.length];

        for (int i = 0; i < returnable.length; returnable[i] = lower + i++) {
        }
        return returnable;
    }


    public int getBiggestTid() {
        int max = -1;                                                                           //coloca um valor inicial nunca returnavel em max

        //para cada dimensão
        for (DIntArray dIntArray : matrix) {
            if (dIntArray == null)
                continue;
            int a = dIntArray.getBigestTid();
            if (a > max)
                max = a;
        }
        return max;                                                             //devolve max
    }


    public int[] getAllTids() {
        int b = getBiggestTid();
        int[] returnable = new int[b + 1];

        for (int i = 0; i < returnable.length; i++)
            returnable[i] = i;

        return returnable;
    }

    /**
     * @param tid      the tuple ID
     * @param tidValue the attribute for this dimension
     *                 <p>
     *                 <p>
     *                 <p>
     *                 Used to add a tuple to the data cube when its origin differs from the file read in the beginning
     */
    public void addNewTuple(int tid, int tidValue) {
        //verifica cardinalidade
        if (tidValue > matrix.length - lower) {
            //aumemnta a cardinalidade desta dimensão
            DIntArray[] b = new DIntArray[tidValue - lower + 1];

            //copia os valores da antiga para a nova
            for (int i = matrix.length; i > 0; b[--i] = matrix[i]) {
            }

            matrix = b;

            System.gc();
        }
        if (matrix[tidValue - lower] == null) {
            matrix[tidValue - lower] = new DIntArray();
        }
        matrix[tidValue - lower].addNewTid(tid);


    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < matrix.length; i++) {
            str.append("\nattribute ").append(i + lower).append(":\n");
            if (matrix[i] != null)
                str.append(matrix[i].toString()).append("\n");
            else
                str.append("<no tids stored>\n");
        }

        str.append("\nModified tuples:\nID\tVALUE\n");
        for (int i = 0; i < sizeModified; i++) {
            str.append(tidModified[i]).append("\t").append(valueModified[i]).append("\n");
        }


        return str.toString();
    }

    public void modifyTuple(int tid, int value) {
        //obter valores deste tid do datacube
        //verificar se novos valores e valores no cubo de dados sao iguais
        //se forem iguais, procurar pelo tid na lista de modificados e remover de lá
        //se forem diferentes, procurar pelo tid na lista de modificados
        //se encontrar - atualizar
        //se não encontrar - adicionar

        int dataCubeValue = getValueFromTid(tid);

        System.out.println("valor:\t\t" + dataCubeValue);

        //caso tenham o mesmo valor
        if (value == dataCubeValue) {
            //procura o tid na lista de mudados
            for (int i = 0; i < sizeModified; i++) {
                //se encontrar, retira-o e volta
                if (tid == tidModified[i]) {
                    //puxar para esquerda
                    for (int j = i; j < sizeModified - 1; j++) {
                        tidModified[j] = tidModified[j + 1];
                        valueModified[j] = valueModified[j + 1];
                    }
                    --sizeModified;
                    return;
                }
            }
            return;
        }
        //se forem diferentes
        //procura atualizar/adicionar tid

        //tenta atualizar
        for (int i = 0; i < sizeModified; i++) {
            //se encontrar, atualiza-o e volta
            if (tid == tidModified[i]) {
                valueModified[i] = value;
                return;
            }
        }

        //adiciona à lista
        //verifica se há espaço
        if (sizeModified == tidModified.length) {
            int[] a = new int[sizeModified + 1];
            int[] b = new int[sizeModified + 1];

            for (int i = sizeModified; i > 0; a[--i] = tidModified[i]) {
            }
            tidModified = a;


            for (int i = sizeModified; i > 0; b[--i] = valueModified[i]) {
            }
            valueModified = b;

            System.gc();
        }

        //procura posição para acrescentar ordenadamente
        for (int i = 0; i < sizeModified; i++) {
            //quando encontra o primeiro maior
            if (tidModified[i] > tid) {
                //puxa para a direita
                for (int j = sizeModified; j > i; j--) {
                    tidModified[j] = tidModified[j - 1];
                    valueModified[j] = valueModified[j - 1];
                }
                //adiciona na posição
                tidModified[i] = tid;
                valueModified[i] = value;
                ++sizeModified;
                return;
            }
        }
        //se chegou aqui é porque é o maior tid e tem de ser acrescentado no final
        tidModified[sizeModified] = tid;
        valueModified[sizeModified] = value;

        ++sizeModified;
    }

    /**
     *
     * @return the cardinality of this dimension
     */
    public int getCardinality() {

        int cardinality = upper;

        for(int val : valueModified)
            if(val > cardinality)
                cardinality = val;

        return cardinality - lower + 1;
    }


    public int getBiggestValue() {
        int biggestVal = upper;

        for(int val : valueModified)
            if(val > biggestVal)
                biggestVal = val;

        return biggestVal;
    }
}
