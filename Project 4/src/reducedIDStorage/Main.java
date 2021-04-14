package reducedIDStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static DataCube mainCube;
    static int lowerValue = 1;


    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String path = "lmao";

        load(path);

        String input;
        //mainCube.showAllDimensions();
        System.out.println("Total memory used:\t" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " bytes");


        do {
            System.out.println(">");
            input = sc.next();
            input += sc.nextLine();

            if (input.charAt(0) == 'q')
                query(input);
            else if (input.equals("show"))
                mainCube.showAllDimensions();
            else if (input.charAt(0) == 'w' && input.charAt(1) == 'r' && input.charAt(2) == 't')
                createAndWriteNew(input);
            else if (input.toLowerCase().equals("sair") || input.toLowerCase().equals("exit") || input.toLowerCase().equals("x") || input.toLowerCase().equals("quit"))
                break;
            else
                System.out.println("Unknown Command");

        } while (true);

    }


    private static void load(String filename) {

        System.out.println("Loading <" + filename + ">...");

        Date startDate = new Date(), endDate;

        int[] sizes = getSizes(filename); // guarda valores dos tamanhos-> size[0] -> numero de tuples, size[1...lenght] cardinalidade de cada tuple
        if (sizes == null) {
            System.out.println("It was not possible to load the file <" + filename + ">");
            return;
        }

        int[][] array = readFromDisk(filename);
        if (array == null) {
            System.out.println("Error reading the file <" + filename + ">");
            return;
        }
        mainCube = new DataCube(array, sizes, lowerValue);

        endDate = new Date();
        long numSeconds = ((endDate.getTime() - startDate.getTime()));
        System.gc();
        System.out.println("Miliseconds Used to Load the data\t" + numSeconds);             //tempo
        System.out.println("Dimensions loaded\t" + mainCube.getNumberShellFragments());          //num dimensões
        System.out.println("number of tuples loaded\t" + mainCube.getNumberTuples());


        System.out.println("load end");

    }

    /**
     * @param input input from the user with all the values
     */
    private static void createAndWriteNew(String input) {
        String[] valuesStr = input.split(" ");
        if (valuesStr.length != 5) {
            System.out.println("bad code");
            return;
        }

        try {
            int[] dimArray;

            valuesStr[3] = valuesStr[3].replace("{", "").replace("}", "");
            String[] dimStr = valuesStr[3].split(",");
            dimArray = new int[dimStr.length];

            for (int i = 0; i < dimArray.length; i++)
                dimArray[i] = Integer.parseInt(dimStr[i]);
            createAndWriteRandomObjetosList(valuesStr[1], Integer.parseInt(valuesStr[2]), dimArray, Integer.parseInt(valuesStr[4]));
        } catch (Exception e) {
            System.out.println("bad code");
        }


    }


    /**
     * @param input    user input. Something like "q 1 2 3"
     */
    private static void query(String input) {

        String[] stringValues = input.split(" ");   // faz split de cada uma das diemnsões
        int[] values = new int[stringValues.length - 1];

        boolean subCubeFlag = false;

        //coloca cada um dos valores no aray values
        for (int i = 1; i < stringValues.length; i++) {
            try {
                values[i - 1] = Integer.parseInt(stringValues[i]);
            } catch (Exception e) {
                switch (stringValues[i]) {
                    case "?":
                        values[i - 1] = '?';
                        subCubeFlag = true;
                        break;
                    case "*":
                        values[i - 1] = '*';
                        break;
                    default:
                        System.out.println("Invalid value in query");
                        return;
                }
            }
        }

        Date startDate = new Date(), endDate;           //incia as datas para fazer contagem do tempo
        if (subCubeFlag) {                  //caso seja um subcube
            mainCube.getSubCube(values);
        } else {
            int searchResult = mainCube.pointQueryCounter(values); //returns array of ids
        if (searchResult == -1)
            System.out.println("Bad Query formation");
        else
            System.out.println("Query answers:\t" + searchResult);
        }
        endDate = new Date();
        long numSeconds = ((endDate.getTime() - startDate.getTime()));
        System.gc();
        System.out.println("Query executed in " + numSeconds+ " ms.");


    }

    /**
     * @param filePath Path of the file to be read
     * @return int array were the first element is the number of tuples and the other ones are the biggest element of each dimension
     *
     */
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


    /**
     * @param filePath path of the database file
     * @return int[][] matrix. Null if any exception was found, such as File Not Found Exception
     * Reads a given file name
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

    /**
     * @param filePath path of the file to write on/create
     * @param array    array to be written.
     *                 <p> d1 d2 d3
     *                 <p> d1 d2 d3
     *                 <p> d1 d2 d3
     *                 <p> d1 d2 d3
     */
    public static void writeOnDisk(String filePath, int[][] array) {

        try {
            FileWriter writer = new FileWriter(filePath, false);
            BufferedWriter bw = new BufferedWriter(writer);

            StringBuilder str = new StringBuilder();

            str.append(array.length).append(" ");        //escreve tamanho do array

            //escreve maior valor de cada uma das dimensões
            for (int i = 0; i < array[0].length; i++) {         //coluna
                int max = array[0][i];
                for (int n = 1; n < array.length; n++)          //linha
                    if (array[n][i] > max)
                        max = array[n][i];

                str.append(max).append(" ");
                ;
            }

            bw.write(str.toString() + "\n");

            for (int[] ints : array) {
                for (int anInt : ints)
                    bw.write(anInt + " ");
                bw.write("\n");
            }

            bw.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param path               path to were the file must be written
     * @param numberOfElements   Number of tuples to be written
     * @param numberOfDimensions Number of Dimensions for each tuple and the smallest value for each tuple.
     *                           the length of the array indicates how many dimensions the tuples have, the value of each position in the array
     *                           indicate the smallest value accepted to that dimension
     * @param cardinality        The interval of values the dimensions may have. An cardinality of 5 means that each dimension may have
     *                           one of 5 different values.
     *
     *                           <p>                    d1 d2 d3
     *                           <p>                    d1 d2 d3
     *                           <p>                    d1 d2 d3
     *                           <p>                    d1 d2 d3
     */
    public static void createAndWriteRandomObjetosList(String path, int numberOfElements, int[] numberOfDimensions, int cardinality) {
        int[][] listObjets = new int[numberOfElements][numberOfDimensions.length];
        Random r = new Random();
        System.out.println("Cardinality " + cardinality);
        for (int i = 0; i < numberOfElements; i++) {                                //para cada um dos elementos
            StringBuilder str = new StringBuilder();                                        //cria uma nova stringBuilder
            for (int n = 0; n < numberOfDimensions.length; n++) {                           //para cada uma das dimensões
                listObjets[i][n] = r.nextInt(cardinality) + numberOfDimensions[n];              //cria um valor para a dimensão com os numeros indicadosa
                str.append(listObjets[i][n]);                                                   //adiciona valor criado a string
                //listObjets[i][n] += numberOfDimensions[n];
                str.append("\t||\t");                                                            //adiciona barras para efeito visual
            }
            System.out.println(str);                                                        //mostra a stringBuilder criada
        }
        writeOnDisk(path, listObjets);                                              //escreve no disco
    }
}
