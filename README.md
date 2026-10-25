# Smart Pantry Manager

## App Description

Smart Pantry Manager is an Android application developed in Java using Android Studio. It helps users manage ingredients stored in their pantry and discover recipes they can prepare with the ingredients they already have.

The application allows users to:
- Add, view, edit, and delete pantry ingredients.
- Record ingredient quantities and units.
- View suggested recipes based on available pantry ingredients.
- View recipe details and cooking instructions.
- Navigate between the pantry, suggested recipes, recipe details, and settings screens.

The recipe-matching feature only suggests recipes when all the required ingredients are available in the user's pantry. This helps users choose meals they can prepare without needing to buy missing ingredients.

## Technologies Used

- Programming language: Java
- IDE: Android Studio
- UI: XML layouts
- Database: SQLite
- Data access: SQLiteOpenHelper
- List display: ListView with a custom adapter
- Navigation: Android Intents and a navigation drawer

## Database Choice

SQLite was selected as the database for Smart Pantry Manager because it is built into Android and stores data locally on the device. It does not require an external database server, an internet connection, or additional database hosting.

SQLite is suitable for this application because pantry ingredients and recipe data can be stored and retrieved locally. The SQLiteOpenHelper class is used to create and manage the database. This approach makes the application practical for a student project and allows users to access their pantry data offline.

## Setup and Run Instructions

### Requirements

- Android Studio installed on your computer.
- The Smart Pantry Manager project source code.
- An Android emulator or a compatible Android device.
- The Android SDK required by the project.

### Installation

1. Clone or download this repository from GitHub.
2. Open Android Studio.
3. Select Open and choose the downloaded SmartPantryManager project folder.
4. Allow Android Studio to sync the Gradle project and download any required dependencies.
5. If prompted, install the Android SDK version required by the project.
6. Connect an Android device with USB debugging enabled, or start an Android emulator.
7. Select the app run configuration.
8. Click Run in Android Studio to build and install the application.
9. Once the application opens, use the navigation menu to access My Pantry, Suggested Recipes, and Settings & Profile.

### Using the Application

1. Open My Pantry from the navigation menu.
2. Add ingredients and enter valid quantities and units.
3. Edit or delete ingredients as needed.
4. Open **Suggested Recipes** to see recipes that match the ingredients currently in the pantry.
5. Select a suggested recipe to view its details and cooking instructions.

## Notes

- Pantry data is stored locally using SQLite.
- Recipe suggestions depend on the ingredients currently saved in the pantry.
- All required ingredients must be available for a recipe to be suggested.
- This project was developed for the Mobile App Development 700 practical assignment.
