package legacy;

public class ShellFragment {

    int[] values;
    int[][] idsList;


    public ShellFragment(int[] rawData) {
        allocValues(rawData);                   //completa o arrau values
        fillIDList(rawData);                    //completa a matrix 2d idsList
    }

    public ShellFragment(int[] rawData, int size) {

        allocValues(rawData);                   //completa o arrau values

        fillIDList(rawData, size);                    //completa a matrix 2d idsList
    }

    public ShellFragment(int[] rawData, int size, int i) {

        allocValues(rawData);                   //completa o arrau values

        fillIDList(rawData, size, i);                    //completa a matrix 2d idsList
    }

    /**
     * @param rawData array of ints with the raw data of some dimension.
     *                This method puts the IDs of the tuples into the id array,
     */
    private void fillIDList(int[] rawData) {

        idsList = new int[values.length][0];               //alocamemoria para os ids, para cada um dos vlroes diferentes

        for (int i = 0; i < rawData.length; i++) {             //para cada um dos valores dos dados originais

            for (int n = 0; n < values.length; n++) {          //para cada um dos valores da dimensão
                if (rawData[i] == values[n])                 //se o valor da dimensão for igual ao valor dos dados
                {
                    int[] idsListSecundario = new int[idsList[n].length + 1];
                    System.arraycopy(idsList[n], 0, idsListSecundario, 0, idsList[n].length);
                    idsListSecundario[idsListSecundario.length - 1] = i;
                    idsList[n] = idsListSecundario;
                    break;
                }
            }
        }
    }

    //poupa 50% do tempo, mas gasta memoria à parva
    private void fillIDList(int[] rawData, int size) {

        idsList = new int[values.length][0];               //alocamemoria para os ids, para cada um dos vlroes diferentes

        int[] counter = new int[values.length];
        for (int count : counter)
            count = 0;

        //System.out.println(rawData.length);
        int[][] idsListSecundario = new int[values.length][size / 5];


        for (int i = 0; i < rawData.length; i++) {             //para cada um dos valores dos dados originais

            for (int n = 0; n < values.length; n++) {          //para cada um dos valores da dimensão
                if (rawData[i] == values[n])                 //se o valor da dimensão for igual ao valor dos dados
                {
                    if (counter[n] < idsListSecundario[n].length) {
                        idsListSecundario[n][counter[n]] = i;
                        counter[n]++;
                        break;
                    } else {
                        int[] idsListTerciario = new int[idsListSecundario[n].length + 1];
                        System.arraycopy(idsListSecundario[n], 0, idsListTerciario, 0, idsListSecundario[n].length);
                        idsListTerciario[idsListTerciario.length - 1] = i;
                        idsListSecundario[n] = idsListTerciario;
                        counter[n]++;
                        break;
                    }
                }
            }

        }

        for (int i = 0; i < idsListSecundario.length; i++) {
            idsList[i] = new int[counter[i]];
            System.arraycopy(idsListSecundario[i], 0, idsList[i], 0, idsList[i].length);
        }

    }


    //poupa 50% do tempo, mas gasta memoria à parva
    private void fillIDList(int[] rawData, int size, int num) {

        idsList = new int[values.length][0];               //alocamemoria para os ids, para cada um dos vlroes diferentes

        int[] counter = new int[values.length];
        for (int count : counter)
            count = 0;

        //System.out.println(rawData.length);
        int[][] idsListSecundario;
        if (num > 9 && num < 54)
            idsListSecundario = new int[values.length][size];
        else if (num <= 9)
            idsListSecundario = new int[values.length][size / 10];
        else
            idsListSecundario = new int[values.length][size / 2];


        for (int i = 0; i < rawData.length; i++) {             //para cada um dos valores dos dados originais

            for (int n = 0; n < values.length; n++) {          //para cada um dos valores da dimensão
                if (rawData[i] == values[n])                 //se o valor da dimensão for igual ao valor dos dados
                {
                    if (counter[n] < idsListSecundario[n].length) {
                        idsListSecundario[n][counter[n]] = i;
                        counter[n]++;
                        break;
                    } else {
                        int[] idsListTerciario = new int[idsListSecundario[n].length + 1];
                        System.arraycopy(idsListSecundario[n], 0, idsListTerciario, 0, idsListSecundario[n].length);
                        idsListTerciario[idsListTerciario.length - 1] = i;
                        idsListSecundario[n] = idsListTerciario;
                        counter[n]++;
                        break;
                    }
                }
            }

        }

        for (int i = 0; i < idsListSecundario.length; i++) {
            idsList[i] = new int[counter[i]];
            System.arraycopy(idsListSecundario[i], 0, idsList[i], 0, idsList[i].length);
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
            if (values[i] == value)
                return idsList[i];

        return null;
    }

    /**
     * @param id ID of the tuple
     * @return Value of the tuple, if not foundd returns -1
     */
    public int getValueFromID(int id) {
        for (int i = 0; i < idsList.length; i++)
            for (int n = 0; n < idsList[i].length; n++)
                if (idsList[i][n] == id)
                    return values[i];
        return -1;
    }

    public int[] getValues() {
        return values.clone();
    }

    public int[] getAllTIDs() {
        int[] retornable = new int[0];

        for (int[] arr : idsList) {
            int[] secundary = new int[retornable.length + arr.length];
            System.arraycopy(retornable, 0, secundary, 0, retornable.length);
            System.arraycopy(arr, 0, secundary, retornable.length, arr.length);
            retornable = secundary;
        }

        return retornable;
    }


}
