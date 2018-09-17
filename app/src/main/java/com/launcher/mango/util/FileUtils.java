package com.launcher.mango.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author tic
 *         created on 18/9/17.
 */

public class FileUtils {

    /**
     * 从手机内存中读取文件内容
     *
     * @param ctx      游标
     * @param filename 文件名
     * @return 内容
     */
    public static String readStringFromMemory(Context ctx, String filename) {
        FileInputStream fis = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            fis = ctx.openFileInput(filename);

            inputStreamReader = new InputStreamReader(fis);
            reader = new BufferedReader(inputStreamReader);

            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fis = null;
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inputStreamReader = null;
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public static File creatSDDir(String dirName) {
        File dir = new File(getSDPATH() + dirName);
        dir.mkdir();
        return dir;
    }

    private static String getSDPATH() {
        return Environment.getExternalStorageDirectory() + "/";
    }

    /**
     * 字符串内容保存到指定的内存空间路径
     *
     * @param ctx      context
     * @param content  内容
     * @param filename 文件名
     */
    public static void writeStringToMemory(Context ctx, String content, String filename) {
        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(content.getBytes("UTF-8"));
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fos = null;
            }
        }
    }

    /**
     * 将字符串内容写入SD指定文件中
     *
     * @param file 文件
     * @return 内容
     */
    public static String readStringFromSdCard(File file) {
        FileReader writer = null;
        BufferedReader reader = null;

        if (file == null) {
            return null;
        }
        try {
            if (!file.getParentFile()
                    .exists()) {
                file.getParentFile()
                        .mkdirs();
            }
            writer = new FileReader(file);
            reader = new BufferedReader(writer);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
                if (writer != null) {
                    writer = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将字符串内容写入SD指定文件中
     *
     * @param file    文件
     * @param content 内容
     * @return 成功失败
     */
    public static boolean writeStringToSdCard(File file, String content) {
        FileWriter writer = null;
        BufferedWriter bufWriter = null;

        if (file == null) {
            return false;
        }
        try {
            if (!file.getParentFile()
                    .exists()) {
                file.getParentFile()
                        .mkdirs();
            }
            writer = new FileWriter(file, false);
            bufWriter = new BufferedWriter(writer);
            bufWriter.write(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufWriter != null) {
                    bufWriter.close();
                    bufWriter = null;
                }
                if (writer != null) {
                    writer = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 文件保存到指定的内存空间路径
     */
    public static boolean writeFileToMemory(Context context, int resId, String filename) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            File file = context.getFileStreamPath(filename);
            if (file.exists()) {
                return true;
            }
            is = context.getResources()
                    .openRawResource(resId);
            if (is == null) {
                throw new RuntimeException("stream is null");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            } else {
                fos = context.openFileOutput(filename, Context.MODE_WORLD_READABLE);
            }
            byte buf[] = new byte[8 * 1024];
            int numRead = is.read(buf);
            while (numRead != -1) {
                fos.write(buf, 0, numRead);
                numRead = is.read(buf);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                is = null;

            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fos = null;
            }
        }

        return false;
    }

    public static void copyfile(File fromFile, File toFile, Boolean rewrite) {
        if (!fromFile.exists()) {
            return;
        }
        if (!fromFile.isFile()) {
            return;
        }
        if (!fromFile.canRead()) {
            return;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        java.io.FileInputStream fosfrom = null;
        java.io.FileOutputStream fosto = null;
        try {
            fosfrom = new java.io.FileInputStream(
                    fromFile);

            fosto = new FileOutputStream(toFile);

            byte bt[] = new byte[1024];

            int c;

            while ((c = fosfrom.read(bt)) > 0) {

                fosto.write(bt, 0, c); // 将内容写到新文件当中

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fosfrom != null) {
                    fosfrom.close();
                }
                if (fosto != null) {
                    fosto.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isSdcardMounted() {
        return Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getSdPath() {
        return Environment.getExternalStorageDirectory() + "/";
    }

    /**
     * 获取sd卡路径的方法
     */
    public static ArrayList<String> getExternalStorageDirectory() {
        File file = Environment.getDataDirectory();
        ArrayList<String> list = new ArrayList<>();
        if (file != null && file.exists()) {
            list.add(file.getPath());
        }
        if (isSdcardMounted()) {
            list.add(getSdPath());
        }

        if (list.size() == 0) {
            return list;
        }

        ArrayList<String> newList = new ArrayList<>();
        for (String externalStorageDir : list) {
            searchFiles(new File(externalStorageDir), newList);
        }

        return newList;
    }

    private static void searchFiles(File f, ArrayList<String> newList) {
        File files[] = f.listFiles();
        if (null != files) {
            for (File tempF : files) {
                if (tempF.isDirectory()) {
                    searchFiles(tempF, newList);
                } else {
                    if (tempF.exists()) {
                        String path = tempF.getPath();
                        if (path.endsWith(".apk")) {
                            newList.add(path);
                        }
                    }
                }
            }
        }
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static boolean DeleteFile(Context context, File file) {
        return delete(file);
    }

    private static boolean delete(File file) {
        if (file.exists() == false) {
            /*mHandler.sendEmptyMessage(0); */
            return false;
        } else {
            if (file.isFile()) {
                return file.delete();
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    return file.delete();
                }
                for (File f : childFile) {
                    delete(f);
                }
                return file.delete();
            }
        }
        return false;
    }

    /**
     * 根据提供的文件夹名称，返回所要创建目录的绝对路径,如果sdcard不存在，则返回null
     * <p>
     * 例如，提供"txtwDownload"，则返回/mnt/sdcard/txtwDownload
     */
    public static File getRootFolder(String folder) {
        if (isSdcardMounted()) {
            File sdDir = Environment.getExternalStorageDirectory();
            File dirFolder = sdDir;
            String[] paths = folder.split("/");
            int length = paths.length;
            for (int i = 0; i < length; i++) {
                dirFolder = new File(dirFolder, paths[i]);
                if (!dirFolder.exists()) {
                    boolean value = dirFolder.mkdir();
                    if (value) {
                    }
                } else {
                }
            }
            return dirFolder;
        }
        return null;
    }

    /**
     * 添加文件路径
     *
     * @param root
     * @param fileName
     * @return
     */
    public static String appendFile(String root, String fileName) {
        if (root == null || root.isEmpty()) {
            return null;
        }
        if (root.endsWith("/")) {
            return root + fileName;
        }
        return root + File.separator + fileName;
    }

    /**
     * 获取扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

}
