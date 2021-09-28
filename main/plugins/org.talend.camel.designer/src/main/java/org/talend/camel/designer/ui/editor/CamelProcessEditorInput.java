// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.ui.editor;

import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.runtime.process.TalendProcessArgumentConstant;
import org.talend.core.ui.editor.JobEditorInput;
import org.talend.designer.core.ui.editor.process.Process;
import org.talend.designer.core.ui.projectsetting.ProjectSettingManager;
import org.talend.repository.utils.MavenVersionUtils;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelProcessEditorInput extends JobEditorInput {

    public CamelProcessEditorInput(ProcessItem item, boolean load, Boolean lastVersion, Boolean readonly)
            throws PersistenceException {
        super(item, load, lastVersion, readonly);
    }

//    public CamelProcessEditorInput(CamelProcessItem processItem, boolean load) throws PersistenceException {
//        this(processItem, load, null, null);
//    }

    public CamelProcessEditorInput(ProcessItem processItem, boolean load, Boolean lastVersion) throws PersistenceException {
        this(processItem, load, lastVersion, null);
    }

    @Override
    protected Process createProcess() {
    	Item item = getItem();
    	if(null == MavenVersionUtils.get(item, TalendProcessArgumentConstant.ARG_BUILD_TYPE)) {
    		MavenVersionUtils.put(item.getProperty(), TalendProcessArgumentConstant.ARG_BUILD_TYPE, "ROUTE");
    	}
    	
    	return new RouteProcess(item.getProperty());
    }

    @Override
    protected void saveProcessBefore() {
        ProjectSettingManager.defaultUseProjectSetting((Process) getLoadedProcess());
    }

}
