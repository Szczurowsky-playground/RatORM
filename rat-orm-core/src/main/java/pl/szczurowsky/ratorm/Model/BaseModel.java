package pl.szczurowsky.ratorm.Model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BaseModel {

    ReadWriteLock lock = new ReentrantReadWriteLock();
    Lock writeLock = lock.writeLock();

    public void lockWrite() {
        writeLock.lock();
    }

    public void unlockWrite() {
        writeLock.unlock();
    }

}
