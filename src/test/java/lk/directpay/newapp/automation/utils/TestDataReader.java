package lk.directpay.newapp.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestDataReader {
    private static final Properties properties = load();

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream input = TestDataReader.class.getClassLoader()
                .getResourceAsStream("testdata.properties")) {
            if (input == null) {
                throw new RuntimeException("testdata.properties not found on classpath");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load testdata.properties", e);
        }
        return props;
    }

    public static String get(String key) {
        return System.getProperty(key, properties.getProperty(key));
    }
}
