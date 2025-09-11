# Mobile Home Service App

This repository contains the source code for the **Mobile Home Service** Android application. The app provides booking, authentication, and staff management features for home services.

## 📦 Project Structure

```
app/
├── build.gradle.kts
├── google-services.json
├── proguard-rules.pro
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/
│   │   │   └── prm_project/
│   │   │       ├── data/
│   │   │       ├── ui/
│   │   │       ├── viewmodel/
│   │   │       └── utils/
│   │   └── res/
│   │       ├── anim/
│   │       ├── color/
│   │       ├── drawable/
│   │       ├── layout/
│   │       ├── menu/
│   │       ├── navigation/
│   │       ├── values/
│   │       ├── values-night/
│   │       └── xml/
│   ├── test/
│   └── androidTest/
```

## 🚀 Key Features

- **Authentication:** JWT-based login, registration, and profile management
- **Booking:** Service booking, history, and management
- **Staff Management:** Staff registration, assignment, and scheduling
- **MVVM Architecture:** Separation of concerns using ViewModel, Repository, and Model layers
- **Hilt Dependency Injection:** Automatic DI for repositories, viewmodels, and services
- **Navigation Component:** Fragment-based navigation for modern UI
- **Encrypted Storage:** Secure token and user data management

## 🛠️ Technologies Used

- **Kotlin/Java**
- **Android Jetpack (ViewModel, LiveData, Navigation)**
- **Hilt (DI)**
- **Retrofit (Networking)**
- **Room (Local Database)**
- **Material Design**

## 📚 Documentation

- [MVVM Architecture](../MVVM_ARCHITECTURE.md)
- [Hilt DI Guide](../HILT_DI_GUIDE.md)
- [Authentication Endpoints](../AUTH_ENDPOINTS_GUIDE.md)
- [Booking Endpoints](../BOOKING_ENDPOINTS_GUIDE.md)
- [Staff Endpoints](../STAFF_ENDPOINTS_GUIDE.md)
- [Customer Booking Endpoints](../CUSTOMER_BOOKING_ENDPOINTS_GUIDE.md)
- [Current Project Structure](../CURRENT_PROJECT_STRUCTURE.md)
- [Option B2 Implementation](../OPTION_B2_IMPLEMENTATION.md)

## ⚡ Getting Started

1. Clone the repository
2. Open in Android Studio
3. Add your Firebase `google-services.json` to `app/`
4. Sync Gradle and build the project

## 📝 License

This project is for educational and demonstration purposes.

---
