# spring-boot-delivery-tracker
📦 Package Delivery Tracker
A full-stack package delivery tracking system built with Java Spring Boot, MySQL, and a vanilla HTML/CSS/JavaScript frontend. Developed as the capstone project for CS 305 — Advanced Java at the University of New York Tirana (UNYT), Spring 2026.
The system simulates a real-world courier management platform with three distinct user roles, real-time status tracking, a background concurrency job, and a complete REST API — covering all 10 advanced Java requirements set by the course.

🎯 What It Does
The Package Delivery Tracker manages the complete lifecycle of a package — from creation by a company operator, through assignment to a delivery driver, to final delivery confirmation. Each stage is tracked in real time and visible to the customer through a public tracking portal.
Three user roles:

🔐 Company Admin — creates packages, assigns drivers, updates statuses, views reporting dashboard
🚛 Driver — logs in with personal credentials, views assigned packages, updates location and status in real time
👤 Customer — tracks packages by email address or tracking code, sees full status history and animated progress bar


⚙️ Technical Features
RequirementImplementationR1 CollectionsHashMap for package cache, ArrayList for status validation, TreeSet in GenericTrackerR2 GenericsGenericTracker<T extends Comparable<T>> with Predicate<T> filter methodR3 LambdasPredicate, Comparator, Consumer, and Function lambdas in service layerR4 Stream APIgroupingBy, counting, filter, sorted, collect in PackageServiceR5 Concurrency@Scheduled + ExecutorService.newFixedThreadPool(2) background job every 30sR6 JDBCRaw JDBC CRUD in JdbcReportService — INSERT, SELECT, UPDATE, DELETER7 JPA/ORMPackage, Driver, Hub entities with @ManyToOne, @PrePersist, @PreUpdateR8 REST API19 endpoints across 3 controllers — full JSON request/response handlingR9 Design PatternObserver Pattern — PackageObserver, StatusHistoryObserver, PackageEventManagerR10 ExceptionsPackageNotFoundException, InvalidStatusException, GlobalExceptionHandler

🏗️ Architecture
deliverytracker/
├── entity/          # JPA entities — Package, Driver, Hub
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic — streams, lambdas, observer calls
├── controller/      # REST API — PackageController, DriverController, HubController
├── concurrency/     # Background job — PackageStatusChecker (@Scheduled)
├── observer/        # Observer Pattern — interface + implementation + manager
├── exception/       # Custom exceptions + GlobalExceptionHandler
└── util/            # GenericTracker<T> (R2) + JdbcReportService (R6)

src/main/resources/
├── static/
│   ├── login.html       # Public tracking page
│   ├── admin.html       # Company Portal
│   ├── driver.html      # Driver Portal
│   ├── dashboard.html   # Customer dashboard
│   ├── track.html       # Package detail tracking
│   ├── style.css        # Dark theme UI
│   └── app.js           # REST API calls via fetch()
└── application.properties

🔄 Application Flow
1. Admin creates a package → TRK tracking code generated automatically
2. Admin assigns a driver → Driver status set to PICKED_UP
3. Driver logs in → sees assigned packages
4. Driver updates location → Observer records status change history
5. Customer logs in with email → sees live package status + progress bar
6. Background job runs every 30s → logs all IN_TRANSIT packages

🌐 REST API Endpoints
Package endpoints:
  POST   /api/packages                    → Create package
  GET    /api/packages                    → Get all packages
  GET    /api/packages/track/{code}       → Track by code
  GET    /api/packages/email/{email}      → Get by recipient email
  PUT    /api/packages/{code}/status      → Update status
  PUT    /api/packages/{code}/assign      → Assign driver
  GET    /api/packages/{code}/history     → Status change history
  GET    /api/packages/delayed            → Packages IN_TRANSIT 24h+
  GET    /api/packages/stats              → Count by status
  GET    /api/packages/report             → JDBC reporting dashboard
  DELETE /api/packages/{id}              → Delete package

Driver endpoints:
  POST   /api/drivers                     → Add new driver
  GET    /api/drivers                     → List all drivers
  GET    /api/drivers/available           → Available drivers only
  POST   /api/drivers/login              → Driver authentication
  PUT    /api/drivers/update-location    → Update package location
  GET    /api/drivers/{username}/packages → Driver's packages

Hub endpoints:
  POST   /api/hubs                        → Create hub
  GET    /api/hubs                        → List all hubs
  GET    /api/hubs/city/{city}           → Find hub by city

🛠️ Tech Stack
Backend:   Java 17+ · Spring Boot 4.0 · Spring Data JPA · Hibernate 7
Database:  MySQL 8.0 · HikariCP connection pooling
Build:     Maven
Frontend:  HTML5 · CSS3 · Vanilla JavaScript (fetch API)
Server:    Embedded Apache Tomcat 11
JSON:      Jackson 3.1

🚀 Getting Started
Prerequisites
✅ Java 17 or higher
✅ MySQL 8.0
✅ Maven 3.x
✅ IntelliJ IDEA (recommended)
Setup
1. Clone the repository
bashgit clone https://github.com/yourusername/delivery-tracker.git
cd delivery-tracker
2. Create the MySQL database
sqlCREATE DATABASE delivery_tracker;
3. Configure application.properties
propertiesspring.datasource.url=jdbc:mysql://localhost:3306/delivery_tracker
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080
4. Run the application
bashmvn spring-boot:run
Or open in IntelliJ and run DeliveryTrackerApplication.java
5. Open in browser
http://localhost:8080/login.html     → Customer tracking page
http://localhost:8080/admin.html     → Company Portal (admin/admin123)
http://localhost:8080/driver.html    → Driver Portal

👤 Default Credentials
Company Admin:
  Username: admin
  Password: admin123

Driver (create from admin panel first):
  Username: driver1
  Password: driver123

📁 Key Classes Explained
PackageService.java — Heart of the application. Contains all business logic including status transition validation using ArrayList.indexOf(), package caching with HashMap, stream-based statistics with groupingBy, and delayed package detection with chained filter() calls.
GenericTracker<T> — Reusable generic class demonstrating type bounds (T extends Comparable<T>), TreeSet for automatic sorting, and Predicate<T> for functional filtering.
PackageStatusChecker — Background job demonstrating concurrency. Uses @Scheduled(fixedRate=30000) for automatic execution and ExecutorService.newFixedThreadPool(2) for thread pool management.
Observer Pattern — PackageObserver interface, StatusHistoryObserver implementation, and PackageEventManager manager. Every status change is recorded automatically without modifying the service layer.
JdbcReportService — Demonstrates raw JDBC alongside JPA. Performs all four CRUD operations using PreparedStatement and try-with-resources for safe resource management.

📄 License
This project was developed for academic purposes as part of CS 305 — Advanced Java at UNYT Spring 2026.

👨‍💻 Author
Ruben Gjata
Advanced Java
University of New York Tirana (UNYT)

