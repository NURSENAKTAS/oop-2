package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class MusteriDao {
    private MongoCollection<Document> musteriCollection;

    public MusteriDao(MongoDatabase database) {this.musteriCollection = database.getCollection("musteriler");}

    public void MusteriEkle(Musteri musteri) {
        Document doc =new Document("_id", musteri.getId())
                .append("musteri_adi", musteri.getMusteri_adi())
                .append("musteri_soyadi", musteri.getMusteri_soyadi())
                .append("musteri_adresi" , musteri.getMusteri_adresi())
                .append("musteri_alacak" , musteri.getMusteri_alacak())
                .append("musteri_borc" , musteri.getMusteri_borc())
                .append("musteri_iskonto" , musteri.getMusteri_iskonto())
                .append("urun_id" , musteri.getUrun_id());

        musteriCollection.insertOne(doc);
    }
    public List<Musteri> MusteriList() {
        List<Musteri> musteriList = new ArrayList<>();
        for(Document doc : musteriCollection.find()) {
            Musteri musteri = new Musteri();
            musteri.setId(doc.getObjectId("_id"));
            musteri.setMusteri_adi(doc.getString("musteri_adi"));
            musteri.setMusteri_soyadi(doc.getString("musteri_soyadi"));
            musteri.setMusteri_adresi(doc.getString("musteri_adresi"));
            
            // Double'ı Integer'a dönüştürme
            Number alacak = doc.get("musteri_alacak", Number.class);
            musteri.setMusteri_alacak(alacak != null ? alacak.intValue() : 0);
            
            Number borc = doc.get("musteri_borc", Number.class);
            musteri.setMusteri_borc(borc != null ? borc.intValue() : 0);
            
            Number iskonto = doc.get("musteri_iskonto", Number.class);
            musteri.setMusteri_iskonto(iskonto != null ? iskonto.intValue() : 0);
            
            if (doc.containsKey("urun_id") && doc.get("urun_id") != null) {
                musteri.setUrun_id(doc.getObjectId("urun_id"));
            }
            
            musteriList.add(musteri);
        }
        return musteriList;
    }
    public void MusteriSil(String id){
        ObjectId objectId = new ObjectId(id);
        musteriCollection.deleteOne(Filters.eq("_id",objectId));
    }
    public void MusteriGuncelle(String id , Musteri musteri){
        ObjectId objectId = new ObjectId(id);
        musteriCollection.updateOne(Filters.eq("_id" , objectId), Updates.combine(
                Updates.set("musteri_adi" , musteri.getMusteri_adi()),
                Updates.set("musteri_soyadi",musteri.getMusteri_soyadi()),
                Updates.set("musteri_adresi",musteri.getMusteri_adresi()),
                Updates.set("musteri_alacak" ,musteri.getMusteri_alacak()),
                Updates.set("musteri_borc",musteri.getMusteri_borc()),
                Updates.set("musteri_iskonto" , musteri.getMusteri_iskonto()),
                Updates.set("urun_id",musteri.getUrun_id())));
    }
}
