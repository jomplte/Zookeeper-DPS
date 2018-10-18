package ZKBO

import java.util

import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}
import org.junit.Test

class ZooKeeperMonitor {

  /**
    * 监听节点的数据变化事件
    */
  var zk: ZooKeeper = _
  @Test
  def testWatchData(): Unit = {
    // 第一种: 获取一个 zookeeper连接对象时启动监听
    zk = new ZooKeeper("h2:2181,h3:2181,h4:2181", 2000, new Watcher {
      override def process(event: WatchedEvent): Unit = {
        println("监听已经启动:")
        println("事件发生的PATH: " + event.getPath)
        println("事件的TYPE: " + event.getType)
        // 设置一直监听
        val bytes: Array[Byte] = zk.getData("/girls", true, null)
        val str: String = new String(bytes)
        println("事件的data: " + str)
      }
    })

    // 等待发生变化
    Thread.sleep(Long.MaxValue)
    zk.close()
  }

  @Test
  def testWatchData1(): Unit = {
    val zk: ZooKeeper = new ZooKeeper("h2:2181,h3:2181,h4:2181", 2000, null)
    // 第二种, 获取节点数据时启动监听, 此时为监听一次
    val bytes: Array[Byte] = zk.getData("/girls", new Watcher {
      override def process(event: WatchedEvent): Unit = {
        println("节点的数据发生了变化")
        println("事件发生PATH: " + event.getPath)
        println("事件的TYPE: " + event.getType)
      }
    }, null)
    val str: String = new String(bytes)
    println(str)
    Thread.sleep(Long.MaxValue)
    zk.close()
  }

  @Test
  def testWatchData2(): Unit = {
    val zk: ZooKeeper = new ZooKeeper("h2:2181,h3:2181,h4:2181", 2000, null)
    // 获取节点数据时启动监听, 自定义 MyWatcher继承 Watcher, 设置为一直监听
    val bytes: Array[Byte] = zk.getData("/girls", new MyWatcher(zk), null)
    val str: String = new String(bytes)
    println(str)
    Thread.sleep(Long.MaxValue)
    zk.close()
  }

  /**
    * 监听节点的子节点变化事件
    */
  @Test
  def testWatchChildren(): Unit = {
    val zk: ZooKeeper = new ZooKeeper("h2:2181,h3:2181,h4:2181", 2000, null)
    val str: util.List[String] = zk.getChildren("/girls", new Watcher {
      override def process(event: WatchedEvent): Unit = {
        println("节点的子节点发生了变化")
        println("事件发生PATH: " + event.getPath)
        println("事件的TYPE: " + event.getType)
      }
    })
    println(str)
    Thread.sleep(Long.MaxValue)
    zk.close()
  }
}
