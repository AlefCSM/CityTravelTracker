package com.example.citytraveltracker.model

import com.example.citytraveltracker.data.City
import com.example.citytraveltracker.data.Connection

class Route(var city: City, var connections: MutableList<Connection>)