#!/bin/bash  
set -e
sbt "++ 2.12.8" core/clean clean example/clean
sbt "++ 2.11.12" core/clean clean example/clean
