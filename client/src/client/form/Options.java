package client.form;

import client.services.FileClientMainService;
import client.services.PropertyService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dmitry on 12.10.2016.
 */
public class Options extends AbstractForm {
    private JPanel mainPanel;
    private JTextField hostTextField;
    private JTextField protTextField;
    private JButton acceptButton;
    private JRadioButton systemRadioButton;
    private JRadioButton decoratedRadioButton;
    private JRadioButton defaultRadioButton;
    private List<JRadioButton> radioButtons = initRadioButtons();

    public Options(FileClientMainService fileClientMainService) {
        super(fileClientMainService);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JFrame frame = new JFrame("Options");
        showProperty();
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fileClientMainService.getPropertyService().saveProperty(propertyFormFormToMap());
                    //// TODO: 12.10.2016
                    System.out.println("Настройки успешно сохранены");
                    frame.dispose();
                } catch (NumberFormatException exception) {
                    //// TODO: 12.10.2016
                    System.out.println("Некорректный формат настроек");
                } catch (Exception exception) {
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
        PropertyService propertyService = fileClientMainService.getPropertyService();
        String design = propertyService.getDesign();
        for (int i = 0; i < radioButtons.size(); i++) {
            JRadioButton r = radioButtons.get(i);
            String rName = r.getName();
            if (rName.equals(design)) {
                r.setSelected(true);
            }

        }
        //radioButtons.stream().forEach(r->{if(r==null){return;}if(r.getName().equals(design)){r.setSelected(true);selectingRaduoButton = design;}});
        hostTextField.setText(propertyService.getHost());
        protTextField.setText(String.valueOf(propertyService.getPort()));
    }

    private Map<String, String> propertyFormFormToMap() {
        PropertyService propertyService = fileClientMainService.getPropertyService();
        Map<String, String> property = new HashMap<>();
        property.put(PropertyService.PROP_PORT, Integer.valueOf(protTextField.getText()).toString());
        property.put(PropertyService.PROP_HOST, hostTextField.getText());
        property.put(PropertyService.PROP_DESIGN, getDesignFromForm());
        return property;
    }

    private String getDesignFromForm() {
        for (int i = 0; i < radioButtons.size(); i++) {
            JRadioButton r = radioButtons.get(i);
            if(r.isSelected()){
                return r.getName();
            }
        }
        return null;
    }

    private List<JRadioButton> initRadioButtons() {
        List<JRadioButton> radioButtons = new ArrayList<>();
        radioButtons.add(systemRadioButton);
        radioButtons.add(decoratedRadioButton);
        radioButtons.add(defaultRadioButton);
        return radioButtons;
    }




}
