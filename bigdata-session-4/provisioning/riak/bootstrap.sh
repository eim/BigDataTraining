#!/bin/bash

: ${HADOOP_PREFIX:=/usr/local/hadoop}

$HADOOP_PREFIX/etc/hadoop/hadoop-env.sh

rm /tmp/*.pid

# installing libraries if any - (resource urls added comma separated to the ACP system variable)
#cd $HADOOP_PREFIX/share/hadoop/common ; for cp in ${ACP//,/ }; do  echo == $cp; curl -LO $cp ; done; cd -

# altering the core-site configuration
sed s/HOSTNAME/$HOSTNAME/ /usr/local/hadoop/etc/hadoop/core-site.xml.template > /usr/local/hadoop/etc/hadoop/core-site.xml


service ssh start
#$HADOOP_PREFIX/sbin/start-dfs.sh
#$HADOOP_PREFIX/sbin/start-yarn.sh


# if [[ $1 == "-d" ]]; then
#   while true; do
#     sleep 1000;
#   done
# fi

if [[ $1 == "-bash" ]]; then
  /bin/bash
fi

export VOLDEMORT_HOME=/opt/voldemort-release-1.10.14-cutoff
export SERVER_PROP=${VOLDEMORT_HOME}/config/server.properties

if [ "$NODE_IP" == "172.16.248.20" ]; then
    grep "node.id=0" ${SERVER_PROP}
    if [[ $? -eq 1 ]]; then
        echo "node.id=0" >> ${SERVER_PROP}
    fi
elif [ "$NODE_IP" == "172.16.248.21" ]; then
    grep "node.id=1" ${SERVER_PROP}
    if [[ $? -eq 1 ]]; then
        echo "node.id=1" >> ${SERVER_PROP}
    fi
else
    grep "node.id=2" ${SERVER_PROP}
    if [[ $? -eq 1 ]]; then
        echo "node.id=2" >> ${SERVER_PROP}
    fi
fi

export YS=/usr/local/hadoop/etc/hadoop/yarn-site.xml

grep "RM_HOSTNAME" ${YS}
if [[ $? -eq 0 ]]; then
    sed -i.bak "s/RM_HOSTNAME/172\.16\.248\.20/" ${YS}
fi

grep "NM_HOSTNAME" ${YS}
if [[ $? -eq 0 ]]; then
    sed -i.bak "s/NM_HOSTNAME/172\.16\.248\.21/" ${YS}
fi

if [ "$NODE_IP" == "172.16.248.20" ]; then
#    $HADOOP_PREFIX/bin/hdfs namenode -format
    $HADOOP_PREFIX/sbin/hadoop-daemon.sh --config $HADOOP_CONF_DIR --script hdfs start namenode
    $HADOOP_YARN_HOME/sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR start resourcemanager
    $HADOOP_PREFIX/bin/hdfs dfs -mkdir -p /user/root
    $HADOOP_PREFIX/bin/hdfs dfs -put $HADOOP_PREFIX/etc/hadoop/ input
else
    $HADOOP_PREFIX/sbin/hadoop-daemons.sh --config $HADOOP_CONF_DIR --script hdfs start datanode
    $HADOOP_YARN_HOME/sbin/yarn-daemons.sh --config $HADOOP_CONF_DIR start nodemanager
    $HADOOP_PREFIX/sbin/mr-jobhistory-daemon.sh --config $HADOOP_CONF_DIR start historyserver
fi

cd ${VOLDEMORT_HOME}
echo "Going to start VOLDEMORT !!!"
./bin/voldemort-server.sh .

while true; do sleep 1000; done
