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
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class UrunDaoTest {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private UrunDao urunDao;
    private ObjectId testUrunId;

    @BeforeEach
    void setUp() {
        try {
            // Test için MongoDB bağlantısı, gerçek veritabanını kullanmayacağız
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("stok_takip_test");
            
            // Test koleksiyonunu oluştur
            if (!collectionExists("urunler")) {
                database.createCollection("urunler");
            } else {
                // Test verilerini temizle
                database.getCollection("urunler").drop();
                database.createCollection("urunler");
            }
            
            urunDao = new UrunDao(database);
            
            // Test için ürün ekle
            testUrunId = new ObjectId();
            Urun testUrun = new Urun(testUrunId, "Test Ürün", "Adet", 100, "A-1");
            urunDao.UrunEkle(testUrun);
            
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
    void testUrunObjesiOlusturma() {
        // Yeni bir ürün oluştur
        ObjectId id = new ObjectId();
        Urun urun = new Urun(id, "Test Ürün", "Adet", 100, "A-1");
        
        // Kontrolleri yap
        assertEquals(id, urun.getId());
        assertEquals("Test Ürün", urun.getUrun_adi());
        assertEquals("Adet", urun.getUrun_birimi());
        assertEquals(100, urun.getUrun_fiyati());
        assertEquals("A-1", urun.getUrun_raf());
    }
    
    @Test
    void testUrunSetterGetter() {
        // Boş ürün oluştur ve set metodlarını kullan
        Urun urun = new Urun();
        ObjectId id = new ObjectId();
        
        urun.setId(id);
        urun.setUrun_adi("Test Ürün 2");
        urun.setUrun_birimi("Kg");
        urun.setUrun_fiyati(200);
        urun.setUrun_raf("B-2");
        
        // Kontrolleri yap
        assertEquals(id, urun.getId());
        assertEquals("Test Ürün 2", urun.getUrun_adi());
        assertEquals("Kg", urun.getUrun_birimi());
        assertEquals(200, urun.getUrun_fiyati());
        assertEquals("B-2", urun.getUrun_raf());
    }
    
    @Test
    void testUrunEkle() {
        if (urunDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Yeni bir ürün oluştur
        ObjectId id = new ObjectId();
        Urun yeniUrun = new Urun(id, "Yeni Test Ürün", "Kutu", 150, "C-3");
        
        // Ürünü ekle
        urunDao.UrunEkle(yeniUrun);
        
        // Eklenen ürünü bul ve kontrol et
        List<Urun> urunler = urunDao.UrunList();
        boolean bulundu = false;
        
        for (Urun urun : urunler) {
            if (urun.getId().equals(id)) {
                assertEquals("Yeni Test Ürün", urun.getUrun_adi());
                assertEquals("Kutu", urun.getUrun_birimi());
                assertEquals(150, urun.getUrun_fiyati());
                assertEquals("C-3", urun.getUrun_raf());
                bulundu = true;
                break;
            }
        }
        
        assertTrue(bulundu, "Eklenen ürün veritabanında bulunamadı");
    }
    
    @Test
    void testUrunList() {
        if (urunDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Ürün listesini al
        List<Urun> urunler = urunDao.UrunList();
        
        // En az bir ürün olmalı (setUp'ta eklediğimiz test ürünü)
        assertFalse(urunler.isEmpty(), "Ürün listesi boş olmamalı");
        
        // Test ürününü kontrol et
        boolean testUrunBulundu = false;
        for (Urun urun : urunler) {
            if (urun.getId().equals(testUrunId)) {
                assertEquals("Test Ürün", urun.getUrun_adi());
                testUrunBulundu = true;
                break;
            }
        }
        
        assertTrue(testUrunBulundu, "Test ürünü bulunamadı");
    }
    
    @Test
    void testUrunSil() {
        if (urunDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Önce ürünü ekleyelim
        ObjectId silinecekId = new ObjectId();
        Urun silinecekUrun = new Urun(silinecekId, "Silinecek Ürün", "Paket", 120, "D-4");
        urunDao.UrunEkle(silinecekUrun);
        
        // Şimdi ürünü silelim
        urunDao.UrunSil(silinecekId.toString());
        
        // Ürünün silindiğini kontrol edelim
        List<Urun> urunler = urunDao.UrunList();
        boolean urunHalaDuruyorMu = false;
        
        for (Urun urun : urunler) {
            if (urun.getId().equals(silinecekId)) {
                urunHalaDuruyorMu = true;
                break;
            }
        }
        
        assertFalse(urunHalaDuruyorMu, "Ürün silinmemiş");
    }
    
    @Test
    void testUrunGuncelle() {
        if (urunDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Güncellenecek ürünü ekleyelim
        ObjectId guncellenecekId = new ObjectId();
        Urun ilkUrun = new Urun(guncellenecekId, "Güncellenecek Ürün", "Birim", 100, "E-5");
        urunDao.UrunEkle(ilkUrun);
        
        // Şimdi ürünü güncelleyelim
        Urun guncelUrun = new Urun(guncellenecekId, "Güncellenmiş Ürün", "Yeni Birim", 200, "F-6");
        urunDao.UrunGuncelle(guncellenecekId.toString(), guncelUrun);
        
        // Güncellemenin yapıldığını kontrol edelim
        List<Urun> urunler = urunDao.UrunList();
        boolean guncellemeTamamMi = false;
        
        for (Urun urun : urunler) {
            if (urun.getId().equals(guncellenecekId)) {
                assertEquals("Güncellenmiş Ürün", urun.getUrun_adi());
                assertEquals("Yeni Birim", urun.getUrun_birimi());
                assertEquals(200, urun.getUrun_fiyati());
                assertEquals("F-6", urun.getUrun_raf());
                guncellemeTamamMi = true;
                break;
            }
        }
        
        assertTrue(guncellemeTamamMi, "Ürün güncellenmemiş");
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