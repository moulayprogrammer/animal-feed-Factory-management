package Models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Payments {

    private int id;
    private int idPayer;
    private int idInvoice;
    private LocalDateTime date;
    private double pay;
    private String raison;

    public Payments() {
    }

    public Payments(int idPayer, int idInvoice, LocalDateTime date, double pay, String raison) {
        this.idPayer = idPayer;
        this.idInvoice = idInvoice;
        this.date = date;
        this.pay = pay;
        this.raison = raison;
    }

    public Payments(double pay) {
        this.pay = pay;
    }

    public Payments(int id, double pay) {
        this.id = id;
        this.pay = pay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPayer() {
        return idPayer;
    }

    public void setIdPayer(int idPayer) {
        this.idPayer = idPayer;
    }

    public int getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(int idInvoice) {
        this.idInvoice = idInvoice;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getPay() {
        return pay;
    }

    public void setPay(double pay) {
        this.pay = pay;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }
}
