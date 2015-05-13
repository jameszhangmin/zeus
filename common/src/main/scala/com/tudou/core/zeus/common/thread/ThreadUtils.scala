package com.tudou.core.zeus.common.thread

import java.util.concurrent.{ExecutorService, Executors}
import com.tudou.core.zeus.cms.EnvUtill._

/**
 * Created by wanganqing on 2015/3/16.
 */
object ThreadUtils {
  var executor: ExecutorService = null

  def init(): Unit = {
    if (executor != null) {
      throw new RuntimeException("not reinit !!")
    }
    executor = Executors.newFixedThreadPool(envInt("thread.number"));
  }

  def submit(runTask: () =>Unit): Unit = {
    if (executor == null) {
      throw new RuntimeException("not init !!")
    }
    executor.submit(new Runnable {
      override def run(): Unit = {
        runTask()
      }
    })
  }
}
