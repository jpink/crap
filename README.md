# Controller Remote Access Protocol (CRAP)

Kotlin multiplatform client library used to do remote procedure calls (RPC) to microcontrollers.

## Examples

### Blink

```kotlin
fun main() = with (Uno("COM3")) { // Connect COM3 serial port and expects it to be Arduino Uno.
    led.mode = Mode.Output        // Call `pinMode(13, OUTPUT)`.
    while (connected) {
        led.high = !led.high      // Change the LED state by calling `digitalWrite(HIGH)` or `LOW`.
        TimeUnit.SECONDS.sleep(1) // Caller is waiting for a second.
    }
}
```

### Relays
```kotlin
fun main() = with (Uno()) {       // Connect default serial port.
  with(attach4RelayShield()) {    // Setup pin modes for 4 Relay Shield.
    while (connected) {
      relays.shuffle()            // Choose random relay.
      relays.first().change()     // Activate or deactivate it.
      TimeUnit.SECONDS.sleep(1)
    }
  }
}
```


## Features
<img alt="Arduino logo" src="src/site/arduino-boobs.jpg" style="float:right"/>

- Arduino Uno support
  - USB serial communication
    - Minimal bytes are transferred
    - Fast sketch flow control. Uses array of lambdas instead of if-else-switch-cases.
    - Sketch takes 35 % of storage space and memory on Uno.
    - [jSerialComm](https://github.com/Fazecast/jSerialComm) JVM implementation.
- Arduino Relay Shield support

## Roadmap / Limitations

- Packages aren't published to Maven Central yet.
- Interrupt Service Routines can't be used, because they leave the connection in asynchronous state (now) and procedure pool is quite full.
- Interrupts can't be disabled because they don't compile inside lambda.

## How to use

1. Clone this repository using `git clone https://github.com/jpink/crap.git`.
2. Publish it to your local Maven using `./gradlew publishToMavenLocal`.
3. Edit your project's `build.gradle.kts` file:
   1. Add `mavenLocal()` to your repositories.
   2. Add `implementation("fi.papinkivi:crap:1.0.0")` to your `main` or `jvmMain` dependencies. 
