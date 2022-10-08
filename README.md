# Controller Remote Access Protocol (CRAP)

Kotlin multiplatform client library used to do remote procedure calls (RPC) to microcontrollers.

## Blink example
```kotlin
fun main() = with (Uno()) { // Connects the first available serial and expects it to be Arduino Uno.
    led.mode = Mode.Output  // Calls pinMode(13, OUTPUT).
    while (connected) {
        led.on = !led.on    // Changes the LED state by calling digitalWrite(HIGH or LOW).
        Thread.sleep(1000)  // Caller is waiting for a second.
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
    - Sketch takes 34 % storage space and 34 % of memory on Uno.

## Limitations

- Interrupts can't be used, because they may cause asynchronous connection.
