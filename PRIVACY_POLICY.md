# ShortsBlocker 개인정보처리방침 / Privacy Policy

최종 수정일 / Last updated: 2026-07-08

## 한국어

**ShortsBlocker는 어떠한 개인정보도 수집, 저장, 전송하지 않습니다.**

### 수집하는 정보
없음. ShortsBlocker는 인터넷 권한(INTERNET permission)이 아예 없는 앱으로, 어떤 데이터도 외부로 전송하는 것이 기술적으로 불가능합니다.

### 접근성 서비스(AccessibilityService) 사용에 대한 안내
ShortsBlocker는 유튜브 쇼츠, 인스타그램 릴스, 페이스북 릴스 화면을 감지하고 차단하기 위해 Android 접근성 서비스를 사용합니다.

- 접근성 서비스는 오직 유튜브(com.google.android.youtube), 인스타그램(com.instagram.android), 페이스북(com.facebook.katana) 3개 앱의 화면 구조(UI 요소)만 확인합니다.
- 화면에서 읽은 정보는 숏폼 콘텐츠 화면인지 판별하는 데에만 즉시 사용되며, 저장되거나 기록되거나 전송되지 않습니다.
- 비밀번호, 메시지 내용, 개인 정보 등은 수집하지 않습니다.
- 모든 처리는 기기 내부에서만(온디바이스) 이루어집니다.

### 앱 설정 데이터
차단 대상 앱 선택, 차단 방식 등 사용자가 지정한 설정은 기기 내부 저장소에만 보관되며 앱을 삭제하면 함께 삭제됩니다. 숏폼 시청 시간 기록(일별 합계) 역시 기기 내부에만 저장됩니다.

### 사용 정보 접근 권한(PACKAGE_USAGE_STATS)
'오늘의 전체 앱 사용 시간' 기능을 사용하도록 선택한 경우, ShortsBlocker는 Android 시스템의 사용 정보 접근 권한을 통해 기기에 기록된 앱별 사용 시간을 읽어 화면에 표시합니다. 이 정보는 조회 즉시 화면 표시에만 사용되며, 저장·기록·전송되지 않습니다. 권한을 허용하지 않아도 차단 기능은 정상 작동합니다.

### 제3자 제공
없음. 광고 SDK, 분석 도구, 외부 서버가 일절 포함되어 있지 않습니다.

### 문의
개인정보처리방침에 대한 문의: GitHub 이슈(https://github.com/Saul-Kim-Goodman/ShortsBlocker/issues)

## English

**ShortsBlocker does not collect, store, or transmit any personal data.**

### Data We Collect
None. ShortsBlocker has no INTERNET permission, making it technically impossible for the app to send any data off your device.

### About the Accessibility Service
ShortsBlocker uses the Android AccessibilityService API solely to detect and block short-form video screens (YouTube Shorts, Instagram Reels, Facebook Reels).

- The service only inspects UI structure of three apps: YouTube, Instagram, and Facebook.
- Screen information is used instantly and only to determine whether a short-form feed is displayed. It is never stored, logged, or transmitted.
- No passwords, messages, or personal information are collected.
- All processing happens on-device.

### App Settings
Your preferences (which apps to block, block mode, etc.) and your daily short-form watch-time totals are stored only in local device storage and are deleted when you uninstall the app.

### Usage Access Permission (PACKAGE_USAGE_STATS)
If you opt in to the "today's app usage" feature, ShortsBlocker reads per-app usage times recorded by the Android system via the Usage Access permission, solely to display them on screen. This information is never stored, logged, or transmitted. The blocking features work fully without this permission.

### Third Parties
None. The app contains no ads, no analytics, and no external servers.

### Contact
For questions about this policy, please open an issue at https://github.com/Saul-Kim-Goodman/ShortsBlocker/issues
