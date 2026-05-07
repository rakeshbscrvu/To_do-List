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

## Methodology

Our step-by-step approach for implementation follows a structured software development lifecycle, focusing on modular design and user-friendly interface development.

### Research & Planning
We identified the need for a simple and efficient task management system to help users organize daily activities. The core functionalities such as task creation, prioritization, status tracking, and reminders were defined. The project structure was planned using a modular approach separating models, services, views, and data handling components.

### Architecture/Design
The application follows a modular and layered architecture using Java and JavaFX. The system execution flows as follows:

1. **User Interaction** (JavaFX UI – Forms, Buttons, Task Views)
   ↓
2. **View Layer** (`MainView`, `TodayView`, `AllTasksView`, `AddTaskView`, `RemindersView`)
   ↓
3. **Component Layer** (Reusable UI elements like `TaskCard`)
   ↓
4. **Service Layer** (`ReminderService` – periodic background task checking)
   ↓
5. **Data Layer** (`TaskStore` – in-memory singleton data management)
   ↓
6. **Model Layer** (`Task` – encapsulates task properties like title, date, priority, and status)

### Implementation Plan
The team will divide tasks as follows. The development is divided into the following components:
- **UI Development**: Designing interactive screens using JavaFX (`MainView`, `TodayView`, etc.)
- **Model Design**: Creating the `Task` class to represent task attributes and behaviors
- **Data Management**: Implementing `TaskStore` to handle task storage and retrieval
- **Reminder System**: Developing `ReminderService` to periodically check deadlines and notify users
- **Styling**: Enhancing user interface using JavaFX CSS for better user experience

### Verification Plan
The system will be tested to ensure correct functionality and usability. This includes validating task creation, updating and deletion, checking filtering and search features, and verifying reminder notifications. The application will also be tested for UI responsiveness and smooth performance under normal usage conditions.
