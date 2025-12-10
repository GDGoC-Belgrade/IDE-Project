# Running the Compose Project

This repository contains a simple Compose Multiplatform project. Follow the steps below to set it up and run it.

## 1. Clone the Repository

```bash
git clone https://github.com/USERNAME/REPOSITORY.git
cd REPOSITORY
```

## 2. Verify Java/Gradle Settings in IntelliJ IDEA

The project requires **Java 17**. Make sure IntelliJ is configured to use the correct Gradle JVM.

Open:
**File → Settings → Build, Execution, Deployment → Build Tools → Gradle**

Then set:

* **Gradle JVM:** `Java 17`

(This is the last option inside the Gradle settings panel — double‑check it.)

## 3. Run the Project

Once IntelliJ finishes syncing:

* Open the **Gradle** tool window
* Run:

  ```
  composeDesktop > run
  ```

Or simply run `Main.kt` using the green **Run** button.

## 4. Notes

* If you encounter errors like *"Unsupported class file major version"*, it means Gradle is not using Java 17.
* If the build behaves unexpectedly, try: **File → Invalidate Caches & Restart**.
