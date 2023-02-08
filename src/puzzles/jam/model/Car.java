package puzzles.jam.model;

/**
 * Car model class
 * @author Kilian Jakstis
 */
public class Car {
    private final char id; // char id of the car
    private int sR; // lowest value row coordinate of the car
    private int eR; // highest value row coordinate of the car
    private int sC; // lowest value col coordinate of the car
    private int eC; // highest value row coordinate of the car
    private final boolean isVCar; // is the car oriented vertically?

    /**
     * Constructor for Car
     * Set the car's fields such that the start row/col are the smaller of the two values
     * Assign the isVCar to true or false based on the row/col values
     * @param id char id of car
     * @param sR one row value
     * @param sC one col value
     * @param eR another row value
     * @param eC another col value
     */
    public Car(char id, int sR, int sC, int eR, int eC){
        this.id=id;
        if (sR > eR){
            this.eR = sR;
            this.sR = eR;
        } else if (eR > sR){
            this.eR = eR;
            this.sR = sR;
        } else {
            this.eR = eR;
            this.sR = eR;
        }
        if (sC > eC){
            this.eC = sC;
            this.sC = eC;
        } else if (eC > sC){
            this.eC = eC;
            this.sC = sC;
        } else {
            this.eC = eC;
            this.sC = eC;
        }
        this.isVCar = (this.sC == this.eC);
    }

    /**
     * @return the Car's starting row
     */
    public int getsR(){
        return sR;
    }

    /**
     * @return the Car's ending row
     */
    public int geteR(){
        return eR;
    }

    /**
     * @return the Car's starting col
     */
    public int getsC(){
        return sC;
    }

    /**
     * @return the Car's ending col
     */
    public int geteC(){
        return eC;
    }

    /**
     * Change the col values of the car by z
     * @param z an int (1 or -1)
     */
    public void setC(int z){
        if (Math.abs(z) != 1) return;
        this.eC += z;
        this.sC += z;
    }

    /**
     * Change the row values of the car by z
     * @param z an int (1 or -1)
     */
    public void setR(int z){
        if (Math.abs(z) != 1) return;
        this.eR += z;
        this.sR += z;
    }

    /**
     * @return value of car's isVCar field
     */
    public boolean isVertCar(){
        return this.isVCar;
    }

    /**
     * @return char value of car's ID
     */
    public char getID(){
        return this.id;
    }

    /**
     * ToString method for Cars; used for debugging
     * @return string representation of car
     */
    @Override
    public String toString(){
        return "Start Col: " + this.getsC() + ", " + "Start Row: " + this.getsR() + ", " + "\n"  +
                "End Col: " + this.geteC() + ", " + "End Row: " + this.geteR() + " :::: " + this.getID();
    }
}
