public class ShellFragmentsWithIDReduction {

    int[] values;
    int[][][] idsList;


    public ShellFragmentsWithIDReduction(int[] rawData) {
        allocValues(rawData);                   //completa o arrau values

        fillIDList(rawData);                    //completa a matrix 2d idsList
    }

    public ShellFragmentsWithIDReduction(int[] rawData, int size) {
        allocValues(rawData);                   //completa o arrau values

        fillIDList(rawData, size);                    //completa a matrix 2d idsList
    }

    /**
     * @param rawData array of ints with the raw data of some dimension.
     *                This method puts the IDs of the tuples into the id array,
     */
    private void fillIDList(int[] rawData) {

        idsList = new int[values.length][0][0];               //alocamemoria para os ids, para cada um dos vlroes diferentes

        for (int i = 0; i < rawData.length; i++) {
            for (int n = 0; n < values.length; n++)
                if (values[n] == rawData[i]) {
                    if (idsList[n].length == 0) {          //primeiro caso
                        idsList[n] = new int[1][1];
                        idsList[n][0][0] = rawData[i];
                    } else if (rawData[i] - getLastValue(n) == 1) {             //acrescimo
                        if (idsList[n][idsList[n].length - 1].length == 1) {                    //tem tamanho 1
                            int[] arrSec = new int[2];
                            arrSec[0] = idsList[n][idsList[n].length - 1][0];
                            arrSec[1] = rawData[i];
                            idsList[n][idsList[n].length - 1] = arrSec;
                        } else {                                                                //tem tamanho 2
                            idsList[n][idsList[n].length - 1][1] = rawData[i];
                        }
                    } else {                       // não é primeiro porém tambem não é acrescimo
                        int[][] arrSecundario = new int[idsList[n].length][0];

                        for (int j = 0; j < idsList[n].length; j++) {
                            arrSecundario[j] = new int[idsList[n][j].length];
                            System.arraycopy(idsList[n][j], 0, arrSecundario[j], 0, idsList[n][j].length);
                        }
                        arrSecundario[arrSecundario.length - 1] = new int[1];
                        arrSecundario[arrSecundario.length - 1][0] = rawData[i];
                        idsList[n] = arrSecundario;
                    }

                    break;
                }
        }
    }

    /**
     * @param index - index of the line where to search the last value in idsList
     * @return the last valur stored in the indicated line of the idsList matrix
     */
    private int getLastValue(int index) {
        return idsList[index][idsList[index].length - 1][idsList[index][idsList[index].length - 1].length - 1];         //points to the last 1/2 positions
    }

    /**
     * @param index - index of the line where to search the last value in idsList
     * @return the last valur stored in the indicated line of the idsList matrix
     */
    private int getLastValue(int[][][] arr, int index) {

        return arr[index][arr[index].length - 1][arr[index][arr[index].length - 1].length - 1];         //points to the last 1/2 positions
    }

    //poupa 50% do tempo, mas gasta memoria à parva
    private void fillIDList(int[] rawData, int size) {

        int[] counter = new int[values.length];
        for (int count : counter)
            count = 0;

        //System.out.println(rawData.length);
        int[][][] idsListSecundario = new int[values.length][size / 2][0];

        for (int i = 0; i < rawData.length; i++) {
            for (int n = 0; n < values.length; n++)
                if (values[n] == rawData[i]) {
                    if (counter[n] == 0)                                 //o primeiro
                    {
                        idsListSecundario[n][0] = new int[1];
                        idsListSecundario[n][0][0] = rawData[i];
                        counter[n]++;
                    } else if (rawData[i] - getLastValue(idsListSecundario, n) == 1) {         //incremento
                        if (idsListSecundario[n][counter[n] - 1].length == 1) {                     //tem tamanho 1
                            int[] sec = new int[2];
                            sec[0] = idsListSecundario[n][counter[n] - 1][0];
                            sec[1] = rawData[i];
                            idsListSecundario[n][counter[n] - 1] = sec;
                        } else                                                                   //tem tamanho 2
                            idsListSecundario[n][counter[n] - 1][1] = rawData[i];
                    } else                                                                   //não é incremento
                    {
                        idsListSecundario[n][counter[n]] = new int[1];
                        idsListSecundario[n][counter[n]][0] = rawData[i];
                        counter[n]++;
                    }

                    break;
                }
        }

        idsList = new int[values.length][0][0];               //alocamemoria para os ids, para cada um dos vlroes diferentes

        for (int i = 0; i < idsListSecundario.length; i++) {            //para cada linha
            idsList[i] = new int[idsListSecundario[i].length][0];       //aloca memoria para casda linha
            for (int n = 0; n < idsListSecundario[i].length; n++)        //para cada copluna
            {
                idsList[i][n] = new int[idsListSecundario[i][n].length];
                System.arraycopy(idsListSecundario[i][n], 0, idsList[i][n], 0, idsListSecundario[i][n].length);
            }
        }

    }

    /**
     * @param rawData int array with the raw data of some dimension.
     *                This method Allocs memory to the values array and puts the different values in there
     */
    private void allocValues(int[] rawData) {
        values = new int[0];
        for (int value : rawData) {                 //para cada um dos valores recibidos
            boolean existe = false;                   //cria flag de existencia
            for (int v : values) {              //para cada um dos valores que ja estao guardados
                if (v == value)                //se ja existir
                {
                    existe = true;                    //flag colocada a true
                    break;                            //para a pesquisa
                }
            }
            if (!existe) {                              //caso nao exista
                int[] val = new int[values.length + 1];          //cria array com tamanho +1
                System.arraycopy(values, 0, val, 0, values.length);   //copia de d1 par ao novo array

                values = val;                        //indica que valores d1 passa a ser novo array
                values[values.length - 1] = value;   //coloca valor na última posição do array
            }
        }
    }


    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found, return null
     */
    public int[] getTIDListFromValue(int value) {
        for (int i = 0; i < values.length; i++)
            if (values[i] == value) {
                int[] retornable = new int[idsList[i].length];
                int counter = 0;

                for (int n = 0; n < idsList[i].length; n++) {

                    if (idsList[i][n].length == 1) {
                        if (counter < retornable.length) {
                            retornable[counter] = idsList[i][n][0];
                        } else {
                            int[] secundary = new int[counter + 1];
                            System.arraycopy(retornable, 0, secundary, 0, retornable.length);
                            secundary[counter] = idsList[i][n][0];
                            retornable = secundary;
                        }
                        counter++;
                        System.gc();
                    } else {
                        for (int j = 0; j < (idsList[i][n][1] - idsList[i][n][0]); j++) {

                            if (counter < retornable.length) {
                                retornable[counter] = idsList[i][n][0] + j;
                            } else {
                                int[] secundary = new int[counter + 1];
                                System.arraycopy(retornable, 0, secundary, 0, retornable.length);
                                secundary[counter] = idsList[i][n][0] + j;
                                retornable = secundary;
                            }
                            counter++;
                            System.gc();
                        }
                    }

                }

                return retornable;
            }

        return null;
    }

    /**
     * @param id ID of the tuple
     * @return Value of the tuple, if not foundd returns -1
     */
    public int getValueFromID(int id) {
        for (int i = 0; i < idsList.length; i++)
            for (int n = 0; n < idsList[i].length; n++) {
                if (idsList[i][n].length == 1) {
                    if (idsList[i][n][0] == id)
                        return values[i];
                } else {
                    if (idsList[i][n][0] <= id && idsList[i][n][1] >= id)
                        return values[i];
                }
            }
        return -1;
    }

    public int[] getValues() {
        return values.clone();
    }

    public int[] getAllTIDs() {
        int[] retornable = new int[0];

        for (int value : values) {
            int[] secondary = new int[retornable.length + getTIDListFromValue(value).length];
            System.arraycopy(retornable, 0, secondary, 0, retornable.length);
            System.arraycopy(getTIDListFromValue(value), 0, secondary, retornable.length, getTIDListFromValue(value).length);
            retornable = secondary;
        }

        return retornable;
    }


}
