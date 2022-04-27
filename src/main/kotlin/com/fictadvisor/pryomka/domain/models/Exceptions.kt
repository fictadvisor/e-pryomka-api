package com.fictadvisor.pryomka.domain.models

/** Exception that signals that user cannot access requested resource because he is not authorized. */
class Unauthorized : IllegalStateException("Can't find user")

/** Exception that signals that the requested resource was not found. */
class NotFound(message: String) : IllegalStateException(message)

/** Exception that signals that user cannot access requested resource because he has no right level of permission. */
class PermissionDenied(message: String) : IllegalStateException(message)

/** Exception that signals that resource already exists. */
class Duplicated(message: String) : IllegalStateException(message)

/** @throws Unauthorized */
fun unauthorized(): Nothing = throw Unauthorized()

/** @throws NotFound */
fun notfound(message: String): Nothing = throw NotFound(message)

/** @throws PermissionDenied */
fun permissionDenied(message: String): Nothing = throw PermissionDenied(message)

/** @throws Duplicated */
fun duplicate(message: String): Nothing = throw Duplicated(message)
