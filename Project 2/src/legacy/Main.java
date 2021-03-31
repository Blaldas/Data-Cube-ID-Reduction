package legacy;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;


public class Main {

    static boolean useMainCube = true;
    static DataCube mainCube = null, subCube = null;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String path = "obj";
        //System.out.println("<path> <faster (T/F)>");        //true usa o 2 args, false usa o 1 args
        //path = sc.next();
        //path += sc.nextLine();

        load("path 2");

        String input;
        //showAllRows();
        System.out.println("Total memory used:\t" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " bytes");
        do {
            System.out.println(">");
            input = sc.next();
            input += sc.nextLine();

            if (input.charAt(0) == 'q')
                newQuery(input);
            else if (input.equals("sub"))
                setUseSubCube();
            else if (input.equals("main"))
                setUseMainCube();
            else if (input.equals("help"))
                System.out.println("\n\nCommands:\nq <params>\nsub\nmain\nsair/ exit/ x\n\n\n");
            else if (input.equals("show"))
                showDataCube();
            else if (input.charAt(0) == 'i' && input.charAt(1) == 'd')
                showTupleID(input);
            else if (input.charAt(0) == 'w' && input.charAt(1) == 'r' && input.charAt(2) == 't')
                createAndWriteNew(input);
            else if (input.charAt(0) == 'l' && input.charAt(1) == 'o' && input.charAt(2) == 'a' && input.charAt(3) == 'd')
                loadUI(input);
            else if (input.equals("all"))
                showAllRows();
            else if (input.toLowerCase().equals("sair") || input.toLowerCase().equals("exit") || input.toLowerCase().equals("x") || input.toLowerCase().equals("quit"))
                break;
            else
                System.out.println("Unknown Command");

        } while (true);

    }

    private static void showAllRows() {
        if (useMainCube)
            mainCube.showAllQueryPossibilities();
        else
            subCube.showAllQueryPossibilities();
    }

    private static void loadUI(String input) {
        String[] str = input.split(" ");
        if (str.length != 3) {
            System.out.println("bad code");
            return;
        }
        String toSend;
        toSend= str[1];
        toSend += " ";
        toSend += str[2];
        load(toSend);
        subCube = null;


        //mainCube.shellFragmentsList[1].Teste();

    }


    private static void load(String filename) {


        String[] input = filename.split(" ");
        if (input.length != 2) {
            System.out.println("unknow input <" + filename + ">");
            return;
        }
        System.out.println("Loading <"+input[0]+">...");

        Date startDate = new Date(), endDate;

        //true usa o 2 args, false usa o 1 args
        if (input[1].toLowerCase().equals("f") || input[1].equals("1")) {                 //
            int[][] array = readFromDisk(input[0]);
            if (array == null) {
                System.out.println("Error reading the file <" + input[0] + ">");
                return;
            }
            mainCube = new DataCube(array);
        } else {                      //true ou 2
            int[] sizes = getSizes(input[0]); // guarda valores dos tamanhos-> size[0] -> numero de tuples, size[1...lenght] cardinalidade de cada tuple
            if (sizes == null) {
                System.out.println("It was not possible to load the file <" + input[0] + ">");
                return;
            }

            int[][] array = readFromDisk(input[0]);
            if (array == null) {
                System.out.println("Error reading the file <" + input[0] + ">");
                return;
            }
            mainCube = new DataCube(array, sizes);
        }
        endDate = new Date();
        long numSeconds = ((endDate.getTime() - startDate.getTime()));
        System.gc();
        System.out.println("Miliseconds Used to Load the data\t" + numSeconds);             //tempo
        //System.out.println("Tuples Read\t" +( mainCube.getBiggestID()+1));                  //num tuples
        System.out.println("Dimensions loaded\t" + mainCube.getNumberShellFragments());          //num dimensões
        System.out.println("cardinality\t" + mainCube.getShellFreagmentSize());
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

    private static void showTupleID(String input) {
        String[] query = input.split(" ");
        System.out.println(input);
        StringBuilder str = new StringBuilder().append("ID\t|\t");
        int numdim;

        if (useMainCube)
            numdim = mainCube.getNumberShellFragments();
        else
            numdim = subCube.getNumberShellFragments();

        for (int i = 1; i <= numdim; i++)
            str.append("D").append(i).append("\t");

        System.out.println(str + "\n");


        for (int i = 1; i < query.length; i++) {
            int[] dimValues = null;
            boolean exceptionFlag = false;
            str.setLength(0);
            try {
                dimValues = mostrarTuplePorID(Integer.parseInt(query[i]));
            } catch (Exception e) {
                System.out.println("Value not recognized: " + query[i]);
                exceptionFlag = true;
            }
            if (dimValues == null && !exceptionFlag)
                System.out.println("Dimension id <" + query[i] + "> was not found");
            else if (!exceptionFlag) {

                str.append(query[i]).append("\t|\t");
                for (int d : dimValues)
                    str.append(d).append("\t");
                System.out.println(str);
            }


        }

    }

    /**
     * Decides which datacube to show, based on the "useMainCube" flag and prints it
     */
    private static void showDataCube() {
        if (useMainCube)
            System.out.println(mainCube.showDimensions());
        else
            System.out.println(subCube.showDimensions());
    }

    /**
     * alters the flag "usaMainCube" to true, or indicates that is already true
     */
    private static void setUseMainCube() {
        if (useMainCube)
            System.out.println("idReducted.Main cube already being used");
        else {
            useMainCube = true;
            System.out.println("using main cube\n");
        }
    }

    /**
     * alters the flag "usaMainCube" to false, or indicates that is already false
     */
    private static void setUseSubCube() {
        if (useMainCube && subCube != null) {
            useMainCube = false;
            System.out.println("using subcube\n");
        } else if (subCube == null)
            System.out.println("No subcube has been created to use");
        else
            System.out.println("Subcube already being used");
    }

    /**
     * @param input query input from the user. Something like "q 1 2 3"
     *              Decides, based on the "useMainCube" flag, which cube to use when calling the "real" query method
     */
    public static void newQuery(String input) {
        if (useMainCube)
            query(input, mainCube);
        else
            query(input, subCube);
    }

    /**
     * @param input    user input. Something like "q 1 2 3"
     * @param dataCube The data cube where the search is going to be made
     */
    private static void query(String input, DataCube dataCube) {

        String[] stringValues = input.split(" ");
        int[] values = new int[stringValues.length - 1];

        boolean subCubeFlag = false;

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
        Date startDate = new Date(), endDate;
        if (subCubeFlag) {
            subCube = dataCube.getSubCube(values);
/*
            if (subCube == null) {
                System.out.println("There were no tuples that had such values");
            } else
                System.out.println(subCube.showIndividualTuples());
*/
        } else {
            if (useMainCube) {
                int[] searchResult = dataCube.searchMultipleDimensionsAtOnce(values); //returns array of ids
                if (searchResult == null)
                    System.out.println("Bad Query formation");
                else
                    System.out.println("Query answers:\t" + searchResult.length);
            } else {
                int[] searchResult = subCube.searchMultipleDimensionsAtOnce(values); //returns array of ids
                if (searchResult == null)
                    System.out.println("Bad Query formation");
                else
                    System.out.println("Query answers:\t" + searchResult.length);
            }
        }
        endDate = new Date();
        long numSeconds = ((endDate.getTime() - startDate.getTime()));
        System.gc();
        System.out.println("Miliseconds Used to Load the data\t" + numSeconds);


    }

    /**
     * @param index index of the value to be shown, it is relative to the data cube itself
     * @return int[] array with all the dimensional values, NULL if that ID was not fund
     */
    private static int[] mostrarTuplePorID(int index) {
        if (useMainCube)
            return mainCube.getDimensions(index);
        return subCube.getDimensions(index);

    }

    /**
     * @param filePath Path of the file to be read
     * @return int array were the first element is the number of tuples and the other ones are the biggest element of each dimension
     * <p>
     * This method was created due to the fact that I thought that the numbers that represent the biggest number fo each dimensions
     * were actually the cardinality, which is the number of different values for each dimension
     * This are only useful when the data structure starts simply by crating an array from an lower limit until that number.
     * Because I believe that kind of structure does not make sense to a program that attempts to use the smallest amount of space possible
     * I am not using that kind of structure, which means that those upper limit numbers are not useful and this function is pretty much a wast of processing power,
     * however, I am lazy and this method does really have a big impact, therefore I am going to concentrate on adding more capailities to the program and, if I got time
     * and/ or patience and remember,then I may consider wasting my time even further here.
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



