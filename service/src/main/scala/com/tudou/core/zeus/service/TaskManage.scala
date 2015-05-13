package com.tudou.core.zeus.service

import java.io.File

import com.tudou.core.zeus.cms._
import com.tudou.core.zeus.common.hdfs.HdfsUtils
import com.tudou.core.zeus.common.dao.SortDao.{SortTaskRow, SortJobRow}
import com.tudou.core.zeus.common.dao.SortDaoUtils
import com.tudou.core.zeus.common.spark.SparkUtils
import org.apache.spark.sql._
import scala.collection.mutable.Map

/**
 * Created by wanganqing on 2015/3/11.
 */
class TaskManage(val task: SortTaskRow) {
  var jobRDDMap: Map[String, SchemaRDD] = Map[String, SchemaRDD]()
  var fullDataRDD: SchemaRDD = null
  var updateDataRDD: SchemaRDD = null

  def startSortTask(): Unit = {
    System.out.println("startSortTask")

    //读取全量和增量数据
    fullDataRDD = SparkUtils.createSchemaRDDByJsonFile(task.fullDataPath.getOrElse(""))
    //    updateDataRDD = SparkUtils.createSchemaRDDByParquetFile(task.updateDataPath.getOrElse(""))
    updateDataRDD = SparkUtils.createSchemaRDDByJsonFile(task.updateDataPath.getOrElse(""))
    //批量启动排序作业
    startAllSortJobByTask()
  }

  def startAllSortJobByTask() = {
    System.out.println("startAllSortJobByTask")
    val sortJobs: List[SortJobRow] = SortDaoUtils.getSortJobByTaskId(task.id)
    sortJobs.foreach(startSortJob)
  }

  def startSortJob(job: SortJobRow) = {
    System.out.println("startSortJob"+job)
    val key:String = job.key
    val rdd = SparkUtils.createSortJobSchemaRDD(fullDataRDD, updateDataRDD)(job.sortFields)(job.filterFields).limit(task.defaultCacheMax.getOrElse(5000))
    jobRDDMap += (key -> rdd)
  }

  /**
   * 请求执行所有的排序作业
   */
  def runRequestAllSortJobByTask() = {
    val sortJobs: List[SortJobRow] = SortDaoUtils.getSortJobByTaskId(task.id)
    sortJobs.map(job => {
      new SortRequest(task.name, job.sortFields.split(","), job.filterFields.split(","), 0, task.defaultCacheMax.get)
    }).foreach(requestSortJob)
  }

  def requestSortJob(request: SortRequest) = {
    System.out.println("request"+request)
    if (!jobRDDMap.contains(request.key)) {
      val jobId = SortDaoUtils.insertSortJob(task.id, request)
      val sortJob = SortDaoUtils.getSortJob(jobId)
      startSortJob(sortJob)
    }
    val key = request.key
    val result:Array[Row] = SparkUtils.take(jobRDDMap(key), task.filedIdNames.split(","), task.defaultCacheMax.getOrElse(5000))
    System.out.println("spark take count "+result.length)
    val result2 = result.map(row => row.mkString("_"))
    val sortResponse = new SortResponse(request, result2) //[id_id2_id_3,id_id2_id3]
    //发送到kafka消息
    KafkaUtils.send(sortResponse)
  }

  def updateData(request: DataRequest) = {
    request.isFullUpdate.booleanValue() match {
      case true => {
        updateData_Full(request)
      }
      case false => {
        updateData_update(request)
      }
    }
  }


  /**
   * 全量更新
   * @param request
   */
  def updateData_Full(request: DataRequest) = {
    val fullDataPath: String = request.data(0)
    //保存task.fullDataPath=newPath , task.updateDataPath = null
    SortDaoUtils.updateSortTaskFullDataPath(task.id, fullDataPath)
    SortDaoUtils.updateSortTaskUpdateDataPath(task.id, null)
    //保存一份数据到rdd履历中
    SortDaoUtils.insertRDDHistory(task.id, fullDataPath,DataRequest.RDD_HIST_SOURCE_INPUT,DataRequest.RDD_HIST_UPDATE_FULL)
    //替换旧的fullRDD
    fullDataRDD = SparkUtils.createSchemaRDDByJsonFile(fullDataPath)
    updateDataRDD = null
    //启动所有的排序作业
    startAllSortJobByTask()
    //请求执行所有的排序作业
    runRequestAllSortJobByTask()
  }

  /**
   * 增量更新
   * @param request
   */
  def updateData_update(request: DataRequest) = {
    //初始化需要更新的NewRDD
//    val neadUpdateDataNewRDD = SparkUtils.createSchemaRDDByJson(request.data)
    val neadUpdateDataNewRDD = SparkUtils.createSchemaRDDByJsonFile(request.data(0))
    SortDaoUtils.insertRDDHistory(task.id, request.data(0), DataRequest.RDD_HIST_SOURCE_INPUT, DataRequest.RDD_HIST_UPDATE_DELTA)
    //数据合并
    val newUpdateDataRDD = SparkUtils.mergeNewRDD(updateDataRDD, neadUpdateDataNewRDD, task.filedIdNames)
    //保存
    val updateDataPath = HdfsUtils.getFullDataHDFSPath(HdfsUtils.generateFullDataPath(false))
    newUpdateDataRDD.toJSON.saveAsTextFile(updateDataPath)
    //    newUpdateDataRDD.saveAsParquetFile(updateDataPath)
    //更新 task.updateDataPath = newPath
    SortDaoUtils.updateSortTaskUpdateDataPath(task.id, updateDataPath)
    SortDaoUtils.insertRDDHistory(task.id, updateDataPath, DataRequest.RDD_HIST_SOURCE_OUTPUT, DataRequest.RDD_HIST_UPDATE_DELTA)
    //替换旧的updateRDD
    //不能从内存对象中获取原update的路径，应该从最新导出的HDFS路径中获取
//    updateDataRDD = SparkUtils.createSchemaRDDByJsonFile(task.updateDataPath.get)
    updateDataRDD = SparkUtils.createSchemaRDDByJsonFile(updateDataPath)
    //    updateDataRDD = SparkUtils.createSchemaRDDByParquetFile(task.updateDataPath.get)


    //重新merge fullDataRDD 且 清空updateDataRDD
    fullDataRDD = fullDataRDD.unionAll(updateDataRDD)
    //写入到HDFS
    val fileName: String = HdfsUtils.generateFullDataPath(true)
    val fullPath: String = HdfsUtils.getFullDataHDFSPath(fileName)
    fullDataRDD.toJSON.saveAsTextFile(fullPath)
    //更新全量路径
    SortDaoUtils.updateSortTaskFullDataPath(task.id, HdfsUtils.getFullDataHDFSPath(fileName))
    SortDaoUtils.updateSortTaskUpdateDataPath(task.id, null)
    updateDataRDD = null
    //记录rdd履历
    SortDaoUtils.insertRDDHistory(task.id, HdfsUtils.getFullDataHDFSPath(fileName), DataRequest.RDD_HIST_SOURCE_OUTPUT, DataRequest.RDD_HIST_UPDATE_FULL)

    //启动所有的排序作业
    startAllSortJobByTask()
    //请求执行所有的排序作业
    runRequestAllSortJobByTask()
  }

}
