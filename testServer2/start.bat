@echo off
cls
copy "..\build\libs\durhack-1.0.jar" "plugins\"
java -Xms2G -Xmx2G -jar paper-1.20.2-263.jar nogui
