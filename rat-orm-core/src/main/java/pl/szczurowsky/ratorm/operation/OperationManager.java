package pl.szczurowsky.ratorm.operation;

import java.util.LinkedList;
import java.util.List;

public class OperationManager {

    protected List<Operation> operations = new LinkedList<>();
    protected List<Thread> threads = new LinkedList<>();


    public int execute(Operation operation) {
        Thread thread = new Thread(operation);
        threads.add(thread);
        operations.add(operation);
        operation.setId(threads.size() - 1);
        thread.setName(String.valueOf(threads.size() - 1));
        thread.start();
        return threads.size() - 1;
    }

    public void cancel(Operation operation) {
        threads.get(operation.getId()).interrupt();
        threads.remove(operation);
        operations.remove(operation);
    }

    public void cancel(int id) {
        threads.get(id).interrupt();
        threads.remove(id);
        operations.remove(id);
    }

    public void cancelAll() {
        for (Thread thread : threads) {
            thread.interrupt();
            threads.remove(thread);
            operations.remove(Integer.parseInt(thread.getName()));
        }
    }

}