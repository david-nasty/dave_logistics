package projekt;

import projekt.Entities.Konta;
import projekt.Entities.Pracownik;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.hibernate.*;
import org.hibernate.query.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Klasa <code>LogInController</code>
 * jest kontrolerem dla <code>Logowanie.fxml</code>
 */
public class LogInController {
    @FXML
    private Label labelLogin;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    /**
     * Metoda <code>userLogIn</code>
     * jeżeli <code>checkLogin</code> zwraca wartość true to pobiera liste pracowników i zmienia scenę na główną
     * @param event realizowane wydarzenie
     * @throws IOException
     */
    public void userLogIn(ActionEvent event) throws IOException {
        if (checkLogin()) {
            List listaPracownikow = listaPracownikow();
            Main m = new Main();
            m.changeScene("Glowna.fxml");
        }
    }

    /**
     * Metoda <code>checkLogin</code>
     * pobiera dane do logowania z bazy danych i porównuje je z wpisanymi do pól tekstowych w oknie logowania
     * @return true/false w zależności od zachodzących warunków
     */
    private boolean checkLogin() {
        boolean successfulLogIn = false;
        SessionFactory sessionFactory = SingletonConnection.getSessionFactory();
        Session session = sessionFactory.openSession();
        List<Konta> kontaLista;
        Map<String, String> listaKont = new HashMap<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Konta> query = builder.createQuery(Konta.class);
        Root<Konta> root = query.from(Konta.class);
        query.select(root);
        Query<Konta> q = session.createQuery("from Konta k where k.login = '" + username.getText().toString() + "' and k.password = '" + password.getText().toString() + "'");
        kontaLista = q.getResultList();
        labelLogin.setText("");
        for (Konta konto : kontaLista) {
            listaKont.put(konto.getLogin(), konto.getPassword());

            if (username.getText().toString().equals(konto.getLogin()) && password.getText().toString().equals(konto.getPassword())) {
                successfulLogIn = true;
            } else {
                successfulLogIn = false;
            }
        }
        if (successfulLogIn) {
            labelLogin.setText("Zalogowano!");
        } else {
            labelLogin.setText("Podaj prawidłowe dane!");
        }
        return successfulLogIn;
    }

    /**
     * Metoda <code>listaPracownikow</code>
     * pobiera pracowników z bazy danych i przypisuje ich kolejno do listy
     * @return pracownicy zwraca listę pracowników
     */
    public List listaPracownikow() {
        SessionFactory sessionFactory = SingletonConnection.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        tx = session.beginTransaction();
        List pracownicy = session.createQuery("FROM Pracownik").list();
        try {
            for (Iterator iterator = pracownicy.iterator(); iterator.hasNext(); ) {
                Pracownik pracownik = (Pracownik) iterator.next();
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return pracownicy;
    }
}