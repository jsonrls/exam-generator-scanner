./gradlew clean :app:installDebug
adb -s 192.168.1.4:43279 shell monkey -p com.pbec.preboardexamchecker 1
