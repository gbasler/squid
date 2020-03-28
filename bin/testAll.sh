#!/bin/bash  
set -e
sbt "++ 2.12.10" core/test test example/test
sbt "++ 2.11.12" core/test test example/test
