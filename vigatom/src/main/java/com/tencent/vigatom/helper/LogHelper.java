package com.tencent.vigatom.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.tencent.vigatom.bean.RecordBean;
import com.tencent.vigatom.utils.ViLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 异常上报的类
 * 1、HandlerThread将loop转到子线程中处理，说白了就是将分担MainLooper的工作量，降低了主线程的压力，使主界面更流畅。
 * 2、开启一个线程起到多个线程的作用。处理任务是串行执行，源于消息队列的同步机制，按消息发送顺序进行处理。HandlerThread本质是一个线程，在线程内部，代码是串行处理的。
 * 3、但是由于每一个任务都将以队列的方式逐个被执行到，一旦队列中有某个任务执行时间过长，那么就会导致后续的任务都会被延迟处理。
 * 4、HandlerThread拥有自己的消息队列，它不会干扰或阻塞UI线程。
 * 5、虽然对于网络IO操作，HandlerThread并不适合，因为它只有一个线程，还得排队一个一个等着，但是我们刚好就是需要这样的一个场合去上传异常日志，避免抢占CPU。
 * 6、我们给HandlerThread设置一个较低优先级，让它能够空闲的时候或者CPU低负荷的时候工作，CPU高负荷的时候尽量让出，保证主线程流畅
 */
public class LogHelper extends HandlerThread {

    private static final String TAG = LogHelper.class.getSimpleName();
    //volatile关键字禁止指令重排
    private static volatile LogHelper singleton;
    private static final String VIGATOM = "vigatom";//日志的文件夹mnt/sdcard/Android/data/<package name>/files/vigatom
    private static final String OUTER_NAME = "outer.log";//外部日志的名字
    private static final String INNER_NAME = "inner.log";//内部日志的名字
    private static final String CRASH_NAME = "crash.log";//崩溃日志的名字
    private static RecordBean recordBean = new RecordBean();//日志的配置
    //消息队列
    private Handler handler;

    private LogHelper(String name) {
        super(name, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        // 给异常上报工作线程设置低优先级，下面两种方式最终都是对应同个优先级，一个是虚拟机的表现形式，一个是jni层的表现形式，指向linux内核同个属性，Process对优先级的划分更细
        startHandle();
    }

    public static LogHelper getInstance() {
        if (singleton == null) {
            synchronized (LogHelper.class) {
                if (singleton == null) {
                    singleton = new LogHelper("vilog-handler-thread");
                }
            }
        }
        return singleton;
    }

    /**
     * 启动线程
     */
    private void startHandle() {
        //开启一个线程，必须执行start才能初始化looper
        start();
        //在这个线程中创建一个handler对象
        handler = new Handler(getLooper());
    }

    /**
     * 执行任务
     * @param runnable 接口
     */
    public void process(Runnable runnable){
        handler.post(runnable);
    }

    /**
     * 退出要清除
     */
    public void release() {
        if (singleton != null) {
            singleton.quit();
        }
        singleton = null;
    }

    /**
     * 设置日志的配置
     */
    public static void setRecordBean(Context context, RecordBean recordBean) {
        LogHelper.recordBean = recordBean;
        LogHelper.getInstance().process(() -> {
            //删除超时的日志
            deleteTimeoutLog(context,recordBean.getSaveDay());
        });
    }

    /**
     * 写入内部日志
     */
    public static void writeInnerLog(Context context,String content){
        if(recordBean.isInnerEnable()){
            LogHelper.getInstance().process(() -> {
                writeLog(context,content,INNER_NAME);
            });
        }
    }

    /**
     * 写外部日志
     */
    public static void writeOuterLog(Context context,String content){
        if(recordBean.isOuterEnable()){
            LogHelper.getInstance().process(() -> {
                writeLog(context,content,OUTER_NAME);
            });
        }
    }

    /**
     * 写崩溃日志
     */
    public static void writeCrashLog(Context context,String content){
        if(recordBean.isCrashEnable()){
            LogHelper.getInstance().process(() -> {
                writeLog(context,content,CRASH_NAME);
            });
        }
    }

    /**
     * 写入日志文件
     * 这类文件不应该存在SD卡的根目录下，而应该存在mnt/sdcard/Android/data/< package name >/files/…这个目录下。这类文件应该随着App的删除而一起删除
     * 需要权限
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
     */
    @SuppressLint("SimpleDateFormat")
    private static void writeLog(Context context,final String origin,final String filename){
        //IO操作放在线程池中操作，单核心线程执行，保证先进先执行
        //SD卡是否已装入
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //当天的日期
            String dateFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            //日志路径
            File logFilesDir = context.getExternalFilesDir(VIGATOM);
            if(logFilesDir != null){
                File folderFile = new File(logFilesDir.getAbsolutePath() + File.separator+dateFolder);
                if(!folderFile.exists()){
                    folderFile.mkdir();
                }
                File logFile = new File(folderFile.getAbsolutePath()+File.separator+filename);
                if(!logFile.exists()){
                    try {
                        logFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(logFile.exists()){//文件存
                    String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
                    String content = "\n" + dateStr + "--" + origin;
                    try {
                        FileOutputStream output = new FileOutputStream(logFile, true);
                        byte[] desBytes = content.getBytes(StandardCharsets.UTF_8);
                        output.write(desBytes);
                        output.flush();
                        output.close();
                        ViLog.v(TAG+"::writeLog-->成功写入日志:"+content +" filename:"+filename);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ViLog.e(TAG+"::writeLog-->写入日志出错:"+e.toString());
                    }
                }
            }
        }
    }

    /**
     * 删除超过指定日期的日志
     * @param context 上下文
     */
    @SuppressLint("SimpleDateFormat")
    private static void deleteTimeoutLog(Context context,int saveDay){
        //SD卡是否已装入
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //日志路径
            File logFilesDir = context.getExternalFilesDir(VIGATOM);
            if(logFilesDir != null){
                //找出目录下所有的文件夹
                File[] files = logFilesDir.listFiles();
                if(files != null && files.length > 0){
                    for(File file : files){
                        if(file.isDirectory()){//只删除文件夹
                            String folderName = file.getName();
                            try {
                                // 用parse方法，可能会异常，所以要try-catch
                                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(folderName);
                                if(date != null){
                                    //文件超过指定的日期就删除
                                    if(System.currentTimeMillis() - date.getTime() > saveDay * 24 * 3600 *1000){
                                        deleteFolderAndFile(file);
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除整个文件(包括文件夹以及文件)
     * @param file 文件对象
     */
    private static void deleteFolderAndFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFolderAndFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }
}
