package org.example;

import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class MusteriForm extends JFrame {
    private JTextField txtMusteriAdi;
    private JTextField txtMusteriSoyadi;
    private JTextField txtMusteriAdresi;
    private JTextField txtMusteriAlacak;
    private JTextField txtMusteriBorc;
    private JTextField txtMusteriIskonto;
    private JComboBox<String> cmbUrunler;
    private JTable tblMusteriler;
    private DefaultTableModel tableModel;
    private JButton btnEkle;
    private JButton btnGuncelle;
    private JButton btnSil;
    private JButton btnTemizle;

    private MusteriDao musteriDao;
    private UrunDao urunDao;
    private ObjectId selectedMusteriId;
    private ObjectId selectedUrunId;
    private Map<String, ObjectId> urunMap = new java.util.HashMap<>();

    public MusteriForm(MongoDatabase database) {
        musteriDao = new MusteriDao(database);
        urunDao = new UrunDao(database);
        
        // Form ayarları
        setTitle("Müşteri Yönetimi");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Ana panel oluşturma
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form paneli
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Müşteri Bilgileri"));
        
        formPanel.add(new JLabel("Müşteri Adı:"));
        txtMusteriAdi = new JTextField();
        formPanel.add(txtMusteriAdi);
        
        formPanel.add(new JLabel("Müşteri Soyadı:"));
        txtMusteriSoyadi = new JTextField();
        formPanel.add(txtMusteriSoyadi);
        
        formPanel.add(new JLabel("Müşteri Adresi:"));
        txtMusteriAdresi = new JTextField();
        formPanel.add(txtMusteriAdresi);
        
        formPanel.add(new JLabel("Müşteri Alacak:"));
        txtMusteriAlacak = new JTextField();
        formPanel.add(txtMusteriAlacak);
        
        formPanel.add(new JLabel("Müşteri Borç:"));
        txtMusteriBorc = new JTextField();
        formPanel.add(txtMusteriBorc);
        
        formPanel.add(new JLabel("Müşteri İskonto:"));
        txtMusteriIskonto = new JTextField();
        formPanel.add(txtMusteriIskonto);
        
        formPanel.add(new JLabel("Ürün:"));
        cmbUrunler = new JComboBox<>();
        formPanel.add(cmbUrunler);
        
        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        btnEkle = new JButton("Ekle");
        btnEkle.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(btnEkle);
        
        btnGuncelle = new JButton("Güncelle");
        btnGuncelle.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(btnGuncelle);
        
        btnSil = new JButton("Sil");
        btnSil.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(btnSil);
        
        btnTemizle = new JButton("Temizle");
        btnTemizle.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(btnTemizle);
        
        // Tablo oluşturma
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Adı");
        tableModel.addColumn("Soyadı");
        tableModel.addColumn("Adresi");
        tableModel.addColumn("Alacak");
        tableModel.addColumn("Borç");
        tableModel.addColumn("İskonto");
        tableModel.addColumn("Ürün Adı");
        
        tblMusteriler = new JTable(tableModel);
        tblMusteriler.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblMusteriler);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Müşteri Listesi"));
        
        // Panelleri ana panele ekleme
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Ana paneli forma ekleme
        add(mainPanel);
        
        // Ürünleri yükleme
        loadUrunler();
        
        // Verileri yükleme
        loadMusteriData();
        
        // Olay dinleyiciler
        tblMusteriler.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblMusteriler.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedMusteriId = new ObjectId(tableModel.getValueAt(selectedRow, 0).toString());
                    txtMusteriAdi.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtMusteriSoyadi.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtMusteriAdresi.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    txtMusteriAlacak.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    txtMusteriBorc.setText(tableModel.getValueAt(selectedRow, 5).toString());
                    txtMusteriIskonto.setText(tableModel.getValueAt(selectedRow, 6).toString());
                    
                    // Ürünü seçme
                    String urunAdi = tableModel.getValueAt(selectedRow, 7).toString();
                    for (int i = 0; i < cmbUrunler.getItemCount(); i++) {
                        String item = cmbUrunler.getItemAt(i);
                        if (item.equals(urunAdi)) {
                            cmbUrunler.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });
        
        btnEkle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMusteri();
            }
        });
        
        btnGuncelle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMusteri();
            }
        });
        
        btnSil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMusteri();
            }
        });
        
        btnTemizle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
    }
    
    private void loadUrunler() {
        cmbUrunler.removeAllItems();
        urunMap.clear(); // Mevcut eşleşmeleri temizle
        List<Urun> urunList = urunDao.UrunList();
        for (Urun urun : urunList) {
            // Sadece ürün adını göster, ID'yi arka planda tut
            String urunAdi = urun.getUrun_adi();
            cmbUrunler.addItem(urunAdi);
            // ID'yi ürün adı ile eşleştirerek map'te sakla
            urunMap.put(urunAdi, urun.getId());
        }
    }
    
    private void loadMusteriData() {
        // Tabloyu temizle
        tableModel.setRowCount(0);
        
        // Müşterileri yükle
        List<Musteri> musteriList = musteriDao.MusteriList();
        for (Musteri musteri : musteriList) {
            // Ürün ID'sine göre ürün adını bul
            String urunAdi = "";
            if (musteri.getUrun_id() != null) {
                for (Urun urun : urunDao.UrunList()) {
                    if (urun.getId().equals(musteri.getUrun_id())) {
                        urunAdi = urun.getUrun_adi();
                        break;
                    }
                }
            }
            
            tableModel.addRow(new Object[]{
                musteri.getId().toString(),
                musteri.getMusteri_adi(),
                musteri.getMusteri_soyadi(),
                musteri.getMusteri_adresi(),
                musteri.getMusteri_alacak(),
                musteri.getMusteri_borc(),
                musteri.getMusteri_iskonto(),
                urunAdi
            });
        }
    }
    
    private void addMusteri() {
        try {
            String musteriAdi = txtMusteriAdi.getText();
            String musteriSoyadi = txtMusteriSoyadi.getText();
            String musteriAdresi = txtMusteriAdresi.getText();
            int musteriAlacak = Integer.parseInt(txtMusteriAlacak.getText());
            int musteriBorc = Integer.parseInt(txtMusteriBorc.getText());
            int musteriIskonto = Integer.parseInt(txtMusteriIskonto.getText());
            
            if (musteriAdi.isEmpty() || musteriSoyadi.isEmpty() || musteriAdresi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Ürün ID'sini alma
            ObjectId urunId = null;
            if (cmbUrunler.getSelectedIndex() != -1) {
                String selectedUrun = cmbUrunler.getSelectedItem().toString();
                urunId = urunMap.get(selectedUrun);
            }
            
            Musteri musteri = new Musteri();
            musteri.setId(new ObjectId());
            musteri.setMusteri_adi(musteriAdi);
            musteri.setMusteri_soyadi(musteriSoyadi);
            musteri.setMusteri_adresi(musteriAdresi);
            musteri.setMusteri_alacak(musteriAlacak);
            musteri.setMusteri_borc(musteriBorc);
            musteri.setMusteri_iskonto(musteriIskonto);
            musteri.setUrun_id(urunId);
            
            musteriDao.MusteriEkle(musteri);
            
            loadMusteriData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Müşteri başarıyla eklendi.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Alacak, borç ve iskonto alanları sayısal değer olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Müşteri eklenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateMusteri() {
        try {
            if (selectedMusteriId == null) {
                JOptionPane.showMessageDialog(this, "Lütfen güncellenecek bir müşteri seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String musteriAdi = txtMusteriAdi.getText();
            String musteriSoyadi = txtMusteriSoyadi.getText();
            String musteriAdresi = txtMusteriAdresi.getText();
            int musteriAlacak = Integer.parseInt(txtMusteriAlacak.getText());
            int musteriBorc = Integer.parseInt(txtMusteriBorc.getText());
            int musteriIskonto = Integer.parseInt(txtMusteriIskonto.getText());
            
            if (musteriAdi.isEmpty() || musteriSoyadi.isEmpty() || musteriAdresi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Ürün ID'sini alma
            ObjectId urunId = null;
            if (cmbUrunler.getSelectedIndex() != -1) {
                String selectedUrun = cmbUrunler.getSelectedItem().toString();
                urunId = urunMap.get(selectedUrun);
            }
            
            Musteri musteri = new Musteri();
            musteri.setId(selectedMusteriId);
            musteri.setMusteri_adi(musteriAdi);
            musteri.setMusteri_soyadi(musteriSoyadi);
            musteri.setMusteri_adresi(musteriAdresi);
            musteri.setMusteri_alacak(musteriAlacak);
            musteri.setMusteri_borc(musteriBorc);
            musteri.setMusteri_iskonto(musteriIskonto);
            musteri.setUrun_id(urunId);
            
            musteriDao.MusteriGuncelle(selectedMusteriId.toString(), musteri);
            
            loadMusteriData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Müşteri başarıyla güncellendi.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Alacak, borç ve iskonto alanları sayısal değer olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Müşteri güncellenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteMusteri() {
        try {
            if (selectedMusteriId == null) {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek bir müşteri seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Seçili müşteriyi silmek istediğinize emin misiniz?", "Müşteri Sil", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                musteriDao.MusteriSil(selectedMusteriId.toString());
                loadMusteriData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Müşteri başarıyla silindi.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Müşteri silinirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        txtMusteriAdi.setText("");
        txtMusteriSoyadi.setText("");
        txtMusteriAdresi.setText("");
        txtMusteriAlacak.setText("");
        txtMusteriBorc.setText("");
        txtMusteriIskonto.setText("");
        cmbUrunler.setSelectedIndex(-1);
        selectedMusteriId = null;
        tblMusteriler.clearSelection();
    }
} 