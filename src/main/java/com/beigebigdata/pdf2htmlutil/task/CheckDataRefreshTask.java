package com.beigebigdata.pdf2htmlutil.task;

import com.beigebigdata.pdf2htmlutil.entity.TxtBltAnn;
import com.beigebigdata.pdf2htmlutil.service.CheckService;
import com.beigebigdata.pdf2htmlutil.service.UpdateService;
import com.beigebigdata.pdf2htmlutil.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: CheckDataRefreshService
 * @Description:
 * @date 2018/6/12
 */
@Slf4j
@Component
public class CheckDataRefreshTask implements ApplicationRunner {

    private Map<String,String> properties = new HashMap<>();

    @Value("${temp.pdf.dir}")
    private String pdfTempPath;

    @Value("${cyclePeriod}")
    private int cyclePeriod;

    @Value("${html.dir}")
    private String htmlPath;

    @Value("${lastUpdateTime.file.name}")
    private String lastUpdateTimeFile;

    @Value("${convertSetting.imageExtract}")
    private int imageExtract;

    @Value("${convertSetting.cssExtract}")
    private int cssExtract;

    @Value("${convertSetting.jsExtract}")
    private int jsExtract;

    @Value("${convertSetting.fontExtract}")
    private int fontExtract;

    @Value("${convertSetting.maxTaskCount}")
    private int maxTaskCount;

    @Value("${convertSetting.platform}")
    private int platform;

    @Autowired
    private UpdateService updateService;

    /**
     * 当前任务数
     */
    private Integer currentTaskCount = 0;

    @Autowired
    private CheckService checkService;

    private ThreadPoolExecutor threadPoolExecutor;

    public  void checkDataRefresh(){


        if (threadPoolExecutor == null){
            threadPoolExecutor = new ThreadPoolExecutor(maxTaskCount, maxTaskCount, 30, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),
                    new ThreadPoolExecutor.CallerRunsPolicy());
        }

        try {
            properties.put("pdfTempPath",pdfTempPath);
            properties.put("htmlPath",htmlPath);
            properties.put("lastUpdateTimeFile",lastUpdateTimeFile);
            properties.put("imageExtract",imageExtract+"");
            properties.put("cssExtract",cssExtract+"");
            properties.put("jsExtract",jsExtract+"");
            properties.put("fontExtract",fontExtract+"");
            properties.put("maxTaskCount",maxTaskCount+"");
            properties.put("platform",platform+"");

            //从数据库中获取需要更新的pdf数据
            List<TxtBltAnn> txtBltAnns = fetchPdfDate();

            for (TxtBltAnn txtBltAnn : txtBltAnns){
                threadPoolExecutor.execute(new ConvertTask(txtBltAnn,properties,updateService));
            }

        }catch (ParseException e){
            e.printStackTrace();
            log.error(e.getMessage());
            log.info("发生异常，解析系统记录的最后更新时间出错！");
        }
    }

    /**
     * 从数据库中获取需要更新的pdf数据
     * @return
     */
    private List<TxtBltAnn> fetchPdfDate() throws ParseException {
        DateFormat bf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastUpdateTimeStr = PropertiesUtil.getProperty(lastUpdateTimeFile,"lastUpdateTime");
        Date lastUpdateTime = null;
        List<TxtBltAnn> txtBltAnns = null;
        if (lastUpdateTimeStr != null){
            log.info("获取到系统记录的最后更新时间为：" + lastUpdateTimeStr);
            lastUpdateTime = bf.parse(lastUpdateTimeStr);

            Calendar c = Calendar.getInstance();
            c.setTime(lastUpdateTime);
            c.add(Calendar.SECOND, -1);
            lastUpdateTime = c.getTime();
            txtBltAnns = checkService.getPdfDataByUpdateTime(lastUpdateTime);
            log.info("获取到待转换pdf " + txtBltAnns.size() + " 条！");
        }else {
            log.info("未获取到系统记录的最后更新时间。将进行全量转换。");
            txtBltAnns = checkService.getPdfData();
        }

        Date lastUpdateTimeDb = txtBltAnns.get(0).getUpd_time();
        String lastUpdateTimeDbStr = bf.format(lastUpdateTimeDb);
        PropertiesUtil.writeProperties(lastUpdateTimeFile,"lastUpdateTime",lastUpdateTimeDbStr);

        return txtBltAnns;
    }

    @Override
    public void run(ApplicationArguments args){

        while (true){
            try {
                checkDataRefresh();
                Thread.sleep(cyclePeriod * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
