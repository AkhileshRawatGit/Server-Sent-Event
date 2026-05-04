# 🚀 Real-Time Notification System using Server-Sent Events (SSE)

## 📌 Overview

This project demonstrates a **real-time communication system** using **Server-Sent Events (SSE)** with **Spring Boot (Backend)** and **HTML/JavaScript (Frontend)**.

It allows the client to:

* Establish a persistent connection with the server
* Receive live updates without refreshing the page
* Trigger backend processes via a button and stream results in real-time

---

## ⚡ Features

* 🔄 Real-time data streaming using SSE
* 👥 Multi-user support using `ConcurrentHashMap`
* 🎯 Event-triggered processing (button click → backend process)
* 📡 Continuous connection without polling
* 🧹 Automatic cleanup on disconnect/timeout

---

## 🏗️ Tech Stack

* **Backend:** Java, Spring Boot
* **Frontend:** HTML, JavaScript
* **Protocol:** Server-Sent Events (SSE)

---

## 📂 Project Structure

```
project-root/
│
├── controller/
│     └── NotificationController.java
│
├── resources/
│     └── application.properties
│
└── frontend/
      └── index.html
```

---

## 🔧 Backend Implementation

### 1. Subscribe API (Establish SSE Connection)

```java
@GetMapping("/subscribe/{jobId}")
public SseEmitter subscribe(@PathVariable String jobId) {
    SseEmitter emitter = new SseEmitter(0L);
    clients.put(jobId, emitter);

    emitter.onCompletion(() -> clients.remove(jobId));
    emitter.onTimeout(() -> clients.remove(jobId));

    return emitter;
}
```

👉 Opens a persistent connection between client and server.

---

### 2. Send API (Trigger Process)

```java
@GetMapping("/send/{JobId}")
    public String send(@PathVariable String JobId){
        SseEmitter sseEmitter=clients.get(JobId);
        new Thread(() -> {
            try {
                sseEmitter.send("🚀 Process Started");

                for (int i = 1; i <= 5; i++) {
                    Thread.sleep(1000);
                    sseEmitter.send("Step " + i + " completed");
                }

                sseEmitter.send("✅ Process Finished");
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
        }).start();

        return "Started";
    }
```

👉 Starts a background task and streams updates to the client.

---

## 🌐 Frontend Implementation

```html
<!DOCTYPE html>
<html>
<body>

<h2>Process Control 🔥</h2>

<button onclick="startProcess()">Start Process</button>

<div id="output"></div>

<script>
    const userId = "user1";

    // SSE connection
    const eventSource = new EventSource(`http://localhost:8080/api/subscribe/${userId}`);

    eventSource.onmessage = function(event) {
        const div = document.getElementById("output");
        div.innerHTML += "<p>" + event.data + "</p>";
    };

    function startProcess() {
        fetch(`http://localhost:8080/api/send/${userId}`)
            .then(res => res.text())
            .then(data => console.log(data));
    }
</script>

</body>
</html>
```

---

## 🔄 Application Flow

1. Client connects to `/subscribe/{jobId}`
2. Server creates and stores `SseEmitter`
3. User clicks button → `/send/{jobId}` API called
4. Backend starts process in a new thread
5. Server sends updates via `emitter.send()`
6. Frontend receives updates in real-time

---

## ⚠️ Important Concepts

### 🔹 SseEmitter

* Acts as a communication channel between server and client
* Keeps HTTP connection open
* Sends real-time updates

### 🔹 ConcurrentHashMap

* Stores active client connections
* Enables multi-user support

### 🔹 Threading

* Prevents blocking the main request thread
* Allows asynchronous processing

---

## 🚀 Future Improvements

* 🔄 Replace `new Thread()` with `ExecutorService`
* 📊 Add progress bar UI
* 🔐 Add authentication (JWT)
* 🔁 Implement reconnection logic
* 📡 Broadcast events to multiple users

---

## 💡 Use Cases

* 📦 Order tracking systems
* 🤖 AI/ML model training logs
* 📊 Real-time dashboards

---

## 🧠 Key Learnings

* Asynchronous backend processing
* Real-time client-server communication
* Event-driven architecture
* SSE vs traditional REST APIs

---

## 👨‍💻 Author

**Akhilesh Rawat**

---

## ⭐ If you like this project

Give it a ⭐ on GitHub and share it 🚀
