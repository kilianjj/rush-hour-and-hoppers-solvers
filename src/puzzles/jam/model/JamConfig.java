package puzzles.jam.model;
import puzzles.common.solver.Configuration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Configuration class for Traffic Jam boards
 * @author Kilian Jakstis
 */
public class JamConfig implements Configuration {
    public static final char EMPTY = '.'; // the character for an empty board cell
    private int rows; // number of rows in board
    private int cols; // number of columns in the board
    public Car[] cars; // list of the cars present on the board

    // a 2d array of characters representing the board
    // INDEXED WITH: boardChars[COL][ROW]
    public char[][] boardChars;

    /**
     * Constructor for Jam puzzle configuration using a Jam configuration file
     * Tries to open and read file; if successful, defines the config's rows, cols, cars, and boardChars fields
     * @param filename name of Jam config file
     */
    public JamConfig(String filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String[] s = br.readLine().split("\s++");
            // get row, col, and number of car info from file
            this.rows = Integer.parseInt(s[0]);
            this.cols = Integer.parseInt(s[1]);
            String line = br.readLine();
            this.cars = new Car[Integer.parseInt(line)];
            line = br.readLine();
            int i = 0;
            // create Cars and add to cars array
            while (line != null) {
                s = line.split("\s++");
                cars[i] = (new Car(s[0].charAt(0), Integer.parseInt(s[1]), Integer.parseInt(s[2]),
                        Integer.parseInt(s[3]), Integer.parseInt(s[4])));
                line = br.readLine();
                i++;
            }
            // create the board
            this.boardChars = makeBoard(this.cars);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for JamConfigs made with row, col, and car array instead of filename
     * Used for generating configurations arising from user moves
     * @param cols number of cols
     * @param rows number of rows
     * @param cars the array containing all the cars in the puzzle
     */
    public JamConfig(int cols, int rows, Car[] cars) {
        // assign the parameters to respective fields
        this.cars = cars;
        this.cols = cols;
        this.rows = rows;
        // make the new board
        this.boardChars = this.makeBoard(this.cars);
    }

    /**
     * Checks if the configuration is a solution to the puzzle
     * @return (the red 'X' car is touching the right side of the board, T or F?)
     */
    @Override
    public boolean isSolution() {
        for (Car c : this.cars) {
            if (c.getID() == 'X' && c.geteC() == this.cols - 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates all possible neighbor configurations to the configuration
     * For every legal move in the position, a new configuration will be generated
     * @return the array of all neighbor positions
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        // for every car on the board, for every open space it can go to, generate a new configuration and add it to
        // the array
        ArrayList<Configuration> neighbors = new ArrayList<>();
        for (Car c : this.cars) {
            if (c.isVertCar()) {
                if (c.geteR() + 1 < this.rows) {
                    if (this.boardChars[c.geteC()][c.geteR() + 1] == EMPTY) {
                        Car next = new Car(c.getID(), c.getsR() + 1, c.getsC(), c.geteR() + 1, c.getsC());
                        Car[] copy = new Car[this.cars.length];
                        for (int i = 0; i < this.cars.length; i++) {
                            if (this.cars[i].getID() != c.getID()) {
                                copy[i] = this.cars[i];
                            } else {
                                copy[i] = next;
                            }
                        }
                        JamConfig config = new JamConfig(this.cols, this.rows, copy);
                        neighbors.add(config);
                    }
                }
                if (c.getsR() - 1 >= 0) {
                    if (this.boardChars[c.geteC()][c.getsR() - 1] == EMPTY) {
                        Car next = new Car(c.getID(), c.getsR() - 1, c.getsC(), c.geteR() - 1, c.getsC());
                        Car[] copy = new Car[this.cars.length];
                        for (int i = 0; i < this.cars.length; i++) {
                            if (this.cars[i].getID() != c.getID()) {
                                copy[i] = this.cars[i];
                            } else {
                                copy[i] = next;
                            }
                        }
                        JamConfig config = new JamConfig(this.cols, this.rows, copy);
                        neighbors.add(config);
                    }
                }
            } else {
                if (c.geteC() + 1 < this.cols) {
                    if (this.boardChars[c.geteC() + 1][c.geteR()] == EMPTY) {
                        Car next = new Car(c.getID(), c.getsR(), c.getsC() + 1, c.geteR(), c.geteC() + 1);
                        Car[] copy = new Car[this.cars.length];
                        for (int i = 0; i < this.cars.length; i++) {
                            if (this.cars[i].getID() != c.getID()) {
                                copy[i] = this.cars[i];
                            } else {
                                copy[i] = next;
                            }
                        }
                        JamConfig config = new JamConfig(this.cols, this.rows, copy);
                        neighbors.add(config);
                    }
                }
                if (c.getsC() - 1 >= 0) {
                    if (this.boardChars[c.getsC() - 1][c.getsR()] == EMPTY) {
                        Car next = new Car(c.getID(), c.getsR(), c.getsC() - 1, c.geteR(), c.geteC() - 1);
                        Car[] copy = new Car[this.cars.length];
                        for (int i = 0; i < this.cars.length; i++) {
                            if (this.cars[i].getID() != c.getID()) {
                                copy[i] = this.cars[i];
                            } else {
                                copy[i] = next;
                            }
                        }
                        JamConfig config = new JamConfig(this.cols, this.rows, copy);
                        neighbors.add(config);
                    }
                }
            }
        }
        return neighbors;
    }

    /**
     * Writes the game's board as a string (used this for easier debugging purposes)
     * @return string depiction of the game's current position
     */
    public String specialPrint() {
        StringBuilder sb1 = new StringBuilder("   ");
        StringBuilder sb2 = new StringBuilder("  ");
        StringBuilder sb3 = new StringBuilder();
        for (int i = 0; i < this.cols; i++) {
            sb1.append(i).append(" ");
            sb2.append("--");
        }
        sb1.append("\n");
        sb2.append("\n");
        sb3.append(sb1);
        sb3.append(sb2);
        for (int j = 0; j < this.rows; j++) {
            StringBuilder sb = new StringBuilder();
            sb.append(j).append("|");
            for (int i = 0; i < this.cols; i++) {
                sb.append(" ").append(this.boardChars[i][j]);
            }
            sb.append("\n");
            sb3.append(sb);
        }
        sb3.append("\n");
        return sb3.toString();
    }

    /**
     * A less pretty board-to-string function
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int y = this.rows;
        int x = this.cols;
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                if (j == x - 1) {
                    sb.append(this.boardChars[j][i]);
                } else {
                    sb.append(this.boardChars[j][i]).append(" ");
                }
            }
            if (i != y - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * @return the number of rows for this configuration
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * @return the number of cols for this configuration
     */
    public int getCols() {
        return this.cols;
    }

    /**
     * Given the array of cars in the configuration and state information for each car, the
     * 2d character array 'board' is constructed
     * @param cars list of all the cars in the game
     * @return the character board
     */
    private char[][] makeBoard(Car[] cars) {
        char[][] b = new char[this.cols][this.rows];
        for (int x = 0; x < this.cols; x++) {
            for (int y = 0; y < this.rows; y++) {
                b[x][y] = EMPTY;
            }
        }
        for (Car c : cars) {
            int startR = c.getsR();
            int startC = c.getsC();
            int endR = c.geteR();
            int endC = c.geteC();

            if (startR == endR) {
                if (endC - startC > 1) {
                    for (int i = startC; i <= endC; i++) {
                        b[i][startR] = c.getID();
                    }
                } else {
                    b[startC][startR] = c.getID();
                    b[endC][endR] = c.getID();
                }
            }
            if (startC == endC) {
                if (endR - startR > 1) {
                    for (int i = startR; i <= endR; i++) {
                        b[startC][i] = c.getID();
                    }
                } else {
                    b[startC][startR] = c.getID();
                    b[endC][endR] = c.getID();
                }
            }
        }
        return b;
    }

    /**
     * Hash function for a configuration
     * @return unique int for every board
     */
    @Override
    public int hashCode() {
        int x = 0;
        for (int i = 0; i < this.cols; i++) {
            x += Arrays.hashCode(this.boardChars[i]);
            for (int j = 0; j < this.rows; j++) {
                char z = this.boardChars[i][j];
                x += z * (i + 1) * (j + 1);
            }
        }
        return x;
    }

    /**
     * Check that a selection is made on the board and that it is not an empty cell
     * @param x x-coordinate of selection
     * @param y y-coordinate of selection
     * @return true if legal, otherwise false
     */
    public boolean isValidSelection(int x, int y) {
        if (x >= 0 && x < this.cols && y >= 0 && y < this.rows) {
            return (this.boardChars[x][y] != EMPTY);
        }
        return false;
    }

    /**
     * Check if two configs are equal
     * @param obj thing to compare to current config
     * @return true if equal, else false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JamConfig o) {
            return (this.hashCode() == o.hashCode());
        }
        return false;
    }

    /**
     * Checks that a user-made move from the GUI is legal
     * @param x1 x-coordinate of first selection
     * @param y1 y-coordinate of first selection
     * @param x2 x-coordinate of second selection
     * @param y2 y-coordinate of second selection
     * @return null if the move is not valid, or if it is valid, return the resulting configuration
     */
    public JamConfig isValidMove(int x1, int y1, int x2, int y2) {
        if (x2 >= 0 && x2 < this.cols && y2 >= 0 && y2 < this.rows) {
            char x = this.boardChars[x1][y1];
            Car theCar = null;
            for (Car c : this.cars) {
                if (c.getID() == x) {
                    theCar = c;
                }
            }
            if (theCar != null) {
                if (this.boardChars[x2][y2] == EMPTY) {
                    if (theCar.isVertCar()){
                        if (x1 == x2 && y1 != y2){
                            if (y1 > y2){
                                if (theCar.getsR() == y2 + 1){
                                    theCar.setR(-1);
                                } else {
                                    return null;
                                }
                            } else {
                                if (theCar.geteR() == y2 - 1){
                                    theCar.setR(1);
                                } else {
                                    return null;
                                }
                            }
                        }
                    } else {
                        if (y1 == y2 && x1 != x2){
                            if (x1 > x2){
                                if (theCar.getsC() == x2 + 1){
                                    theCar.setC(-1);
                                } else {
                                    return null;
                                }
                            } else {
                                if (theCar.geteC() == x2 - 1){
                                    theCar.setC(1);
                                } else {
                                    return null;
                                }
                            }
                        }
                    }
                    Car[] newCars = this.cars.clone();
                    for (int i = 0; i< newCars.length; i++) {
                        if (newCars[i].getID() == theCar.getID()) {
                            newCars[i] = theCar;
                        }
                    }
                    return new JamConfig(this.cols, this.rows, newCars);
                }
            }
        }
        return null;
    }
}