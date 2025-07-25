# 📋 MVVM Components Documentation

This document provides a detailed explanation of every component in the MVVM architecture, their purposes, and how they work together.

## 🏗️ Architecture Overview

The MVVM pattern separates the application into three main layers:
- **View**: UI components (Activities, Fragments)
- **ViewModel**: UI business logic and state management
- **Model**: Data handling (Repository, Data Sources, Models)

---

## 📱 VIEW LAYER (`ui/view/`)

### `MainActivity.java`
**Purpose**: Main UI component that displays data and handles user interactions.

**Key Responsibilities**:
- Initialize UI components using Data Binding
- Create and observe ViewModel
- Handle UI lifecycle events
- Respond to user interactions (indirectly through Data Binding)

**Key Methods**:
```java
- onCreate(): Set up data binding, initialize ViewModel, observe data changes
- observeViewModel(): Subscribe to LiveData changes from ViewModel
```

**Why It Exists**:
- Provides the user interface for the application
- Acts as the entry point for user interactions
- Maintains separation between UI and business logic

**Connections**:
- Observes `MainViewModel` via LiveData
- Uses Data Binding to connect XML layout to ViewModel
- No direct data access - all data comes through ViewModel

---

## 🧠 VIEWMODEL LAYER (`ui/viewmodel/`)

### `MainViewModel.java`
**Purpose**: Manages UI-related data and handles business logic for the MainActivity.

**Key Responsibilities**:
- Hold and manage UI state (message, loading status)
- Communicate with Repository layer
- Expose data to UI via LiveData
- Handle user actions (button clicks, data loading)
- Survive configuration changes (screen rotation)

**Key Components**:
```java
- MutableLiveData<String> message: Holds text to display
- MutableLiveData<Boolean> isLoading: Tracks loading state
- MainRepository repository: Access to data layer
```

**Key Methods**:
```java
- getMessage(): Exposes message LiveData to UI
- getIsLoading(): Exposes loading state to UI
- loadData(): Triggers data loading from repository
- updateMessage(): Updates displayed message
- onCleared(): Cleanup when ViewModel is destroyed
```

**Why It Exists**:
- Separates UI logic from UI components
- Provides lifecycle-aware data management
- Enables testable business logic
- Maintains state across configuration changes

**Connections**:
- Communicates with `MainRepository` for data operations
- Exposes data to `MainActivity` via LiveData
- No direct Android framework dependencies (except ViewModel base class)

---

## 📊 MODEL LAYER (`data/`)

## Repository (`data/repository/`)

### `MainRepository.java`
**Purpose**: Single source of truth for data operations. Coordinates between local and remote data sources.

**Key Responsibilities**:
- Decide whether to fetch data from local or remote sources
- Implement caching strategy
- Handle data synchronization
- Provide unified API for data access
- Manage data consistency

**Key Methods**:
```java
- getData(DataCallback): Get general data with caching logic
- getUser(int, UserCallback): Get user with local-first strategy
- saveUser(User): Save user data locally
- clearCache(): Clear local cache
```

**Key Interfaces**:
```java
- DataCallback: For string data operations
- UserCallback: For user data operations
```

**Why It Exists**:
- Implements Repository pattern for clean data access
- Provides offline-first capability
- Centralizes data access logic
- Abstracts data sources from ViewModels

**Connections**:
- Uses `LocalDataSource` for local data operations
- Uses `RemoteDataSource` for network operations
- Called by `MainViewModel` for all data needs

## Models (`data/model/`)

### `User.java`
**Purpose**: Data model representing a user entity with database and network annotations.

**Key Responsibilities**:
- Define user data structure
- Provide Room database entity mapping
- Handle JSON serialization/deserialization
- Validate data integrity

**Key Annotations**:
```java
- @Entity: Marks as Room database table
- @PrimaryKey: Defines primary key for database
- @SerializedName: Maps JSON fields to Java properties
```

**Key Properties**:
```java
- id: Unique identifier
- name: User's full name
- email: User's email address
- phone: User's phone number
- createdAt: Timestamp of creation
```

**Why It Exists**:
- Represents business data structure
- Enables type-safe data handling
- Provides database schema definition
- Ensures consistent data format across layers

**Connections**:
- Used by all data sources and repository
- Stored in Room database via `UserDao`
- Serialized for network operations

## Local Data Source (`data/local/`)

### `LocalDataSource.java`
**Purpose**: Handles all local data operations including database access and caching.

**Key Responsibilities**:
- Manage Room database operations
- Handle data caching
- Provide offline data access
- Execute database queries on background threads

**Key Methods**:
```java
- cacheData(String): Cache simple string data
- getCachedData(): Retrieve cached data
- saveUser(User): Save user to database
- getUser(int): Retrieve user by ID
- deleteUser(int): Remove user from database
- updateUser(User): Update existing user
```

**Why It Exists**:
- Provides offline data access
- Implements local caching strategy
- Handles database operations
- Ensures data persistence

**Connections**:
- Uses `AppDatabase` and `UserDao` for database operations
- Called by `MainRepository` for local data needs
- Works with `User` model for data operations

### `UserDao.java`
**Purpose**: Data Access Object interface that defines database operations for User entities.

**Key Responsibilities**:
- Define SQL queries for user operations
- Provide type-safe database access
- Handle CRUD operations
- Return LiveData for reactive UI updates

**Key Methods**:
```java
- getAllUsers(): Get all users as LiveData
- getUserById(int): Get specific user synchronously
- getUserByIdLiveData(int): Get specific user as LiveData
- insertUser(User): Insert single user
- insertUsers(List<User>): Insert multiple users
- updateUser(User): Update existing user
- deleteUser(User/int): Delete user
- getUserCount(): Get total user count
```

**Key Annotations**:
```java
- @Dao: Marks as Data Access Object
- @Query: Defines custom SQL queries
- @Insert, @Update, @Delete: CRUD operations
- @OnConflictStrategy.REPLACE: Handles conflicts
```

**Why It Exists**:
- Provides clean database API
- Enables compile-time SQL verification
- Supports reactive programming with LiveData
- Separates SQL from business logic

**Connections**:
- Used by `LocalDataSource` for database operations
- Works with `User` entities
- Part of `AppDatabase` Room setup

### `AppDatabase.java`
**Purpose**: Room database configuration and access point.

**Key Responsibilities**:
- Define database schema and version
- Provide DAO access
- Handle database creation and migration
- Implement singleton pattern for database instance

**Key Components**:
```java
- entities = {User.class}: Defines database tables
- version = 1: Database schema version
- INSTANCE: Singleton database instance
```

**Key Methods**:
```java
- getDatabase(Context): Get database instance
- userDao(): Access to UserDao
- destroyInstance(): Clean up database instance
```

**Why It Exists**:
- Configures Room database settings
- Provides centralized database access
- Manages database lifecycle
- Ensures single database instance

**Connections**:
- Contains `UserDao` for data operations
- Used by `LocalDataSource` for database access
- Manages `User` entity table

## Remote Data Source (`data/remote/`)

### `RemoteDataSource.java`
**Purpose**: Handles all network operations and API communications.

**Key Responsibilities**:
- Make HTTP requests to backend APIs
- Handle network errors and timeouts
- Process API responses
- Provide callback-based async operations
- Simulate network operations (in this implementation)

**Key Methods**:
```java
- fetchData(RemoteCallback): Get general data from API
- fetchUser(int, UserRemoteCallback): Get specific user
- postUser(User, UserRemoteCallback): Create new user
- updateUser(User, UserRemoteCallback): Update existing user
- deleteUser(int, RemoteCallback): Delete user
```

**Key Interfaces**:
```java
- RemoteCallback: For string-based responses
- UserRemoteCallback: For User-based responses
```

**Why It Exists**:
- Separates network logic from other components
- Provides consistent API for remote operations
- Handles network-specific concerns (timeouts, errors)
- Enables offline/online data strategy

**Connections**:
- Uses `ApiService` for actual network calls
- Uses `RetrofitClient` for HTTP client configuration
- Called by `MainRepository` for remote data needs

### `ApiService.java`
**Purpose**: Retrofit interface defining REST API endpoints.

**Key Responsibilities**:
- Define HTTP request methods and endpoints
- Specify request/response data types
- Handle URL parameters and request bodies
- Provide type-safe API calls

**Key Endpoints**:
```java
- GET /users: Retrieve all users
- GET /users/{id}: Get specific user
- POST /users: Create new user
- PUT /users/{id}: Update user
- DELETE /users/{id}: Delete user
- GET /users/search: Search users
```

**Key Annotations**:
```java
- @GET, @POST, @PUT, @DELETE: HTTP methods
- @Path: URL path parameters
- @Query: URL query parameters
- @Body: Request body data
```

**Why It Exists**:
- Defines contract between app and backend API
- Provides type-safe network operations
- Separates API definition from implementation
- Enables easy API testing and mocking

**Connections**:
- Used by `RemoteDataSource` for network calls
- Created by `RetrofitClient` configuration
- Works with `User` model for request/response

### `RetrofitClient.java`
**Purpose**: Configures and provides Retrofit HTTP client instance.

**Key Responsibilities**:
- Configure HTTP client settings (timeouts, logging)
- Set up JSON converters
- Provide singleton Retrofit instance
- Handle base URL configuration

**Key Configurations**:
```java
- Base URL: API server endpoint
- Timeouts: Connection, read, write timeouts
- Logging: HTTP request/response logging
- Gson Converter: JSON serialization/deserialization
```

**Key Methods**:
```java
- getInstance(): Get configured Retrofit instance
- setBaseUrl(String): Update API base URL
```

**Why It Exists**:
- Centralizes HTTP client configuration
- Provides reusable Retrofit setup
- Handles network-level concerns
- Enables easy configuration changes

**Connections**:
- Used by `RemoteDataSource` to create `ApiService`
- Configured with timeouts and interceptors
- Handles JSON conversion for API calls

---

## 🛠️ UTILITY LAYER (`utils/`)

### `Constants.java`
**Purpose**: Centralized storage for application-wide constants and configuration values.

**Key Responsibilities**:
- Define API configuration constants
- Store database configuration values
- Provide SharedPreferences keys
- Define request codes and error messages

**Key Constant Categories**:
```java
- API Constants: Base URL, timeouts
- Database Constants: Database name, version
- SharedPreferences Keys: User data keys
- Intent Keys: Data passing between components
- Request Codes: Activity result codes
- Error/Success Messages: User-facing messages
```

**Why It Exists**:
- Prevents magic numbers and strings
- Centralizes configuration management
- Enables easy configuration changes
- Improves code maintainability

**Connections**:
- Used throughout the application
- Referenced by database, network, and UI components
- Provides consistent configuration values

### `NetworkUtils.java`
**Purpose**: Utility methods for network connectivity checks and network-related operations.

**Key Responsibilities**:
- Check internet connectivity status
- Detect WiFi vs mobile data connection
- Provide network state information
- Helper methods for network operations

**Key Methods**:
```java
- isNetworkAvailable(Context): Check if internet is available
- isWifiConnected(Context): Check if connected to WiFi
- isMobileDataConnected(Context): Check if using mobile data
```

**Why It Exists**:
- Provides network status information
- Enables offline/online feature decisions
- Centralizes network utility functions
- Supports adaptive network behavior

**Connections**:
- Used by Repository and RemoteDataSource
- Helps determine when to make network calls
- Supports offline-first architecture decisions

---

## 🔄 Component Interaction Flow

### Data Loading Flow:
1. **User Action**: User taps "Load Data" button
2. **View**: MainActivity receives click via Data Binding
3. **ViewModel**: MainViewModel.loadData() is called
4. **Repository**: MainRepository.getData() coordinates data fetching
5. **Local Check**: LocalDataSource checks for cached data
6. **Remote Fetch**: If no local data, RemoteDataSource fetches from API
7. **Data Return**: Data flows back through Repository → ViewModel → View
8. **UI Update**: LiveData triggers automatic UI update via Data Binding

### User Management Flow:
1. **Request**: ViewModel requests user data via Repository
2. **Local First**: Repository checks LocalDataSource first
3. **Remote Fallback**: If not found locally, queries RemoteDataSource
4. **Caching**: Remote data is cached locally for future use
5. **Response**: User data flows back to ViewModel as LiveData
6. **UI Display**: View automatically updates when LiveData changes

---

## 🎯 Benefits of This Architecture

1. **Separation of Concerns**: Each component has a single, well-defined responsibility
2. **Testability**: Each layer can be unit tested independently
3. **Maintainability**: Clear structure makes code easy to understand and modify
4. **Scalability**: New features can be added following the same patterns
5. **Offline Support**: Local data sources provide offline functionality
6. **Configuration Changes**: ViewModels survive device rotations
7. **Reactive UI**: LiveData ensures UI stays in sync with data changes
8. **Clean Code**: Following SOLID principles and Android best practices

This architecture provides a solid foundation for building scalable, maintainable Android applications! 🚀