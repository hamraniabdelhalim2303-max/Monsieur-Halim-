package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

// Elegant logo Composable matching Monsieur Halim's minimalist black & white with gold identity
@Composable
fun LogoMH(
    modifier: Modifier = Modifier,
    size: Int = 44,
    showBorder: Boolean = true,
    isDarkTheme: Boolean = false
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(if (isDarkTheme) Color.White else DeepSlate)
            .then(
                if (showBorder) Modifier.border(
                    width = 1.5.dp,
                    color = Gold,
                    shape = CircleShape
                ) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "MH",
                color = if (isDarkTheme) DeepSlate else Gold,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = (size * 0.35).sp,
                lineHeight = (size * 0.35).sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedCourseId by viewModel.selectedCourseId.collectAsStateWithLifecycle()
    val selectedStoryId by viewModel.selectedStoryId.collectAsStateWithLifecycle()
    val selectedLibraryId by viewModel.selectedLibraryId.collectAsStateWithLifecycle()
    val activeGameId by viewModel.activeGameId.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val isAr = progress?.currentLanguage == "AR"
    val isDark = progress?.isDarkMode == true
    val role = progress?.userRole ?: "STUDENT"

    // Set Layout Direction based on selected language for instant RTL support
    val layoutDirection = if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Toast handler
    val context = LocalContext.current
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            bottomBar = {
                // Bottom navigation only visible if no sub-screens are active to maintain clean view bounds
                if (selectedCourseId == null && selectedStoryId == null && selectedLibraryId == null && activeGameId == null) {
                    BottomNavBar(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.setTab(it) },
                        isAr = isAr
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                // Navigating screens based on state flows
                when {
                    selectedCourseId != null -> {
                        val course = EducationData.courses.find { it.id == selectedCourseId }
                        if (course != null) {
                            CourseDetailScreen(
                                course = course,
                                viewModel = viewModel,
                                isAr = isAr
                            )
                        } else {
                            viewModel.selectCourse(null)
                        }
                    }
                    selectedStoryId != null -> {
                        val story = EducationData.stories.find { it.id == selectedStoryId }
                        if (story != null) {
                            StoryDetailScreen(
                                story = story,
                                viewModel = viewModel,
                                isAr = isAr
                            )
                        } else {
                            viewModel.selectStory(null)
                        }
                    }
                    selectedLibraryId != null -> {
                        val libItem = EducationData.libraryItems.find { it.id == selectedLibraryId }
                        if (libItem != null) {
                            LibraryDetailScreen(
                                item = libItem,
                                viewModel = viewModel,
                                isAr = isAr
                            )
                        } else {
                            viewModel.selectLibraryItem(null)
                        }
                    }
                    activeGameId != null -> {
                        val game = EducationData.gamesList.find { it.id == activeGameId }
                        if (game != null) {
                            GameContainerScreen(
                                game = game,
                                viewModel = viewModel,
                                isAr = isAr
                            )
                        } else {
                            viewModel.selectGame(null)
                        }
                    }
                    else -> {
                        // Main Bottom Tabs
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                            },
                            label = "TabTransition"
                        ) { tab ->
                            when (tab) {
                                0 -> DashboardScreen(viewModel = viewModel, isAr = isAr)
                                1 -> CoursesListScreen(viewModel = viewModel, isAr = isAr)
                                2 -> LibraryScreen(viewModel = viewModel, isAr = isAr)
                                3 -> GamesScreen(viewModel = viewModel, isAr = isAr)
                                4 -> ProfileScreen(viewModel = viewModel, isAr = isAr)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Bottom navigation bar matching Geometric Balance styling
@Composable
fun BottomNavBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    isAr: Boolean
) {
    val items = listOf(
        TabItem(
            titleFr = "Accueil",
            titleAr = "الرئيسية",
            iconSelected = Icons.Filled.Home,
            iconUnselected = Icons.Outlined.Home
        ),
        TabItem(
            titleFr = "Cours",
            titleAr = "الدروس",
            iconSelected = Icons.Filled.MenuBook,
            iconUnselected = Icons.Outlined.MenuBook
        ),
        TabItem(
            titleFr = "Bibliothèque",
            titleAr = "المكتبة",
            iconSelected = Icons.Filled.LocalLibrary,
            iconUnselected = Icons.Outlined.LocalLibrary
        ),
        TabItem(
            titleFr = "Jeux",
            titleAr = "الألعاب",
            iconSelected = Icons.Filled.SportsEsports,
            iconUnselected = Icons.Outlined.SportsEsports
        ),
        TabItem(
            titleFr = "Profil",
            titleAr = "الحساب",
            iconSelected = Icons.Filled.Person,
            iconUnselected = Icons.Outlined.Person
        )
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            .testTag("bottom_nav_bar")
    ) {
        items.forEachIndexed { index, tab ->
            val isSelected = currentTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.iconSelected else tab.iconUnselected,
                        contentDescription = if (isAr) tab.titleAr else tab.titleFr,
                        tint = if (isSelected) Gold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                },
                label = {
                    Text(
                        text = if (isAr) tab.titleAr else tab.titleFr,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Gold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("nav_tab_$index")
            )
        }
    }
}

data class TabItem(
    val titleFr: String,
    val titleAr: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
)

// Header layout reusable across main dashboards
@Composable
fun AppHeader(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val isDark = progress?.isDarkMode == true

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LogoMH(isDarkTheme = isDark)
                    Column {
                        Text(
                            text = if (isAr) "منصة الأستاذ حليم" else "La Plateforme de Monsieur Halim",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isAr) "التميز في اللغة الفرنسية 🇩🇿" else "L'excellence en français 🇩🇿",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Switch Language & Role trigger
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                            .testTag("lang_switch_button")
                    ) {
                        Text(
                            text = if (isAr) "FR" else "عربي",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                            .testTag("dark_mode_button")
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = Gold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // User stats row (XP & Streak)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // XP Card
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Gold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "XP",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = if (isAr) "نقاط الخبرة" else "Points d'XP",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${progress?.xpPoints ?: 2450} pts",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Divider(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                // Streak Card
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "🔥",
                        fontSize = 18.sp
                    )
                    Column {
                        Text(
                            text = if (isAr) "سلسلة التعلم" else "Série",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = if (isAr) "${progress?.streakDays ?: 12} أيام" else "${progress?.streakDays ?: 12} Jours",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Divider(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                // Level Card
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "🏆",
                        fontSize = 16.sp
                    )
                    Column {
                        Text(
                            text = if (isAr) "المستوى" else "Niveau",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${progress?.level ?: 5}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// TAB 1: ACCUEIL (DASHBOARD)
// ----------------------------------------------------------------------------------
@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val role = progress?.userRole ?: "STUDENT"

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(viewModel = viewModel, isAr = isAr)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dynamic Role Badge (allow changing role easily via floating header or click)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "دورك الحالي:" else "Votre rôle :",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("STUDENT", "PARENT", "TEACHER").forEach { r ->
                            val isSel = role == r
                            val label = when (r) {
                                "STUDENT" -> if (isAr) "تلميذ" else "Élève"
                                "PARENT" -> if (isAr) "ولي أمر" else "Parent"
                                else -> if (isAr) "معلم" else "Enseignant"
                            }
                            FilterChip(
                                selected = isSel,
                                onClick = { viewModel.setUserRole(r) },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Gold,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("role_chip_$r")
                            )
                        }
                    }
                }
            }

            // Daily Motivation Quote
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("✨", fontSize = 18.sp)
                            Text(
                                text = if (isAr) "حكمة اليوم" else "Motivation du Jour",
                                style = MaterialTheme.typography.labelLarge,
                                color = Gold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "« L'éducation est l'arme la plus puissante pour changer le monde. »",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "« التعليم هو أقوى سلاح يمكنك استخدامه لتغيير العالم. »",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Hero Section: Continue Learning
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepSlate),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectCourse("1") } // Open Passé Composé course
                        .testTag("continue_learning_card")
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Geometric background decoration circle
                        Canvas(modifier = Modifier.size(160.dp).align(Alignment.TopEnd)) {
                            drawCircle(
                                color = Gold.copy(alpha = 0.15f),
                                radius = size.minDimension / 1.5f,
                                center = Offset(size.width, 0f),
                                style = Stroke(width = 8.dp.toPx())
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Gold, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isAr) "متابعة التعلم" else "REPRENDRE LE COURS",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Conjugaison : Le Passé Composé",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White
                            )
                            Text(
                                text = "القواعد: الماضي المركب في اللغة الفرنسية",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = 0.75f,
                                    color = Gold,
                                    trackColor = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "75%",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Claim daily reward section
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) "الهدية اليومية" else "Récompense Quotidienne",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isAr) "احصل على +50 نقطة خبرة الآن!" else "Réclamez +50 XP gratuits aujourd'hui !",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Button(
                            onClick = { viewModel.claimDailyReward() },
                            colors = ButtonDefaults.buttonColors(containerColor = Gold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("claim_reward_button")
                        ) {
                            Text(if (isAr) "استلام" else "Réclamer", color = Color.White)
                        }
                    }
                }
            }

            // Quick Category Selectors
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isAr) "الأقسام السريعة" else "Raccourcis d'apprentissage",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Quick Lecture Shortcut
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setTab(1) }, // Go to Courses
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("📖", fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isAr) "قراءة" else "Lecture",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAr) "تحسين القراءة المستمرة" else "Améliorez votre lecture",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }

                        // Quick Games Shortcut
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setTab(3) }, // Go to Games
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("✍️", fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isAr) "ألعاب إملائية" else "Orthographe",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAr) "تعلم بالقواعد واللعب" else "Apprenez par le jeu",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            // AI Floating Assistant Widget
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WarmCream),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Gold.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Quick navigate to AI assistant screen by setting a tab or opening a simulated dialogue
                            // We can use a popup dialog or go directly to Profile and let them chat, or expand an embedded chat!
                            // Let's create an embedded beautiful bottom sheet or a full overlay. Even better, let's include
                            // a Chat floating button that opens a beautiful full screen Chat dialog!
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🤖", fontSize = 24.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) "مساعد الذكاء الاصطناعي" else "Assistant IA Monsieur Halim",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = DeepSlate
                            )
                            Text(
                                text = if (isAr) "اسألني أي سؤال في القواعد أو الإملاء!" else "Une question de grammaire ? Posez-la moi !",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DeepSlate.copy(alpha = 0.7f)
                            )
                        }

                        var showChatDialog by remember { mutableStateOf(false) }
                        if (showChatDialog) {
                            AiChatDialog(viewModel = viewModel, isAr = isAr) { showChatDialog = false }
                        }

                        IconButton(
                            onClick = { showChatDialog = true },
                            modifier = Modifier
                                .background(DeepSlate, CircleShape)
                                .size(36.dp)
                                .testTag("ai_widget_arrow")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "Open Chat",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// AI ASSISTANT CHAT DIALOG
// ----------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatDialog(
    viewModel: AppViewModel,
    isAr: Boolean,
    onDismiss: () -> Unit
) {
    val chatHistory by viewModel.chatHistory.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .testTag("ai_chat_dialog"),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DeepSlate)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LogoMH(size = 32, showBorder = false, isDarkTheme = true)
                        Column {
                            Text(
                                text = if (isAr) "معلمك الشخصي الذكي" else "Tuteur Intelligent IA",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isAr) "الأستاذ حليم يجيبك دائماً" else "Monsieur Halim en ligne",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Chat Messages Feed
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (chatHistory.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("🤖", fontSize = 48.sp)
                                    Text(
                                        text = if (isAr) "أهلاً بك! أنا معلمك الشخصي للغة الفرنسية." else "Bonjour ! Je suis votre tuteur personnel de français.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = if (isAr) "اسألني عن القواعد، تصريف الأفعال أو صحح كتاباتك!" else "Posez-moi vos questions de grammaire, conjugaison, ou demandez-moi de corriger vos textes !",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    items(chatHistory) { msg ->
                        val isUser = msg.sender == "USER"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUser) Gold else WarmCream
                                ),
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 16.dp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .shadow(1.dp, RoundedCornerShape(16.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = msg.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isUser) Color.White else DeepSlate
                                    )
                                }
                            }
                        }
                    }

                    if (isAiLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = WarmCream),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = Gold
                                        )
                                        Text(
                                            text = if (isAr) "الأستاذ حليم يكتب..." else "Monsieur Halim réfléchit...",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DeepSlate
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Input Actions Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = {
                            Text(
                                if (isAr) "اسأل الأستاذ حليم هنا..." else "Posez votre question...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_chat_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions.Default,
                        keyboardActions = KeyboardActions(onSend = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendChatMessage(messageText)
                                messageText = ""
                            }
                        })
                    )

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendChatMessage(messageText)
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .background(Gold, CircleShape)
                            .size(44.dp)
                            .testTag("ai_chat_send_button"),
                        enabled = messageText.isNotBlank() && !isAiLoading
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// TAB 2: COURS (COURSES LIST)
// ----------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesListScreen(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val courseProgressList by viewModel.courseProgressList.collectAsStateWithLifecycle()

    var selectedCategory by remember { mutableStateOf("Tous") }

    val categories = listOf("Tous", "Conjugaison", "Grammaire", "Orthographe", "Vocabulaire")

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(viewModel = viewModel, isAr = isAr)

        // Title and Category filter chips
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = if (isAr) "كتالوج الدروس" else "Catalogue des Cours",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSel = selectedCategory == cat
                    val displayLabel = when (cat) {
                        "Tous" -> if (isAr) "الكل" else "Tous"
                        "Conjugaison" -> if (isAr) "تصريف" else "Conjugaison"
                        "Grammaire" -> if (isAr) "نحو" else "Grammaire"
                        "Orthographe" -> if (isAr) "إملاء" else "Orthographe"
                        else -> if (isAr) "مفردات" else "Vocabulaire"
                    }

                    FilterChip(
                        selected = isSel,
                        onClick = { selectedCategory = cat },
                        label = { Text(displayLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Gold,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("course_filter_$cat")
                    )
                }
            }
        }

        // List of courses
        val filteredCourses = if (selectedCategory == "Tous") {
            EducationData.courses
        } else {
            EducationData.courses.filter { it.category == selectedCategory }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredCourses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isAr) "لا توجد دروس حالياً في هذا القسم." else "Aucun cours disponible pour cette catégorie.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            items(filteredCourses) { course ->
                val prg = courseProgressList.find { it.courseId == course.id }
                val isCompleted = prg?.isCompleted == true
                val step = prg?.currentStep ?: 0

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectCourse(course.id) }
                        .testTag("course_card_${course.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Category Icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = when (course.category) {
                                        "Conjugaison" -> Color(0xFFFEE2E2)
                                        "Grammaire" -> Color(0xFFE0F2FE)
                                        "Orthographe" -> Color(0xFFDCFCE7)
                                        else -> Color(0xFFFEF9C3)
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (course.category) {
                                    "Conjugaison" -> "⏳"
                                    "Grammaire" -> "📜"
                                    "Orthographe" -> "✒️"
                                    else -> "💡"
                                },
                                fontSize = 22.sp
                            )
                        }

                        // Info Column
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = course.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gold,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isCompleted) {
                                    Text(
                                        text = if (isAr) "✓ مكتمل" else "✓ COMPLÉTÉ",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF22C55E),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isAr) course.titleAr else course.titleFr,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isAr) course.descriptionAr else course.descriptionFr,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = "Details",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// COURSE DETAIL SCREEN
// ----------------------------------------------------------------------------------
@Composable
fun CourseDetailScreen(
    course: Course,
    viewModel: AppViewModel,
    isAr: Boolean
) {
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val courseProgressList by viewModel.courseProgressList.collectAsStateWithLifecycle()

    val prg = courseProgressList.find { it.courseId == course.id }
    val step = prg?.currentStep ?: 0
    val quizScore = prg?.quizScore ?: -1

    // Sub tabs: 0 = Video, 1 = PDF, 2 = Quiz
    var activeSubTab by remember { mutableStateOf(step) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Simple Back Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepSlate)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { viewModel.selectCourse(null) },
                modifier = Modifier.testTag("course_back_button")
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = if (isAr) course.titleAr else course.titleFr,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            // Favorite Button
            val isBookmarked = progress?.bookmarkedItems?.split(",")?.contains(course.id) == true
            IconButton(
                onClick = { viewModel.bookmarkItem(course.id, !isBookmarked) },
                modifier = Modifier.testTag("course_bookmark_button")
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isBookmarked) Color.Red else Color.White
                )
            }
        }

        // Sub Tabs Indicator
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Gold
        ) {
            val tabs = listOf(
                if (isAr) "الفيديو 🎥" else "Vidéo 🎥",
                if (isAr) "الدرس PDF 📄" else "Cours PDF 📄",
                if (isAr) "الاختبار 📝" else "Quiz 📝"
            )
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = activeSubTab == idx,
                    onClick = { activeSubTab = idx },
                    text = { Text(title, style = MaterialTheme.typography.labelLarge) },
                    modifier = Modifier.testTag("course_tab_$idx")
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (activeSubTab) {
                0 -> {
                    // Simulated premium Video Player
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = { viewModel.showToast("Lecture de la vidéo en cours... ⏯️") },
                                        modifier = Modifier
                                            .size(64.dp)
                                            .background(Gold, CircleShape)
                                            .testTag("play_video_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayArrow,
                                            contentDescription = "Play",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Text(
                                        text = if (isAr) "تشغيل فيديو الدرس (${course.videoDuration})" else "Regarder la vidéo (${course.videoDuration})",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isAr) "حول هذا الفيديو :" else "À propos de cette leçon :",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAr) "فيديو تفصيلي من تقديم الأستاذ حليم يشرح فيه المبادئ الأساسية وقواعد الدرس مع أمثلة حية مأخوذة من الحياة اليومية لتبسيط الفهم." else "Vidéo détaillée animée par Monsieur Halim, expliquant les bases fondamentales de cette leçon avec des astuces visuelles pour une assimilation rapide.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                viewModel.completeCourseStep(course.id, 0)
                                activeSubTab = 1
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Gold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("video_complete_button")
                        ) {
                            Text(
                                if (isAr) "تمت المشاهدة، الانتقال إلى PDF" else "J'ai regardé, passer au PDF",
                                color = Color.White
                            )
                        }
                    }
                }
                1 -> {
                    // Course PDF View
                    Column(modifier = Modifier.fillMaxSize()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "📄 SUPPORT DE COURS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = course.pdfContentFr,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 24.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = course.pdfContentAr,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 26.sp,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.showToast("Téléchargement du PDF démarré... ⬇️") },
                                border = BorderStroke(1.dp, Gold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("download_pdf_button")
                            ) {
                                Text(if (isAr) "تحميل PDF" else "Télécharger PDF", color = Gold)
                            }

                            Button(
                                onClick = {
                                    viewModel.completeCourseStep(course.id, 1)
                                    activeSubTab = 2
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pdf_complete_button")
                            ) {
                                Text(
                                    if (isAr) "قرأت، الانتقال إلى الاختبار" else "J'ai lu, passer au Quiz",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Quiz Section
                    InteractiveQuizView(
                        questions = course.quizQuestions,
                        savedScore = quizScore,
                        isAr = isAr,
                        onSubmit = { score ->
                            viewModel.submitQuizScore(course.id, score, course.quizQuestions.size)
                        }
                    )
                }
            }
        }
    }
}

// Interactive Quiz View reusable
@Composable
fun InteractiveQuizView(
    questions: List<QuizQuestion>,
    savedScore: Int,
    isAr: Boolean,
    onSubmit: (Int) -> Unit
) {
    var selectedAnswers by remember { mutableStateOf(MutableList(questions.size) { -1 }) }
    var quizSubmitted by remember { mutableStateOf(savedScore >= 0) }
    var currentScore by remember { mutableStateOf(if (savedScore >= 0) savedScore else 0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (quizSubmitted) {
            Card(
                colors = CardDefaults.cardColors(containerColor = WarmCream),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🎉", fontSize = 48.sp)
                    Text(
                        text = if (isAr) "اكتمل الاختبار بنجاح !" else "Quiz Complété !",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepSlate
                    )
                    Text(
                        text = if (isAr) "النتيجة النهائية : ${if (savedScore >= 0) savedScore else currentScore} / ${questions.size}" else "Votre score : ${if (savedScore >= 0) savedScore else currentScore} / ${questions.size}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Gold,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = {
                            selectedAnswers = MutableList(questions.size) { -1 }
                            quizSubmitted = false
                        },
                        border = BorderStroke(1.dp, Gold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isAr) "إعادة المحاولة" else "Recommencer le Quiz", color = Gold)
                    }
                }
            }
        } else {
            questions.forEachIndexed { qIdx, question ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Question ${qIdx + 1} / ${questions.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAr) question.questionAr else question.questionFr,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val options = if (isAr) question.optionsAr else question.optionsFr
                        options.forEachIndexed { oIdx, opt ->
                            val isSelected = selectedAnswers[qIdx] == oIdx
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) Gold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.tertiary.copy(
                                            alpha = 0.2f
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Gold else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        val newAns = selectedAnswers.toMutableList()
                                        newAns[qIdx] = oIdx
                                        selectedAnswers = newAns
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        val newAns = selectedAnswers.toMutableList()
                                        newAns[qIdx] = oIdx
                                        selectedAnswers = newAns
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = Gold)
                                )
                                Text(
                                    text = opt,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            val allAnswered = selectedAnswers.none { it == -1 }
            Button(
                onClick = {
                    var score = 0
                    questions.forEachIndexed { idx, q ->
                        if (selectedAnswers[idx] == q.correctAnswerIndex) {
                            score++
                        }
                    }
                    currentScore = score
                    quizSubmitted = true
                    onSubmit(score)
                },
                enabled = allAnswered,
                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_quiz_button")
            ) {
                Text(if (isAr) "إرسال الإجابات" else "Soumettre les Réponses", color = Color.White)
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// TAB 3: BIBLIOTHÈQUE (LIBRARY & STORIES)
// ----------------------------------------------------------------------------------
@Composable
fun LibraryScreen(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    var selectedSection by remember { mutableStateOf(0) } // 0 = Livres & Contes, 1 = Fiches & Flashcards

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(viewModel = viewModel, isAr = isAr)

        // Sub Tab Selector
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Gold
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text(if (isAr) "قصص مصورة 📖" else "Contes Illustrés 📖") },
                modifier = Modifier.testTag("lib_tab_stories")
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text(if (isAr) "بطاقات تعليمية 📂" else "Fiches & Flashcards 📂") },
                modifier = Modifier.testTag("lib_tab_fiches")
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedSection == 0) {
                // Stories catalog
                items(EducationData.stories) { story ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectStory(story.id) }
                            .testTag("story_card_${story.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("🦁", fontSize = 36.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = story.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAr) story.titleAr else story.titleFr,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAr) "استمع واقرأ هذه القصة الجميلة" else "Écoutez et lisez ce conte magnifique",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.PlayCircle,
                                contentDescription = "Play Story",
                                tint = Gold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            } else {
                // PDF Fiches & Flashcards
                items(EducationData.libraryItems) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectLibraryItem(item.id) }
                            .testTag("lib_item_card_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                if (item.category == "Flashcard") "🗂️" else "📄",
                                fontSize = 32.sp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAr) item.titleAr else item.titleFr,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAr) item.descriptionAr else item.descriptionFr,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "Open",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// STORY DETAIL SCREEN WITH AUDIO NARRATION & COMPREHENSION
// ----------------------------------------------------------------------------------
@Composable
fun StoryDetailScreen(
    story: Story,
    viewModel: AppViewModel,
    isAr: Boolean
) {
    var isPlaying by remember { mutableStateOf(false) }
    var audioProgress by remember { mutableStateOf(0f) }

    // Simulating progress slider increment
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (audioProgress < 1f) {
                kotlinx.coroutines.delay(1000)
                audioProgress += 0.02f
            }
            isPlaying = false
            audioProgress = 0f
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Back Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepSlate)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.selectStory(null) },
                modifier = Modifier.testTag("story_back_button")
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = if (isAr) story.titleAr else story.titleFr,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simulated Audio Player Box
            Card(
                colors = CardDefaults.cardColors(containerColor = WarmCream),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "استمع إلى القصة بصوت الأستاذ حليم" else "Narration audio par M. Halim",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = story.audioDuration,
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepSlate.copy(alpha = 0.6f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .background(DeepSlate, CircleShape)
                                .size(44.dp)
                                .testTag("story_audio_play")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play Narration",
                                tint = Color.White
                            )
                        }

                        Slider(
                            value = audioProgress,
                            onValueChange = { audioProgress = it },
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = Gold,
                                activeTrackColor = Gold
                            )
                        )
                    }
                }
            }

            // Dual Language Content
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = story.contentFr,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = story.contentAr,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 26.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Vocabulary Helper (click to reveal translation)
            Text(
                text = if (isAr) "قاموس القصة المساعد" else "Aide-mémoire Vocabulaire",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                story.vocabulary.forEach { pair ->
                    var revealed by remember { mutableStateOf(false) }
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (revealed) Gold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .clickable { revealed = !revealed }
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = pair.first,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (revealed) pair.second else "???",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (revealed) Gold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }

            // Comprehension Quiz
            Text(
                text = if (isAr) "اختبر فهمك للقصة !" else "Vérifiez votre compréhension !",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            InteractiveQuizView(
                questions = story.quizQuestions,
                savedScore = -1,
                isAr = isAr,
                onSubmit = { score ->
                    viewModel.submitQuizScore(story.id, score, story.quizQuestions.size)
                }
            )
        }
    }
}

// ----------------------------------------------------------------------------------
// FLASHCARDS / PDF INTERACTIVE LIBRARY VIEW
// ----------------------------------------------------------------------------------
@Composable
fun LibraryDetailScreen(
    item: LibraryItem,
    viewModel: AppViewModel,
    isAr: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Back Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepSlate)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.selectLibraryItem(null) },
                modifier = Modifier.testTag("lib_back_button")
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = if (isAr) item.titleAr else item.titleFr,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (item.category == "Flashcard") {
                // Interactive Flipping Flashcards!
                Text(
                    text = if (isAr) "انقر على البطاقة لقلبها ومعرفة الترجمة" else "Cliquez sur la carte pour la retourner",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                item.flashcards.forEach { card ->
                    var isFlipped by remember { mutableStateOf(false) }

                    // Smooth flip rotation animation
                    val rotation by animateFloatAsState(
                        targetValue = if (isFlipped) 180f else 0f,
                        animationSpec = tween(durationMillis = 400)
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isFlipped) Gold else WarmCream
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(180.dp)
                            .rotate(rotation)
                            .clickable { isFlipped = !isFlipped }
                            .shadow(3.dp, RoundedCornerShape(24.dp)),
                        border = BorderStroke(2.dp, Gold)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Undo rotation for the text so it does not render mirrored/upside down
                            val textRotation = if (isFlipped) -180f else 0f
                            Column(
                                modifier = Modifier
                                    .rotate(textRotation)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (isFlipped) "ARABE (عربي)" else "FRANÇAIS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isFlipped) Color.White.copy(alpha = 0.8f) else DeepSlate.copy(
                                        alpha = 0.6f
                                    ),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isFlipped) card.second else card.first,
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = if (isFlipped) Color.White else DeepSlate,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            } else {
                // Simulated rich PDF handout reader
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "FICHE DE RÉVISION PDF",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = item.contentFr,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = item.contentAr,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 26.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Button(
                    onClick = { viewModel.showToast("Téléchargement du document lancé... 📥") },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lib_item_download")
                ) {
                    Text(if (isAr) "تحميل المرفق التعليمي" else "Télécharger le support de révision", color = Color.White)
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// TAB 4: JEUX (GAMES LIST & CONTAINER)
// ----------------------------------------------------------------------------------
@Composable
fun GamesScreen(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(viewModel = viewModel, isAr = isAr)

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isAr) "مركز الألعاب والترفيه" else "Jeux Éducatifs",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAr) "تعلم اللغة الفرنسية بالمرح واكتسب نقاط XP إضافية !" else "Apprenez en vous amusant et gagnez des points d'XP !",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(EducationData.gamesList) { game ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectGame(game.id) }
                        .testTag("game_card_${game.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(GoldLight, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(game.icon, fontSize = 28.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) game.titleAr else game.titleFr,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isAr) game.descriptionAr else game.descriptionFr,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✨", fontSize = 12.sp)
                                Text(
                                    text = "+${game.xpReward} XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                            tint = Gold
                        )
                    }
                }
            }
        }
    }
}

// GAME CONTAINER SCREEN
@Composable
fun GameContainerScreen(
    game: GameInfo,
    viewModel: AppViewModel,
    isAr: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Back Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepSlate)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.selectGame(null) },
                modifier = Modifier.testTag("game_back_button")
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = if (isAr) game.titleAr else game.titleFr,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (game.id) {
                "game_1" -> DecoderGameView(viewModel = viewModel, isAr = isAr)
                "game_2" -> VocabularyMatchGameView(viewModel = viewModel, isAr = isAr)
                "game_3" -> MemoryGameView(viewModel = viewModel, isAr = isAr)
                else -> {
                    Text("Jeu inconnu")
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// GAME 1: DECODAGE ALPHABET WORD ASSEMBLY
// ----------------------------------------------------------------------------------
@Composable
fun DecoderGameView(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    val targetWord = "ECOLE"
    val scrambledLetters = remember { listOf("L", "C", "O", "E", "E") }
    var selectedIndices by remember { mutableStateOf(emptyList<Int>()) }
    var gameCompleted by remember { mutableStateOf(false) }

    val spelledWord = selectedIndices.map { scrambledLetters[it] }.joinToString("")

    LaunchedEffect(spelledWord) {
        if (spelledWord == targetWord) {
            gameCompleted = true
            viewModel.addXp(100)
            viewModel.showToast("Félicitations ! Vous avez décodé 'ECOLE' ! +100 XP 🎉")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = if (isAr) "رتب الحروف لتشكيل كلمة 'مدرسة' بالفرنسية" else "Arrangez les lettres pour décoder le mot : ÉCOLE",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Spelled Area
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(64.dp)
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .border(2.dp, Gold, RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            spelledWord.forEach { char ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Gold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(44.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = char.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Letter selection board
        if (gameCompleted) {
            Card(
                colors = CardDefaults.cardColors(containerColor = WarmCream),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🎉 EXCELLENT !", style = MaterialTheme.typography.titleLarge, color = DeepSlate)
                    Text(
                        text = if (isAr) "رائع جداً ! لقد قمت بتركيب كلمة ECOLE بشكل صحيح." else "Bravo ! Vous avez trouvé le mot ÉCOLE !",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = {
                            selectedIndices = emptyList()
                            gameCompleted = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Gold)
                    ) {
                        Text("Rejouer / إعادة اللعب", color = Color.White)
                    }
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                scrambledLetters.forEachIndexed { idx, letter ->
                    val isUsed = selectedIndices.contains(idx)
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUsed) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(54.dp)
                            .clickable(enabled = !isUsed) {
                                selectedIndices = selectedIndices + idx
                            }
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        border = BorderStroke(1.5.dp, Gold)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = letter,
                                style = MaterialTheme.typography.headlineMedium,
                                color = if (isUsed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { selectedIndices = emptyList() },
                colors = ButtonDefaults.buttonColors(containerColor = DeepSlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isAr) "إعادة تعيين" else "Réinitialiser", color = Color.White)
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// GAME 2: VOCABULARY MATCHING CHALLENGE
// ----------------------------------------------------------------------------------
@Composable
fun VocabularyMatchGameView(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    val frWords = listOf("Père", "Livre", "École", "Sœur")
    val arWords = listOf("أب", "كتاب", "مدرسة", "أخت")

    var selectedFr by remember { mutableStateOf<String?>(null) }
    var selectedAr by remember { mutableStateOf<String?>(null) }
    var matchedPairs by remember { mutableStateOf(emptySet<String>()) }
    var scoreAwarded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedFr, selectedAr) {
        if (selectedFr != null && selectedAr != null) {
            val correctAr = when (selectedFr) {
                "Père" -> "أب"
                "Livre" -> "كتاب"
                "École" -> "مدرسة"
                "Sœur" -> "أخت"
                else -> ""
            }
            if (selectedAr == correctAr) {
                matchedPairs = matchedPairs + selectedFr!!
                viewModel.showToast("Correct ! ✓")
            } else {
                viewModel.showToast("Réessayez ! ✗")
            }
            selectedFr = null
            selectedAr = null
        }
    }

    LaunchedEffect(matchedPairs.size) {
        if (matchedPairs.size == frWords.size && !scoreAwarded) {
            scoreAwarded = true
            viewModel.addXp(100)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isAr) "طابق كل كلمة بالفرنسية مع معناها بالعربية" else "Associez chaque mot français à son équivalent arabe",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        if (matchedPairs.size == frWords.size) {
            Card(
                colors = CardDefaults.cardColors(containerColor = WarmCream),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🏆 PARFAIT !", style = MaterialTheme.typography.titleLarge, color = DeepSlate)
                    Text(
                        text = if (isAr) "عمل ممتاز ! لقد قمت بمطابقة جميع المفردات بشكل صحيح ! +100 XP" else "Excellent travail ! Vous avez associé tous les mots ! +100 XP",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = {
                            matchedPairs = emptySet()
                            scoreAwarded = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Gold)
                    ) {
                        Text("Rejouer / إعادة اللعب", color = Color.White)
                    }
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // French side
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    frWords.forEach { word ->
                        val isMatched = matchedPairs.contains(word)
                        val isSelected = selectedFr == word
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMatched) Color(0xFFDCFCE7) else if (isSelected) Gold else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, if (isSelected) Gold else MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isMatched) { selectedFr = word }
                                .padding(vertical = 4.dp)
                        ) {
                            Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Arabic side
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    arWords.forEach { word ->
                        val isMatched = matchedPairs.any { fr ->
                            (fr == "Père" && word == "أب") ||
                            (fr == "Livre" && word == "كتاب") ||
                            (fr == "École" && word == "مدرسة") ||
                            (fr == "Sœur" && word == "أخت")
                        }
                        val isSelected = selectedAr == word
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMatched) Color(0xFFDCFCE7) else if (isSelected) Gold else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, if (isSelected) Gold else MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isMatched) { selectedAr = word }
                                .padding(vertical = 4.dp)
                        ) {
                            Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// GAME 3: JEU DE MEMOIRE (MEMORY MATCH)
// ----------------------------------------------------------------------------------
@Composable
fun MemoryGameView(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    val itemsList = remember {
        listOf(
            "Chat" to "قط", "Chat" to "قط",
            "Chien" to "كلب", "Chien" to "كلب",
            "Maison" to "منزل", "Maison" to "منزل",
            "Soleil" to "شمس", "Soleil" to "شمس"
        ).shuffled()
    }

    var flippedIndices by remember { mutableStateOf(emptySet<Int>()) }
    var matchedIndices by remember { mutableStateOf(emptySet<Int>()) }
    var scoreAwarded by remember { mutableStateOf(false) }

    LaunchedEffect(flippedIndices) {
        if (flippedIndices.size == 2) {
            val list = flippedIndices.toList()
            val firstWord = itemsList[list[0]].first
            val secondWord = itemsList[list[1]].first
            if (firstWord == secondWord) {
                matchedIndices = matchedIndices + list[0] + list[1]
                viewModel.showToast("Paire trouvée ! 🎉")
            } else {
                kotlinx.coroutines.delay(1000)
            }
            flippedIndices = emptySet()
        }
    }

    LaunchedEffect(matchedIndices.size) {
        if (matchedIndices.size == itemsList.size && !scoreAwarded) {
            scoreAwarded = true
            viewModel.addXp(100)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isAr) "ابحث عن الكلمات المتطابقة لتكسب اللعبة" else "Trouvez les paires de mots identiques !",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        if (matchedIndices.size == itemsList.size) {
            Card(
                colors = CardDefaults.cardColors(containerColor = WarmCream),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🚀 BRAVO !", style = MaterialTheme.typography.titleLarge, color = DeepSlate)
                    Text(
                        text = if (isAr) "عمل مدهش ! لقد تذكرت جميع البطاقات ! +100 XP" else "Mémoire d'acier ! Toutes les cartes sont jumelées ! +100 XP",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = {
                            matchedIndices = emptySet()
                            flippedIndices = emptySet()
                            scoreAwarded = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Gold)
                    ) {
                        Text("Rejouer / إعادة اللعب", color = Color.White)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(itemsList.size) { idx ->
                    val isFlipped = flippedIndices.contains(idx) || matchedIndices.contains(idx)
                    val displayWord = if (isFlipped) itemsList[idx].first else "?"

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (matchedIndices.contains(idx)) Color(0xFFDCFCE7) else if (isFlipped) Gold else DeepSlate
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(100.dp)
                            .clickable(enabled = !isFlipped && flippedIndices.size < 2) {
                                flippedIndices = flippedIndices + idx
                            }
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = displayWord,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isFlipped) DeepSlate else Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// TAB 5: PROFIL & DASHBOARDS
// ----------------------------------------------------------------------------------
@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val posts by viewModel.socialPosts.collectAsStateWithLifecycle()
    val role = progress?.userRole ?: "STUDENT"

    var activeSubDashboard by remember { mutableStateOf(0) } // 0 = Mon Compte, 1 = Forum Social

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(viewModel = viewModel, isAr = isAr)

        // Sub Tab selector
        TabRow(
            selectedTabIndex = activeSubDashboard,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Gold
        ) {
            Tab(
                selected = activeSubDashboard == 0,
                onClick = { activeSubDashboard = 0 },
                text = { Text(if (isAr) "لوحة التحكم ⚙️" else "Tableau de Bord ⚙️") },
                modifier = Modifier.testTag("profile_tab_dashboard")
            )
            Tab(
                selected = activeSubDashboard == 1,
                onClick = { activeSubDashboard = 1 },
                text = { Text(if (isAr) "منتدى التواصل 💬" else "Discussion Forum 💬") },
                modifier = Modifier.testTag("profile_tab_forum")
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (activeSubDashboard) {
                0 -> {
                    // Load corresponding dashboard based on active role
                    when (role) {
                        "STUDENT" -> StudentDashboardView(viewModel = viewModel, isAr = isAr)
                        "PARENT" -> ParentDashboardView(viewModel = viewModel, isAr = isAr)
                        "TEACHER" -> TeacherDashboardView(viewModel = viewModel, isAr = isAr)
                    }
                }
                1 -> {
                    // Discussion Forum Feed
                    ForumFeedView(posts = posts, viewModel = viewModel, isAr = isAr)
                }
            }
        }
    }
}

// STUDENT DASHBOARD
@Composable
fun StudentDashboardView(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val completedCount = progress?.completedCourses?.split(",")?.filter { it.isNotEmpty() }?.size ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isAr) "حساب التلميذ المتفوق" else "Tableau de Bord Élève",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Stats card grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎓", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr) "الدروس المكتملة" else "Cours Terminés",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$completedCount / ${EducationData.courses.size}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎖️", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr) "الرتبة والخبرة" else "Classement & XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${progress?.xpPoints ?: 2450} XP",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                }
            }
        }

        // Complete certificate of excellence generator
        if (completedCount > 0) {
            Text(
                text = if (isAr) "شهادة التفوق الخاصة بك" else "Votre Certificat d'Excellence",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = WarmCream),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(3.dp, Gold),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LogoMH(size = 54)
                    Text(
                        text = "LA PLATEFORME DE MONSIEUR HALIM",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    Text(
                        text = "CERTIFICAT D'EXCELLENCE",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Décerné à l'élève brillant pour avoir complété avec succès les modules de français avancés.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Divider(
                        color = Gold.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Enseignant Créateur : Monsieur Halim",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "« L'excellence n'est pas un acte, c'est une habitude. »",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Button(
                        onClick = { viewModel.showToast("Certificat exporté avec succès dans vos téléchargements ! 📂") },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepSlate),
                        modifier = Modifier.testTag("export_cert_button")
                    ) {
                        Text("Télécharger la Certification / تحميل الشهادة", color = Color.White)
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🔒", fontSize = 36.sp)
                    Text(
                        text = if (isAr) "أكمل درساً واحداً على الأقل لفتح شهادتك!" else "Complétez au moins 1 cours pour débloquer votre certificat !",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// PARENT DASHBOARD
@Composable
fun ParentDashboardView(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isAr) "فضاء أولياء الأمور المتميز" else "Espace Parent d'Élève",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Progress breakdown
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isAr) "تقرير نشاط الطفل" else "Rapport d'activité de l'enfant",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isAr) "الحضور والمواظبة" else "Présence générale", style = MaterialTheme.typography.bodyMedium)
                    Text("98% (Excellent)", color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isAr) "الواجبات المنزلية المكتملة" else "Devoirs rendus", style = MaterialTheme.typography.bodyMedium)
                    Text("12 / 12", fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isAr) "آخر ملاحظات المعلم" else "Commentaire de M. Halim", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (isAr) "ذكاء ومثابرة ممتازة !" else "Élève motivé et appliqué !",
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                }
            }
        }

        // Direct parent-teacher simulated contact form
        Card(
            colors = CardDefaults.cardColors(containerColor = WarmCream),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isAr) "تواصل مباشرة مع الأستاذ حليم" else "Contacter Monsieur Halim",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DeepSlate
                )

                var parentMsgText by remember { mutableStateOf("") }
                TextField(
                    value = parentMsgText,
                    onValueChange = { parentMsgText = it },
                    placeholder = { Text(if (isAr) "اكتب رسالتك هنا للأستاذ..." else "Votre message...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                Button(
                    onClick = {
                        viewModel.showToast("Message envoyé à Monsieur Halim ! ✉️")
                        parentMsgText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepSlate),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_send_teacher")
                ) {
                    Text(if (isAr) "إرسال الرسالة" else "Envoyer le Message", color = Color.White)
                }
            }
        }
    }
}

// TEACHER DASHBOARD
@Composable
fun TeacherDashboardView(
    viewModel: AppViewModel,
    isAr: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isAr) "لوحة تحكم الأستاذ حليم" else "Espace Administration Enseignant",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Teacher stats
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👨‍🎓", fontSize = 28.sp)
                    Text(
                        text = if (isAr) "مجموع الطلاب" else "Élèves inscrits",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text("1,240", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Gold)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📈", fontSize = 28.sp)
                    Text(
                        text = if (isAr) "نسبة النجاح" else "Taux de réussite",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text("96.4%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Gold)
                }
            }
        }

        // Publish announcements / upload mock handouts
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isAr) "نشر إعلان جديد للطلبة والأولياء" else "Publier une annonce générale",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                var announcementText by remember { mutableStateOf("") }
                TextField(
                    value = announcementText,
                    onValueChange = { announcementText = it },
                    placeholder = { Text(if (isAr) "اكتب الإعلان هنا..." else "Contenu de l'annonce...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (announcementText.isNotBlank()) {
                            viewModel.addPost(announcementText)
                            announcementText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_publish_button")
                ) {
                    Text(if (isAr) "نشر الآن" else "Publier maintenant", color = Color.White)
                }
            }
        }
    }
}

// FORUM FEED VIEW
@Composable
fun ForumFeedView(
    posts: List<SocialPost>,
    viewModel: AppViewModel,
    isAr: Boolean
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Post creator
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var postText by remember { mutableStateOf("") }
                TextField(
                    value = postText,
                    onValueChange = { postText = it },
                    placeholder = { Text(if (isAr) "شارك رأيك أو سؤالك هنا..." else "Posez une question ou partagez...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )

                IconButton(
                    onClick = {
                        if (postText.isNotBlank()) {
                            viewModel.addPost(postText)
                            postText = ""
                        }
                    },
                    modifier = Modifier
                        .background(Gold, CircleShape)
                        .testTag("forum_post_send"),
                    enabled = postText.isNotBlank()
                ) {
                    Icon(Icons.Filled.Send, contentDescription = "Post", tint = Color.White)
                }
            }
        }

        // List of community posts
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(posts) { post ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(GoldLight, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(if (post.authorName.contains("Halim")) "👨‍🏫" else "👤")
                                }
                                Column {
                                    Text(text = post.authorName, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = post.authorRole,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = post.text, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.likePost(post) }) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Filled.Favorite, contentDescription = "Like", tint = Color.Red)
                                    Text("${post.likesCount}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
