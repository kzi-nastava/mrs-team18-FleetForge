# Fleet Forge - Ride-Hailing System

**Fleet Forge** is a full-stack, Uber-like ride-hailing platform developed as an integrated project across four university courses: *Inženjerstvo klijentskog sloja*, *Inženjerstvo serverskog sloja*, *Mobilne aplikacije*, and *Testiranje softvera*. The system facilitates seamless interaction between passengers, drivers, and administrators, focusing on reducing interaction with providers to ensure a safer and more consistent transport process.


---

## 👥 Authors

- [**Vukašin Vitomirović**](https://github.com/Kakodane)  
- [**Miloš Damjanović**](https://github.com/pifchina)  
- [**Ognjen Vujović**](https://github.com/ognjenvujovic04)

---

## 🛠️ Tech Stack & Architecture

The project follows a modern distributed architecture, ensuring separation of concerns across web, mobile, and server layers.

### ⚙️ Backend – Spring Boot REST API
* **Java & Spring Boot:** Core framework for building the serverski sloj.
* **Spring Security (JWT):** Implementation of secure logging and registration via JSON Web Tokens.
* **JPA / Hibernate:** For object-relational mapping with the database.
* **H2:** Relational databases used for data persistence.
* **JUnit & Mockito:** Frameworks used for Unit and Integration testing.
* **SendGrid:** Integration for automated email activation and notifications.

**Core Backend Responsibilities:**
* **Authentication & Role-Based Access:** Managing Passenger, Driver, and Admin roles.
* **Ride Assignment Algorithm:** Logic for selecting the nearest free driver or those finishing current rides.
* **Safety Logic:** Enforcement of the 8-hour daily work limit for drivers.
* **Price Calculation:** Automated calculation based on distance and vehicle type.

### 💻 Frontend – Angular Web App
* **Angular (TS, HTML, CSS):** Core framework for the klijentski sloj.
* **Angular Material:** Libraries used for responsive UI components.
* **Leaflet (OpenStreetMap):** Integration for interactive maps and vehicle tracking.
* **Angular AuthGuard:** Used for securing routes based on user authentication state.
* **Figma:** Tool used for prototyping the UI/UX design.

### 📱 Mobile Application – Android (Java)
* **Java:** Native programming language for the Android application.
* **Material Design 3:** Library used for modern UI components.
* **OpenStreetMap:** Services for maps and location tracking.
* **Android Notification System:** Native system for real-time ride alerts.
* **SharedPreferences:** Used for storing application settings locally.

**Mobile-Specific Features:**
* **Multi-Layer Architecture:** Organized into UI, Domain, and Data layers.
* **Sensor Integration:** Use of the accelerometer for "Shake" gesture.
* **Real-time Navigation:** Always-visible navigation bar for easy access to features.

---

## 🧪 Quality Assurance & Testing

The project includes a dedicated focus on software testing to validate the robustness of the system.

* **Backend Testing:** Unit and integration tests for core logic and API endpoints.
* **Frontend Testing:** Validation of Angular components and user flows.
* **E2E Testing:** Automated browser testing using **Selenium** to verify complex scenarios like the registration-to-ride lifecycle.
