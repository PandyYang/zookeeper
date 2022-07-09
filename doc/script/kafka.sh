#!/bin/bash


case $1 in
"start")
	for i in master node1 node2
	do 
		echo "----start kafka num is $i"
		ssh $i "cd ~ && ./kafka/bin/kafka-server-start.sh -daemon /root/kafka/config/server.properties"
	done
;;
"stop")
	for i in master node1 node2
	do 
		echo "----stop kafka num is $i"
		ssh $i "cd ~ && ./kafka/bin/kafka-server-stop.sh"
	done
;;
esac