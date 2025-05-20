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

public class StokSatisForm extends JFrame {
    private JTextField txtBirimFiyat;
    private JTextField txtStokAdet;
    private JTextField txtStokFiyat;
    private JComboBox<String> cmbUrunler;
    private JTable tblStokSatislar;
    private DefaultTableModel tableModel;
    private JButton btnEkle;
    private JButton btnGuncelle;
    private JButton btnSil;
    private JButton btnTemizle;
    private JButton btnHesapla;

    private StokSatisDao stokSatisDao;
    private UrunDao urunDao;
    private ObjectId selectedStokSatisId;
    private Map<String, ObjectId> urunMap = new java.util.HashMap<>();

    public StokSatisForm(MongoDatabase database) {
        stokSatisDao = new StokSatisDao(database);
        urunDao = new UrunDao(database);
        
        // Form ayarları
        setTitle("Stok Satış Yönetimi");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Ana panel oluşturma
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form paneli
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Stok Satış Bilgileri"));
        
        formPanel.add(new JLabel("Birim Fiyat:"));
        txtBirimFiyat = new JTextField();
        formPanel.add(txtBirimFiyat);
        
        formPanel.add(new JLabel("Stok Adet:"));
        txtStokAdet = new JTextField();
        formPanel.add(txtStokAdet);
        
        formPanel.add(new JLabel("Stok Fiyat:"));
        txtStokFiyat = new JTextField();
        formPanel.add(txtStokFiyat);
        
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
        
        btnHesapla = new JButton("Fiyat Hesapla");
        btnHesapla.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(btnHesapla);
        
        // Tablo oluşturma
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Birim Fiyat");
        tableModel.addColumn("Stok Adet");
        tableModel.addColumn("Stok Fiyat");
        tableModel.addColumn("Ürün Adı");
        
        tblStokSatislar = new JTable(tableModel);
        tblStokSatislar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblStokSatislar);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Stok Satış Listesi"));
        
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
        loadStokSatisData();
        
        // Olay dinleyiciler
        tblStokSatislar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblStokSatislar.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedStokSatisId = new ObjectId(tableModel.getValueAt(selectedRow, 0).toString());
                    txtBirimFiyat.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtStokAdet.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtStokFiyat.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    
                    // Ürünü seçme
                    String urunAdi = tableModel.getValueAt(selectedRow, 4).toString();
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
                addStokSatis();
            }
        });
        
        btnGuncelle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStokSatis();
            }
        });
        
        btnSil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStokSatis();
            }
        });
        
        btnTemizle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        btnHesapla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hesaplaFiyat();
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
    
    private void loadStokSatisData() {
        // Tabloyu temizle
        tableModel.setRowCount(0);
        
        // Stok satışları yükle
        List<StokSatis> stokSatisList = stokSatisDao.StokSatisList();
        for (StokSatis stokSatis : stokSatisList) {
            // Ürün ID'sine göre ürün adını bul
            String urunAdi = "";
            if (stokSatis.getUrun_id() != null) {
                for (Urun urun : urunDao.UrunList()) {
                    if (urun.getId().equals(stokSatis.getUrun_id())) {
                        urunAdi = urun.getUrun_adi();
                        break;
                    }
                }
            }
            
            tableModel.addRow(new Object[]{
                stokSatis.getId().toString(),
                stokSatis.getBirim_fiyat(),
                stokSatis.getStok_adet(),
                stokSatis.getStok_fiyat(),
                urunAdi
            });
        }
    }
    
    private void hesaplaFiyat() {
        try {
            int birimFiyat = Integer.parseInt(txtBirimFiyat.getText());
            int stokAdet = Integer.parseInt(txtStokAdet.getText());
            
            int toplamFiyat = birimFiyat * stokAdet;
            txtStokFiyat.setText(String.valueOf(toplamFiyat));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Birim fiyat ve stok adeti sayısal değer olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addStokSatis() {
        try {
            int birimFiyat = Integer.parseInt(txtBirimFiyat.getText());
            int stokAdet = Integer.parseInt(txtStokAdet.getText());
            int stokFiyat = Integer.parseInt(txtStokFiyat.getText());
            
            // Ürün ID'sini alma
            ObjectId urunId = null;
            if (cmbUrunler.getSelectedIndex() != -1) {
                String selectedUrun = cmbUrunler.getSelectedItem().toString();
                urunId = urunMap.get(selectedUrun);
            }
            
            StokSatis stokSatis = new StokSatis();
            stokSatis.setId(new ObjectId());
            stokSatis.setBirim_fiyat(birimFiyat);
            stokSatis.setStok_adet(stokAdet);
            stokSatis.setStok_fiyat(stokFiyat);
            stokSatis.setUrun_id(urunId);
            
            stokSatisDao.StokEkle(stokSatis);
            
            loadStokSatisData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Stok satış başarıyla eklendi.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tüm sayısal alanlar doğru formatta olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Stok satış eklenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStokSatis() {
        try {
            if (selectedStokSatisId == null) {
                JOptionPane.showMessageDialog(this, "Lütfen güncellenecek bir stok satış seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int birimFiyat = Integer.parseInt(txtBirimFiyat.getText());
            int stokAdet = Integer.parseInt(txtStokAdet.getText());
            int stokFiyat = Integer.parseInt(txtStokFiyat.getText());
            
            // Ürün ID'sini alma
            ObjectId urunId = null;
            if (cmbUrunler.getSelectedIndex() != -1) {
                String selectedUrun = cmbUrunler.getSelectedItem().toString();
                urunId = urunMap.get(selectedUrun);
            }
            
            StokSatis stokSatis = new StokSatis();
            stokSatis.setId(selectedStokSatisId);
            stokSatis.setBirim_fiyat(birimFiyat);
            stokSatis.setStok_adet(stokAdet);
            stokSatis.setStok_fiyat(stokFiyat);
            stokSatis.setUrun_id(urunId);
            
            stokSatisDao.StokGuncelle(selectedStokSatisId.toString(), stokSatis);
            
            loadStokSatisData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Stok satış başarıyla güncellendi.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tüm sayısal alanlar doğru formatta olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Stok satış güncellenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteStokSatis() {
        try {
            if (selectedStokSatisId == null) {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek bir stok satış seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Seçili stok satışı silmek istediğinize emin misiniz?", "Stok Satış Sil", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                stokSatisDao.StokSil(selectedStokSatisId.toString());
                loadStokSatisData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Stok satış başarıyla silindi.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Stok satış silinirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        txtBirimFiyat.setText("");
        txtStokAdet.setText("");
        txtStokFiyat.setText("");
        cmbUrunler.setSelectedIndex(-1);
        selectedStokSatisId = null;
        tblStokSatislar.clearSelection();
    }
} 