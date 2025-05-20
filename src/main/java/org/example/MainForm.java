package org.example;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainForm extends JFrame {
    private JButton btnUrunYonetimi;
    private JButton btnPersonelYonetimi;
    private JButton btnMusteriYonetimi;
    private JButton btnStokYonetimi;
    private JButton btnStokIadeYonetimi;
    private JButton btnStokGrafik;

    private MongoClient mongoClient;
    private MongoDatabase database;

    public MainForm() {
        try {
            // MongoDB bağlantısı
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            
            // Veritabanı adını değiştir (stok_takip_sistemi -> stok_takip)
            database = mongoClient.getDatabase("stok_takip_sistemi");
            
            // Koleksiyonların varlığını kontrol et, yoksa oluştur
            if (!collectionExists("urunler")) {
                database.createCollection("urunler");
                System.out.println("urun koleksiyonu oluşturuldu");
            }
            if (!collectionExists("personel")) {
                database.createCollection("personel");
                System.out.println("personel koleksiyonu oluşturuldu");
            }
            if (!collectionExists("musteriler")) {
                database.createCollection("musteriler");
                System.out.println("musteri koleksiyonu oluşturuldu");
            }
            if (!collectionExists("stok_satis")) {
                database.createCollection("stok_satis");
                System.out.println("stokSatis koleksiyonu oluşturuldu");
            }
            if (!collectionExists("stok_iade")) {
                database.createCollection("stok_iade");
                System.out.println("stokIade koleksiyonu oluşturuldu");
            }
            
            System.out.println("MongoDB bağlantısı başarılı: " + database.getName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "MongoDB bağlantı hatası: " + e.getMessage() + 
                "\nLütfen MongoDB servisinin çalıştığından emin olun.",
                "Veritabanı Hatası", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // Form ayarları
        setTitle("Stok Takip Sistemi");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Layout oluşturma
        setLayout(new BorderLayout());
        
        // Başlık panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(800, 100));
        JLabel lblTitle = new JLabel("STOK TAKİP SİSTEMİ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        
        // Ana menü panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(6, 1, 10, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));
        
        btnUrunYonetimi = createButton("Ürün Yönetimi");
        btnPersonelYonetimi = createButton("Personel Yönetimi");
        btnMusteriYonetimi = createButton("Müşteri Yönetimi");
        btnStokYonetimi = createButton("Stok Satış Yönetimi");
        btnStokIadeYonetimi = createButton("Stok İade Yönetimi");
        btnStokGrafik = createButton("Stok Grafik Gösterimi");
        
        menuPanel.add(btnUrunYonetimi);
        menuPanel.add(btnPersonelYonetimi);
        menuPanel.add(btnMusteriYonetimi);
        menuPanel.add(btnStokYonetimi);
        menuPanel.add(btnStokIadeYonetimi);
        menuPanel.add(btnStokGrafik);
        
        // Alt bilgi panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(41, 128, 185));
        footerPanel.setPreferredSize(new Dimension(800, 50));
        JLabel lblFooter = new JLabel("© 2025 Stok Takip Sistemi");
        lblFooter.setForeground(Color.WHITE);
        footerPanel.add(lblFooter);
        
        // Panelleri forma ekleme
        add(headerPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        
        // Buton olayları
        btnUrunYonetimi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UrunForm urunForm = new UrunForm(database);
                urunForm.setVisible(true);
            }
        });
        
        btnPersonelYonetimi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PersonelForm personelForm = new PersonelForm(database);
                personelForm.setVisible(true);
            }
        });
        
        btnMusteriYonetimi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MusteriForm musteriForm = new MusteriForm(database);
                musteriForm.setVisible(true);
            }
        });
        
        btnStokYonetimi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StokSatisForm stokSatisForm = new StokSatisForm(database);
                stokSatisForm.setVisible(true);
            }
        });
        
        btnStokIadeYonetimi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StokIadeForm stokIadeForm = new StokIadeForm(database);
                stokIadeForm.setVisible(true);
            }
        });
        
        btnStokGrafik.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UrunStokGrafikForm grafikForm = new UrunStokGrafikForm(database);
                grafikForm.setVisible(true);
            }
        });
        
        // MongoDB bağlantısını kapatmak için pencere kapatma olayı
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (mongoClient != null) {
                    mongoClient.close();
                    System.out.println("MongoDB bağlantısı kapatıldı");
                }
            }
        });
    }
    
    private boolean collectionExists(String collectionName) {
        for (String name : database.listCollectionNames()) {
            if (name.equals(collectionName)) {
                return true;
            }
        }
        return false;
    }
    
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    // Ana metot
    public static void main(String[] args) {
        try {
            // FlatLaf tema uygulaması
            FlatCobalt2IJTheme.setup();
            
            // Sistem teması ile uyumlu tema
            // UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainForm mainForm = new MainForm();
                mainForm.setVisible(true);
            }
        });
    }
} 