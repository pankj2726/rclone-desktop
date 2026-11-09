#!/bin/sh
#
# Minimal Gradle wrapper launcher (Android project standard).
#

# Resolve APP_HOME
app_path=$0
while
    APP_HOME=${app_path%"${app_path#*/}"}
    [ -h "$app_path" ]
do
    ls=$( ls -ld "$app_path" )
    link=${ls#*' -> '}
    case $link in
      /*)   app_path=$link ;;
      *)    app_path=$APP_HOME$link ;;
    esac
done
APP_HOME=$( cd "${APP_HOME:-./}" && pwd -P ) || exit

# JVM options
MAX_FD="maximum"
warn () { echo "$*" >&2; }
die () { echo "$*" >&2; exit 1; }

# Find java
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    elif [ -x "$JAVA_HOME/bin/java" ] ; then
        JAVACMD="$JAVA_HOME/bin/java"
    else
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD=`which java`
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
    fi
fi

# Build classpath
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec "$JAVACMD" \
  -Dorg.gradle.appname=${0##*/} \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
