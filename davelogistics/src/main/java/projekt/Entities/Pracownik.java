package projekt.Entities;

import javax.persistence.*;

@Entity
@Table(name = "pracownik")
public class Pracownik {
    @Id
    @Column(name = "id_pracownika")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPracownika;

    @Column(name = "imie")
    private String imie;

    @Column(name = "nazwisko")
    private String nazwisko;

    @ManyToOne
    @JoinColumn(name = "id_stanowiska")
    private Stanowiska idStanowiska;

    @Column(name = "lata_pracy")
    private Integer lataPracy;

    @Column(name = "wyksztalcenie")
    private String wyksztalcenie;

    @Column(name = "prawo_jazdy")
    private String prawoJazdy;

    @Column(name = "adres_pracownika")
    private String adresPracownika;

    @Column(name = "telefon")
    private String telefon;

    @ManyToOne
    @JoinColumn(name = "id_oddzialu")
    private Oddzialy idOddzialu;

    public Integer getIdPracownika() {
        return this.idPracownika;
    }

    public void setIdPracownika(Integer idPracownika) {
        this.idPracownika = idPracownika;
    }

    public String getImie() {
        return this.imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return this.nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getIdStanowiska() {
        return this.idStanowiska.getNazwaStanowiska();
    }

    public void setIdStanowiska(Stanowiska idStanowiska) {
        this.idStanowiska = idStanowiska;
    }

    public Integer getLataPracy() {
        return this.lataPracy;
    }

    public void setLataPracy(Integer lataPracy) {
        this.lataPracy = lataPracy;
    }

    public String getWyksztalcenie() {
        return this.wyksztalcenie;
    }

    public void setWyksztalcenie(String wyksztalcenie) {
        this.wyksztalcenie = wyksztalcenie;
    }

    public String getPrawoJazdy() {
        return this.prawoJazdy;
    }

    public void setPrawoJazdy(String prawoJazdy) {
        this.prawoJazdy = prawoJazdy;
    }

    public String getAdresPracownika() {
        return this.adresPracownika;
    }

    public void setAdresPracownika(String adresPracownika) {
        this.adresPracownika = adresPracownika;
    }

    public String getTelefon() {
        return this.telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getIdOddzialu() {
        return this.idOddzialu.getMiasto()+ " " +this.idOddzialu.getUlica()+ " " +this.idOddzialu.getNumerBudynku();
    }

    public void setIdOddzialu(Oddzialy idOddzialu) {
        this.idOddzialu = idOddzialu;
    }

    public Pracownik() {
    }

    public Pracownik(String imie, String nazwisko, Stanowiska idStanowiska, Integer lataPracy, String wyksztalcenie, String prawoJazdy, String adresPracownika, String telefon, Oddzialy idOddzialu) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.idStanowiska = idStanowiska;
        this.lataPracy = lataPracy;
        this.wyksztalcenie = wyksztalcenie;
        this.prawoJazdy = prawoJazdy;
        this.adresPracownika = adresPracownika;
        this.telefon = telefon;
        this.idOddzialu = idOddzialu;
    }
}
