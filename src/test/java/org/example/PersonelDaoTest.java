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

class PersonelDaoTest {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private PersonelDao personelDao;
    private ObjectId testPersonelId;

    @BeforeEach
    void setUp() {
        try {
            // Test için MongoDB bağlantısı
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("stok_takip_test");
            
            // Test koleksiyonunu oluştur
            if (!collectionExists("personel")) {
                database.createCollection("personel");
            } else {
                // Test verilerini temizle
                database.getCollection("personel").drop();
                database.createCollection("personel");
            }
            
            personelDao = new PersonelDao(database);
            
            // Test için personel ekle
            testPersonelId = new ObjectId();
            Personel testPersonel = new Personel(testPersonelId, "Test Ad", "Test Soyad", "Test Adres", 5000);
            personelDao.PersonelEkle(testPersonel);
            
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
    void testPersonelObjesiOlusturma() {
        // Yeni bir personel oluştur
        ObjectId id = new ObjectId();
        Personel personel = new Personel(id, "Ahmet", "Yılmaz", "İstanbul", 5000);
        
        // Kontrolleri yap
        assertEquals(id, personel.getId());
        assertEquals("Ahmet", personel.getPersonel_adi());
        assertEquals("Yılmaz", personel.getPersonel_soyadi());
        assertEquals("İstanbul", personel.getPersonel_adresi());
        assertEquals(5000, personel.getPersonel_maas());
    }
    
    @Test
    void testPersonelSetterGetter() {
        // Boş personel oluştur ve set metodlarını kullan
        Personel personel = new Personel();
        ObjectId id = new ObjectId();
        
        personel.setId(id);
        personel.setPersonel_adi("Mehmet");
        personel.setPersonel_soyadi("Kaya");
        personel.setPersonel_adresi("Ankara");
        personel.setPersonel_maas(6000);
        
        // Kontrolleri yap
        assertEquals(id, personel.getId());
        assertEquals("Mehmet", personel.getPersonel_adi());
        assertEquals("Kaya", personel.getPersonel_soyadi());
        assertEquals("Ankara", personel.getPersonel_adresi());
        assertEquals(6000, personel.getPersonel_maas());
    }
    
    @Test
    void testPersonelEkle() {
        if (personelDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Yeni bir personel oluştur
        ObjectId id = new ObjectId();
        Personel yeniPersonel = new Personel(id, "Yeni Ad", "Yeni Soyad", "Yeni Adres", 7000);
        
        // Personeli ekle
        personelDao.PersonelEkle(yeniPersonel);
        
        // Eklenen personeli bul ve kontrol et
        List<Personel> personeller = personelDao.PersonelList();
        boolean bulundu = false;
        
        for (Personel personel : personeller) {
            if (personel.getId().equals(id)) {
                assertEquals("Yeni Ad", personel.getPersonel_adi());
                assertEquals("Yeni Soyad", personel.getPersonel_soyadi());
                assertEquals("Yeni Adres", personel.getPersonel_adresi());
                assertEquals(7000, personel.getPersonel_maas());
                bulundu = true;
                break;
            }
        }
        
        assertTrue(bulundu, "Eklenen personel veritabanında bulunamadı");
    }
    
    @Test
    void testPersonelList() {
        if (personelDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Personel listesini al
        List<Personel> personeller = personelDao.PersonelList();
        
        // En az bir personel olmalı (setUp'ta eklediğimiz test personeli)
        assertFalse(personeller.isEmpty(), "Personel listesi boş olmamalı");
        
        // Test personelini kontrol et
        boolean testPersonelBulundu = false;
        for (Personel personel : personeller) {
            if (personel.getId().equals(testPersonelId)) {
                assertEquals("Test Ad", personel.getPersonel_adi());
                assertEquals("Test Soyad", personel.getPersonel_soyadi());
                assertEquals("Test Adres", personel.getPersonel_adresi());
                assertEquals(5000, personel.getPersonel_maas());
                testPersonelBulundu = true;
                break;
            }
        }
        
        assertTrue(testPersonelBulundu, "Test personeli bulunamadı");
    }
    
    @Test
    void testPersonelSil() {
        if (personelDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Önce personeli ekleyelim
        ObjectId silinecekId = new ObjectId();
        Personel silinecekPersonel = new Personel(silinecekId, "Silinecek Ad", "Silinecek Soyad", "Silinecek Adres", 4000);
        personelDao.PersonelEkle(silinecekPersonel);
        
        // Şimdi personeli silelim
        personelDao.PersonelSil(silinecekId.toString());
        
        // Personelin silindiğini kontrol edelim
        List<Personel> personeller = personelDao.PersonelList();
        boolean personelHalaDuruyorMu = false;
        
        for (Personel personel : personeller) {
            if (personel.getId().equals(silinecekId)) {
                personelHalaDuruyorMu = true;
                break;
            }
        }
        
        assertFalse(personelHalaDuruyorMu, "Personel silinmemiş");
    }
    
    @Test
    void testPersonelGuncelle() {
        if (personelDao == null) {
            return; // MongoDB bağlantısı yoksa testi atla
        }
        
        // Güncellenecek personeli ekleyelim
        ObjectId guncellenecekId = new ObjectId();
        Personel ilkPersonel = new Personel(guncellenecekId, "İlk Ad", "İlk Soyad", "İlk Adres", 3000);
        personelDao.PersonelEkle(ilkPersonel);
        
        // Şimdi personeli güncelleyelim
        Personel guncelPersonel = new Personel(guncellenecekId, "Güncel Ad", "Güncel Soyad", "Güncel Adres", 8000);
        personelDao.PersonelGuncelle(guncellenecekId.toString(), guncelPersonel);
        
        // Güncellemenin yapıldığını kontrol edelim
        List<Personel> personeller = personelDao.PersonelList();
        boolean guncellemeTamamMi = false;
        
        for (Personel personel : personeller) {
            if (personel.getId().equals(guncellenecekId)) {
                assertEquals("Güncel Ad", personel.getPersonel_adi());
                assertEquals("Güncel Soyad", personel.getPersonel_soyadi());
                assertEquals("Güncel Adres", personel.getPersonel_adresi());
                assertEquals(8000, personel.getPersonel_maas());
                guncellemeTamamMi = true;
                break;
            }
        }
        
        assertTrue(guncellemeTamamMi, "Personel güncellenmemiş");
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