#!/bin/bash

mvn clean package
java -jar target/homebrew-bot-0.0.1.jar
