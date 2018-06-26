package com.beigebigdata.pdf2htmlutil.task;

import com.beigebigdata.pdf2htmlutil.entity.TxtBltAnn;
import com.beigebigdata.pdf2htmlutil.service.CheckService;
import com.beigebigdata.pdf2htmlutil.service.UpdateService;
import com.beigebigdata.pdf2htmlutil.utils.CmdExec;
import com.beigebigdata.pdf2htmlutil.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
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
//@EnableAsync
public class CheckDataRefreshTask {

    @Value("${temp.pdf.dir}")
    private String pdfTempPath;

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


    public static void main(String[] args) {

        CheckDataRefreshTask task = new CheckDataRefreshTask();

        while (true){
            try {
                Thread.sleep(30*1000);
                task.checkDataRefresh();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 当前任务数
     */
    private Integer currentTaskCount = 0;

    @Autowired
    private CheckService checkService;

    @Autowired
    private UpdateService updateService;

    //@Scheduled(cron="${cronExpression}")
    //@Async
    public  void checkDataRefresh()  {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 30, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),
                new ThreadPoolExecutor.CallerRunsPolicy());

        synchronized (currentTaskCount){
            if(currentTaskCount >= maxTaskCount ) return;
            currentTaskCount++;
            log.info("当前正在执行的总任务数为："+currentTaskCount);
        }

        DateFormat bf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastUpdateTimeStr = PropertiesUtil.getProperty(lastUpdateTimeFile,"lastUpdateTime");

        try {

            //从数据库中获取需要更新的pdf数据
            List<TxtBltAnn> txtBltAnns = fetchPdfDate();

//            for (TxtBltAnn txtBltAnn : txtBltAnns){
//                threadPoolExecutor.execute(new ConvertTask(txtBltAnn));
//            }



            //转存临时pdf文件
            List<String> pdfPaths = saveTempPdfFile(txtBltAnns);

            log.info("待转换的PDF文件有：" + pdfPaths.size() + "个。");
            log.debug("待转换的PDF文件：" + pdfPaths);

            List<String> htmlPaths = excuteTransition(pdfPaths);

            Map<String,String> htmlContentMap = readHtmlContent(htmlPaths);

            //更新数据库Html字段
            updateHtmlContent(htmlContentMap);
            currentTaskCount--;
        }catch (Exception e){
            currentTaskCount--;
            if (lastUpdateTimeStr != null){
                PropertiesUtil.writeProperties(lastUpdateTimeFile,"lastUpdateTime",lastUpdateTimeStr);
            }else{
                PropertiesUtil.removeProperty(lastUpdateTimeFile,"lastUpdateTime");
            }
            e.printStackTrace();
            log.error(e.getMessage());
            log.info("发生异常，将最后更新时间回退！");
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

    /**
     * 将db中的pdf转存到本地
     * @param txtBltAnns
     * @return
     */
    private List<String> saveTempPdfFile(List<TxtBltAnn> txtBltAnns) throws IOException {

        List<String> pdfPaths = new ArrayList<>();
        for (TxtBltAnn txtBltAnn : txtBltAnns) {
            byte bb[] = txtBltAnn.getAnn_cont();
            File file = new File(pdfTempPath + txtBltAnn.getOrig_id() );
            if (file.exists()) file.delete();
            file.mkdirs();

            StringBuffer pdfNameBuffer = new StringBuffer();
            pdfNameBuffer.append(pdfTempPath).append(txtBltAnn.getOrig_id()).append("/").append(txtBltAnn.getOrig_id()).append(".pdf");
            String pdfNameStr = pdfNameBuffer.toString();

            FileOutputStream out = new FileOutputStream(new File(pdfNameStr));
            out.write(bb);
            out.close();
            pdfPaths.add(pdfNameStr);
            log.info("存储pdf临时文件：" + pdfNameStr);
        }
        return pdfPaths;
    }

    /**
     * 更新html内容到db
     * @param htmlContentMap
     */
    private void updateHtmlContent(Map<String,String> htmlContentMap) {
        for(Map.Entry<String, String> entry : htmlContentMap.entrySet()){
            long orig_id = Long.parseLong(entry.getKey());
            String cont_html = entry.getValue();
            updateService.updateHtmlContent(orig_id,cont_html);
            log.info(orig_id + " 更新成功！");
        }
    }

    /**
     * 从文件中读取html内容
     * @param htmlPaths
     */
    private Map<String,String> readHtmlContent(List<String> htmlPaths) throws IOException {

        Map<String,String> htmlContentMap = new HashMap<>();

        for (String htmlPath : htmlPaths){
            String htmlContent = readToString(htmlPath);
            int index = htmlPath.lastIndexOf("/");
            String pdfname = htmlPath.substring(index+1,htmlPath.length()-5);
            htmlContentMap.put(pdfname,htmlContent);
        }
        return htmlContentMap;
    }

    public String readToString(String fileName) throws IOException {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);
        in.close();

        return new String(filecontent, encoding);
    }

    /**
     * 执行pdf转html
     * @param pdfPaths
     */
    private List<String> excuteTransition(List<String> pdfPaths) {
        int count = pdfPaths.size();
        List<String> htmlPaths = new ArrayList<>();
        for (String pdfPath : pdfPaths){
            int index = pdfPath.lastIndexOf("/");
            String pdfname = pdfPath.substring(index+1,pdfPath.length()-4);
            log.info("pdfname : " + pdfname);
            File file = new File(htmlPath+pdfname);
            if (file.exists()){
                file.delete();
            }
            file.mkdirs();

            StringBuffer cmdStr = new StringBuffer("pdf2htmlEX --dest-dir ").append(htmlPath).append(pdfname);
            if (platform == 0){
                cmdStr.append("/");
            }
            cmdStr.append(" --hdpi 72 --vdpi 72 ");
            if (imageExtract == 1){
                cmdStr.append("--embed-image 0 ");
            }
            if (cssExtract == 1){
                cmdStr.append("--embed-css 0 ");
            }
            if (jsExtract == 1){
                cmdStr.append("--embed-javascript 0 ");
            }
            if (fontExtract == 1){
                cmdStr.append("--embed-font 0 ");
            }
            cmdStr.append(pdfPath);

            CmdExec.executeLinuxCmd(cmdStr.toString());
            log.info(pdfPath + "转换完成。剩余：" + --count);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(htmlPath).append(pdfname).append("/").append(pdfname).append(".html");
            htmlPaths.add(stringBuffer.toString());
        }
        return htmlPaths;
    }
}
