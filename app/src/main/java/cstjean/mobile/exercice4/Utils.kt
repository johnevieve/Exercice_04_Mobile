package cstjean.mobile.exercice4

import android.location.Location
fun Location?.toText():String {
    return if (this != null) {
        "$latitude, $longitude"
    } else {
        "Unknown location"
    }
}