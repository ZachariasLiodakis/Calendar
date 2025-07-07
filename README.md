![Daily Task Manager](Images/Daily%20Task%20Manager.png)
# Daily Task Manager Android App

## 📱 Overview
This Android application allows users to create, manage, and monitor their daily tasks. Each task includes information such as a short name, description, start time, duration, and location. The app automatically tracks task statuses over time and supports viewing, updating, deleting, and exporting tasks, as well as integration with other Android apps via a Content Provider.

---

## ✅ Implemented Functionalities

### A. Create a New Task
- User-friendly form to input:
  - Short Name
  - Description
  - Start Time
  - Duration
  - Location (optional)
- On form submission:
  - Task is inserted into the Room database.
  - Default status set to `recorded`.
  - Input validation is enforced.

---

### B. Delete Task by ID
- User inputs the Task ID.
- Task is deleted from the database.
- A Toast message shows how many rows were affected.

---

### C. Periodic Status Update
- A `WorkManager` job runs every 1 hour.
- Task status automatically updates based on:
  - Start time & duration.
  - Transitions:  
    `recorded → in-progress → expired`
- Logic respects the task state transition diagram.

---

### D. View All Non-Completed Tasks
- Displayed using `RecyclerView`.
- Tasks sorted with urgent (`expired`) ones on top.
- On click, detailed view opens with:
  - Full task information.
  - Action buttons (see E).

---

### E. Update Status & Google Maps Integration
- In the task details screen:
  - Mark task as `completed` via a button.
  - If location is set, user can open it directly in Google Maps.

---

### F. Export Tasks to File
- All non-completed tasks are exported to an `.txt` file.
- File saved to the selected directory.
- File contains all task details, easily shareable and viewable in browsers or editors.

---

### G. Content Provider Support
- Custom `ContentProvider` allows external apps to:
  - Insert, read, update, or delete tasks.
- For testing, internal UI can trigger operations using `ContentResolver`.

---

## 🗃️ Database Structure

### Tables:
1. **Task**
   - `uid` (Primary Key)
   - `shortName`
   - `description`
   - `startTime`
   - `duration`
   - `statusId` (Foreign Key)
   - `location`

2. **Status**
   - `id` (Primary Key)
   - `name` (recorded, in-progress, expired, completed)

---

## 📸 Screenshots
### Task creation form:
![Task Creation](Images/Task%20Creation.png)
### Task deletion form:
![Task Deletion](Images/Task%20Deletion.png)
### Task details screen:
![Task Overview](Images/Task%20Overview.png)
### Google Maps integration:
![Google Maps](Images/Google%20Maps.png)
### Exported TXT file example:
![Task Exporting](Images/Task%20Exporting.png)

---

## ⚠ Notes
- Ensure you grant permissions for writing to external storage when exporting.
- Google Maps opens using an implicit Intent if a location is provided.
- The periodic status checker uses `WorkManager` and respects Android background limitations.
