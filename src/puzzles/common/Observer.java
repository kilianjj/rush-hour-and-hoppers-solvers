package puzzles.common;

/**
 * An interface representing classes whose objects are to be notified
 * when objects they are observing are changed.
 * @param <Subject> the type of object an implementor is observing
 * @param <ClientData> data the model can send to the observer
 * @author Kilian Jakstis
 */
public interface Observer<Subject, ClientData> {

    /**
     * The observed class calls this method for all of its observers when it wishes to
     * alert them of a change in its state.
     * @param subject the object that alerts this object about a change in state
     * @param data optional data the model can tell the observer
     */
    void update(Subject subject, ClientData data);
}
