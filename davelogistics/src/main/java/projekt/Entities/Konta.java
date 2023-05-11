package projekt.Entities;

import javax.persistence.*;

@Entity
@Table(name = "konta")
public class Konta {
    @Id
    @Column(name = "id_konta")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idKonta;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    public Integer getIdKonta() {
        return this.idKonta;
    }

    public void setIdKonta(Integer idKonta) {
        this.idKonta = idKonta;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Konta() {
    }

    public Konta(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
