package com.tudou.core.zeus.cms

import akka.actor.Actor

/**
 * Created by wanganqing on 2015/3/13.
 */
class DispatchActor() extends Actor {
  override def receive: Receive = {

    case sortResponse: SortResponse => {
      CMSService.proccess(sortResponse)
    }
    case data => {
      println("!!!!!! ignore Message  by DispatchActor: " + data)
    }
  }
}
