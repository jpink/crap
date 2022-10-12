package fi.papinkivi.crap.arduino

fun main() = Uno("COM3").setup {
    dallasTemperature().onChange {
            old, new -> println("$old -> $new °C")
    }
}()
