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

class MusteriDaoTest {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MusteriDao musteriDao;
    private ObjectId testMusteriId;
    private ObjectId testUrunId;

    @BeforeEach
    void setUp() {
        try {
            // Test için MongoDB bağlantısı
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("stok_takip_test");
            
            // Test koleksiyonunu oluştur
            if (!collectionExists("musteriler"))  { //eğer koleksiyon yoksa yeni bir koelskiyon oluşturuyor burada
                database.createCollection("musteriler");
            } else {
                // Test verilerini temizle
                database.getCollection("musteriler").drop();
                database.createCollection("musteriler");
            }
            
            // Test için ürün koleksiyonu da lazım
            if (!collectionExists("urunler")) {
                database.createCollection("urunler");
            }
            
            musteriDao = new MusteriDao(database); //müşteri dao yu çağırıyorum burada
            
            // Test için ürün ID'si oluştur
            testUrunId = new ObjectId();
            
            // Test için müşteri ekle
            testMusteriId = new ObjectId();
            Musteri testMusteri = new Musteri(testMusteriId, "Test Müşteri", "Test Soyad", "Test Adres", 1000, 500, 10, testUrunId);
            musteriDao.MusteriEkle(testMusteri);
            
        } catch (Exception e) {
            // MongoDB bağlantısı kurulamadıysa testleri atla
            System.out.println("Test MongoDB bağlantısı kurulamadı: " + e.getMessage());
        }
    }
    
    @AfterEach //tüm testlerin sonunda mongoclientı kapat eğer kapanmamışsa
    void tearDown() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Test
    void testMusteriObjesiOlusturma() { //otomatik kendim nesne oluşturuyorum ki test edim
        // Yeni bir müşteri oluştur
        ObjectId id = new ObjectId();
        ObjectId urunId = new ObjectId();
        Musteri musteri = new Musteri(id, "Ali", "Veli", "İzmir", 1000, 500, 10, urunId);
        
        // Kontrolleri yap
        assertEquals(id, musteri.getId()); //assertEquals denk mi yani eşit mi demek
        assertEquals("Ali", musteri.getMusteri_adi());
        assertEquals("Veli", musteri.getMusteri_soyadi());
        assertEquals("İzmir", musteri.getMusteri_adresi());
        assertEquals(1000, musteri.getMusteri_alacak());
        assertEquals(500, musteri.getMusteri_borc());
        assertEquals(10, musteri.getMusteri_iskonto());
        assertEquals(urunId, musteri.getUrun_id());
    }
    
    @Test
    void testMusteriSetterGetter() {
        // Boş müşteri oluştur ve set metodlarını kullan
        Musteri musteri = new Musteri();
        ObjectId id = new ObjectId();
        ObjectId urunId = new ObjectId();
        
        musteri.setId(id);//set verii almak demek set ile aynı zamanda güncellerim.
        musteri.setMusteri_adi("Ayşe");
        musteri.setMusteri_soyadi("Demir");
        musteri.setMusteri_adresi("Bursa");
        musteri.setMusteri_alacak(2000);
        musteri.setMusteri_borc(1000);
        musteri.setMusteri_iskonto(15);
        musteri.setUrun_id(urunId);
        
        // Kontrolleri yap
        assertEquals(id, musteri.getId());
        assertEquals("Ayşe", musteri.getMusteri_adi());
        assertEquals("Demir", musteri.getMusteri_soyadi());
        assertEquals("Bursa", musteri.getMusteri_adresi());
        assertEquals(2000, musteri.getMusteri_alacak());
        assertEquals(1000, musteri.getMusteri_borc());
        assertEquals(15, musteri.getMusteri_iskonto());
        assertEquals(urunId, musteri.getUrun_id());
    }
    
    @Test
    void testMusteriEkle() {
        if (musteriDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Yeni bir müşteri oluştur
        ObjectId id = new ObjectId();
        ObjectId urunId = new ObjectId();
        Musteri yeniMusteri = new Musteri(id, "Yeni Müşteri", "Yeni Soyad", "Yeni Adres", 3000, 1500, 20, urunId);
        
        // Müşteriyi ekle
        musteriDao.MusteriEkle(yeniMusteri);
        
        // Eklenen müşteriyi bul ve kontrol et
        List<Musteri> musteriler = musteriDao.MusteriList();
        boolean bulundu = false;
        
        for (Musteri musteri : musteriler) {
            if (musteri.getId().equals(id)) {
                assertEquals("Yeni Müşteri", musteri.getMusteri_adi());
                assertEquals("Yeni Soyad", musteri.getMusteri_soyadi());
                assertEquals("Yeni Adres", musteri.getMusteri_adresi());
                assertEquals(3000, musteri.getMusteri_alacak());
                assertEquals(1500, musteri.getMusteri_borc());
                assertEquals(20, musteri.getMusteri_iskonto());
                assertEquals(urunId, musteri.getUrun_id());
                bulundu = true;
                break;
            }
        }
        
        assertTrue(bulundu, "Eklenen müşteri veritabanında bulunamadı");
    }
    
    @Test
    void testMusteriList() {
        if (musteriDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Müşteri listesini al
        List<Musteri> musteriler = musteriDao.MusteriList();
        
        // En az bir müşteri olmalı (setUp'ta eklediğimiz test müşterisi)
        assertFalse(musteriler.isEmpty(), "Müşteri listesi boş olmamalı");
        
        // Test müşterisini kontrol et
        boolean testMusteriBulundu = false;
        for (Musteri musteri : musteriler) {
            if (musteri.getId().equals(testMusteriId)) {
                assertEquals("Test Müşteri", musteri.getMusteri_adi());
                assertEquals("Test Soyad", musteri.getMusteri_soyadi());
                assertEquals("Test Adres", musteri.getMusteri_adresi());
                assertEquals(1000, musteri.getMusteri_alacak());
                assertEquals(500, musteri.getMusteri_borc());
                assertEquals(10, musteri.getMusteri_iskonto());
                assertEquals(testUrunId, musteri.getUrun_id());
                testMusteriBulundu = true;
                break;
            }
        }
        
        assertTrue(testMusteriBulundu, "Test müşterisi bulunamadı");
    }
    
    @Test
    void testMusteriSil() {
        if (musteriDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Önce müşteriyi ekleyelim
        ObjectId silinecekId = new ObjectId();
        ObjectId urunId = new ObjectId();
        Musteri silinecekMusteri = new Musteri(silinecekId, "Silinecek Müşteri", "Silinecek Soyad", "Silinecek Adres", 500, 200, 5, urunId);
        musteriDao.MusteriEkle(silinecekMusteri);
        
        // Şimdi müşteriyi silelim
        musteriDao.MusteriSil(silinecekId.toString());
        
        // Müşterinin silindiğini kontrol edelim
        List<Musteri> musteriler = musteriDao.MusteriList();
        boolean musteriHalaDuruyorMu = false;
        
        for (Musteri musteri : musteriler) {
            if (musteri.getId().equals(silinecekId)) {
                musteriHalaDuruyorMu = true;
                break;
            }
        }
        
        assertFalse(musteriHalaDuruyorMu, "Müşteri silinmemiş");
    }
    
    @Test
    void testMusteriGuncelle() {
        if (musteriDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Güncellenecek müşteriyi ekleyelim
        ObjectId guncellenecekId = new ObjectId();
        ObjectId ilkUrunId = new ObjectId();
        Musteri ilkMusteri = new Musteri(guncellenecekId, "İlk Müşteri", "İlk Soyad", "İlk Adres", 800, 300, 8, ilkUrunId);
        musteriDao.MusteriEkle(ilkMusteri);
        
        // Şimdi müşteriyi güncelleyelim
        ObjectId yeniUrunId = new ObjectId();
        Musteri guncelMusteri = new Musteri(guncellenecekId, "Güncel Müşteri", "Güncel Soyad", "Güncel Adres", 1200, 600, 12, yeniUrunId);
        musteriDao.MusteriGuncelle(guncellenecekId.toString(), guncelMusteri);
        
        // Güncellemenin yapıldığını kontrol edelim
        List<Musteri> musteriler = musteriDao.MusteriList();
        boolean guncellemeTamamMi = false;
        
        for (Musteri musteri : musteriler) {
            if (musteri.getId().equals(guncellenecekId)) {
                assertEquals("Güncel Müşteri", musteri.getMusteri_adi());
                assertEquals("Güncel Soyad", musteri.getMusteri_soyadi());
                assertEquals("Güncel Adres", musteri.getMusteri_adresi());
                assertEquals(1200, musteri.getMusteri_alacak());
                assertEquals(600, musteri.getMusteri_borc());
                assertEquals(12, musteri.getMusteri_iskonto());
                assertEquals(yeniUrunId, musteri.getUrun_id());
                guncellemeTamamMi = true;
                break;
            }
        }
        
        assertTrue(guncellemeTamamMi, "Müşteri güncellenmemiş");
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