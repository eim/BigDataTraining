#!/bin/bash

: ${HADOOP_PREFIX:=/usr/local/hadoop}

$HADOOP_PREFIX/etc/hadoop/hadoop-env.sh

rm /tmp/*.pid

# installing libraries if any - (resource urls added comma separated to the ACP system variable)
cd $HADOOP_PREFIX/share/hadoop/common ; for cp in ${ACP//,/ }; do  echo == $cp; curl -LO $cp ; done; cd -

# altering the core-site configuration
sed s/HOSTNAME/$HOSTNAME/ /usr/local/hadoop/etc/hadoop/core-site.xml.template > /usr/local/hadoop/etc/hadoop/core-site.xml


service ssh start
$HADOOP_PREFIX/sbin/start-dfs.sh
$HADOOP_PREFIX/sbin/start-yarn.sh


# if [[ $1 == "-d" ]]; then
#   while true; do
#     sleep 1000;
#   done
# fi

if [[ $1 == "-bash" ]]; then
  /bin/bash
fi


export VOLDEMORT_HOME=/opt/voldemort-release-1.10.15-cutoff

if [ "$NODE_IP" == "172.16.248.20" ]; then
    echo "node.id=0" > /opt/voldemort-release-1.10.15-cutoff/config/server.properties
elif [ "$NODE_IP" == "172.16.248.21" ]; then
    echo "node.id=1" > /opt/voldemort-release-1.10.15-cutoff/config/server.properties
else
    echo "node.id=2" > /opt/voldemort-release-1.10.15-cutoff/config/server.properties
fi

cd ${VOLDEMORT_HOME}
echo "Going to start VOLDEMORT !!!"
./bin/voldemort-server.sh .

while true; do sleep 1000; done
