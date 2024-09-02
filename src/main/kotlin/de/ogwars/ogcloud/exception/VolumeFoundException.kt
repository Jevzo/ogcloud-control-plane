package de.ogwars.ogcloud.exception

class VolumeFoundException(name: String) : RuntimeException("Volume with name $name already exists!")
