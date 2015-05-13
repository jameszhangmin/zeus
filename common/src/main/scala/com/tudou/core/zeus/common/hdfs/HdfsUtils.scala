package com.tudou.core.zeus.common.hdfs

import java.io.{OutputStream, InputStream, BufferedInputStream, FileInputStream}
import java.net.URI
import java.text.SimpleDateFormat
import java.util.{UUID, Date}

import com.tudou.core.zeus.cms.EnvUtill
import com.tudou.core.zeus.common.dao.SortDao
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.IOUtils
import org.apache.hadoop.util.Progressable


/**
 * Created by wanganqing on 2015/3/12.
 */
object HdfsUtils extends App{
  //for local test
  uploadToHdfs("d://update_2.txt", "hdfs://a01.test.spark.sh.jj.tudou.com:9100/input/update_2.json")

  def init() {
    System.setProperty("HADOOP_USER_NAME", "app_admin")

  }


  /**
   * 写入HDFS文件
   * @param filePath
   * @param data
   * */
  def write(filePath: String, data: Array[Byte]): Unit = {
    val hdfs_uri:String = EnvUtill.env("hdfs.uri")
    val dist_path:String = EnvUtill.env("hdfs.file_path") + "/" + filePath
    val path = new Path(dist_path)
    val conf = new Configuration()
    conf.set("fs.defaultFS", hdfs_uri)
    val fs = FileSystem.get(conf)
    val os = fs.create(path)
    os.write(data)
    fs.close()
  }

  /**
   * 本地文件上传至HDFS目录
   * @param localSrc
   * @param dist
   * */
  def uploadToHdfs(localSrc: String, dist: String): Unit = {
    val in = new BufferedInputStream(new FileInputStream(localSrc))
    val conf = new Configuration()

    val fs = FileSystem.get(URI.create(dist), conf)
    val out = fs.create(new Path(dist), new Progressable() {
      def progress() {
        println("...")
      }
    })
    IOUtils.copyBytes(in, out, 4096, true)
  }

  def generateFullDataPath(isFull: Boolean): String={
    val is_full_str = if (isFull) "full" else "update"
    val token: String = UUID.randomUUID().toString
    val str:String = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())
    "data_" + str + "_" + is_full_str + "_" + token + ".json";
  }

  def getFullDataHDFSPath(fileName: String): String = {
    EnvUtill.env("hdfs.uri") + EnvUtill.env("hdfs.file_path") + "/" + fileName
  }
  /**
   * 根据SortDao.SortTaskRow创建路径,原始路径不做删除
   * @param row
   * @return
   */
  def createTaskUpdateDataPath(row: SortDao.SortTaskRow): String = {
    "hdfs://a01.test.spark.sh.jj.tudou.com:9100/input/mydata_102.json"
  }

}
