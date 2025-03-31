package com.imgoingonanadventure.di

import com.imgoingonanadventure.model.RouteIdToRouteMapper

interface MapperModule {
    val routeIdToRouteMapper: RouteIdToRouteMapper
}

class MapperModuleImpl : MapperModule {
    override val routeIdToRouteMapper: RouteIdToRouteMapper
        get() = RouteIdToRouteMapper()
}