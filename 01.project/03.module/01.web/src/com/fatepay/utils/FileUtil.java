package com.fatepay.utils;

import java.io.*;
import java.text.DecimalFormat;

/**
 * 文件工具类
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 00:12
 */
public class FileUtil {
    /**
     * 缓存大小
     */
    private static final int BUFFER_SIZE = 16 * 1024;

    /**
     * 拷贝文件
     * @param src
     * @param dst
     */
    public static void copy(File src, File dst) {
        try {
            int byteRead;
            if (src.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(src); //读入原文件
                FileOutputStream fs = new FileOutputStream(dst);
                byte[] buffer = new byte[BUFFER_SIZE];
                while ( (byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 取得文件大小
     * @param f
     * @return
     * @throws Exception
     */
    public static  long getFileSizes(File f) throws Exception{
        long s=0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s= fis.available();
        } else {
            f.createNewFile();
            System.out.println("文件不存在");
        }
        return s;
    }

    /**
     * 递归 取得文件夹大小
     * @param f
     * @return
     * @throws Exception
     */
    public static  long getFileSize(File f)throws Exception
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++)
        {
            if (flist[i].isDirectory())
            {
                size = size + getFileSize(flist[i]);
            } else
            {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    public static  String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 递归求取目录文件个数
     * @param f
     * @return
     */
    public static  long getList(File f){
        long size = 0;
        File flist[] = f.listFiles();
        size=flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getList(flist[i]);
                size--;
            }
        }
        return size;
    }
}
