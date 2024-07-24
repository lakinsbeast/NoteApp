package ru.tfk.utils

fun checkTitleTextNotNull(text: String) = if (text.isEmpty()) "Без названия" else text

fun checkTextNotNull(text: String) = if (text.isEmpty()) "Нет дополнительного текста" else text
