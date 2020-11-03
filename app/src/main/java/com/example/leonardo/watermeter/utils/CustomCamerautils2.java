package com.example.leonardo.watermeter.utils;

import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2018/3/1 0001.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CustomCamerautils2 implements Comparator<Size> {
    /*
   记录初始两手指间距
    */
    public static int getFingerSpacing(MotionEvent event) {
        float x1 = event.getX(0);
        float y1 = event.getY(0);
        float x2 = event.getX(1);
        float y2 = event.getY(1);
        float x = x1 - x2;
        float y = y1 - y2;
        return (int) Math.sqrt(x * x + y * y);
    }

    /*
    支持三个缩放倍数 1.0 2.0 3.0 4.0 5.0
     */
    public static void getRectZoom(double lastzoom, Rect maxZoomrect, Camera2Preview preview, boolean isenlarge) {
        if (isenlarge) {
            if (lastzoom < 2.0) {
                lastzoom = 2.0;
            } else if (lastzoom >= 2.0 && lastzoom < 3.0) {
                lastzoom = 3.0;
            } else if (lastzoom >= 3.0 && lastzoom < 4.0) {
                lastzoom = 4.0;
            } else if (lastzoom >= 4.0 && lastzoom < 5.0) {
                lastzoom = 5.0;
            } else if (lastzoom >= 5.0 && lastzoom < 6.0) {
                lastzoom = 6.0;
            } else if (lastzoom >= 6.0 && lastzoom < 7.0) {
                lastzoom = 7.0;
            } else {
                lastzoom = 8.0;
            }
        } else {
            if (lastzoom > 7.0 && lastzoom <= 8.0) {
                lastzoom = 7.0;
            } else if (lastzoom > 6.0 && lastzoom <= 7.0) {
                lastzoom = 6.0;
            } else if (lastzoom > 5.0 && lastzoom <= 6.0) {
                lastzoom = 5.0;
            } else if (lastzoom > 4.0 && lastzoom <= 5.0) {
                lastzoom = 4.0;
            } else if (lastzoom > 3.0 && lastzoom <= 4.0) {
                lastzoom = 3.0;
            } else if (lastzoom > 2.0 && lastzoom <= 3.0) {
                lastzoom = 2.0;
            } else {
                lastzoom = 1.0;
            }
        }
        Rect picRect = getLocalRect(maxZoomrect, lastzoom);
        preview.SetPreviewRect(picRect, lastzoom);
    }


    public static Rect getLocalRect(Rect maxZoomrect, double zoom) {
        Rect picRect = new Rect(maxZoomrect);
        int xDistance = (picRect.right - picRect.left);
        int yDistance = (picRect.bottom - picRect.top);
        int xZoomDis = getZoomDis(xDistance, zoom);
        int yZoomDis = getZoomDis(yDistance, zoom);
        picRect.top = (int) (maxZoomrect.top + yZoomDis);
        picRect.left = (int) (maxZoomrect.left + xZoomDis);
        picRect.right = (int) (maxZoomrect.right - xZoomDis);
        picRect.bottom = (int) (maxZoomrect.bottom - yZoomDis);
        Log.i("waterMeter", "top " + picRect.top + " left " + picRect.left + " right " + picRect.right + " bottom " + picRect.bottom + " zoom " + zoom);
        return picRect;
    }

    public static int getZoomDis(int distance, double zoom) {
        return (int) ((distance - distance / zoom) / 2);
    }

    public static Size chooseMinSize(Size[] choices, int width, int height) {
        // 收集摄像头支持的大过预览Surface的分辨率
        List<Size> bigEnough = new ArrayList<>();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * width / height && option.getWidth() >= height && option.getHeight() >= width) {
                bigEnough.add(option);
            }
        }
        // 如果找到多个预览尺寸，获取其中面积最大的
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CustomCamerautils2());
        } else {
            return GetSize(choices, width, height);
        }
    }


    public static Size chooseMaxSize(Size[] choices, int width, int height) {
        // 收集摄像头支持的大过预览Surface的分辨率
        List<Size> bigEnough = new ArrayList<>();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * width / height && option.getWidth() >= height && option.getHeight() >= width) {
                bigEnough.add(option);
            }
        }
        // 如果找到多个预览尺寸，获取其中面积最大的
        if (bigEnough.size() > 0) {
            return Collections.max(bigEnough, new CustomCamerautils2());
        } else {
            return GetSize(choices, width, height);
        }
    }

    public static Size GetSize(Size[] sizes, int wid, int hei) {
        int Max = wid * hei;
        int Min = 0;
        int flag = 0;
        for (int i = 0; i < sizes.length; i++) {
            if ((sizes[i].getWidth() * sizes[i].getHeight()) <= Max && sizes[i].getWidth() <= wid && sizes[i].getHeight() <= hei) {
                int middle = sizes[i].getWidth() * sizes[i].getHeight();
                if (middle > Min && middle <= Max) {
                    Min = middle;
                    flag = i;
                }
            }
        }
        System.out.println("找不到合适的预览尺寸！！！" + sizes[flag].getWidth() + "-----" + sizes[flag].getHeight());
        return sizes[flag];
    }


    @Override
    public int compare(Size o1, Size o2) {
        // 强转为long保证不会发生溢出
        return Long.signum((long) o1.getWidth() * o1.getHeight() - (long) o2.getWidth() * o2.getHeight());
    }


    public static Rect calculateTapArea(float x, float y, float coefficient, int screenWidth, int screenHeight) {
        int FOCUS_AREA_SIZE = 180;
        int areaSize = Float.valueOf(FOCUS_AREA_SIZE * coefficient).intValue();
        int centerX = clamp((int) x, areaSize / 2, screenWidth - areaSize / 2);
        int centerY = clamp((int) y, areaSize / 2 + 135, screenHeight - 15 - areaSize / 2);
        return new Rect(centerX - areaSize * 2 / 5, centerY - areaSize * 2 / 5, centerX + areaSize * 2 / 5, centerY + areaSize * 2 / 5);
    }

    private static int clamp(int touchvalue, int Minvalue, int Maxvalue) {
        if (touchvalue >= Minvalue && touchvalue <= Maxvalue) {
            return touchvalue;
        } else if (touchvalue < Minvalue) {
            return Minvalue;
        } else {
            return Maxvalue;

        }
    }
}
