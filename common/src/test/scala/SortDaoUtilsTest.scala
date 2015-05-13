import com.tudou.core.zeus.common.dao.SortDaoUtils
import org.junit.{Before, Test}

/**
 * Created by wanganqing on 2015/3/10.
 */
class SortDaoUtilsTest {
  @Before def initDb(): Unit = {
    SortDaoUtils.initMysqlDataSource( )
  }

  @Test def testPrintln(): Unit = {
    println("Test Mysql connect : " + SortDaoUtils.testConnect())
  }

  @Test def testInsertTask(): Unit = {
    println("Test Mysql insert Task  : " + SortDaoUtils.insertSortTask( null))
  }
  @Test def testSortTask(): Unit ={
    val list = SortDaoUtils.getAllSortTask()
    println(list)
  }
}
