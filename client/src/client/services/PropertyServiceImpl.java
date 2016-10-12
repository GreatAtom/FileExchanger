package client.services;

import java.io.*;
import java.util.*;

/**
 * Created by Dmitry on 12.10.2016.
 */
public class PropertyServiceImpl implements PropertyService {
    private final static String DEFAULT_HOST = "localhost";
    private final static int DEFAULT_PORT = 8989;

    private Map<String, String> defaultProperty = initDefaultProperty();

    private String host;
    private int port;

    public PropertyServiceImpl() {
        initProperty();
    }

    private void initProperty() {
        Map<String, String> property = loadProperty();
        setProperty(property);
    }

    private void setProperty(Map<String, String> property) {
        host = getProperty(property, PROP_HOST);
        try {
            port = Integer.valueOf(getProperty(property, PROP_PORT));
        }
        catch (NumberFormatException e){
            port = Integer.valueOf(getProperty(null, PROP_PORT));
        }
    }

    /**
     * Загружает проперти из файла, если возможно. Если нет. Подгружает дефолтные
     * @return
     */
    private Map<String, String> loadProperty() {
        try {
            File file = new File("property.conf");
            return readFormFile(file);
        } catch (IOException e) {
            return getDefaultProperty();
        }

    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean saveProperty(Map<String, String> property){
        boolean ans = updatePrpertyFromFile(property);
        setProperty(property);
        return ans;
    }

    private boolean updatePrpertyFromFile(Map<String, String> property) {
        File file = new File("property.conf");

        try(FileWriter fw = new FileWriter(file)) {
            Set<String> keys = property.keySet();

            keys.stream().forEach(k->{
                try {
                    fw.append(k+"="+property.get(k));
                    fw.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        catch (IOException e){
            return false;
        }
    }

    private Map<String, String> getDefaultProperty() {
        return defaultProperty;
    }

    private Map<String, String> initDefaultProperty() {
        Map<String, String> property = new HashMap<>();
        property.put(PROP_HOST, DEFAULT_HOST);
        property.put(PROP_PORT, String.valueOf(DEFAULT_PORT));
        return property;
    }

    private String getProperty(Map<String, String> property, String key){
        String value = null;
        if(property!=null) {
            value = property.get(key);
        }
        if(value==null || value.trim().equals("")){
            value = getDefaultProperty().get(key);
        }
        return value;
    }

    private Map<String, String> readFormFile(File file) throws IOException {
        Map<String, String> property = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] prop = line.trim().split("=");
            property.put(prop[0], prop[1]);
        }
        return property;
    }

}
