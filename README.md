# Temperature Dashboard – Simulated Sensor Data with StateFlow & Coroutines

This project implements a **real-time temperature dashboard** in **Jetpack Compose (Material 3)** that simulates sensor data updates using **coroutines** and **StateFlow**.  
It visualizes live temperature readings, summary statistics, and a simple line chart, demonstrating reactive UI updates in Compose.

---

## Features
- **Simulated Temperature Streaming**  
  Generates random temperature readings between 65°F and 85°F every 2 seconds.
- **Reactive Data Flow**  
  Uses StateFlow to update UI components instantly as new readings arrive.
- **Summary Statistics**  
  Displays current, average, minimum, and maximum temperature values.
- **Dynamic Chart**  
  Simple line chart drawn using Compose’s Canvas API.
- **Recent Readings List**  
  Shows the last 20 temperature readings with timestamps.
- **Pause / Resume Streaming**  
  Toggle data generation on and off in real time.
- **Material 3 Design**  
  Fully built in Jetpack Compose with responsive layout and clean UI.

---

## Technologies Used
- Jetpack Compose (Material 3)
- ViewModel + StateFlow
- Kotlin Coroutines
- Canvas API (for chart)
- LazyColumn + Card + Button
