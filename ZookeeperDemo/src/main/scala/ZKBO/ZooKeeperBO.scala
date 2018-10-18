package ZKBO

import java.util

import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper.{CreateMode, ZooKeeper}
import org.junit.Test

class ZooKeeperBO {

  @Test
  def testCreate(): Unit = {
    // 获取一个 zookeeper连接对象
    val zk: ZooKeeper = new ZooKeeper("h2:2181,h3:2181,h4:2181", 2000, null)
    // 创建数据节点(znode)
    zk.create("/mygirls", "liuyifei".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
    zk.close()
  }

  @Test
  def testUpdate(): Unit = {
    val zk: ZooKeeper = new ZooKeeper("h2:2181,h3:2181,h4:2181", 2000, null)
    // 修改节点的数据, version = -1: 表示修改集群中这个节点的所有版本为新数据
    zk.setData("/girls", "libingbing".getBytes(), -1)
    zk.close()
  }

  @Test
  def testGet(): Unit = {
    val zk: ZooKeeper = new ZooKeeper("h2:2181,h3:2181,h4:2181", 2000, null)
    // 查询节点的数据, stat = null: 也表示查询最新版本
    val bytes: Array[Byte] = zk.getData("/girls", false, null)
    val str: String = new String(bytes)
    println(str)
    zk.close()
  }

  @Test
  def testGetChildren(): Unit = {
    val zk: ZooKeeper = new ZooKeeper("h2:2181,h3:2181,h4:2181", 2000, null)
    // 查询节点的子结点
    val string: util.List[String] = zk.getChildren("/", false)
    println(string)
    // 查询该节点子节点的数据, 因为 getChildren()得到的是 java的 List, 故只能使用迭代器取结果
    val str: util.Iterator[String] = string.iterator()
    while (str.hasNext) {
      val st: String = str.next()
      val bytes: Array[Byte] = zk.getData("/" + st, false, null)
      val result: String = new String(bytes)
      println(result)
    }
    zk.close()
  }

  @Test
  def testDelete(): Unit = {
    val zk: ZooKeeper = new ZooKeeper("h2:2181,h3:2181,h4:2181", 2000, null)
    // 删除没有子节点的节点
    // zk.delete("/mygirls", -1)

    // 递归删除节点
    RMR.deleteZNode(zk, "/mygirls")
    zk.close()
  }

  object RMR {
    def deleteZNode(zk: ZooKeeper, path: String): Unit = {
      val str: util.List[String] = zk.getChildren(path, false)
      val st: util.Iterator[String] = str.iterator()
      while (st.hasNext) {
        val result: String = st.next()
        deleteZNode(zk, path + "/" + result)
      }
      zk.delete(path, -1)
    }
  }
}
