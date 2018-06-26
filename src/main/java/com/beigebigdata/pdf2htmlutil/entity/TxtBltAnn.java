package com.beigebigdata.pdf2htmlutil.entity;

import java.util.Date;

/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: TxtBltAnn
 * @Description:
 * @date 2018/6/13
 */
public class TxtBltAnn {


    /*
    id         NUMBER(18) not null,
  orig_id    NUMBER(18) not null,
  ann_name   VARCHAR2(500) not null,
  ann_fmt    VARCHAR2(10),
  ann_cont   BLOB,
  ann_sz     NUMBER(19,2),
  ann_page   NUMBER(5),
  ent_time   DATE not null,
  upd_time   DATE not null,
  grd_time   DATE not null,
  rs_id      VARCHAR2(20) not null,
  rec_id     VARCHAR2(50),
  clear_flag VARCHAR2(1),
  file_id    VARCHAR2(32),
  rel_path   VARCHAR2(200),
  id_name    VARCHAR2(100)
     */

    private long id;

    private long orig_id;

    private String ann_fmt;

    private byte[] ann_cont;

    private Date upd_time;

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

    public String getAnn_fmt() {
        return ann_fmt;
    }

    public void setAnn_fmt(String ann_fmt) {
        this.ann_fmt = ann_fmt;
    }

    public byte[] getAnn_cont() {
        return ann_cont;
    }

    public void setAnn_cont(byte[] ann_cont) {
        this.ann_cont = ann_cont;
    }

    public Date getUpd_time() {
        return upd_time;
    }

    public void setUpd_time(Date upd_time) {
        this.upd_time = upd_time;
    }
}
