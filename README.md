# Story App — Android (Kotlin)

A social media-style Android application built with Kotlin that allows users to capture photos, share "stories" with location data, and view them on an interactive map.

## Features
- **User Authentication:** Registration, login, and session token persistence.
- **Story Feed & Pagination:** Efficient feed rendering with pagination support.
- **Story Creation:** Image capture via camera or gallery picker with compression.
- **Location Mapping:** Geolocation visualization powered by Google Maps API.

## Tech Stack & Architecture
- **Language:** Kotlin
- **Architecture:** MVVM Pattern
- **Async & Concurrency:** Kotlin Coroutines & Flow / LiveData
- **Networking:** Retrofit2 & OkHttp3
- **Local Storage:** Room Database & Preferences DataStore
- **Testing:** Unit Testing with JUnit

## Configuration
Add your Google Maps API Key to `local.properties`:
```properties
MAPS_API_KEY=YOUR_ACTUAL_API_KEY
