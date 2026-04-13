@echo off
cd src

javac --module-path "C:\Users\Dell\Downloads\openjfx-21.0.10_windows-x64_bin-sdk\javafx-sdk-21.0.10\lib" --add-modules javafx.controls,javafx.fxml -d .. main/Main.java models/*.java data/*.java services/*.java views/*.java

cd ..

java --module-path "C:\Users\Dell\Downloads\openjfx-21.0.10_windows-x64_bin-sdk\javafx-sdk-21.0.10\lib" --add-modules javafx.controls,javafx.fxml main.Main

pause
