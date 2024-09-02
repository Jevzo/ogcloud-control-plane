package de.ogwars.ogcloud.exception

class StatefulSetFoundException(name: String) : RuntimeException("StateFul Set with name $name already exists!")
