#!/usr/bin/env bash

if [ -z ${BACKEND_URL+x} ]
then
	echo 'ERROR: Environment-variable BACKEND_URL is not set!'
	exit 1
fi

echo "Setting Backend-URL from environment to '$BACKEND_URL'."
sed -i "s|<BACKEND>|$BACKEND_URL|" src/proxy.conf.json
ng serve --host 0.0.0.0 --disableHostCheck
