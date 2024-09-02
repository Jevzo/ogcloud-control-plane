package de.ogwars.ogcloud.exception

class ServiceFoundException(name: String) : RuntimeException("Service with name $name already exists!")
