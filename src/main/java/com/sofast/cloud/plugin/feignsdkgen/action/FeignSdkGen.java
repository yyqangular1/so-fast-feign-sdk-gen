/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package com.sofast.cloud.plugin.feignsdkgen.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.sofast.cloud.plugin.feignsdkgen.dict.GlobalDict;
import com.sofast.cloud.plugin.feignsdkgen.ui.MethodListDialog;

import java.awt.*;

/**
 * @author : NCIT
 * @date : 2021/9/17 4:21 下午
 */
public class FeignSdkGen extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取当前在操作工程的上下文
        Project project = e.getProject();

        if (project == null) {
            return;
        }

        MethodListDialog dialog = new MethodListDialog(project, e);
        dialog.setTitle(GlobalDict.TITLE_INFO);
        int x = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
        int y = Toolkit.getDefaultToolkit().getScreenSize().height / 2;
        dialog.setLocation(x - 300, y - 250);
        dialog.pack();
        dialog.setSize(600, 500);
        dialog.setMinimumSize(new Dimension(500, 400));
        dialog.setModal(true);
        dialog.setVisible(true);


    }
}
