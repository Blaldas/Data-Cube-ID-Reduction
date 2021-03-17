import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Scanner;


public class Main {


    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String path = "Objectos";
        //int number = 25000;
        Date startDate = new Date(), endDate;


        //createAndWriteRandomObjetosList(path, number);
        //isto é parvo, mas é o que vem à cabeça
        int[] sizes = getSizes(path); // guarda valores dos tamanhos-> size[0] -> numero de tuples, size[1...lenght] cardinalidade de cada tuple
        if (sizes == null)
            return;

        int[][] array = readFromDisk(path);
        if (array == null)
            return;


        DataCube dataCube = new DataCube(array, sizes);
        endDate = new Date();
        int numSeconds = (int) ((endDate.getTime() - startDate.getTime()) / 1000);
        System.out.println(numSeconds + " seconds were used to load the data");
        System.out.println(sizes[0] + " tuples read");
        System.out.println(sizes.length - 1 + " dimensions loaded");

        System.out.println(dataCube.getNumberShellFragments() + " Shell Fragments constructed");

        printIntArray(dataCube.shellFragmentsList[0].values);
        System.out.println("num: " + dataCube.shellFragmentsList[0].values.length);

        int opt = 0;
        do {
            System.out.println("1 - Fazer pesquisa de point querie");
            System.out.println("2 - Fazer pesquisa de subcube querie");
            System.out.println("3 - Mostrar cubo de dados");
            System.out.println("9 - Sair do programa");

            opt = sc.nextInt();
            switch (opt) {
                case 1:
                    fazPesquisaPointQuery(sc, dataCube);
                    break;
                case 2:
                    fazPesquisaSubCube(sc, dataCube);
                    break;
                case 3:
                    System.out.println(dataCube.showDimensions());
            }

        } while (opt != 9);

    }

    private static int[] getSizes(String filePath) {
        Path path = Path.of(filePath);
        try {
            String line = null;                                         //the information will be read here
            String[] values;
            //int [] sizes;       //size[0] -> num of tuple //else num of diferent values

            InputStream in = Files.newInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            //first read -> reads the number of objects
            line = reader.readLine();

            values = line.split(" ");
            int[] sizes = new int[values.length];

            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = Integer.parseInt(values[i]);
            }
            reader.close();
            in.close();
            return sizes;
        } catch (Exception e) {             //in case there is any eception
            e.printStackTrace();
            return null;
        }

    }


    private static void printIntArray(int[] array) {
        if (array.length == 0)
            System.out.println("Não foram encontrados resultados");
        else {
            StringBuilder str = new StringBuilder();
            for (int i : array)
                str.append(i).append(" ");
            System.out.println("TIDs\t" + str);
        }
    }


    private static void fazPesquisaSubCube(Scanner sc, DataCube dataCube) {
        int[] arrayOfValues = new int[0];
        String dValue;
        int dimCounter = 1;

        do {
            System.out.println("Indique valor para dimensão número " + dimCounter + " : valor númerico, '*' ou '?', ou favor não númerico para sair");
            dValue = sc.next();
            //dValue += sc.nextLine();

            int[] secundaryArray = new int[arrayOfValues.length + 1];
            System.arraycopy(arrayOfValues, 0, secundaryArray, 0, arrayOfValues.length);
            try {
                secundaryArray[secundaryArray.length - 1] = Integer.parseInt(dValue);
            } catch (Exception e) {
                if (dValue.equals("?") || dValue.equals("*")) {
                    secundaryArray[secundaryArray.length - 1] = dValue.charAt(0);
                } else
                    break;
            }
            arrayOfValues = secundaryArray;
            dimCounter++;
        } while (!dValue.toLowerCase().equals("sair") && !dValue.toLowerCase().equals("exit"));


        DataCube subCube = dataCube.getSubCube(arrayOfValues);

        int opt = 0;

        do {
            System.out.println("\n\nPESQUISA NO SUBCUBO:\n");
            System.out.println("1 - Fazer pesquisa de point querie no subcubo");
            System.out.println("2 - Fazer pesquisa de subcube querie no subcubo");
            System.out.println("3 - Mostrar subcubo de dados");
            System.out.println("9 - Sair do subcubo");

            opt = sc.nextInt();
            switch (opt) {
                case 1:
                    fazPesquisaPointQuery(sc, subCube);
                    break;
                case 2:
                    fazPesquisaSubCube(sc, subCube);
                case 3:
                    System.out.println(subCube.showDimensions());
            }


        } while (opt != 9);


    }

    private static void fazPesquisaPointQuery(Scanner sc, DataCube dataCube) {
        int[] arrayOfValues = new int[0];
        String dValue;
        int M;
        int dimCounter = 1;

        do {
            System.out.println("Indique valor para dimensão número " + dimCounter + " : valor númerico, '*' ou '?', ou favor não númerico para sair");
            dValue = sc.next();
            //dValue += sc.nextLine();

            int[] secundaryArray = new int[arrayOfValues.length + 1];
            System.arraycopy(arrayOfValues, 0, secundaryArray, 0, arrayOfValues.length);
            try {
                secundaryArray[secundaryArray.length - 1] = Integer.parseInt(dValue);
            } catch (Exception e) {
                if (dValue.equals("?") || dValue.equals("*")) {
                    secundaryArray[secundaryArray.length - 1] = dValue.charAt(0);
                } else
                    break;
            }
            arrayOfValues = secundaryArray;
            dimCounter++;
        } while (!dValue.toLowerCase().equals("sair") && !dValue.toLowerCase().equals("exit"));


        System.out.println("Indique valor para obter:\n1 - Lista de Tids dos objetos resultantes\n2- Listar numero de IDs correspondentes");
        M = sc.nextInt();

        int[] searchResult = dataCube.searchMultipleDimensionsAtOnce(arrayOfValues); //returns array of ids
        if (M == 1) {

            if (searchResult == null)
                System.out.println("Número de dimensões colocadas acima das existentes");
            else
                printIntArray(searchResult);
        } else if (M == 2)
            System.out.println("Tamanho:\t" + searchResult.length);

    }


    /**
     * @param array int[n][4] array
     * @return Objeto[n] array based on the int array
     */
    private static Objeto[] createObjetoFromIntArray(int[][] array) {
        Objeto[] objList = new Objeto[array.length];

        for (int i = 0; i < array.length; i++) {
            objList[i] = new Objeto(array[i][0], array[i][1], array[i][2], array[i][3]);
        }

        return objList;
    }


    /**
     * @param filePath path of the database file
     * @return int[][4] matrix of 4 collumns; Null if any error was found, such as File Not Found Exception
     * <p>
     * Reads a given file name
     * File specifications:
     * 1st line: 1 int with the number of objects that will be read -> used to allocate space to the objects arr
     * other lines: 4 ints separeted with a space (" ") from each other
     */
    public static int[][] readFromDisk(String filePath) {

        int[][] listaObjetos;


        Path path = Path.of(filePath);
        try {
            String line = null;                                         //the information will be read here
            String[] values;
            //int [] sizes;       //size[0] -> num of tuple //else num of diferent values

            InputStream in = Files.newInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            //first read -> reads the number of objects
            line = reader.readLine();

            values = line.split(" ");
            int[] sizes = new int[values.length];

            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = Integer.parseInt(values[i]);
            }

            //size = 100000;
            listaObjetos = new int[sizes[0]][];            //allocs the memory to the array


            for (int i = 0; i < sizes[0]; i++) {            //reads all the lines
                line = reader.readLine();               //reads a line
                //System.out.println(line);
                values = line.split(" ");           //splits the line read into X Strings

                //stores the values of the Strings
                listaObjetos[i] = new int[values.length];
                for (int n = 0; n < values.length; n++)
                    listaObjetos[i][n] = Integer.parseInt(values[n]);
                //System.out.println(i);
            }
            reader.close();
            in.close();
        } catch (Exception e) {             //in case there is any eception
            e.printStackTrace();
            return null;
        }

        return listaObjetos;
    }


    public static void writeOnDisk(String filePath, Objeto[] listaObjetos) {


        try {
            FileWriter writer = new FileWriter(filePath, false);
            BufferedWriter bw = new BufferedWriter(writer);

            bw.write(String.valueOf(listaObjetos.length));

            for (Objeto listaObjeto : listaObjetos) {
                bw.write("\n" + String.valueOf(listaObjeto.getD1()) + " " + String.valueOf(listaObjeto.getD2()) + " " + String.valueOf(listaObjeto.getD3()) + " " + String.valueOf(listaObjeto.getD4()) + " ");
            }

            bw.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void createAndWriteRandomObjetosList(String path, int number) {
        Objeto[] listaObjetos = new Objeto[number];
        for (int i = 0; i < number; i++) {
            listaObjetos[i] = new Objeto();
        }

        writeOnDisk(path, listaObjetos);
    }

}



