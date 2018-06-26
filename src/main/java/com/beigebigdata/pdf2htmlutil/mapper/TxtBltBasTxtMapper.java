package com.beigebigdata.pdf2htmlutil.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: TxtBltBasTxt
 * @Description:
 * @date 2018/6/13
 */
@Mapper
public interface TxtBltBasTxtMapper {
    void updateHtmlContent(@Param("orig_id")long orig_id, @Param("cont_html")String cont_html);
}
