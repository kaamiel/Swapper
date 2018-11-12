package swapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducenciKonsumenci {

    private static final int DODATNI = 10;
    private static final int UJEMNI = 10;
    private static final int PRODUKOWANE = 5000;
    private static final int KONSUMENCI = 5;
    // (DODATNI + UJEMNI) * PRODUKOWANE % KONSUMENCI == 0
    private static final int KONSUMOWANE = ((DODATNI + UJEMNI) * PRODUKOWANE / KONSUMENCI);

    private static final int ROZMIAR = 10;

    private static final int[] bufor = new int[ROZMIAR];

    private static Swapper<Integer> zajęte = new Swapper<>();
    private static Swapper<Integer> wolne = new Swapper<>();

    private static AtomicInteger pierwszaZajęta = new AtomicInteger(0);
    private static AtomicInteger pierwszaWolna = new AtomicInteger(0);

    static {
        try {
            for (int i = 0; i < ROZMIAR; ++i) {
                wolne.swap(Collections.emptySet(), Collections.singleton(i));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    private static int get() throws InterruptedException {
        int i = pierwszaZajęta.getAndUpdate(j -> (j + 1) % ROZMIAR);
        zajęte.swap(Collections.singleton(i), Collections.emptySet());
        int res = bufor[i];
        wolne.swap(Collections.emptySet(), Collections.singleton(i));
        return res;
    }

    private static void put(int x) throws InterruptedException {
        int i = pierwszaWolna.getAndUpdate(j -> (j + 1) % ROZMIAR);
        wolne.swap(Collections.singleton(i), Collections.emptySet());
        bufor[i] = x;
        zajęte.swap(Collections.emptySet(), Collections.singleton(i));
    }

    private static class Producent implements Runnable {

        private final int mój;

        public Producent(int mój) {
            this.mój = mój;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < PRODUKOWANE; ++i) {
                    put(mój);
                }
            } catch (InterruptedException e) {
                Thread t = Thread.currentThread();
                t.interrupt();
                System.err.println(t.getName() + " przerwany");
            }
        }

    }

    private static class Konsument implements Runnable {

        @Override
        public void run() {
            Thread t = Thread.currentThread();
            try {
                int suma = 0;
                int pobrane = 0;
                for (int i = 0; i < KONSUMOWANE; ++i) {
                    int x = get();
                    suma += x;
                    ++pobrane;
                }
                System.out.println(t.getName() + " pobrał: " + pobrane + ", suma: " + suma);
            } catch (InterruptedException e) {
                t.interrupt();
                System.err.println(t.getName() + " przerwany");
            }
        }
    }

    public static void main(String args[]) {
        List<Thread> wątki = new ArrayList<>();
        for (int i = 0; i < DODATNI; ++i) {
            Thread t = new Thread(new Producent(1), "Dodatni" + i);
            wątki.add(t);
        }
        for (int i = 0; i < UJEMNI; ++i) {
            Thread t = new Thread(new Producent(-1), "Ujemny" + i);
            wątki.add(t);
        }
        for (int i = 0; i < KONSUMENCI; ++i) {
            Thread t = new Thread(new Konsument(), "Konsument" + i);
            wątki.add(t);
        }
        for (Thread t : wątki) {
            t.start();
        }
        try {
            for (Thread t : wątki) {
                t.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Główny przerwany");
        }
    }
}
