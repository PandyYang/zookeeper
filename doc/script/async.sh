#!/bin/bash
# 判断参数是否足够
if [ $# -lt 1 ]
then
 echo Not Enounh Arguement!
 exit;
fi

# 遍历所有的机器
for host in master node1 node2
do
 echo ============  $host ============
 for file in $@
 do
  # 判断文件是否存在
  if [ -e $file ]
  then
   # 获取父目录
   pdir=$(cd -P $(dirname $file); pwd)

   # 获取当前目录的名称
   fname=$(basename $file)
   ssh $host "mkdir -p $pdir"
   rsync -av $pdir/$fname $host:$pdir
  else
   echo $file does not exists!
  fi
 done
done