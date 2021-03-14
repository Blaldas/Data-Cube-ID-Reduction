public class Car {



    public enum Color{
        red, black, green, white, blue, yellow;
    }

    public enum CarType{
        city, compact, SUV, family;
    }

    public enum FuelType{
        electric, gasoline, diesel, hybrid;
    }


    private int power;
    private Color color;
    private int numberDoors;
    private int year;
    private CarType carType;
    private FuelType fuelType;


    public Car(int power, Color color, int numberDoors, int year, CarType carType, FuelType fuelType) {
        this.power = power;
        this.color = color;
        this.numberDoors = numberDoors;
        this.year = year;
        this.carType = carType;
        this.fuelType = fuelType;
    }

    public int getPower() {
        return power;
    }

    public Color getColor() {
        return color;
    }

    public int getNumberDoors() {
        return numberDoors;
    }

    public int getYear() {
        return year;
    }

    public CarType getCarType() {
        return carType;
    }

    public FuelType getFuelType() {
        return fuelType;
    }
}
