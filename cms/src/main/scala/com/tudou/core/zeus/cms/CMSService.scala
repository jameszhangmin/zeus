package com.tudou.core.zeus.cms

/**
 * Created by wanganqing on 2015/3/13.
 */
object CMSService {
  def proccess(response: SortResponse): Unit = {
    println("add memcache " + response.request.key)
    ClientSort.cacheSortResponse(response);
  }

}
