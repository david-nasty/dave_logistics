package projekt.Entities;

import javax.persistence.*;

@Entity
@Table(name = "stanowiska")
public class Stanowiska {
    @Id
    @Column(name = "id_stanowiska")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idStanowiska;

    @Column(name = "nazwa_stanowiska")
    private String nazwaStanowiska;

    public Integer getIdStanowiska() {
        return this.idStanowiska;
    }

    public void setIdStanowiska(Integer idStanowiska) {
        this.idStanowiska = idStanowiska;
    }

    public String getNazwaStanowiska() {
        return this.nazwaStanowiska;
    }

    public void setNazwaStanowiska(String nazwaStanowiska) {
        this.nazwaStanowiska = nazwaStanowiska;
    }

    @Override
    public String toString() {
        return nazwaStanowiska;
    }
}
