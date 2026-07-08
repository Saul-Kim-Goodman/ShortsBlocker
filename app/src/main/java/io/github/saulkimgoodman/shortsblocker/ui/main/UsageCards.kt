package io.github.saulkimgoodman.shortsblocker.ui.main

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.saulkimgoodman.shortsblocker.PreferencesManager
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class AppUsageEntry(val label: String, val packageName: String, val ms: Long)

fun formatDuration(ms: Long): String {
    val totalMinutes = ms / 60_000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val seconds = (ms % 60_000) / 1000
    return when {
        hours > 0 -> "${hours}시간 ${minutes}분"
        totalMinutes > 0 -> "${minutes}분 ${seconds}초"
        else -> "${seconds}초"
    }
}

fun hasUsageAccessPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName
        )
    } else {
        @Suppress("DEPRECATION")
        appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName
        )
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

/** Today's foreground time per app via UsageStatsManager, heaviest first. */
fun queryTodayAppUsage(context: Context, limit: Int = 8): List<AppUsageEntry> {
    val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val stats = usm.queryAndAggregateUsageStats(start, System.currentTimeMillis())
    val pm = context.packageManager
    return stats.values
        .filter { it.totalTimeInForeground >= 60_000L } // hide < 1 minute noise
        .sortedByDescending { it.totalTimeInForeground }
        .take(limit)
        .map { s ->
            val label = try {
                pm.getApplicationLabel(pm.getApplicationInfo(s.packageName, 0)).toString()
            } catch (e: Exception) {
                s.packageName.substringAfterLast('.')
            }
            AppUsageEntry(label, s.packageName, s.totalTimeInForeground)
        }
}

@Composable
fun LimitModeCard(
    enabled: Boolean,
    limitMode: String,
    onLimitModeChanged: (String) -> Unit,
    dailyLimitMinutes: Int,
    onLimitMinutesChanged: (Int) -> Unit,
    todayTotalMs: Long
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (enabled) 1f else 0.5f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "차단 방식",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = limitMode == PreferencesManager.LIMIT_MODE_ALWAYS,
                    enabled = enabled,
                    onClick = { if (enabled) onLimitModeChanged(PreferencesManager.LIMIT_MODE_ALWAYS) }
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text("항상 차단 (기본)", fontWeight = FontWeight.Medium)
                    Text(
                        "숏폼 화면에 들어가는 즉시 차단합니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = limitMode == PreferencesManager.LIMIT_MODE_DAILY,
                    enabled = enabled,
                    onClick = { if (enabled) onLimitModeChanged(PreferencesManager.LIMIT_MODE_DAILY) }
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text("하루 허용 시간", fontWeight = FontWeight.Medium)
                    Text(
                        "정해진 시간만큼은 허용하고, 다 쓰면 차단합니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (limitMode == PreferencesManager.LIMIT_MODE_DAILY) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(5, 10, 15, 30, 60).forEach { minutes ->
                        FilterChip(
                            selected = dailyLimitMinutes == minutes,
                            enabled = enabled,
                            onClick = { onLimitMinutesChanged(minutes) },
                            label = { Text("${minutes}분") }
                        )
                    }
                }

                val limitMs = dailyLimitMinutes * 60_000L
                val remaining = (limitMs - todayTotalMs).coerceAtLeast(0L)
                val fraction = (todayTotalMs.toFloat() / limitMs).coerceIn(0f, 1f)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (remaining == 0L) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "오늘 사용: ${formatDuration(todayTotalMs)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = if (remaining == 0L) "오늘 허용량 소진" else "남은 시간: ${formatDuration(remaining)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (remaining == 0L) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UsageStatsCard(
    ytMs: Long,
    igMs: Long,
    fbMs: Long,
    recentDays: List<Pair<LocalDate, Long>>,
    hasUsagePermission: Boolean,
    appUsage: List<AppUsageEntry>,
    onRequestUsagePermission: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "사용 시간 기록",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider()

            Text("오늘의 숏폼 시청", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            UsageRow("유튜브 쇼츠", ytMs)
            UsageRow("인스타그램 릴스", igMs)
            UsageRow("페이스북 릴스", fbMs)

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text("최근 7일 숏폼 시청", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            val maxMs = (recentDays.maxOfOrNull { it.second } ?: 0L).coerceAtLeast(1L)
            val dayFormatter = DateTimeFormatter.ofPattern("M/d")
            recentDays.forEach { (date, ms) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        date.format(dayFormatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(36.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ms.toFloat() / maxMs)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                    Text(
                        if (ms == 0L) "-" else formatDuration(ms),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(72.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text("오늘의 전체 앱 사용 시간", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            if (!hasUsagePermission) {
                Text(
                    "휴대전화 전체 앱의 사용 시간을 보려면 '사용 정보 접근' 권한이 필요합니다. 이 정보는 기기 안에서만 표시되며 어디에도 전송되지 않습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onRequestUsagePermission,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("사용 정보 접근 허용하기", fontWeight = FontWeight.SemiBold)
                }
            } else if (appUsage.isEmpty()) {
                Text(
                    "오늘 1분 이상 사용한 앱이 아직 없습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                appUsage.forEach { entry -> UsageRow(entry.label, entry.ms) }
            }
        }
    }
}

@Composable
private fun UsageRow(label: String, ms: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            if (ms == 0L) "-" else formatDuration(ms),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (ms == 0L) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
        )
    }
}
