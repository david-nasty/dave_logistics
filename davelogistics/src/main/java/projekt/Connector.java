package projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import projekt.Entities.Konta;
import projekt.Entities.Oddzialy;
import projekt.Entities.Pracownik;
import projekt.Entities.Stanowiska;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa <code>Connector</code>
 * w klasie tej przechowywane są metody wykorzystywane do pobierania listy stanowisk, mappingu oraz przemapowywania
 */
public class Connector {
    public ObservableList<String> wyksztalcenieLista = FXCollections.observableArrayList("Podstawowe","Zawodowe","Średnie","Wyższe");
    public ObservableList<String> prawoJazdyLista = FXCollections.observableArrayList("B","BE","C","C1E","CE");
    private final static Connector instance = new Connector();

    /**
     * Metoda <code>getInstance</code>
     * @return instance
     */
    public static Connector getInstance() {
        return instance;
    }

    /**
     * Metoda <code>mapowanieOddzialu</code>
     * umożliwia mapowanie listy oddzialow, kazdej nazwie oddzialow przyporzadkowane sa im odpowiednie id.
     * @param list lista, ktora ma zostac zmapowana
     * @return argMap mapa par nazwy oddzialu-id
     */
    public static Map mapowanieOddzialu(List<Oddzialy> list) {
        Map<String, Integer> argMap;
        argMap = new HashMap<>();
        for (Oddzialy oddzial : list
        ) {
            argMap.put(oddzial.toString(), oddzial.getIdOddzialu());
        }
        return argMap;
    }
    /**
     * Metoda <code>stanowiska</code>
     * umożliwia pobranie stanowisk z bazy danych i przypisanie ich kolejno do ObservableList
     * @return obList zwraca ObservableList zawierajacą stanowiska
     */
    static ObservableList stanowiska() {
        SessionFactory sessionFactory = SingletonConnection.getSessionFactory();
        Session session = sessionFactory.openSession();
        ObservableList obList = FXCollections.observableArrayList();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Stanowiska> query = builder.createQuery(Stanowiska.class);
        Root<Stanowiska> root = query.from(Stanowiska.class);
        Query<Stanowiska> quer = session.createQuery("from Stanowiska");
        List<Stanowiska> stanowiskaList = quer.getResultList();
        for (Stanowiska stanowisko : stanowiskaList) {
            obList.add(stanowisko.getNazwaStanowiska());
        }
        return obList;
    }

    /**
     * Metoda <code>przemapowanieOddzialu</code>
     * @param mapa mapa w której sprawdzamy zawartość klucza
     * @param wartosc klucz
     * @return mapa.get(wartosc)
     * @return 0
     */
    static int przemapowanieOddzialu(Map<String, Integer> mapa, String wartosc) {
        for (int i = 0; i < mapa.size(); i++) {
            if (mapa.containsKey(wartosc)) {
                return mapa.get(wartosc);
            }
        }
        return 0;
    }
    /**
     * Metoda <code>dodanieRekordow</code>
     * umożliwia dodanie startowych rekordów stanowisk/oddziałów/pracowników do bazy
     */
    static void dodanieRekordow() {
        SessionFactory sessionFactory = SingletonConnection.getSessionFactory();
        Session session = sessionFactory.openSession();
        //stanowiska
        Stanowiska kierowcaTira = new Stanowiska();
        kierowcaTira.setNazwaStanowiska("Kierowca tira");
        Stanowiska kierowcaBusa = new Stanowiska();
        kierowcaBusa.setNazwaStanowiska("Kierowca busa");
        Stanowiska handlowiec = new Stanowiska();
        handlowiec.setNazwaStanowiska("Handlowiec");
        Stanowiska magazynier = new Stanowiska();
        magazynier.setNazwaStanowiska("Magazynier");
        Stanowiska logistyk = new Stanowiska();
        logistyk.setNazwaStanowiska("Logistyk");
        session.beginTransaction();
        session.save(kierowcaTira);
        session.save(kierowcaBusa);
        session.save(handlowiec);
        session.save(magazynier);
        session.save(logistyk);
        //oddzialy
        Oddzialy przemysl = new Oddzialy();
        przemysl.setMiasto("Przemyśl");
        przemysl.setUlica("Sezamkowa");
        przemysl.setNumerBudynku(25);
        Oddzialy konin = new Oddzialy();
        konin.setMiasto("Konin");
        konin.setUlica("Traugutta");
        konin.setNumerBudynku(12);
        Oddzialy poznan = new Oddzialy();
        poznan.setMiasto("Poznań");
        poznan.setUlica("Sucha");
        poznan.setNumerBudynku(1);
        Oddzialy krosno = new Oddzialy();
        krosno.setMiasto("Krosno");
        krosno.setUlica("Transportowa");
        krosno.setNumerBudynku(5);
        session.save(krosno);
        session.save(przemysl);
        session.save(konin);
        session.save(poznan);
        //pracownicy
        Pracownik prac1 = new Pracownik("Dawid", "Kamiński", logistyk, 4, "Średnie", "B", "Grunwaldzka 12", "933444222", krosno);
        Pracownik prac2 = new Pracownik("Andrzej", "Jałowiec", magazynier, 8, "Średnie", "B", "Stanowska 9B", "533269053", przemysl);
        Pracownik prac3 = new Pracownik("Kamil", "Mazurek", kierowcaTira, 6, "Wyższe", "CE", "Prałkowska 1/12", "600392883", przemysl);
        Pracownik prac4 = new Pracownik("Zdzisław", "Kowal", kierowcaBusa, 3, "Wyższe", "CE", "Mokra 19", "500600700", konin);
        Pracownik prac5 = new Pracownik("Krystian", "Klamowski", kierowcaTira, 6, "Średnie", "CE", "Krakowska", "538555948", poznan);
        Pracownik prac6 = new Pracownik("Marcin", "Mazur", magazynier, 7, "Średnie", "CE", "Górna 51/3B", "883734555", konin);
        Pracownik prac7 = new Pracownik("Władysław", "Paczkowski", magazynier, 4, "Zawodowe", "B", "Rakowiecka 4", "533884713", poznan);
        Pracownik prac8 = new Pracownik("Agnieszka", "Opaluch", logistyk, 8, "Wyższe", "B", "Grunwaldzka 127/2B", "773441232", krosno);
        Pracownik prac9 = new Pracownik("Janusz", "Kura", kierowcaBusa, 2, "Podstawowe", "C1E", "Tomaszowska 7", "813914915", przemysl);
        Pracownik prac10 = new Pracownik("Karol", "Koźma", kierowcaBusa, 3, "Wyższe", "C1E", "Kaniowska 2/3A", "855488483", krosno);
        Pracownik prac11 = new Pracownik("Adam", "Martyński", magazynier, 4, "Podstawowe", "B", "Mickiewicza 2B", "544332221", poznan);
        Pracownik prac12 = new Pracownik("Dariusz", "Paryga", logistyk, 6, "Wyższe", "B", "Dworskiego 4", "600533533", konin);
        Pracownik prac13 = new Pracownik("Jolanta", "Karpik", logistyk, 3, "Wyższe", "B", "Opornicka 125/4C", "600998997", krosno);
        Pracownik prac14 = new Pracownik("Robert", "Kwaśniak", handlowiec, 5, "Zawodowe", "B", "Komarnicka 17", "566788999", przemysl);
        Pracownik prac15 = new Pracownik("Marek", "Olszowy", handlowiec, 2, "Wyższe", "B", "Słodka 12/6", "500394483", konin);
        session.save(prac1);
        session.save(prac2);
        session.save(prac3);
        session.save(prac4);
        session.save(prac5);
        session.save(prac6);
        session.save(prac7);
        session.save(prac8);
        session.save(prac9);
        session.save(prac10);
        session.save(prac11);
        session.save(prac12);
        session.save(prac13);
        session.save(prac14);
        session.save(prac15);
        Konta konto = new Konta();
        konto.setLogin("root");
        konto.setPassword("root");
        session.save(konto);
        session.getTransaction().commit();
        session.close();
    }
    /**
     * Metoda <code>sprawdzCzyIstnieje</code>
     * sprawdza czy istnieje konto potrzebne do logowania, jezeli nie wywoluje metode <code>dodawanieRekordow</code>
     */
    static void sprawdzCzyIstnieje() {
        SessionFactory sessionFactory = SingletonConnection.getSessionFactory();
        Session session = sessionFactory.openSession();
        List<Konta> konta = session.createQuery("FROM Konta").getResultList();
        System.out.println(konta);
        if(konta.isEmpty()){
            dodanieRekordow();
        }
    }
}