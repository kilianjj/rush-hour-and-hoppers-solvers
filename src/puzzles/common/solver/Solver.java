package puzzles.common.solver;
import java.util.*;

/**
 * BFS common solver class
 * Generates and returns the shortest path
 * Provides stats for the number of configurations
 * @author Kilian Jakstis
 */
public class Solver {

    /**
     * Fields for tracking configuration stats
     */
    private int uniqueConfigs = 0;
    private int totalConfigs = 0;

    /**
     * Finds the shortest path from a start Configuration to a solved Configuration
     * @param start config
     * @return Path Collection of Configurations detailing the path
     */
    public Collection<Configuration> findPath(Configuration start) {
        // if start is a solution:
        if (start.isSolution()) {
            ArrayList<Configuration> thing = new ArrayList<>();
            thing.add(start);
            uniqueConfigs++;
            totalConfigs++;
            return thing;
        }
        // make predecessor map and to-visit queue
        Map<Configuration, Configuration> visited = new HashMap<>();
        ArrayDeque<Configuration> queue = new ArrayDeque<>();
        visited.put(start, null);
        queue.add(start);
        // start generating new configurations
        Configuration currentPoint;
        while (!queue.isEmpty() && !queue.peek().isSolution()){
            currentPoint = queue.remove();
            Collection<Configuration> neighbors = currentPoint.getNeighbors();
            for (Configuration config : neighbors){
                totalConfigs++;
                if (!visited.containsKey(config)){
                    queue.add(config);
                    uniqueConfigs++;
                    visited.put(config, currentPoint);
                }
            }
        }
        if (queue.isEmpty()) return null;
        else {
            ArrayList<Configuration> path = new ArrayList<>();
            Configuration endConfig = (queue.peek());
            path.add(endConfig);
            Configuration config = visited.get(endConfig);
            while (config != null ) {
                path.add(config);
                config = visited.get(config);
            }
            ArrayList<Configuration> pathCorrect = new ArrayList<>();
            for (int i = path.size()-1; i>=0; i--){
                pathCorrect.add(path.get(i));
            }
            totalConfigs++;
            return pathCorrect;
        }
    }

    /**
     * @return the number of unique configurations generated
     */
    public int getUniqueConfigs(){
        return uniqueConfigs;
    }

    /**
     * @return the total number of total configurations generated
     */
    public int getTotalConfigs() {
        return totalConfigs;
    }
}

