#!/bin/sh
set -eu

SDK_MGR=$ANDROID_HOME/tools/bin/sdkmanager
if test ! -f $SDK_MGR; then
  SDK_MGR=$ANDROID_HOME/cmdline-tools/tools/bin/sdkmanager
fi

if test ! -f $SDK_MGR; then
  cmdtoolsfile=commandlinetools-linux-${ANDROID_SDK_TOOLS}_latest.zip
  if test ! -f ${cmdtoolsfile}
  then
    wget --quiet "https://dl.google.com/android/repository/${cmdtoolsfile}" --output-document "${cmdtoolsfile}"
  fi
  sudo unzip -d $ANDROID_HOME/cmdline-tools ${cmdtoolsfile} > /dev/null
fi

echo "y" | sudo $SDK_MGR "tools" > /dev/null
echo "y" | sudo $SDK_MGR --licenses > /dev/null
