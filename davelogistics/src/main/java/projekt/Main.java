/**
 * @author Dawid Kuźmiński
 */
package projekt;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Main extends Application {
    private static Stage stage;

    /**
     * Metoda <code>start</code>
     * umożliwia załadowanie sceny Logowania
     * @param primaryStage
     * @throws Exception
     */
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("Logowanie.fxml"));
        stage.setTitle("Dave Logistics");
        stage.setScene(new Scene(root, 770, 440));
        stage.getIcons().add(new Image("icon.png"));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Metoda <code>changeScene</code>
     * umożliwia zmianę sceny
     * @param fxml
     * @throws IOException
     */
    public void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(this.getClass().getResource(fxml));
        stage.setScene(new Scene(pane, 1054, 568));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Main
     * @param args
     */
    public static void main(String[] args) {
        SessionFactory sessionFactory = SingletonConnection.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Connector.getInstance().sprawdzCzyIstnieje();
        session.getTransaction().commit();
        session.close();
        launch(args);
    }
}
