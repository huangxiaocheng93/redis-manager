package com.vincent.h.util;

import java.io.*;

/**
 * @description 文件工具类
 *
 * @author huangxiaocheng
 * @date 2020/2/25 13:50
 */
public class FileUtils {

    private static final String CONFIG_NAME = "config.json";

    /**
     * 读取配置文件信息
     *
     * @return
     */
    public static String readConfigFile() {
        File file = new File(".", CONFIG_NAME);
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            if(!file.exists()) {
                if(!file.createNewFile()) {
                    return null;
                }
            }

            if(!file.isFile()) {
                return null;
            }

            String encoding = "UTF-8";
            read = new InputStreamReader(new FileInputStream(file), encoding);
            bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null)
            {
                sb.append(lineTxt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(bufferedReader != null) {
                    bufferedReader.close();
                }
                if(read != null) {
                    read.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void writeConfigFile(String content) {
        File file = new File(".", CONFIG_NAME);
        OutputStreamWriter writer = null;
        BufferedWriter bufferedWriter = null;
        try {
            String encoding = "UTF-8";
            writer = new OutputStreamWriter(new FileOutputStream(file), encoding);
            bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if(writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
