package com.mvp.plug.biz.dir.impl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.mvp.plug.biz.file.generator.JavaModeFileGenerator;
import org.jetbrains.annotations.NotNull;

/**
 * @author Administrator
 * @since 2017/4/9
 */
public class PresenterModeDirGenerator extends BaseDirGenerator {

    public PresenterModeDirGenerator(AnActionEvent actionEvent, String prefix) {
        super(actionEvent, prefix);
    }

    /**
     * start generation
     */
    @Override
    public void start() {
        generateDirsBasedOnSuffix("presenter");

        new JavaModeFileGenerator(myProject, myContractDir, myModelDir, myPresenterDir, myPrefix).start();
    }

    @Override
    protected void onGenerateForkDirs(@NotNull String subPackage) {
        String prefix = subPackage.replace("presenter", "");
        String subPackageM = prefix + "model";
        String subPackageC = prefix + "contract";//if prefix exist.
        myContractDir = moveDirPointer(myCurrentDir, subPackageC);
        myModelDir = moveDirPointer(myCurrentDir, subPackageM);
        myPresenterDir = myCurrentDir.findSubdirectory(subPackage);
    }
}
