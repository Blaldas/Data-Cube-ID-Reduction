import java.util.Random;

public class Objeto {

    private final int D1;
    private final int D2;
    private final int D3;
    private final int D4;


    public Objeto(int D1, int D2, int D3, int D4) {
        this.D1 = D1;
        this.D2 = D2;
        this.D3 = D3;
        this.D4 = D4;
    }

    public Objeto() {

        Random r = new Random();
        D1 = r.nextInt(3) + 1;
        D2 = r.nextInt(3) + 11;
        D3 = r.nextInt(3) + 21;
        D4 = r.nextInt(3) + 31;
    }

    @Override
    public String toString() {
        return "Objeto{" +
                "D1=" + D1 +
                ", D2=" + D2 +
                ", D3=" + D3 +
                ", D4=" + D4 +
                '}';
    }

    public int getD4() {
        return D4;
    }

    public int getD3() {
        return D3;
    }

    public int getD2() {
        return D2;
    }

    public int getD1() {
        return D1;
    }
}
