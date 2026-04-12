# TaskFlow — JavaFX Task Management System

##  Project Structure

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

