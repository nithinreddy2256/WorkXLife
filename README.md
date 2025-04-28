#  WorkXLife — AI-Powered Job Portal

##  Overview
**WorkXLife** is an AI-powered job portal that connects job seekers with employers through intelligent matching algorithms.  
It enables candidates to find the best-suited jobs based on their skills and experience, while employers can post jobs and find qualified candidates efficiently.  
Built with a scalable **Microservices Architecture**, WorkXLife integrates **AI-driven job recommendations**, **real-time notifications** features.

---

##  Features
-  **Employee Module**: Register, create profiles, search and apply for jobs.
-  **Employer Module**: Post jobs, view applications, manage listings.
-  **Admin Module**: Monitor platform activity, manage users and jobs.
-  **AI-Powered Recommendations**: Personalized job suggestions based on candidate profiles.
-  **Real-Time Notifications**: Get notified instantly about job updates and applications.
-  **Secure Authentication**: JWT-based login, role-based access control.
-  **Microservices Architecture**: Highly modular, scalable, and independently deployable services.

---

##  Tech Stack
| Layer          | Technology Stack                                                            |
|----------------|-----------------------------------------------------------------------------|
| Backend        | Core Java, Servlets, JSP, Spring Boot (Microservices), Hibernate, REST APIs |
| Database       | MySQL                                                                       |
| AI/ML          | OpenAI API Integration (for job matching and resume parsing)                |
| Security       | Spring Security, JWT Authentication                                         |
| Architecture   | Microservices, API Gateway, Eureka Discovery Server                         |
| Messaging      | RabbitMQ (for real-time notifications)                                      |


---

##  Microservices Structure
- **api-gateway**: Handles routing and authentication forwarding.
- **service-registry**: Eureka server for service discovery.
- **authentication-service**: Manages user registration, login, and JWT tokens.
- **employee-service**: Handles employee (job seeker) profiles and applications.
- **employer-service**: Manages employers and job postings.
- **job-service**: CRUD operations for job listings and applications.
- **recommendation-service**: AI-based personalized job recommendations.
- **notification-service**: Send emails and real-time notifications.


##  Installation & Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL/PostgreSQL
- Postman (for API testing)

### Backend Setup
```bash
# Clone the repository
git clone https://github.com/nithinreddy2256/WorkXLife.git
cd workxlife

# Build and run each microservice separately
cd authentication-service
mvn spring-boot:run

cd employee-service
mvn spring-boot:run

cd employer-service
mvn spring-boot:run

# Repeat for all other services
