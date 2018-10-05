
package org.rb.androidspringrestclient.model;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Developer
 */
public class Good implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
   public static final int FCOUNT = 5;

    long id;
    private Date   cdate;
    private String name;
    private String shop;
    private double price;
    private double discount;
   

    public Good(){
    }
    public Good(Date cdate, String name, String shop, double price, double discount) {
        this.cdate = cdate;
        this.name = name;
        this.shop = shop;
        this.price = price;
        this.discount = discount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); 
    }
    public Good makeCopy() throws CloneNotSupportedException{
     return (Good) this.clone();
    }
    

    
    @Override
    public String toString() {
        return "id="+id+" date=" + dformat() + ", name=" + name + ", shop=" + shop + ", price=" + price + ", discount=" + discount;
    }

   private String dformat(){
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");	
                
               return sdf.format(cdate);
               
   }
   
    public static Good getFromStream(DataInputStream din) throws IOException {
        
        Good good = new Good();
        //long tt = new Date().getTime();
        Date d = new Date();
        d.setTime(din.readLong());
        good.setCdate(d);
        good.setName(din.readUTF());
        good.setShop(din.readUTF());
        good.setPrice(din.readDouble());
        good.setDiscount(din.readDouble());
        return good;
    }


    public static void putToStream(DataOutputStream dos, Good good) throws IOException{
     dos.writeLong(good.getCdate().getTime());
     dos.writeUTF(good.getName());
     dos.writeUTF(good.getShop());
     dos.writeDouble(good.getPrice());
     dos.writeDouble(good.getDiscount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Good good = (Good) o;

        if (Double.compare(good.price, price) != 0) return false;
        if (Double.compare(good.discount, discount) != 0) return false;
        if (cdate != null ? !cdate.equals(good.cdate) : good.cdate != null) return false;
        if (name != null ? !name.equals(good.name) : good.name != null) return false;
        return shop != null ? shop.equals(good.shop) : good.shop == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = cdate != null ? cdate.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (shop != null ? shop.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(discount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
