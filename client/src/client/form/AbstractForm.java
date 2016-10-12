package client.form;

import client.services.FileClientMainService;

/**
 * Created by Dmitry on 12.10.2016.
 */
abstract public class AbstractForm {

    protected FileClientMainService fileClientMainService;

    public AbstractForm(FileClientMainService fileClientMainService) {
        this.fileClientMainService = fileClientMainService;
    }

    public FileClientMainService getFileClientMainService() {
        return fileClientMainService;
    }
}
