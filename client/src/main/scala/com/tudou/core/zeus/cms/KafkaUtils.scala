package com.tudou.core.zeus.cms

import java.util.Properties
import java.util.concurrent.ConcurrentHashMap

import com.tudou.core.zeus.cms.EnvUtill._
//import com.tudou.utils.json.JsonTool
import kafka.consumer._
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import kafka.serializer.StringDecoder
//import org.codehaus.jackson.map.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper


/**
 * Created by wanganqing on 2015/3/11.
 */

object KafkaUtils {


  val Topic_DataRequest = env("kafka.Topic.DataRequest")
  val Topic_SortRequest = env("kafka.Topic.SortRequest")
  val Topic_SortResponse = env("kafka.Topic.SortResponse")
  val Topic_TaskRegister = env("kafka.Topic.TaskRegister")

  val producerMap = new ConcurrentHashMap[String, Object]()

  def initProducer[T](): Producer[String, T] = {
    val props = new Properties()
    //http://my.oschina.net/cloudcoder/blog/299215
    props.put("metadata.broker.list", env("kafka.product.metadata.broker.list"))
    props.put("key.serializer.class", env("kafka.product.key.serializer.class"))
    props.put("serializer.class", env("kafka.product.serializer.class"))
    props.put("request.required.acks", env("kafka.product.request.required.acks"))
    val config = new ProducerConfig(props)
    val producer = new Producer[String, T](config)
    producer
  }

  def createConsumerStream[T](topic: String): Seq[KafkaStream[String, T]] = {
    val props = new Properties()
    props.put("zookeeper.connect", env("kafka.consumer.zk.list"));
    props.put("zookeeper.session.timeout.ms", env("kafka.consumer.zookeeper.session.timeout.ms"));
    props.put("zookeeper.sync.time.ms", env("kafka.consumer.zookeeper.sync.time.ms"));
    props.put("group.id", env("kafka.consumer.group.id"));
    props.put("auto.commit.interval.ms", env("kafka.consumer.auto.commit.interval.ms"));
    var config = new ConsumerConfig(props)
    val consumer = Consumer.create(config)
    consumer.createMessageStreamsByFilter(Whitelist(topic), 1, new StringDecoder(), new com.tudou.core.zeus.cms.StringDecoder[T]())
  }

  def send(message: Any) = {
    def send[T](topic: String, key: String, value: T) = {
      if (!producerMap.containsKey(topic)) {
        producerMap.put(topic, initProducer[DataRequest]())
      }
      val producer = producerMap.get(topic).asInstanceOf[Producer[String, T]]
      producer.send(KeyedMessage(topic, key, null, value))
    }

    message match {
        //new ObjectMapper().writeValueAsString(m)
      case m: DataRequest => send(Topic_DataRequest, m.taskName, new ObjectMapper().writeValueAsString(m))
      case m: SortRequest => send(Topic_SortRequest, m.taskName, new ObjectMapper().writeValueAsString(m))
      case m: SortResponse => send(Topic_SortResponse, m.request.taskName, new ObjectMapper().writeValueAsString(m))
      case m: TaskRegister => send(Topic_TaskRegister, m.taskName, new ObjectMapper().writeValueAsString(m))
      case m => throw new RuntimeException("not matche producer !!")
    }
  }


}

