# Databass

Databass is a full-stack, microservices-based AI music generation platform. It transforms text prompts into high-fidelity audio tracks using state-of-the-art AI models, featuring a built-in virtual economy and an automated prompt-enhancement engine.

## Key Features
* **AI Audio Generation:** Asynchronous track rendering via Hugging Face inference models.
* **Prompt Engineering:** Automated LLM-based prompt enhancement using Google Gemini.
* **Virtual Economy:** Dynamic credit system based on track duration, claim streaks, and parameter complexity.
* **Social Ecosystem:** Public producer profiles, global discover feeds, and real-time streaming analytics.
* **Scalable Architecture:** Spring Cloud microservices communicating via an API Gateway and Eureka Service Registry.

## Tech Stack
* **Frontend:** Vue 3, Vite, Tailwind CSS, Pinia
* **Backend:** Java 17, Spring Boot, Spring Cloud
* **Infrastructure:** PostgreSQL, RabbitMQ, MinIO (S3-compatible storage), Docker

## Getting Started

Because the application is fully containerized, deployment is handled entirely through Docker Compose.

### Prerequisites
* Docker and Docker Compose
* API Keys for Google Gemini and Hugging Face

### 1. Environment Configuration
Create a `.env` file in the root directory of the project and populate it with your API keys:

```env
HF_TOKEN=your_huggingface_token
JWT_SECRET_KEY=your_secure_jwt_secret
GEMINI_API_KEY=your_gemini_api_key
```

### 2. Bootstrapping the Platform
Navigate to the root directory containing the `docker-compose.yml` and run:

```bash
docker compose up --build
```

This command automatically pulls the required infrastructure images, compiles the Spring Boot microservices via Maven, builds the Vue application, and initializes the MinIO storage buckets.

### 3. Application Access
Once the containers are running, the platform services are available at the following local endpoints:

* **Web Application:** `http://localhost:5173`
* **API Gateway:** `http://localhost:8080`
* **MinIO Storage Console:** `http://localhost:9001`
* **RabbitMQ Management:** `http://localhost:15672`

---
*Created as part of a thesis project.*
