package com.beigebigdata.pdf2htmlutil.task;

import com.beigebigdata.pdf2htmlutil.entity.TxtBltAnn;
import com.beigebigdata.pdf2htmlutil.service.UpdateService;
import com.beigebigdata.pdf2htmlutil.utils.CmdExec;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: ConvertTask
 * @Description:
 * @date 2018/6/26
 */
@Slf4j
public class ConvertTask implements Runnable {

    private UpdateService updateService;

    private Map<String,String> properties;

    private TxtBltAnn tba;

    public ConvertTask(TxtBltAnn tba,Map<String,String> properties, UpdateService updateService) {
        this.properties = properties;
        this.tba = tba;
        this.updateService = updateService;
    }

    @Override
    public void run() {
        //转存临时pdf文件
        String pdfPath = null;
        try {
            pdfPath = saveTempPdfFile(tba);
        } catch (IOException e) {
            log.error("保存pdf文件出错，路径为：" + pdfPath);
            e.printStackTrace();
        }

        String htmlPath = excuteTransition(pdfPath);

        Map<String,String> htmlContentMap = null;
        try {
            htmlContentMap = readHtmlContent(htmlPath);
            //更新数据库Html字段
            updateHtmlContent(htmlContentMap);
        } catch (IOException e) {
            log.error("读取html文件出错，路径为：" + htmlPath);
            log.error("请检查pdf文件：" + pdfPath);
            updateService.convertFail(tba.getOrig_id());
            e.printStackTrace();
        }



    }

    /**
     * 将db中的pdf转存到本地
     * @param txtBltAnn
     * @return
     */
    private String saveTempPdfFile(TxtBltAnn txtBltAnn) throws IOException {

        byte bb[] = txtBltAnn.getAnn_cont();
        File file = new File(properties.get("pdfTempPath") + txtBltAnn.getOrig_id() );
        if (file.exists()) file.delete();
        file.mkdirs();

        StringBuffer pdfNameBuffer = new StringBuffer();
        pdfNameBuffer.append(properties.get("pdfTempPath")).append(txtBltAnn.getOrig_id()).append("/").append(txtBltAnn.getOrig_id()).append(".pdf");
        String pdfNameStr = pdfNameBuffer.toString();

        FileOutputStream out = new FileOutputStream(new File(pdfNameStr));
        out.write(bb);
        out.close();

        log.info("存储pdf临时文件：" + pdfNameStr);

        return pdfNameStr;
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
     * @param htmlPath
     */
    private Map<String,String> readHtmlContent(String htmlPath) throws IOException {

        Map<String,String> htmlContentMap = new HashMap<>();

        String htmlContent = readToString(htmlPath);
        int index = htmlPath.lastIndexOf("/");
        String pdfname = htmlPath.substring(index+1,htmlPath.length()-5);
        htmlContentMap.put(pdfname,htmlContent);
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
     * @param pdfPath
     */
    private String excuteTransition(String pdfPath){

        int index = pdfPath.lastIndexOf("/");
        String pdfname = pdfPath.substring(index+1,pdfPath.length()-4);
        log.info(pdfname + ".pdf开始执行转换。");
        File file = new File(properties.get("htmlPath") + pdfname);
        if (file.exists()){
            file.delete();
        }
        file.mkdirs();

        StringBuffer cmdStr = new StringBuffer("pdf2htmlEX --dest-dir ").append(properties.get("htmlPath")).append(pdfname);
        if (Integer.parseInt(properties.get("platform")) == 0){
            cmdStr.append("/");
        }
        cmdStr.append(" --hdpi 72 --vdpi 72 ");
        if (Integer.parseInt(properties.get("imageExtract")) == 1){
            cmdStr.append("--embed-image 0 ");
        }
        if (Integer.parseInt(properties.get("cssExtract")) == 1){
            cmdStr.append("--embed-css 0 ");
        }
        if (Integer.parseInt(properties.get("jsExtract")) == 1){
            cmdStr.append("--embed-javascript 0 ");
        }
        if (Integer.parseInt(properties.get("fontExtract")) == 1){
            cmdStr.append("--embed-font 0 ");
        }
        cmdStr.append(pdfPath);

        CmdExec.executeLinuxCmd(cmdStr.toString());
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(properties.get("htmlPath")).append(pdfname).append("/").append(pdfname).append(".html");
        log.info(pdfname + ".pdf 转换完成。转换后的html路径为：" + stringBuffer.toString());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
}
