package com.ifscfinder.domain.engine

data class UnitDef(val id: String, val label: String, val toBase: Double)

enum class UnitCategory(val displayName: String, val units: List<UnitDef>) {
    LENGTH("Length", listOf(
        UnitDef("m", "Meter", 1.0),
        UnitDef("km", "Kilometer", 1000.0),
        UnitDef("cm", "Centimeter", 0.01),
        UnitDef("mm", "Millimeter", 0.001),
        UnitDef("ft", "Foot", 0.3048),
        UnitDef("in", "Inch", 0.0254),
        UnitDef("mi", "Mile", 1609.344),
        UnitDef("yd", "Yard", 0.9144)
    )),
    WEIGHT("Weight", listOf(
        UnitDef("kg", "Kilogram", 1.0),
        UnitDef("g", "Gram", 0.001),
        UnitDef("mg", "Milligram", 0.000001),
        UnitDef("lb", "Pound", 0.45359237),
        UnitDef("oz", "Ounce", 0.028349523125),
        UnitDef("ton", "Metric Ton", 1000.0)
    )),
    TEMPERATURE("Temperature", listOf(
        UnitDef("C", "Celsius", 1.0),
        UnitDef("F", "Fahrenheit", 1.0),
        UnitDef("K", "Kelvin", 1.0)
    )),
    AREA("Area", listOf(
        UnitDef("sqm", "Sq Meter", 1.0),
        UnitDef("sqft", "Sq Foot", 0.09290304),
        UnitDef("acre", "Acre", 4046.8564224),
        UnitDef("ha", "Hectare", 10000.0),
        UnitDef("sqkm", "Sq Kilometer", 1_000_000.0)
    )),
    VOLUME("Volume", listOf(
        UnitDef("l", "Liter", 1.0),
        UnitDef("ml", "Milliliter", 0.001),
        UnitDef("gal", "US Gallon", 3.785411784),
        UnitDef("cup", "US Cup", 0.2365882365),
        UnitDef("cbm", "Cubic Meter", 1000.0)
    )),
    SPEED("Speed", listOf(
        UnitDef("mps", "m/s", 1.0),
        UnitDef("kph", "km/h", 1.0 / 3.6),
        UnitDef("mph", "mph", 0.44704),
        UnitDef("knot", "Knot", 0.514444)
    ))
}

object UnitConverterEngine {
    fun convert(value: Double, from: UnitDef, to: UnitDef, category: UnitCategory): Double {
        if (category == UnitCategory.TEMPERATURE) return convertTemperature(value, from.id, to.id)
        val base = value * from.toBase
        return base / to.toBase
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        val celsius = when (from) {
            "C" -> value
            "F" -> (value - 32) * 5 / 9
            "K" -> value - 273.15
            else -> value
        }
        return when (to) {
            "C" -> celsius
            "F" -> celsius * 9 / 5 + 32
            "K" -> celsius + 273.15
            else -> celsius
        }
    }
}
