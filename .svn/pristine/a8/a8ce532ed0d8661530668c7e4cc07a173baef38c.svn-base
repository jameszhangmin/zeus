package com.tudou.core.zeus.cms

import java.util.Date

import com.tudou.core.zeus.cms.EnvUtill._
import com.tudou.utils.client.XmemcachedClientAdapterImpl
import net.rubyeye.xmemcached.buffer.BufferAllocator
import net.rubyeye.xmemcached.transcoders.Transcoder
import net.rubyeye.xmemcached.{CommandFactory, MemcachedSessionLocator}

/**
 * Created by wanganqing on 2015/3/16.
 */
object MemCacheUtils {


  var hdMemClient: XmemcachedClientAdapterImpl = null
  var memCacheFailTime: Int = 10

  //10分钟失效
  def init(): Unit = {
    if (hdMemClient != null) {
      throw new RuntimeException("not reinit !!")
    }
    val transcoder = envObject[Transcoder[_]]("xmemcached.hdservice.transcoder")
    transcoder.setPrimitiveAsString(envBoolean("xmemcached.hdservice.primitiveAsString"))

    val hdXmemcachedBuilder = new net.rubyeye.xmemcached.XMemcachedClientBuilder(env("xmemcached.hdservice.addr"))
    hdXmemcachedBuilder.setConnectionPoolSize(envInt("xmemcached.hdservice.connectionPoolSize"))
    hdXmemcachedBuilder.setConnectTimeout(envLong("xmemcached.hdservice.connectTimeout"))
    hdXmemcachedBuilder.setOpTimeout(envLong("xmemcached.hdservice.opTimeout"))
    hdXmemcachedBuilder.setCommandFactory(envObject[CommandFactory]("xmemcached.hdservice.commandFactory")); //
    hdXmemcachedBuilder.setSessionLocator(envObject[MemcachedSessionLocator]("xmemcached.hdservice.sessionLocator")); //
    hdXmemcachedBuilder.setTranscoder(transcoder)
    hdXmemcachedBuilder.setBufferAllocator(envObject[BufferAllocator]("xmemcached.hdservice.bufferAllocator"))
    hdXmemcachedBuilder.setFailureMode(envBoolean("xmemcached.hdservice.failureMode"))
    val memcachedClient = hdXmemcachedBuilder.build()
    hdMemClient = new XmemcachedClientAdapterImpl(memcachedClient)
    memCacheFailTime = envInt("xmemcached.key.memCacheFailTime.minute")
  }

  def get(key: String): Any = {
    if (hdMemClient == null) {
      throw new RuntimeException("not init !!")
    }
    hdMemClient.get(key)
  }

  def put(key: String, value: Any) = {
    if (hdMemClient == null) {
      throw new RuntimeException("not init !!")
    }
    hdMemClient.set(key, value, new Date(memCacheFailTime * 60 * 1000))
  }

  def clean(key: String) = {
    if (hdMemClient == null) {
      throw new RuntimeException("not init !!")
    }
    hdMemClient.delete(key)
  }
}
