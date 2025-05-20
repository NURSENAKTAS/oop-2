package org.example;

import org.bson.types.ObjectId;

public class Urun {
    private ObjectId id;
    private String urun_adi;
    private String urun_birimi;
    private int urun_fiyati;
    private String urun_raf;


    public Urun() {}

    public Urun(ObjectId id , String urun_adi,String urun_birimi,int urun_fiyati,String urun_raf) {
        this.id = id;
        this.urun_adi = urun_adi;
        this.urun_birimi = urun_birimi;
        this.urun_fiyati = urun_fiyati;
        this.urun_raf = urun_raf;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUrun_adi() {
        return urun_adi;
    }

    public void setUrun_adi(String urun_adi) {
        this.urun_adi = urun_adi;
    }

    public String getUrun_birimi() {
        return urun_birimi;
    }

    public void setUrun_birimi(String urun_birimi) {
        this.urun_birimi = urun_birimi;
    }

    public int getUrun_fiyati() {
        return urun_fiyati;
    }

    public void setUrun_fiyati(int urun_fiyati) {
        this.urun_fiyati = urun_fiyati;
    }

    public String getUrun_raf() {
        return urun_raf;
    }

    public void setUrun_raf(String urun_raf) {
        this.urun_raf = urun_raf;
    }
}
