package puzzles.hoppers.model;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.*;

/**
 * Model for regulating Hopper Game
 * @author Kilian jakstis
 */
public class HoppersModel {

    /** the collection of observers of this model */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();
    /** the current configuration */
    public HoppersConfig currentConfig;
    /** first configuration for the specific file */
    public HoppersConfig startConfig;
    /** Solver for computing hints */
    private final Solver solver;

    /**
     * contains the valid first selection coordinates
     */
    public Integer selectedRow = -1;
    public Integer selectedCol = -1;

    /**
     * Constructor for the model
     * Instantiates the solver
     */
    public HoppersModel(){
        this.solver = new Solver();
    }

    /**
     * Loads the inputted fie
     * Resets any current selected point, resets the start and current configurations, and alerts view
     * @param filename - file for the Hopper game
     */
    public void load (String filename){
        this.selectedCol = -1;
        this.selectedRow = -1;
        this.startConfig = new HoppersConfig(filename);
        this.currentConfig = startConfig;
        String[] s = filename.split("/");
        if (s.length == 3){
            this.alertObservers("Loaded: " + s[2] + "\n" + startConfig.specialPrint() + "\n");
        } else {
            this.alertObservers("Loaded: " + filename + "\n" + startConfig.specialPrint() + "\n");
        }
    }

    /**
     * Resets the puzzle by setting current configuration to the starting one
     * Resets any currently selected point
     */
    public void reset(){
        this.currentConfig = this.startConfig;
        this.selectedRow = -1;
        this.selectedCol = -1;
        this.alertObservers("Puzzle reset: " + "\n" + startConfig.specialPrint() + "\n");
    }

    /**
     * Uses the solver to solve the current configuration - if possible - and displays this next configuration to the
     * view. If the puzzle is already solved or no solution is found, the appropriate message is relayed to the view.
     */
    public void hint(){
        ArrayList<Configuration> path = (ArrayList<Configuration>) this.solver.findPath(this.currentConfig);
        if (path == null){
            this.alertObservers("No solution found.");
            return;
        }
        if (this.currentConfig.isSolution()){
            this.alertObservers("Already solved!");
        } else {
            HoppersConfig h = (HoppersConfig) path.get(1);
            this.currentConfig = h;
            this.alertObservers("Next step: " + "\n" + h.specialPrint() + "\n");
        }
    }

    /**
     * Handles the selection and movement according to the user
     * If the selection is legal, the coordinates of this point are stored
     * Upon the input of a second legal selection, the movement will be determined as legal or illegal
     * If the move is legal, the resulting configuration will be displayed to the view
     * Otherwise, the selected points are reset
     * @param row - row of selection
     * @param col - column of selection
     */
    public void select(String row, String col) {
        int r = Integer.parseInt(row);
        int c = Integer.parseInt(col);
        if (selectedCol == -1 && selectedRow == -1){
            HoppersConfig.moveState runningOutOfNames = this.currentConfig.isValidSelection(c, r);
            if (runningOutOfNames == HoppersConfig.moveState.VALID) {
                this.selectedCol = c;
                this.selectedRow = r;
                this.alertObservers("Selected (" + r + ", " + c + ")" + "\n" + this.currentConfig.specialPrint() + "\n");
            } else if (runningOutOfNames == HoppersConfig.moveState.INVALID){
                this.alertObservers("No frog at (" + c + ", "+ r + "). \n" + this.currentConfig.specialPrint() + "\n");
            }
            else {
                this.alertObservers("This is not a legal selection.");
            }
        }

        else {
            HoppersConfig.moveState state = this.currentConfig.isValidMove(this.selectedCol, this.selectedRow, c, r);
            if (state == HoppersConfig.moveState.BLOCKED){
                alertObservers("Can't jump from (" + this.selectedRow + ", " + this.selectedCol + ") to (" + r +
                        ", " + c + ")! " +
                        "Spot is blocked by another frog!" + "\n" + this.currentConfig.specialPrint() + "\n");
                this.selectedCol = -1;
                this.selectedRow = -1;
            }
            if (state == HoppersConfig.moveState.NOT_LEGAL){
                this.alertObservers("This spot is not on the board!" + "\n" + this.currentConfig.specialPrint() + "\n");
                this.selectedCol = -1;
                this.selectedRow = -1;
            }
            if (state == HoppersConfig.moveState.INVALID){
                this.alertObservers("Can't jump from (" + this.selectedCol + ", " + this.selectedRow + ") to (" +
                c + ", " + r + ")!" + "\n" + this.currentConfig.specialPrint() + "\n");
                this.selectedCol = -1;
                this.selectedRow = -1;
            }

            if (state == HoppersConfig.moveState.VALID){
                char x = this.currentConfig.board[this.selectedCol][this.selectedRow];
                char[][] hi = this.currentConfig.copyBoard();
                hi[this.selectedCol][this.selectedRow] = HoppersConfig.VALID;

                if (r == this.selectedRow){
                    if (this.selectedCol > c){
                        hi[this.selectedCol-2][r] = HoppersConfig.VALID;
                    } else {
                        hi[c-2][r] = HoppersConfig.VALID;
                    }
                }
                if (c == this.selectedCol){
                    if (this.selectedRow > r){
                        hi[c][this.selectedRow-2] = HoppersConfig.VALID;
                    } else {
                        hi[c][r-2] = HoppersConfig.VALID;
                    }
                }

                if (r != this.selectedRow && c != this.selectedCol){
                    if (r > this.selectedRow && c > this.selectedCol){
                        hi[c-1][r-1] = HoppersConfig.VALID;
                    }
                    if (r > this.selectedRow && c < this.selectedCol){
                        hi[this.selectedCol-1][r-1] = HoppersConfig.VALID;
                    }
                    if (r < this.selectedRow && c > this.selectedCol){
                        hi[c-1][this.selectedRow-1] = HoppersConfig.VALID;
                    }
                    if (r < this.selectedRow && c < this.selectedCol){
                        hi[this.selectedCol - 1][this.selectedRow - 1] = HoppersConfig.VALID;
                    }
                }
                hi[c][r] = x;
                this.currentConfig = new HoppersConfig(hi, this.currentConfig.rows, this.currentConfig.cols);
                this.alertObservers("Jumped from (" + this.selectedRow + ", " + this.selectedCol + ") to ("
                        + r + ", " + c + ")." + "\n" + this.currentConfig.specialPrint());
                this.selectedRow = -1;
                this.selectedCol = -1;
            }
        }
    }

    /**
     * The view calls this to add itself as an observer.
     * @param observer the view
     */
    public void addObserver(Observer<HoppersModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }
}
