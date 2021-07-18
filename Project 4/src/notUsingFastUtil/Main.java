package notUsingFastUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static DataCube mainCube;
    static int lowerValue = 1;
    public static boolean verbose = false;


    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("fragCubing_java.jar <dataset name>");
            System.exit(1);
        }

        System.out.println("\nnot using ID REDUCTION \n");

        Scanner sc = new Scanner(System.in);
        String path = args[0];
        load(path);
        System.gc();

        System.out.println("Total memory used:\t" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " bytes");

        String input;

        //every single memory check and garbage collector call must be made inside this loop on order to avoid to have errors
        do {
            System.out.println(">");
            input = sc.next();
            input += sc.nextLine();

            if (input.charAt(0) == 'q') {
                query(input);
                System.out.println("Used memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            }
            if (input.toLowerCase().equals("sair") || input.toLowerCase().equals("exit") || input.toLowerCase().equals("x") || input.toLowerCase().equals("quit"))
                break;
            else if (input.toLowerCase().equals("v"))
                verboseChange();
            else
                System.out.println("Unknown Command");

            System.gc();        //used to be able to do multiple operations in a single run.
        } while (true);
    }

    private static void verboseChange() {
        verbose = !verbose;
        System.out.println("verbose: " + verbose);
    }

    private static void load(String filename) {

        System.out.println("Loading <" + filename + ">...");

        Date startDate = new Date(), endDate;

        generalReadFromDisk(filename);

        endDate = new Date();
        long numSeconds = ((endDate.getTime() - startDate.getTime()));
        //System.gc();
        System.out.println("Miliseconds Used to Load the data\t" + numSeconds);             //tempo
        System.out.println("Dimensions loaded\t" + mainCube.getNumberShellFragments());          //num dimensões
        //System.out.println("Cardinality\t" + mainCube.shellFragmentList[0].size.length);          //num dimensões
        System.out.println("number of tuples loaded\t" + mainCube.getNumberTuples());


        System.out.println("load end");

    }

    /**
     * @param input user input. Something like "q 1 2 3"
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
                        values[i - 1] = -99;
                        subCubeFlag = true;
                        break;
                    case "*":
                        values[i - 1] = -88;
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
            int[] searchResult = mainCube.pointQuerySeach(values); //returns array of ids
            if (searchResult == null) {
                System.out.println("Bad Query formation");
            } else
                System.out.println("Query answers:\t" + searchResult.length);
        }

        endDate = new Date();
        long numSeconds = ((endDate.getTime() - startDate.getTime()));
        //System.gc();
        System.out.println("Query executed in " + numSeconds + " ms.");

    }


    /**
     * @param filePath path of the database file
     *                 Reads a given dataset and creates the data cube
     */
    public static void generalReadFromDisk(String filePath) {

        Path path = Path.of(filePath);
        try {
            String line = null;                                         //the information will be read here
            String[] values;
            //int [] sizes;       //size[0] -> num of tuple //else num of diferent values
            int totalTuples;

            InputStream in = Files.newInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            //first read -> reads the number of objects
            line = reader.readLine();

            values = line.split(" ");

            totalTuples = Integer.parseInt(values[0]);

            int[] sizes = new int[values.length - 1];

            for (int i = 1; i < values.length; i++) {
                sizes[i - 1] = Integer.parseInt(values[i]);
            }


            mainCube = new DataCube(sizes, lowerValue);


            int numDimensions = sizes.length;        //guarda o numero de dimensãos

            int[] newTuple = new int[numDimensions];
            for (int i = 0; i < totalTuples; i++) {            //reads all the lines
                line = reader.readLine();               //reads a line

                values = line.split(" ");           //splits the line read into X Strings

                if (values.length != numDimensions) {
                    System.out.println("tuple id = " + i + " doesn't have the same number of dimensions");
                    System.exit(1);
                }

                for (int n = 0; n < numDimensions; n++)
                    newTuple[n] = Integer.parseInt(values[n]);
                mainCube.addTuple(i, newTuple);

            }
            reader.close();
            in.close();


        } catch (Exception e) {             //in case there is any eception
            e.printStackTrace();
            System.exit(1);
        }

        mainCube.proneShellfragments();
    }

}
