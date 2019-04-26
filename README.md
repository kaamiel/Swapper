# Swapper

## Wprowadzenie

Swapper to zbiór, na którym można wykonać niepodzielną operację usunięcia a następnie dodania obiektów.

W pakiecie `swapper`, implementującym w Javie swapper wartości typu `E`, jest definicja klasy `Swapper<E>`:
```java
package swapper;

import java.util.Collection;
...

public class Swapper<E> {

    public Swapper() {
        ...
    }

    public void swap(Collection<E> removed, Collection<E> added) throws InterruptedException {
        ...
    }

    ...

}
```

Bezparametrowy konstruktor tworzy swapper, który w stanie początkowym jest pusty.

Metoda `swap(removed, added)` wstrzymuje wątek do chwili, gdy w swapperze będą wszystkie elementy kolekcji `removed`. Następnie, niepodzielnie:
1. usuwa ze swappera wszystkie elementy kolekcji `removed`, po czym
2. dodaje do swappera wszystkie elementy kolekcji `added`.

Kolekcje `removed` i `added` mogą mieć niepuste przecięcie.

Elementy swappera nie powtarzają się. Dodanie do swappera obiektu, który już w nim jest, nie ma żadnego efektu.

Zarówno kolekcja `removed` jak i `added` może mieć powtórzenia. Nie wpływają one na działanie metody.

W przypadku przerwania wątku metoda zgłasza wyjątek `InterruptedException`.

Przerwane wykonanie metody nie zmienia zawartości swappera.

Przerwanie wątku korzystającego ze swappera nie wpływa na poprawność działania pozostałych wątków.

## Polecenie

* (8 pkt)

  Zaimplementuj w Javie swapper zgodny z powyższą specyfikacją. Do pakietu `swapper` dołącz wszystkie potrzebne definicje pomocnicze.

* (2 pkt)

  Napisz dwa programy przykładowe, demonstrujące zastosowanie swappera do rozwiązania:

  * problemu producentów i konsumentów ze skończonym buforem wieloelementowym,
  * problemu czytelników i pisarzy.

  W programach przykładowych, oprócz swappera, nie należy używać żadnych innych mechanizmów synchronizacji.

## Uwagi

Program ma być w wersji 8 języka Java.

Implementacja nie musi gwarantować, że wątek nie zostanie zagłodzony przez inne wątki korzystające za swappera.

## Walidacja

Rozwiązania zostaną poddane walidacji, wstępnie sprawdzającej zgodność ze specyfikacją.

Na komputerze `students`, w katalogu walidacji, będzie:
* podkatalog `packed` z rozwiązaniami,
* plik `validate.sh`,
* podkatalog `validate` z plikiem `validate/Validate.java`.

Polecenie
```
sh validate.sh ab123456
```
przeprowadzi walidację rozwiązania studenta o identyfikatorze ab123456. Komunikat `OK` poinformuje o sukcesie.

Rozwiązania, które pomyślnie przejdą walidację, zostaną dopuszczone do testów poprawności.

