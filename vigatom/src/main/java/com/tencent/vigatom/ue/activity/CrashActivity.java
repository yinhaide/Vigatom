package com.tencent.vigatom.ue.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import com.tencent.vigatom.R;
import com.tencent.vigatom.ue.view.ViStatusBarView;
import com.tencent.vigatom.utils.ScreenUtil;
import dalvik.system.DexFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 异常崩溃的Activity
 */
public class CrashActivity extends Activity {

    public static String ERROR_INTENT = "ERROR_INTENT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ScreenUtil.isLanscape(this) ? R.layout.vigatom_activity_crash_l : R.layout.vigatom_activity_crash_p);
        initView(getIntent());
    }

    /**
     * 初始化
     */
    private void initView(Intent intent){
        ViStatusBarView viStatusBarView = new ViStatusBarView(getWindow());
        //设置状态栏颜色
        viStatusBarView.setVigatomStyle();
        //白色图标风格
        viStatusBarView.setWhiteStyle();
        if(intent != null){
            TextView tvContent = findViewById(R.id.tv_content);
            //支持点击滑动
            tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
            //设置内容项目
            tvContent.setText(extractMessageByRegular(intent.getStringExtra(ERROR_INTENT)));
        }
    }

    /**
     * 使用正则表达式替换括号中的内容
     * @param msg 查找内容
     * @return 找到的内容
     */
    public Spanned extractMessageByRegular(String msg){
        List<String> dexClass = getClassName(getPackageName(),getPackageCodePath());
        String newMsg = msg;
        String reg2 = "\\((.*?)\\)";
        //String reg3 = "(\\([^\\)]*\\))";
        Pattern p = Pattern.compile(reg2);
        Matcher m = p.matcher(msg);
        while(m.find()){
            String content = m.group().substring(1, m.group().length()-1);
            if(content.contains(".") && content.contains(":")){//找出 xx.java:xx 格式的字符串
                String[] className = content.split(":");
                //按着.拆分类，取第一个，也就是ClassSimpleName
                String[] classSimple = className[0].split("\\.");
                if(dexClass.contains(classSimple[0])){
                    newMsg = newMsg.replace(content,"<font color='#398EFF'>"+content+"</font>");
                }else{
                    newMsg = newMsg.replace(content,"<font color='#aaaaaa'>"+content+"</font>");
                }
            }
        }
        return Html.fromHtml(newMsg);
    }

    /**
     * 找出指定包名下所有的类
     * @param packageName 包名
     * @return List<String> 找到的所有的类
     */
    public List<String> getClassName(String packageName,String packageCodePath){
        List<String> classNameList = new ArrayList<>();
        try {
            //DexFile在模拟器中解析不出来
            DexFile df = new DexFile(packageCodePath);//通过DexFile查找当前的APK中可执行文件
            Enumeration<String> enumeration = df.entries();//获取df中的元素,这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                String className = enumeration.nextElement();
                if (className.contains(packageName) || className.contains("com.tencent.vigatom")) {//在当前所有可执行的类里面查找包含有该包名的所有类
                    if(!className.contains("$")){//不需要类中类
                        if(className.contains(".")){
                            //按着.拆分类，取最后一个，也就是ClassSimpleName
                            String[] classNames = className.split("\\.");
                            classNameList.add(classNames[classNames.length-1]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  classNameList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
