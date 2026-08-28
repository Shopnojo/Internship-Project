package com.internship.classai.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    fun format(amount: Int): String {

        return NumberFormat.getNumberInstance(Locale("en", "IN"))
            .format(amount)

    }

}