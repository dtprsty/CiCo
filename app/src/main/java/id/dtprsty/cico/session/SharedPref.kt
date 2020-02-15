package id.dtprsty.cico.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPref {

    private var _context: Context? = null
    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private val PRIVATE_MODE = 0
    private val PREF_NAME = "MyPreferences"

    private val KEY = "key"
    private val IS_CLOCKED_IN = "is_clocked_in"

    constructor(context: Context?) {
        _context = context
        pref = _context?.getSharedPreferences(
            PREF_NAME,
            PRIVATE_MODE
        )
    }

    fun clear() {
        pref?.edit {
            clear()
            apply()
        }
    }

    fun getKey(): String? {
        return pref?.getString(KEY, null)
    }

    fun setKey(key: String) {
        pref?.edit {
            putString(KEY, key)
        }
    }

    fun getIsClockedIn(): Boolean? {
        return pref?.getBoolean(IS_CLOCKED_IN, false)
    }

    fun setIsClockedIn(isClockedIn: Boolean) {
        pref?.edit {
            putBoolean(IS_CLOCKED_IN, isClockedIn)
        }
    }
}