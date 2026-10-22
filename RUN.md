./gradlew clean :app:installDebug
adb -s 192.168.254.168:41073 shell monkey -p com.pbec.preboardexamchecker 1

## Offline scanning

Sign in while online, select a complete exam cluster in Scan, and prepare it for offline use.
The saved package contains the three subjects, answer keys, and roster. Choose **Use Saved Session**
to scan without a network connection, including after restarting the app. Results remain local
until the existing background sync can reconnect. Prepare each needed cluster before going offline;
refresh a saved session online after changing its questions or roster.

## Email changes

Open Account → Security → Change Email. Enter the current password and new email, then follow
the verification link sent to the new address. Return to Security to refresh the status. If profile
sync fails after verification, retry the status check; do not request another address until it syncs.
The new email can also be used to sign in and finish profile synchronization.

## Verification

Run `./gradlew testDebugUnitTest assembleDebug assembleDebugAndroidTest` for local tests/builds.
Run `./gradlew connectedDebugAndroidTest` with a connected Android device for instrumentation tests.
Native OpenCV JVM tests skip when a desktop OpenCV library is unavailable; a successful debug build
alone does not verify camera accuracy, real-device latency, airplane-mode scanning, or live email links.
