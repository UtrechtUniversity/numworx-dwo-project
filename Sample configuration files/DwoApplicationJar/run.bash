#!/bin/bash
#add -d64 for forcing 64-bit
#uncomment required RunClass.
nohup java  -Xms512m -Xmx1g -cp lib/mysql-connector-java-5.1.23-bin.jar -jar DWO.jar