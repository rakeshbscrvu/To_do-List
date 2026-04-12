package models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Task {

    public enum Priority { LOW, MEDIUM, HIGH }
    public enum Status { PENDING, IN_PROGRESS, DONE }

    private String id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private LocalTime reminderTime;
    private Priority priority;
    private Status status;
    private String category;

    public Task(String title, String description, LocalDate dueDate,
                LocalTime reminderTime, Priority priority, String category) {
        this.id = java.util.UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.reminderTime = reminderTime;
        this.priority = priority;
        this.status = Status.PENDING;
        this.category = category;
    }

    // Getters
    public String getId()             { return id; }
    public String getTitle()          { return title; }
    public String getDescription()    { return description; }
    public LocalDate getDueDate()     { return dueDate; }
    public LocalTime getReminderTime(){ return reminderTime; }
    public Priority getPriority()     { return priority; }
    public Status getStatus()         { return status; }
    public String getCategory()       { return category; }

    // Setters
    public void setTitle(String title)          { this.title = title; }
    public void setDescription(String desc)     { this.description = desc; }
    public void setDueDate(LocalDate date)      { this.dueDate = date; }
    public void setReminderTime(LocalTime time) { this.reminderTime = time; }
    public void setPriority(Priority p)         { this.priority = p; }
    public void setStatus(Status s)             { this.status = s; }
    public void setCategory(String category)    { this.category = category; }

    public boolean isDueToday() {
        return dueDate != null && dueDate.equals(LocalDate.now());
    }

    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && status != Status.DONE;
    }

    @Override
    public String toString() {
        return title + " [" + priority + "] - " + status;
    }
}
