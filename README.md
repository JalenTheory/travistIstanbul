travistIstanbul
===============

You need to add map file manually for the time being. 

put this istanbul-gh folder into /sdcard/graphhopper/maps/ on your device.
https://drive.google.com/?authuser=0#folders/0BwgPULW0li42RmF5N1FNeXdwLUk

You can use adb to push the file into your device. 
Using for example using "adb" in your android_sdk.Probably that is in
./android_sdk/platform-tools/adb.exe
use "adb shell" to access your phone or access it be windows-magic and
create folders:
/sdcard/graphhopper/maps/istanbul-gh/
/sdcard/graphhopper/maps/map-gh/

Then use the adb-tool to push (adb push) the map files into your phone
syntax is "adb push <local file> <remote location>

Final result should be 
/sdcard/graphhopper/maps/istanbul-gh/istanbul.map
..and other files in that folder.

This is where the app finds the actual map (.map) to display on the screen.

Leppavaara map is
https://drive.google.com/?authuser=0#folders/0BwgPULW0li42ZEFuc2JiY3RQQnM

and location should be 
/sdcard/graphhopper/maps/leppavaara-gh/map.map

Similar process.

the file locations are hardcoded for the time being..
