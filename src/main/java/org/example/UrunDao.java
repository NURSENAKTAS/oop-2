package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;


public class UrunDao {
    private MongoCollection<Document> urunCollection;
    public UrunDao(MongoDatabase database) {this.urunCollection = database.getCollection("urunler");}

    public void UrunEkle(Urun urun) {
        Document doc = new Document("_id", urun.getId())
                .append("urun_adi", urun.getUrun_adi())
                .append("urun_birimi", urun.getUrun_birimi())
                .append("urun_fiyati" , urun.getUrun_fiyati())
                .append("urun_raf" , urun.getUrun_raf());
        urunCollection.insertOne(doc);
    }
    public List<Urun> UrunList() {
        List<Urun> urunList = new ArrayList<>();
        for (Document doc : urunCollection.find()) {
            Urun urun = new Urun();
            urun.setId(doc.getObjectId("_id"));
            urun.setUrun_adi(doc.getString("urun_adi"));
            urun.setUrun_birimi(doc.getString("urun_birimi"));
            Number fiyat = doc.get("urun_fiyati", Number.class);
            urun.setUrun_fiyati(fiyat != null ? fiyat.intValue() : 0);
            urun.setUrun_raf(doc.getString("urun_raf"));
            urunList.add(urun);
        }
        return urunList;
    }
    public void UrunSil(String id) {
        ObjectId objectId = new ObjectId(id);
        urunCollection.deleteOne(Filters.eq("_id", objectId));
    }
    public void UrunGuncelle(String id , Urun urun) {
        ObjectId objectId = new ObjectId(id);
        urunCollection.updateOne(Filters.eq("_id", objectId), Updates.combine(
                Updates.set("urun_adi", urun.getUrun_adi()),
                Updates.set("urun_birimi", urun.getUrun_birimi()),
                Updates.set("urun_fiyati", urun.getUrun_fiyati()),
                Updates.set("urun_raf", urun.getUrun_raf())));
    }
}
