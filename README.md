# Counter++ – Reactive UI with StateFlow & Coroutines

This project showcases a **reactive counter app** in **Jetpack Compose (Material 3)** that uses **StateFlow** for unidirectional data flow and **coroutines** for timed auto-increment behavior.  
The app demonstrates real-time state management with a clean, modern Compose UI.

---

## Features
- **Reactive Counter State**  
  Managed by a ViewModel using StateFlow for consistent, observable state updates.
- **Manual Controls**  
  Includes +1, –1, and Reset buttons to manually adjust the count.
- **Auto Mode**  
  Automatically increments the counter every few seconds when toggled on.
- **Coroutine-Based Background Logic**  
  Auto-increment implemented using a coroutine running in the ViewModel scope.
- **Settings Screen**  
  Allows configuration of the auto-increment interval (in seconds).
- **Material 3 UI**  
  Uses Scaffold, CenterAlignedTopAppBar, and Material styling for a polished look.

---

## Technologies Used
- Jetpack Compose (Material 3)
- ViewModel + StateFlow
- Kotlin Coroutines
- Scaffold + Buttons + Text
- Reactive unidirectional data flow
