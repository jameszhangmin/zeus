package com.tudou.core.zeus.cms;

import java.io.Serializable;

/**
 * Created by wanganqing on 2015/3/11.
 */
public class SortResponse implements Serializable {
    public static final String DataType_common = "common";//id_id_id这种形式
    public SortRequest request;
    public String dataType;
    public String[] data;
    public SortResponse(){ }
    public SortResponse(SortRequest request, String[] data) {
        this(request, DataType_common, data);
    }

    public SortResponse(SortRequest request, String dataType, String[] data) {
        this.request = request;
        this.dataType = dataType;
        this.data = data;
    }
}
