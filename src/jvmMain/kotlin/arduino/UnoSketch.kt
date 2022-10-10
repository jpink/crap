package fi.papinkivi.crap.arduino

import java.io.File

fun main() {
    val sketch = Uno().buildSketch()
    println(sketch)
    File(Uno.SKETCH).writeText(sketch)
}
