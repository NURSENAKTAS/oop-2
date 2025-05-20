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

class StokSatisDaoTest {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private StokSatisDao stokSatisDao;
    private ObjectId testStokSatisId;
    private ObjectId testUrunId;

    @BeforeEach
    void setUp() {
        try {
            // Test için MongoDB bağlantısı
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("stok_takip_test");
            
            // Test koleksiyonunu oluştur
            if (!collectionExists("stok_satis")) {
                database.createCollection("stok_satis");
            } else {
                // Test verilerini temizle
                database.getCollection("stok_satis").drop();
                database.createCollection("stok_satis");
            }
            
            // Test için ürün koleksiyonu da lazım
            if (!collectionExists("urunler")) {
                database.createCollection("urunler");
            }
            
            stokSatisDao = new StokSatisDao(database);
            
            // Test için ürün ID'si oluştur
            testUrunId = new ObjectId();
            
            // Test için stok satış ekle
            testStokSatisId = new ObjectId();
            StokSatis testStokSatis = new StokSatis(testStokSatisId, 50, 10, 500, testUrunId);
            stokSatisDao.StokEkle(testStokSatis);
            
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
    void testStokSatisObjesiOlusturma() {
        // Yeni bir stok satış oluştur
        ObjectId id = new ObjectId();
        ObjectId urunId = new ObjectId();
        StokSatis stokSatis = new StokSatis(id, 50, 10, 500, urunId);
        
        // Kontrolleri yap
        assertEquals(id, stokSatis.getId());
        assertEquals(50, stokSatis.getBirim_fiyat());
        assertEquals(10, stokSatis.getStok_adet());
        assertEquals(500, stokSatis.getStok_fiyat());
        assertEquals(urunId, stokSatis.getUrun_id());
    }
    
    @Test
    void testStokSatisSetterGetter() {
        // Boş stok satış oluştur ve set metodlarını kullan
        StokSatis stokSatis = new StokSatis();
        ObjectId id = new ObjectId();
        ObjectId urunId = new ObjectId();
        
        stokSatis.setId(id);
        stokSatis.setBirim_fiyat(75);
        stokSatis.setStok_adet(5);
        stokSatis.setStok_fiyat(375);
        stokSatis.setUrun_id(urunId);
        
        // Kontrolleri yap
        assertEquals(id, stokSatis.getId());
        assertEquals(75, stokSatis.getBirim_fiyat());
        assertEquals(5, stokSatis.getStok_adet());
        assertEquals(375, stokSatis.getStok_fiyat());
        assertEquals(urunId, stokSatis.getUrun_id());
    }
    
    @Test
    void testStokEkle() {
        if (stokSatisDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Yeni bir stok satış oluştur
        ObjectId id = new ObjectId();
        ObjectId urunId = new ObjectId();
        StokSatis yeniStokSatis = new StokSatis(id, 60, 20, 1200, urunId);
        
        // Stok satışı ekle
        stokSatisDao.StokEkle(yeniStokSatis);
        
        // Eklenen stok satışı bul ve kontrol et
        List<StokSatis> stokSatislar = stokSatisDao.StokSatisList();
        boolean bulundu = false;
        
        for (StokSatis stokSatis : stokSatislar) {
            if (stokSatis.getId().equals(id)) {
                assertEquals(60, stokSatis.getBirim_fiyat());
                assertEquals(20, stokSatis.getStok_adet());
                assertEquals(1200, stokSatis.getStok_fiyat());
                assertEquals(urunId, stokSatis.getUrun_id());
                bulundu = true;
                break;
            }
        }
        
        assertTrue(bulundu, "Eklenen stok satış veritabanında bulunamadı");
    }
    
    @Test
    void testStokSatisList() {
        if (stokSatisDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Stok satış listesini al
        List<StokSatis> stokSatislar = stokSatisDao.StokSatisList();
        
        // En az bir stok satış olmalı (setUp'ta eklediğimiz test stok satış)
        assertFalse(stokSatislar.isEmpty(), "Stok satış listesi boş olmamalı");
        
        // Test stok satışını kontrol et
        boolean testStokSatisBulundu = false;
        for (StokSatis stokSatis : stokSatislar) {
            if (stokSatis.getId().equals(testStokSatisId)) {
                assertEquals(50, stokSatis.getBirim_fiyat());
                assertEquals(10, stokSatis.getStok_adet());
                assertEquals(500, stokSatis.getStok_fiyat());
                assertEquals(testUrunId, stokSatis.getUrun_id());
                testStokSatisBulundu = true;
                break;
            }
        }
        
        assertTrue(testStokSatisBulundu, "Test stok satış bulunamadı");
    }
    
    @Test
    void testStokSil() {
        if (stokSatisDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Önce stok satışı ekleyelim
        ObjectId silinecekId = new ObjectId();
        ObjectId urunId = new ObjectId();
        StokSatis silinecekStokSatis = new StokSatis(silinecekId, 40, 5, 200, urunId);
        stokSatisDao.StokEkle(silinecekStokSatis);
        
        // Şimdi stok satışı silelim
        stokSatisDao.StokSil(silinecekId.toString());
        
        // Stok satışın silindiğini kontrol edelim
        List<StokSatis> stokSatislar = stokSatisDao.StokSatisList();
        boolean stokSatisHalaDuruyorMu = false;
        
        for (StokSatis stokSatis : stokSatislar) {
            if (stokSatis.getId().equals(silinecekId)) {
                stokSatisHalaDuruyorMu = true;
                break;
            }
        }
        
        assertFalse(stokSatisHalaDuruyorMu, "Stok satış silinmemiş");
    }
    
    @Test
    void testStokGuncelle() {
        if (stokSatisDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Güncellenecek stok satışı ekleyelim
        ObjectId guncellenecekId = new ObjectId();
        ObjectId ilkUrunId = new ObjectId();
        StokSatis ilkStokSatis = new StokSatis(guncellenecekId, 70, 15, 1050, ilkUrunId);
        stokSatisDao.StokEkle(ilkStokSatis);
        
        // Şimdi stok satışı güncelleyelim
        ObjectId yeniUrunId = new ObjectId();
        StokSatis guncelStokSatis = new StokSatis(guncellenecekId, 80, 25, 2000, yeniUrunId);
        stokSatisDao.StokGuncelle(guncellenecekId.toString(), guncelStokSatis);
        
        // Güncellemenin yapıldığını kontrol edelim
        List<StokSatis> stokSatislar = stokSatisDao.StokSatisList();
        boolean guncellemeTamamMi = false;
        
        for (StokSatis stokSatis : stokSatislar) {
            if (stokSatis.getId().equals(guncellenecekId)) {
                assertEquals(80, stokSatis.getBirim_fiyat());
                assertEquals(25, stokSatis.getStok_adet());
                assertEquals(2000, stokSatis.getStok_fiyat());
                assertEquals(yeniUrunId, stokSatis.getUrun_id());
                guncellemeTamamMi = true;
                break;
            }
        }
        
        assertTrue(guncellemeTamamMi, "Stok satış güncellenmemiş");
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