package org.example;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class StokIadeDaoTest {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private StokIadeDao stokIadeDao;
    private ObjectId testStokIadeId;
    private ObjectId testUrunId;

    @BeforeEach
    void setUp() {
        try {
            // Test için MongoDB bağlantısı
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("stok_takip_test");
            
            // Test koleksiyonunu oluştur
            if (!collectionExists("stok_iade")) {
                database.createCollection("stok_iade");
            } else {
                // Test verilerini temizle
                database.getCollection("stok_iade").drop();
                database.createCollection("stok_iade");
            }
            
            // Test için ürün koleksiyonu da lazım
            if (!collectionExists("urunler")) {
                database.createCollection("urunler");
            }
            
            stokIadeDao = new StokIadeDao(database);
            
            // Test için ürün ID'si oluştur
            testUrunId = new ObjectId();
            
            // Test için stok iade ekle
            testStokIadeId = new ObjectId();
            StokIade testStokIade = new StokIade(testStokIadeId, "Test İade Notu", testUrunId);
            stokIadeDao.StokIadeEkle(testStokIade);
            
        } catch (Exception e) {
            // MongoDB bağlantısı kurulamadıysa testleri atla
            System.out.println("Test MongoDB bağlantısı kurulamadı: " + e.getMessage());
        }
    }
    
    @AfterEach
    void tearDown() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Test
    void testStokIadeObjesiOlusturma() {
        // Yeni bir stok iade oluştur
        ObjectId id = new ObjectId();
        ObjectId urunId = new ObjectId();
        StokIade stokIade = new StokIade(id, "Ürün hasarlı", urunId);
        
        // Kontrolleri yap
        assertEquals(id, stokIade.getId());
        assertEquals("Ürün hasarlı", stokIade.getSi_not());
        assertEquals(urunId, stokIade.getUrun_id());
    }
    
    @Test
    void testStokIadeSetterGetter() {
        // Boş stok iade oluştur ve set metodlarını kullan
        StokIade stokIade = new StokIade();
        ObjectId id = new ObjectId();
        ObjectId urunId = new ObjectId();
        
        stokIade.setId(id);
        stokIade.setSi_not("Müşteri memnun kalmadı");
        stokIade.setUrun_id(urunId);
        
        // Kontrolleri yap
        assertEquals(id, stokIade.getId());
        assertEquals("Müşteri memnun kalmadı", stokIade.getSi_not());
        assertEquals(urunId, stokIade.getUrun_id());
    }
    
    @Test
    void testStokIadeEkle() {
        if (stokIadeDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Yeni bir stok iade oluştur
        ObjectId id = new ObjectId();
        ObjectId urunId = new ObjectId();
        StokIade yeniStokIade = new StokIade(id, "Yeni İade Notu", urunId);
        
        // Stok iadeyi ekle
        stokIadeDao.StokIadeEkle(yeniStokIade);
        
        // Eklenen stok iadeyi bul ve kontrol et
        List<StokIade> stokIadeler = stokIadeDao.StokIadeList();
        boolean bulundu = false;
        
        for (StokIade stokIade : stokIadeler) {
            if (stokIade.getId().equals(id)) {
                assertEquals("Yeni İade Notu", stokIade.getSi_not());
                assertEquals(urunId, stokIade.getUrun_id());
                bulundu = true;
                break;
            }
        }
        
        assertTrue(bulundu, "Eklenen stok iade veritabanında bulunamadı");
    }
    
    @Test
    void testStokIadeList() {
        if (stokIadeDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Stok iade listesini al
        List<StokIade> stokIadeler = stokIadeDao.StokIadeList();
        
        // En az bir stok iade olmalı (setUp'ta eklediğimiz test stok iade)
        assertFalse(stokIadeler.isEmpty(), "Stok iade listesi boş olmamalı");
        
        // Test stok iadeyi kontrol et
        boolean testStokIadeBulundu = false;
        for (StokIade stokIade : stokIadeler) {
            if (stokIade.getId().equals(testStokIadeId)) {
                assertEquals("Test İade Notu", stokIade.getSi_not());
                assertEquals(testUrunId, stokIade.getUrun_id());
                testStokIadeBulundu = true;
                break;
            }
        }
        
        assertTrue(testStokIadeBulundu, "Test stok iade bulunamadı");
    }
    
    @Test
    void testStokIadeSil() {
        if (stokIadeDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Önce stok iadeyi ekleyelim
        ObjectId silinecekId = new ObjectId();
        ObjectId urunId = new ObjectId();
        StokIade silinecekStokIade = new StokIade(silinecekId, "Silinecek İade", urunId);
        stokIadeDao.StokIadeEkle(silinecekStokIade);
        
        // Şimdi stok iadeyi silelim
        stokIadeDao.StokIadeSil(silinecekId.toString());
        
        // Stok iadenin silindiğini kontrol edelim
        List<StokIade> stokIadeler = stokIadeDao.StokIadeList();
        boolean stokIadeHalaDuruyorMu = false;
        
        for (StokIade stokIade : stokIadeler) {
            if (stokIade.getId().equals(silinecekId)) {
                stokIadeHalaDuruyorMu = true;
                break;
            }
        }
        
        assertFalse(stokIadeHalaDuruyorMu, "Stok iade silinmemiş");
    }
    
    @Test
    void testStokIadeGuncelle() {
        if (stokIadeDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Güncellenecek stok iadeyi ekleyelim
        ObjectId guncellenecekId = new ObjectId();
        ObjectId ilkUrunId = new ObjectId();
        StokIade ilkStokIade = new StokIade(guncellenecekId, "İlk İade Notu", ilkUrunId);
        stokIadeDao.StokIadeEkle(ilkStokIade);
        
        // Şimdi stok iadeyi güncelleyelim
        ObjectId yeniUrunId = new ObjectId();
        StokIade guncelStokIade = new StokIade(guncellenecekId, "Güncel İade Notu", yeniUrunId);
        stokIadeDao.StokIadeGuncelle(guncellenecekId.toString(), guncelStokIade);
        
        // Güncellemenin yapıldığını kontrol edelim
        List<StokIade> stokIadeler = stokIadeDao.StokIadeList();
        boolean guncellemeTamamMi = false;
        
        for (StokIade stokIade : stokIadeler) {
            if (stokIade.getId().equals(guncellenecekId)) {
                assertEquals("Güncel İade Notu", stokIade.getSi_not());
                assertEquals(yeniUrunId, stokIade.getUrun_id());
                guncellemeTamamMi = true;
                break;
            }
        }
        
        assertTrue(guncellemeTamamMi, "Stok iade güncellenmemiş");
    }
    
    private boolean collectionExists(String collectionName) {
        if (database == null) return false;
        
        for (String name : database.listCollectionNames()) {
            if (name.equals(collectionName)) {
                return true;
            }
        }
        return false;
    }
} 