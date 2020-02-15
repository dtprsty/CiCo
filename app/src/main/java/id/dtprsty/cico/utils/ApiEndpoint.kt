package id.dtprsty.cico.utils

import id.dtprsty.cico.BuildConfig

object ApiEndpoint {
    const val URL_LOGIN = BuildConfig.BASE_URL + "auth/login/"
    const val URL_GET_JOB = BuildConfig.BASE_URL + "staff-requests/26074/"
    const val URL_CLOCKED_IN = BuildConfig.BASE_URL + "staff-requests/26074/clock-in/"
    const val URL_CLOCKED_OUT = BuildConfig.BASE_URL + "staff-requests/26074/clock-out/"
}