package projekt;

import projekt.Entities.Oddzialy;
import projekt.Entities.Pracownik;
import projekt.Entities.Stanowiska;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Klasa <code>DodawaniePracownika</code>
 * jest kontrolerem dla <code>DodawaniePracownika.fxml</code>
 */
public class DodawaniePracownika extends GlownaController implements Initializable {

    SessionFactory sessionFactory = SingletonConnection.getSessionFactory();
    Alert alert = new Alert(Alert.AlertType.NONE);
    Map<String, Integer> mapOddzialy;

    @FXML
    private TextField imieFld;
    @FXML
    private TextField nazwiskoFld;
    @FXML
    private TextField latapracyFld;
    @FXML
    private TextField adrespracownikaFld;
    @FXML
    private TextField telefonFld;
    @FXML
    private ComboBox<String> stanowiskoMenu;
    @FXML
    private ComboBox<String> oddzialMenu;
    @FXML
    private ComboBox<String> wyksztalcenieMenu;
    @FXML
    private ComboBox<String> prawoJazdyMenu;

    /**
     * Metoda <code>listaStanowisk</code>
     * pobiera listę stanowisk z bazy danych i przypisuje ją do ObservableList oraz ustawia elementy listy do opcji wyboru comboboxa
     */
    private void listaStanowisk() {
        ObservableList obListStanowiska = FXCollections.observableArrayList();
        obListStanowiska = Connector.stanowiska();
        stanowiskoMenu.setItems(obListStanowiska);
    }

    /**
     * Metoda <code>listaOddzialow</code>
     * pobiera listę oddzialow z bazy danych i przypisuje ją do ObservableList oraz ustawia elementy listy do opcji wyboru comboboxa
     */
    private void listaOddzialow() {
        Session session = sessionFactory.openSession();
        ObservableList obListOddzialy = FXCollections.observableArrayList();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Oddzialy> query = builder.createQuery(Oddzialy.class);
        Root<Oddzialy> root = query.from(Oddzialy.class);
        Query<Oddzialy> q = session.createQuery("from Oddzialy");
        List<Oddzialy> oddzialyList = q.getResultList();
        mapOddzialy = Connector.mapowanieOddzialu(oddzialyList);
        for (Oddzialy oddzial : oddzialyList) {
            obListOddzialy.add(oddzial.getMiasto() + " " + oddzial.getUlica() + " " + oddzial.getNumerBudynku());
        }
        oddzialMenu.setItems(obListOddzialy);
    }

    /**
     * Metoda <code>listaWyksztalcen</code>
     * umożliwia pobranie listy wykształceń i przypisanie jej elementow do comboboxa
     */
    private void listaWyksztalcen() {
        ObservableList<String> wyksztalcenieLista = Connector.getInstance().wyksztalcenieLista;
        wyksztalcenieMenu.setItems(wyksztalcenieLista);
    }

    /**
     * Metoda <code>listaKategoriiPrawJazdy</code>
     * umożliwia pobranie listy kategorii praw jazdy i przypisanie jej elementow do comboboxa
     */
    private void listaKategoriiPrawJazdy() {
        ObservableList<String> kategorieLista = Connector.getInstance().prawoJazdyLista;
        prawoJazdyMenu.setItems(kategorieLista);
    }
    /**
     * Metoda <code>dodawaniePracownika</code>
     * umożliwia dodanie pracownika do tabeli
     */
    @FXML
    public void dodawaniePracownika() {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pracownik prac = new Pracownik();
            //jezeli pola tekstowe sa puste i wartosci z menu opcji nie zostaly wybrane
            if (imieFld.getText().isEmpty() || nazwiskoFld.getText().isEmpty() || stanowiskoMenu.getSelectionModel().getSelectedItem() == null || latapracyFld.getText().isEmpty() || wyksztalcenieMenu.getSelectionModel().getSelectedItem() == null ||
                    prawoJazdyMenu.getSelectionModel().getSelectedItem() == null || adrespracownikaFld.getText().isEmpty() || telefonFld.getText().isEmpty() || oddzialMenu.getSelectionModel().getSelectedItem() == null) {
                alert.setAlertType(Alert.AlertType.WARNING);
                alert.setHeaderText("Uzupełnij wszystkie pola!");
                alert.show();
            }
            //jezeli pola tekstowe sa uzupelnione i wartosci z menu opcji zostaly wybrane
            else {
                String telefon = telefonFld.getText();
                int lataPracy = Integer.parseInt(latapracyFld.getText());
                //walidacja numeru telefonu
                if (telefon.length() == 9 && telefon.matches("[0-9]+") && lataPracy >= 0) {
                    prac.setImie(imieFld.getText());
                    prac.setNazwisko(nazwiskoFld.getText());
                    Query query = session.createQuery("from Stanowiska where nazwaStanowiska='" + stanowiskoMenu.getValue() + "'");
                    Stanowiska stanowisko = (Stanowiska) query.uniqueResult();
                    prac.setIdStanowiska(stanowisko);
                    prac.setLataPracy(Integer.parseInt(latapracyFld.getText()));
                    String wyksztalcenie = wyksztalcenieMenu.getValue();
                    prac.setWyksztalcenie(wyksztalcenie);
                    String kategoria = prawoJazdyMenu.getValue();
                    prac.setPrawoJazdy(kategoria);
                    prac.setAdresPracownika(adrespracownikaFld.getText());
                    prac.setTelefon(telefonFld.getText());
                    int idOddzialu = Connector.przemapowanieOddzialu(mapOddzialy, oddzialMenu.getValue());
                    Query query2 = session.createQuery("from Oddzialy where idOddzialu='" + idOddzialu + "'");
                    Oddzialy oddzial = (Oddzialy) query2.uniqueResult();
                    prac.setIdOddzialu(oddzial);
                    session.save(prac);
                    tx.commit();
                    alert.setAlertType(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Pomyślnie dodano pracownika!");
                    alert.setContentText("");
                    alert.show();
                    GlownaController glc = new GlownaController();
                    glc.dodajPracownika(prac);
                    czyszczenie();
                } else {
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setHeaderText("UWAGA!");
                    alert.getDialogPane().setPrefHeight(195.0);
                    alert.setContentText("Telefon zawiera 9 cyf z zakresu 0-9 oraz liczba przepracowanych lat nie może być mniejsza od zera!");
                    alert.show();
                }
            }
        } catch (HibernateException ex) {
            if (tx != null)
                tx.rollback();
        } finally {
            session.close();
        }
    }

    /**
     * Metoda <code>wyczyscBtnAction</code>
     * @param event realizowane wydarzenie
     * @throws Exception
     */
    @FXML
    public void wyczyscBtnAction(MouseEvent event) throws Exception {
        czyszczenie();
    }

    /**
     * Metoda <code>czyszczenie</code>
     * czyśći pola tekstowe związane z dodawaniem pracownika oraz czyści wybrane opcje z comboboxa
     */
    public void czyszczenie() {
        imieFld.setText("");
        nazwiskoFld.setText("");
        latapracyFld.setText("");
        adrespracownikaFld.setText("");
        telefonFld.setText("");
        wyksztalcenieMenu.getSelectionModel().clearSelection();
        stanowiskoMenu.getSelectionModel().clearSelection();
        oddzialMenu.getSelectionModel().clearSelection();
        prawoJazdyMenu.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listaStanowisk();
        listaOddzialow();
        listaWyksztalcen();
        listaKategoriiPrawJazdy();
    }
}
