package com.example.acmarge.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Checkbox
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextField
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import android.os.Handler
import android.os.Looper
import com.example.acmarge.R


@Composable
fun TaskManagementScreen(
modifier: Modifier = Modifier,
tasks: MutableMap<String, MutableList<String>>,
selectedDate: String,
onDateSelected: (String) -> Unit,
onCameraRequest: () -> Unit,

// Yeni eklediğimiz parametreler:
completedTasks: MutableMap<String, MutableList<String>>,      // Tamamlanan görevleri mainActivity’den alıyoruz
onCompletedTasksChange: (MutableMap<String, MutableList<String>>) -> Unit // MainActivity'ye geri bildirim için
) {
    // Mevcut tarih ve saat
    val currentDate = remember { mutableStateOf("") }
    val currentTime = remember { mutableStateOf("") }

    // Görev saatlerini izlemek için bir zamanlayıcı
    LaunchedEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val dateFormat = SimpleDateFormat("EEE dd", Locale.ENGLISH)
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                val now = Calendar.getInstance()

                currentDate.value = dateFormat.format(now.time)
                currentTime.value = timeFormat.format(now.time)

                handler.postDelayed(this, 1000) // 1 saniyede bir kontrol
            }
        }
        handler.post(runnable)
    }

    val dynamicDateList = getDateList()

    // Görev ve tamamlanma listesi
    var showTaskSelectionDialog by remember { mutableStateOf(false) }

    // Saat geldiğinde kamerayı aç
    LaunchedEffect(currentTime.value) {
        tasks[selectedDate]?.forEach { task ->
            val taskTime = task.split(" at ").getOrNull(1) ?: return@forEach
            if (taskTime == currentTime.value) {
                onCameraRequest() // Kamerayı aç
            }
        }

    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .padding(innerPadding)
        ) {
            Text(
                text = selectedDate,
                fontSize = 14.sp,
                color = Color(0xFF7A7A7A)
            )
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedDateRow(
                selectedDate = selectedDate,
                onDateSelected = { onDateSelected(it) }, // Tarih değişimini aktar
                tasks = tasks,
                completedTasks = completedTasks

            )


            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Daily Task",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Seçilen tarihe ait görevler
            val filteredTasks = tasks[selectedDate] ?: mutableListOf()
            val filteredCompletedTasks = completedTasks[selectedDate] ?: mutableListOf()

            TaskList(
                tasks = filteredTasks,
                completedTasks = filteredCompletedTasks,
                onTaskChecked = { taskName, isChecked ->
                    val updatedMap = completedTasks.toMutableMap()
                    val updatedList = (updatedMap[selectedDate] ?: mutableListOf()).toMutableList()

                    if (isChecked) {
                        if (!updatedList.contains(taskName)) {
                            updatedList.add(taskName)
                        }
                    } else {
                        updatedList.remove(taskName)
                    }

                    // Haritayı güncelleyip callback ile MainActivity'ye bildiriyoruz
                    updatedMap[selectedDate] = updatedList
                    onCompletedTasksChange(updatedMap)
                }
            )

            if (showTaskSelectionDialog) {
                TaskSelectionDialog(
                    onDismiss = { showTaskSelectionDialog = false },
                    onTaskSelected = { selectedTask, selectedTime, selectedDays ->
                        selectedDays.forEach { date ->
                            val taskWithTime = "$selectedTask at $selectedTime"
                            tasks[date] = (tasks[date] ?: mutableListOf()).apply {
                                add(taskWithTime)
                            }
                        }
                        showTaskSelectionDialog = false
                    },
                    dateList = getDateList()
                )
            }
        }
    }
}

@Composable
fun HomeManagementScreen(
    modifier: Modifier = Modifier,
    tasks: MutableMap<String, MutableList<String>>,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onCameraRequest: () -> Unit,

    // Yeni eklediğimiz parametreler:
    completedTasks: MutableMap<String, MutableList<String>>,      // Tamamlanan görevleri mainActivity’den alıyoruz
    onCompletedTasksChange: (MutableMap<String, MutableList<String>>) -> Unit // MainActivity'ye geri bildirim için
) {
    // Mevcut tarih ve saat (Bu kısımlar kullanılmıyorsa kaldırabilirsiniz)
    val currentDate = remember { mutableStateOf("") }
    val currentTime = remember { mutableStateOf("") }

    val dynamicDateList = getDateList()

    // Görev ve tamamlanma listesi
    var showTaskSelectionDialog by remember { mutableStateOf(false) }

    // LaunchedEffect ile selectedDate'i bugünün tarihi olarak ayarlama
    LaunchedEffect(Unit) {
        onDateSelected(getTodayDateFormatted())
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .padding(innerPadding)
        ) {
            Text(
                text = selectedDate,
                fontSize = 14.sp,
                color = Color(0xFF7A7A7A)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Daily Task",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Seçilen tarihe ait görevler
            val filteredTasks = tasks[selectedDate] ?: mutableListOf()
            val filteredCompletedTasks = completedTasks[selectedDate] ?: mutableListOf()

            TaskList(
                tasks = filteredTasks,
                completedTasks = filteredCompletedTasks,
                onTaskChecked = { taskName, isChecked ->
                    val updatedMap = completedTasks.toMutableMap()
                    val updatedList = (updatedMap[selectedDate] ?: mutableListOf()).toMutableList()

                    if (isChecked) {
                        if (!updatedList.contains(taskName)) {
                            updatedList.add(taskName)
                        }
                    } else {
                        updatedList.remove(taskName)
                    }

                    // Haritayı güncelleyip callback ile MainActivity'ye bildiriyoruz
                    updatedMap[selectedDate] = updatedList
                    onCompletedTasksChange(updatedMap)
                }
            )

            // Eğer `showTaskSelectionDialog` true ise dialog göster
            if (showTaskSelectionDialog) {
                TaskSelectionDialog(
                    onDismiss = { showTaskSelectionDialog = false },
                    onTaskSelected = { selectedTask, selectedTime, selectedDays ->
                        // Sadece bugün ve ileri tarihli günlere ekleme yap
                        val allDates = getDateList()
                        val futureSelectedDays = selectedDays.filter { day ->
                            val index = allDates.indexOf(day)
                            index >= 30 // 30. indeks bugünü temsil eder
                        }

                        futureSelectedDays.forEach { date ->
                            val taskWithTime = "$selectedTask at $selectedTime"
                            tasks[date] = (tasks[date] ?: mutableListOf()).apply {
                                add(taskWithTime)
                            }
                        }
                        showTaskSelectionDialog = false
                        onCompletedTasksChange(tasks)
                    },
                    dateList = getDateList() // Tüm tarihleri veriyoruz ama ekleme sırasında filtreleyeceğiz
                )
            }
        }
    }
}




fun convertWeekdaysToDates(selectedDays: List<String>, dateList: List<String>): List<String> {
    val generatedDates = mutableListOf<String>()

    for (date in dateList) {
        val weekday = date.split(" ")[0] // Haftanın gününü alın (örn: "Mon", "Tue")
        if (selectedDays.any { it.startsWith(weekday, ignoreCase = true) }) {
            generatedDates.add(date) // Gün uyuyorsa listeye ekleyin
        }
    }

    return generatedDates
}

fun getDateList(): List<String> {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEE dd", Locale.ENGLISH) // Kısa tarih formatı

    // 30 gün geçmiş ve 30 gün geleceği kapsayan liste oluştur
    return (0 until 60).map { offset ->
        calendar.time = Calendar.getInstance().time // Bugünün tarihini temel al
        calendar.add(Calendar.DAY_OF_YEAR, offset - 30) // Geçmiş ve gelecek tarihleri ekle
        dateFormat.format(calendar.time) // Formatlanmış tarihi ekle
    }
}
fun getTodayDateFormatted(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEE dd", Locale.ENGLISH)
    return dateFormat.format(calendar.time)
}
@Composable
fun DateRow(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    tasks: Map<String, MutableList<String>>,
    completedTasks: Map<String, MutableList<String>>
) {
    val dateList = getDateList()
    val currentDayIndex = 30
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = currentDayIndex)

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(dateList.size) { index ->
            val date = dateList[index]
            val totalTasks = tasks[date]?.size ?: 0
            val completedTasksCount = completedTasks[date]?.size ?: 0
            val completionPercentage = if (totalTasks > 0) completedTasksCount / totalTasks.toFloat() else 0f

            DateCard(
                date = date,
                isSelected = date == selectedDate,
                completionPercentage = completionPercentage, // Tamamlanma yüzdesi
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
fun DateCard(
    date: String,
    isSelected: Boolean,
    completionPercentage: Float, // Tamamlanma yüzdesi (0.0 - 1.0 arasında tutulmalı)
    onClick: () -> Unit
) {
    // Tamamlanma yüzdesini 0.0 ile 1.0 arasında sınırlıyoruz
    val clampedCompletion = completionPercentage.coerceIn(0f, 1f)

    // Animasyonlu dolum
    val animatedCompletion by animateFloatAsState(
        targetValue = clampedCompletion, // Animasyonlu dolma oranı
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp) // Dış çember boyutu
            .clickable { onClick() }
    ) {
        // Çemberi çizmek için Canvas
        Canvas(
            modifier = Modifier
                .size(80.dp) // Çemberin kesinlikle kare olduğundan emin oluyoruz
        ) {
            // Çemberin yarıçapını tam merkezlenmiş olarak hesaplıyoruz
            val strokeWidth = 6.dp.toPx()
            val radius = (size.width - strokeWidth) / 2 // Çember boyutunu tam kare yapıyoruz

            // Çemberin başlangıç noktasını belirlemek için topLeft ayarı
            val topLeft = Offset(
                (size.width - radius * 2) / 2,
                (size.height - radius * 2) / 2
            )
            // Arkaplan çember (boş bar)
            drawCircle(
                color = Color.LightGray,
                radius = radius,
                center = center, // Çemberi tam merkezde çiz
                style = Stroke(width = strokeWidth)
            )

            // Tamamlanma oranına bağlı dolu bar
            drawArc(
                color = Color(0xFF1E88E5),
                startAngle = -90f,
                sweepAngle = animatedCompletion * 360, // Doluluk oranına göre yay açısı
                useCenter = false,
                size = Size(radius * 2, radius * 2), // Çemberin boyutlarını kare yap
                topLeft = topLeft, // Çemberi tam ortalamak için ayar
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // İçteki yuvarlak içerik (DateCard)
        Box(
            modifier = Modifier
                .size(60.dp) // İçteki içerik boyutu
                .clip(CircleShape) // Yuvarlak hale getirildi
                .background(if (isSelected) Color(0xFF1E88E5) else Color(0xFFE3F2FD)) // Arkaplan rengi
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFF1565C0) else Color.Transparent,
                    shape = CircleShape
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val day = date.split(" ")[0]
                val dateNumber = date.split(" ")[1]
                Text(
                    text = day,
                    color = if (isSelected) Color.White else Color(0xFF1E88E5),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateNumber,
                    color = if (isSelected) Color.White else Color(0xFF1E88E5),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AnimatedDateRow(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    tasks: Map<String, MutableList<String>>,
    completedTasks: Map<String, MutableList<String>>
) {
    var rowHeight by remember { mutableStateOf(60.dp) }
    val animatedHeight by animateDpAsState(targetValue = rowHeight)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        rowHeight = (rowHeight + dragAmount.dp).coerceIn(60.dp, 300.dp) // Min ve max yükseklik
                    },
                    onDragEnd = {
                        rowHeight = if (rowHeight > 150.dp) 300.dp else 60.dp
                    }
                )
            }
    ) {
        if (rowHeight > 150.dp) {
            MonthlyCalendar(onDateSelected = onDateSelected) // Tarih seçimini iletin
        } else {
            DateRow(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                tasks = tasks,
                completedTasks = completedTasks
            )
        }
    }
}

@Composable
fun MonthlyCalendar(onDateSelected: (String) -> Unit) {
    // Sistemin bugünkü tarihini al
    val calendar = Calendar.getInstance()

    // Ayın günlerini hesapla
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthName = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(calendar.time)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Ayın ismini göster
        Text(
            text = monthName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Günleri bir grid şeklinde göster
        val days = (1..daysInMonth).toList()
        val columns = 7 // Haftanın gün sayısı

        Column {
            days.chunked(columns).forEach { week ->
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    week.forEach { day ->
                        Text(
                            text = day.toString(),
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFEEEEEE), CircleShape)
                                .wrapContentHeight(Alignment.CenterVertically)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .clickable {
                                    // Seçilen günü "EEE dd" formatına çevir ve ilet
                                    val formattedDate = SimpleDateFormat(
                                        "EEE dd", Locale.ENGLISH
                                    ).format(
                                        calendar.apply {
                                            set(Calendar.DAY_OF_MONTH, day)
                                        }.time
                                    )
                                    onDateSelected(formattedDate)
                                },
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeekdaySelectionDialog(
    onDismiss: () -> Unit,
    onDaysSelected: (List<String>) -> Unit
) {
    val daysOfWeek = listOf(
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    )
    val selectedDays = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Select Days") },
        text = {
            Column {
                daysOfWeek.forEach { day ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selectedDays.contains(day)) {
                                    selectedDays.remove(day)
                                } else {
                                    selectedDays.add(day)
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = selectedDays.contains(day),
                            onCheckedChange = {
                                if (it) selectedDays.add(day) else selectedDays.remove(day)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = day)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDaysSelected(selectedDays)
                onDismiss()
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TaskSelectionDialog(
    onDismiss: () -> Unit,
    onTaskSelected: (String, String, List<String>) -> Unit,
    dateList: List<String>
) {
    val tasks = listOf(
        "Sports or Fitness",
        "Studying",
        "Cooking",
        "Playing an Instrument",
        "Painting or Drawing",
        "Cleaning or Organizing",
        "Walking",
        "Gardening",
        "Skincare Routine",
        "Meditation"
    )

    val taskIcons = mapOf(
        "Sports or Fitness" to R.drawable.fitness_icon,
        "Studying" to R.drawable.study_icon,
        "Cooking" to R.drawable.cooking_icon,
        "Playing an Instrument" to R.drawable.music_icon,
        "Painting or Drawing" to R.drawable.drawing_icon,
        "Cleaning or Organizing" to R.drawable.cleaning_icon,
        "Walking" to R.drawable.walking_icon,
        "Gardening" to R.drawable.gardening_icon,
        "Skincare Routine" to R.drawable.skincare_icon,
        "Meditation" to R.drawable.meditation_icon
    )

    var selectedTask by remember { mutableStateOf<String?>(null) }
    var selectedDays by remember { mutableStateOf<List<String>>(emptyList()) }
    var showDaySelection by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("12:00 PM") }

    if (showDaySelection) {
        WeekdaySelectionDialog(
            onDismiss = { showDaySelection = false },
            onDaysSelected = { days ->
                selectedDays = convertWeekdaysToDates(days, dateList)
                if (selectedTask != null && selectedDays.isNotEmpty()) {
                    showTimePicker = true
                }
                showDaySelection = false
            }
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = selectedTime,
            onTimeSelected = { time ->
                selectedTime = time
                if (selectedTask != null && selectedDays.isNotEmpty()) {
                    onTaskSelected(selectedTask!!, selectedTime, selectedDays)
                }
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Select a Task") },
        text = {
            Column {
                tasks.forEach { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedTask = task
                                showDaySelection = true
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = taskIcons[task] ?: R.drawable.default_icon),
                            contentDescription = task,
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text(
                            text = task,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Divider()
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TimePickerDialog(
    initialTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var timeInput by remember { mutableStateOf(initialTime) }
    var isInputValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Select Time") },
        text = {
            Column {
                Text(
                    text = "Enter time in HH:MM AM/PM format",
                    fontSize = 14.sp,
                    color = if (isInputValid) Color.Black else Color.Red
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = timeInput,
                    onValueChange = {
                        timeInput = it
                        isInputValid = validateTimeFormat(it)
                    },
                    placeholder = { Text("e.g., 12:30 PM") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validateTimeFormat(timeInput)) {
                        onTimeSelected(timeInput)
                    } else {
                        isInputValid = false
                    }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

fun validateTimeFormat(time: String): Boolean {
    val timeRegex = "^(1[0-2]|0?[1-9]):[0-5][0-9] (AM|PM)$".toRegex()
    return timeRegex.matches(time)
}



@Composable
fun TaskList(tasks: List<String>, completedTasks: List<String>, onTaskChecked: (String, Boolean) -> Unit) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        tasks.forEach { task ->
            val isChecked = completedTasks.contains(task) // Görev tamamlanmış mı kontrol et
            TaskItem(
                taskName = task,
                isChecked = isChecked,
                onCheckedChange = { isChecked -> // Checkbox değişikliği
                    onTaskChecked(task, isChecked)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TaskItem(
    taskName: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    val taskIcons = mapOf(
        "Sports or Fitness" to R.drawable.fitness_icon,
        "Studying" to R.drawable.study_icon,
        "Cooking" to R.drawable.cooking_icon,
        "Playing an Instrument" to R.drawable.music_icon,
        "Painting or Drawing" to R.drawable.drawing_icon,
        "Cleaning or Organizing" to R.drawable.cleaning_icon,
        "Walking" to R.drawable.walking_icon,
        "Gardening" to R.drawable.gardening_icon,
        "Skincare Routine" to R.drawable.skincare_icon,
        "Meditation" to R.drawable.meditation_icon
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = MaterialTheme.shapes.small)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Görev ikonu
        taskIcons[taskName]?.let { iconRes ->
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = taskName,
                modifier = Modifier.size(40.dp).padding(end = 8.dp)
            )
        }

        // Görev adı
        Text(
            text = taskName,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        // Checkbox
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onCheckedChange(it) }
        )
    }
}