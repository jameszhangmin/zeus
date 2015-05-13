package com.tudou.core.zeus.cms

import java.io.FileInputStream
import java.util.Properties

/**
 * Created by wanganqing on 2015/3/12.
 */
object EnvUtill {
  val configFilePath = "conf.properties"
  var properties: Properties = null

  def init(): Unit = {
    val path = if (System.getProperty(configFilePath) != null) {
      System.getProperty(configFilePath)
    } else if (System.getenv(configFilePath) != null){
      System.getenv(configFilePath)
    }else{
      configFilePath
    }
    if (path == null) {
      throw new RuntimeException("please set env " + configFilePath)
    }
    properties = new Properties()
    properties.load(new FileInputStream(path))
  }

  def env(key: String): String = {
    if (properties == null) {
      throw new RuntimeException("please init 　！！！！　 ")
    }
    properties.get(key).toString
  }
  def envInt(key: String): Int = {
    env(key).toInt
  }
  def envLong(key: String): Long = {
    env(key).toLong
  }
  def envObject[T](key: String): T = {
    Class.forName(env(key)).newInstance().asInstanceOf[T]
  }
  def envBoolean (key: String): Boolean = {
    env(key) == "true"
  }
}

