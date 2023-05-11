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
 * Klasa <code>DodawanieOddzStan</code>
 * jest kontrolerem dla <code>DodawanieOddzStan.fxml</code>
 */
public class DodawanieOddzStan implements Initializable {

    SessionFactory sessionFactory = SingletonConnection.getSessionFactory();
    Alert alert = new Alert(Alert.AlertType.NONE);
    Map oddzialyMap;
    private ObservableList obListStanowisk = FXCollections.observableArrayList();;
    @FXML
    private TextField noweStanowiskoFld;
    @FXML
    private TextField noweMiastoFld;
    @FXML
    private TextField nowaUlicaFld;
    @FXML
    private TextField nowyNumerBudynkuFld;
    @FXML
    private ComboBox<String> stanowiskoMenuUsuwanie;
    @FXML
    private ComboBox<String> oddzialyMenuUsuwanie;

    /**
     * Metoda <code>listaStanowisk</code>
     * pobiera listę stanowisk z bazy danych i przypisuje ją do ObservableList oraz ustawia elementy listy do opcji wyboru comboboxa
     */
    private void listaStanowisk(){
        obListStanowisk = Connector.stanowiska();
        stanowiskoMenuUsuwanie.setItems(obListStanowisk);
    }
    ObservableList obListOddzialy;
    /**
     * Metoda <code>listaOddzialow</code>
     * pobiera listę oddziałów z bazy danych i przypisuje ją do ObservableList oraz ustawia elementy listy do opcji wyboru comboboxa
     */
    private void listaOddzialow(){
        obListOddzialy = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Oddzialy> query = builder.createQuery(Oddzialy.class);
        Root<Oddzialy> root = query.from(Oddzialy.class);
        Query<Oddzialy> q =session.createQuery("from Oddzialy");
        List<Oddzialy> oddzialyList = q.getResultList();
        oddzialyMap = Connector.mapowanieOddzialu(oddzialyList);
        for (Oddzialy oddzial : oddzialyList) {
            obListOddzialy.add(oddzial.getMiasto() + " " + oddzial.getUlica() + " " + oddzial.getNumerBudynku());
        }
        oddzialyMenuUsuwanie.setItems(obListOddzialy);
    }

    /**
     * Metoda <code>dodajStanowisko</code>
     * umożliwia dodanie stanowiska do tabeli
     */
    @FXML
    public void dodajStanowisko() {
        Stanowiska stanowisko = new Stanowiska();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        tx = session.beginTransaction();
        try {
            if (!noweStanowiskoFld.getText().isEmpty()){
                stanowisko.setNazwaStanowiska(noweStanowiskoFld.getText());
                obListStanowisk.add(stanowisko);
                session.save(stanowisko);
                tx.commit();
                noweStanowiskoFld.setText("");
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Pomyślnie dodano stanowisko!");
                alert.setContentText("");
                alert.show();
                listaStanowisk();
            } else {
                alert.setAlertType(Alert.AlertType.WARNING);
                alert.setHeaderText("Wypelnij pole!");
                alert.setContentText("");
                alert.show();
            }
        } catch (HibernateException ex) {
            if (tx != null)
                tx.rollback();
        } finally {
            session.close();
        }
    }

    /**
     * Metoda <code>usunStanowisko</code>
     * umożliwia usunięcie istniejęcego stanowiska
     */
    @FXML
    public void usunStanowisko(){
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        tx = session.beginTransaction();
        try {
            if(stanowiskoMenuUsuwanie.getSelectionModel().getSelectedItem() == null){
                alert.setAlertType(Alert.AlertType.WARNING);
                alert.setHeaderText("Wybierz stanowisko z menu!");
                alert.setContentText("");
                alert.show();
            }
            else{
                List<Pracownik> pracownicy = session.createQuery("FROM Pracownik").getResultList();
                String stanCombo = stanowiskoMenuUsuwanie.getValue();
                Query qStan = session.createQuery("from Stanowiska where nazwaStanowiska='" + stanCombo + "'");
                Stanowiska stanowisko = (Stanowiska) qStan.uniqueResult();
                for(int i = 0; i < pracownicy.size(); i++){
                    if(pracownicy.get(i).getIdStanowiska() == stanowisko.getNazwaStanowiska()){
                        alert.setAlertType(Alert.AlertType.WARNING);
                        alert.setHeaderText("Uwaga!");
                        alert.getDialogPane().setPrefHeight(195.0);
                        alert.setContentText("Można usuwać wyłącznie stanowiska, które nie są powiązane z żadnym pracownikiem. Aby usunąć to stanowisko sprawdź listę pracowników i wprowadź korektę.");
                        alert.show();
                        return;
                    }
                }
                obListStanowisk.remove(stanowisko);
                session.delete(stanowisko);
                tx.commit();
                stanowiskoMenuUsuwanie.getSelectionModel().clearSelection();
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Pomyślnie usunięto stanowisko!");
                alert.setContentText("");
                alert.show();
                listaStanowisk();
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }
    }

    /**
     * Metoda <code>dodajOddział</code>
     * umożliwia dodanie oddziału
     */
    @FXML
    public void dodajOddzial() {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        tx = session.beginTransaction();
        Oddzialy oddzial = new Oddzialy();
        try {
            //jezeli pola tekstowe dotyczace oddzialow sa puste
            if (noweMiastoFld.getText().isEmpty() || nowaUlicaFld.getText().isEmpty() || Integer.parseInt(nowyNumerBudynkuFld.getText()) <= 0){
                alert.setAlertType(Alert.AlertType.WARNING);
                alert.setHeaderText("Wypelnij wszystkie pola!");
                alert.setContentText("");
                alert.show();
            }
            //jezeli pola tekstowe dotyczace oddzialow nie sa puste
            else {
                oddzial.setMiasto(noweMiastoFld.getText());
                oddzial.setUlica(nowaUlicaFld.getText());
                oddzial.setNumerBudynku(Integer.parseInt(nowyNumerBudynkuFld.getText()));
                obListOddzialy.add(oddzial);
                session.save(oddzial);
                tx.commit();
                noweMiastoFld.setText("");
                nowaUlicaFld.setText("");
                nowyNumerBudynkuFld.setText("");
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Pomyślnie dodano oddział!");
                alert.setContentText("");
                alert.show();
                listaOddzialow(); //ponowne zaladowanie listy oddzialow wraz z aktualizacja
            }
        } catch (HibernateException ex) {
            if (tx != null)
                tx.rollback();
        } finally {
            session.close();
        }
    }

    /**
     * Metoda <code>usunOddzial</code>
     * umożliwia usunięcie istniejącego oddziału
     */
    @FXML
    public void usunOddzial(){
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        tx = session.beginTransaction();
        try {
            //jezeli oddzial nie został wybrany z menu
            if(oddzialyMenuUsuwanie.getSelectionModel().getSelectedItem() == null){
                alert.setAlertType(Alert.AlertType.WARNING);
                alert.setHeaderText("Wybierz oddział z menu!");
                alert.setContentText("");
                alert.show();
            }
            //jezeli oddzial zostal wybrany z menu
            else {
                List<Pracownik> pracownicy = session.createQuery("FROM Pracownik").getResultList();
                String oddzCombo = oddzialyMenuUsuwanie.getValue();
                int idOddzialu = Connector.przemapowanieOddzialu(oddzialyMap, oddzCombo);
                Query qq = session.createQuery("from Oddzialy where idOddzialu='" + idOddzialu + "'");
                Oddzialy oddzial = (Oddzialy) qq.uniqueResult();
                for(int i = 0; i < pracownicy.size(); i++){
                    if(pracownicy.get(i).getIdOddzialu().equals(oddzial.toString())){
                        alert.setAlertType(Alert.AlertType.WARNING);
                        alert.setHeaderText("Uwaga!");
                        alert.getDialogPane().setPrefHeight(205.0);
                        alert.setContentText("Można usuwać wyłącznie oddziały, które nie są powiązane z żadnym pracownikiem.\nAby usunąć ten oddział sprawdź listę pracowników i wprowadź korektę.");
                        alert.show();
                        return;
                    }
                }
                obListOddzialy.remove(oddzial);
                session.delete(oddzial);
                tx.commit();
                oddzialyMenuUsuwanie.getSelectionModel().clearSelection(); //czyszczenie wybranej opcji z menu
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Pomyślnie usunięto oddział!");
                alert.setContentText("");
                alert.show();
                listaOddzialow(); //ponowne zaladowanie listy oddzialow wraz z aktualizacja
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listaStanowisk();
        listaOddzialow();
    }
}
