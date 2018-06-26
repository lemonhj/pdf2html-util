package com.beigebigdata.pdf2htmlutil.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: CmdExec
 * @Description:
 * @date 2018/6/12
 */
@Slf4j
public class CmdExec {

    public static String executeLinuxCmd(String cmd) {
        log.info("got cmd job : " + cmd);
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmd);
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[8192];
                for (int n; (n = in.read(b)) != -1;) {
                    out.append(new String(b, 0, n));
            }
            String result = out.toString();
            log.info("job result [" + result + "]");
            in.close();
            // process.waitFor();
            process.destroy();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
