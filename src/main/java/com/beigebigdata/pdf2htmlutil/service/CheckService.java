package com.beigebigdata.pdf2htmlutil.service;

import com.beigebigdata.pdf2htmlutil.entity.TxtBltAnn;

import java.util.Date;
import java.util.List;

/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: CheckService
 * @Description:
 * @date 2018/6/12
 */
public interface CheckService {

    List<TxtBltAnn> getPdfData();


    List<TxtBltAnn> getPdfDataByUpdateTime(Date lastUpdateTime);
}
