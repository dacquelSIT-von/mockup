
package com.example.mockup_mobapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

import com.example.mockup_mobapp.data.model.FoundItem
import com.example.mockup_mobapp.data.model.ItemStatus
import com.example.mockup_mobapp.data.model.ReportType
import com.example.mockup_mobapp.domain.repository.FoundItemRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun ReportItemScreen(
    repository: FoundItemRepository,
    onGoHome: () -> Unit
) {
    var showSuccessDialog by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    var itemName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var dateFound by remember { mutableStateOf(TextFieldValue("")) }
    var contactDetails by remember { mutableStateOf("") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var photoMessage by remember { mutableStateOf("") }
    var formMessage by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val categories = listOf(
        "Electronics",
        "Bags",
        "IDs",
        "Clothing",
        "Accessories",
        "Other"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Report a Found Item",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Provide details about the item you found.")

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Upload Photo")
                Text("Choose a clear photo of the found item.")

                OutlinedButton(
                    onClick = {
                        photoMessage =
                            "Photo picker will be connected next."
                    }
                ) {
                    Text("Choose Photo")
                }

                if (photoMessage.isNotEmpty()) {
                    Text(photoMessage)
                }
            }
        }

        OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name *") },
            placeholder = { Text("e.g., Black wallet") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Column {
            Text("Category *")

            Box {
                OutlinedButton(
                    onClick = {
                        categoryExpanded = true
                    }
                ) {
                    Text(
                        if (category.isEmpty()) {
                            "Choose Category"
                        } else {
                            category
                        }
                    )
                }

                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = {
                        categoryExpanded = false
                    }
                ) {
                    categories.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                category = option
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            placeholder = {
                Text("Color, brand, or other details")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Where Found *") },
            placeholder = {
                Text("Building / room / area")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = dateFound,
            onValueChange = { input ->

                val isDeleting =
                    input.text.length < dateFound.text.length

                val digits = input.text
                    .filter { it.isDigit() }
                    .take(8)

                val formattedDate = buildString {
                    digits.forEachIndexed { index, char ->
                        append(char)

                        if (
                            !isDeleting &&
                            (index == 3 || index == 5) &&
                            index < digits.lastIndex
                        ) {
                            append('/')
                        }
                    }
                }

                val digitsBeforeCursor = input.text
                    .take(input.selection.start)
                    .count { it.isDigit() }
                    .coerceAtMost(digits.length)

                val cursorPosition = digitsBeforeCursor +
                    (if (digitsBeforeCursor >= 4) 1 else 0) +
                    (if (digitsBeforeCursor >= 6) 1 else 0)

                dateFound = TextFieldValue(
                    text = formattedDate,
                    selection = TextRange(cursorPosition)
                )
            },
            label = { Text("Date Found *") },
            placeholder = { Text("YYYY/MM/DD") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = contactDetails,
            onValueChange = { contactDetails = it },
            label = { Text("Contact Details (Optional)") },
            placeholder = {
                Text("Safe contact or handover instructions")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Text(
            text = "Avoid sharing private phone numbers or sensitive personal information publicly.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            onClick = {
                formMessage = ""

                if (
                    itemName.isBlank() ||
                    category.isBlank() ||
                    location.isBlank() ||
                    dateFound.text.isBlank()
                ) {
                    formMessage =
                        "Please complete all required fields."
                } else {
                    coroutineScope.launch {
                        isSubmitting = true

                        try {
                            val newItem = FoundItem(
                                id = "item-${kotlin.random.Random.nextLong()}",
                                itemName = itemName.trim(),
                                category = category,
                                description = description.trim(),
                                location = location.trim(),
                                dateFound = dateFound.text.replace('/', '-'),
                                reportType = ReportType.FOUND,
                                status = ItemStatus.AVAILABLE
                            )

                            repository.addFoundItem(newItem)

                            showSuccessDialog = true

                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            formMessage =
                                "Unable to submit your report. Please check your connection and try again."
                        } finally {
                            isSubmitting = false
                        }
                    }
                }
            },
            enabled = !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submitting...")
            } else {
                Text("Submit Report")
            }
        }

        if (formMessage.isNotEmpty()) {
            Text(
                text = formMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
            },
            title = {
                Text("Report Submitted!")
            },
            text = {
                Text(
                    "Your found item has been successfully submitted."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onGoHome()
                    }
                ) {
                    Text("Back to Home")
                }
            }
        )
    }
}
