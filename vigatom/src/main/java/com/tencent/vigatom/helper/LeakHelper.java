package com.tencent.vigatom.helper;

import android.app.Activity;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 内存泄漏监控器，因为是在UI线程空闲的时候采取做检测，所以不会造成卡顿
 */
public class LeakHelper {
    private final static String TAG = LeakHelper.class.getSimpleName();
    //volatile防止指令重排序，内存可见(缓存中的变化及时刷到主存，并且其他的线程副本内存失效，必须从主存获取)，但不保证原子性
    private static volatile LeakHelper singleton = null;
    //主线程的Handler
    private Handler mainHandler;
    //WeakHashMap，用来缓存Activity相关的对象
    private WeakHashMap<Activity, String> activityWeakHashMap = new WeakHashMap<>();
    //WeakHashMap，用来缓存Fragemt相关的对象
    private WeakHashMap<Fragment, String> fragmentWeakHashMap = new WeakHashMap<>();
    //WeakHashMap，用来缓存View相关的对象
    private WeakHashMap<View, String> viewWeakHashMap = new WeakHashMap<>();
    //WeakHashMap，用来缓存其他对象相关的对象
    private WeakHashMap<Object, String> objectWeakHashMap = new WeakHashMap<>();
    //是否开启检测，默认不开启
    private boolean enable;
    //延迟检测时间
    private int delayTime = 5 * 1000;
    //上次执行的Runnable
    private Runnable lastRunnable;

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static LeakHelper getInstance() {
        if (singleton == null) {
            synchronized (LeakHelper.class) {
                if (singleton == null) {
                    singleton = new LeakHelper();
                }
            }
        }
        return singleton;
    }

    /**
     * 构造函数
     */
    private LeakHelper() {
        //如果已存在，直接抛出异常，保证只会被new 一次，解决反射破坏单例的方案
        if (singleton != null) {
            throw new RuntimeException("对象已存在不可重复创建");
        }
        //拿到主线程
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 设置延迟检测时间,页面销毁后延迟一定时间去检测内存泄漏
     *
     * @param timeMil 延迟时间
     */
    public LeakHelper setDelayTime(int timeMil) {
        delayTime = timeMil;
        return singleton;
    }

    /**
     * 是否开启检测
     *
     * @return 结果
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * 设置是否要检测
     *
     * @param enable 是否检测
     */
    public LeakHelper setEnable(boolean enable) {
        this.enable = enable;
        return singleton;
    }

    /**
     * 监控Activity的泄漏，要在onDestroy中调用
     */
    public void monitorActivity(Activity activity){
        activityWeakHashMap.put(activity,activity.getClass().getSimpleName());
        //UI线程空闲的时候才会执行，不会造成卡顿
        monitor(activityWeakHashMap);
    }


    /**
     * 监控Fragment的泄漏，要在onDestroy中调用
     * @param fragment 监控对象
     */
    public void monitorFragment(Fragment fragment){
        fragmentWeakHashMap.put(fragment,fragment.getClass().getSimpleName());
        //UI线程空闲的时候才会执行，不会造成卡顿
        monitor(fragmentWeakHashMap);
    }

    /**
     * 监控View的泄漏，要在onDettach中调用
     * @param view 监控对象
     */
    public void monitorView(View view){
        viewWeakHashMap.put(view,view.getClass().getSimpleName());
        //UI线程空闲的时候才会执行，不会造成卡顿
        monitor(viewWeakHashMap);
    }

    /**
     * 监控带生命周期对象的泄漏，要在需要释放的时候调用
     * @param object 监控对象
     */
    public void monitorObject(Object object){
        objectWeakHashMap.put(object,object.getClass().getSimpleName());
        //UI线程空闲的时候才会执行，不会造成卡顿
        monitor(objectWeakHashMap);
    }

    /**
     * 开始监控
     * @param weakHashMap 监控的对象
     * @param <T> 类型
     */
    private  <T> void monitor(WeakHashMap<T,String> weakHashMap){
        //如果不开启监控的话直接返回
        if (!enable) {
            return;
        }
        //UI线程空闲的时候才会执行，不会造成卡顿
        Looper.myQueue().addIdleHandler(() -> {
            //开始时间
            long begin = System.currentTimeMillis();
            //开始启动一次gc，正常耗时20ms
            Runtime.getRuntime().gc();
            //保证多次触发，只会执行最后一次检测
            if(lastRunnable != null){
                TimeoutHelper.getInstance().removeTask(lastRunnable);
            }
            lastRunnable = () -> {
                try {
                    //执行一次回收，大概耗时20ms
                    Runtime.getRuntime().gc();
                    //延迟时间
                    long delayTime = 100;
                    //gc之后延迟一定时间，耗时100ms
                    SystemClock.sleep(delayTime);
                    //调用所有对象的finalize方法，释放对象，耗时1ms
                    System.runFinalization();
                    //开始检测泄漏
                    HashMap<String, Integer> hashMap = new HashMap<>();
                    for (Map.Entry<T, String> activityStringEntry : weakHashMap.entrySet()) {
                        String name = activityStringEntry.getKey().getClass().getName();
                        Integer value = hashMap.get(name);
                        //统计一个泄漏出现的次数
                        if (value == null) {
                            hashMap.put(name, 1);
                        } else {
                            hashMap.put(name, value + 1);
                        }
                    }
                    onLeaksNext(System.currentTimeMillis() - begin - delayTime - this.delayTime, hashMap);
                }catch (Exception e){
                    e.printStackTrace();
                }
            };
            //延迟一定时间之后开始检测泄漏的情况
            TimeoutHelper.getInstance().putTask(lastRunnable, delayTime);
            return false;
        });
    }

    /************************ 内存泄漏回调 start *******************/

    //listener全局变量
    private OnLeaksListener onLeaksListener;

    //供外部调用的set方法
    public LeakHelper setOnLeaksListener(OnLeaksListener onLeaksListener) {
        this.onLeaksListener = onLeaksListener;
        return singleton;
    }

    //内部调用传递信息到外部
    private void onLeaksNext(long cosTime,HashMap<String, Integer> hashMap){
        if(this.onLeaksListener != null && mainHandler != null){
            mainHandler.post(() -> onLeaksListener.onLeaks(cosTime,hashMap));
        }
    }

    //定义listener
    public interface OnLeaksListener {
        void onLeaks(long cosTime, HashMap<String, Integer> hashMap);
    }

    /************************ 内存泄漏回调 end  *******************/
}
