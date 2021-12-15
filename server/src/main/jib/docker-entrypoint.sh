#!/bin/sh
set -e
java $JAVA_OPTS \
  -server \
  -Djava.awt.headless=true \
  -cp \
  /app/resources:/app/classes:/app/libs/* \
  io.github.nefilim.ktorpatterns.MainKt -config=/app/resources/application-container.conf