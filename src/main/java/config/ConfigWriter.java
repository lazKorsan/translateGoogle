package config;

import java.io.*;
import java.util.Properties;

import static pages.BasePage.green;
import static pages.BasePage.redBold;


public class ConfigWriter {

    private static final String FILE_PATH = "src/main/resources/config.properties";

    public static void setProperty(String key, String value) {
        Properties properties = new Properties();

        // 1. Dosyayı UTF-8 karakter seti ile oku
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(FILE_PATH), "UTF-8")) {
            properties.load(isr);
        } catch (IOException e) {
            System.out.println("Dosya bulunamadı veya okunamadı, yeni oluşturulacak: " + e.getMessage());
        }

        // 2. Yeni key-value değerini set et
        properties.setProperty(key, value);

        // 3. Dosyayı UTF-8 karakter seti ile yaz/kaydet
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(FILE_PATH), "UTF-8")) {
            properties.store(osw, "Updated via MyMemory API with UTF-8");
            System.out.println("Başarıyla yazıldı -> " + key + " = " + value);
            System.out.println(green("Başarıyla yazıldı -> ")+ redBold(key + " = " + value));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Properties dosyasına yazılamadı!");
        }
    }

    // System.out.println("Başarıyla yazıldı -> " + key + " = " + value);
}