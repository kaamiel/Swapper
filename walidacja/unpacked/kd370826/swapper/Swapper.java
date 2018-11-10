package swapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class Swapper<E> {

    private Set<E> set;

    public Swapper() {
        set = new HashSet<>();
    }

    public void swap(Collection<E> removed, Collection<E> added) throws InterruptedException {
        synchronized (this) {
            while (!set.containsAll(removed)) {
                wait();
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            set.removeAll(removed);
            set.addAll(added);
            if (!added.isEmpty()) {
                notifyAll();
            }
        }
    }
}