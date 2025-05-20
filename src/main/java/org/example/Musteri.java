package org.example;

import org.bson.types.ObjectId;

public class Musteri {
    private ObjectId id;
    private String musteri_adi;
    private String musteri_soyadi;
    private String musteri_adresi;
    private int musteri_alacak;
    private int musteri_borc;
    private int musteri_iskonto;
    private ObjectId urun_id;

    public Musteri() {}

    public Musteri(ObjectId id ,String musteri_adi , String musteri_soyadi , String musteri_adresi,int musteri_alacak,int musteri_borc,int musteri_iskonto,ObjectId urun_id) {
        this.id = id;
        this.musteri_adi = musteri_adi;
        this.musteri_soyadi = musteri_soyadi;
        this.musteri_adresi = musteri_adresi;
        this.musteri_alacak = musteri_alacak;
        this.musteri_borc = musteri_borc;
        this.musteri_iskonto = musteri_iskonto;
        this.urun_id = urun_id;

    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getMusteri_adi() {
        return musteri_adi;
    }

    public void setMusteri_adi(String musteri_adi) {
        this.musteri_adi = musteri_adi;
    }

    public String getMusteri_soyadi() {
        return musteri_soyadi;
    }

    public void setMusteri_soyadi(String musteri_soyadi) {
        this.musteri_soyadi = musteri_soyadi;
    }

    public String getMusteri_adresi() {
        return musteri_adresi;
    }

    public void setMusteri_adresi(String musteri_adresi) {
        this.musteri_adresi = musteri_adresi;
    }

    public int getMusteri_alacak() {
        return musteri_alacak;
    }

    public void setMusteri_alacak(int musteri_alacak) {
        this.musteri_alacak = musteri_alacak;
    }

    public int getMusteri_borc() {
        return musteri_borc;
    }

    public void setMusteri_borc(int musteri_borc) {
        this.musteri_borc = musteri_borc;
    }

    public int getMusteri_iskonto() {
        return musteri_iskonto;
    }

    public void setMusteri_iskonto(int musteri_iskonto) {
        this.musteri_iskonto = musteri_iskonto;
    }

    public ObjectId getUrun_id() {
        return urun_id;
    }

    public void setUrun_id(ObjectId urun_id) {
        this.urun_id = urun_id;
    }
}
