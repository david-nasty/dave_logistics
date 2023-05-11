package projekt.Entities;

import javax.persistence.*;

@Entity
@Table(name = "oddzialy")
public class Oddzialy {
    @Id
    @Column(name = "id_oddzialu")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOddzialu;

    @Column(name = "miasto")
    private String miasto;

    @Column(name = "ulica")
    private String ulica;

    @Column(name = "numer_budynku")
    private Integer numerBudynku;

    public Integer getIdOddzialu() {
        return this.idOddzialu;
    }

    public void setIdOddzialu(Integer idOddzialu) {
        this.idOddzialu = idOddzialu;
    }

    public String getMiasto() {
        return this.miasto;
    }

    public void setMiasto(String miasto) {
        this.miasto = miasto;
    }

    public String getUlica() {
        return this.ulica;
    }

    public void setUlica(String ulica) {
        this.ulica = ulica;
    }

    public Integer getNumerBudynku() {
        return this.numerBudynku;
    }

    public void setNumerBudynku(Integer numerBudynku) {
        this.numerBudynku = numerBudynku;
    }

    @Override
    public String toString() {
        return miasto + " " + ulica +  " " + numerBudynku;
    }
}
