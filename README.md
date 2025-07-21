
# 🏋️‍♂️ GemFit – AI-Powered Fitness Application

GemFit is a microservices-based fitness application that empowers users to achieve their fitness goals through AI-driven recommendations. Users enter their target calories, preferred duration, and desired activity type. Based on this input, the system recommends a personalized workout plan.

---

## 🚀 Features

- 🔐 Secure user authentication via **Keycloak**
- 🧠 AI-based workout recommendations
- 📊 Track calorie burn, duration, and activity suggestions
- 🔄 Asynchronous communication using **RabbitMQ**
- 🧩 Microservice architecture with **Eureka Service Discovery**
- 💾 Persistence via **MySQL** and **MongoDB**
- 🌐 Responsive frontend with **React**

---

## 🛠️ Tech Stack

| Layer         | Technology             |
|--------------|------------------------|
| Backend       | Spring Boot (Java)     |
| Frontend      | React                  |
| Auth          | Keycloak               |
| Messaging     | RabbitMQ               |
| Service Discovery | Eureka             |
| Databases     | MySQL, MongoDB         |
| API Gateway   | Spring Cloud Gateway (if applicable) |
| Build Tool    | Maven         |


## 📦 Microservices

| Service        | Description                                      |
|----------------|--------------------------------------------------|
| **User Service** | Handles registration, profile, and goals         |
| **AI Recommender** | Generates activity suggestions via AI logic     |
| **Workout Logger** | Logs user activity history and feedback         |
| **Notification Service** | (Optional) Sends reminders (if included) |
| **Gateway Service** | Routes and filters requests                  |

---

## 🔐 Authentication (Keycloak)

- Integrated via Spring Security adapter
- Roles: `USER`, `ADMIN`
- Token-based authentication (JWT)

---

## ⚙️ Setup & Installation

### Prerequisites:
- Java 17+
- Node.js 18+
- RabbitMQ server
- Docker (optional for databases)
- Keycloak server (can be containerized)

---

### 🖥 Backend Setup

```bash
cd backend/
./mvnw clean install
Start Eureka:


cd eureka-server/
./mvnw spring-boot:run
Start each microservice:


cd user-service/
./mvnw spring-boot:run
# Repeat for AI recommender, workout service, etc.

🌐 Frontend Setup

cd frontend/
npm install
npm start
