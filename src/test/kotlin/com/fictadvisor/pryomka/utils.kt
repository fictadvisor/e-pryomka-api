package com.fictadvisor.pryomka

import org.mockito.Mockito

internal inline fun <reified T> any(): T = Mockito.any(T::class.java)
