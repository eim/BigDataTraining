I. Installation:

    1. cd provisioning
    2. ./build.sh
    3. ./run.sh
    4. cd ../
    5. ./gradlew clean build


II. Run

    Use execute_*.sh scripts to run appropriate job

III. Restart containers:

    1. cd provisioning
    2. ./rm.sh
    3. ./run.sh
    4. cd ../

IV. Home work

    Located in ./homework.txt
-------------------------------------------------------
1) In server.properties
readonly.hadoop.config.path=
readonly.keytab.path=

2) In core-site.xml
    <property>
        <name>hadoop.security.authentication</name>
        <value>simple</value> <!-- A value of "simple" would disable security. -->
    </property>

    <property>
        <name>hadoop.security.authorization</name>
        <value>true</value>
    </property>

3) hdfs-site.xml and core-site.xml copy into /usr/local/hadoop/etc/hadoop

4)

