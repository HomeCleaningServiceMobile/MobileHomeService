# MVVM Architecture Structure

This Android project follows the **Model-View-ViewModel (MVVM)** architecture pattern using Android Architecture Components.

## 📁 Package Structure

```
com.example.prm_project/
├── ui/
│   ├── view/           # Activities, Fragments (UI Components)
│   │   └── MainActivity.java
│   └── viewmodel/      # ViewModels (UI Logic)
│       └── MainViewModel.java
├── data/
│   ├── model/          # Data Models/Entities
│   │   └── User.java
│   ├── repository/     # Repository Pattern (Single Source of Truth)
│   │   └── MainRepository.java
│   ├── local/          # Local Data Sources (Room Database)
│   │   ├── AppDatabase.java
│   │   ├── UserDao.java
│   │   └── LocalDataSource.java
│   └── remote/         # Remote Data Sources (API)
│       ├── ApiService.java
│       ├── RetrofitClient.java
│       └── RemoteDataSource.java
└── utils/              # Utility Classes
    ├── Constants.java
    └── NetworkUtils.java
```

## 🏗️ Architecture Components

### 1. **View Layer** (`ui/view/`)
- **Responsibility**: Handle UI interactions and display data
- **Components**: Activities, Fragments
- **Key Features**:
  - Uses Data Binding for automatic UI updates
  - Observes ViewModel LiveData
  - No business logic

### 2. **ViewModel Layer** (`ui/viewmodel/`)
- **Responsibility**: Manage UI-related data and business logic
- **Components**: ViewModel classes
- **Key Features**:
  - Survives configuration changes
  - Exposes data via LiveData
  - Communicates with Repository
  - No Android framework dependencies

### 3. **Model Layer** (`data/`)

#### **Repository** (`data/repository/`)
- **Responsibility**: Single source of truth for data
- **Key Features**:
  - Coordinates between local and remote data sources
  - Implements caching strategy
  - Handles data synchronization

#### **Local Data Source** (`data/local/`)
- **Responsibility**: Handle local database operations
- **Components**: Room Database, DAOs
- **Key Features**:
  - Offline data storage
  - Fast data access
  - Data persistence

#### **Remote Data Source** (`data/remote/`)
- **Responsibility**: Handle API calls and network operations
- **Components**: Retrofit interfaces, API services
- **Key Features**:
  - Network communication
  - Data fetching from server
  - Error handling

#### **Models** (`data/model/`)
- **Responsibility**: Define data structures
- **Components**: Entity classes, POJOs
- **Key Features**:
  - Room entities
  - Gson serialization
  - Data validation

## 🔄 Data Flow

```
View (Activity/Fragment) 
    ↕ (observes LiveData)
ViewModel 
    ↕ (calls repository methods)
Repository 
    ↕ (coordinates)
Local DataSource ←→ Remote DataSource
```

## 📚 Libraries Used

- **Architecture Components**:
  - ViewModel & LiveData
  - Room Database
  - Data Binding

- **Networking**:
  - Retrofit2
  - OkHttp3
  - Gson

- **Reactive Programming**:
  - RxJava3 (optional)

## 🎯 Benefits

1. **Separation of Concerns**: Each layer has a specific responsibility
2. **Testability**: Easy to unit test each component
3. **Maintainability**: Clean code structure
4. **Scalability**: Easy to add new features
5. **Configuration Changes**: ViewModel survives rotations
6. **Offline Support**: Local caching with Room
7. **Data Consistency**: Single source of truth pattern

## 🚀 Usage Example

```java
// In MainActivity
viewModel = new ViewModelProvider(this).get(MainViewModel.class);
binding.setViewModel(viewModel);

// Observe data changes
viewModel.getMessage().observe(this, message -> {
    // UI automatically updates via data binding
});

// Trigger data loading
viewModel.loadData();
```

## 🧪 Testing Strategy

- **Unit Tests**: ViewModel, Repository, Utilities
- **Integration Tests**: Repository with data sources
- **UI Tests**: Activities with Espresso

## 📝 Notes

- Always use Repository pattern for data access
- ViewModels should not hold references to Views
- Use LiveData for UI updates
- Handle loading states in ViewModel
- Implement proper error handling
- Use constants for configuration values 