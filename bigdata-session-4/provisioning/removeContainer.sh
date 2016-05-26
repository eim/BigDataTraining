#!/usr/bin/env bash

docker ps -a|awk '{print $1}'|tail -n +2|xargs docker $1