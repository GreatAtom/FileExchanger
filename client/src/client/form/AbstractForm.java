package client.form;

import client.services.CommonService;

/**
 * Created by Dmitry on 12.10.2016.
 */
abstract public class AbstractForm {

    protected CommonService commonService;

    public AbstractForm(CommonService commonService) {
        this.commonService = commonService;
    }

    public CommonService getCommonService() {
        return commonService;
    }
}
