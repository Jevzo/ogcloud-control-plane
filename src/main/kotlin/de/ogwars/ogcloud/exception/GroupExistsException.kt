package de.ogwars.ogcloud.exception

class GroupExistsException(name: String) : RuntimeException("Group with name $name already exists!")
