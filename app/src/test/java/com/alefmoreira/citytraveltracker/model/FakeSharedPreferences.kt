package com.alefmoreira.citytraveltracker.model

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class FakeSharedPreferences : SharedPreferences {

    val stringMap = HashMap<String, String>()
    val longMap = HashMap<String, Long>()

    inner class FakeEditor : Editor {
        override fun putString(key: String?, value: String?): Editor {
            stringMap[key ?: ""] = value ?: ""
            return this
        }

        override fun putStringSet(
            key: String?,
            values: MutableSet<String>?
        ): Editor {
            TODO("Not yet implemented")
        }

        override fun putInt(key: String?, value: Int): Editor {
            TODO("Not yet implemented")
        }

        override fun putLong(key: String?, value: Long): Editor {
            longMap[key ?: ""] = value
            return this
        }

        override fun putFloat(key: String?, value: Float): Editor {
            TODO("Not yet implemented")
        }

        override fun putBoolean(key: String?, value: Boolean): Editor {
            TODO("Not yet implemented")
        }

        override fun remove(key: String?): Editor {
            TODO("Not yet implemented")
        }

        override fun clear(): Editor {
            TODO("Not yet implemented")
        }

        override fun commit(): Boolean {
            return true
        }

        override fun apply() {
            TODO("Not yet implemented")
        }

    }

    override fun getAll(): MutableMap<String, *> {
        TODO("Not yet implemented")
    }

    override fun getString(key: String?, defValue: String?): String? {
        return stringMap.getOrDefault(key, defValue)
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        TODO("Not yet implemented")
    }

    override fun getInt(key: String?, defValue: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return longMap.getOrDefault(key, defValue)
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        TODO("Not yet implemented")
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(key: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun edit(): Editor {
        return this.FakeEditor()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("Not yet implemented")
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("Not yet implemented")
    }
}