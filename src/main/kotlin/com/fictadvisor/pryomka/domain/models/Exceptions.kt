package com.fictadvisor.pryomka.domain.models

class Unauthorized : IllegalStateException("Can't find user")
class NotFound(message: String) : IllegalStateException(message)
class PermissionDenied(message: String) : IllegalStateException(message)
class Duplicated(message: String) : IllegalStateException(message)

fun unauthorized(): Nothing = throw Unauthorized()
fun notfound(message: String): Nothing = throw NotFound(message)
fun permissionDenied(message: String): Nothing = throw PermissionDenied(message)
fun duplicate(message: String): Nothing = throw Duplicated(message)
