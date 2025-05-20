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

public class PersonelForm extends JFrame {
    private JTextField txtPersonelAdi;
    private JTextField txtPersonelSoyadi;
    private JTextField txtPersonelAdresi;
    private JTextField txtPersonelMaas;
    private JTable tblPersoneller;
    private DefaultTableModel tableModel;
    private JButton btnEkle;
    private JButton btnGuncelle;
    private JButton btnSil;
    private JButton btnTemizle;

    private PersonelDao personelDao;
    private ObjectId selectedPersonelId;

    public PersonelForm(MongoDatabase database) {
        personelDao = new PersonelDao(database);
        
        // Form ayarları
        setTitle("Personel Yönetimi");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Ana panel oluşturma
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form paneli
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Personel Bilgileri"));
        
        formPanel.add(new JLabel("Personel Adı:"));
        txtPersonelAdi = new JTextField();
        formPanel.add(txtPersonelAdi);
        
        formPanel.add(new JLabel("Personel Soyadı:"));
        txtPersonelSoyadi = new JTextField();
        formPanel.add(txtPersonelSoyadi);
        
        formPanel.add(new JLabel("Personel Adresi:"));
        txtPersonelAdresi = new JTextField();
        formPanel.add(txtPersonelAdresi);
        
        formPanel.add(new JLabel("Personel Maaşı:"));
        txtPersonelMaas = new JTextField();
        formPanel.add(txtPersonelMaas);
        
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
        tableModel.addColumn("Personel Adı");
        tableModel.addColumn("Personel Soyadı");
        tableModel.addColumn("Personel Adresi");
        tableModel.addColumn("Personel Maaşı");
        
        tblPersoneller = new JTable(tableModel);
        tblPersoneller.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblPersoneller);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Personel Listesi"));
        
        // Panelleri ana panele ekleme
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Ana paneli forma ekleme
        add(mainPanel);
        
        // Verileri yükleme
        loadPersonelData();
        
        // Olay dinleyiciler
        tblPersoneller.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblPersoneller.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedPersonelId = new ObjectId(tableModel.getValueAt(selectedRow, 0).toString());
                    txtPersonelAdi.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtPersonelSoyadi.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtPersonelAdresi.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    txtPersonelMaas.setText(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });
        
        btnEkle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPersonel();
            }
        });
        
        btnGuncelle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePersonel();
            }
        });
        
        btnSil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePersonel();
            }
        });
        
        btnTemizle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
    }
    
    private void loadPersonelData() {
        // Tabloyu temizle
        tableModel.setRowCount(0);
        
        // Personelleri yükle
        List<Personel> personelList = personelDao.PersonelList();
        for (Personel personel : personelList) {
            tableModel.addRow(new Object[]{
                personel.getId().toString(),
                personel.getPersonel_adi(),
                personel.getPersonel_soyadi(),
                personel.getPersonel_adresi(),
                personel.getPersonel_maas()
            });
        }
    }
    
    private void addPersonel() {
        try {
            String personelAdi = txtPersonelAdi.getText();
            String personelSoyadi = txtPersonelSoyadi.getText();
            String personelAdresi = txtPersonelAdresi.getText();
            int personelMaas = Integer.parseInt(txtPersonelMaas.getText());
            
            if (personelAdi.isEmpty() || personelSoyadi.isEmpty() || personelAdresi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Personel personel = new Personel();
            personel.setId(new ObjectId());
            personel.setPersonel_adi(personelAdi);
            personel.setPersonel_soyadi(personelSoyadi);
            personel.setPersonel_adresi(personelAdresi);
            personel.setPersonel_maas(personelMaas);
            
            personelDao.PersonelEkle(personel);
            
            loadPersonelData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Personel başarıyla eklendi.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Maaş alanı sayısal bir değer olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Personel eklenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updatePersonel() {
        try {
            if (selectedPersonelId == null) {
                JOptionPane.showMessageDialog(this, "Lütfen güncellenecek bir personel seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String personelAdi = txtPersonelAdi.getText();
            String personelSoyadi = txtPersonelSoyadi.getText();
            String personelAdresi = txtPersonelAdresi.getText();
            int personelMaas = Integer.parseInt(txtPersonelMaas.getText());
            
            if (personelAdi.isEmpty() || personelSoyadi.isEmpty() || personelAdresi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Personel personel = new Personel();
            personel.setId(selectedPersonelId);
            personel.setPersonel_adi(personelAdi);
            personel.setPersonel_soyadi(personelSoyadi);
            personel.setPersonel_adresi(personelAdresi);
            personel.setPersonel_maas(personelMaas);
            
            personelDao.PersonelGuncelle(selectedPersonelId.toString(), personel);
            
            loadPersonelData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Personel başarıyla güncellendi.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Maaş alanı sayısal bir değer olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Personel güncellenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deletePersonel() {
        try {
            if (selectedPersonelId == null) {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek bir personel seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Seçili personeli silmek istediğinize emin misiniz?", "Personel Sil", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                personelDao.PersonelSil(selectedPersonelId.toString());
                loadPersonelData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Personel başarıyla silindi.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Personel silinirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        txtPersonelAdi.setText("");
        txtPersonelSoyadi.setText("");
        txtPersonelAdresi.setText("");
        txtPersonelMaas.setText("");
        selectedPersonelId = null;
        tblPersoneller.clearSelection();
    }
} 