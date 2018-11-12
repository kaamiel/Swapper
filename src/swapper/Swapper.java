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
            Set<E> toRemove = new HashSet<>(removed);
            Set<E> toAdd = new HashSet<>(added);
            try {
                while (!set.containsAll(toRemove)) {
                    wait();
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                Set<E> backup = new HashSet<>(set);

                set.removeAll(toRemove);
                set.addAll(toAdd);
                if (Thread.interrupted()) {
                    set = backup;
                    throw new InterruptedException();
                }
            } finally {
                notifyAll();
            }
        }
    }
}