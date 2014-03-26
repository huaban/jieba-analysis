#!/bin/bash

ROOT=`dirname $0`
cd $ROOT/..
mvn package install -DcreateChecksum=true -DskipTests

