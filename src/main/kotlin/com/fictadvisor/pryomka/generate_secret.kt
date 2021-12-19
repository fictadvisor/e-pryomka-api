package com.fictadvisor.pryomka

import com.fictadvisor.pryomka.data.datasources.FsDocumentDataSource
import java.security.SecureRandom
import java.util.*

/** Generates secret master key for whole app.
 * Should be run only locally and produced result should be
 * saved in secure environment for deployment.
 **/
fun main() {
    val random = SecureRandom()
    val bytes = ByteArray(FsDocumentDataSource.KEY_SIZE / 8)
    random.nextBytes(bytes)

    val secret = Base64.getEncoder().encodeToString(bytes)

    println("GENERATED SECRET: '$secret'")
}
