package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StokIadeDao {
    private MongoCollection<Document> stokIadeCollection;

    public StokIadeDao(MongoDatabase database) {this.stokIadeCollection = database.getCollection("stok_iade");}

    public void StokIadeEkle(StokIade stokIade) {
        Document doc = new Document("_id", stokIade.getId())
                .append("si_not", stokIade.getSi_not())
                .append("urun_id", stokIade.getUrun_id());
        stokIadeCollection.insertOne(doc);
    }
    public List<StokIade> StokIadeList() {
        List<StokIade> StokIadeList = new ArrayList<>();
        for (Document doc : stokIadeCollection.find()) {
            StokIade stokIade = new StokIade();
            stokIade.setId(doc.getObjectId("_id"));
            stokIade.setSi_not(doc.getString("si_not"));
            stokIade.setUrun_id(doc.getObjectId("urun_id"));

            StokIadeList.add(stokIade);
        }
        return StokIadeList;
    }
    public void StokIadeSil(String id)
    {
        ObjectId objectId = new ObjectId(id);
      stokIadeCollection.deleteOne(Filters.eq("_id", objectId));
    }

    public void StokIadeGuncelle(String id, StokIade stokIade) {
        ObjectId objectId = new ObjectId(id);
        stokIadeCollection.updateOne(Filters.eq("_id",objectId), Updates.combine(
                Updates.set("si_not",stokIade.getSi_not()),
                Updates.set("urun_id",stokIade.getUrun_id())));

    }
}
