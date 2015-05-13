package com.tudou.core.zeus.common.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.analysis.UnresolvedAttribute
import org.apache.spark.sql.catalyst.expressions._
import org.apache.spark.{sql, SparkContext, SparkConf}
import com.tudou.core.zeus.cms.EnvUtill._

/**
 * Created by wanganqing on 2015/3/10.
 */
object SparkUtils {

  var sparkContext: SparkContext = null

  var sqlContext: SQLContext = null

  def initSpark(): Unit = {
    if (sparkContext != null) {
      throw new RuntimeException("not reinit !!")
    }
    //初始化spark
    val sparkConf = new SparkConf().setAppName(env("spark.appName"))
    //
    sparkConf.set("spark.hadoop.validateOutputSpecs", "false")
    if (env("spark.isLocal") == "true") {
//      System.setProperty("hadoop.home.dir", "d:\\opt\\")
      sparkConf.setMaster("local[*]") //是否在本地 driver上执行,一般本地调试用
    }
    sparkContext = new SparkContext(sparkConf)
    sqlContext = new SQLContext(sparkContext)
  }

  def sc: SparkContext = {
    if (sparkContext == null) {
      throw new RuntimeException("please init spark !!")
    }
    sparkContext
  }

  def sqlc: SQLContext = {
    if (sqlContext == null) {
      throw new RuntimeException("please init spark sql !!")
    }
    sqlContext
  }

  def createSchemaRDDByJsonFile(path: String): SchemaRDD = {
    try {
      return sqlc.jsonFile(path)
    } catch {
      case e: Exception => {
        println(e)
      }
    }
    return null
  }

//  def createSchemaRDDByParquetFile(path: String): SchemaRDD = {
//
//    try {
//      return sqlc.createParquetFile(path)
//    } catch {
//      case e: Exception => {
//        println(e)
//      }
//    }
//    return null
//  }

  def createSchemaRDDByJson(jsons: Array[String]): SchemaRDD = {
    sqlc.jsonRDD(sc.makeRDD(jsons))
  }

  def createSortJobSchemaRDD(fullDataRDD: SchemaRDD, updateDataRDD: SchemaRDD)(sortFileds: String)(filterFields: String): SchemaRDD = {
    val sqlContext = sqlc
    import sqlContext._
    //构造排序list
    val sortExprs: Array[SortOrder] = for (sortField <- sortFileds.split("\\,")) yield {
      val fieldAndOrder = sortField.split("\\.")
      val (field: String, order: String) = if (fieldAndOrder.length > 1) {
        (fieldAndOrder(0), fieldAndOrder(1))
      } else if (fieldAndOrder.length > 0) {
        (fieldAndOrder(0), null)
      }

      if ("asc" == order) {
        Symbol(field).asc
      } else {
        Symbol(field).desc
      }
    }

    val whereExprs:Expression = if (!filterFields.isEmpty) {
      val whereExprsArr: Array[Expression] = for (whereField <- filterFields.split("\\,")) yield {
          val fieldAndValue = whereField.split("=")
          val (field: String, value: String) = if (fieldAndValue.length > 1) {
            (fieldAndValue(0), fieldAndValue(1))
          } else if (fieldAndValue.length > 0) {
            (fieldAndValue(0), null)
          }
          EqualTo(Symbol(field), value)
        }

        whereExprsArr.foldLeft(And(1,1))((e1, e2) => {
        And(e1, e2)
      })
    }else {
      EqualTo(1,1)
    }

    //创建rdd
    if (updateDataRDD != null) {
      fullDataRDD.unionAll(updateDataRDD).where(whereExprs).orderBy(sortExprs: _*)
    } else {
      fullDataRDD.where(whereExprs).orderBy(sortExprs: _*)
    }
  }

  def take(rdd: SchemaRDD, fields: Array[String], count: Int): Array[Row] = {
    val sqlContext = sqlc

    val expr: Array[Expression] = for (field <- fields) yield {
      UnresolvedAttribute(field)
    }

//    val condition: Seq[Symbol] = for (field <- fields) yield {
//      Symbol(field)
//    }
    rdd.select(expr: _*).take(count)
  }

  def mergeNewRDD(oldRDD: SchemaRDD, newRDD: SchemaRDD, byIdFields: String): SchemaRDD = {
    if (oldRDD == null){
      return  newRDD
    }
    //初始化ID
    val sqlContext = sqlc
    import sqlContext._
    val idFields = byIdFields.split(",")
    val idSymbols = idFields.map(id => {
      Symbol(id)
    })
    val insetExps = idSymbols.map(idSymbol => {
      //新的data ids 记录
      val ids = newRDD.select(idSymbol).collect().map(r => r.getAs[Any](0)).toSet
      InSet(idSymbol, ids)
    })
    //构造需要更新的where表达式
    val needUpdateIdsExp = if (insetExps.length == 1) {
      insetExps(0)
    } else {
      // >１
      insetExps.foldLeft(And(idSymbols(0), idSymbols(0)))((e1, e2) => {
        And(e1, e2)
      })
    }
    // 找出需要更新的老记录RDD
    val needUpadateOldRDD = oldRDD.where(needUpdateIdsExp)
    //老RDD 减去 需要更新的老记录RDD
    val remaindOldRDD = oldRDD.subtract(needUpadateOldRDD)
    // 合并新的RDD
    val updateNewRDD = remaindOldRDD.unionAll(newRDD)
    updateNewRDD
  }
}


