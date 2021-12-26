package com.fictadvisor.pryomka.domain.models

class Unauthorized : IllegalStateException("Can't find user")
class NotFound(msg: String) : IllegalStateException(msg)
class PermissionDenied(msg: String) : IllegalStateException(msg)
class Duplicated(msg: String) : IllegalStateException(msg)

fun unauthorized(): Nothing = throw Unauthorized()
fun notfound(msg: String): Nothing = throw NotFound(msg)
fun permissionDenied(msg: String): Nothing = throw PermissionDenied(msg)
fun duplicate(msg: String): Nothing = throw Duplicated(msg)
