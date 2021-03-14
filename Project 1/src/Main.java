import java.util.Random;

public class Main {

    public static void main(String[] args) {


        Car[] carlist = new Car[1000000];

        for (int i = 0; i < 1000000; i++) {
            carlist[i] = carRancomizer();
        }

        InvertedIndexCars iv = new InvertedIndexCars(carlist);

        iv.showInvertedIndex();


    }

    /**
     * @return car with random values
     */
    public static Car carRancomizer() {
        Random r = new Random();
        //power
        int lowPower = 30;
        int highPower = 500;
        int power = r.nextInt(highPower - lowPower) + lowPower;

        //color -> red, back, green, white, blue, yellow;
        int colorNumber = r.nextInt(6);
        Car.Color color;
        switch (colorNumber) {
            case 0:
                color = Car.Color.red;
                break;
            case 1:
                color = Car.Color.black;
                break;
            case 2:
                color = Car.Color.green;
                break;
            case 3:
                color = Car.Color.white;
                break;
            case 4:
                color = Car.Color.blue;
                break;
            case 5:
                color = Car.Color.yellow;
                break;
            default:
                color = Car.Color.black;
        }

        //number of doors
        int randomNumberOfDoors = r.nextInt(2);
        int numberOfDoors = 5;
        switch (randomNumberOfDoors) {
            case 0:
                numberOfDoors = 3;
                break;
            case 1:
                numberOfDoors = 5;
        }

        //year
        int lowYear = 1970;
        int highYear = 2020;
        int Year = r.nextInt(highYear - lowYear) + lowYear;

        //type  city, compact, SUV, family;
        int randomCarType = r.nextInt(4);
        Car.CarType carType = Car.CarType.family;
        ;
        switch (randomCarType) {
            case 0:
                carType = Car.CarType.city;
                break;
            case 1:
                carType = Car.CarType.compact;
                break;
            case 2:
                carType = Car.CarType.SUV;
                break;
            case 3:
                carType = Car.CarType.family;
        }

        //fuel type  electric, gasoline, diesel, hybrid;
        int randomFuelType = r.nextInt(4);
        Car.FuelType fuelType = Car.FuelType.diesel;
        switch (randomFuelType) {
            case 0:
                fuelType = Car.FuelType.electric;
                break;
            case 1:
                fuelType = Car.FuelType.gasoline;
                break;
            case 2:
                fuelType = Car.FuelType.diesel;
                break;
            case 3:
                fuelType = Car.FuelType.hybrid;
        }


        return new Car(power, color, numberOfDoors, Year, carType, fuelType);

    }


}
