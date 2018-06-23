export JAVA_HOME=/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home
UNITY_ROOT="/Applications/Unity/"
ClASSPATH=$UNITY_ROOT"/PlaybackEngines/AndroidPlayer/Variations/mono/Release/Classes/classes.jar"

javac PermissionPlease.java -bootclasspath $ANDROID_HOME/platforms/android-26/android.jar -classpath $ClASSPATH -d .
javap -s net.jimmar.unityplugins.PermissionPlease
jar cvfM PermissionPlease.jar net/
rm -rf net
mkdir ../libs
mv PermissionPlease.jar ../libs/