package com.tudou.core.zeus.cms;

import java.io.Serializable;

/**
 * Created by wanganqing on 2015/3/13.
 */
public class TaskRegister implements Serializable {
    public String taskName;
    public String filedIDNames;
    public String fieldNames;
    public int defaultCacheMax;
    public String fullDataPath;
    public String updateDataPath;
    public int status;
    public TaskRegister(){ }
    public TaskRegister(String taskName, String filedIDNames, String fieldNames, int defaultCacheMax, String fullDataPath, String updateDataPath, int status) {
        this.taskName = taskName;
        this.filedIDNames = filedIDNames;
        this.fieldNames = fieldNames;
        this.defaultCacheMax = defaultCacheMax;
        this.fullDataPath = fullDataPath;
        this.updateDataPath = updateDataPath;
        this.status = status;
    }
}
