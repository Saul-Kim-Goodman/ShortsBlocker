package io.github.saulkimgoodman.shortsblocker.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation3.runtime.NavKey
import io.github.saulkimgoodman.shortsblocker.BlockerAccessibilityService
import io.github.saulkimgoodman.shortsblocker.PreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val prefs = remember { PreferencesManager(context) }

    // Service & Battery states checked on start / resume
    var isServiceActive by remember { mutableStateOf(false) }
    var isIgnoringBattery by remember { mutableStateOf(false) }

    fun refreshStates() {
        isServiceActive = checkAccessibilityServiceActive(context)
        isIgnoringBattery = checkBatteryOptimizationIgnored(context)
    }

    // Refresh states when the screen becomes visible
    LifecycleStartEffect(Unit) {
        refreshStates()
        onStopOrDispose { }
    }

    // Preferences states
    var masterEnabled by remember { mutableStateOf(prefs.isMasterEnabled) }
    var ytEnabled by remember { mutableStateOf(prefs.isYouTubeEnabled) }
    var igEnabled by remember { mutableStateOf(prefs.isInstagramEnabled) }
    var fbEnabled by remember { mutableStateOf(prefs.isFacebookEnabled) }
    var blockMode by remember { mutableStateOf(prefs.blockMode) }
    var customMsg by remember { mutableStateOf(prefs.customMessage) }
    var debugEnabled by remember { mutableStateOf(prefs.isDebugMode) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ShortsBlocker",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Accessibility Service Status Card
            StatusCard(
                isActive = isServiceActive,
                onGoToSettings = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                },
                onRefresh = { refreshStates() }
            )

            // 2. Global Master ON/OFF Toggle Card
            MasterToggleCard(
                enabled = masterEnabled,
                onEnabledChanged = {
                    masterEnabled = it
                    prefs.isMasterEnabled = it
                }
            )

            // 3. Block Targets Toggle Card
            TargetsCard(
                enabled = masterEnabled, // Disable interactions if master is OFF
                ytEnabled = ytEnabled,
                onYtChanged = {
                    ytEnabled = it
                    prefs.isYouTubeEnabled = it
                },
                igEnabled = igEnabled,
                onIgChanged = {
                    igEnabled = it
                    prefs.isInstagramEnabled = it
                },
                fbEnabled = fbEnabled,
                onFbChanged = {
                    fbEnabled = it
                    prefs.isFacebookEnabled = it
                }
            )

            // 4. Blocking Action Options Card
            ActionConfigCard(
                enabled = masterEnabled, // Disable if master is OFF
                blockMode = blockMode,
                onBlockModeChanged = {
                    blockMode = it
                    prefs.blockMode = it
                },
                customMsg = customMsg,
                onCustomMsgChanged = {
                    customMsg = it
                    prefs.customMessage = it
                },
                onFocusClear = { focusManager.clearFocus() }
            )

            // 5. Optimization & Onboarding Card
            OnboardingCard(
                context = context,
                isIgnoringBattery = isIgnoringBattery,
                onRefreshStates = { refreshStates() }
            )

            // 6. Debug Mode Toggle Card
            DebugCard(
                debugEnabled = debugEnabled,
                onDebugChanged = {
                    debugEnabled = it
                    prefs.isDebugMode = it
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatusCard(
    isActive: Boolean,
    onGoToSettings: () -> Unit,
    onRefresh: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isActive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = if (isActive) "접근성 서비스 활성화됨" else "접근성 서비스 비활성화됨",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onRefresh) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "새로고침")
                }
            }

            Text(
                text = "앱의 정상 작동을 위해서는 반드시 접근성 서비스를 켜야 합니다. 아래 버튼을 눌러 목록에서 ShortsBlocker를 찾아서 활성화해 주세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onGoToSettings,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("접근성 설정 화면으로 이동", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun MasterToggleCard(
    enabled: Boolean,
    onEnabledChanged: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (enabled) "차단 서비스 작동 중" else "차단 서비스 일시 정지됨",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (enabled) "지정한 앱들의 숏폼 차단이 켜진 상태입니다." else "차단 기능이 일시적으로 꺼진 상태입니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun TargetsCard(
    enabled: Boolean,
    ytEnabled: Boolean,
    onYtChanged: (Boolean) -> Unit,
    igEnabled: Boolean,
    onIgChanged: (Boolean) -> Unit,
    fbEnabled: Boolean,
    onFbChanged: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (enabled) 1f else 0.5f), // Dim if master is disabled
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "차단할 소셜 미디어 앱",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            TargetToggleItem(
                title = "유튜브 쇼츠",
                description = "YouTube Shorts 피드 진입 시 차단",
                checked = ytEnabled && enabled,
                enabled = enabled,
                onCheckedChange = onYtChanged
            )
            TargetToggleItem(
                title = "인스타그램 릴스",
                description = "Instagram Reels 탭 및 피드 진입 시 차단",
                checked = igEnabled && enabled,
                enabled = enabled,
                onCheckedChange = onIgChanged
            )
            TargetToggleItem(
                title = "페이스북 릴스",
                description = "Facebook Reels 뷰어 피드 진입 시 차단",
                checked = fbEnabled && enabled,
                enabled = enabled,
                onCheckedChange = onFbChanged
            )
        }
    }
}

@Composable
fun TargetToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun ActionConfigCard(
    enabled: Boolean,
    blockMode: String,
    onBlockModeChanged: (String) -> Unit,
    customMsg: String,
    onCustomMsgChanged: (String) -> Unit,
    onFocusClear: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (enabled) 1f else 0.5f), // Dim if master is disabled
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "차단 동작 설정",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider()

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = blockMode == "BACK",
                        enabled = enabled,
                        onClick = { if (enabled) onBlockModeChanged("BACK") }
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        Text("즉시 뒤로가기 (기본)", fontWeight = FontWeight.Medium)
                        Text("숏폼 감지 시 뒤로가기 키를 눌러 차단", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = blockMode == "OVERLAY",
                        enabled = enabled,
                        onClick = { if (enabled) onBlockModeChanged("OVERLAY") }
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        Text("전체화면 차단 안내 창 (오버레이)", fontWeight = FontWeight.Medium)
                        Text("숏폼 위에 닫기 버튼이 포함된 메시지 화면 표시", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            AnimatedVisibility(
                visible = blockMode == "OVERLAY" && enabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "오버레이 문구 설정",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = customMsg,
                        onValueChange = onCustomMsgChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = enabled,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onFocusClear() }),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingCard(
    context: Context,
    isIgnoringBattery: Boolean,
    onRefreshStates: () -> Unit
) {
    var expandedSideload by remember { mutableStateOf(false) }
    var expandedSamsung by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "배터리 및 OS 가이드 (안정적 백그라운드 구동)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider()

            // Battery Optimization status and action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("배터리 최적화 예외 상태", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = if (isIgnoringBattery) "제한 없음 설정 완료" else "배터리 최적화 활성화 중 (꺼질 수 있음)",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isIgnoringBattery) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                    )
                }
                if (!isIgnoringBattery) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback to Application settings if direct permission request fails
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("예외 허용 요청")
                    }
                }
            }

            // Samsung Battery optimization guide helper
            ExpandableGuide(
                title = "삼성(One UI) 배터리 예외 추가 안내",
                expanded = expandedSamsung,
                onToggle = { expandedSamsung = !expandedSamsung }
            ) {
                Text(
                    text = "삼성 갤럭시 기기에서 접근성 서비스가 며칠 후 조용히 멈추는 것을 방지하려면:\n" +
                            "1. 스마트폰 설정 > 배터리 > 이 앱 > '제한 없음(Unrestricted)'으로 설정합니다.\n" +
                            "2. 스마트폰 설정 > 배터리 > 백그라운드 사용 제한 > '절전 모드로 전환되지 않는 앱'에 ShortsBlocker를 추가합니다.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }

            // Android 13+ sideloading restricted settings guide
            ExpandableGuide(
                title = "안드로이드 13+ 제한된 설정(Restricted Settings) 안내",
                expanded = expandedSideload,
                onToggle = { expandedSideload = !expandedSideload }
            ) {
                Text(
                    text = "직접 APK를 설치(사이드로딩) 시 접근성 켤 때 경고가 발생하는 경우:\n" +
                            "1. 스마트폰 설정 > 애플리케이션 > ShortsBlocker 선택\n" +
                            "2. 우측 상단 더보기 버튼(점 3개 ⋮)을 선택\n" +
                            "3. '제한된 설정 허용'을 터치하고 본인인증 진행\n" +
                            "4. 접근성 설정으로 돌아가 스위치를 활성화합니다.\n\n" +
                            "※ 타깃 Galaxy S23 (One UI 8.5/Android 16 QPR2) 이상 기기에서 사이드로딩이 안 되면 '설정 > 보안 및 개인정보 보호 > 보안 위험 차단기(Auto Blocker)'를 잠시 해제해 주세요.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun ExpandableGuide(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, fontSize = 14.sp)
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun DebugCard(
    debugEnabled: Boolean,
    onDebugChanged: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("디버그 모드", fontWeight = FontWeight.Bold)
                    Text("활성화 시 노드 정보를 Logcat에 출력합니다.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = debugEnabled,
                    onCheckedChange = onDebugChanged
                )
            }
            if (debugEnabled) {
                Text(
                    text = "Logcat 태그: ShortsBlockerDebug\n" +
                            "소셜 미디어 앱 실행 후 화면을 움직이면 화면에 렌더링된 컴포넌트의 클래스명, Resource ID, 텍스트, 설명이 출력됩니다. 이를 통해 새 차단 시그니처를 발굴할 수 있습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// Helper utilities to check states
private fun checkAccessibilityServiceActive(context: Context): Boolean {
    val serviceClass = BlockerAccessibilityService::class.java
    val expectedComponentName = ComponentName(context, serviceClass)
    val enabledServicesSetting = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServicesSetting)
    while (colonSplitter.hasNext()) {
        val componentNameString = colonSplitter.next()
        val enabledService = ComponentName.unflattenFromString(componentNameString)
        if (enabledService != null && enabledService == expectedComponentName) {
            return true
        }
    }
    return false
}

private fun checkBatteryOptimizationIgnored(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
}
