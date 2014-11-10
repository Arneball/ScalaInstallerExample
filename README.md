Sample project using scala installer found at https://github.com/Arneball/Android-Scala-Installer

#### Compile without \<use-library\>
#### Runs proguard to treeshake the release

gradle app:assembleRelease 


#### Compile with \<use-library ...\>
#### for that you need to use Android-Scala-Installer

gradle app:assembleDebug

