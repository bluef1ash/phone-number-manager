#!/bin/sh
# shellcheck disable=SC2039
# shellcheck disable=SC2235
# shellcheck disable=SC1105
if ((type -p java)) || (([[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]])) then
   # shellcheck disable=SC2046
   path=$(dirname $(readlink -f "$0"))
   file=${path}"/phone-number-manager-0.0.1-SNAPSHOT.jar"
    while getopts ":p:" opt
    do
        case $opt in
            p)
            echo "参数a的值$OPTARG"
            if [ -n "$OPTARG" ]; then
                path=$OPTARG
            fi
            nohup java -jar "${file}" -Dlogging.path="${path}" > /dev/null &
            exit 0;;
            ?)
            echo "未知参数"
            exit 1;;
        esac
    done
    nohup java -jar "${file}" -Dlogging.path="${path}" > /dev/null &

else
    echo "no java"
fi
