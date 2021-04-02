package com.vportals.app.service

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val NAME = "VPortalsKotlin"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var prefs: SharedPreferences

    // list of app specific preferences
    private val IS_FIRST_RUN_PREF = Pair("is_first_run", false)
    private val IS_SIGN_IN = Pair("is_sign_in", false)
    private val FIRST_NAME = Pair("first_name", null)
    private val LAST_NAME = Pair("last_name", null)
    private val FULL_NAME = Pair("full_name", null)
    private val EMAIL_ID = Pair("email_id", null)
    private val PHONE_NO = Pair("phone_no", null)
    private val LOGO_URL = Pair("logo_url", null)
    private val A_NAME = Pair("a_name", null)
    private val SECTOR_ID = Pair("sector_id", null)
    private val AUTH_TOKEN = Pair("auth_token", null)

    fun init(context: Context) {
        prefs = context.getSharedPreferences(NAME, MODE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var firstRun: Boolean
        get() = prefs.getBoolean(IS_FIRST_RUN_PREF.first, IS_FIRST_RUN_PREF.second)
        set(value) = prefs.edit {
            it.putBoolean(IS_FIRST_RUN_PREF.first, value)
        }

    var signIn: Boolean
        get() = prefs.getBoolean(IS_SIGN_IN.first, IS_SIGN_IN.second)
        set(value) = prefs.edit {
            it.putBoolean(IS_SIGN_IN.first, value)
        }

    var firstName: String?
        get() = prefs.getString(FIRST_NAME.first, FIRST_NAME.second)
        set(value) = prefs.edit {
            it.putString(FIRST_NAME.first, value)
        }

    var lastName: String?
        get() = prefs.getString(LAST_NAME.first, LAST_NAME.second)
        set(value) = prefs.edit {
            it.putString(LAST_NAME.first, value)
        }

    var fullName: String?
        get() = prefs.getString(FULL_NAME.first, FULL_NAME.second)
        set(value) = prefs.edit {
            it.putString(FULL_NAME.first, value)
        }

    var emailId: String?
        get() = prefs.getString(EMAIL_ID.first, EMAIL_ID.second)
        set(value) = prefs.edit {
            it.putString(EMAIL_ID.first, value)
        }

    var phoneNo: String?
        get() = prefs.getString(PHONE_NO.first, PHONE_NO.second)
        set(value) = prefs.edit {
            it.putString(PHONE_NO.first, value)
        }

    var logoUrl: String?
        get() = prefs.getString(LOGO_URL.first, LOGO_URL.second)
        set(value) = prefs.edit {
            it.putString(LOGO_URL.first, value)
        }

    var aName: String?
        get() = prefs.getString(A_NAME.first, A_NAME.second)
        set(value) = prefs.edit {
            it.putString(A_NAME.first, value)
        }

    var sectorId: String?
        get() = prefs.getString(SECTOR_ID.first, SECTOR_ID.second)
        set(value) = prefs.edit {
            it.putString(SECTOR_ID.first, value)
        }

    var authToken: String?
        get() = prefs.getString(AUTH_TOKEN.first, AUTH_TOKEN.second)
        set(value) = prefs.edit {
            it.putString(AUTH_TOKEN.first, value)
        }

}