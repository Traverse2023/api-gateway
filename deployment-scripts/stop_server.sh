#!/bin/bash

isExistApp=$(pgrep java)
if [[ -n  $isExistApp ]]; then
  echo "Stopping the java application..."
  sudo kill -9 $isExistApp
  echo "The application is stopped."
else
  echo "The application is not running."
fi

