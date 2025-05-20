package org.example;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Personel {
    private ObjectId id;
    private String personel_adi;
    private String personel_soyadi;
    private String personel_adresi;
    private int personel_maas;

    public Personel() {}

    public Personel(ObjectId id, String personel_adi, String personel_soyadi, String personel_adresi, int personel_maas) {
        this.id = id;
        this.personel_adi = personel_adi;
        this.personel_soyadi = personel_soyadi;
        this.personel_adresi = personel_adresi;
        this.personel_maas = personel_maas;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getPersonel_adi() {
        return personel_adi;
    }

    public void setPersonel_adi(String personel_adi) {
        this.personel_adi = personel_adi;
    }

    public String getPersonel_soyadi() {
        return personel_soyadi;
    }

    public void setPersonel_soyadi(String personel_soyadi) {
        this.personel_soyadi = personel_soyadi;
    }

    public String getPersonel_adresi() {
        return personel_adresi;
    }

    public void setPersonel_adresi(String personel_adresi) {
        this.personel_adresi = personel_adresi;
    }

    public int getPersonel_maas() {
        return personel_maas;
    }

    public void setPersonel_maas(int personel_maas) {
        this.personel_maas = personel_maas;
    }
}
