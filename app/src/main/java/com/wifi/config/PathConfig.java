package com.wifi.config;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StatFs;
import android.provider.MediaStore.Images;
import android.util.Log;

import com.example.leonardo.watermeter.utils.ModifyImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PathConfig {
    private Activity context;
    private SwitchConfig mSwitchConfig;
    public static SdcardSelector sdcardItem = SdcardSelector.BUILT_IN;

    private List<String> videoList = new ArrayList<String>();

    public static enum SdcardSelector {
        BUILT_IN, EXTERNAL
    }

    public PathConfig(Activity context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        mSwitchConfig = new SwitchConfig(context);
        if (mSwitchConfig.readSdcardChoose())
            sdcardItem = SdcardSelector.EXTERNAL;
        else
            sdcardItem = SdcardSelector.BUILT_IN;
    }

    public void setSdcardItem(SdcardSelector item) {
        sdcardItem = item;
    }

    /**
     * return video path, if the video is not exist, then create it
     *
     * @param parentFolder like:DCIM/VIDEO
     * @param videoName    like:VIDEO1.AVI
     * @return
     */
    public String getVideoPath(String parentFolder, String videoName) {
        String absolutePath = null;
        try {
            String sdCardDir;
            if (sdcardItem == SdcardSelector.BUILT_IN) {
                sdCardDir = SdCardUtils.getFirstExternPath();
            } else {
                sdCardDir = SdCardUtils.getSecondExternPath();
                if (sdCardDir == null)
                    return null;
            }
            String videoPath = sdCardDir + "/" + parentFolder + "/";
            File folder = new File(videoPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File saveVideo = new File(videoPath + videoName);
            if (!saveVideo.exists()) {
                saveVideo.createNewFile();
            }
            absolutePath = saveVideo.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return absolutePath;
    }

    /**
     * return the sdcard path
     *
     * @return
     */
    public String getRootPath() {
        String sdCardDir;
        if (sdcardItem == SdcardSelector.BUILT_IN) {
            sdCardDir = SdCardUtils.getFirstExternPath();
        } else {
            sdCardDir = SdCardUtils.getSecondExternPath();
            if (sdCardDir == null)
                return null;
        }

        return sdCardDir;
    }

    /**
     * save photos
     *
     * @param imagePath like:IMAGE1.JPG
     * @param imagedata image data
     */

    public void savePhoto(String imagePath, byte[] imagedata) {
        String sdCardDir = getRootPath();
        try {
            FileOutputStream fout;
            fout = new FileOutputStream(imagePath);
            Bitmap bitmapsave = BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length);
            bitmapsave = compressImage(ModifyImage.rotateBitmapByDegree(bitmapsave, 90));
            bitmapsave.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public List<String> getImagesList(final File photoPath) {
        List<String> photoList = new ArrayList<String>();

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isFile()
                        && (file.getAbsolutePath().toLowerCase().endsWith(".bmp") || file.getAbsolutePath().toLowerCase().endsWith(".jpg") || file.getAbsolutePath().toLowerCase().endsWith(".png"))) {
                    return true;
                } else
                    return false;
            }
        };

        File[] filterFiles = photoPath.listFiles(filter);
        if (null != filterFiles && filterFiles.length > 0) {

            for (File file : filterFiles) {
                // ��߶��ļ����й���
                if (photoList.indexOf(file.getAbsolutePath()) == -1) {
                    // Log.e(Tag, file.getAbsolutePath());
                    photoList.add(file.getAbsolutePath());
                }

            }
        }
        return photoList;
    }

    public List<String> getVideosList(final File videoPath) {
        getVideoList(videoPath);
        return videoList;
    }

    private void getVideoList(final File videoPath) {
        List<String> temp = new ArrayList<String>();
        File[] files = videoPath.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    if (files[i].getAbsolutePath().toLowerCase().endsWith(".avi") || files[i].getAbsolutePath().toLowerCase().endsWith(".3gp")
                            || files[i].getAbsolutePath().toLowerCase().endsWith(".mp4")) {
                        String lcPath = files[i].getAbsolutePath().toLowerCase();
                        String absPath = files[i].getAbsolutePath();
                        String photopath = null;
                        if (lcPath.contains(".avi")) {
                            photopath = absPath.replace(".avi", ".jpg");
                        } else if (lcPath.contains(".mp4")) {
                            photopath = absPath.replace(".mp4", ".jpg");
                        } else if (lcPath.contains(".3gp")) {
                            photopath = absPath.replace(".3gp", ".jpg");
                        }
                        File photofile = new File(photopath);
                        if (photofile.exists()) {
                            if (temp.indexOf(photofile.getAbsolutePath()) == -1) {
                                temp.add(photofile.getAbsolutePath());
                                videoList.add(photofile.toString());
                            }
                        }
                    }

                } else {
                    if (files[i].isDirectory() && files[i].getPath().indexOf("/.") == -1) {
                        getVideoList(files[i]);
                    }
                }
            }
        }
    }

    private Uri path2uri(Uri uri) {
        if (uri.getScheme().equals("file")) {
            String path = uri.getEncodedPath();
            // Log.d("", "path1 is " + path);
            if (path != null) {
                path = Uri.decode(path);
                // Log.d("", "path2 is " + path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI, new String[]{Images.ImageColumns._ID}, buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    Log.d("", "uri_temp is " + uri_temp);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

    @SuppressWarnings("deprecation")
    public int getSdcardAvilibleSize() {
        String sdCardDir = getRootPath();
        StatFs stat = new StatFs(new File(sdCardDir).getPath());
        /* ��ȡblock��SIZE */
        long blockSize = stat.getBlockSize();
        /* ���е�Block������ */
        long availableBlocks = stat.getAvailableBlocks();
        /* ����bit��Сֵ */
        return (int) (availableBlocks * blockSize / 1024 / 1024);
    }

    @SuppressWarnings("deprecation")
    public int getSdcardTotalSize() {
        String sdCardDir = getRootPath();
        StatFs stat = new StatFs(new File(sdCardDir).getPath());
        /* ��ȡblock��SIZE */
        long blockSize = stat.getBlockSize();
        /* ���е�Block������ */
        long blockCount = stat.getBlockCount();
        /* ����bit��Сֵ */
        return (int) (blockCount * blockSize / 1024 / 1024);
    }

    /**
     * ��¼���޸�ʱ����Ⱥ�����
     */
    public List<File> sortVideoList(List<File> photoList) {
        Collections.sort(photoList, new Comparator<File>() {

            @Override
            public int compare(File curFile, File nextFile) {
                // TODO Auto-generated method stub
                long firstDate = curFile.lastModified();
                long nextDate = nextFile.lastModified();
                return (firstDate > nextDate) ? 1 : -1; // �����ڣ������޸ģ�����1�������޸�ʱ��˳��
            }
        });
        return photoList;
    }

    /**
     * delete all the files in the folder and it's sub folders
     */
    public void deleteFiles(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    this.deleteFiles(files[i]);
                }
            }
            file.delete();
        }
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        System.out.println("压缩-质量压缩时：" + baos.toByteArray().length);
        int options = 60;
        Log.i("shn", "压缩的图片质量比为1:" + baos.toByteArray().length / 1024);
        while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            Log.i("shn", "压缩的图片质量比为2：" + baos.toByteArray().length / 1024);
            baos.reset();//重置baos即清空baos
            options -= 5;//每次都减少5
            System.out.println("压缩-质量压缩时-内部：" + baos.toByteArray().length);
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(
                baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
