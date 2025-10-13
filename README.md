# Yams App 🎯

A modern Kotlin Multiplatform app built with the latest architecture patterns, demonstrating clean code organization and fast development workflow.

## 🏗️ Architecture

This project follows a **modern, modularized architecture** based on the proven patterns from the Molkky Champion project:

- **Clean Architecture** with clear separation of concerns
- **Multi-module structure** for scalability and maintainability
- **Convention Plugins** for consistent build configuration
- **Feature-based organization** with shared core modules

## 📁 Project Structure

```
yams/
├── app/                    # Modern application entry point
├── build-logic/            # Convention plugins for build consistency
├── core/                   # Foundation modules
│   ├── database/          # Room database setup
│   ├── designsystem/      # Theme, colors, typography
│   ├── file/              # File handling abstractions
│   ├── model/             # Domain models
│   └── ui/                # Shared UI components
├── data/                   # Repository pattern implementations
│   └── user/              # User data management
├── feature/                # Self-contained UI features
│   ├── home/              # Home screen
│   ├── profile/           # Profile management
│   └── welcome/           # Onboarding
├── iosApp/                 # iOS Swift wrapper
└── gradle/                 # Version catalogs
```

## 🚀 Getting Started

### Prerequisites
- **JDK 17+**
- **Android Studio** (for Android development)
- **Xcode** (for iOS development)

### Running the App

#### 🖥️ Desktop (Recommended for Development)
```bash
# Regular run
./gradlew app:jvmRun

# With Hot Reload 🔥 (Recommended)
./gradlew app:hotRunJvmAsync
```

#### 📱 Android
```bash
./gradlew app:installDebug
```

#### 🍎 iOS
Open `iosApp/YamsApp.xcodeproj` in Xcode and run.

## 🔥 Development Workflow

### **Fast Development with Desktop + Hot Reload**

1. **Start the desktop app with hot reload:**
   ```bash
   ./gradlew app:hotRunJvmAsync
   ```

2. **Make changes to your UI code** - see them instantly without restart!

3. **Test on mobile** when needed for platform-specific behavior

### **Key Benefits:**
- ✅ **No emulator needed** - runs natively on desktop
- ✅ **Hot reload** - instant UI updates
- ✅ **Same business logic** - what you see is what you get
- ✅ **10x faster** than traditional mobile development

## 🎯 Platform Support

- **Android** (API 24+)
- **iOS** (iOS 13+)  
- **Desktop** (Windows, macOS, Linux)

## 🛠️ Technology Stack

- **UI Framework:** Compose Multiplatform
- **Architecture:** Clean Architecture + MVVM
- **Dependency Injection:** Koin
- **Database:** Room + SQLite
- **Navigation:** Jetpack Navigation Compose
- **Networking:** Ktor (ready to add)
- **Build System:** Gradle with Convention Plugins
- **Hot Reload:** Compose Hot Reload

## 🏗️ Build System

This project uses **Convention Plugins** for consistent build configuration:

- **`yams-kotlin-multiplatform`** - Standard KMP setup
- **`yams-compose-multiplatform`** - Compose + KMP setup  
- **`yams-feature`** - Feature module setup (KMP + Compose + dependencies)

### Adding New Modules

1. **Feature Module:**
   ```kotlin
   plugins {
       alias(libs.plugins.yams.feature)
   }
   ```

2. **Core/Data Module:**
   ```kotlin
   plugins {
       alias(libs.plugins.yams.kotlin.multiplatform)
   }
   ```

## 📦 Module Dependencies

The architecture enforces **unidirectional dependency flow**:

```
app → features → data → core
```

- **Features** depend on **Data** and **Core**
- **Data** depends only on **Core**  
- **Core** modules are independent
- **App** orchestrates everything

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :core:model:test
```

## 📈 Key Features

- 🚀 **Fast development** with desktop hot reload
- 🏗️ **Scalable architecture** with clear module boundaries
- 🔧 **Convention-based** build system
- 📱 **True multiplatform** - one codebase, all platforms
- 🎨 **Modern UI** with Material Design 3
- 🔒 **Type-safe navigation** with sealed route classes
- 💉 **Clean dependency injection** with Koin

### Theming
Customize colors and typography in `core:designsystem`.

---

**Built with ❤️ using Kotlin Multiplatform + Compose Multiplatform**

*This project demonstrates modern mobile development practices with maximum developer productivity and code sharing across platforms.*
