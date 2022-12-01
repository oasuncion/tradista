#!/bin/bash
export DB_HOST=localhost
export DB_PORT=1527
export SERVICE_ACCOUNT=oli
export SERVICE_ACCOUNT_PASSWORD=oli
$WILDFLY_HOME\bin\jboss-cli.sh --connect --file=./commands.cli