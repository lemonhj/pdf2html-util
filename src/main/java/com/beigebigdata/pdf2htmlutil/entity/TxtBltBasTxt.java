package com.beigebigdata.pdf2htmlutil.entity;

import java.util.Date;

/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: TxtBltBasTxt
 * @Description:
 * @date 2018/6/13
 */
public class TxtBltBasTxt {
    /**
     id        NUMBER(18) not null,
     orig_id   NUMBER(18) not null,
     cont_fmt  NUMBER(10),
     cont      CLOB,
     ent_time  DATE not null,
     upd_time  DATE not null,
     grd_time  DATE not null,
     rs_id     VARCHAR2(20) not null,
     rec_id    VARCHAR2(50),
     cont_html CLOB
     */

    private long id;

    private long orig_id;

    private Date upd_time;

    private String cont_html;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrig_id() {
        return orig_id;
    }

    public void setOrig_id(long orig_id) {
        this.orig_id = orig_id;
    }

    public Date getUpd_time() {
        return upd_time;
    }

    public void setUpd_time(Date upd_time) {
        this.upd_time = upd_time;
    }

    public String getCont_html() {
        return cont_html;
    }

    public void setCont_html(String cont_html) {
        this.cont_html = cont_html;
    }
}
