package edu.iu.habahram.sensorssample.model

data class Compass(var heading: Float = 0F,
                   var oldHeading: Float = 0F,
                   val longitude: Float = 0F,
                   val latitude: Float = 0F,
                   val altitude: Float = 0F,
                   val magneticDeclination: Float = 0F,
                   var trueHeading: Float = 0F
)
