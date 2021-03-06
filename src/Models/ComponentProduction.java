package Models;

public class ComponentProduction {

    private int idProduct;
    private int idComponent;
    private int qte;

    public ComponentProduction() {
    }

    public ComponentProduction(int idProduct, int idComponent, int qte) {
        this.idProduct = idProduct;
        this.idComponent = idComponent;
        this.qte = qte;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getIdComponent() {
        return idComponent;
    }

    public void setIdComponent(int idComponent) {
        this.idComponent = idComponent;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }
}
