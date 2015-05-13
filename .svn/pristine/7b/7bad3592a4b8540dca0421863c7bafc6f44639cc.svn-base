package com.tudou.core.zeus.service

import akka.actor.{Props, ActorSystem, Actor}
import akka.routing.RoundRobinRouter
import com.tudou.core.zeus.cms._
import kafka.message.MessageAndMetadata

/**
 * Created by wanganqing on 2015/3/13.
 */
class DispatchActor() extends Actor {
  override def receive: Receive = {

    case dataRequest: DataRequest => {
      SortService.startUpdateData(dataRequest)
    }
    case sortRequest: SortRequest => {
      SortService.requestSortJob(sortRequest)
    }
    case taskRegister: TaskRegister => {
      SortService.registerSortTask(taskRegister)
    }
    case data => {
      println("!!!!!! ignore Message  by DispatchActor: " + data)
    }
  }
}
