package com.beigebigdata.pdf2htmlutil.service.impl;

import com.beigebigdata.pdf2htmlutil.mapper.TxtBltBasTxtMapper;
import com.beigebigdata.pdf2htmlutil.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: UpdateServiceImpl
 * @Description:
 * @date 2018/6/13
 */
@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    private TxtBltBasTxtMapper txtBltBasTxtMapper;

    @Override
    public void updateHtmlContent(long orig_id, String cont_html) {
        txtBltBasTxtMapper.updateHtmlContent(orig_id,cont_html);
    }

    @Override
    public void convertFail(long orig_id) {
        txtBltBasTxtMapper.convertFail(orig_id);
    }
}
