package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class StokSatisDao {
    private MongoCollection<Document> stokSatisCollection;
    public StokSatisDao(MongoDatabase database) {this.stokSatisCollection = database.getCollection("stok_satis");}

    public void StokEkle(StokSatis stok) {
        Document doc = new Document("_id", stok.getId())
                .append("birim_fiyat", stok.getBirim_fiyat())
                .append("stok_adet",stok.getStok_adet())
                .append("stok_fiyat",stok.getStok_fiyat())
                .append("urun_id",stok.getUrun_id());
        stokSatisCollection.insertOne(doc);
    }
    public List<StokSatis> StokSatisList(){
        List<StokSatis> stokSatisList = new ArrayList<>();
        for(Document doc : stokSatisCollection.find()){
            StokSatis stok = new StokSatis();
            stok.setId(doc.getObjectId("_id"));
            
            // Double/Number -> Integer dönüşümleri
            Number birimFiyat = doc.get("birim_fiyat", Number.class);
            stok.setBirim_fiyat(birimFiyat != null ? birimFiyat.intValue() : 0);
            
            Number stokAdet = doc.get("stok_adet", Number.class);
            stok.setStok_adet(stokAdet != null ? stokAdet.intValue() : 0);
            
            Number stokFiyat = doc.get("stok_fiyat", Number.class);
            stok.setStok_fiyat(stokFiyat != null ? stokFiyat.intValue() : 0);
            
            stok.setUrun_id(doc.getObjectId("urun_id"));
            
            stokSatisList.add(stok);
        }
        return stokSatisList;
    }
    public void StokSil(String id)
    {
        ObjectId objectId = new ObjectId(id);
        stokSatisCollection.deleteOne(Filters.eq("_id", objectId));
    }
    public void StokGuncelle(String id , StokSatis stok)
    {
        ObjectId objectId = new ObjectId(id);
        stokSatisCollection.updateOne(Filters.eq("_id",objectId), Updates.combine(
                Updates.set("birim_fiyat",stok.getBirim_fiyat()),
                Updates.set("stok_adet",stok.getStok_adet()),
                Updates.set("stok_fiyat",stok.getStok_fiyat()),
                Updates.set("urun_id",stok.getUrun_id())));
    }
}
