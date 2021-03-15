import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;


public class Main {


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String path = "Objectos.txt";
        int number = 25000;

        //createAndWriteRandomObjetosList(path, number);
        int[][] array = readFromDisk(path);
        if (array == null)
            return;

        Objeto[] listaObjetos = createObjetoFromIntArray(array);

        //InvertedIndexTableWithReducesIds iIT = new InvertedIndexTableWithReducesIds(listaObjetos);
        InvertedIndexTables iIT = new InvertedIndexTables(listaObjetos);
        iIT.showTable();

        int opt = 0;
        do {
            System.out.println("1 - Fazer pesquisa de point querie");
            System.out.println("2 - Fazer pesquisa de subcube querie");
            System.out.println("3 - Mostrar cubo de dados");
            System.out.println("9 - Sair do programa");

            opt = sc.nextInt();
            switch (opt) {
                case 1:
                    fazPesquisaPointQuery(sc, iIT);
                    break;
                case 2:
                    fazPesquisaSubCube(sc, iIT);
                case 3:
                    iIT.showTable();
            }

        } while (opt != 9);

    }

    private static void fazPesquisaSubCube(Scanner sc, InvertedIndexTables iIT) {
        String d1, d2, d3, d4;
        int M;

        System.out.println("Indique valor para d1: valor númerico, '*' ou '?'");
        d1 = sc.next();
        d1 += sc.nextLine();
        System.out.println("Indique valor para d2: valor númerico, '*' ou '?'");
        d2 = sc.next();
        d2 += sc.nextLine();
        System.out.println("Indique valor para d3: valor númerico, '*' ou '?'");
        d3 = sc.next();
        d3 += sc.nextLine();
        System.out.println("Indique valor para d4: valor númerico, '*' ou '?'");
        d4 = sc.next();
        d4 += sc.nextLine();

        InvertedIndexTables newInvertedTable = iIT.subcubeQuery(d1, d2,d3, d4);

        int opt = 0;

        do{
            System.out.println("\n\nPESQUISA NO SUBCUBO:\n");
            System.out.println("1 - Fazer pesquisa de point querie no subcubo");
            System.out.println("2 - Fazer pesquisa de subcube querie no subcubo");
            System.out.println("3 - Mostrar subcubo de dados");
            System.out.println("9 - Sair do subcubo");

            opt = sc.nextInt();
            switch (opt) {
                case 1:
                    fazPesquisaPointQuery(sc, newInvertedTable);
                    break;
                case 2:
                    fazPesquisaSubCube(sc, newInvertedTable);
                case 3:
                    newInvertedTable.showTable();
            }



        }while(opt != 9);


    }

    private static void fazPesquisaPointQuery(Scanner sc, InvertedIndexTables iIT) {
        String d1, d2, d3, d4;
        int M;

        System.out.println("Indique valor para d1: valor númerico, '*' ou '?'");
        d1 = sc.next();
        d1 += sc.nextLine();
        System.out.println("Indique valor para d2: valor númerico, '*' ou '?'");
        d2 = sc.next();
        d2 += sc.nextLine();
        System.out.println("Indique valor para d3: valor númerico, '*' ou '?'");
        d3 = sc.next();
        d3 += sc.nextLine();
        System.out.println("Indique valor para d4: valor númerico, '*' ou '?'");
        d4 = sc.next();
        d4 += sc.nextLine();
        System.out.println("Indique valor para obter:\n1 - Lista de Tids dos objetos resultantes\n");
        M = sc.nextInt();


        if (M == 1) {
            int[] searchResult = iIT.searchInstanciatedValues(d1, d2, d3, d4); //returns array of ids
            if (searchResult.length == 0)
                System.out.println("Não foram encontrados resultados");
            else {
                StringBuilder str = new StringBuilder();
                for (int i : searchResult)
                    str.append(i).append(" ");
                System.out.println("TIDs\t" + str);
            }
        }
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
            String[] values = new String[4];
            int size;

            InputStream in = Files.newInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            //first read -> reads the number of objects
            line = reader.readLine();
            size = Integer.parseInt(line);

            listaObjetos = new int[size][];            //allocs the memory to the array


            for (int i = 0; i < size; i++) {            //reads all the lines
                line = reader.readLine();               //reads a line
                //System.out.println(line);
                values = line.split(" ");           //splits the line read into 4 Strings

                //stores the values of the 4 Strings
                listaObjetos[i] = new int[]{Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3])};
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
                bw.write("\n" + String.valueOf(listaObjeto.getD1()) + " " + String.valueOf(listaObjeto.getD2()) + " " + String.valueOf(listaObjeto.getD3()) + " " + String.valueOf(listaObjeto.getD4()));
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



