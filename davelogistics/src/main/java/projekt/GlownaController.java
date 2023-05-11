package projekt;

import projekt.Entities.Oddzialy;
import projekt.Entities.Pracownik;
import projekt.Entities.Stanowiska;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Klasa <code>GlownaController</code>
 * jest kontrolerem dla glownej sceny <code>Glowna.fxml</code>
 */
public class GlownaController extends LogInController implements Initializable {

    SessionFactory sessionFactory = SingletonConnection.getSessionFactory();
    ObservableList<Pracownik> przesortowanaLista;
    Alert alert = new Alert(Alert.AlertType.NONE);
    static ObservableList<Pracownik> data;

    @FXML
    private TextField poOddzialeFld;
    @FXML
    private TextField poNazwiskuFld;
    @FXML
    private TextField poStanowiskuFld;
    @FXML
    private Button logOut;
    @FXML
    private TableView<Pracownik> tbData;
    @FXML
    public TableColumn<Pracownik, Integer> col_id;
    @FXML
    public TableColumn<Pracownik, String> col_imie;
    @FXML
    public TableColumn<Pracownik, String> col_nazwisko;
    @FXML
    public TableColumn<Pracownik, String> col_stanowisko;
    @FXML
    public TableColumn<Pracownik, Integer> col_latapracy;
    @FXML
    public TableColumn<Pracownik, String> col_wyksztalcenie;
    @FXML
    public TableColumn<Pracownik, String> col_prawajazdy;
    @FXML
    public TableColumn<Pracownik, String> col_adres;
    @FXML
    public TableColumn<Pracownik, String> col_telefon;
    @FXML
    public TableColumn<Pracownik, String> col_oddzial;
    @FXML
    public TableColumn<Pracownik, Integer> col_akcje;

    Map<String,Integer> mapOddzialy;
    String stanowiskoEdycja = "";
    String oddzialEdycja = "";
    String kategoriaEdycja = "";
    String wyksztalcenieEdycja= "";
    List<Oddzialy> oddzialyList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        col_id.setCellValueFactory(new PropertyValueFactory<>("idPracownika"));
        col_imie.setCellValueFactory(new PropertyValueFactory<>("imie"));
        col_nazwisko.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
        col_stanowisko.setCellValueFactory(val -> new SimpleStringProperty(val.getValue().getIdStanowiska()));
        col_latapracy.setCellValueFactory(new PropertyValueFactory<>("lataPracy"));
        col_wyksztalcenie.setCellValueFactory(new PropertyValueFactory<>("wyksztalcenie"));
        col_prawajazdy.setCellValueFactory(new PropertyValueFactory<>("prawoJazdy"));
        col_adres.setCellValueFactory(new PropertyValueFactory<>("adresPracownika"));
        col_telefon.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        col_oddzial.setCellValueFactory(new PropertyValueFactory<>("idOddzialu"));
        col_akcje.setCellValueFactory(new PropertyValueFactory<Pracownik, Integer>("idPracownika"));
        Callback<TableColumn<Pracownik, Integer>, TableCell<Pracownik, Integer>> cellUpdateFactory =
                new Callback<TableColumn<Pracownik, Integer>, TableCell<Pracownik, Integer>>() {
                    @Override
                    public TableCell call(final TableColumn<Pracownik, Integer> column) {
                        final TableCell<Pracownik, Integer> cell = new TableCell<Pracownik, Integer>() {
                            final Button button = new Button();
                            @Override
                            public void updateItem(Integer ID, boolean empty) {
                                super.updateItem(ID, empty);
                                Image image = null;
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    image = new Image("editicon.png");
                                    setText(null);
                                    button.setOnAction(event -> {
                                        edycjaPracownika();
                                    });
                                    button.setGraphic(new ImageView(image));
                                    setGraphic(button);
                                    setContentDisplay(ContentDisplay.CENTER);
                                }
                                setText(null);
                            }
                        };
                        return cell;
                    }
                };
        col_akcje.setCellFactory(cellUpdateFactory);
        tbData.setItems(buildData());
        tbData.setEditable(true);
        edycjaKomorek();
    }

    /**
     * Metoda <code>edycjaKomorek</code>
     * umożliwia nam ingerencje w poszczególne komórki oraz przypisanie od nich odpowiednich wartości
     */
    public void edycjaKomorek(){
        col_imie.setCellFactory(TextFieldTableCell.forTableColumn());
        col_imie.setOnEditCommit(e->{
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setImie(e.getNewValue());
        });

        col_nazwisko.setCellFactory(TextFieldTableCell.forTableColumn());
        col_nazwisko.setOnEditCommit(e->{
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setNazwisko(e.getNewValue());
        });

        col_stanowisko.setCellFactory(ComboBoxTableCell.forTableColumn(listaStanowisk()));
        col_stanowisko.setOnEditCommit(e -> stanowiskoEdycja = e.getNewValue());

        col_adres.setCellFactory(TextFieldTableCell.forTableColumn());
        col_adres.setOnEditCommit(e->{
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setAdresPracownika(e.getNewValue());
        });
        col_telefon.setCellFactory(TextFieldTableCell.forTableColumn());
        col_telefon.setOnEditCommit(e->{
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setTelefon(e.getNewValue());
        });
        col_latapracy.setCellFactory(TextFieldTableCell.<Pracownik,Integer>forTableColumn(new IntegerStringConverter()));
        col_latapracy.setOnEditCommit(e->{
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setLataPracy(e.getNewValue());
        });

        col_prawajazdy.setCellFactory(ComboBoxTableCell.forTableColumn(listaKategoriiPrawJazdy()));
        col_prawajazdy.setOnEditCommit(e-> kategoriaEdycja = e.getNewValue());

        col_wyksztalcenie.setCellFactory(ComboBoxTableCell.forTableColumn(wyksztalceniaLista()));
        col_wyksztalcenie.setOnEditCommit(e-> wyksztalcenieEdycja = e.getNewValue());

        col_oddzial.setCellFactory(ComboBoxTableCell.forTableColumn(listaOddzialow()));
        col_oddzial.setOnEditCommit(e -> oddzialEdycja = e.getNewValue());
    }

    /**
     * Metoda <code>listaStanowisk</code>
     * umożliwia pobranie stanowisk z bazy danych i przypisanie ich odpowiednio do listy
     * @return Connector.stanowiska - zwraca liste stanowisk
     */
    private ObservableList listaStanowisk(){
        return Connector.stanowiska();
    }

    /**
     * Metoda <code>listaOddzialow</code>
     * umożliwia pobranie oddziałów z bazy danych i przypisanie ich odpowiednio do listy
     * @return oddzialyObservableList zwraca liste oddziałów
     */
    private ObservableList<String> listaOddzialow(){
        ObservableList<String> oddzialyObservableList = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Oddzialy> query = builder.createQuery(Oddzialy.class);
        Root<Oddzialy> root = query.from(Oddzialy.class);
        Query<Oddzialy> q =session.createQuery("from Oddzialy");
        oddzialyList = q.getResultList();
        for (Oddzialy oddzial : oddzialyList) {
            oddzialyObservableList.add(oddzial.getMiasto() + " " + oddzial.getUlica() + " " + oddzial.getNumerBudynku());
        }
        mapOddzialy = Connector.mapowanieOddzialu(oddzialyList);
        return oddzialyObservableList;
    }

    /**
     * Metoda <code>buildData</code>
     * umożliwia dodanie pracowników pobranych z bazy danych metodą <code>listaPracownikow</code> do listy data
     * @return data zwraca liste z dodanymi pracownikami
     */
    private ObservableList<Pracownik> buildData() {
        data = FXCollections.observableArrayList();
        List<Pracownik> listprac = listaPracownikow();

        for (Pracownik prac : listprac) {
            data.add(prac);
        }
        return data;
    }

    /**
     * Metoda <code>wyksztalceniaLista</code>
     * umożliwia pobranie listy wykształceń zdefiniowanej w klasie Connector i przypisane jej od ObservableListy wyksztalcenieLista
     * @return wyksztalcenieLista zwraca liste wyksztalcen
     */
    private ObservableList<String> wyksztalceniaLista(){
        ObservableList<String> wyksztalcenieLista = Connector.getInstance().wyksztalcenieLista;
        return wyksztalcenieLista;
    }
    /**
     * Metoda <code>listaKategoriiPrawJazdy</code>
     * umożliwia pobranie listy kategorii prawa jazdy zdefininiowanej w klasie Connector i przypisanie jej do ObservaleListy kategorieLista
     */
    private ObservableList<String> listaKategoriiPrawJazdy() {
        ObservableList<String> kategorieLista = Connector.getInstance().prawoJazdyLista;
        return kategorieLista;
    }
    /**
     * Metoda <code>wylogowywanieAkcja</code>
     * umożliwia powrót do ekranu logowaniu po kliknięciu przycisku wyloguj
     * @param event realizowane wydarzenie
     * @throws Exception
     */
    @FXML
    private void wylogowywanieAkcja(ActionEvent event) throws Exception {
        Stage stage;
        Parent root;
        if (event.getSource() == logOut) {
            stage = (Stage) logOut.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Logowanie.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        }
    }

    /**
     * Metoda <code>dodajPracBtn</code>
     * załadowuje i uruchamia okno, w który można dodawać pracowników do tabeli
     * @param event realizowane wydarzenie
     * @throws Exception
     */
    @FXML
    private void dodajPracBtn(MouseEvent event) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("DodawaniePracownika.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Dodawanie pracownika");
        stage.getIcons().add(new Image("icon.png"));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Metoda <code>infoBtn</code>
     * załadowuje oraz uruchamia okno z podstawowymi informacjami
     * @throws IOException
     */
    @FXML
    private void infoBtn() throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("Info.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Instrukcja");
        stage.getIcons().add(new Image("icon.png"));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Metoda <code>edycjaPracownika</code>
     * umożliwia edycje pracownika oraz waliduje odpowiednio pola tekstowe
     */
    @FXML
    private void edycjaPracownika() {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pracownik pracownik = (Pracownik) session.get(Pracownik.class, tbData.getSelectionModel().getSelectedItem().getIdPracownika());
            //dla imienia
            String imie = pracownik.getImie();
            String imie2 = tbData.getSelectionModel().getSelectedItem().getImie();
            if(!imie.equals(imie2)){
                pracownik.setImie(imie2);
            }
            //dla nazwiska
            String nazwisko = pracownik.getNazwisko();
            String nazwisko2 = tbData.getSelectionModel().getSelectedItem().getNazwisko();
            if(!nazwisko.equals(nazwisko2)){
                pracownik.setNazwisko(nazwisko2);
            }
            //dla stanowisk
            if(!stanowiskoEdycja.isEmpty()){
                Query query = session.createQuery("from Stanowiska where nazwaStanowiska='" + stanowiskoEdycja + "'");
                Stanowiska stanowisko = (Stanowiska) query.uniqueResult();
                pracownik.setIdStanowiska(stanowisko);
            }
            //dla oddzialow
            if(!oddzialEdycja.isEmpty()){
                int idOddz = Connector.przemapowanieOddzialu(mapOddzialy, oddzialEdycja);
                Query query1 = session.createQuery("from Oddzialy where idOddzialu='" + idOddz + "'");
                Oddzialy oddz  = (Oddzialy) query1.uniqueResult();
                pracownik.setIdOddzialu(oddz);
            }
            //dla kategorii prawo jazd
            if(!kategoriaEdycja.isEmpty()){
                pracownik.setPrawoJazdy(kategoriaEdycja);
            }
            //dla wyksztalcenia
            if(!wyksztalcenieEdycja.isEmpty()){
                pracownik.setWyksztalcenie(wyksztalcenieEdycja);
            }
            //dla adresu pracownika
            String adres = pracownik.getAdresPracownika();
            String adres2 = tbData.getSelectionModel().getSelectedItem().getAdresPracownika();
            if(!adres.equals(adres2)){
                pracownik.setAdresPracownika(adres2);
            }
            //dla doswiadczenia
            int doswiadczenie = pracownik.getLataPracy();
            int doswiadczenie2 = tbData.getSelectionModel().getSelectedItem().getLataPracy();
            if(doswiadczenie == doswiadczenie2){
                System.out.println();
            }
            else{
                if(doswiadczenie2 >= 0){
                    pracownik.setLataPracy(doswiadczenie2);
                }
                else{
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setHeaderText("LATA PRACY");
                    alert.setContentText("Pracownik nie może mieć ujemnej liczby przepracowanych lat!");
                    alert.show();
                }
            }
            //dla telefonu pracownika
            String numerTelefonu = pracownik.getTelefon();
            String numerTelefonu2 = tbData.getSelectionModel().getSelectedItem().getTelefon();
            if(!numerTelefonu.equals(numerTelefonu2)){
                if(numerTelefonu2.length() == 9 && numerTelefonu2.matches("[0-9]+")){
                    pracownik.setTelefon(numerTelefonu2);
                }
                else{
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setHeaderText("TELEFON");
                    alert.setContentText("Telefon zawiera 9 cyf z zakresu 0-9.");
                    alert.show();
                }
            }
            session.update(pracownik);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Metoda <code>dodawanieStanOddz</code>
     * załadowuje i uruchamia okno, w który można dodawać i usuwać oddziały/stanowiska
     * @param event realizowane wydarzenie
     * @throws Exception
     */
    @FXML
    private void dodawanieStanOddz(MouseEvent event) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("DodawanieOddzStan.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.getIcons().add(new Image("icon.png"));
        stage.setTitle("Dodawanie stanowisk lub oddziałów");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Metoda <code>usuwanieBtn</code>
     * usuwa wybranego z tabeli pracownika
     * @param event realizowane wydarzenie
     */
    @FXML
    private void usuwanieBtn(ActionEvent event) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if(tbData.getSelectionModel().getSelectedItem() == null){
                return;
            }
            Pracownik pracownik = (Pracownik) session.get(Pracownik.class, tbData.getSelectionModel().getSelectedItem().getIdPracownika());
            session.delete(pracownik);
            tx.commit();
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Pracownik został pomyślnie usunięty!");
            alert.show();
            //czyszcze od razu tabele
            tbData.getItems().removeAll(tbData.getSelectionModel().getSelectedItem());
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Metoda <code>szukajPoStanowisku</code>
     * umożliwia wyszukiwanie pracownika/pracowników pracujących na podanym w polu tekstowym stanowisku
     */
    @FXML
    private void szukajPoStanowisku(){
        Session session = sessionFactory.openSession();
        przesortowanaLista = FXCollections.observableArrayList();
        if(!poStanowiskuFld.getText().isEmpty()) {
            Query query = session.createQuery("from Pracownik where idStanowiska.nazwaStanowiska like" + "'%" + poStanowiskuFld.getText() + "%'");
            List<Pracownik> listaSt = query.getResultList();
            przesortowanaLista.addAll(listaSt);
            tbData.setItems(przesortowanaLista);
            poStanowiskuFld.setText("");
        }
        else{
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setHeaderText("Opcja wyszukiwania pracownikow po stanowisku");;
            alert.setContentText("Proces wyszukiwania pracownikow następuje po rozpoczęciu wpisywania stanowiska!");
            alert.show();
        }
    }
    /**
     * Metoda <code>szukajPoOddziale</code>
     * umożliwia wyszukiwanie pracownika/pracowników pracujących w podanym w polu tekstowym oddziale
     */
    @FXML
    private void szukajPoOddziale(){
        Session session = sessionFactory.openSession();
        przesortowanaLista = FXCollections.observableArrayList();
        if(!poOddzialeFld.getText().isEmpty()) {
            Query query = session.createQuery("from Pracownik where idOddzialu.miasto like" + "'%" + poOddzialeFld.getText() + "%'");
            List<Pracownik> listaOdd = query.getResultList();
            przesortowanaLista.addAll(listaOdd);
            tbData.setItems(przesortowanaLista);
            poOddzialeFld.setText("");
        }
        else{
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setHeaderText("Opcja wyszukiwania pracownikow po oddziale");;
            alert.setContentText("Proces wyszukiwania pracownikow następuje po rozpoczęciu wpisywania nazwy miejscowości!");
            alert.show();
        }
    }

    /**
     * Metoda <code>szukajPoNazwisku</code>
     * umożliwia wyszukiwanie pracownika o podanym w polu tekstowym nazwisku
     */
    @FXML
    private void szukajPoNazwisku(){
        Session session = sessionFactory.openSession();
        przesortowanaLista = FXCollections.observableArrayList();
        if(!poNazwiskuFld.getText().isEmpty()) {
            Query query = session.createQuery("from Pracownik where nazwisko like" + "'%" + poNazwiskuFld.getText() + "%'");
            List<Pracownik> listaNaz = query.getResultList();
            przesortowanaLista.addAll(listaNaz);
            tbData.setItems(przesortowanaLista);
            poNazwiskuFld.setText("");
        }
        else{
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setHeaderText("Opcja wyszukiwania pracownika po nazwisku");;
            alert.setContentText("Proces wyszukiwania pracownika następuje po rozpoczęciu pisania nazwiska osoby, którą chcemy wyszukać!");
            alert.show();
        }
    }

    /**
     * Metoda <code>odswiezBtn</code>
     * przeładowuje liste pracowników oraz aktualną scenę
     * @throws IOException
     */
    @FXML
    private void odswiezBtn() throws IOException {
        listaPracownikow().clear();
        tbData.setItems(buildData());
        Main main = new Main();
        main.changeScene("Glowna.fxml");
    }

    /**
     * Metoda <code>dodajPracownika</code>
     * dodaje obiekt-praocwnik do listy pracowników ObservableList
     * @param pracownik obiekt dodawany do listy data
     */
    public void dodajPracownika(Pracownik pracownik){
        data.add(pracownik);
    }

    /**
     * Metoda <code>zamykanieAplikacji</code>
     * zamyka okno aplikacji
     */
    public void zamykanieAplikacji(){
        Platform.exit();
    }
}




