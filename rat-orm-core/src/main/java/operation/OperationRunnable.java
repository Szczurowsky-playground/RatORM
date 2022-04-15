package operation;

import java.util.HashMap;

public interface OperationRunnable extends Runnable {

    void execute(HashMap<String, Object> options);

}
