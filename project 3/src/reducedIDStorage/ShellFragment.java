package reducedIDStorage;

public class ShellFragment {

    int[][][] matrix;
    int lower;
    int upper;

    ShellFragment(int[] rawData, int lower, int upper) {
        this.lower = lower;
        this.upper = upper;

        matrix = new int[upper - lower + 1][0][0];

        fillMatrix(rawData);

    }


    ShellFragment(int[] rawData, int lower, int upper, int size) {
        this.lower = lower;
        this.upper = upper;

        matrix = new int[upper - lower + 1][0][0];

        fillMatrix(rawData, size);

    }

    ShellFragment(int[] rawData, int lower, int upper, int size, int num) {
        this.lower = lower;
        this.upper = upper;

        matrix = new int[upper - lower + 1][0][0];
        fillMatrix(rawData, size, num);
    }

    private void fillMatrix(int[] rawData) {
        for (int i = 0; i < rawData.length; i++) {                             //para cada uma dos tuples
            if (matrix[rawData[i] - lower].length == 0) {                               //se for a primeira tuple com esse valor
                matrix[rawData[i] - lower] = new int[1][1];                                     //aloca 1 espaço
                matrix[rawData[i] - lower][0][0] = i;                                           //coloca o indice no espaço indicado
            } else if (i - getLastValue(rawData[i]) == 1) {                                //caso seja um incremento
                if (matrix[rawData[i] - lower][matrix[rawData[i] - lower].length - 1].length == 1) {
                    int[] secundary = new int[2];
                    secundary[0] = matrix[rawData[i] - lower][matrix[rawData[i] - lower].length - 1][0];
                    secundary[1] = i;
                    matrix[rawData[i] - lower][matrix[rawData[i] - lower].length - 1] = secundary;
                } else
                    matrix[rawData[i] - lower][matrix[rawData[i] - lower].length - 1][1] = i;
            } else {
                int[][] secundary = new int[matrix[rawData[i] - lower].length + 1][0];                                                   //cria array secundário
                for (int n = 0; n < matrix[rawData[i] - lower].length; n++)                                                          //para cada posição da linha
                {
                    secundary[n] = new int[matrix[rawData[i] - lower][n].length];                                                               //aloca a devido tamanho no secundario
                    System.arraycopy(matrix[rawData[i] - lower][n], 0, secundary[n], 0, matrix[rawData[i] - lower][n].length);      //passa os valores do secundário
                }
                secundary[secundary.length - 1] = new int[1];                 //aloca para ultimo valor
                secundary[secundary.length - 1][0] = i;                       //coloca ultimo valor na ultima posição
                matrix[rawData[i] - lower] = secundary;                     //coloca posição na matrix a apontar par ao novo array
            }
        }
    }

    //returns the last value stored for value i
    private int getLastValue(int val) {
        return matrix[val - lower][matrix[val - lower].length - 1][matrix[val - lower][matrix[val - lower].length - 1].length - 1];
    }

    private void fillMatrix(int[] rawData, int size) {
        int[] counter = new int[matrix.length];                                 //cria array counter
        int[][][] secundary = new int[matrix.length][][];                      //cria array secundário com  numero de valores
        for (int i = 0; i < secundary.length; i++)                              //para cada linha
            secundary[i] = new int[size / 10][1];                                //aloca size/x innicialmente

        for (int i = 0; i < rawData.length; i++) {                                  //para cada uma dos tuples
            if (counter[rawData[i] - lower] < secundary[rawData[i] - lower].length) {          //caso ainda haja espaço no buffer inicial
                if (counter[rawData[i] - lower] == 0) {                                                    ///caso seja o primeiro
                    secundary[rawData[i] - lower][counter[rawData[i] - lower]][0] = i;                      //coloca indice na posição
                    counter[rawData[i] - lower]++;                                                          //aumenta o counter
                } else if (i - getLastValue(secundary, rawData[i], counter[rawData[i] - lower] - 1) == 1) {      //caso seja incremento
                    if (secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1].length == 1) {               //caso ultimo tem tamanho 1
                        int[] terciary = new int[2];                                                               //aloca array secundario com tam 2
                        terciary[0] = secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1][0];               //para valor para array secundario
                        terciary[1] = i;                                                                            //adiona valor no array secundário
                        secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1] = terciary;                      //coloca a apontar para array secundario
                    } else {                                                                      //caso já tenha tamanho 2
                        secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1][1] = i;                  //adiciona valor na devida posição
                    }
                } else {                                                                          //caso nao seja incremento
                    secundary[rawData[i] - lower][counter[rawData[i] - lower]][0] = i;                          //adiciona valor na devida posição
                    counter[rawData[i] - lower]++;
                }
            } else {
                if (i - getLastValue(secundary, rawData[i], counter[rawData[i] - lower] - 1) == 1) {               //caso seja incremto
                    if (secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1].length == 1) {                       //caso tenha tamanho 1
                        int[] terciary = new int[2];
                        terciary[0] = secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1][0];               //para valor para array secundario
                        terciary[1] = i;                                                                            //adiona valor no array secundário
                        secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1] = terciary;                      //coloca a apontar para array secundario
                    } else {
                        secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1][1] = i;
                    }

                } else {
                    int[][] terciary = new int[secundary[rawData[i] - lower].length + 1][];             //aloca matrix secundaria para aquela linha
                    for (int n = 0; n < secundary[rawData[i] - lower].length; n++) {                    //para cada uma das linhas
                        terciary[n] = new int[secundary[rawData[i] - lower][n].length];                     //aloca memoria da anterior mais 1
                        System.arraycopy(secundary[rawData[i] - lower][n], 0, terciary[n], 0, secundary[rawData[i] - lower][n].length);
                    }
                    terciary[terciary.length - 1] = new int[1];
                    terciary[terciary.length - 1][0] = i;
                    secundary[rawData[i] - lower] = terciary;
                    counter[rawData[i] - lower]++;
                }
            }
        }

        matrix = new int[secundary.length][][];
        for (int i = 0; i < secundary.length; i++) {
            matrix[i] = new int[counter[i]][];
            for (int n = 0; n < counter[i]; n++) {
                matrix[i][n] = new int[secundary[i][n].length];
                System.arraycopy(secundary[i][n], 0, matrix[i][n], 0, secundary[i][n].length);
            }
        }


    }

    private void fillMatrix(int[] rawData, int size, int num) {


        int[] counter = new int[matrix.length];                                 //cria array counter
        int[][][] secundary = new int[matrix.length][][];                      //cria array secundário com  numero de valores
        for (int i = 0; i < secundary.length; i++)
            if (num > 9 && num < 54)
                secundary[i] = new int[size][];
            else if (num <= 9)
                secundary[i] = new int[size / 8][];
            else
                secundary[i] = new int[size / 2][];


        for (int i = 0; i < rawData.length; i++) {                                  //para cada uma dos tuples
            if (counter[rawData[i] - lower] < secundary[rawData[i] - lower].length) {          //caso ainda haja espaço no buffer inicial
                if (counter[rawData[i] - lower] == 0) {                                                    ///caso seja o primeiro
                    secundary[rawData[i] - lower][counter[rawData[i] - lower]] = new int[1];
                    secundary[rawData[i] - lower][counter[rawData[i] - lower]][0] = i;                      //coloca indice na posição
                    counter[rawData[i] - lower]++;                                                          //aumenta o counter
                } else if (i - getLastValue(secundary, rawData[i], counter[rawData[i] - lower] - 1) == 1) {      //caso seja incremento
                    if (secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1].length == 1) {               //caso ultimo tem tamanho 1
                        int[] terciary = new int[2];                                                               //aloca array secundario com tam 2
                        terciary[0] = secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1][0];               //para valor para array secundario
                        terciary[1] = i;                                                                            //adiona valor no array secundário
                        secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1] = terciary;                      //coloca a apontar para array secundario
                    } else {                                                                      //caso já tenha tamanho 2
                        secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1][1] = i;                  //adiciona valor na devida posição
                    }
                } else {                                                                          //caso nao seja incremento
                    secundary[rawData[i] - lower][counter[rawData[i] - lower]] = new int[1];
                    secundary[rawData[i] - lower][counter[rawData[i] - lower]][0] = i;                          //adiciona valor na devida posição
                    counter[rawData[i] - lower]++;
                }
            } else {        //caso o buffer tiver acabado
                if (i - getLastValue(secundary, rawData[i], counter[rawData[i] - lower] - 1) == 1) {               //caso seja incremto
                    if (secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1].length == 1) {                       //caso tenha tamanho 1
                        int[] terciary = new int[2];
                        terciary[0] = secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1][0];               //para valor para array secundario
                        terciary[1] = i;                                                                            //adiona valor no array secundário
                        secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1] = terciary;                      //coloca a apontar para array secundario
                    } else {                                                                                                //caso ja tenha tamanho 2
                        secundary[rawData[i] - lower][counter[rawData[i] - lower] - 1][1] = i;
                    }
                } else {                                                                                                    //caso nao seja incremento
                    int[][] terciary = new int[secundary[rawData[i] - lower].length + 1][];             //aloca matrix secundaria para aquela linha
                    for (int n = 0; n < secundary[rawData[i] - lower].length; n++) {                    //para cada uma das linhas
                        terciary[n] = new int[secundary[rawData[i] - lower][n].length];                     //aloca memoria da anterior mais 1
                        System.arraycopy(secundary[rawData[i] - lower][n], 0, terciary[n], 0, secundary[rawData[i] - lower][n].length);
                    }
                    terciary[terciary.length - 1] = new int[1];
                    terciary[terciary.length - 1][0] = i;
                    secundary[rawData[i] - lower] = terciary;
                    counter[rawData[i] - lower]++;
                }
            }
        }

        for (int i = 0; i < secundary.length; i++) {
            matrix[i] = new int[counter[i]][];
            for (int n = 0; n < counter[i]; n++) {
                matrix[i][n] = new int[secundary[i][n].length];
                System.arraycopy(secundary[i][n], 0, matrix[i][n], 0, secundary[i][n].length);
            }
        }


    }

    private int getLastValue(int[][][] m, int val, int i) {
        return m[val - lower][i][m[val - lower][i].length - 1];
    }

    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0. Care that the array of a found value may be zero as well, so it's not a flag
     */
    public int[] getTidsListFromValue(int value) {
        if (value > upper || value < lower)
            return new int[0];

        int[] secundary = new int[matrix[value - lower].length * 2];

        int counter = 0;

        for (int i = 0; i < matrix[value - lower].length; i++) {                    //para cada um dos ids do valor pedido
            if (matrix[value - lower][i].length == 1) {                                 //caso tenha length 1
                if (counter < secundary.length) {                                              //caso esteja no buffer inicial
                    secundary[counter] = matrix[value - lower][i][0];                          //adiona o valor ao array
                    counter++;                                                                  //aumenta o contador
                } else {                                                                //caso o buffer inicial este cheio
                    int[] terciary = new int[secundary.length + 1];                               //aloca memoria para um novo array com tamanho +1
                    System.arraycopy(secundary, 0, terciary, 0, secundary.length);   //copia valores para o novo array
                    terciary[terciary.length - 1] = matrix[value - lower][i][0];                   //adiciona novo valor ao array
                    secundary = terciary;                                                          //coloca array velho a apontar para o novo
                    counter++;                                                                       //aumenta o contador
                }
            } else {                                                                //caso tenha length 2
                for (int n = matrix[value - lower][i][0]; n <= matrix[value - lower][i][1]; n++) { //para cada um dos valores que se encontram no intervalor
                    if (counter < secundary.length) {                                                       //caso esteja no buffer inicial
                        secundary[counter] = n;                                                             //adiciona valor ao array
                        counter++;                                                                           //aumenta o contador
                    } else {                                                                        //caso o buffer inicial este cheio
                        int[] terciary = new int[secundary.length + 1];                                   //cria array secundario com tamanho +1
                        System.arraycopy(secundary, 0, terciary, 0, secundary.length);       //copia valores para o array secundário
                        terciary[terciary.length - 1] = n;                                                  //coloca valor no novo array
                        secundary = terciary;
                        counter++;
                    }
                }
            }
        }

        int[] returnable = new int[counter];

        System.arraycopy(secundary, 0, returnable, 0, counter);
        return returnable;
    }

    /**
     * @param tid id of the tuple to be seached
     * @return the value of such tuple, or lower-1 if not found.
     */
    public int getValueFromTid(int tid) {
        for (int i = 0; i < matrix.length; i++) {                   //para cada uma das linhas
            for (int[] v : matrix[i]) {                                 //para cada coluna das linhas
                if (v[0] == tid)                                            //se tiver o id pretendiso
                    return lower + i;                                           //devolve logo o valor
                else if (v.length == 2 && v[0] >= tid && v[1] <= tid)       //se tiver tamanho 2 e o id estiver entre os valores
                    return lower + i;                                           //devolve logo o valor
                else if (v[0] > tid)                                        //se ids forem superiores - eficiencia
                    break;                                                  //faz break;
            }
        }
        return lower - 1;
    }

    /**
     * @return all the values being stored
     */
    public int[] getAllValues() {
        int[] returnable = new int[matrix.length];

        for (int i = 0; i < returnable.length; i++)
            returnable[i] = lower + i;

        return returnable;
    }


    public int[] getAllTids() {
        int b = getBiggestTid();
        int[] returnable = new int[b + 1];

        for (int i = 0; i < returnable.length; i++)
            returnable[i] = i;

        return returnable;
    }

    public int getBiggestTid() {
        int max = -1;

        for (int[][] d : matrix)
            if (d.length == 0)
                continue;
            else if (max < d[d.length - 1][d[d.length - 1].length - 1])
                max = d[d.length - 1][d[d.length - 1].length - 1];

        return max;
    }


}
