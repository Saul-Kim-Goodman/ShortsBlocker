# ShortsBlocker 🚫📱

ShortsBlocker는 **유튜브 쇼츠(YouTube Shorts), 인스타그램 릴스(Instagram Reels), 페이스북 릴스(Facebook Reels)의 피드(동영상 플레이어) 진입만 차단**하고, 각 앱의 나머지 핵심 기능(검색, 일반 피드 동영상 시청, DM, 스토리, 게시물 업로드 등)은 100% 정상 작동하도록 돕는 오픈소스 안드로이드 네이티브 앱입니다.

100% 온디바이스(On-device)로 처리되어 **인터넷 권한을 전혀 사용하지 않는 강력한 프라이버시 보호**와 함께, 배터리 및 CPU 소모를 극도로 최소화하도록 최적화 설계되었습니다.

---

## ✨ 주요 기능 및 특징

1. **소셜 미디어별 맞춤 감지 및 차단**
   * **유튜브 쇼츠**: 전체화면 Shorts 플레이어 감지 시 차단
   * **인스타그램 릴스**: 하단 릴스 탭 및 피드 릴스 감지 시 차단 (※ 메인 피드에 노출되는 일반 비디오 글은 정상 허용)
   * **페이스북 릴스**: 전체화면 릴스 플레이어 감지 시 차단

2. **개인 맞춤형 차단 방식 선택**
   * **즉시 뒤로가기 (Back action)**: 숏폼 로드 시 즉각 `GLOBAL_ACTION_BACK` 신호를 호출해 이전 화면으로 튕겨냅니다.
   * **전체화면 오버레이 (Overlay action)**: 숏폼 위에 차단 안내 카드 레이아웃을 씌우고 '돌아가기' 버튼 터치를 유도합니다.

3. **강력한 프라이버시 보호 (No Internet)**
   * 앱 매니페스트에 `INTERNET` 권한을 요청하지 않습니다. 화면 감지, 차단, 설정 저장 등 모든 데이터 처리가 완전히 오프라인 상태로 기기 내부에서만 수행됩니다.

4. **배터리 & CPU 경량화 최적화 (Battery-Saving)**
   * **스캔 주기 조절 (Throttling)**: 스크롤 중 무수히 튀는 화면 갱신 이벤트를 최대 150ms당 1회로 병목 제어하여 CPU 점유율을 90% 이상 절감합니다.
   * **탐색 깊이 제한 (Max Depth Limit)**: 차단 화면이 상위 레이아웃(10단계 이내)에 배치되는 특징을 살려, 10단계 이하 하위 뷰 탐색을 완전히 프루닝(Pruning) 처리했습니다.
   * **비활성 노드 무시 (`isVisibleToUser`)**: 화면 트랜지션 중 백그라운드나 가상 뷰 캐시에 남아있는 보이지 않는 레이아웃 노드를 완전 무시하여 뒤로가기가 중복 실행되어 앱이 꺼지는 문제를 방지합니다.

5. **실시간 ID 수집을 위한 디버그 모드 (Debug Mode)**
   * 활성화 시 스마트폰의 현재 화면 컴포넌트 구조(Resource-ID, 클래스명, 텍스트, 설명)를 Logcat(`ShortsBlockerDebug` 태그)에 트리 형태로 깔끔하게 덤프하여, 차후 앱 업데이트로 ID가 바뀌었을 때 시그니처 수정이 용이하게 돕습니다.

---

## 🛠️ 기술 스택 및 구조

- **Language**: Kotlin 100%
- **minSdk**: 26 (Android 8.0 Oreo)
- **targetSdk**: 36
- **UI Framework**: Jetpack Compose (Material3)
- **Background Core**: Android AccessibilityService
- **Preferences**: SharedPreferences (PreferencesManager)

---

## 🚀 설치 및 설정 가이드 (Android 13+ / Samsung One UI)

앱을 설치한 후 백그라운드에서 오작동 없이 안정적으로 구동하기 위해 아래 설정을 반드시 권장합니다.

### 1. 접근성 서비스 켜기 & 제한된 설정 해제 (Android 13+)
직접 APK로 다운로드하여 설치(사이드로딩)하는 경우 구글 정책상 접근성 토글이 비활성화됩니다.
1. 스마트폰 **설정 > 애플리케이션 > ShortsBlocker**로 이동합니다.
2. 우측 상단 **더보기 아이콘(점 3개 ⋮)**을 선택하고 **'제한된 설정 허용'**을 터치합니다.
3. 본인인증(지문/PIN)을 마칩니다.
4. 다시 **설정 > 접근성 > 설치된 앱 > ShortsBlocker**로 이동하여 스위치를 **사용 중(ON)**으로 켭니다.

### 2. 삼성 갤럭시 배터리 최적화 해제 안내 (One UI)
삼성 스마트폰의 강박적인 백그라운드 절전 정책으로 인해 서비스가 자동 종료되는 현상을 막아야 합니다.
1. 스마트폰 **설정 > 배터리 > 이 앱 (ShortsBlocker) > '제한 없음(Unrestricted)'**으로 설정합니다.
2. 스마트폰 **설정 > 배터리 > 백그라운드 사용 제한 > '절전 모드로 전환되지 않는 앱'**에 이 앱을 추가합니다.
3. 앱 화면 내 **'예외 허용 요청'** 버튼을 터치하여 시스템 배터리 최적화 팝업에서 예외 승인을 완료합니다.

---

## 📂 프로젝트 폴더 구조

```text
ShortsBlocker/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/shortsblocker/
│   │   │   │   ├── MainActivity.kt                 # 메인 화면 및 설정 상태 관리
│   │   │   │   ├── BlockerAccessibilityService.kt  # 실시간 화면 감지 및 차단 수행 서비스
│   │   │   │   ├── ShortFormDetector.kt            # 각 소셜 미디어 감지 로직 추상화 구현체
│   │   │   │   ├── DetectionSignatures.kt          # 업데이트 대응용 ID/텍스트 규칙 상수 분리
│   │   │   │   ├── PreferencesManager.kt           # 차단 설정 값 로컬 저장소
│   │   │   │   └── BlockOverlayActivity.kt         # 오버레이 모드 선택 시 출력되는 팝업 UI
│   │   │   ├── res/
│   │   │   │   ├── xml/accessibility_service_config.xml # 감지 패키지 및 이벤트 범위 설정
│   │   │   │   └── values/strings.xml              # 접근성 서비스 이름 및 설명
│   │   │   └── AndroidManifest.xml                 # 퍼미션 및 컴포넌트 선언
│   │   └── build.gradle.kts                        # 앱 종속성 및 Compose 빌드 설정
│   └── build.gradle.kts                            # 프로젝트 레벨 빌드
└── settings.gradle.kts                             # 의존성 레포지토리 관리
```

---

## 📄 라이선스

이 프로젝트는 오픈소스 소프트웨어이며 개인적인 학습, 배포, 커스터마이징이 완전 무료로 허용됩니다.
