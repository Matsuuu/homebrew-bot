#!/bin/bash

mvn clean package
export $(cat .env | xargs)
java -jar target/homebrew-bot-0.0.1-jar-with-dependencies.jar $DISCORD_BOT_TOKEN
