#!/bin/sh
export DISPLAY=:0
xhost +si:localuser:root
xhost +
docker build -t swingapp -f DockerfileAlone .
docker run -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix swingapp