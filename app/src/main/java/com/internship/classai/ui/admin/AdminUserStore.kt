package com.internship.classai.ui.admin

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONObject

data class AdminAccount(
    val name: String,
    val adminId: String,
    val email: String,
    val isActive: Boolean = true
)

data class AdminEmployee(
    val name: String,
    val employeeId: String,
    val role: String,
    val isActive: Boolean = true
)

object AdminUserStore {

    private const val PREFS_NAME = "classai_admin_user_store"
    private const val ADMINS_KEY = "admins"
    private const val EMPLOYEES_KEY = "employees"
    private const val NEXT_ADMIN_NUMBER_KEY = "next_admin_number"
    private const val NEXT_EMPLOYEE_NUMBER_KEY = "next_employee_number"

    private lateinit var preferences: android.content.SharedPreferences

    private var initialized = false

    val admins = mutableStateListOf<AdminAccount>()

    val employees = mutableStateListOf<AdminEmployee>()

    private var nextAdminNumber = 4
    private var nextEmployeeNumber = 4

    fun initialize(context: Context) {

        if (initialized) return

        preferences = context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        loadData()

        initialized = true
    }

    private fun loadData() {

        admins.clear()
        employees.clear()

        val savedAdmins = preferences.getString(
            ADMINS_KEY,
            null
        )

        val savedEmployees = preferences.getString(
            EMPLOYEES_KEY,
            null
        )

        if (savedAdmins == null) {

            admins.addAll(
                listOf(
                    AdminAccount(
                        name = "Admin One",
                        adminId = "ADM001",
                        email = "admin1@classai.edu"
                    ),
                    AdminAccount(
                        name = "Admin Two",
                        adminId = "ADM002",
                        email = "admin2@classai.edu"
                    ),
                    AdminAccount(
                        name = "Admin Three",
                        adminId = "ADM003",
                        email = "admin3@classai.edu"
                    )
                )
            )

        } else {
            loadAdmins(savedAdmins)
        }

        if (savedEmployees == null) {

            employees.addAll(
                listOf(
                    AdminEmployee(
                        name = "Employee One",
                        employeeId = "EMP001",
                        role = "Accountant"
                    ),
                    AdminEmployee(
                        name = "Employee Two",
                        employeeId = "EMP002",
                        role = "Accountant"
                    ),
                    AdminEmployee(
                        name = "Employee Three",
                        employeeId = "EMP003",
                        role = "Employee"
                    )
                )
            )

        } else {
            loadEmployees(savedEmployees)
        }

        nextAdminNumber = preferences.getInt(
            NEXT_ADMIN_NUMBER_KEY,
            calculateNextAdminNumber()
        )

        nextEmployeeNumber = preferences.getInt(
            NEXT_EMPLOYEE_NUMBER_KEY,
            calculateNextEmployeeNumber()
        )

        saveData()
    }

    fun addAdmin(email: String) {

        val adminId = "ADM%03d".format(nextAdminNumber)

        nextAdminNumber++

        admins.add(
            AdminAccount(
                name = displayNameFromEmail(email),
                adminId = adminId,
                email = email
            )
        )

        saveData()
    }

    fun addEmployee(email: String) {

        val employeeId = "EMP%03d".format(nextEmployeeNumber)

        nextEmployeeNumber++

        employees.add(
            AdminEmployee(
                name = displayNameFromEmail(email),
                employeeId = employeeId,
                role = "Employee"
            )
        )

        saveData()
    }

    fun toggleAdmin(adminId: String) {

        val index = admins.indexOfFirst {
            it.adminId == adminId
        }

        if (index != -1) {

            admins[index] = admins[index].copy(
                isActive = !admins[index].isActive
            )

            saveData()
        }
    }

    fun toggleEmployee(employeeId: String) {

        val index = employees.indexOfFirst {
            it.employeeId == employeeId
        }

        if (index != -1) {

            employees[index] = employees[index].copy(
                isActive = !employees[index].isActive
            )

            saveData()
        }
    }

    private fun saveData() {

        val adminsJson = JSONArray()

        admins.forEach { admin ->

            adminsJson.put(
                JSONObject().apply {
                    put("name", admin.name)
                    put("adminId", admin.adminId)
                    put("email", admin.email)
                    put("isActive", admin.isActive)
                }
            )
        }

        val employeesJson = JSONArray()

        employees.forEach { employee ->

            employeesJson.put(
                JSONObject().apply {
                    put("name", employee.name)
                    put("employeeId", employee.employeeId)
                    put("role", employee.role)
                    put("isActive", employee.isActive)
                }
            )
        }

        preferences.edit()
            .putString(
                ADMINS_KEY,
                adminsJson.toString()
            )
            .putString(
                EMPLOYEES_KEY,
                employeesJson.toString()
            )
            .putInt(
                NEXT_ADMIN_NUMBER_KEY,
                nextAdminNumber
            )
            .putInt(
                NEXT_EMPLOYEE_NUMBER_KEY,
                nextEmployeeNumber
            )
            .apply()
    }

    private fun loadAdmins(json: String) {

        val array = JSONArray(json)

        for (i in 0 until array.length()) {

            val item = array.getJSONObject(i)

            admins.add(
                AdminAccount(
                    name = item.getString("name"),
                    adminId = item.getString("adminId"),
                    email = item.getString("email"),
                    isActive = item.optBoolean(
                        "isActive",
                        true
                    )
                )
            )
        }
    }

    private fun loadEmployees(json: String) {

        val array = JSONArray(json)

        for (i in 0 until array.length()) {

            val item = array.getJSONObject(i)

            employees.add(
                AdminEmployee(
                    name = item.getString("name"),
                    employeeId = item.getString("employeeId"),
                    role = item.getString("role"),
                    isActive = item.optBoolean(
                        "isActive",
                        true
                    )
                )
            )
        }
    }

    private fun calculateNextAdminNumber(): Int {

        return admins
            .mapNotNull {
                it.adminId
                    .removePrefix("ADM")
                    .toIntOrNull()
            }
            .maxOrNull()
            ?.plus(1)
            ?: 4
    }

    private fun calculateNextEmployeeNumber(): Int {

        return employees
            .mapNotNull {
                it.employeeId
                    .removePrefix("EMP")
                    .toIntOrNull()
            }
            .maxOrNull()
            ?.plus(1)
            ?: 4
    }

    private fun displayNameFromEmail(email: String): String {

        val username = email
            .substringBefore("@")
            .replace(".", " ")
            .replace("_", " ")
            .replace("-", " ")
            .trim()

        return username
            .split(" ")
            .filter {
                it.isNotBlank()
            }
            .joinToString(" ") { word ->

                word.replaceFirstChar {
                    it.uppercase()
                }
            }
    }
}