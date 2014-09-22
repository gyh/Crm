package com.gyh.crm.app.listener;

import com.gyh.crm.app.common.Base;

import java.util.List;

/**
 * Created by GYH on 2014/9/20.
 */
public interface DBServiceListener {
    void success(List<Base> bases);
}
