Smart Pantry Manager

An Android application designed to help users efficiently manage their household food inventory, reduce waste, and discover recipe suggestions based on available pantry items.

Features

Pantry Inventory Management: Add, edit, and keep track of pantry items with quantity, units, and expiration dates.

Smart Recipe Matching: Discover recipes you can prepare based on the ingredients currently available in your pantry.

Pre-seeded Database: Pre-loaded with common household items and sample recipes for immediate testing.

Customizable Preferences: Manage app configurations and user settings easily via SharedPreferences.

Modern UI & Dark Theme: Responsive layouts supporting dynamic UI elements and night theme configurations.

Tech Stack & Dependencies

Language: Java

UI Architecture: XML Layouts, RecyclerView, Material Components

Database: SQLite (DatabaseHelper)

Storage: Android SharedPreferences

Minimum SDK: API Level 26 (Android 8.0)

Target SDK: API Level 34 / 35

IDE: Android Studio

Project Structure
app/src/main/
├── java/com/example/smartpantrymanager/
│   ├── MainActivity.java                # Core dashboard displaying pantry inventory list
│   ├── AddEditIngredientActivity.java   # Activity to add or update pantry items with validation
│   ├── SuggestedRecipesActivity.java   # Displays dynamic recipe recommendations
│   ├── RecipeDetailActivity.java      # Shows detailed recipe steps and ingredients
│   ├── SettingsActivity.java          # App settings and preferences management
│   ├── PantryAdapter.java             # RecyclerView adapter for pantry items
│   ├── PantryItem.java                # Data model for individual inventory items
│   ├── Recipe.java                    # Data model for recipe entities
│   └── DatabaseHelper.java            # SQLite helper for persistent storage and queries
└── res/
├── layout/                        # Activity layouts and list item card views
├── values/                        # Color themes, string resources, and styles
├── values-night/                  # Dark mode theme overrides
└── xml/                           # Preference schemas and configuration settings

Getting Started
Prerequisites

Android Studio (Ladybug or newer recommended)

Java Development Kit (JDK): Version 17 or higher

Android Device / Emulator: Running Android API Level 26+

Installation & Setup

Clone the Repository:

git clone [https://github.com/TondaniInData/SmartPantryManager.git](https://github.com/TondaniInData/SmartPantryManager.git)


Open Project in Android Studio:

Launch Android Studio.

Click Open and select the SmartPantryManager root project folder.

Build & Run:

Allow Gradle to download dependencies and sync automatically.

Run the application on an emulator or physical Android device by clicking Run (Shift + F10).

Testing

The repository includes unit tests and instrumentation tests:

Unit Tests: app/src/test/ (Tests core models and business logic)

UI & Instrumentation Tests: app/src/androidTest/ (Tests database integrity and activity navigation)

Run test suites using:

./gradlew test

Author

Tondani Sadiki

GitHub: @TondaniInData

Email: tondanisadiki20@gmail.com

