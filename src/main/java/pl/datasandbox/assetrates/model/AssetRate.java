package pl.datasandbox.assetrates.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="DWH_RATES", schema = "LWALLET")
public class AssetRate {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "pk_rate_key_seq")
    @SequenceGenerator(name="pk_rate_key_seq", allocationSize = 1)
    private Long id;
    @Column(name="SYMBOL")
    private String symbol;
    @Column(name="RATE_DATE")
    private Date date;
    @Column(name="RATE")
    private Double rate;
    @Column(name="DWH_LOAD_DATE")
    private Date loadDate;


    public AssetRate(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Date getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(Date loadDate) {
        this.loadDate = loadDate;
    }
}
