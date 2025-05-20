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

public class StokIadeForm extends JFrame {
    private JTextField txtIadeNot;
    private JComboBox<String> cmbUrunler;
    private JTable tblStokIadeler;
    private DefaultTableModel tableModel;
    private JButton btnEkle;
    private JButton btnGuncelle;
    private JButton btnSil;
    private JButton btnTemizle;

    private StokIadeDao stokIadeDao;
    private UrunDao urunDao;
    private ObjectId selectedStokIadeId;
    private Map<String, ObjectId> urunMap = new java.util.HashMap<>();

    public StokIadeForm(MongoDatabase database) {
        stokIadeDao = new StokIadeDao(database);
        urunDao = new UrunDao(database);
        
        // Form ayarları
        setTitle("Stok İade Yönetimi");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Ana panel oluşturma
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form paneli
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Stok İade Bilgileri"));
        
        formPanel.add(new JLabel("İade Not:"));
        txtIadeNot = new JTextField();
        formPanel.add(txtIadeNot);
        
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
        tableModel.addColumn("İade Not");
        tableModel.addColumn("Ürün Adı");
        
        tblStokIadeler = new JTable(tableModel);
        tblStokIadeler.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblStokIadeler);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Stok İade Listesi"));
        
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
        loadStokIadeData();
        
        // Olay dinleyiciler
        tblStokIadeler.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblStokIadeler.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedStokIadeId = new ObjectId(tableModel.getValueAt(selectedRow, 0).toString());
                    txtIadeNot.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    
                    // Ürünü seçme
                    String urunAdi = tableModel.getValueAt(selectedRow, 2).toString();
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
                addStokIade();
            }
        });
        
        btnGuncelle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStokIade();
            }
        });
        
        btnSil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStokIade();
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
    
    private void loadStokIadeData() {
        // Tabloyu temizle
        tableModel.setRowCount(0);
        
        // Stok iadeleri yükle
        List<StokIade> stokIadeList = stokIadeDao.StokIadeList();
        for (StokIade stokIade : stokIadeList) {
            // Ürün ID'sine göre ürün adını bul
            String urunAdi = "";
            if (stokIade.getUrun_id() != null) {
                for (Urun urun : urunDao.UrunList()) {
                    if (urun.getId().equals(stokIade.getUrun_id())) {
                        urunAdi = urun.getUrun_adi();
                        break;
                    }
                }
            }
            
            tableModel.addRow(new Object[]{
                stokIade.getId().toString(),
                stokIade.getSi_not(),
                urunAdi
            });
        }
    }
    
    private void addStokIade() {
        try {
            String iadeNot = txtIadeNot.getText();
            
            if (iadeNot.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen iade notu giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Ürün ID'sini alma
            ObjectId urunId = null;
            if (cmbUrunler.getSelectedIndex() != -1) {
                String selectedUrun = cmbUrunler.getSelectedItem().toString();
                urunId = urunMap.get(selectedUrun);
            }
            
            StokIade stokIade = new StokIade();
            stokIade.setId(new ObjectId());
            stokIade.setSi_not(iadeNot);
            stokIade.setUrun_id(urunId);
            
            stokIadeDao.StokIadeEkle(stokIade);
            
            loadStokIadeData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Stok iade başarıyla eklendi.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Stok iade eklenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStokIade() {
        try {
            if (selectedStokIadeId == null) {
                JOptionPane.showMessageDialog(this, "Lütfen güncellenecek bir stok iade seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String iadeNot = txtIadeNot.getText();
            
            if (iadeNot.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen iade notu giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Ürün ID'sini alma
            ObjectId urunId = null;
            if (cmbUrunler.getSelectedIndex() != -1) {
                String selectedUrun = cmbUrunler.getSelectedItem().toString();
                urunId = urunMap.get(selectedUrun);
            }
            
            StokIade stokIade = new StokIade();
            stokIade.setId(selectedStokIadeId);
            stokIade.setSi_not(iadeNot);
            stokIade.setUrun_id(urunId);
            
            stokIadeDao.StokIadeGuncelle(selectedStokIadeId.toString(), stokIade);
            
            loadStokIadeData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Stok iade başarıyla güncellendi.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Stok iade güncellenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteStokIade() {
        try {
            if (selectedStokIadeId == null) {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek bir stok iade seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Seçili stok iadeyi silmek istediğinize emin misiniz?", "Stok İade Sil", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                stokIadeDao.StokIadeSil(selectedStokIadeId.toString());
                loadStokIadeData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Stok iade başarıyla silindi.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Stok iade silinirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        txtIadeNot.setText("");
        cmbUrunler.setSelectedIndex(-1);
        selectedStokIadeId = null;
        tblStokIadeler.clearSelection();
    }
} 