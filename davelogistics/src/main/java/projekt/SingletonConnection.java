package projekt;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Klasa <code>SingletonConnection</code>
 * wzorzec projektowy, który gwarantuje iż będziemy mieć tylko jedną instancję danej klasy.
 */
public class SingletonConnection {

    private static SessionFactory sessionFactory;

    private SingletonConnection() {
    }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        }
        return sessionFactory;
    }

}