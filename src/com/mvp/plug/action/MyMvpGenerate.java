package com.mvp.plug.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.mvp.plug.biz.checker.impl.EnvironmentChecker;
import com.mvp.plug.gui.MyDialog;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyMvpGenerate extends AnAction {

    private Project project;
    private EnvironmentChecker checker;
    private String packageName = "";
    private String mAuthor;//作者
    private String mModuleName;//模块名称
    private String mModuleType;

    private enum CodeType {
        Activity, ActivityXML, ActivityList, ActivityListXML,
        Fragment, FragmentXML, FragmentList, FragmentListXML,
        Contract, Presenter, BindingActivity, BindingFragment,
        Adapter, AdapterXml
    }

    public MyMvpGenerate() {
    }

    public MyMvpGenerate(String mAuthor, String mModuleName) {
        this.mAuthor = mAuthor;
        this.mModuleName = mModuleName;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        checker = new EnvironmentChecker(e);
        project = e.getData(PlatformDataKeys.PROJECT);
        packageName = checker.getPackageName();

        showDialog();
        refreshProject(e);
    }

    /**
     * 刷新项目
     *
     * @param e
     */
    private void refreshProject(AnActionEvent e) {
        e.getProject().getBaseDir().refresh(false, true);
    }

    /**
     * 初始化Dialog
     */
    private void showDialog() {
        MyDialog myDialog = new MyDialog(new MyDialog.DialogCallBack() {
            @Override
            public void ok(String author, String moduleName, String moduleType) {
                mAuthor = author;
                mModuleName = moduleName;
                mModuleType = moduleType;
                createClassFiles();
                Messages.showInfoMessage(project, "create mvp code success", "title");
            }
        });
        myDialog.setVisible(true);
    }

    private void createClassFiles() {
        if (mModuleType.equals("activity")) {
            createClassFile(CodeType.Activity);
            createClassFile(CodeType.ActivityXML);
            createClassFile(CodeType.Contract);
            createClassFile(CodeType.Presenter);
            createClassFile(CodeType.BindingActivity);
        } else if (mModuleType.equals("fragment")) {
            createClassFile(CodeType.Fragment);
            createClassFile(CodeType.FragmentXML);
            createClassFile(CodeType.Contract);
            createClassFile(CodeType.Presenter);
            createClassFile(CodeType.BindingFragment);
        } else if (mModuleType.equals("activityList")) {
            createClassFile(CodeType.ActivityList);
            createClassFile(CodeType.ActivityListXML);
            createClassFile(CodeType.Contract);
            createClassFile(CodeType.Presenter);
            createClassFile(CodeType.BindingActivity);
            createClassFile(CodeType.Adapter);
            createClassFile(CodeType.AdapterXml);
        } else if (mModuleType.equals("fragmentList")) {
            createClassFile(CodeType.FragmentList);
            createClassFile(CodeType.FragmentListXML);
            createClassFile(CodeType.Contract);
            createClassFile(CodeType.Presenter);
            createClassFile(CodeType.BindingFragment);
            createClassFile(CodeType.Adapter);
            createClassFile(CodeType.AdapterXml);
        } else {
            Messages.showInfoMessage(project, "请输入Activity或者Fragment", "警告");
        }
    }

    /**
     * 读取模板文件中的字符内容
     *
     * @param fileName 模板文件名
     * @return
     */
    private String ReadTemplateFile(String fileName) {
        InputStream in = null;
        in = this.getClass().getResourceAsStream("/com/mvp/plug/template/" + fileName);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
            inputStream.close();
        }

        return outputStream.toByteArray();
    }

    /**
     * 替换模板中字符
     *
     * @param content
     * @return
     */
    private String dealTemplateContent(String content) {
        content = content.replace("$name", mModuleName);
        if (content.contains("$packagename")) {
            content = content.replace("$packagename", packageName + "." + mModuleName.toLowerCase());
        }
        if (content.contains("$basepackagename")) {
            content = content.replace("$basepackagename", packageName + ".base");
        }
        if (content.contains("$scope")) {
            if (mModuleType.contains("activity")) {
                content = content.replace("$scope", "@PerActivity");
            } else if (mModuleType.contains("fragment")) {
                content = content.replace("$scope", "@PerFragment");
            }
        }

        content = content.replace("$author", mAuthor);
        content = content.replace("$date", getDate());
        content = content.replace("$xml", mModuleName.toLowerCase());
        return content;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public String getDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 生成
     *
     * @param content   类中的内容
     * @param classPath 类文件路径
     * @param className 类文件名称
     */
    private void writeToFile(String content, String classPath, String className) {
        System.out.println(classPath + "        " + className);
        try {
            if (project != null) {
                classPath = project.getBaseDir().getPath() + "/generate/" + classPath;
            }
            File floder = new File(classPath);
            if (!floder.exists()) {
                floder.mkdirs();
            }
            System.out.println(floder.getAbsoluteFile());
            File file = new File(classPath + "/" + className);
            if (!file.exists()) {
                file.createNewFile();
            }
            System.out.println(file.getAbsoluteFile());
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            Messages.showErrorDialog(
                    e.toString(),
                    className);
        }

    }

    /**
     * 生成mvp框架代码
     *
     * @param codeType
     */
    private void createClassFile(CodeType codeType) {
        String fileName = "";
        String content = "";
        String appPath = "";//getAppPath();
        switch (codeType) {
            case Activity:
                fileName = "TemplateActivity.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), mModuleName + "Activity.java");
                break;
            case ActivityList:
                fileName = "TemplateActivityList.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), mModuleName + "Activity.java");
                break;
            case Fragment:
                fileName = "TemplateFragment.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), mModuleName + "Fragment.java");
                break;
            case FragmentList:
                fileName = "TemplateFragmentList.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), mModuleName + "Fragment.java");
                break;
            case Contract:
                fileName = "TemplateContract.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), mModuleName + "Contract.java");
                break;
            case Presenter:
                fileName = "TemplatePresenter.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), mModuleName + "Presenter.java");
                break;
            case BindingActivity:
                fileName = "TemplateBindingActivity.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), "ActivityBindingModule.java");
                break;
            case BindingFragment:
                fileName = "TemplateBindingFragment.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), "FragmentBindingModule.java");
                break;
            case ActivityXML:
                fileName = "TemplateActivityXml.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), "activity_" + mModuleName.toLowerCase() + ".xml");
                break;
            case ActivityListXML:
                fileName = "TemplateActivityListXml.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), "activity_" + mModuleName.toLowerCase() + ".xml");
                break;
            case FragmentXML:
                fileName = "TemplateFragmentXml.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), "fragment_" + mModuleName.toLowerCase() + ".xml");
                break;
            case FragmentListXML:
                fileName = "TemplateFragmentListXml.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), "fragment_" + mModuleName.toLowerCase() + ".xml");
                break;
            case Adapter:
                fileName = "TemplateAdapter.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), mModuleName + "Adapter.java");
                break;
            case AdapterXml:
                fileName = "TemplateAdapterXml.txt";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + mModuleName.toLowerCase(), "item_" + mModuleName.toLowerCase() + ".xml");
                break;
        }
    }


    public static void main(String[] args) {
        MyMvpGenerate mvpCreateAction = new MyMvpGenerate("kcj", "Test");
//        mvpCreateAction.createClassFile(CodeType.Activity);
//        mvpCreateAction.createClassFile(CodeType.Fragment);
//        mvpCreateAction.createClassFile(CodeType.Contract);
//        mvpCreateAction.createClassFile(CodeType.Presenter);
//        mvpCreateAction.createClassFile(CodeType.BindingActivity);
//        mvpCreateAction.createClassFile(CodeType.BindingFragment);
//        mvpCreateAction.createClassFile(CodeType.FragmentList);
//        mvpCreateAction.createClassFile(CodeType.FragmentListXML);
//        mvpCreateAction.createClassFile(CodeType.Adapter);
//        mvpCreateAction.createClassFile(CodeType.AdapterXml);
//        mvpCreateAction.createClassFile(CodeType.ActivityList);
//        mvpCreateAction.createClassFile(CodeType.ActivityListXML);
    }
}
