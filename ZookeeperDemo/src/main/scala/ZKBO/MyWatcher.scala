package ZKBO

import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}

class MyWatcher(zk: ZooKeeper) extends Watcher{
  override def process(event: WatchedEvent): Unit = {
    println("节点的数据发生了变化")
    println("事件发生PATH: " + event.getPath)
    println("事件的TYPE: " + event.getType)
    // 设置递归实例化, 一直监听
    zk.getData("/girls", new MyWatcher(zk), null)
  }
}
