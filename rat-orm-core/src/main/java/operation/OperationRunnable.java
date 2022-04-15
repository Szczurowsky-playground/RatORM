package operation;

import java.util.HashMap;

/**
 * Child of runnable, used to run operations.
 */
public interface OperationRunnable extends Runnable {

    void execute(HashMap<String, Object> options);

}
