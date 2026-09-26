package com.internship.classai.ui.payments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internship.classai.data.model.ClassItem
import com.internship.classai.data.model.SectionItem
import com.internship.classai.data.model.Student
import com.internship.classai.data.model.Due
import com.internship.classai.data.model.PaymentRecord
import com.internship.classai.data.remote.RetrofitClient
import com.internship.classai.data.repository.ClassAIRepository
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {

    private val repository =
        ClassAIRepository(RetrofitClient.apiService)

    var classes by mutableStateOf<List<ClassItem>>(emptyList())
        private set

    var sections by mutableStateOf<List<SectionItem>>(emptyList())
        private set

    var students by mutableStateOf<List<Student>>(emptyList())
        private set

    var dues by mutableStateOf<List<Due>>(emptyList())
        private set

    var lastPayment by mutableStateOf<PaymentRecord?>(null)
        private set

    var selectedClass by mutableStateOf<ClassItem?>(null)
        private set

    val filteredSections: List<SectionItem>
        get() = selectedClass?.let { selected ->
            sections.filter {
                it.classId == selected.id
            }
        } ?: emptyList()

    var selectedSection by mutableStateOf<SectionItem?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var isAdvancedSearch by mutableStateOf(false)
        private set

    val isSearching: Boolean
        get() = searchQuery.isNotBlank()

    val searchResults: List<Student>
        get() {
            val query = searchQuery.trim().lowercase()

            if (query.isBlank()) {
                return emptyList()
            }

            return students.filter { student ->
                val firstName =
                    student.name
                        .substringBefore(" ")
                        .lowercase()

                val studentId =
                    student.studentId
                        .removePrefix("STI-")

                firstName.startsWith(query) ||
                        studentId.startsWith(query)
            }.sortedBy {
                it.name
            }
        }

    val browseStudents: List<Student>
        get() {
            val currentClass = selectedClass ?: return emptyList()
            val currentSection = selectedSection ?: return emptyList()

            return students.filter {
                it.classId == currentClass.id &&
                        it.sectionId == currentSection.id
            }.sortedBy {
                it.name
            }
        }

    var selectedStudent by mutableStateOf<Student?>(null)

    var selectedDue by mutableStateOf<Due?>(null)
        private set

    var selectedDues by mutableStateOf<List<Due>>(emptyList())
        private set

    val selectedDuesTotal: Int
        get() = selectedDues.sumOf {
            it.netAmount
        }

    val selectedStudentDues: List<Due>
        get() = selectedStudent?.let { student ->
            dues.filter {
                it.studentId == student.id
            }
        } ?: emptyList()

    val studentsWithPendingDues: List<Student>
        get() = students.filter { student ->
            dues.any {
                it.studentId == student.id
            }
        }.sortedBy {
            it.name
        }

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                classes = repository.getClasses()
                sections = repository.getSections()
                students = repository.getStudents()

                selectedClass = classes.firstOrNull()
                selectedSection = filteredSections.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadStudentDues(
        studentId: Int
    ) {
        viewModelScope.launch {
            try {
                dues = repository.getStudentDues(studentId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setNormalSearch() {
        isAdvancedSearch = false
    }

    fun setAdvancedSearch() {
        isAdvancedSearch = true
    }

    fun selectClass(
        classItem: ClassItem
    ) {
        selectedClass = classItem

        selectedSection = sections.firstOrNull {
            it.classId == classItem.id
        }

        selectedStudent = null
        selectedDue = null
        selectedDues = emptyList()
        searchQuery = ""
    }

    fun selectSection(
        sectionItem: SectionItem
    ) {
        selectedSection = sectionItem
        selectedStudent = null
        selectedDue = null
        selectedDues = emptyList()
        searchQuery = ""
    }

    fun selectStudent(
        student: Student
    ) {
        selectedStudent = student
        selectedDue = null
        selectedDues = emptyList()

        selectedClass = classes.firstOrNull {
            it.id == student.classId
        }

        selectedSection = sections.firstOrNull {
            it.id == student.sectionId
        }

        searchQuery = student.name
        loadStudentDues(student.id)
    }

    fun selectDue(
        due: Due?
    ) {
        selectedDue = due
    }

    fun toggleDueSelection(
        due: Due
    ) {
        val studentDues = selectedStudentDues.sortedBy {
            it.dueDate
        }

        val dueIndex = studentDues.indexOf(due)

        if (dueIndex == -1) {
            return
        }

        if (selectedDues.contains(due)) {
            selectedDues = selectedDues.filter { selected ->
                val selectedIndex = studentDues.indexOf(selected)
                selectedIndex < dueIndex
            }
        } else {
            selectedDues = studentDues.take(dueIndex + 1)
        }
    }

    // --------------------------------------------------
    // COLLECT PAYMENT
    // --------------------------------------------------

    suspend fun collectPayment(
        paymentMethod: String,
        paymentDate: String,
        remarks: String
    ): Boolean {

        val due =
            selectedDue
                ?: selectedDues.firstOrNull()
                ?: return false

        val student =
            selectedStudent
                ?: return false

        val className =
            classes.firstOrNull {
                it.id == student.classId
            }?.name ?: ""

        val sectionName =
            sections.firstOrNull {
                it.id == student.sectionId
            }?.name ?: ""

        return try {

            // 1. Let the unchanged parent website confirm the payment.
            // It generates the official receipt and stores it in its own
            // public/assets/receipts directory.
            val websiteResponse =
                repository.submitParentPayment(
                    dueId = due.id,
                    paymentDate = paymentDate,
                    paymentMode = paymentMethod,
                    remarks = remarks
                )

            if (!websiteResponse.isSuccessful) {
                return false
            }

            // 2. Read the already-confirmed record from our shared DB.
            // The backend does not perform the confirmation here; it only
            // returns the authoritative values created by the website.
            val response =
                repository.getConfirmedPayment(due.id)

            if (!response.success) {
                return false
            }

            val returnedTransactionNo =
                response.transaction_no
                    ?: throw IllegalStateException(
                        "Backend did not return transaction_no"
                    )

            val amount =
                response.amount
                    ?: throw IllegalStateException(
                        "Backend did not return amount"
                    )

            val penalty =
                response.penalty
                    ?: throw IllegalStateException(
                        "Backend did not return penalty"
                    )

            val waiver =
                response.waiver
                    ?: throw IllegalStateException(
                        "Backend did not return waiver"
                    )

            val netAmount =
                response.net_amount
                    ?: throw IllegalStateException(
                        "Backend did not return net_amount"
                    )

            lastPayment =
                PaymentRecord(
                    receiptNumber = returnedTransactionNo,
                    transactionId = returnedTransactionNo,
                    studentId = student.id,
                    studentName = student.name,
                    className = className,
                    sectionName = sectionName,
                    month = due.month,
                    amountPaid = amount,
                    penalty = penalty,
                    waiver = waiver,
                    netAmount = netAmount,
                    paymentMethod = paymentMethod,
                    paymentDate = paymentDate,
                    remarks = remarks,
                    collectedBy = "Admin",
                    timestamp = System.currentTimeMillis()
                )

            dues = dues.filter {
                it.id != due.id
            }

            selectedDue = null
            selectedDues = emptyList()
            selectedStudent = null
            searchQuery = ""
            isAdvancedSearch = false

            true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun updateSearch(
        query: String
    ) {
        searchQuery = query

        if (query.isBlank()) {
            selectedStudent = null
            selectedDue = null
            selectedDues = emptyList()
            isAdvancedSearch = false
        }
    }

    fun clearSearch() {
        searchQuery = ""
        selectedStudent = null
        selectedDue = null
        selectedDues = emptyList()
        isAdvancedSearch = false
    }
}
