# NearFix

A location-based repair service platform where customers can post device repair requests, receive bids from local service providers, and manage the entire repair process — from bidding to chat to reviews.

Built as a student CV project using Spring Boot, Thymeleaf, and PostgreSQL.

---

## Features

**For Customers**
- Register and log in as a customer
- Post repair requests with title, description, and media attachments (photos/videos)
- Requests are visible to service providers within a 12km radius using the Haversine formula
- View all bids placed on a request — see price, estimated hours, and provider star rating
- Accept a bid to move the request to ACCEPTED status
- Chat with the service provider after accepting a bid
- Mark the job as done once the device is returned
- Leave a 1-5 star review with a comment after job completion
- View active requests and order history separately on the dashboard
- Chat with admin at any time

**For Service Providers**
- Register and set up a shop profile (shop name and description)
- See only repair requests within 12km of their stored location
- Place one bid per request — with a price, estimated hours, and a message
- View accepted jobs and open chat with the customer
- View completed job history
- Chat with admin at any time

**For Admin**
- View all registered customers and service providers
- Enable or disable any user account
- Chat with any customer or provider directly

**General**
- Session-based authentication (no JWT)
- BCrypt password hashing
- Browser geolocation captured on login and stored in the database
- Haversine formula applied at the DB level via JPQL query
- Scheduled task runs every hour to mark expired PENDING requests as DESTROYED
- Request lifecycle: PENDING, ACCEPTED, DONE, DESTROYED

---

## Screenshot

![image alt](https://github.com/SneakySolo/NearFix/blob/9fe3c20404c2bd413f5921aabeb664d7893d656d/1.PNG) 

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3 |
| Frontend | Thymeleaf, HTML, CSS |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Auth | Spring Session (HttpSession) |
| Password | BCrypt |
| Build | Maven |

---

## Project Structure

```
src/main/java/com/SneakySolo/nearfix/
├── config/
│   ├── AppConfig.java
│   ├── SessionFilter.java
│   └── WebConfig.java
├── controller/
│   ├── AuthController.java
│   ├── CustomerController.java
│   ├── ProviderController.java
│   ├── AdminController.java
│   ├── ChatController.java
│   ├── ReviewController.java
│   ├── LocationController.java
│   └── HomeController.java
├── domain/
│   ├── user/
│   │   ├── User.java
│   │   └── Role.java
│   ├── bid/
│   │   ├── Bid.java
│   │   └── BidStatus.java
│   └── message/
│       └── Message.java
├── dto/
│   ├── RegisterDTO.java
│   ├── LoginDTO.java
│   ├── CreateRequestDTO.java
│   ├── PlaceBidDTO.java
│   └── CreateShopDTO.java
├── entity/
│   ├── RepairRequest.java
│   ├── RequestStatus.java
│   ├── RequestMedia.java
│   ├── RepairShop.java
│   ├── Review.java
│   └── AdminMessage.java
├── repository/
│   ├── UserRepository.java
│   ├── RepairRequestRepository.java
│   ├── BidRepository.java
│   ├── RepairShopRepository.java
│   ├── RequestMediaRepository.java
│   ├── MessageRepository.java
│   ├── ReviewRepository.java
│   └── AdminMessageRepository.java
└── service/
    ├── UserService.java
    ├── SessionService.java
    ├── RepairRequestService.java
    ├── BidService.java
    ├── RepairShopService.java
    ├── MessageService.java
    ├── ReviewService.java
    ├── AdminMessageService.java
    ├── LocationService.java
    └── SchedulerService.java
```

## Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL

### Setup

**1. Clone the repository**

```bash
git clone https://github.com/yourusername/nearfix.git
cd nearfix
```

**2. Create the database**

```sql
CREATE DATABASE nearfix;
```

**3. Configure application.yaml**

Edit src/main/resources/application.yaml:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nearfix
    username: your_postgres_username
    password: your_postgres_password
  jpa:
    hibernate:
      ddl-auto: update

app:
  upload:
    dir: /your/path/to/uploads
```

**4. Run the application**

```bash
mvn spring-boot:run
```

**5. Open in browser**

```
http://localhost:8080
```

---

## Key Implementation Details

### Location and 12km Radius

When a user logs in, their browser captures GPS coordinates via the Geolocation API and sends them to the backend silently. These are stored on the User row in the database.

Service providers only see requests within 12km. This filter happens at the database level using a JPQL query with the Haversine formula:

```java
SELECT r FROM RepairRequest r
WHERE r.status = 'PENDING'
AND (6371 * acos(
    LEAST(1.0, GREATEST(-1.0,
        cos(radians(:lat)) * cos(radians(r.customer.latitude)) *
        cos(radians(r.customer.longitude) - radians(:lng)) +
        sin(radians(:lat)) * sin(radians(r.customer.latitude))
    ))
)) <= 12
```

### Session-Based Auth

No Spring Security or JWT is used. Authentication is handled manually using HttpSession. A SessionFilter intercepts every request and checks for a USER_ID session attribute. Public URLs like /auth/login, /auth/register, and / are whitelisted.

### Request Lifecycle

```
Customer posts request   →   PENDING
Provider bids → Customer accepts   →   ACCEPTED
Customer marks done   →   DONE
Nobody accepts within 12 hours   →   DESTROYED   (via @Scheduled task)
```

### Chat

Chat between customer and provider is tied to a RepairRequest. Messages are stored in the database and the page polls for new messages every 3 seconds using a JavaScript fetch call to a fragment endpoint — no WebSockets needed.

Admin chat uses a separate AdminMessage entity so it is not tied to any specific request.

---

## What I Learned

- MVC architecture with Spring Boot — Controllers, Services, Repositories, DTOs
- Session-based authentication without Spring Security
- Spatial queries using the Haversine formula in JPQL
- File upload handling and serving static files from an external directory
- Thymeleaf templating — fragments, conditional rendering, form binding
- Scheduled background tasks with @Scheduled
- How servlet filters work — the foundation that Spring Security builds on

---

## Future Improvements

- Migrate authentication to Spring Security and JWT
- Add WebSocket support for real-time chat instead of polling
- Email notifications when a bid is placed or accepted
- Mobile responsive design
- Deploy to a cloud platform

---

## Author

**Kumar Aditya**
[LinkedIn]([https://linkedin.com/in/yourprofile](https://www.linkedin.com/in/kumar-aditya-567403278/))
