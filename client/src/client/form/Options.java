package client.form;

import client.services.CommonService;
import client.services.PropertyService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dmitry on 12.10.2016.
 */
public class Options extends AbstractForm {
    private JPanel mainPanel;
    private JTextField hostTextField;
    private JTextField protTextField;
    private JButton acceptButton;

    public Options(CommonService commonService) {
        super(commonService);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JFrame frame = new JFrame("Options");
        showProperty();
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    commonService.getPropertyService().saveProperty(propertyFormFormToMap());
                    //// TODO: 12.10.2016
                    System.out.println("Настройки успешно сохранены");
                    frame.dispose();
                }
                catch (NumberFormatException exception) {
                    //// TODO: 12.10.2016
                    System.out.println("Некорректный формат настроек");
                }
                catch (Exception exception) {
                    //// TODO: 12.10.2016
                    System.out.println("Во время сохранения настроек произошла ошибка");
                }
            }
        });
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private void showProperty() {
        PropertyService propertyService = commonService.getPropertyService();
        hostTextField.setText(propertyService.getHost());
        protTextField.setText(String.valueOf(propertyService.getPort()));
    }

    private Map<String, String> propertyFormFormToMap() {
        PropertyService propertyService = commonService.getPropertyService();
        Map<String, String> property = new HashMap<>();
        property.put(PropertyService.PROP_PORT, Integer.valueOf(protTextField.getText()).toString());
        property.put(PropertyService.PROP_HOST, hostTextField.getText());
        return property;
    }



}
