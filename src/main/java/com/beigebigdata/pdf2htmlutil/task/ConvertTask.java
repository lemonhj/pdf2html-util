package com.beigebigdata.pdf2htmlutil.task;

import com.beigebigdata.pdf2htmlutil.entity.TxtBltAnn;


/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: ConvertTask
 * @Description:
 * @date 2018/6/26
 */
public class ConvertTask implements Runnable {

    private TxtBltAnn txtBltAnn;


    public ConvertTask(TxtBltAnn txtBltAnn) {
        this.txtBltAnn = txtBltAnn;
    }

    @Override
    public void run() {



    }
}
