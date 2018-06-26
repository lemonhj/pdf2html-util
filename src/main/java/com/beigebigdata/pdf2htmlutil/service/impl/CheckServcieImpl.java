package com.beigebigdata.pdf2htmlutil.service.impl;

import com.beigebigdata.pdf2htmlutil.entity.TxtBltAnn;
import com.beigebigdata.pdf2htmlutil.mapper.TxtBltAnnMapper;
import com.beigebigdata.pdf2htmlutil.mapper.TxtBltBasTxtMapper;
import com.beigebigdata.pdf2htmlutil.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: CheckServcieImpl
 * @Description:
 * @date 2018/6/12
 */
@Service
public class CheckServcieImpl implements CheckService {

    @Autowired
    private TxtBltAnnMapper txtBltAnnMapper;

    @Autowired
    private TxtBltBasTxtMapper txtBltBasTxtMapper;

    @Override
    public List<TxtBltAnn> getPdfData() {
        return txtBltAnnMapper.getPdfData();
    }

    @Override
    public List<TxtBltAnn> getPdfDataByUpdateTime(Date lastUpdateTime) {
        return txtBltAnnMapper.getPdfDataByUpdateTime(lastUpdateTime);
    }


}
