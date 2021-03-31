package idReducted;

import javax.swing.plaf.IconUIResource;
import java.util.ArrayList;

public class ShellFragmentsWithIDReduction {

    int[] values;
    int[][][] idsList;


    public ShellFragmentsWithIDReduction(int[] rawData) {
        allocValues(rawData);                   //completa o arrau values

        fillIDList(rawData);                    //completa a matrix 2d idsList
    }

    public ShellFragmentsWithIDReduction(int[] rawData, int size) {
        System.out.println("1");
        allocValues(rawData);                   //completa o arrau values
        System.out.println("2");

        fillIDList(rawData, size);                    //completa a matrix 2d idsList
        System.out.println("3");

    }

    /**
     * @param rawData array of ints with the raw data of some dimension.
     *                This method puts the IDs of the tuples into the id array,
     */
    private void fillIDList(int[] rawData) {

        idsList = new int[values.length][0][0];               //alocamemoria para os ids, para cada um dos vlroes diferentes

        for (int i = 0; i < rawData.length; i++) {              //para cada um dos valores da dimensão
            for (int n = 0; n < values.length; n++)                     //para cada uma das dinesões
                if (values[n] == rawData[i]) {                              //se valor do tuple for igual ao vaor da dimensão
                    if (idsList[n].length == 0) {          //primeiro caso
                        idsList[n] = new int[1][1];
                        idsList[n][0][0] = i;
                    } else if (i - getLastValue(n) == 1) {             //acrescimo
                        if (idsList[n][idsList[n].length - 1].length == 1) {                    //tem tamanho 1
                            int[] arrSec = new int[2];
                            arrSec[0] = idsList[n][idsList[n].length - 1][0];
                            arrSec[1] = i;
                            idsList[n][idsList[n].length - 1] = arrSec;
                        } else {                                                                //tem tamanho 2
                            idsList[n][idsList[n].length - 1][1] = i;
                        }
                    } else {                       // não é primeiro porém tambem não é acrescimo
                        int[][] arrSecundario = new int[idsList[n].length + 1][0];      //aloca matrix secundaria com tamanho + 1

                        for (int j = 0; j < idsList[n].length; j++) {
                            arrSecundario[j] = new int[idsList[n][j].length];
                            System.arraycopy(idsList[n][j], 0, arrSecundario[j], 0, idsList[n][j].length);
                        }
                        arrSecundario[arrSecundario.length - 1] = new int[1];
                        arrSecundario[arrSecundario.length - 1][0] = i;
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
    private int getLastValue(int[][][] arr, int index, int[] counter) {

        return arr[counter[index] - 1][arr[counter[index]- 1].length - 1][arr[counter[index]- 1][arr[counter[index]- 1].length - 1].length - 1];         //points to the last 1/2 positions
    }

    //poupa 50% do tempo, mas gasta memoria à parva
    private void fillIDList(int[] rawData, int size) {

        int[] counter = new int[values.length];
        for (int count : counter)
            count = 0;

        //System.out.println(rawData.length);
        int[][][] idsListSecundario = new int[values.length][size / 2][0];

        for (int i = 0; i < rawData.length; i++) {                              //para cada um dos tuples ()
            for (int n = 0; n < values.length; n++)                                 //para cada uma das dimensoes
                if (values[n] == rawData[i])                                            //Se valor da diemnsão for o mesmo que o valor do tuple a ser avaliado
                    if (counter[n] == 0)                                                    //caso seja o primeiro
                    {
                        idsListSecundario[n][0] = new int[1];                                   //alloca 1 de memoria
                        idsListSecundario[n][0][0] = rawData[i];                                //adiciona o valor
                        counter[n]++;                                                            //aumenta o contador
                    } else if (counter[n] < idsListSecundario[n].length) {              //caso ainda esteja no alocado incialmente
                        if (rawData[i] - getLastValue(idsListSecundario, n, counter) == 1) {         //incremento
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
                    } else {
                        if (rawData[i] - getLastValue(idsListSecundario, n, counter) == 1) {         //incremento
                            if (idsListSecundario[n][counter[n] - 1].length == 1) {                     //tem tamanho 1
                                int[] sec = new int[2];
                                sec[0] = idsListSecundario[n][counter[n] - 1][0];
                                sec[1] = rawData[i];
                                idsListSecundario[n][counter[n] - 1] = sec;
                            } else                                                                   //tem tamanho 2
                                idsListSecundario[n][counter[n] - 1][1] = rawData[i];
                        } else {
                            int[][] terc = new int[counter[n] + 1][0];
                            for (int j = 0; i < idsListSecundario[n].length; j++) {       //alloca e copia dados do array velhjjo para o novo
                                terc[j] = new int[idsListSecundario[n][j].length];
                                System.arraycopy(idsListSecundario[n][j], 0, terc[j], 0, idsListSecundario[n][j].length);
                            }
                            terc[counter[n]] = new int[1];
                            terc[counter[n]][0] = i;
                            idsListSecundario[n] = terc;
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
        for (int i = 0; i < values.length; i++)                         //para cada uma das dimensões
            if (values[i] == value) {
                int[] retornable = new int[getNumberTIDsFromIndex(i)];
                int counter = 0;

                for (int[] n : idsList[i]) {
                    if (n.length == 1) {
                        retornable[counter] = n[0];
                        counter++;
                    } else {
                        for (int j = n[0]; j <= n[1]; j++) {
                            retornable[counter] = j;
                            counter++;
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

    /**
     * @return returns list with all the ids of the
     */
    public int[] getAllTIDs() {
/*
        int size = 0;
        int count =0;

        for(int i=0; i < values.length; i++)
            size += getNumberTIDsFromIndex(i);
        
        int[] retornable = new int[size];

        for (int value : values) {
            int[] valueList = getTIDListFromValue(value);
            System.arraycopy(valueList, 0, retornable, count, valueList.length);
            count += valueList.length;
        }
*/
        int[] retornable = new int[getBigestID() + 1];

        for (int i = 0; i < retornable.length; i++)
            retornable[i] = i;

        return retornable;
    }


    /**
     * @param index position to be looked for on the idsList
     * @return number of ids represented in that lne
     * <p>
     * This method can be used to know how many spaces to alloc in a lot of scenerarios.+
     */
    private int getNumberTIDsFromIndex(int index) {
        int count = 0;

        for (int[] i : idsList[index])
            if (i.length == 1)
                count++;
            else
                count += (i[1] - i[0]) + 1;

        return count;

    }


    public void Teste() {
        for (int i = 0; i < values.length; i++) {
            StringBuilder str = new StringBuilder();
            str.append("valor\t").append(values[i]).append("\nids\t");
            for (int n = 0; n < idsList[i].length; n++) {
                if (idsList[i][n].length == 1) {
                    str.append(idsList[i][n][0]).append(" ");
                } else {
                    str.append("{ ").append(idsList[i][n][0]).append(" ; ").append(idsList[i][n][1]).append(" } ");
                }
            }
            System.out.println(str);
        }
    }


    /**
     * @return biggest ID in the list
     */
    public int getBigestID() {
        int max = 0;

        for (int[][] ints : idsList) {
            if (max < ints[ints.length - 1][ints[ints.length - 1].length - 1]) {
                max = ints[ints.length - 1][ints[ints.length - 1].length - 1];
            }
        }
        return max;
    }

    public int[][] getTIDListFromValueWithIntervals(int value) {

        for (int n = 0; n < values.length; n++)
            if (value == values[n])
                return idsList[n];
        return new int[0][0];
    }
}
