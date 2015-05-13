package com.tudou.core.zeus.cms;

import java.io.Serializable;

/**
 * Created by wanganqing on 2015/3/11.
 */
public class DataRequest implements Serializable {
    public static final String DataType_Json = "json";
    public    String taskName;
    public    Boolean isFullUpdate;
    public    String dataType;
    public    String[] data;
    //描述rdd履历定义的常量
    public static final int RDD_HIST_SOURCE_INPUT = 0;
    public static final int RDD_HIST_SOURCE_OUTPUT = 1;
    public static final int RDD_HIST_UPDATE_FULL = 1;
    public static final int RDD_HIST_UPDATE_DELTA  = 0;
    public DataRequest(){ }

    public DataRequest(String taskName, Boolean isFullUpdate, String[] data) {
        this(  taskName,   isFullUpdate,   DataType_Json,  data);
    }

    public DataRequest(String taskName, Boolean isFullUpdate, String dataType, String[] data) {
        this.taskName = taskName;
        this.isFullUpdate = isFullUpdate;
        this.dataType = dataType;
        this.data = data;
    }
}
