#!/usr/bin/env sh

if [ -z ${ST_SERVER_URL+x} ]
then
	echo 'Warning: Environment-variable ST_SERVER_URL is not set!'
else
	if [ ! -f 'config/server-written' ]
	then
		echo 'Server-list was no yet written, overwriting with server from ST_SERVER_URL.'
		touch config/server-written
		echo "[
  {
    \"name\": \"Frost-Docker\",
    \"url\": \"$ST_SERVER_URL\"
  }
]" > config/servers.json

	else
		echo 'Server-list has already been written, leaving it unchanged.'
	fi
fi

java -jar "backend-0.0.1.jar"
