package fi.papinkivi.crap.arduino

fun main() = Uno("COM3").setup {
    switch(d3).onChange {
        println("Switch changed to $it.")
    }
}()
