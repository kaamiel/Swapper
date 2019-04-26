package swapper;

import java.util.*;

public class CzytelnicyPisarze {

    private static final int CZYTELNICY = 10;
    private static final int PISARZE = 3;

    private static final int CZYTANIE = 10;
    private static final int PISANIE = 4;

    private static final int CZAS_CZYTANIA = 200;
    private static final int CZAS_PISANIA = 100;

    private enum MUTEX {
        M
    }

    private static int iluCzyta = 0, iluPisze = 0, czekaCzyt = 0, czekaPis = 0;
    private static Swapper<MUTEX> mutex = new Swapper<>();
    private static Swapper<Integer> czytelnicy = new Swapper<>();
    private static Swapper<Integer> pisarze = new Swapper<>();

    private static List<Character> bufor = new ArrayList<>();

    static {
        try {
            mutex.swap(Collections.emptySet(), Collections.singleton(MUTEX.M));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    private static void śpij(long czas) {
        try {
            Thread.sleep(czas);
        } catch (InterruptedException e) {
            Thread t = Thread.currentThread();
            t.interrupt();
            System.err.println(t.getName() + " przerwany");
        }
    }

    private static void czytanie(int nr) {
        System.out.println("\nCzytam (" + nr + ")... " + bufor + "\nRazem ze mną pisze: " +
                iluPisze + " pisarzy, czyta: " + (iluCzyta - 1) + " czytelników");
        śpij(CZAS_CZYTANIA);
        System.out.println("Skończyłem czytać (" + nr + ").");
    }

    private static void pisanie(int nr, char next) {
        System.out.println("\nPiszę (" + nr + ")... " + next + "\nRazem ze mną pisze: " +
                (iluPisze - 1) + " pisarzy, czyta: " + iluCzyta + " czytelników");
        bufor.add(next);
        śpij(CZAS_PISANIA);
        System.out.println("Skończyłem pisać (" + nr + ").");
    }

    private static class Czytelnik implements Runnable {
        private int nr;

        public Czytelnik(int nr) {
            this.nr = nr;
        }

        @Override
        public void run() {
            for (int i = 0; i < CZYTANIE; ++i) {
                try {
                    mutex.swap(Collections.singleton(MUTEX.M), Collections.emptySet());

                    if (iluPisze + czekaPis > 0) {
                        ++czekaCzyt;
                        mutex.swap(Collections.emptySet(), Collections.singleton(MUTEX.M));
                        czytelnicy.swap(Collections.singleton(czekaCzyt), Collections.emptySet());
                        --czekaCzyt;
                    }
                    ++iluCzyta;
                    if (czekaCzyt > 0) {
                        czytelnicy.swap(Collections.emptySet(), Collections.singleton(czekaCzyt));
                    } else {
                        mutex.swap(Collections.emptySet(), Collections.singleton(MUTEX.M));
                    }

                    czytanie(nr);

                    mutex.swap(Collections.singleton(MUTEX.M), Collections.emptySet());
                    --iluCzyta;
                    if (iluCzyta == 0 && czekaPis > 0) {
                        pisarze.swap(Collections.emptySet(), Collections.singleton(czekaPis));
                    } else {
                        mutex.swap(Collections.emptySet(), Collections.singleton(MUTEX.M));
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Pisarz implements Runnable {
        private int nr;
        private static char last = 'a';

        public Pisarz(int nr) {
            this.nr = nr;
        }

        @Override
        public void run() {
            for (int i = 0; i < PISANIE; ++i) {
                try {
                    mutex.swap(Collections.singleton(MUTEX.M), Collections.emptySet());
                    if (iluPisze + iluCzyta > 0) {
                        ++czekaPis;
                        mutex.swap(Collections.emptySet(), Collections.singleton(MUTEX.M));
                        pisarze.swap(Collections.singleton(czekaPis), Collections.emptySet());
                        --czekaPis;
                    }
                    ++iluPisze;
                    mutex.swap(Collections.emptySet(), Collections.singleton(MUTEX.M));

                    pisanie(nr, last++);
                    if (last > 'z') {
                        last = 'a';
                    }

                    mutex.swap(Collections.singleton(MUTEX.M), Collections.emptySet());
                    --iluPisze;
                    if (czekaCzyt > 0) {
                        czytelnicy.swap(Collections.emptySet(), Collections.singleton(czekaCzyt));
                    } else if (czekaPis > 0) {
                        pisarze.swap(Collections.emptySet(), Collections.singleton(czekaPis));
                    } else {
                        mutex.swap(Collections.emptySet(), Collections.singleton(MUTEX.M));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) {

        List<Thread> wątki = new ArrayList<>();
        for (int i = 0; i < PISARZE; ++i) {
            wątki.add(new Thread(new Pisarz(i + 1), "Pisarz" + i));
        }
        for (int i = 0; i < CZYTELNICY; ++i) {
            wątki.add(new Thread(new Czytelnik(i + 1), "Czytelnik" + i));
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
