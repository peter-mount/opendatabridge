#!/bin/ash

cd /opt/nre

java \
    -cp $(echo $(ls *.jar)|sed "s/ /:/g") \
    -Djava.util.logging.config.file=logging.properties \
    dev.area51.opendatabridge.Main \
    /config.xml

echo "Sleeping 15 seconds"
sleep 15s
