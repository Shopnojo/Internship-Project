package com.internship.classai.ui.students

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.internship.classai.data.model.ClassItem
import com.internship.classai.data.model.SectionItem
import com.internship.classai.data.model.Student
import com.internship.classai.data.remote.RetrofitClient
import com.internship.classai.data.repository.ClassAIRepository
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.components.SearchField
import com.internship.classai.ui.components.ScreenHeader
import com.internship.classai.ui.components.StudentCard
import com.internship.classai.ui.components.FilterSection
import com.internship.classai.ui.theme.AppColors
import com.internship.classai.ui.payments.PaymentViewModel

@Composable
fun StudentsScreen(
    navController: NavHostController,
    paymentViewModel: PaymentViewModel
) {

    val repository = remember {
        ClassAIRepository(RetrofitClient.apiService)
    }

    // ==================================================
    // BACKEND DATA
    // ==================================================

    var students by remember {
        mutableStateOf<List<Student>>(emptyList())
    }

    var classes by remember {
        mutableStateOf<List<ClassItem>>(emptyList())
    }

    var sections by remember {
        mutableStateOf<List<SectionItem>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var loadError by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        try {

            students = repository.getStudents()
            classes = repository.getClasses()
            sections = repository.getSections()

            loadError = false

        } catch (e: Exception) {

            e.printStackTrace()

            loadError = true

        } finally {

            isLoading = false

        }

    }

    // ==================================================
    // NORMAL SEARCH
    // ==================================================

    var searchQuery by remember {
        mutableStateOf("")
    }

    var foundStudent by remember {
        mutableStateOf<Student?>(null)
    }

    var searchPerformed by remember {
        mutableStateOf(false)
    }

    var advancedSearch by remember {
        mutableStateOf(false)
    }

    // ==================================================
    // ADVANCED SEARCH
    // ==================================================

    var selectedClass by remember {
        mutableStateOf<ClassItem?>(null)
    }

    var selectedSection by remember {
        mutableStateOf<SectionItem?>(null)
    }

    var nameQuery by remember {
        mutableStateOf("")
    }

    /*
     * Advanced search is now based on a list.
     *
     * Any one of the following is enough:
     *
     * - Class
     * - Section
     * - Name
     *
     * Multiple filters can also be combined.
     */
    val advancedResults = remember(
        students,
        selectedClass,
        selectedSection,
        nameQuery
    ) {

        val hasFilter =
            selectedClass != null ||
                    selectedSection != null ||
                    nameQuery.trim().isNotEmpty()

        if (!hasFilter) {

            emptyList()

        } else {

            val query =
                nameQuery.trim()

            students.filter { student ->

                val classMatches =
                    selectedClass == null ||
                            student.classId ==
                            selectedClass!!.id

                val sectionMatches =
                    selectedSection == null ||
                            student.sectionId ==
                            selectedSection!!.id

                val nameMatches =
                    query.isEmpty() ||
                            student.name
                                .trim()
                                .contains(
                                    query,
                                    ignoreCase = true
                                )

                classMatches &&
                        sectionMatches &&
                        nameMatches
            }
        }
    }

    // ==================================================
    // ADVANCED SEARCH SECTION OPTIONS
    // ==================================================

    /*
     * If a class is selected, only sections belonging to
     * that class are shown.
     *
     * If no class is selected, all sections are available.
     *
     * This allows Section to be used as an independent
     * search field.
     */
    val availableSections =
        selectedClass?.let { currentClass ->

            sections.filter { section ->

                section.classId ==
                        currentClass.id

            }

        } ?: sections

    Scaffold(

        topBar = {

            AppToolbar(

                onMenuClick = {

                    navController.navigate(
                        Routes.SETTINGS
                    )

                }

            )

        }

    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(innerPadding)

        ) {

            ScreenHeader(

                title = "Students",

                subtitle = "Manage student records"

            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // ==================================================
            // LOADING
            // ==================================================

            if (isLoading) {

                CircularProgressIndicator(
                    modifier =
                        Modifier.padding(16.dp)
                )

                Text(

                    text =
                        "Loading student data...",

                    modifier =
                        Modifier.padding(
                            horizontal = 16.dp
                        ),

                    color =
                        AppColors.TextSecondary

                )

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

            }

            // ==================================================
            // BACKEND ERROR
            // ==================================================

            else if (loadError) {

                Text(

                    text =
                        "Unable to connect to the server.",

                    modifier =
                        Modifier.padding(
                            horizontal = 16.dp
                        ),

                    color =
                        AppColors.TextSecondary

                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(

                    text =
                        "Make sure the FastAPI server is running and the phone is connected to the same Wi-Fi network.",

                    modifier =
                        Modifier.padding(
                            horizontal = 16.dp
                        ),

                    color =
                        AppColors.TextSecondary

                )

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

            }

            // ==================================================
            // NORMAL STUDENT ID SEARCH
            // ==================================================

            else if (!advancedSearch) {

                SearchField(

                    searchQuery =
                        searchQuery,

                    onSearchQueryChanged = {

                        searchQuery = it

                        searchPerformed = false
                        foundStudent = null

                    },

                    onClearSearch = {

                        searchQuery = ""

                        searchPerformed = false
                        foundStudent = null

                    },

                    onFocusChanged = { },

                    onSearch = {

                        val query =
                            searchQuery.trim()

                        if (query.isNotEmpty()) {

                            paymentViewModel
                                .setNormalSearch()

                            foundStudent =
                                students.firstOrNull { student ->

                                    student.studentId
                                        .trim()
                                        .equals(
                                            query,
                                            ignoreCase = true
                                        )

                                }

                            searchPerformed = true

                        } else {

                            foundStudent = null
                            searchPerformed = false

                        }

                    }

                )

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )

                if (searchPerformed) {

                    if (foundStudent != null) {

                        StudentCard(

                            student =
                                foundStudent,

                            onClick = {

                                foundStudent?.let { student ->

                                    paymentViewModel
                                        .selectStudent(
                                            student
                                        )

                                    navController.navigate(
                                        Routes.PAYMENTS
                                    )

                                }

                            }

                        )

                    } else {

                        Text(

                            text =
                                "Student not found",

                            modifier =
                                Modifier.padding(
                                    horizontal = 16.dp
                                ),

                            color =
                                AppColors.TextSecondary

                        )

                    }

                }

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                Button(

                    onClick = {

                        advancedSearch = true

                        searchQuery = ""
                        foundStudent = null
                        searchPerformed = false

                        nameQuery = ""

                        selectedClass = null
                        selectedSection = null

                        paymentViewModel
                            .setAdvancedSearch()

                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                AppColors.PrimaryEnd,

                            contentColor =
                                AppColors.White

                        )

                ) {

                    Text(
                        text =
                            "Advanced Search"
                    )

                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

            }

            // ==================================================
            // ADVANCED SEARCH
            // ==================================================

            else {

                // ==================================================
                // FILTERS
                // ==================================================

                FilterSection(

                    classes =
                        classes,

                    sections =
                        availableSections,

                    selectedClass =
                        selectedClass,

                    selectedSection =
                        selectedSection,

                    onClassSelected = { classItem ->

                        selectedClass =
                            classItem

                        /*
                         * If the currently selected section
                         * doesn't belong to the newly selected
                         * class, clear it.
                         *
                         * We do NOT automatically select a
                         * section anymore.
                         */
                        if (
                            classItem == null ||
                            selectedSection?.classId !=
                            classItem.id
                        ) {

                            selectedSection = null

                        }

                        paymentViewModel
                            .setAdvancedSearch()

                    },

                    onSectionSelected = { sectionItem ->

                        selectedSection =
                            sectionItem

                        paymentViewModel
                            .setAdvancedSearch()

                    }

                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                // ==================================================
                // NAME SEARCH
                // ==================================================

                SearchField(

                    searchQuery =
                        nameQuery,

                    onSearchQueryChanged = {

                        nameQuery = it

                        paymentViewModel
                            .setAdvancedSearch()

                    },

                    onClearSearch = {

                        nameQuery = ""

                    },

                    onFocusChanged = { },

                    onSearch = {

                        paymentViewModel
                            .setAdvancedSearch()

                    },

                    placeholder =
                        "Enter Student Name"

                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                // ==================================================
                // RESULTS
                // ==================================================

                val hasAdvancedFilter =
                    selectedClass != null ||
                            selectedSection != null ||
                            nameQuery
                                .trim()
                                .isNotEmpty()

                if (!hasAdvancedFilter) {

                    Text(

                        text =
                            "Select a class, section, or enter a student name to search.",

                        modifier =
                            Modifier.padding(
                                horizontal = 16.dp
                            ),

                        color =
                            AppColors.TextSecondary

                    )

                } else if (advancedResults.isEmpty()) {

                    Text(

                        text =
                            "No students found.",

                        modifier =
                            Modifier.padding(
                                horizontal = 16.dp
                            ),

                        color =
                            AppColors.TextSecondary

                    )

                } else {

                    // ==================================================
                    // SCROLLABLE STUDENT RESULTS
                    // ==================================================

                    LazyColumn(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),

                        contentPadding =
                            androidx.compose.foundation.layout
                                .PaddingValues(
                                    bottom = 12.dp
                                )

                    ) {

                        items(

                            items =
                                advancedResults,

                            key = { student ->

                                student.id

                            }

                        ) { student ->

                            StudentCard(

                                student =
                                    student,

                                onClick = {

                                    paymentViewModel
                                        .selectStudent(
                                            student
                                        )

                                    navController.navigate(
                                        Routes.PAYMENTS
                                    )

                                }

                            )

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                        }

                    }

                }

                // ==================================================
                // BACK BUTTON
                // ==================================================

                Button(

                    onClick = {

                        advancedSearch = false

                        nameQuery = ""

                        selectedClass = null
                        selectedSection = null

                        foundStudent = null

                        paymentViewModel
                            .setNormalSearch()

                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                AppColors.PrimaryEnd,

                            contentColor =
                                AppColors.White

                        )

                ) {

                    Text(
                        text =
                            "Back to Student ID Search"
                    )

                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

            }

        }

    }

}