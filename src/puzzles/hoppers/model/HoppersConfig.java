package puzzles.hoppers.model;
import puzzles.common.solver.Configuration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * HopperConfig class representing a single legal board position
 * @author Kilian Jakstis
 */
public class HoppersConfig implements Configuration {
    /** Possible states for potential moves */
    public enum moveState {BLOCKED, VALID, INVALID, NOT_LEGAL}
    /** Character representation of game spaces */
    public static final char VALID = '.';
    public static final char INVALID = '*';
    public static final char GREEN = 'G';
    public static final char RED = 'R';
    /** the number of rows and columns in the board */
    public int rows;
    public int cols;
    /** 2d character array for storing the board position */
    public char[][] board;

    /**
     * Constructor for HopperConfig when based on a file input
     * Initializes board, rows, and cols
     * @param filename - the file which contains the game board
     */
    public HoppersConfig(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String[] s = br.readLine().split("\s++");
            int r = Integer.parseInt(s[0]);
            int c = Integer.parseInt(s[1]);
            this.rows = r;
            this.cols = c;
            this.board = new char[c][r];
            int i = 0;
            String line = br.readLine();
            while (line != null) {
                s = line.split("\s++");
                for (int j = 0; j < this.cols; j++) {
                    this.board[j][i] = s[j].charAt(0);
                }
                i++;
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for HopperConfig when based on a 2d character board
     * Used to generate new configurations
     * @param board - the board state
     * @param r - number of rows
     * @param c - number of cols
     */
    public HoppersConfig(char[][] board, int r, int c) {
        this.board = board;
        this.rows = r;
        this.cols = c;
    }

    /**
     * Provides a copy of the current board - used for generation of neighbors
     * @return the copy
     */
    public char[][] copyBoard() {
        char[][] copy = new char[this.cols][this.rows];
        for (int i = 0; i < this.cols; i++) {
            copy[i] = this.board[i].clone();
        }
        return copy;
    }

    /**
     * Is the object equal to this HopperConfig?
     * @param obj the other object
     * @return true if they're hopperConfigs with the same board position - else false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HoppersConfig o) {
            return (this.hashCode() == o.hashCode());
        }
        return false;
    }

    /**
     * Hashes the config
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        int x = 0;
        for (int i = 0; i < this.cols; i++) {
            x += Arrays.hashCode(this.board[i]);
            for (int j = 0; j < this.rows; j++) {
                if (this.board[i][j] == GREEN){
                    x += (i + 1) * (j + 1);
                }
            }
        }
        return x;
    }

    /**
     * ToString for a HopperConfig
     * @return the board as text
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final int y = this.rows;
        final int x = this.cols;
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                if (j == x - 1) {
                    sb.append(this.board[j][i]);
                } else {
                    sb.append(this.board[j][i]).append(" ");
                }
            }
            if (i != y - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Is there only one red frog left with no green frogs left?
     * @return yes? Return true. No? return false.
     */
    @Override
    public boolean isSolution() {
        int reds = 0;
        int greens = 0;
        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                char c = this.board[i][j];
                if (c == GREEN) {
                    greens++;
                }
                if (c == RED) {
                    reds++;
                }
            }
        }
        return (reds == 1 && greens == 0);
    }

    /**
     * So many if statements...
     * Generates the legal neighbor configurations based on the Hopper movement rules
     * @return the arrayList of these resulting configs
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        // I feel like this is gonna be the longest method I've written in java unfortunately
        ArrayList<Configuration> neighbors = new ArrayList<>();
        final int cols = this.cols;
        final int rows = this.rows;
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                char current = this.board[x][y];
                if (current == INVALID || current == VALID) {
                    continue;
                }

                // horizontal moves
                if (x - 4 >= 0) {
                    if (this.board[x - 2][y] == GREEN) {
                        if (this.board[x - 4][y] == VALID) {
                            char[][] copy = this.copyBoard();
                            copy[x - 4][y] = current;
                            copy[x - 2][y] = VALID;
                            copy[x][y] = VALID;
                            HoppersConfig neighbor = new HoppersConfig(copy, this.rows, this.cols);
//                            System.out.println(neighbor + "\n");
                            neighbors.add(neighbor);
                        }
                    }
                }
                if (x + 4 < cols) {
                    if (this.board[x + 2][y] == GREEN) {
                        if (this.board[x + 4][y] == VALID) {
                            char[][] copy = this.copyBoard();
                            copy[x + 4][y] = current;
                            copy[x + 2][y] = VALID;
                            copy[x][y] = VALID;
                            HoppersConfig neighbor = new HoppersConfig(copy, this.rows, this.cols);
//                            System.out.println(neighbor + "\n");
                            neighbors.add(neighbor);
                        }
                    }
                }

                // vertical moves
                if (y - 4 >= 0) {
                    if (this.board[x][y - 2] == GREEN) {
                        if (this.board[x][y - 4] == VALID) {
                            char[][] copy = this.copyBoard();
                            copy[x][y - 4] = current;
                            copy[x][y - 2] = VALID;
                            copy[x][y] = VALID;
                            HoppersConfig neighbor = new HoppersConfig(copy, this.rows, this.cols);
//                            System.out.println(neighbor + "\n");
                            neighbors.add(neighbor);
                        }
                    }
                }
                if (y + 4 < rows) {
                    if (this.board[x][y + 2] == GREEN) {
                        if (this.board[x][y + 4] == VALID) {
                            char[][] copy = this.copyBoard();
                            copy[x][y + 4] = current;
                            copy[x][y + 2] = VALID;
                            copy[x][y] = VALID;
                            HoppersConfig neighbor = new HoppersConfig(copy, this.rows, this.cols);
//                            System.out.println(neighbor + "\n");
                            neighbors.add(neighbor);
                        }
                    }
                }

                // diagonal moves
                if (x - 2 >= 0 && y - 2 >= 0) {
                    if (this.board[x - 1][y - 1] == GREEN) {
                        if (this.board[x - 2][y - 2] == VALID) {
                            char[][] copy = this.copyBoard();
                            copy[x - 2][y - 2] = current;
                            copy[x - 1][y - 1] = VALID;
                            copy[x][y] = VALID;
                            HoppersConfig neighbor = new HoppersConfig(copy, this.rows, this.cols);
                            neighbors.add(neighbor);
//                            System.out.println(neighbor + "\n");
                        }
                    }
                }
                if (x - 2 >= 0 && y + 2 <= rows - 1) {
                    if (this.board[x - 1][y + 1] == GREEN) {
                        if (this.board[x - 2][y + 2] == VALID) {
                            char[][] copy = this.copyBoard();
                            copy[x - 2][y + 2] = current;
                            copy[x - 1][y + 1] = VALID;
                            copy[x][y] = VALID;
                            HoppersConfig neighbor = new HoppersConfig(copy, this.rows, this.cols);
                            neighbors.add(neighbor);
//                            System.out.println(neighbor + "\n");
                        }
                    }
                }
                if (x + 2 < cols && y - 2 >= 0) {
                    if (this.board[x + 1][y - 1] == GREEN) {
                        if (this.board[x + 2][y - 2] == VALID) {
                            char[][] copy = this.copyBoard();
                            copy[x + 2][y - 2] = current;
                            copy[x + 1][y - 1] = VALID;
                            copy[x][y] = VALID;
                            HoppersConfig neighbor = new HoppersConfig(copy, this.rows, this.cols);
                            neighbors.add(neighbor);
//                            System.out.println(neighbor + "\n");
                        }
                    }
                }
                if (x + 2 < cols && y + 2 < rows) {
                    if (this.board[x + 1][y + 1] == GREEN) {
                        if (this.board[x + 2][y + 2] == VALID) {
                            char[][] copy = this.copyBoard();
                            copy[x + 2][y + 2] = current;
                            copy[x + 1][y + 1] = VALID;
                            copy[x][y] = VALID;
                            HoppersConfig neighbor = new HoppersConfig(copy, this.rows, this.cols);
                            neighbors.add(neighbor);
//                            System.out.println(neighbor + "\n");
                        }
                    }
                }
            }
        }
        // welp it definitely was
        return neighbors;
    }

    /**
     * A special to toString used by the PTUI
     * Just adds numbers to label to the rows and columns
     * @return that^^
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
                sb.append(" ").append(this.board[i][j]);
            }
            sb.append("\n");
            sb3.append(sb);
        }
        sb3.append("\n");
        return sb3.toString();
    }

    /*
     * if you're already here, you may as well take a sec to appreciate this swag little text frog I stole from someone
     *             _     _
     *            (')-=-(')
     *          __(   "   )__
     *         / _/'-----'\_ \
     *      ___\\ \\     // //___
     *      >____)/_\---/_\(____<
     *
     * Sorry, I like frogs I had to do this
     */

    /**
     * For the first selected point: Is the currently selected point a frog?
     * @param x - column of the point
     * @param y - row of the point
     * @return True if it is a frog, False if not
     *
     */
    public moveState isValidSelection(int x, int y) {
        if (x >= 0 && y >= 0 && x < this.cols && y < this.rows) {
            if (this.board[x][y] == RED || this.board[x][y] == GREEN) {
                return moveState.VALID;
            } else {
                return moveState.INVALID;
            }
        }
        return moveState.NOT_LEGAL;
    }

    /**
     * 118-line story short, checks to see if the move from selected point (c1, r1) to (c2, r2) is legal
     * and returns the appropriate moveState
     * Unfortunately, I'm too lazy to break this into several methods
     * @param c1 - col of point one
     * @param r1 - row of point one
     * @param c2 - col of point two
     * @param r2 - row of point two
     * @return corresponding moveState value
     */
    public moveState isValidMove(int c1, int r1, int c2, int r2) {
        if (c2 >= 0 && r2 >= 0 && c2 < this.cols && r2 < this.rows) {
            if (c1 == c2){
              if (r1 > r2){
                  if (r1-4 != r2){
                      return moveState.INVALID;
                  }
              }
              if (r2 > r1){
                  if (r2-4 != r1){
                      return moveState.INVALID;
                  }
              }
          }
            if (r1 == r2){
                if (c1 > c2){
                    if (c1-4 != c2){
                        return moveState.INVALID;
                    }
                }
                if (c2 > c1){
                    if (c2-4 != c1){
                        return moveState.INVALID;
                    }
                }
            }
            if (r1 != r2 && c1 != c2){
                if (r1 > r2 && c1 > c2){
                    if (r1 - 2 != r2 || c1 - 2 != c2){
                        return moveState.INVALID;
                    }
                }
                if (r1 > r2 && c2 > c1){
                    if (r1 - 2 != r2 || c2 - 2 != c1){
                        return moveState.INVALID;
                    }
                }
                if (r2 > r1 && c1 > c2){
                    if (r2 - 2 != r1 || c1 - 2 != c2){
                        return moveState.INVALID;
                    }
                }
                if (r2 > r1 && c2 > c1){
                    if (r2 - 2 != r1 || c2 - 2 != c1){
                        return moveState.INVALID;
                    }
                }
            }
            char x = this.board[c2][r2];
            if (x == INVALID) {
                return moveState.INVALID;
            } else if (x == GREEN || x == RED) {
                return moveState.BLOCKED;
            }

            else {

                if (r1 == r2) {
                    if (c1 > c2) {
                        if (this.board[c1 - 2][r1] == HoppersConfig.GREEN) {
                            return moveState.VALID;
                        } else {
                            return moveState.INVALID;
                        }
                    } else {
                        if (this.board[c2 - 2][r1] == HoppersConfig.GREEN) {
                            return moveState.VALID;
                        } else {
                            return moveState.INVALID;
                        }
                    }
                }

                if (c1 == c2) {
                        if (r1 > r2) {
                            if (this.board[c1][r1-2] == HoppersConfig.GREEN) {
                                return moveState.VALID;
                            } else {
                                return moveState.INVALID;
                            }
                        } else {
                            if (this.board[c1][r2-2] == HoppersConfig.GREEN) {
                                return moveState.VALID;
                            } else {
                                return moveState.INVALID;
                            }
                        }
                    }

                if (r1 > r2 && c1 > c2) {
                    if (this.board[c1 - 1][r1 - 1] == GREEN) {
                        return moveState.VALID;
                    } else {
                        return moveState.INVALID;
                    }
                }
                if (r1 > r2) {
                    if (this.board[c2 - 1][r1 - 1] == GREEN) {
                        return moveState.VALID;
                    } else {
                        return moveState.INVALID;
                    }
                }
                if (c1 > c2) {
                    if (this.board[c1 - 1][r2 - 1] == GREEN) {
                        return moveState.VALID;
                    } else {
                        return moveState.INVALID;
                    }
                }
                if (this.board[c2 - 1][r2 - 1] == GREEN) {
                    return moveState.VALID;
                }

            }
        }
        return moveState.NOT_LEGAL;
    }
}
