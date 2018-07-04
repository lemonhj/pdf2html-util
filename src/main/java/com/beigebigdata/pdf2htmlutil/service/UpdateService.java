package com.beigebigdata.pdf2htmlutil.service;


/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: UpdateService
 * @Description:
 * @date 2018/6/13
 */
public interface UpdateService {

    void updateHtmlContent(long orig_id, String cont_html);

    void convertFail(long orig_id);
}
