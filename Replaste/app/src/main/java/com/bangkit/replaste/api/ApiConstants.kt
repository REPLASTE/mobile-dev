package com.bangkit.replaste.api

object ApiConstants {
    const val BASE_URL = "https://replaste-1036434457572.asia-southeast2.run.app"
    const val PLASTIC_URL = "https://plastic-classifier-1036434457572.asia-southeast2.run.app"

    const val LOGIN = "/login"
    const val REGISTER = "/register"
    const val REQUEST_RESET = "/request-reset"
    const val HISTORY = "/predictions/{userId}"
    const val PROFILE_USER = "/getUserProfile/{userId}"
    const val CHANGE_PASSWORD_USER = "/updatePassword/{userId}"
    const val UPDATE_PROFILE_USER = "updateProfile/{userId}"
    const val LIST_TYPE_PLASTIC = "/getAllPlastik"


    const val PREDICT_PLASTIC = "/predict"

}