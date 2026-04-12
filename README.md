# TaskFlow — JavaFX Task Management System

## 📁 Project Structure

```
TaskManagerApp/
├── src/
│   ├── main/
│   │   └── Main.java               ← Entry point, launches JavaFX app
│   │
│   ├── models/
│   │   └── Task.java               ← Task data model (title, date, priority, status)
│   │
│   ├── data/
│   │   └── TaskStore.java          ← In-memory data store (singleton) + sample data
│   │
│   ├── services/
│   │   └── ReminderService.java    ← Background reminder checker (every 30s)
│   │
│   ├── views/
│   │   ├── MainView.java           ← Root layout: sidebar + content area
│   │   ├── TodayView.java          ← "Today" screen with overdue + today's tasks
│   │   ├── AllTasksView.java       ← All tasks with search, category & status filter
│   │   ├── AddTaskView.java        ← Add task form with validation
│   │   ├── RemindersView.java      ← Reminders grouped by today / upcoming
│   │   └── TaskCard.java           ← Reusable task card component
│   │
│   └── resources/
│       ├── styles/
│          └── style.css           ← Full dark-glass UI theme + background image
```

---

## 🚀 Features

| Feature | Description |
|---|---|
| 📅 Today View | Shows today's tasks + overdue items highlighted in red |
| 📋 All Tasks | Complete list with search bar, category & status filters |
| ➕ Add Task | Form with title, description, due date, reminder time, priority, category |
| ⏰ Reminders | Lists tasks with reminder times grouped (today / upcoming) |
| ⏰ Auto Reminders | Background service shows popup alerts when reminder time arrives |
| ✅ Status Toggle | Mark tasks as PENDING / IN_PROGRESS / DONE inline |
| 🗑 Delete Tasks | Delete with confirmation dialog |
| 🎨 Background Image | Custom background image via CSS (`-fx-background-image`) |

---

## 🛠 How to Run

### Prerequisites
- Java 17+ (with JavaFX bundled or separately)
- JavaFX SDK (if using separate JDK)

### Option A — IntelliJ IDEA
1. Create a new JavaFX project
2. Copy all `.java` files into their respective packages
3. Copy `style.css` to `src/resources/styles/`
4. Add your background image to `src/resources/images/background.jpg`
5. Add VM options: `--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml`
6. Run `Main.java`

### Option B — Command Line (with JavaFX on classpath)
```bash
javac --module-path $JAVAFX_HOME/lib --add-modules javafx.controls \
      -d out src/**/*.java

java --module-path $JAVAFX_HOME/lib --add-modules javafx.controls \
     -cp out main.Main
```

---

