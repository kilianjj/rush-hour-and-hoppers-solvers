package puzzles.jam.model;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Model for Traffic Jam game
 * @author Kilian Jakstis
 */
public class JamModel {

    /**
     * the collection of observers of this model
     */
    private final List<Observer<JamModel, String>> observers = new LinkedList<>();

    public JamConfig currentConfig; // holds the current configuration of the game
    public JamConfig startConfig; // holds the initial configuration of the game for resetting purposes
    private final Solver solver; // holds the solver instance
    public Integer selectedY = -1; // holds the selected X and Y coordinates when a car is selected on the GUI
    public Integer selectedX = -1; // ^

    /**
     * Creates the model and instantiates the solver field
     */
    public JamModel() {
        this.solver = new Solver();
    }

    /**
     * The view calls this to add itself as an observer.
     * Adds the passed-in class to the Model's observer list
     * @param observer the observing class
     */
    public void addObserver(Observer<JamModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed, so inform the observers via
     * the update method
     * param msg: the msg string describing how the view should update
     */
    private void alertObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }

    /**
     * Load the Jam puzzle file, set initial game states, alert observers to changes
     * Creates the Jam configeration from the given file
     * @param filename the jam file
     */
    public void load(String filename) {
        this.selectedX = -1;
        this.selectedY = -1;
        this.startConfig = new JamConfig(filename);
        this.currentConfig = startConfig;
        String[] s = filename.split("/");
        if (s.length == 3) {
            this.alertObservers("Loaded: " + s[2] + "\n" + startConfig.specialPrint() + "\n");
        } else {
            this.alertObservers("Loaded: " + filename + "\n" + startConfig.specialPrint() + "\n");
        }
    }

    /**
     * Reset the current Jam configuration to the original configuration, alert observers
     */
    public void reset() {
        this.currentConfig = this.startConfig;
        this.alertObservers("Puzzle reset: " + "\n" + startConfig.specialPrint() + "\n");
    }

    /**
     * Attempts to generate the next move needed to solve the puzzle in its current position. If the solution is
     * solvable, generates the list of moves to solve the position and sets the current configuration to the first
     * new configuration. Observers are then updated.
     *
     * If the position has no solution or is already solved, the observers are updated appropriately.
     */
    public void hint() {
        ArrayList<Configuration> path = (ArrayList<Configuration>) this.solver.findPath(this.currentConfig);
        if (path == null) {
            this.alertObservers("No solution found.");
            return;
        }
        if (this.currentConfig.isSolution()) {
            this.alertObservers("Already solved!");
        } else {
            JamConfig h = (JamConfig) path.get(1);
            this.currentConfig = h;
            this.alertObservers("Next step: " + "\n" + h.specialPrint() + "\n");
        }
    }

    /**
     * Handles the selection and movement of cars
     * @param x the column of the selected button
     * @param y the row of the selected button
     */
    public void select(int x, int y) {
        // checks if there is not a previous selection, in which case, we save the selected point
        if (selectedX == -1 && selectedY == -1) {
            boolean runningOutOfNames = this.currentConfig.isValidSelection(x, y);
            if (runningOutOfNames) {
                this.selectedX = x;
                this.selectedY = y;
                this.alertObservers("Selected (" + x + ", " + y + ")" + "\n" + this.currentConfig.specialPrint() + "\n");
            } else {
                this.alertObservers("No car at (" + x + ", " + y + ")!");
            }
        } else {
            // handles if there was a previous selection; checks that the proposed move is legal and if it is, changes
            // the current configuration
            JamConfig isValidMove = this.currentConfig.isValidMove(this.selectedX, this.selectedY, x, y);
            if (isValidMove != null){
                // the move is legal
                this.currentConfig = isValidMove;
                this.alertObservers("Moved from (" + this.selectedX + ", " + this.selectedY + ") to ("
                        + x + ", " + y + ")." + "\n" + this.currentConfig.specialPrint());

            } else {
                // the move is not legal
                this.alertObservers("Not a legal move");
            }
            this.selectedX = -1;
            this.selectedY = -1;
        }
    }
}
