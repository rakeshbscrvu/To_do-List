# TaskFlow — JavaFX Task Management System

##  Project Structure

```
To_do-list/
├── lib/
│   └── sqlite-jdbc-3.53.1.0.jar        ← SQLite JDBC driver
│
├── src/
│   ├── main/
│   │   └── Main.java                   ← Entry point, initializes DB, launches JavaFX
│   │
│   ├── models/
│   │   └── Task.java                   ← Data model (title, date, priority, status, reminder)
│   │
│   ├── data/
│   │   ├── DatabaseManager.java        ← NEW — SQLite connection + table creation
│   │   └── TaskStore.java              ← NEW — all CRUD operations via SQL
│   │
│   ├── services/
│   │   └── ReminderService.java        ← Background reminder checker (every 30s)
│   │
│   ├── views/
│   │   ├── MainView.java               ← Root layout: sidebar, stat cards, content area
│   │   ├── TodayView.java              ← Hero banner + overdue + today's tasks
│   │   ├── AllTasksView.java           ← All tasks with search, category & status filter
│   │   ├── AddTaskView.java            ← Add task form with validation
│   │   ├── RemindersView.java          ← Reminders grouped by today / upcoming
│   │   ├── TaskCard.java               ← Reusable card with accent bar, edit, delete
│   │   └── EditTaskDialog.java         ← NEW — pre-filled edit popup dialog
│   │
│   └── resources/
│       └── styles/
│           └── style.css               ← Dark neon theme
│
├── out/                                ← Empty — compiled .class files go here
└── run.bat                             ← Compile + run script
```

Methodology
Our step-by-step approach for implementation follows a structured software development lifecycle, focusing on modular design, persistent storage, and a modern user interface.
Research & Planning
We identified the need for a simple and efficient task management system to help users organize daily activities. The core functionalities such as task creation, prioritization, status tracking, reminders, and persistent storage were defined. The project structure was planned using a modular approach separating models, services, views, data handling, and database components.
Architecture/Design
The application follows a modular and layered architecture using Java 21 and JavaFX. The system execution flows as follows:

User Interaction (JavaFX UI – Forms, Buttons, Task Cards, Edit Dialogs)
↓
View Layer (MainView, TodayView, AllTasksView, AddTaskView, RemindersView)
↓
Component Layer (Reusable UI elements like TaskCard and EditTaskDialog)
↓
Service Layer (ReminderService – periodic background task checking every 30 seconds)
↓
Data Layer (TaskStore – all SQL queries: SELECT, INSERT, UPDATE, DELETE)
↓
Database Layer (DatabaseManager – SQLite connection management and table creation)
↓
Model Layer (Task – encapsulates task properties like title, date, priority, status, and reminder time)
↓
Storage (taskflow.db – SQLite file stored in the user's home directory)

Implementation Plan
The development is divided into the following components:

UI Development: Designing interactive screens using JavaFX (MainView, TodayView, etc.) with a neon glassmorphism dark theme using inline styles to ensure consistent rendering on Windows
Model Design: Creating the Task class to represent task attributes and behaviours including a setId() method required for loading tasks back from the database
Database Layer: Implementing DatabaseManager for SQLite connection management and TaskStore for all CRUD operations using the SQLite JDBC driver
Reminder System: Developing ReminderService using a JavaFX Timeline to periodically check deadlines every 30 seconds and notify users via popup alerts with null-safe CSS handling
Edit Functionality: Building EditTaskDialog as a pre-filled popup form allowing users to update any field of an existing task with immediate database persistence
Styling: Enhancing the user interface using a combination of JavaFX CSS and inline styles for a modern dark theme with teal, blue, purple, and amber accents

Verification Plan
The system was tested to ensure correct functionality and usability across all features:

Task CRUD – task creation, editing, and deletion confirmed working with data persisting after app restart 
Search & Filter – search by title and description, filter by category and status confirmed
Reminder Notifications – popup alert confirmed firing automatically at the set reminder time
Status Tracking – checkbox and dropdown status changes confirmed writing to database 
Overdue Detection – tasks past due date correctly highlighted in red 
UI Consistency – dark theme confirmed applying across all screens including scroll areas 
Database Persistence – all tasks confirmed loading correctly from taskflow.db on app restart 