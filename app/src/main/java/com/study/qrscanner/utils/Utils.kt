package com.study.qrscanner.utils

import android.R
import android.R.attr.label
import android.R.attr.text
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern



fun Context.copyToClipboard(label: String, text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("arbitrary label", text)
    clipboard.setPrimaryClip(clip)
}

fun SearchView.setOnQuerySubmitListener(
    onSubmit: (query: String) -> Unit,
    onChange: (query: String) -> Unit = {},
) {
    this.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            onSubmit(query)
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            onChange(newText)
            return false
        }
    })

}

fun Bitmap.saveBitmapToFile(fileName: String): File {
    // Get the external storage directory
    val storageDir = Environment.getExternalStorageDirectory()
    // Create a file in the external storage directory
    val file = File(storageDir, fileName)

    try {
        // Create an output stream to write to the file
        val out = FileOutputStream(file)
        // Compress the bitmap and write it to the output stream
        this.compress(Bitmap.CompressFormat.PNG, 100, out)
        // Close the output stream
        out.flush()
        out.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return file
}

fun createRequestBodyFromUri(uri: Uri): RequestBody {
    val formBuilder = FormBody.Builder()

    // Iterate over query parameters and add them to the form body
    uri.queryParameterNames.forEach { key ->
        uri.getQueryParameter(key)?.let { value ->
            formBuilder.add(key, value)
        }
    }

    return formBuilder.build()
}


fun requestToCurl(request: Request): String {
    val builder = StringBuilder()
    builder.append("curl -X ${request.method} \\\n")

    // Append headers
    for ((name, value) in request.headers) {
        builder.append("    -H \"$name: $value\" \\\n")
    }

    // Append request body if present
    request.body?.let { requestBody ->
        if (requestBody is RequestBody) {
            try {
                val charset = requestBody.contentType()?.charset() ?: Charsets.UTF_8
                val bodyString = requestBodyToString(requestBody, charset)
                val escapedBody = escapeSpecialChars(bodyString)
                builder.append("    -d \"$escapedBody\" \\\n")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }
    }

    // Append URL
    builder.append("    \"${request.url}\"")

    return builder.toString()
}

@Throws(UnsupportedEncodingException::class)
private fun requestBodyToString(
    requestBody: RequestBody,
    charset: java.nio.charset.Charset,
): String {
    val buffer = okio.Buffer()
    requestBody.writeTo(buffer)
    return buffer.readString(charset)
}

private fun escapeSpecialChars(input: String): String {
    return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
}

fun ChipGroup.chipChangeListener(onChange: (chip: Chip) -> Unit) {
    this.setOnCheckedStateChangeListener { group, checkedIds ->
        if (checkedIds.isNotEmpty()) {
            val selectedChip = this.findViewById<Chip>(checkedIds.first())
            selectedChip?.let { chip ->
                onChange(chip)
            }
        }
    }
}


fun EditText.textChange(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(text: Editable?) {
            afterTextChanged.invoke(text.toString())
        }
    })
}


fun RecyclerView.getRecyclerViewHeight(): Int {
    val adapter = this.adapter
    val layoutManager = this.layoutManager

    if (adapter == null || layoutManager == null) {
        // Adapter or LayoutManager is not set
        return 0
    }

    var totalHeight = 0

    for (i in 0 until adapter.itemCount) {
        val childView = layoutManager.findViewByPosition(i)
        childView?.let {
            val widthSpec =
                View.MeasureSpec.makeMeasureSpec(this.width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            it.measure(widthSpec, heightSpec)
            totalHeight += it.measuredHeight
        }
    }

    return totalHeight
}

fun TextView.getTextViewHeight(): Int {
    val widthMeasureSpec =
        View.MeasureSpec.makeMeasureSpec(this.width, View.MeasureSpec.EXACTLY)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

    this.measure(widthMeasureSpec, heightMeasureSpec)

    return this.measuredHeight
}

fun View.hideView() {
    visibility = View.GONE
}

fun View.showView() {
    visibility = View.VISIBLE
}

fun <VB : ViewBinding> Fragment.setBottomSheet(
    bindingInflater: (LayoutInflater) -> VB,
    onBind: (VB, BottomSheetDialog) -> Unit,
) {
    val bottomSheetDialog = BottomSheetDialog(requireContext())
    val bottomSheetBinding = bindingInflater(layoutInflater)
    onBind(bottomSheetBinding, bottomSheetDialog)
    bottomSheetDialog.setContentView(bottomSheetBinding.root)
    bottomSheetDialog.show()
}

fun <VB : ViewBinding> Fragment.showCustomDialog(
    bindingInflater: (LayoutInflater) -> VB,
    onBind: (VB, Dialog) -> Unit,
) {
    val dialog = Dialog(requireContext())
    val dialogBinding = bindingInflater(layoutInflater)
    onBind(dialogBinding, dialog)
    dialog.setContentView(dialogBinding.root)
    dialog.window?.setBackgroundDrawableResource(R.color.transparent)
    dialog.show()
}

fun getFormattedDate(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd MMM EEEE", Locale.getDefault())
    val date = dateFormat.format(calendar.time).split(" ")[0]
    val month = dateFormat.format(calendar.time).split(" ")[1]
    val day = dateFormat.format(calendar.time).split(" ")[2].uppercase()
    return "$date $month $day"
}


//fragment back pressed handler
fun Fragment.backPressedHandle(handle: () -> Unit) {
    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handle()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
}

fun Fragment.hideSoftKeyboard() {
    requireActivity().currentFocus?.let {
        val inputMethodManager =
            ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

@SuppressLint("ClickableViewAccessibility")
fun Fragment.setupUI(view: View, handle: () -> Unit) {

    // Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText) {
        view.setOnTouchListener { v, event ->
            hideSoftKeyboard()
            handle()
            false
        }
    }

    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            setupUI(innerView, handle)
        }
    }
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getFileSize(uri: Uri?, context: Context): Long {
    if (uri != null) {
        return context.contentResolver.openAssetFileDescriptor(uri, "r")!!.length
    }
    return 0
}

fun Fragment.orientationPortrait() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun Fragment.orientationLandscape() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun TextInputEditText.textChange(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(text: Editable?) {
            afterTextChanged.invoke(text.toString())
        }
    })
}

fun AutoCompleteTextView.textChange(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(text: Editable?) {
            afterTextChanged.invoke(text.toString())
        }
    })
}

fun Fragment.pickDate(
    _day: Int, _month: Int, _year: Int, _date: (Int, Int, Int) -> Unit,
) {
    val datePickerDialog = DatePickerDialog(
        requireContext(), { view, year, monthOfYear, dayOfMonth ->
            _date.invoke(year, monthOfYear, dayOfMonth)
        }, _year, _month, _day
    )
    datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
    datePickerDialog.show()
}

fun Fragment.pickTime(
    _hour: Int, _minute: Int, is24Hour: Boolean, _time: (hourOfDay: Int, minute: Int) -> Unit,
) {
    val timePickerDialog = TimePickerDialog(
        requireContext(), { view, hourOfDay, minute ->
            _time.invoke(hourOfDay, minute)
        }, _hour, _minute, is24Hour
    )
    timePickerDialog.show()
}

fun isNotNullOrZero(number: String, errorMessage: String): String? {
    if (number.trim().isNullOrEmpty() || number.trim() == "0") {
        return errorMessage
    }
    return null
}

fun isValidName(name: String, errorMessage: String = ""): String? {
    if (name.trim().isNullOrEmpty()) {
        return errorMessage
    }
    return null
}

fun isValidEmail(email: String, errorMessage: String = ""): String? {
    if (email.trim().isNullOrEmpty()) {
        return errorMessage
    } else if (!Pattern.compile("[a-zA-Z0-9+_.-]+@[a-zA-Z0-9]+[.-][a-zA-Z][a-z.A-Z]+")
            .matcher(email).matches()
    ) {
        return errorMessage
    }
    return null
}

fun isValidMobileNumber(number: String, errorMessage: String): String? {
    if (number.trim().isNullOrEmpty()) {
        return errorMessage
    } else if (number.trim().length != 10) {
        return errorMessage
    } else if (!number.trim().matches("^[6-9][0-9]{9}$".toRegex())) {
        return errorMessage
    }
    return null
}

fun isValidNumber(number: String, errorMessage: String): String? {
    if (number.trim().isNullOrEmpty()) {
        return errorMessage
    } else if (number.trim().length != 10) {
        return errorMessage
    }
    return null
}

fun Fragment.toastMsg(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastLongMsg(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
}

fun Activity.toastMsg(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}