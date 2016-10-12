package client.services;

/**
 * Created by Dmitry on 12.10.2016.
 */

/**
 * Место хранения всех сервисов
 * Чтобы у всех фреймов были одни и те же инстансы всех сервисов.
 * Некая такая обобщённая инъекция зависимостей
 */
public class CommonService {
    private PropertyService propertyService;

    public CommonService() {
        this.propertyService = new PropertyServiceImpl();
    }

    public PropertyService getPropertyService() {
        return propertyService;
    }
}
