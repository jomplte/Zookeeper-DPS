package ZKSA

import java.util
import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object Consumer {

  // 1. 创建 zookeeper连接对象
  var zk: ZooKeeper = _
  // 声明一个集合, 保存在线的服务器
  var onlineServers = new ArrayBuffer[String]()
  val random = new Random()

  def main(args: Array[String]): Unit = {
    zk = new ZooKeeper("h2:2181,h3:2181,h4:2181", 2000, new Watcher {
      // 2. 监听注册(Provider服务器的数量(子节点)变化)
      override def process(event: WatchedEvent): Unit = {
        // (重新)查询, 得到在线服务器的集合
        saveOnlineServers()
      }
    })

    // 3. 随机挑选服务器, 处理业务
    while (true) {
      if (onlineServers.isEmpty) {
        println("没有在线的服务器")
        Thread.sleep(2000)
      } else {
        val str: String = onlineServers(random.nextInt(onlineServers.size))
        println("本消费者连接了服务器: " + str)
        Thread.sleep(2000)
      }
    }
    zk.close()

  }

  def saveOnlineServers(): Unit = {
    // 为线程安全, 声明一个集合, 临时保存在线的服务器
    val tmpOneLineServers = new ArrayBuffer[String]()
    // 查询, 并将在线的服务器保存到临时集合
    val string: util.List[String] = zk.getChildren("/server", true, null)
    val str: util.Iterator[String] = string.iterator()
    while (str.hasNext) {
      val st: String = str.next()
      val bytes: Array[Byte] = zk.getData("/server/" + st, false, null)
      val result: String = new String(bytes)
      tmpOneLineServers += result
      println("本消费者查询了一次服务器列表: " + string)
    }
    // 将保存到临时集合中的服务器转到 onelineServers
    onlineServers = tmpOneLineServers
  }
}