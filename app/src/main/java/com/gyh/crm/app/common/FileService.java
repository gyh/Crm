package com.gyh.crm.app.common;

/**
 * Created by GYH on 2014/6/28.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import android.os.Environment;
import android.util.Log;
public class FileService {

    private String pathName = "/sdcard/crm/";
    private String fileName = "crmuser.txt";


    /**
     * 获取数据
     * */
    public String  getFromSDCard(){
        String readedStr = "";
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return "";
        }
        File path = new File(pathName);
        File file = new File(pathName + fileName);
        if (!path.exists()) {
            Log.d("TestFile", "Create the path:" + pathName);
            return "";
        }
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            BufferedReader br= new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String tmp;

            while((tmp=br.readLine())!=null){
                readedStr+=tmp;
            }
            br.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readedStr;
    }

    /**
     * 导出数据
     * */
    public void saveToSDCard( String content) {
        try{
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                Log.d("TestFile", "SD card is not avaiable/writeable right now.");
                return;
            }
            File path = new File(pathName);
            File file = new File(pathName + fileName);
            if (!path.exists()) {
                Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + fileName);
                file.createNewFile();
            }else {
                file.delete();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(content.getBytes());
            raf.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        }
    }

    /**
     * 判断文件是否存在
     * */
    public boolean ishasFile(){
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return false;
        }
        File path = new File(pathName);
        File file = new File(pathName + fileName);
        if (!path.exists()) {
            Log.d("TestFile", "Create the path:" + pathName);
            return false;
        }
        if (!file.exists()) {
            Log.d("TestFile", "Create the file:" + fileName);
            return false;
        }
        return true;
    }

    public void saveToRom(String name, String content) {
        // TODO Auto-generated method stub
    }

}
