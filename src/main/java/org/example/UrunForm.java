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

public class UrunForm extends JFrame {
    private JTextField txtUrunAdi;
    private JTextField txtUrunBirimi;
    private JTextField txtUrunFiyati;
    private JTextField txtUrunRaf;
    private JTable tblUrunler;
    private DefaultTableModel tableModel;
    private JButton btnEkle;
    private JButton btnGuncelle;
    private JButton btnSil;
    private JButton btnTemizle;

    private UrunDao urunDao;
    private ObjectId selectedUrunId;

    public UrunForm(MongoDatabase database) {
        urunDao = new UrunDao(database);
        
        // Form ayarları
        setTitle("Ürün Yönetimi");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Ana panel oluşturma
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form paneli
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Ürün Bilgileri"));
        
        formPanel.add(new JLabel("Ürün Adı:"));
        txtUrunAdi = new JTextField();
        formPanel.add(txtUrunAdi);
        
        formPanel.add(new JLabel("Ürün Birimi:"));
        txtUrunBirimi = new JTextField();
        formPanel.add(txtUrunBirimi);
        
        formPanel.add(new JLabel("Ürün Fiyatı:"));
        txtUrunFiyati = new JTextField();
        formPanel.add(txtUrunFiyati);
        
        formPanel.add(new JLabel("Ürün Raf:"));
        txtUrunRaf = new JTextField();
        formPanel.add(txtUrunRaf);
        
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
        tableModel.addColumn("Ürün Adı");
        tableModel.addColumn("Ürün Birimi");
        tableModel.addColumn("Ürün Fiyatı");
        tableModel.addColumn("Ürün Raf");
        
        tblUrunler = new JTable(tableModel);
        tblUrunler.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblUrunler);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Ürün Listesi"));
        
        // Panelleri ana panele ekleme
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Ana paneli forma ekleme
        add(mainPanel);
        
        // Verileri yükleme
        loadUrunData();
        
        // Olay dinleyiciler
        tblUrunler.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblUrunler.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedUrunId = new ObjectId(tableModel.getValueAt(selectedRow, 0).toString());
                    txtUrunAdi.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtUrunBirimi.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtUrunFiyati.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    txtUrunRaf.setText(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });
        
        btnEkle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUrun();
            }
        });
        
        btnGuncelle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUrun();
            }
        });
        
        btnSil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUrun();
            }
        });
        
        btnTemizle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
    }
    
    private void loadUrunData() {
        // Tabloyu temizle
        tableModel.setRowCount(0);
        
        // Ürünleri yükle
        List<Urun> urunList = urunDao.UrunList();
        for (Urun urun : urunList) {
            tableModel.addRow(new Object[]{
                urun.getId().toString(),
                urun.getUrun_adi(),
                urun.getUrun_birimi(),
                urun.getUrun_fiyati(),
                urun.getUrun_raf()
            });
        }
    }
    
    private void addUrun() {
        try {
            String urunAdi = txtUrunAdi.getText();
            String urunBirimi = txtUrunBirimi.getText();
            int urunFiyati = Integer.parseInt(txtUrunFiyati.getText());
            String urunRaf = txtUrunRaf.getText();
            
            if (urunAdi.isEmpty() || urunBirimi.isEmpty() || urunRaf.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Urun urun = new Urun();
            urun.setId(new ObjectId());
            urun.setUrun_adi(urunAdi);
            urun.setUrun_birimi(urunBirimi);
            urun.setUrun_fiyati(urunFiyati);
            urun.setUrun_raf(urunRaf);
            
            urunDao.UrunEkle(urun);
            
            loadUrunData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Ürün başarıyla eklendi.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Fiyat alanı sayısal bir değer olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ürün eklenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateUrun() {
        try {
            if (selectedUrunId == null) {
                JOptionPane.showMessageDialog(this, "Lütfen güncellenecek bir ürün seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String urunAdi = txtUrunAdi.getText();
            String urunBirimi = txtUrunBirimi.getText();
            int urunFiyati = Integer.parseInt(txtUrunFiyati.getText());
            String urunRaf = txtUrunRaf.getText();
            
            if (urunAdi.isEmpty() || urunBirimi.isEmpty() || urunRaf.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Urun urun = new Urun();
            urun.setId(selectedUrunId);
            urun.setUrun_adi(urunAdi);
            urun.setUrun_birimi(urunBirimi);
            urun.setUrun_fiyati(urunFiyati);
            urun.setUrun_raf(urunRaf);
            
            urunDao.UrunGuncelle(selectedUrunId.toString(), urun);
            
            loadUrunData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Ürün başarıyla güncellendi.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Fiyat alanı sayısal bir değer olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ürün güncellenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteUrun() {
        try {
            if (selectedUrunId == null) {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek bir ürün seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Seçili ürünü silmek istediğinize emin misiniz?", "Ürün Sil", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                urunDao.UrunSil(selectedUrunId.toString());
                loadUrunData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Ürün başarıyla silindi.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ürün silinirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        txtUrunAdi.setText("");
        txtUrunBirimi.setText("");
        txtUrunFiyati.setText("");
        txtUrunRaf.setText("");
        selectedUrunId = null;
        tblUrunler.clearSelection();
    }
} 