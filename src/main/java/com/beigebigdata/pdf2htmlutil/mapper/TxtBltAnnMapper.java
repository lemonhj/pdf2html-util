package com.beigebigdata.pdf2htmlutil.mapper;

import com.beigebigdata.pdf2htmlutil.entity.TxtBltAnn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: TxtBltAnnMapper
 * @Description:
 * @date 2018/6/13
 */
@Mapper
public interface TxtBltAnnMapper {
    List<TxtBltAnn> getPdfData();

    List<TxtBltAnn> getPdfDataByUpdateTime(@Param("lastUpdateTime") Date lastUpdateTime);
}
