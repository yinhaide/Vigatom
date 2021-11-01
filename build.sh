#/bin/sh
export ANDROID_HOME=$ANDROID_SDK
export GRADLE_HOME=/data/rdm/apps/gradle/gradle-2.14.1
export JAVA_HOME=$JDK8
export PATH=$JDK8/bin:$GRADLE_HOME/bin:$PATH

rm -rf bin/*.apk

if [ $debugBuild = "true" ]
then
    gradle :app:assembleDebug --info --stacktrace
    cp app/build/outputs/apk/* bin/
fi

if [ $releaseBuild = "true" ]
then
    gradle :app:assembleRelease --info --stacktrace
    cp app/build/outputs/apk/* bin/
fi




