@echo off
echo.
echo  TaskFlow - Compiling...
echo.

javac --module-path "C:\Users\subba rao kg\Downloads\openjfx-21.0.11_windows-x64_bin-sdk\javafx-sdk-21.0.11\lib" ^
      --add-modules javafx.controls,javafx.fxml ^
      -cp "lib\sqlite-jdbc-3.53.1.0.jar" ^
      -d out ^
      src\main\Main.java ^
      src\models\Task.java ^
      src\data\DatabaseManager.java ^
      src\data\TaskStore.java ^
      src\services\ReminderService.java ^
      src\views\MainView.java ^
      src\views\TodayView.java ^
      src\views\AllTasksView.java ^
      src\views\AddTaskView.java ^
      src\views\RemindersView.java ^
      src\views\TaskCard.java ^
      src\views\EditTaskDialog.java

if %errorlevel% neq 0 (
    echo.
    echo  COMPILE FAILED - check errors above
    pause
    exit /b 1
)

echo.
echo  Compile OK - Launching TaskFlow...
echo.

java --module-path "C:\Users\subba rao kg\Downloads\openjfx-21.0.11_windows-x64_bin-sdk\javafx-sdk-21.0.11\lib" ^
     --add-modules javafx.controls,javafx.fxml ^
     -cp "out;lib\sqlite-jdbc-3.53.1.0.jar" ^
     main.Main

pause
