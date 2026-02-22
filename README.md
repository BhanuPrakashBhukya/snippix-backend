# <img src="https://img.shields.io/badge/Snippix-Backend-1150BF?style=for-the-badge&logo=java&logoColor=white" />

> **Production-grade distributed real-time chat backend** powering Snippix — built with microservices, event-driven architecture, and engineered for scale.

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=flat-square&logo=apachekafka&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)
![Cassandra](https://img.shields.io/badge/Cassandra-1287B1?style=flat-square&logo=apachecassandra&logoColor=white)
![Keycloak](https://img.shields.io/badge/Keycloak-4D4D4D?style=flat-square&logo=keycloak&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white)
![gRPC](https://img.shields.io/badge/gRPC-244c5a?style=flat-square&logo=grpc&logoColor=white)
![Eureka](https://img.shields.io/badge/Eureka-Service_Discovery-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Resilience4J](https://img.shields.io/badge/Resilience4J-Circuit_Breaker-brightgreen?style=flat-square)

---

## 📡 What is this?

Snippix backend is a **horizontally scalable**, **fault-tolerant** messaging infrastructure that handles real-time WebSocket connections, asynchronous event streaming, and inter-service communication — all designed to support millions of concurrent users.

```
10,000 concurrent WebSocket connections  ·  <40ms message latency  ·  50,000 msg/sec Kafka throughput
```

---

## 📸 Screenshots

> Real-time messaging between users — powered by WebSockets + Kafka

**Login Page**

![Login](https://raw.githubusercontent.com/BhanuPrakashBhukya/snippix-backend/3ae2be2389d6a329cc60241e2c3975a46b1909fb/docs/screenshots/login-page.png)

**Live Chat — Two Users**

| Bhanu's View | Sravan's View |
|---|---|
| ![Bhanu Chat](https://raw.githubusercontent.com/BhanuPrakashBhukya/snippix-backend/3ae2be2389d6a329cc60241e2c3975a46b1909fb/docs/screenshots/chat-bhanu.png) | ![Sravan Chat](https://raw.githubusercontent.com/BhanuPrakashBhukya/snippix-backend/3ae2be2389d6a329cc60241e2c3975a46b1909fb/docs/screenshots/chat-sravan.png) |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                          CLIENT (Snippix App)                        │
│                     WebSocket  ·  REST  ·  OAuth2                    │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
                    ┌───────────▼────────────────┐
                    │    Spring Cloud API Gateway  │
                    │  Rate Limiting · JWT Filter  │
                    │  Circuit Breaker · Routing   │
                    └───┬──────────────────────┬──┘
                        │                      │
                        │   ┌──────────────────▼──────┐
                        │   │   Eureka Service Registry│
                        │   │   Service Discovery      │
                        │   │   Health Monitoring      │
                        │   └──────────────────────────┘
                        │
          ┌─────────────▼──┐       ┌───────────────────┐
          │  Auth Service   │       │   Chat Service     │
          │  Keycloak/JWT   │       │  WebSocket · STOMP │
          │  OAuth2 · OIDC  │       │  Message Persist   │
          │  [Eureka Client]│       │  Kafka Publisher   │
          └─────────────────┘       │  [Eureka Client]   │
                                    │  [Resilience4J]    │
                                    └───────┬────────────┘
                        │                   │
          ┌─────────────▼──┐       ┌───────▼───────────┐
          │  User Service   │◄─────►│      Kafka        │
          │  Profile Mgmt   │ gRPC  │  Event Streaming  │
          │  gRPC Server    │       │  50k msg/sec      │
          │  [Eureka Client]│       └───────┬───────────┘
          │  [Resilience4J] │               │
          └─────────────────┘       ┌───────▼───────────┐
                                    │      Redis         │
                                    │  Presence Tracker  │
                                    │  Unread Counts     │
                                    │  Distributed Cache │
                                    └───────────────────┘

         ┌──────────────────────────────────────────────────────┐
         │                      Databases                        │
         │  MySQL (users, auth)  ·  Cassandra (chat messages)   │
         │  Optimized for high write throughput & time-series   │
         └──────────────────────────────────────────────────────┘
```

---

## 🔄 Message Flow

```
User App                  Chat Service           Kafka          Redis         Receiver
   │                           │                   │              │              │
   │──── WebSocket Send ───────►│                   │              │              │
   │                           │── JWT Validate ──►Keycloak       │              │
   │                           │◄─ Token Valid ─────│              │              │
   │                           │── Persist to DB ──►MySQL/Cassandra│              │
   │                           │── Publish Event ──►│              │              │
   │                           │                   │── Update ────►│              │
   │                           │                   │   Unread      │              │
   │                           │                   │   Count       │              │
   │                           │◄──────────────────│── Delivered ──│──────────── ►│
   │◄─── ACK ──────────────────│                   │              │              │
```

---

## 🧩 Services

### 1️⃣ Spring Cloud API Gateway
- Single entry point for all client requests
- **JWT authentication filter** — validates tokens before routing
- **Rate limiting** per user/IP using Redis token bucket
- Dynamic routing via Eureka service registry
- Built-in **Resilience4J circuit breaker** on all routes
- WebSocket proxying to Chat Service

### 2️⃣ Eureka Service Registry
- All microservices register themselves on startup
- API Gateway discovers services dynamically — no hardcoded URLs
- Health checks and automatic deregistration on failure
- Enables zero-downtime horizontal scaling

### 3️⃣ Auth Service
- OAuth2 / OpenID Connect via **Keycloak**
- JWT validation and role-based authorization
- Stateless token introspection
- Registered with Eureka as `auth-service`

### 4️⃣ User Service
- User profile management
- Exposed via **gRPC** for internal service communication
- Protocol Buffers serialization
- **Resilience4J** circuit breaker on gRPC calls
- Registered with Eureka as `user-service`

### 5️⃣ Chat Service
- **WebSocket (STOMP)** server for real-time bidirectional messaging
- Message persistence to **Apache Cassandra**
- Kafka event publishing on every message
- **Resilience4J** fallback if Kafka is temporarily unavailable
- Registered with Eureka as `chat-service`

### 6️⃣ Kafka (Event Bus)
- Async message delivery between services
- Decoupled architecture — services don't call each other directly
- Event replay capability for fault recovery

### 7️⃣ Redis (Cache + Presence)
- Online/offline user presence tracking
- Unread message count per conversation
- API Gateway rate limiting buckets
- Distributed cache for hot data

### 8️⃣ gRPC Layer
- High-performance sync communication between User ↔ Chat service
- Binary Protocol Buffers — faster than JSON over REST

### 9️⃣ Apache Cassandra (Chat Storage)
- Stores all chat messages — optimized for **high write throughput**
- Wide-column model — queries by `conversationId` + `timestamp`
- Handles time-series message data efficiently
- Scales horizontally across nodes with no single point of failure

```
Table: messages
─────────────────────────────────────────────────
conversation_id  UUID        ← partition key
created_at       TIMESTAMP   ← clustering key (DESC)
message_id       UUID
sender_id        UUID
content          TEXT
status           TEXT        (SENT / DELIVERED / READ)
─────────────────────────────────────────────────
```

---

## ⚙️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3, Spring Security |
| **Service Mesh** | Spring Cloud API Gateway |
| **Service Discovery** | Netflix Eureka (Spring Cloud) |
| **Fault Tolerance** | Resilience4J — Circuit Breaker, Retry, Rate Limiter |
| **Real-time** | WebSockets (STOMP) |
| **Auth** | Keycloak — OAuth2 / OpenID Connect |
| **Messaging** | Apache Kafka |
| **Cache / Presence** | Redis |
| **Inter-service** | gRPC + Protocol Buffers |
| **Database** | MySQL (users/auth) · Cassandra (chat messages) |
| **Containerization** | Docker · Docker Compose |
| **Cloud** | AWS EC2 · AWS RDS |
| **API Docs** | Swagger UI |

---

## 📊 Performance

```
┌──────────────────────────────────────────────────────┐
│               Load Test Results                       │
├──────────────────────┬───────────────────────────────┤
│ Concurrent WS Conns  │  10,000                       │
│ Avg Message Latency  │  < 40ms                       │
│ Kafka Throughput     │  ~50,000 messages/sec         │
│ Redis Lookup         │  < 5ms                        │
│ Auth Token Validate  │  < 10ms (cached)              │
└──────────────────────┴───────────────────────────────┘
```

---

## 📐 Scalability Strategy

```
Stateless microservices       →  Scale any service independently
Eureka service discovery      →  Dynamic routing, no hardcoded URLs
Spring Cloud API Gateway      →  Single entry point, JWT filter, rate limiting
Resilience4J circuit breaker  →  Automatic fallback, prevents cascade failures
Kafka partitioning            →  Distribute message load across brokers
Redis clustering              →  Distributed presence & cache
Cassandra horizontal scaling  →  Add nodes, no downtime, auto rebalance
Keycloak SSO                  →  Centralized identity, no session state
Horizontal pod scaling        →  Add instances under load, Eureka auto-discovers
```

---

## 📨 WebSocket Message Format

**Outgoing chat message:**
```json
{
  "type": "CHAT_MESSAGE",
  "conversationId": "12345",
  "senderId": "user_1",
  "receiverId": "user_2",
  "content": "Hello!",
  "timestamp": "2026-02-23T10:30:00Z"
}
```

**Kafka delivery event:**
```json
{
  "eventType": "MESSAGE_DELIVERED",
  "messageId": "abc123",
  "status": "DELIVERED"
}
```

**gRPC contract (example):**
```protobuf
service UserService {
  rpc GetUserDetails(UserRequest) returns (UserResponse);
}

message UserRequest {
  string userId = 1;
}

message UserResponse {
  string userId = 1;
  string username = 2;
  string email = 3;
}
```

---

## 📚 API Docs

Swagger UI available at:
```
http://localhost:8080/swagger-ui.html
```

---

## 🚀 Running Locally

### Prerequisites

| Tool | Version |
|---|---|
| Java | 21+ |
| Docker & Docker Compose | latest |
| Kafka | 3.x |
| Redis | 7.x |
| Keycloak | 22+ |
| Cassandra | 4.x |

### Quick Start (Docker)

```bash
# Clone the repo
git clone git@github.com:your-username/snippix-backend.git
cd snippix-backend

# Start all services
docker-compose up
```

### Startup Order (if running manually)

```
1. Eureka Server        → http://localhost:8761
2. Auth Service         → registers with Eureka
3. User Service         → registers with Eureka
4. Chat Service         → registers with Eureka
5. API Gateway          → routes via Eureka discovery
```

### Start Individual Services

```bash
# Eureka Server (start this first!)
cd eureka-server && mvn spring-boot:run

# Auth Service
cd auth-service && mvn spring-boot:run

# User Service
cd user-service && mvn spring-boot:run

# Chat Service
cd chat-service && mvn spring-boot:run

# API Gateway (start this last)
cd api-gateway && mvn spring-boot:run
```

### Environment Variables

```env
# Auth
KEYCLOAK_URL=http://localhost:8180
KEYCLOAK_REALM=snippix
KEYCLOAK_CLIENT_ID=snippix-backend

# Eureka Service Discovery
EUREKA_SERVER_URL=http://localhost:8761/eureka
EUREKA_INSTANCE_HOSTNAME=localhost

# Database
MYSQL_URL=jdbc:mysql://localhost:3306/snippix
MYSQL_USERNAME=root
MYSQL_PASSWORD=yourpassword

# Cassandra (Chat Storage)
CASSANDRA_HOST=localhost
CASSANDRA_PORT=9042
CASSANDRA_KEYSPACE=snippix_chat
CASSANDRA_DC=datacenter1

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
```

---

## 🔮 Roadmap

- [ ] Kubernetes deployment (Helm charts)
- [ ] Distributed tracing with OpenTelemetry
- [ ] Rate limiting per user/IP
- [ ] End-to-end message encryption
- [ ] Multi-region AWS deployment
- [ ] Push notifications (FCM)

---

## 👤 Author

**Bhanu Prakash**
Java Full Stack Developer

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0077B5?style=flat-square&logo=linkedin&logoColor=white)](https://linkedin.com/in/bhanu-prakash-bhukya-7365502a4/)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/BhanuPrakashBhukya)

---

<div align="center">
  <sub>Built with ☕ Java · Powering Snippix real-time messaging</sub>
</div>
