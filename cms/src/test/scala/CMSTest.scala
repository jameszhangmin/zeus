
import com.tudou.core.zeus.cms._
import org.junit.{Before, Test}

/**
 * Created by wanganqing on 2015/3/12.
 */
class CMSTest {
  @Before def setup(): Unit = {
    System.setProperty(EnvUtill.configFilePath, "D:\\Projects\\Workspace-spark\\zeus\\conf.properties")
  }

  /**
   * 启动
   */
  @Test def testMain(): Unit = {
    Bootstrap.main(null)
  }



}
