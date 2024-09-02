package de.ogwars.ogcloud.exception

class GroupNotFoundException(group: String) : RuntimeException("Group with name $group not found.")
