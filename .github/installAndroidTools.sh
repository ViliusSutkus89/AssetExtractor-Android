#!/bin/sh
set -eu

ANDROID_SDK_TOOLS=6514223
BUILD_TOOLS="build-tools;30.0.0"
PLATFORM="platforms;android-30"


SDK_MGR=$ANDROID_HOME/tools/bin/sdkmanager
if test ! -f $SDK_MGR; then
  SDK_MGR=$ANDROID_HOME/cmdline-tools/tools/bin/sdkmanager
fi

if test ! -f $SDK_MGR; then
  echo "Installing cmdline tools"
  cmdtoolsfile=commandlinetools-linux-${ANDROID_SDK_TOOLS}_latest.zip
  if test ! -f ${cmdtoolsfile}
  then
    wget --quiet "https://dl.google.com/android/repository/${cmdtoolsfile}" --output-document "${cmdtoolsfile}"
  fi
  sudo unzip -d $ANDROID_HOME/cmdline-tools ${cmdtoolsfile} > /dev/null
fi

echo "Installing ${BUILD_TOOLS}"
echo "y" | sudo $SDK_MGR "${BUILD_TOOLS}" > /dev/null

echo "Installing ${PLATFORM}"
echo "y" | sudo $SDK_MGR "${PLATFORM}" > /dev/null

echo "y" | sudo $SDK_MGR --licenses > /dev/null
