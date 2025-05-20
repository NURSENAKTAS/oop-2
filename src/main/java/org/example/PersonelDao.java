package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PersonelDao {
    private MongoCollection<Document> personelCollection;

    public PersonelDao(MongoDatabase database) {this.personelCollection = database.getCollection("personel");}

    public void PersonelEkle(Personel personel) {
        Document doc = new Document("_id", personel.getId())
                .append("personel_adi", personel.getPersonel_adi())
                .append("personel_adresi", personel.getPersonel_adresi())
                .append("personel_maas", personel.getPersonel_maas())
                .append("personel_soyadi", personel.getPersonel_soyadi());
        personelCollection.insertOne(doc);
    }
    public List<Personel> PersonelList() {
        List<Personel> personelList = new ArrayList<>();
        for (Document doc : personelCollection.find()) {
            Personel personel = new Personel();
            personel.setId(doc.getObjectId("_id"));
            personel.setPersonel_adi(doc.getString("personel_adi"));
            personel.setPersonel_adresi(doc.getString("personel_adresi"));
            
            // Double'ı Integer'a dönüştürme
            Number maas = doc.get("personel_maas", Number.class);
            personel.setPersonel_maas(maas != null ? maas.intValue() : 0);
            
            personel.setPersonel_soyadi(doc.getString("personel_soyadi"));
            personelList.add(personel);
        }
        return personelList;
    }
    public void PersonelSil(String id)
    {
        ObjectId objectId = new ObjectId(id);
        personelCollection.deleteOne(Filters.eq("_id", objectId));
    }

    public void PersonelGuncelle(String id, Personel personel) {
        ObjectId objectId = new ObjectId(id);
        personelCollection.updateOne(Filters.eq("_id",objectId), Updates.combine(
                Updates.set("personel_adi",personel.getPersonel_adi()),
                Updates.set("personel_adresi",personel.getPersonel_adresi()),
                Updates.set("personel_maas",personel.getPersonel_maas()),
                Updates.set("personel_soyadi",personel.getPersonel_soyadi())));
    }


}
