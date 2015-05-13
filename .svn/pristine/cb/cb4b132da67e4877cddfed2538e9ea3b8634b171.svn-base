package com.tudou.core.zeus.cms;

import java.io.File;
import java.io.Serializable;

/**
 * Created by wanganqing on 2015/3/11.
 */
public class SortRequest implements Serializable {
    public String taskName;
    public String[] sortFields;
    public String[] filterFields;
    public int start;
    public int count;
    public String key;
    public SortRequest(){ }

    public SortRequest(String taskName, String[] sortFields, String[] filterFields, int start, int count) {
        this.taskName = taskName;
        this.sortFields = sortFields;
        this.filterFields = filterFields;
        this.start = start;
        this.count = count;
        this.key = ClientSort.sortKey(taskName, sortFields,filterFields);
    }

    public static String getFilePath(String path) {
        String directPath = getOneDirectPath(path, new File(path).listFiles());
        if(directPath == null){
            return path;
        }else{
            return directPath;
        }
    }

    public  static String getOneDirectPath(String parentPath, File[] tempList) {
        if (tempList != null || tempList.length > 0) {
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isDirectory()) {
                    String currentPath = tempList[i].getName();
                    String nextPath = getOneDirectPath(currentPath, tempList[i].listFiles());
                    if (nextPath == null) {
                        return parentPath + File.separator + currentPath;
                    } else {
                        return parentPath + File.separator + currentPath + File.separator + nextPath;
                    }
                }
            }
        }
        return null;
    }
  public static void main(String arg [] ){
      System.out.println(getFilePath("d:\\"));
  }
}
