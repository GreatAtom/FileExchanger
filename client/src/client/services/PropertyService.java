package client.services;

import java.util.Map;

/**
 * Created by Dmitry on 12.10.2016.
 */
public interface PropertyService {
     String PROP_PORT = "PROP_PORT";
     String PROP_HOST = "PROP_HOST";
     String PROP_DESIGN = "PROP_DESIGN";

     String getHost();
     int getPort();
     String getDesign();
     boolean saveProperty(Map<String, String> property);
}
