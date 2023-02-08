package puzzles.common.solver;
import java.util.Collection;

/**
 * Interface for configurations of either Traffic Jam or Hoppers
 * @author Kilian Jakstis
 */
public interface Configuration {

    /**
     * Check if a configuration is a solution
     * @return is the configuration a solution to the puzzle, T or F?
     */
    boolean isSolution();

    /**
     * Get all neighbor configurations to the current one
     * @return a collection of all such configs
     */
    Collection<Configuration> getNeighbors();

    /**
     * Do two configurations equal each other?
     * @param other the other config in question
     * @return true if they are identical, else false
     */
    boolean equals(Object other);

    /**
     * A function for hashing configurations
     * @return unique hash int for every unique position
     */
    int hashCode();

    /**
     * Make a string representation of the configuration
     * @return the string
     */
    String toString();
}
