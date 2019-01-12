package com.mvp.plug.biz.config.impl;

import com.intellij.openapi.project.Project;
import com.mvp.plug.biz.config.MvpConfigurable;
import com.mvp.plug.gui.MvpHelperConfigPanel;

/**
 * @author Administrator
 * @since 2017/4/25.
 */
public class MvpConfig implements MvpConfigurable {

    private final MvpConfigurable configurable;

    public MvpConfig(Project project) {
        configurable = new MvpHelperConfigPanel(project, false);
    }

    @Override
    public boolean hasSuffix() {
        return configurable.hasSuffix();
    }

    @Override
    public String getSuffix() {
        return configurable.getSuffix();
    }
}
