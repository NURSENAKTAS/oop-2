package org.example;

import org.bson.types.ObjectId;

public class StokSatis {
    private ObjectId id ;
    private int birim_fiyat ;
    private int stok_adet;
    private int stok_fiyat;
    public ObjectId urun_id;

    public StokSatis() {}

    public StokSatis(ObjectId id , Integer birim_fiyat, Integer stok_adet, Integer stok_fiyat , ObjectId urun_id) {
        this.id  = id ;
        this.birim_fiyat = birim_fiyat ;
        this.stok_adet = stok_adet ;
        this.stok_fiyat = stok_fiyat ;
        this.urun_id = urun_id ;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getBirim_fiyat() {
        return birim_fiyat;
    }

    public void setBirim_fiyat(int birim_fiyat) {
        this.birim_fiyat = birim_fiyat;
    }

    public int getStok_adet() {
        return stok_adet;
    }

    public void setStok_adet(int stok_adet) {
        this.stok_adet = stok_adet;
    }

    public int getStok_fiyat() {
        return stok_fiyat;
    }

    public void setStok_fiyat(int stok_fiyat) {
        this.stok_fiyat = stok_fiyat;
    }

    public ObjectId getUrun_id() {
        return urun_id;
    }

    public void setUrun_id(ObjectId urun_id) {
        this.urun_id = urun_id;
    }
}
