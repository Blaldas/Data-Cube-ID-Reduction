
import invertedIndex.InvertedIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndexCars {


    Map<String, Map<String, List<Integer>>> table;

    public InvertedIndexCars(Car[] carList) {
        table = new HashMap<>();

        createInvertedIndex(carList);
    }

    /**
     * @param carList -> array of cars
     *                <p>
     *                This method is used to create an Inverted Index Table to store cars from the Car class
     */
    private void createInvertedIndex(Car[] carList) {
        //initialize the internal hasmap
        table.put("Type", new HashMap<>());
        table.put("Color", new HashMap<>());
        table.put("FuelType", new HashMap<>());
        table.put("Year", new HashMap<>());
        table.put("NumberOfDoors", new HashMap<>());
        table.put("Power", new HashMap<>());

        //get the data there
        for (int i = 0; i < carList.length; i++) {
            if (!table.get("Type").containsKey(carList[i].getCarType().toString())) {
                table.get("Type").put(carList[i].getCarType().toString(), new ArrayList<>());
                table.get("Type").get(carList[i].getCarType().toString()).add(i);
            } else {
                table.get("Type").get(carList[i].getCarType().toString()).add(i);
            }
            if (!table.get("Color").containsKey(carList[i].getColor().toString())) {
                table.get("Color").put(carList[i].getColor().toString(), new ArrayList<>());
                table.get("Color").get(carList[i].getColor().toString()).add(i);
            } else {
                table.get("Color").get(carList[i].getColor().toString()).add(i);
            }
            if (!table.get("FuelType").containsKey(carList[i].getFuelType().toString())) {
                table.get("FuelType").put(carList[i].getFuelType().toString(), new ArrayList<>());
                table.get("FuelType").get(carList[i].getFuelType().toString()).add(i);
            } else {
                table.get("FuelType").get(carList[i].getFuelType().toString()).add(i);
            }
            if (!table.get("NumberOfDoors").containsKey(String.valueOf(carList[i].getNumberDoors()))) {
                table.get("NumberOfDoors").put(String.valueOf(carList[i].getNumberDoors()), new ArrayList<>());
                table.get("NumberOfDoors").get(String.valueOf(carList[i].getNumberDoors())).add(i);
            } else {
                table.get("NumberOfDoors").get(String.valueOf(carList[i].getNumberDoors())).add(i);
            }
            if (!table.get("Power").containsKey(String.valueOf(carList[i].getPower()))) {
                table.get("Power").put(String.valueOf(carList[i].getPower()), new ArrayList<>());
                table.get("Power").get(String.valueOf(carList[i].getPower())).add(i);
            } else {
                table.get("Power").get(String.valueOf(carList[i].getPower())).add(i);
            }
            if (!table.get("Year").containsKey(String.valueOf(carList[i].getYear()))) {
                table.get("Year").put(String.valueOf(carList[i].getYear()), new ArrayList<>());
                table.get("Year").get(String.valueOf(carList[i].getYear())).add(i);
            } else {
                table.get("Year").get(String.valueOf(carList[i].getYear())).add(i);
            }

        }


    }

    public void showInvertedIndex() {
        List<String> keys = new ArrayList<>(table.keySet());

        for (String key : keys) {
            System.out.println("\n");
            System.out.println("\n");
            System.out.println(key);

            Map<String, List<Integer>> subMap = table.get(key);
            List<String> subKeys = new ArrayList<>(subMap.keySet());

            for (String subkey : subKeys) {


                List<Integer> posList = subMap.get(subkey);
                StringBuilder positionListString = new StringBuilder();
                for (int lineNumber : posList)
                    positionListString.append(lineNumber).append(" ");

                System.out.println(subkey + "\t\t" + positionListString);
            }
        }
    }

    //Until a better idea, this method can't be as abstract as i'd like

    /**
     *
     * @param power             String with the power of the car, or '*'
     * @param color             String with the color of the car, or '*'
     * @param numberDoors       String with the number of doors of the car, or '*'
     * @param year              String with the year of the car, or '*'
     * @param carType           String with the type of the car, or '*'
     * @param fuelType          String with the fuel type of the car, or '*'
     *
     * @return                  ArrayList<Integer> with all the Indexes of the cars that respect the search params 
     */
    public List<Integer> seach(String power, String color, String numberDoors, String year, String carType, String fuelType) {
        List<Integer> result = new ArrayList<>(), secundary = null;

        //the first one goes directly to the main result variable
        if (power.equals("*")) {
            List<String> subkeyList = new ArrayList<>(table.get("Power").keySet());

            for (String subKey : subkeyList)
                result.addAll(table.get("Power").get(subKey));

        } else {
            result = table.get("Power").get(power);
            if (result.size() == 0)
                return null;
        }

        secundary = new ArrayList<>();
        //color
        if (color.equals("*")) {    //if it doesn't matter
            //gets all the subkeys
            List<String> subkeyList = new ArrayList<>(table.get("Color").keySet());

            //for each subkey, obtains all the values and stores in secondary
            for (String subKey : subkeyList)
                secundary.addAll(table.get("Color").get(subKey));

            //intersects secundary and result.
            result.retainAll(secundary);

        } else {
            result.retainAll(table.get("Color").get(color));
            if (result.size() == 0)
                return null;
        }

        secundary = new ArrayList<>();
        //number of doors
        if (numberDoors.equals("*")) {    //if it doesn't matter
            //gets all the subkeys
            List<String> subkeyList = new ArrayList<>(table.get("NumberOfDoors").keySet());

            //for each subkey, obtains all the values and stores in secondary
            for (String subKey : subkeyList)
                secundary.addAll(table.get("NumberOfDoors").get(subKey));

            //intersects secundary and result.
            result.retainAll(secundary);

        } else {
            result.retainAll(table.get("NumberOfDoors").get(numberDoors));
            if (result.size() == 0)
                return null;
        }

        secundary = new ArrayList<>();
        //year
        if (year.equals("*")) {    //if it doesn't matter
            //gets all the subkeys
            List<String> subkeyList = new ArrayList<>(table.get("Year").keySet());

            //for each subkey, obtains all the values and stores in secondary
            for (String subKey : subkeyList)
                secundary.addAll(table.get("Year").get(subKey));

            //intersects secundary and result.
            result.retainAll(secundary);

        } else {
            result.retainAll(table.get("Year").get(year));
            if (result.size() == 0)
                return null;
        }

        secundary = new ArrayList<>();
        //year
        if (carType.equals("*")) {    //if it doesn't matter
            //gets all the subkeys
            List<String> subkeyList = new ArrayList<>(table.get("Type").keySet());

            //for each subkey, obtains all the values and stores in secondary
            for (String subKey : subkeyList)
                secundary.addAll(table.get("Type").get(subKey));

            //intersects secundary and result.
            result.retainAll(secundary);

        } else {
            result.retainAll(table.get("Type").get(carType));
            if (result.size() == 0)
                return null;
        }

        secundary = new ArrayList<>();
        //year
        if (fuelType.equals("*")) {    //if it doesn't matter
            //gets all the subkeys
            List<String> subkeyList = new ArrayList<>(table.get("FuelType").keySet());

            //for each subkey, obtains all the values and stores in secondary
            for (String subKey : subkeyList)
                secundary.addAll(table.get("FuelType").get(subKey));

            //intersects secundary and result.
            result.retainAll(secundary);

        } else {
            result.retainAll(table.get("FuelType").get(fuelType));
            if (result.size() == 0)
                return null;
        }

        return result;

    }


}
