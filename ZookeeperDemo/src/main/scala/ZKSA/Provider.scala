package ZKSA

import java.net.{InetAddress, ServerSocket, Socket}

import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper.{CreateMode, ZooKeeper}


object Provider {
  def main(args: Array[String]): Unit = {

    // 1. 获取 zookeeper连接对象
    val zk: ZooKeeper = new ZooKeeper("h2:2181,h3:2181,h4:2181", 2000, null)

    // 2. 获取当前主机名, 注册
    val hostname = InetAddress.getLocalHost.getHostName
    if (zk.exists("/server", false) == null) {
      zk.create("/server", null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
    }
    val path: String = zk.create("/server/server", (hostname + ":" + 8080).getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL)
    println(hostname + "成功注册了一个ZK节点" + path)

    // 3. 处理业务
    val server = new ServerSocket(8080)
    while (true) {
      val socket: Socket = server.accept()
      /**
        * 业务逻辑 ...
        */
    }
    zk.close()
  }
}
