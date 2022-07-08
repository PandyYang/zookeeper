# !/bin/bash

case $1 in 
"start") {
	for i in master node1 node2
	do
		echo ------------ zookeeper $i 启动------------
		ssh $i "/root/zookeeper-3.5.10/bin/zkServer.sh start"
	done
}
;;
"stop") {
	for i in master node1 node2
	do
		echo ------------ zookeeper $i 停止------------
		ssh $i "/root/zookeeper-3.5.10/bin/zkServer.sh stop"
	done
}
;;
"status") {
	for i in master node1 node2
	do
		echo ------------ zookeeper $i 状态------------
		ssh $i "/root/zookeeper-3.5.10/bin/zkServer.sh status"
	done
}
;;
esac