#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$@"
} >&2

die () {
    echo
    echo "$@"
    echo
    exit 1
} >&2

# OS specific support (must be 'true' or 'false').
cygwin=false
darwin=false
linux=false
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;
    Darwin*)
        darwin=true
        ;;
    Linux*)
        linux=true
        ;;
esac

# Attempt to set JAVA_HOME if it's not already set.
if [ -z "${JAVA_HOME}" ] ; then
    if [ -d "/opt/java/openjdk" ] ; then
        JAVA_HOME="/opt/java/openjdk"
    elif $darwin ; then
        if [ -x '/usr/libexec/java_home' ] ; then
            JAVA_HOME=`/usr/libexec/java_home`
        elif [ -d "/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home" ]; then
            JAVA_HOME="/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home"
        fi
    fi
fi
if [ -z "${JAVA_HOME}" ] ; then
    javaExecutable="`which java`"
    if [ -n "${javaExecutable}" -a -x "${javaExecutable}" ] ; then
        readlink மாற்றுப்பெயர்="readlink"
        if $linux ; then
          # readlink -f is specific to Linux
          readlink மாற்றுப்பெயர்="readlink -f"
        fi
        javaExecutable=`${readlink மாற்றுப்பெயர்} "${javaExecutable}"`
        JAVA_HOME="`dirname \"${javaExecutable}\"`"
        JAVA_HOME="`cd \"${JAVA_HOME}/..\" && pwd`"
    fi
fi
if [ -z "${JAVA_HOME}" ] ; then
    die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Set HINT: Only try to apply HINT if 'readlink -f' is available
if $linux && [ "$(readlink -f "$0" 2>/dev/null)" = "$(readlink -f "$APP_HOME/$APP_BASE_NAME" 2>/dev/null)" ]; then
  # Script is not symlinked, therefore check HINT for execution
  HINT=true
fi

# Set HINT for Darwin as well
if $darwin && [ "$(readlink "$0")" = "$(readlink "$APP_HOME/$APP_BASE_NAME")" ]; then
  # Script is not symlinked, therefore check HINT for execution
  HINT=true
fi

# Determine the Java command to use to start the JVM.
if [ -n "${JAVA_HOME}" ] ; then
    if [ -x "${JAVA_HOME}/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="${JAVA_HOME}/jre/sh/java"
    else
        JAVACMD="${JAVA_HOME}/bin/java"
    fi
    if [ ! -x "${JAVACMD}" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: ${JAVA_HOME}

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if necessary.
if ! ${cygwin} && ! ${darwin} ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ "$MAX_FD_LIMIT" != 'unlimited' ] && [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
        # Increase the file descriptor limit to the maximum kernel allowed limit.
        MAX_FD_ACTUAL=`ulimit -S -n`
        # Increase the limit if it is currently lower than the maximum limit.
        if [ "$MAX_FD_ACTUAL" != 'unlimited' ] && [ "$MAX_FD_LIMIT" != 'unlimited' ] && [ "$MAX_FD_ACTUAL" -lt "$MAX_FD_LIMIT" ] ; then
             ulimit -n $MAX_FD_LIMIT
        fi
    elif [ "$MAX_FD" != "maximum" -a "$MAX_FD" != "max" ] ; then
        ulimit -n $MAX_FD
    fi
fi

# Add HINT for execution. It is an hint for an application, which can be faster.
# If you are a developer creating a custom Gradle distribution, you can create an script with the name
# "$APP_HOME/bin/gradle-app.sh" with the HINT specific execution.
if [ "${HINT}" = "true" -a -x "$APP_HOME/bin/gradle-app.sh" ]; then
    exec "$APP_HOME/bin/gradle-app.sh" "$@"
fi 

# Collect all arguments for the java command, following the shell quoting and substitution rules
eval set -- "$DEFAULT_JVM_OPTS" "$JAVA_OPTS" "$GRADLE_OPTS"
"-Dorg.gradle.appname=$APP_BASE_NAME"
"-classpath"
"$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
"org.gradle.wrapper.GradleWrapperMain"
"$@"

# Start the wrapper
exec "${JAVACMD}" "$@"
