package org.example;

import com.mongodb.client.MongoDatabase;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.bson.types.ObjectId;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrunStokGrafikForm extends JFrame {
    private UrunDao urunDao;
    private StokSatisDao stokSatisDao;
    private StokIadeDao stokIadeDao;
    private JTabbedPane tabbedPane;

    public UrunStokGrafikForm(MongoDatabase database) {
        urunDao = new UrunDao(database);
        stokSatisDao = new StokSatisDao(database);
        stokIadeDao = new StokIadeDao(database);
        
        // Form ayarları
        setTitle("Ürün Stok Grafikleri");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Tab paneli oluşturma
        tabbedPane = new JTabbedPane();
        
        // Grafikleri oluştur ve tab paneline ekle
        tabbedPane.addTab("Ürün Stok Dağılımı (Sütun Grafik)", createBarChart());
        tabbedPane.addTab("Ürün Stok Dağılımı (Pasta Grafik)", createPieChart());
        tabbedPane.addTab("Ürün Fiyat Karşılaştırması", createPriceBarChart());

        // Ana panele tab panelini ekle
        add(tabbedPane);
    }

    private Map<ObjectId, Integer> calculateStockMap() {
        // Ürün ID'si -> Stok miktarı eşlemesi
        Map<ObjectId, Integer> stokMap = new HashMap<>();
        
        // Ürünleri yükle ve başlangıç stok değerlerini 0 olarak ayarla
        List<Urun> urunList = urunDao.UrunList();
        for (Urun urun : urunList) {
            stokMap.put(urun.getId(), 0);
        }
        
        // Satış kayıtlarından stok miktarlarını hesapla
        List<StokSatis> stokSatisList = stokSatisDao.StokSatisList();
        for (StokSatis stokSatis : stokSatisList) {
            if (stokSatis.getUrun_id() != null) {
                ObjectId urunId = stokSatis.getUrun_id();
                if (stokMap.containsKey(urunId)) {
                    int mevcutStok = stokMap.get(urunId);
                    stokMap.put(urunId, mevcutStok + stokSatis.getStok_adet());
                }
            }
        }
        
        // İade kayıtlarından stok miktarlarını düşür
        List<StokIade> stokIadeList = stokIadeDao.StokIadeList();
        for (StokIade stokIade : stokIadeList) {
            if (stokIade.getUrun_id() != null) {
                ObjectId urunId = stokIade.getUrun_id();
                if (stokMap.containsKey(urunId)) {
                    // Varsayılan olarak her iade kaydında 1 adet iade edildiğini varsayıyoruz
                    int mevcutStok = stokMap.get(urunId);
                    stokMap.put(urunId, Math.max(0, mevcutStok - 1));
                }
            }
        }
        
        return stokMap;
    }

    private JPanel createBarChart() {
        // Veri seti oluşturma
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Stok haritasını hesapla
        Map<ObjectId, Integer> stokMap = calculateStockMap();
        
        // Ürünleri yükle ve grafik verilerini oluştur
        List<Urun> urunList = urunDao.UrunList();
        for (Urun urun : urunList) {
            int stokMiktari = stokMap.getOrDefault(urun.getId(), 0);
            dataset.addValue(stokMiktari, "Stok Miktarı", urun.getUrun_adi());
        }

        // Grafik oluşturma
        JFreeChart chart = ChartFactory.createBarChart(
                "Ürünlere Göre Stok Dağılımı",
                "Ürün Adı",
                "Stok Miktarı",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Grafik özelliklerini ayarlama
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.1);
        
        // Ürün adlarını daha iyi göstermek için etiketleri eğik yaz
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

        // Grafik paneli oluşturma
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(850, 500));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createPieChart() {
        // Veri seti oluşturma
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Stok haritasını hesapla
        Map<ObjectId, Integer> stokMap = calculateStockMap();
        
        // Ürünleri yükle ve grafik verilerini oluştur
        List<Urun> urunList = urunDao.UrunList();
        for (Urun urun : urunList) {
            int stokMiktari = stokMap.getOrDefault(urun.getId(), 0);
            if (stokMiktari > 0) {
                dataset.setValue(urun.getUrun_adi(), stokMiktari);
            }
        }

        // Grafik oluşturma
        JFreeChart chart = ChartFactory.createPieChart(
                "Ürünlere Göre Stok Dağılımı",
                dataset,
                true,
                true,
                false
        );

        // Grafik özelliklerini ayarlama
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("Stok verisi bulunamadı");
        plot.setCircular(true);
        plot.setLabelGap(0.02);

        // Grafik paneli oluşturma
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(850, 500));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createPriceBarChart() {
        // Veri seti oluşturma
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Ürünleri yükle
        List<Urun> urunList = urunDao.UrunList();

        // Ürün fiyatlarını ekle
        for (Urun urun : urunList) {
            dataset.addValue(urun.getUrun_fiyati(), "Fiyat", urun.getUrun_adi());
        }

        // Grafik oluşturma
        JFreeChart chart = ChartFactory.createBarChart(
                "Ürünlere Göre Fiyat Karşılaştırması",
                "Ürün Adı",
                "Fiyat (TL)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Grafik özelliklerini ayarlama
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.1);
        renderer.setSeriesPaint(0, new Color(79, 129, 189));
        
        // Ürün adlarını daha iyi göstermek için etiketleri eğik yaz
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

        // Grafik paneli oluşturma
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(850, 500));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
} 