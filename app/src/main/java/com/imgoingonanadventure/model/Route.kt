package com.imgoingonanadventure.model


enum class Route(val fileName: String, val routeId: String) {
    BAG_END_TO_RIVENDELL("bag_end_to_rivendell.json", "0.0"),
    RIVENDELL_TO_LOTHLORIEN("rivendell_to_lothlorien.json", "0.1"),
    LOTHLORIEN_TO_RAUROS("lothlorien_to_rauros.json", "0.2"),
    RAUROS_TO_DOOM("rauros_to_doom.json", "0.3"),
}

object RouteSequence {
    val sequenceMain: List<Route> = listOf(
        Route.BAG_END_TO_RIVENDELL,
        Route.RIVENDELL_TO_LOTHLORIEN,
        Route.LOTHLORIEN_TO_RAUROS,
        Route.RAUROS_TO_DOOM
    )
}

class RouteIdToRouteMapper {
    operator fun invoke(routeId: String): Route {
        return when (routeId) {
            Route.BAG_END_TO_RIVENDELL.routeId -> Route.BAG_END_TO_RIVENDELL
            Route.RIVENDELL_TO_LOTHLORIEN.routeId -> Route.RIVENDELL_TO_LOTHLORIEN
            Route.LOTHLORIEN_TO_RAUROS.routeId -> Route.LOTHLORIEN_TO_RAUROS
            Route.RAUROS_TO_DOOM.routeId -> Route.RAUROS_TO_DOOM
            else -> Route.BAG_END_TO_RIVENDELL
        }
    }
}