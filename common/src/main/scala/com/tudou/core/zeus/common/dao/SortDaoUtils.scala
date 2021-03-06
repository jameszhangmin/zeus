package com.tudou.core.zeus.common.dao

import javax.sql.DataSource

import com.tudou.core.zeus.cms.{TaskRegister, SortRequest}
import org.apache.commons.dbcp.BasicDataSource
import org.apache.spark.sql.SchemaRDD

import scala.slick.driver.MySQLDriver
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.jdbc.GetResult
import com.tudou.core.zeus.common.dao.SortDao._


/**
 * Created by wanganqing on 2015/3/10.
 */
object SortDaoUtils {

  var dataSource: DataSource = null;

  def initMysqlDataSource(): Unit = {
    if (dataSource == null) {
      import com.tudou.core.zeus.cms.EnvUtill._
      dataSource = DBUtils.createMysqlDataSource(env("mysql.host"), env("mysql.port"), env("mysql.dbName"), env("mysql.userName"), env("mysql.password"))
    }
  }

  def db: MySQLDriver.backend.DatabaseDef = {
    if (dataSource == null) {
      throw new RuntimeException("please init mysql DataSource!!")
    }
    Database.forDataSource(dataSource)
  }

  def testConnect(): Boolean = {
    val sql = sql"select 1 ".as[Int]
    db.withSession[Boolean](implicit session => {
      sql.list.size > 0
    })
  }

  def insertSortTask(register: TaskRegister): Int = {
    db.withSession[Int](implicit session => {
      SortTask.map(task => (task.name,
        task.filedIdNames,
        task.fieldNames,
        task.fullDataPath,
        task.updateDataPath,
        task.defaultCacheMax,
        task.status
        ))
        .returning(SortTask.map(task => task.id))
        .insert(register.taskName,
          register.filedIDNames,
          Option(register.fieldNames),
          Option(register.fullDataPath),
          Option(register.updateDataPath),
          Option(register.defaultCacheMax),
          Option(register.status)
        )
    })
  }

  def updateSortTaskByTaskName(taskName: String, register: TaskRegister): SortTaskRow = {
    db.withSession[SortTaskRow](implicit session => {
      SortTask.map(task => (task.name,
        task.filedIdNames,
        task.fieldNames,
        task.fullDataPath,
        task.updateDataPath,
        task.defaultCacheMax,
        task.status
        )).filter(_._1 === taskName)
        .update(register.taskName,
          register.filedIDNames,
          Option(register.fieldNames),
          Option(register.fullDataPath),
          Option(register.updateDataPath),
          Option(register.defaultCacheMax),
          Option(register.status)
        )
      val list = SortTask.filter(_.name === taskName).list
      if (list.size > 0) list(0) else null
    })
  }

  def insertRDDHistory(taskId: Int, rdd_Path: String, is_output: Int, is_full: Int): Int = {
    db.withSession[Int](implicit session => {
      SortRddHistory.map(row_data =>
        (row_data.taskId, row_data.rddPath, row_data.isOutput, row_data.isFull))
        .returning(SortRddHistory.map(row_data => row_data.id))
        .insert(taskId, rdd_Path, is_output.toByte, is_full.toByte)
    })
  }


  def getSortTaskById(id: Int): SortTaskRow = {
    db.withSession[SortTaskRow](implicit session => {
      val list = SortTask.filter(_.id === id).filter(_.isDelete === false).list
      if (list.size > 0) list(0) else null
    })
  }

  def insertSortJob(taskId: Int, jobRequest: SortRequest): Int = {
    val sortFields: String = jobRequest.sortFields.toArray.mkString(",")
    val filterFields: String = jobRequest.filterFields.toArray.mkString(",")
    val key: String = jobRequest.key

    db.withSession[Int](implicit session => {
      SortJob.map(job =>
        (job.taskId, job.sortFields, job.filterFields, job.key))
        .returning(SortJob.map(job => job.id))
        .insert(taskId, sortFields, filterFields, key)
    })
  }


  def getAllSortTask(): List[SortTaskRow] = {
    db.withSession[List[SortTaskRow]](implicit session => {
      SortTask.filter(_.isDelete === false).list
    })
  }

  def getSortJobByTaskId(byTaskId: Int): List[SortJobRow] = {
    db.withSession[List[SortJobRow]](implicit session => {
      SortJob.filter(_.taskId === byTaskId).filter(_.isDelete === false).list
    })
  }

  def getSortJob(id: Int): SortJobRow = {
    db.withSession[SortJobRow](implicit session => {
      val list: List[SortDao.SortJob#TableElementType] = SortJob.filter(_.id === id).filter(_.isDelete === false).list(session)
      if (list.size > 0) list(0) else null
    })
  }

  def updateSortTaskFullDataPath(id: Int, fullDataPath: String): Int = {
    db.withSession[Int](implicit session => {
      SortTask.map(task => (task.id, task.fullDataPath)).filter(_._1 === id).update(id, Option(fullDataPath))
    })
  }

  def updateSortTaskUpdateDataPath(id: Int, updateDataPath: String): Int = {
    db.withSession[Int](implicit session => {
      SortTask.map(task => (task.id, task.updateDataPath)).filter(_._1 === id).update(id, Option(updateDataPath))
    })
  }


}
