public class InvertedIndexTables {

    //contains the values
    private int[] d1;
    private int[] d2;
    private int[] d3;
    private int[] d4;

    //contains the IDs
    private int[][] td1;
    private int[][] td2;
    private int[][] td3;
    private int[][] td4;

    public InvertedIndexTables(Objeto[] listaObjetos) {
        criaIndiceInvertido(listaObjetos);
    }

    public InvertedIndexTables(int[][] listaInts) {
        criaIndiceInvertidoFromInts(listaInts);
    }

    private void criaIndiceInvertidoFromInts(int[][] listaInts) {
        alocateMemoryFromInts(listaInts);

        td1 = new int[d1.length][0];
        td2 = new int[d2.length][0];
        td3 = new int[d3.length][0];
        td4 = new int[d4.length][0];

        for (int i = 0; i < listaInts.length; i++) {
            //d1
            int index = getIndexOfValue(d1, listaInts[i][0]);

            //verifica se contem um valor (verificação redundante)
            if (index < 0) {
                System.out.println("index < 0\ncriaIndiceInvertido");
                System.exit(-1);
            }

            //aloca memoria com lenght +1 do array do index
            int[] newArray = new int[td1[index].length + 1];
            //copia valores para lá
            System.arraycopy(td1[index], 0, newArray, 0, td1[index].length); //copia de d1 par ao novo array
            newArray[newArray.length - 1] = i;                        //coloca o novo valor no array
            td1[index] = newArray;                                  //coloca td1 a apontar para o novo array


            //d2
            index = getIndexOfValue(d2, listaInts[i][1]);

            //verifica se contem um valor (verificação redundante)
            if (index < 0) {
                System.out.println("index < 0\ncriaIndiceInvertido");
                System.exit(-1);
            }

            //aloca memoria com lenght +1 do array do index
            newArray = new int[td2[index].length + 1];
            //copia valores para lá
            System.arraycopy(td2[index], 0, newArray, 0, td2[index].length); //copia de d1 par ao novo array
            newArray[newArray.length - 1] = i;                        //coloca o novo valor no array
            td2[index] = newArray;                                  //coloca td1 a apontar para o novo array


            //d3
            index = getIndexOfValue(d3, listaInts[i][2]);

            //verifica se contem um valor (verificação redundante)
            if (index < 0) {
                System.out.println("index < 0\ncriaIndiceInvertido");
                System.exit(-1);
            }

            //aloca memoria com lenght +1 do array do index
            newArray = new int[td3[index].length + 1];
            //copia valores para lá
            System.arraycopy(td3[index], 0, newArray, 0, td3[index].length); //copia de d1 par ao novo array
            newArray[newArray.length - 1] = i;                        //coloca o novo valor no array
            td3[index] = newArray;                                  //coloca td1 a apontar para o novo array


            //d4
            index = getIndexOfValue(d4, listaInts[i][3]);

            //verifica se contem um valor (verificação redundante)
            if (index < 0) {
                System.out.println("index < 0\ncriaIndiceInvertido");
                System.exit(-1);
            }

            //aloca memoria com lenght +1 do array do index
            newArray = new int[td4[index].length + 1];
            //copia valores para lá
            System.arraycopy(td4[index], 0, newArray, 0, td4[index].length); //copia de d1 par ao novo array
            newArray[newArray.length - 1] = i;                        //coloca o novo valor no array
            td4[index] = newArray;                                  //coloca td1 a apontar para o novo array
        }
    }

    private void alocateMemoryFromInts(int[][] listaInts) {

        d1 = new int[0];
        d2 = new int[0];
        d3 = new int[0];
        d4 = new int[0];

        for (int[] obj : listaInts) {             //para cada um dos objetos
            boolean existe = false;                   //cria flag de existencia
            for (int v : d1) {              //para cada um dos valores que ja estao guardados
                if (v == obj[0])                //se ja existir
                {
                    existe = true;                    //flag colocada a true
                    break;                            //para a pesquisa
                }
            }
            if (!existe) {                              //caso nao exista
                int[] val = new int[d1.length + 1];          //cria array com tamanho +1
                System.arraycopy(d1, 0, val, 0, d1.length);   //copia de d1 par ao novo array

                d1 = val;                        //indica que valores d1 passa a ser novo array
                d1[d1.length - 1] = obj[0];   //coloca valor na última posição do array
            }

            //D2
            existe = false;                   //cria flag de existencia
            for (int v : d2) {              //para cada um dos valores que ja estao guardados
                if (v == obj[1])                //se ja existir
                {
                    existe = true;                    //flag colocada a true
                    break;                            //para a pesquisa
                }
            }
            if (!existe) {                              //caso nao exista
                int[] val = new int[d2.length + 1];          //cria array com tamanho +1

                System.arraycopy(d2, 0, val, 0, d2.length); //copia de d1 par ao novo array

                d2 = val;                        //indica que valores d1 passa a ser novo array
                d2[d2.length - 1] = obj[1];   //coloca valor na última posição do array
            }

            //D3
            existe = false;                   //cria flag de existencia
            for (int v : d3) {              //para cada um dos valores que ja estao guardados
                if (v == obj[2])                //se ja existir
                {
                    existe = true;                    //flag colocada a true
                    break;                            //para a pesquisa
                }
            }
            if (!existe) {                              //caso nao exista
                int[] val = new int[d3.length + 1];          //cria array com tamanho +1
                System.arraycopy(d3, 0, val, 0, d3.length);    //copia de d1 par ao novo array

                d3 = val;                        //indica que valores d1 passa a ser novo array
                d3[d3.length - 1] = obj[2];   //coloca valor na última posição do array
            }

            //D4
            existe = false;                   //cria flag de existencia
            for (int v : d4) {              //para cada um dos valores que ja estao guardados
                if (v == obj[3])                //se ja existir
                {
                    existe = true;                    //flag colocada a true
                    break;                            //para a pesquisa
                }
            }
            if (!existe) {                              //caso nao exista
                int[] val = new int[d4.length + 1];          //cria array com tamanho +1
                System.arraycopy(d4, 0, val, 0, d4.length);    //copia de d1 par ao novo array

                d4 = val;                        //indica que valores d1 passa a ser novo array
                d4[d4.length - 1] = obj[3];   //coloca valor na última posição do array
            }
        }
    }

    public void showTable() {
        System.out.println("\n\nDIMENSION 1:");
        for (int i = 0; i < td1.length; i++) {
            StringBuilder str = new StringBuilder();
            for (int n = 0; n < td1[i].length; n++)
                str.append(td1[i][n]).append(" ");
            System.out.println("\nValor\t" + d1[i]);
            System.out.println("TIDs\t" + str);
        }

        System.out.println("\n\nDIMENSION 2:");
        for (int i = 0; i < td2.length; i++) {
            StringBuilder str = new StringBuilder();
            for (int n = 0; n < td2[i].length; n++)
                str.append(td2[i][n]).append(" ");
            System.out.println("\nValor\t" + d2[i]);
            System.out.println("TIDs\t" + str);
        }

        System.out.println("\n\nDIMENSION 3:");
        for (int i = 0; i < td3.length; i++) {
            StringBuilder str = new StringBuilder();
            for (int n = 0; n < td3[i].length; n++)
                str.append(td3[i][n]).append(" ");
            System.out.println("\nValor\t" + d3[i]);
            System.out.println("TIDs\t" + str);
        }

        System.out.println("\n\nDIMENSION 4:");
        for (int i = 0; i < td4.length; i++) {
            StringBuilder str = new StringBuilder();
            for (int n = 0; n < td4[i].length; n++)
                str.append(td4[i][n]).append(" ");
            System.out.println("\nValor\t" + d4[i]);
            System.out.println("TIDs\t" + str);
        }
    }

    /**
     * @param d1 valor Dimensão 1
     * @param d2 valor Dimensão 2
     * @param d3 valor Dimensão 3
     * @param d4 valor Dimensão 4
     * @return matrix of size 4, with every arr[] representing a dimension from 1 to 4
     */
   /* public int[][] searchInquiredValues(String d1, String d2, String d3, String d4)
    {
        int[][] matrix = new int[4][];
        if (d1.compareTo("?") == 0){

        }

    }*/

    /**
     * @param d1 valor Dimensão 1
     * @param d2 valor Dimensão 2
     * @param d3 valor Dimensão 3
     * @param d4 valor Dimensão 4
     * @return array com indices de todos os tuples que respeitam o que se pede
     * <p>
     * <p>
     * Para cada dimensão:
     * Se valor de dimensão for indicado (!= '?' && != '*')
     * Obtem Index daquele valor na dimensão
     * Se aquele valor não existir
     * retorna um array vazio
     * Caso exista
     * Se for a primeira dimensão pesquisada
     * Adiciona array obtido ao array final
     * Se não for a primeira dimensão pesquisada
     * faz interceção dos TIDs resultados das outras pesquisas com os TIDs
     * Se não houver valores resultantes no array final
     * Devolve o array vazio
     */
    public int[] searchInstanciatedValues(String d1, String d2, String d3, String d4) {

        int[] listaFinal = new int[0];
        boolean allFlag = false;                                                        //flag que indica se todas as dimensões até lá tiveram valor '*'

        //d1

        //subcube queries
        if (d1.compareTo("*") != 0 && d1.compareTo("?") != 0) {
            int index = getIndexOfValue(this.d1, Integer.parseInt(d1));                 //obtem posição com o valor pedido
            if (index < 0)                                                              //testa se valor existir
                return new int[0];                                                      //se não existe, retorna uma listav vazia
            listaFinal = td1[index].clone();                                            //obtem array de resultado -> não faz interceção por ser a primeira pesquisa;

            if (listaFinal.length == 0)                                                 //caso tamanho seja 0, não vale a pena continuar
                return listaFinal;
        } else
            allFlag = true;


        //d2

        //subcube queries
        if (d2.compareTo("*") != 0 && d2.compareTo("?") != 0 ) {
            int index = getIndexOfValue(this.d2, Integer.parseInt(d2));                 //obtem posição com o valor pedido
            if (index < 0)                                                              //testa se valor existir
                return new int[0];                                                      //se não existe, retorna uma lista vazia

            if (allFlag) {                                                              //todas as dimensões anteriores tiveram valor '*', sendo que esta é a primeira dimensão com valor definido
                listaFinal = td2[index].clone();                                        //obtem todos os resultados
                allFlag = false;                                                        //coloca flag a falso
            } else {
                int[] listaSecundaria = this.td2[index].clone();                        //lista que guarda valores resuiltados da pesqiisa
                listaFinal = doIntersection(listaFinal, listaSecundaria);
                if (listaFinal.length == 0)                                             //caso tamanho seja 0, não vale a pena continuar
                    return listaFinal;
            }
        }

        //d3
        //subcube queries
        if (d3.compareTo("*") != 0 && d3.compareTo("?") != 0) {
            int index = getIndexOfValue(this.d3, Integer.parseInt(d3));                 //obtem posição com o valor pedido
            if (index < 0)                                                              //testa se valor existir
                return new int[0];                                                      //se não existe, retorna uma listav vazia

            if (allFlag) {                                                              //todas as dimensões anteriores tiveram valor '*', sendo que esta é a primeira dimensão com valor definido
                listaFinal = td3[index].clone();                                        //obtem todos os resultados
                allFlag = false;                                                            //coloca flag a falso

            } else {
                int[] listaSecundaria = this.td3[index].clone();                        //lista que guarda valores resuiltados da pesqiisa
                listaFinal = doIntersection(listaFinal, listaSecundaria);
                if (listaFinal.length == 0)                                             //caso tamanho seja 0, não vale a pena continuar
                    return listaFinal;
            }
        }


        //d4

        //subcube queries
        if (d4.compareTo("*") != 0 && d4.compareTo("?") != 0) {
            int index = getIndexOfValue(this.d4, Integer.parseInt(d4));                 //obtem posição com o valor pedido
            if (index < 0)                                                              //testa se valor existir
                return new int[0];                                                      //se não existe, retorna uma listav vazia
            if (allFlag) {                                                              //todas as dimensões anteriores tiveram valor '*', sendo que esta é a primeira dimensão com valor definido
                listaFinal = td2[index].clone();                                        //obtem todos os resultados
                allFlag = false;                                                        //coloca flag a falso
            } else {
                int[] listaSecundaria = this.td4[index].clone();                        //lista que guarda valores resuiltados da pesqiisa
                listaFinal = doIntersection(listaFinal, listaSecundaria);
                if (listaFinal.length == 0)                                             //caso tamanho seja 0, não vale a pena continuar
                    return listaFinal;
            }
        }


        if (allFlag) {        //caso todos os valores da query tenham tido '*', devolve todos os TIDs
            for (int[] arr : td1)                           //para cada uma dos arrays com os TIDs
            {
                int[] listaSec = new int[arr.length + listaFinal.length];                       //cria novo array secundario com tamanho da soma dos array que ja existe e o novo
                System.arraycopy(listaFinal, 0, listaSec, 0, listaFinal.length);  //copia valores que ja estavam obtidos para a nova lista
                System.arraycopy(arr, 0, listaSec, listaFinal.length, arr.length);       //adiviona novos valores da posição para a nova lista
                listaFinal = listaSec;                                                         //coloca lista final a apontar para a lista com todos os valores
            }
        }

        return listaFinal;

    }


    /**
     * @param index index value (used as ID) of the tuple/object
     * @return int[4] array with the values of each dimension
     */
    public int[] getDimensions(int index) {
        int d1, d2, d3, d4;

        d1 = this.d1[getIndexOfTID(td1, index)];
        d2 = this.d2[getIndexOfTID(td2, index)];
        d3 = this.d3[getIndexOfTID(td3, index)];
        d4 = this.d4[getIndexOfTID(td4, index)];

        if (d1 == -1)
            return null;

        return new int[]{d1, d2, d3, d4};

    }

    /**
     * @param td    Dimension matrix beig searched
     * @param index TID/index of the tuple/object to be found
     * @return Column where the TID was found or -1 if index not found
     * <p>
     * This method is used in the filtrarion process
     */
    private int getIndexOfTID(int[][] td, int index) {
        for (int i = 0; i < td.length; i++) {
            for (int n = 0; n < td[i].length; n++) {
                if (td[i][n] == index) {
                    return i;

                }
            }
        }
        return -1;
    }


    /**
     * @param listaObjetos array de objetos a listar
     *                     <p>
     *                     Lista número de valores diferentes para cada dimensão
     *                     Aloca memória estritamente necessária para criar o array que guarda todos os valores de cada dimensão
     *                     <p>
     *                     para cada um dos objetos:
     *                     Obtem index da posição do valor no array que guarda valores
     *                     Cria um novo array com tamanho + 1 do array que guardava os IDs correspondentes ao valor
     *                     Copia os valores do array velho para o novo
     *                     Adiciona o novo valor
     */
    private void criaIndiceInvertido(Objeto[] listaObjetos) {
        alocateMemory(listaObjetos);

        td1 = new int[d1.length][0];
        td2 = new int[d2.length][0];
        td3 = new int[d3.length][0];
        td4 = new int[d4.length][0];

        for (int i = 0; i < listaObjetos.length; i++) {
            //d1
            int index = getIndexOfValue(d1, listaObjetos[i].getD1());

            //verifica se contem um valor (verificação redundante)
            if (index < 0) {
                System.out.println("index < 0\ncriaIndiceInvertido");
                System.exit(-1);
            }

            //aloca memoria com lenght +1 do array do index
            int[] newArray = new int[td1[index].length + 1];
            //copia valores para lá
            System.arraycopy(td1[index], 0, newArray, 0, td1[index].length); //copia de d1 par ao novo array
            newArray[newArray.length - 1] = i;                        //coloca o novo valor no array
            td1[index] = newArray;                                  //coloca td1 a apontar para o novo array


            //d2
            index = getIndexOfValue(d2, listaObjetos[i].getD2());

            //verifica se contem um valor (verificação redundante)
            if (index < 0) {
                System.out.println("index < 0\ncriaIndiceInvertido");
                System.exit(-1);
            }

            //aloca memoria com lenght +1 do array do index
            newArray = new int[td2[index].length + 1];
            //copia valores para lá
            System.arraycopy(td2[index], 0, newArray, 0, td2[index].length); //copia de d1 par ao novo array
            newArray[newArray.length - 1] = i;                        //coloca o novo valor no array
            td2[index] = newArray;                                  //coloca td1 a apontar para o novo array


            //d3
            index = getIndexOfValue(d3, listaObjetos[i].getD3());

            //verifica se contem um valor (verificação redundante)
            if (index < 0) {
                System.out.println("index < 0\ncriaIndiceInvertido");
                System.exit(-1);
            }

            //aloca memoria com lenght +1 do array do index
            newArray = new int[td3[index].length + 1];
            //copia valores para lá
            System.arraycopy(td3[index], 0, newArray, 0, td3[index].length); //copia de d1 par ao novo array
            newArray[newArray.length - 1] = i;                        //coloca o novo valor no array
            td3[index] = newArray;                                  //coloca td1 a apontar para o novo array


            //d4
            index = getIndexOfValue(d4, listaObjetos[i].getD4());

            //verifica se contem um valor (verificação redundante)
            if (index < 0) {
                System.out.println("index < 0\ncriaIndiceInvertido");
                System.exit(-1);
            }

            //aloca memoria com lenght +1 do array do index
            newArray = new int[td4[index].length + 1];
            //copia valores para lá
            System.arraycopy(td4[index], 0, newArray, 0, td4[index].length); //copia de d1 par ao novo array
            newArray[newArray.length - 1] = i;                        //coloca o novo valor no array
            td4[index] = newArray;                                  //coloca td1 a apontar para o novo array
        }


    }

    /**
     * @param d     array of ints
     * @param value int to be checked
     * @return index of the position where d1 appears in d[] or -1 if doesn't exist
     */
    private int getIndexOfValue(int[] d, int value) {
        for (int i = 0; i < d.length; i++)
            if (d[i] == value)
                return i;
        return -1;
    }

    /**
     * @param listaObjetos Objeto's array
     *                     <p>
     *                     This method is used to alloc memory and index the different Dimensional values in the values array.
     */
    private void alocateMemory(Objeto[] listaObjetos) {

        d1 = new int[0];
        d2 = new int[0];
        d3 = new int[0];
        d4 = new int[0];

        for (Objeto obj : listaObjetos) {             //para cada um dos objetos
            boolean existe = false;                   //cria flag de existencia
            for (int v : d1) {              //para cada um dos valores que ja estao guardados
                if (v == obj.getD1())                //se ja existir
                {
                    existe = true;                    //flag colocada a true
                    break;                            //para a pesquisa
                }
            }
            if (!existe) {                              //caso nao exista
                int[] val = new int[d1.length + 1];          //cria array com tamanho +1
                System.arraycopy(d1, 0, val, 0, d1.length);   //copia de d1 par ao novo array

                d1 = val;                        //indica que valores d1 passa a ser novo array
                d1[d1.length - 1] = obj.getD1();   //coloca valor na última posição do array
            }

            //D2
            existe = false;                   //cria flag de existencia
            for (int v : d2) {              //para cada um dos valores que ja estao guardados
                if (v == obj.getD2())                //se ja existir
                {
                    existe = true;                    //flag colocada a true
                    break;                            //para a pesquisa
                }
            }
            if (!existe) {                              //caso nao exista
                int[] val = new int[d2.length + 1];          //cria array com tamanho +1

                System.arraycopy(d2, 0, val, 0, d2.length); //copia de d1 par ao novo array

                d2 = val;                        //indica que valores d1 passa a ser novo array
                d2[d2.length - 1] = obj.getD2();   //coloca valor na última posição do array
            }

            //D3
            existe = false;                   //cria flag de existencia
            for (int v : d3) {              //para cada um dos valores que ja estao guardados
                if (v == obj.getD3())                //se ja existir
                {
                    existe = true;                    //flag colocada a true
                    break;                            //para a pesquisa
                }
            }
            if (!existe) {                              //caso nao exista
                int[] val = new int[d3.length + 1];          //cria array com tamanho +1
                System.arraycopy(d3, 0, val, 0, d3.length);    //copia de d1 par ao novo array

                d3 = val;                        //indica que valores d1 passa a ser novo array
                d3[d3.length - 1] = obj.getD3();   //coloca valor na última posição do array
            }

            //D4
            existe = false;                   //cria flag de existencia
            for (int v : d4) {              //para cada um dos valores que ja estao guardados
                if (v == obj.getD4())                //se ja existir
                {
                    existe = true;                    //flag colocada a true
                    break;                            //para a pesquisa
                }
            }
            if (!existe) {                              //caso nao exista
                int[] val = new int[d4.length + 1];          //cria array com tamanho +1
                System.arraycopy(d4, 0, val, 0, d4.length);    //copia de d1 par ao novo array

                d4 = val;                        //indica que valores d1 passa a ser novo array
                d4[d4.length - 1] = obj.getD4();   //coloca valor na última posição do array
            }
        }
    }

    /**
     * @param listaFinal      int array number one
     * @param listaSecundaria int array number two
     * @return Logic intersection result of both arrays.
     * <p>
     * This method is used, during a search, to filtrate the arrays for each dimension
     */
    private int[] doIntersection(int[] listaFinal, int[] listaSecundaria) {
        //interceção
        int[] lista = new int[0];                                            //lista que guarda valores de interseção
        for (int j : listaFinal) {                                           //procura na lista final
            for (int i : listaSecundaria) {                                 //e procura na lista secundaria
                if (j == i) {                                               //caso tenham os dois valores em comum
                    int[] listaTerciaria = new int[lista.length + 1];                               //cria nova lista de ajuda com tamanho igua à de interceção mais 1 para o novo TID
                    System.arraycopy(lista, 0, listaTerciaria, 0, lista.length);       //copia valores da lista de interceção para a nova lista
                    listaTerciaria[listaTerciaria.length - 1] = j;                                    //coloca novo valor no final da nova lista
                    lista = listaTerciaria;                                                           //coloca lista de interceção a apontar para a nova lista
                }
            }
        }
        return lista;
    }


    public InvertedIndexTables subcubeQuery(String d1, String d2, String d3, String d4) {
        int[] result = searchInstanciatedValues(d1, d2, d3, d4);            //obtem IDs dos tuples que respeitam o pedido feito

        int[][] resultTuples = new int[result.length][];                    //aloca memoria para array com representções de objetos

        for (int i = 0; i < result.length; i++) {                           //para cada um dos IDs de tuples que respeita o pedido
            resultTuples[i] = getDimensions(result[i]);                     //obtem-se os seus valores e coloca-se no array de representação de objetos
        }

        return new InvertedIndexTables(resultTuples);                       //devolve uma nova tabela criada
    }
}
