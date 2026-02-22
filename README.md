# 🎵 Databass

![Status: Work in Progress](https://img.shields.io/badge/Status-Work_in_Progress-orange?style=flat-square)
![Stack: Spring Boot](https://img.shields.io/badge/Backend-Spring_Boot-6DB33F?style=flat-square&logo=springboot)
![Stack: Vue.js](https://img.shields.io/badge/Frontend-Vue.js-4FC08D?style=flat-square&logo=vuedotjs)
![Stack: Docker](https://img.shields.io/badge/Infrastructure-Docker-2496ED?style=flat-square&logo=docker)

**Databass** is a full-stack AI music generation platform. It allows users to turn simple text prompts into high-quality audio tracks using state-of-the-art AI models, complete with a built-in credit economy and prompt-enhancement engine.

---

## ✨ Features
* **AI Music Generation**: Queue and generate audio tracks asynchronously using Hugging Face models.
* **LLM Prompt Enhancement**: Automatically upgrades basic user inputs (e.g., "sad piano") into professional-grade AI prompts using Google Gemini.
* **Credit-Based Economy**: Users pay "credits" based on song duration and advanced parameter usage.
* **Secure Authentication**: JWT-based user registration and login.
* **Cloud Storage**: Audio files are safely stored and served via MinIO (S3-compatible storage).

## 🛠️ Tech Stack
**Frontend:**
* Vue 3 + Vite
* Tailwind CSS
* Pinia (State Management)
* Vue Router

**Backend:**
* Java / Spring Boot
* PostgreSQL (Database)
* RabbitMQ (Asynchronous Task Queue)
* MinIO (Object Storage)
* APIs: Google Gemini & Hugging Face Inference

## 🚧 Current Status & Roadmap
This project is currently under active development. 

- [x] **Backend Engine**: DB, Auth, Economy, Queues, and AI integration complete.
- [x] **Frontend Initialization**: Vue + Vite + Tailwind environment set up.
- [x] **Infrastructure**: Docker Compose setup for local development.
- [ ] **Frontend UI**: Login page, User Dashboard, and Audio Player (In Progress).
- [ ] **Deployment**: Dockerize Spring Boot and Vue services for full production.

## 🚀 Getting Started

### Prerequisites
* Java 17+
* Node.js & npm
* Docker & Docker Desktop
* API Keys for Google Gemini & Hugging Face

### 1. Boot up the Infrastructure
We use Docker Compose to run the required external services (Database, Message Queue, and S3 Storage) locally. Navigate to the root directory of the project and run:

```bash
docker compose up -d
```
*Note: You can verify RabbitMQ is running at `http://localhost:15672` and MinIO at `http://localhost:9001`.*

### 2. Run the Backend (Spring Boot)
Open a new terminal window:
```bash
cd databass-backend
# Ensure your application.properties and .env file are configured
./mvnw spring-boot:run
```

### 3. Run the Frontend (Vue.js)
Open another terminal window:
```bash
cd databass-frontend
npm install
npm run dev
```
The application will be available at `http://localhost:5173`.

---
*Created as part of a thesis project.*
