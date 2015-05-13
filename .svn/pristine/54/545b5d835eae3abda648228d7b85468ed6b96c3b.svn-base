package com.tudou.core.zeus.cms

import com.tudou.core.zeus.cms.EnvUtill._

/**
 * Created by wanganqing on 2015/3/11.
 */
object ClientSort {


  lazy val countByPage: Int = envInt("client.key.countByPage")

//  def sortKey(name: String, sortFileds: String, filterFields: String): String = {
//    return name + "###" + sortFileds + "###" + filterFields;
//  }

  /**
   * 根据排序字段和查询字段的字母序排列，以"_"连接
   * */
  def sortKey(taskName: String, sortFieldList: Array[String], filterFieldList: Array[String]): String = {
    val my_key = taskName + "###" + sortFieldList.toList.sorted.mkString("_") + "###" +filterFieldList.toList.sorted.mkString("_")
    println(">>>>>>>>>>>>>>>>my key:" + my_key)
    return my_key;
  }

  def registerTask(register: TaskRegister): Unit = KafkaUtils.send(register)


  //请求排序,先缓存后spark
  def sortRequest(request: SortRequest): Vector[String] = {
    val value = MemCacheUtils.get(getKeyOfPage(request.key));
    if (value == null) {
      val sparkDoing = MemCacheUtils.get(getKeyOfSpark(request.key)) //spark是否正在处理
      if (sparkDoing == null) {
        sortRequestBySpark(request)
        MemCacheUtils.put(getKeyOfSpark(request.key), "sparkdoing")
      }
      null
    } else {
      sortRequestByMemcache(request)
    }
  }

  // 获取本次查询的总大小  从另一个key中获取
  def sortRequestTotal(request: SortRequest): Int = {
    val value = MemCacheUtils.get(getKeyOfPage(request.key));
    if (value == null) {
      val sparkDoing = MemCacheUtils.get(getKeyOfSpark(request.key)) //spark是否正在处理
      if (sparkDoing == null) {
        sortRequestBySpark(request)
        MemCacheUtils.put(getKeyOfSpark(request.key), "sparkdoing")
      }
      0
    } else {
      MemCacheUtils.get(getKeyOfTotalk(request.key)).asInstanceOf[String].toInt
    }
  }

  def sortRequestBySpark(request: SortRequest): Vector[String] = {
    KafkaUtils.send(request)
    null
  }
  def dataRequest(request: DataRequest): Unit ={
    KafkaUtils.send(request)
  }
  def sortRequestByMemcache(request: SortRequest): Vector[String] = {

    val pageStart = request.start / countByPage * countByPage
    val pageEnd = (request.start + request.count) / countByPage * countByPage

    //获取所涉及的分页数据
    val allPageData = for (page <- pageStart.to(pageEnd, countByPage)) yield {
      val pageKey = keyGen(request.key, page)
      (page, MemCacheUtils.get(pageKey))
    }

    import scala.collection.mutable.Map
    val allPageDataMap = Map[Int, Array[String]]()
    for (value <- allPageData) {
      allPageDataMap += (value._1 -> value._2.asInstanceOf[Array[String]])
    }
    //取start + count 记录
    val result = for (page <- pageStart.to(pageEnd, countByPage)) yield {
      val data = allPageDataMap(page)
      //如果数据总量分布区间不在请求范围内，返回null
      if (data == null) return null
      if (page < request.start) {
        data.drop(request.start - page)
      } else if (page >= request.start && page < pageEnd) {
        data
      } else if (page >= pageEnd) {
        data.take(request.start + request.count - page)
      }
    }
    //结果flat 处理
    //result.asInstanceOf[Vector[Array[String]]].flatMap((v: Array[String]) => v.map(a => a))
    result.asInstanceOf[Vector[Array[String]]].flatten
  }

  //分页放入缓存
  def cacheSortResponse(response: SortResponse): Unit = {
    val pageKey = getKeyOfPage(response.request.key)
    //cleanMemcache(pageKey) //clean old Data

    //开始插入新的数据
    val pages = getByPage(response)
    val pageValueKeys = for ((key, data) <- pages) yield key

    //分页key
    MemCacheUtils.put(pageKey, pageValueKeys)
    //分页放入
    pages.foreach(page => {
      MemCacheUtils.put(page._1, page._2)
    })
    //总数
    MemCacheUtils.put(getKeyOfTotalk(response.request.key), response.data.length)
    //清空状态位
    MemCacheUtils.clean(getKeyOfSpark(response.request.key))
  }

  private def cleanMemcache(pageKey: String): Unit = {
    MemCacheUtils.get(pageKey).asInstanceOf[Array[String]].foreach(key => {
      MemCacheUtils.clean(key)
    })
    MemCacheUtils.clean(pageKey)
  }

  private def keyGen(key: String, num: Int): String = {
    key + "____" + num
  }

  private def getKeyOfPage(key: String): String = {
    keyGen(key, -1)
  }

  private def getKeyOfSpark(key: String): String = {
    keyGen(key, -2)
  }

  private def getKeyOfTotalk(key: String): String = {
    keyGen(key, -3)
  }

  private def getKeyByPage(request: SortRequest): Seq[String] = {
    for (page <- 0.until(request.count, countByPage)) yield {
      keyGen(request.key, page)
    }
  }

  private def getByPage(response: SortResponse): Seq[(String, Array[String])] = {
    val request: SortRequest = response.request
    for (page <- 0.until(response.data.length, countByPage)) yield {
      val start = page
      val end = if (page + countByPage <= response.data.length) {
        page + countByPage
      } else {
        response.data.length
      }
      (keyGen(request.key, start), response.data.slice(start, end))
    }
  }

}
