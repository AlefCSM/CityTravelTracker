package com.alefmoreira.citytraveltracker.model

import com.alefmoreira.citytraveltracker.data.City
import com.alefmoreira.citytraveltracker.data.Connection

class Route(var city: City, var connections: MutableList<Connection>)