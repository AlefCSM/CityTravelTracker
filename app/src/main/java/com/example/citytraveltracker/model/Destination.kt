package com.example.citytraveltracker.model

import com.example.citytraveltracker.data.City
import com.example.citytraveltracker.data.Connection

class Destination(city: City, var connections: List<Connection>) : Route(city) {


}