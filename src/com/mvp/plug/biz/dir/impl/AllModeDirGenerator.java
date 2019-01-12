package com.mvp.plug.biz.dir.impl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.mvp.plug.biz.file.generator.JavaModeFileGenerator;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: KCJ
 * Date: 2019/1/12 11:20
 * Description:
 */
public class AllModeDirGenerator extends BaseDirGenerator {


    public AllModeDirGenerator(@NotNull AnActionEvent actionEvent, @NotNull String prefix) {
        super(actionEvent, prefix);
    }

    @Override
    protected void onGenerateForkDirs(@NotNull String subPackage) {
        String prefix = subPackage.replace("contract", "");
        String subPackageM = prefix + "model";
        String subPackageP = prefix + "presenter";//if prefix exist.
        myContractDir = myCurrentDir.findSubdirectory(subPackage);
        myModelDir = moveDirPointer(myCurrentDir, subPackageM);
        myPresenterDir = moveDirPointer(myCurrentDir, subPackageP);
    }

    @Override
    public void start() {
        Messages.showInfoMessage(myProject, "1", "title");
        generateDirsBasedOnSuffix("contract");
        Messages.showInfoMessage(myProject, "2", "title");
        new JavaModeFileGenerator(myProject, myContractDir, myModelDir, myPresenterDir, myPrefix).start();
    }
}
