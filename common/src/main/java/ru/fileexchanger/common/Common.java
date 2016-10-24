package ru.fileexchanger.common;

import java.util.List;

/**
 * Created by Dmitry on 24.10.2016.
 */
public class Common {

    public static boolean isEmpty(List<? extends Object> list){
        if(list==null || list.size()==0){
            return true;
        } else {
            return false;
        }
    }
}
