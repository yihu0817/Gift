package com.scxh.gift.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.os.Environment;

public class FavorFileUtil {
	//Log信息
    private static final String TAG = "FileUtil";

    private File mStorageDirectory;
    
    public volatile static FavorFileUtil sFileUtil = null;

	public static FavorFileUtil getInstance() {
		if (sFileUtil == null) {
			synchronized (FavorFileUtil.class) {
				if (sFileUtil == null) {
					sFileUtil = new FavorFileUtil();
				}
			}
		}
		return sFileUtil;
	}
    
    private FavorFileUtil() {
      
    }
    
    //设置保存文件的路径
    public void setDirPath(String dirPath){
        File baseDirectory = new File(Environment.getExternalStorageDirectory(), dirPath);
        createDirectory(baseDirectory);	//创建文件夹
        mStorageDirectory = baseDirectory;	//赋值文件夹路径
    }
    
    public boolean exists(String fileName) {
        return getFile(fileName).exists();
    }

    //根据文件名，取得文件全路径
    public File getFile(String name) {
        return new File(mStorageDirectory.toString() + File.separator + name);
        //别忘了，File.separator是分隔符，Linux是“/”，windows是“\\”
    }

    //使用InputStream读取文件
    public InputStream getInputStream(String hash) throws IOException {
        return (InputStream) new FileInputStream(getFile(hash));
    }

    //保存（图片）文件
    public boolean store(String name, Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(getFile(name));
            if (name.endsWith("png") || name.endsWith("PNG")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            
            return true;
        } catch (Exception e) {
//            Logs.e(TAG, "store failed to store: " + name);	///////
            e.printStackTrace();
            return false;
        }finally{
            if(out != null){
                try {
                	out.flush();
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //保存（图片）文件
    public boolean store(String key, InputStream is) {
        is = new BufferedInputStream(is, 8096);
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(getFile(key)), 8096);

            byte[] b = new byte[2048];
            int count;
            int total = 0;

            while ((count = is.read(b)) > 0) {
                os.write(b, 0, count);
                total += count;
            }
            return true;
        } catch (IOException e) {
            return false;
        }finally{
            if(os != null){
                try {
                    os.close();
                    os = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /*
    //实际的保存方法
    public void saveFile(Bitmap bitmap, String fileName, String newUrl) throws IOException {
	    String savePath = Environment.getExternalStorageDirectory() + "/.naryou/";
        File dirFile = new File(savePath);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(savePath + fileName);
        if(myCaptureFile.exists())
        	myCaptureFile.delete();
//        FileOutputStream fos = new FileOutputStream(savePath + saveName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();

        //保存成功之后，才写入SharedPreferences标记
    	SharedPreferences perferences = PreferenceManager.getDefaultSharedPreferences(CheapMainAct.this);
    	SharedPreferences.Editor editor = perferences.edit();
    	editor.putString(fileName, newUrl);
    	editor.commit();
    }*/
    /**
     * 指定文件名查找
     */
    public boolean findFile(String bitmapName){
        File cacheDir = mStorageDirectory;
        File[] cacheFiles = cacheDir.listFiles();
        if(cacheFiles != null){
            int i = 0;
            for (; i < cacheFiles.length; i++) {
                if (bitmapName.equals(cacheFiles[i].getName())) {
                    break;
                }
            }

            if (i < cacheFiles.length) {
                return true;

            }
        }
        return false;
    }
    //删除文件
    public void deleteFile(String name) {
        getFile(name).delete();
    }
    /**
     * 清空所有指定文件夹下所有文件
     */
    public void clearFile() {
        String[] children = mStorageDirectory.list();
        if (children != null) {
            int size = children.length;
            for (int i = 0; i < size; i++) {
                File child = new File(mStorageDirectory, children[i]);
                child.delete();
            }
        }
        mStorageDirectory.delete();
    }
    /**
     * 创建指定文件夹
     * @param storageDirectory
     */
    public void createDirectory(File storageDirectory) {
        if (!storageDirectory.exists()) {
            storageDirectory.mkdirs();
        }
    }
    /**
     *  清除配置文件 
     * @param path
     * @param name 
     */
    public static void deleteFiles(String path,String name){
        File sharedPrefsDir = new File(path);
        File[] files = sharedPrefsDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(name)) {
                file.delete();
            }
        }
    }
    /**
     * 删除文件夹里面的所有文件
     * 
     * @param path
     *            String 文件夹路径 如 c:/fqf
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
            }
        }
    }

    public static void delFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            return;
        }
        file.delete();
    }

    /**
     * 删除文件夹
     * 
     * @param filePathAndName
     *            String 文件夹路径及名称 如c:/fqf
     * @param fileContent
     *            String
     * @return boolean
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
