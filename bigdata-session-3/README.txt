I. Installation:

    1. cd provisioning
    2. ./build.sh
    3. ./run.sh

    //Wait until nodes is registred in YARN

    4. cd ../
    5. ./gradlew clean build
    6. ./load_data.sh


II. Run

    Use execute_*.sh scripts to run appropriate job

III. Restart containers:

    1. cd provisioning
    2. ./rm.sh
    3. ./run.sh
    4. cd ../
    5. ./load_data.sh

IV. Home work

    Located in ./homework.txt


