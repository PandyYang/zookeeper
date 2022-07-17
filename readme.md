# zookeeper简明教程
此教程基于三个节点的集群进行学习及操作。分别为master, node1, node2节点。
我已经将三个集群的镜像打包，开箱即用：
链接：https://pan.baidu.com/s/1MQAI4JfqMRIarDkUwa648Q?pwd=5j0x 
提取码：5j0x 

### 启动
```shell
./zkCli.sh -server master:2181
```
### help查看zk所有相关命令
```shell
addauth scheme auth
close 
config [-c] [-w] [-s]
connect host:port
create [-s] [-e] [-c] [-t ttl] path [data] [acl]
delete [-v version] path
deleteall path
delquota [-n|-b] path
get [-s] [-w] path
          获得节点的值 [可监听]
          -w 监听节点内容变化
          -s 附加次级信息

getAcl [-s] path
history 
listquota path
ls [-s] [-w] [-R] path
          使用 ls 命令来查看当前 znode 的子节点 [可监听]
          -w 监听子节点变化
          -s 附加次级信息

ls2 path [watch]
printwatches on|off
quit 
reconfig [-s] [-v version] [[-file path] | [-members serverID=host:port1:port2;port3[,...]*]] | [-add serverId=host:port1:port2;port3[,...]]* [-remove serverId[,...]*]
redo cmdno
removewatches path [-c|-d|-a] [-l]
rmr path
set [-s] [-v version] path data
          设置节点的具体值
setAcl [-s] [-v version] [-R] path acl
setquota -n|-b val path
stat [-w] path
          查看节点状态
sync path
```
# 节点相关操作
### 查看当前znode中所包含的内容
```shell
ls /
```
```shell
[dubbo, kafka, locks, pandy, pandy2, sanguo, servers, zookeeper]
```
### 查看当前节点详细数据
```shell
ls -s /
ls -s /sanguo
ls -s /sanguo/shuguo
```
```shell
[dubbo, kafka, locks, pandy, pandy2, sanguo, servers, zookeeper]
cZxid = 0x0
ctime = Wed Dec 31 16:00:00 PST 1969
mZxid = 0x0
mtime = Wed Dec 31 16:00:00 PST 1969
pZxid = 0x20000010c
cversion = 12
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 0
numChildren = 8

参数解释：
（1）czxid：创建节点的事务 zxid
每次修改 ZooKeeper 状态都会产生一个 ZooKeeper 事务 ID。事务 ID 是 ZooKeeper 中所
有修改总的次序。每次修改都有唯一的 zxid，如果 zxid1 小于 zxid2，那么 zxid1 在 zxid2 之
前发生。
（2）ctime：znode 被创建的毫秒数（从 1970 年开始）
（3）mzxid：znode 最后更新的事务 zxid
（4）mtime：znode 最后修改的毫秒数（从 1970 年开始）
（5）pZxid：znode 最后更新的子节点 zxid
（6）cversion：znode 子节点变化号，znode 子节点修改次数
（7）dataversion：znode 数据变化号
（8）aclVersion：znode 访问控制列表的变化号
（9）ephemeralOwner：如果是临时节点，这个是 znode 拥有者的 session id。如果不是临时节点则是 0。
（10）dataLength：znode 的数据长度
（11）numChildren：znode 子节点数量
```
### 节点类型 （持久、短暂、有序号、无序号）
持久（Persistent）：客户端和服务器端断开连接后，创建的节点不删除
短暂（Ephemeral）：客户端和服务器端断开连接后，创建的节点自己删除
### 创建永久节点不带序号
```shell
create /sanguo "diaochan"

create /sanguo/shuguo "liubei"
```
### 创建带序号的
```shell
create -e -s /sanguo/wuguo "zhouyu"
```
```shell
[zk: master:2181(CONNECTED) 12] create -e -s /sanguo/wuguo "zhouyu"
Created /sanguo/wuguo0000000005
[zk: master:2181(CONNECTED) 13] ls /sanguo
[shuguo, weiguo, wuguo0000000005]
此时连接一旦断开，临时节点就不存在了。
```
### 修改节点的值
```shell
get -s /sanguo/weiguo
caocao
set -s /sanguo/weiguo "simayi"
```

```shell
get -s /sanguo/weiguo
```
## 删除
### 删除节点
```shell
delete /sanguo/jin
```
### 递归删除节点
```shell
deleteall /sanguo/shuguo
```
### 查看节点状态
```shell
stat /sanguo
```

# 监听器
### 监听器的原理
客户端注册监听它关心的目录节点，当目录节点发生变化（数据改变、节点删除、子目
录节点增加删除）时，ZooKeeper 会通知客户端。监听机制保证 ZooKeeper 保存的任何的数
据的任何改变都能快速的响应到监听了该节点的应用程序。
步骤：
1. 首先要有一个main()线程
2. 在main线程中创建Zookeeper客户端，这时就会创建两个线
程，一个负责网络连接通信（connet），一个负责监听（listener）。
3. 通过connect线程将注册的监听事件发送给Zookeeper。
4. 在Zookeeper的注册监听器列表中将注册的监听事件添加到列表中。
5. Zookeeper监听到有数据或路径变化，就会将这个消息发送给listener线程。
6. listener线程内部调用了process()方法。

### 常见的监听
1. 监听节点数据的变化
```shell
get path [watch]
```
2. 监听子节点增减的变化
```shell
ls path [watch]
```
### 设置节点监听器
启动node1 node2节点，运行zkCli
node2上执行监听三国的命令。
```shell
get -w /sanguo
meiren
```
node1上执行更改。
```shell
set /sanguo "diaochan"
```
node2上监听到更改。
```shell
[zk: localhost:2181(CONNECTED) 1] 
WATCHER::

WatchedEvent state:SyncConnected type:NodeDataChanged path:/sanguo
```
但是在node1上继续执行更改，监听器没有变化。因为注册一次，只能监听一次，要想再次监听，就要再注册。
### 设置节点的子节点监听器
```shell
[zk: localhost:2181(CONNECTED) 1] ls -w /sanguo
[shuguo, weiguo, wuguo0000000005]
```
```shell
create /sanguo/jinguo "simayi"
```
```shell
[zk: localhost:2181(CONNECTED) 2] 
WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/sanguo

```
