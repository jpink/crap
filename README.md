# Controller Remote Access Protocol (CRAP)

Kotlin multiplatform client library used to do remote procedure calls (RPC) to microcontrollers.

## Blink example
```kotlin
fun main() = with (Uno()) { // Connect the first available serial.
    led.mode = Mode.Output  // Calls pinMode(13, OUTPUT).
    var on = false
    while (true) {
        led.on = on         // Calls digitalWrite(LOW) on first time.
        Thread.sleep(1000)  // Caller is waiting a second.
        on = !on            // Switch boolean to call HIGH on next time.
    }
}
```

## Features
<img alt="Arduino logo" src="src/site/arduino-boobs.jpg" style="float:right"/>

- Arduino Uno support
  - USB serial communication
    - JVM implementation which uses [jSerialComm](https://github.com/Fazecast/jSerialComm)
    - Minimal bytes are transferred
    - Fast sketch flow control. Uses array of lambdas instead of if-else-switch-cases. 
