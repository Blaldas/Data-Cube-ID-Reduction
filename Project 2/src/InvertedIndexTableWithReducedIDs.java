public class InvertedIndexTableWithReducedIDs {

    /**
     * Esta class contem 2 tipos de arrays:
     * array[] -> existe um para cada diomensão e tem como ojetivo guardar os valores das dimensões. O index de cada variavel estará relacionado com a posição da lista de tids correspondentes
     * No array a seguir descrito
     * <p>
     * Array[][][] -> Existe um para cada dimensão e tem como objetivo guardar as listas de TIDs cujos tuples/objetos contêm a variavel com o mesmo index no array cimna descrito
     * -> Considerando [][][] -> {x, y, z}:
     * -> Cada X está relacionado com as valores no array em cima, sendo que a relação é baseada no index
     * -> Cada Y, dentro de cada X, guarda um ID de um tuple/objeto que, na devida dimensão, contem a variavel X no array em cima
     * -> A dimensão Z tem como objetivo permitir a redução de espaço, eis o seu funcionamento:
     * ---> Quando o primeiro valor é adicionado, indica-se que o tamanho de Z é 1, sendo que é alocado 1 espaço onde o primeiro valor adicionado;
     * ---> Quando os valores subsequentes são adicionados é feito uma verificação, procura-se o último valor incluido no array, É preciso ter em conta o tamanho de Z.
     * ---> Se o novo valor for o incremento do último valor registado nesse matrix, então é preciso fazer mais alguma pesquisa sobre a sua coplocação:
     * ------> Caso o tamanho do último Z seja 1, então realoca-se memória de formas a que o tamanho seja 2 (uma cópia do array terá de ser feita, porém apenas é necessária aquela dimensão e colocar a apontar para a cópia)
     * ------> Caso o tamanho do último seja 2, então substitui-se o último valor pelo novo
     * ---> Se o último valor não for o incremento do último registado, então realoca-se memória para acrecentar o novo valor em X, uma cópia "2D" é necessária, uma vez que houve realocamento de memória.
     */

//contains the values
    private int[] d1;
    private int[] d2;
    private int[] d3;
    private int[] d4;

    //contains the IDs
    private int[][][] td1;
    private int[][][] td2;
    private int[][][] td3;
    private int[][][] td4;

    public InvertedIndexTableWithReducedIDs(Objeto[] listaObjetos) {
        criaIndiceInvertido(listaObjetos);
    }

    private void criaIndiceInvertido(Objeto[] listaObjetos) {
        alocateMemory(listaObjetos);                            //aloca memória e cria as tabela de valores d1/d2/d3/d4

        td1 = new int[d1.length][0][0];
        td2 = new int[d2.length][0][0];
        td3 = new int[d3.length][0][0];
        td4 = new int[d4.length][0][0];

        for (int i = 0; i < listaObjetos.length; i++) {
            adicionaIndexATabela(listaObjetos, i, td1, d1, listaObjetos[i].getD1());
            adicionaIndexATabela(listaObjetos, i, td2, d2, listaObjetos[i].getD2());
            adicionaIndexATabela(listaObjetos, i, td3, d3, listaObjetos[i].getD3());
            adicionaIndexATabela(listaObjetos, i, td4, d4, listaObjetos[i].getD4());
        }
    }


    private int getIndexOfValue(int[] d, int value) {
        for (int i = 0; i < d.length; i++)
            if (d[i] == value)
                return i;
        return -1;
    }

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


    private void adicionaIndexATabela(Objeto[] listaObjetos, int novoValor, int[][][] td, int[] d, int valueD) {
        int index = getIndexOfValue(d, valueD);                                           //obtem X do objeto
        if (index < 0) {                                                                                    //verifica se contem um valor (verificação redundante)
            System.out.println("index < 0\ncriaIndiceInvertido");
            System.exit(-1);
        }


        //caso seja o primeiro
        if (td[index].length == 0) {                                                              //caso não tenha nenhum na 2a dimensão
            td[index] = new int[1][1];                                                          //aloca as dimensões para o valor
            td[index][0][0] = novoValor;                                                                //coloca lá o valor
        } else {                                                                                   //caso tenha lá coisas
            //obtem o último valor
            int last = td[index][td[index].length - 1][td[index][td[index].length - 1].length - 1];       //obtem valor da última posição

            //caso sejam seguidos
            if (novoValor - last == 1) {                                                                        //caso sejam valores seguidos
                if (td[index][td[index].length - 1].length == 1) {                                      //caso tenha tamanho 1
                    int[] newArray = new int[2];                                                        //aloca array com tamanho 2
                    newArray[0] = td[index][td[index].length - 1][0];                                   //copia 1 valor para o array
                    newArray[1] = novoValor;                                                                    //coloca novo valor no array
                    td[index][td[index].length - 1] = newArray;                                         //mete array antigo a apontar para o novo array
                } else {                                                                                   //caso tenha 2 valores
                    td[index][td[index].length - 1][1] = novoValor;                                             //coloca novo valor n array, sustituindo array antigo
                }
                //cas0 não sejam valores seguidos
            } else {                                                                                       //caso não tenha sejam seguidos
                int[][] newArray = new int[td[index].length + 1][];                                          //aloca memória para array secundário
                for (int i = 0; i < td[index].length - 1; i++)                                               //para cada uma das segundas dimensões do novo array
                    newArray[i] = new int[td[index][i].length];                                          //aloca o númer de terceiras dimensões necessário
                System.arraycopy(td[index], 0, newArray, 0, td[index].length); //copia para a segunda dimensão do novo array a 3a do array antigo
                newArray[newArray.length - 1] = new int[1];                                            //aloca memória para nova posição do array (ultima posição)
                newArray[newArray.length - 1][0] = novoValor;                                           //coloca novo valor na última posição do array
                td[index] = newArray;                                                                  //coloca a apontar para o novo array

            }
        }


    }


    public void showTable() {
        System.out.println("\n\nDIMENSION 1:");
        showFullDimension(td1, d1);
        System.out.println("\n\nDIMENSION 2:");
        showFullDimension(td2, d2);
        System.out.println("\n\nDIMENSION 3:");
        showFullDimension(td3, d3);
        System.out.println("\n\nDIMENSION 4:");
        showFullDimension(td4, d4);

    }

    private void showFullDimension(int[][][] td, int[] d) {
        int countSaved = 0, countUsed = 0;
        for (int i = 0; i < td.length; i++) {
            StringBuilder str = new StringBuilder();

            for (int n = 0; n < td[i].length; n++) {
                if (td[i][n].length == 1)
                    str.append(td[i][n][0]).append(" ");
                else
                {
                    str.append("{ ").append(td[i][n][0]).append(" até ").append(td[i][n][1]).append(" } ");
                    countSaved++;
                    countUsed++;
                }

                countUsed++;
            }
            System.out.println("\nValor\t" + d[i]);
            System.out.println("TIDs\t" + str);
        }
        System.out.println("Memória poupada nesta dimensão: " + 4 * countSaved + " bytes");
        System.out.println("Memória usada nesta dimensão: " + 4 * countUsed + " bytes");
    }
}
