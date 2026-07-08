# Google Play 등록 자료 (Play Console에 복사해서 사용)

## 기본 정보

| 항목 | 값 |
|---|---|
| 앱 이름 (30자 이내) | ShortsBlocker - 쇼츠·릴스 차단 |
| 패키지명 (applicationId) | io.github.saulkimgoodman.shortsblocker |
| 카테고리 | 생산성 (Productivity) |
| 이메일 (필수 공개) | charles25strain@gmail.com |
| 개인정보처리방침 URL | https://github.com/Saul-Kim-Goodman/ShortsBlocker/blob/main/PRIVACY_POLICY.md |
| 가격 | 무료 |
| 광고 포함 여부 | 아니요 |

## 짧은 설명 (80자 이내)

**한국어:**
> 유튜브 쇼츠, 인스타 릴스, 페북 릴스만 골라서 차단. 나머지 기능은 그대로.

**English:**
> Block only YouTube Shorts, Instagram Reels & Facebook Reels. Everything else works.

## 전체 설명 (4000자 이내)

**한국어:**

> 숏폼에 빼앗긴 시간을 되찾으세요.
>
> ShortsBlocker는 유튜브 쇼츠, 인스타그램 릴스, 페이스북 릴스 화면만 정확히 감지해서 차단합니다. 앱 전체를 막는 것이 아니라 숏폼 피드 진입만 차단하기 때문에 동영상 시청, DM, 게시물 보기 등 나머지 기능은 평소처럼 쓸 수 있습니다.
>
> ■ 주요 기능
> • 앱별 개별 차단: 유튜브 / 인스타그램 / 페이스북 각각 켜고 끌 수 있습니다
> • 두 가지 차단 방식: 즉시 뒤로가기 또는 차단 안내 화면(오버레이) 표시
> • 나만의 차단 메시지: 차단 화면에 표시할 문구를 직접 설정
> • 마스터 스위치: 한 번에 전체 켜기/끄기
>
> ■ 강력한 프라이버시 보호
> • 인터넷 권한이 아예 없습니다 — 어떤 데이터도 밖으로 나갈 수 없습니다
> • 모든 처리는 기기 안에서만 이루어집니다
> • 광고 없음, 추적 없음, 분석 도구 없음
> • 오픈소스: https://github.com/Saul-Kim-Goodman/ShortsBlocker
>
> ■ 가볍고 효율적
> • 화면 감지 로직을 최적화하여 배터리와 CPU 사용을 최소화했습니다
> • 유튜브·인스타그램·페이스북 3개 앱에서만 동작하며 다른 앱에는 일절 관여하지 않습니다
>
> ■ 접근성 서비스 사용 안내
> ShortsBlocker는 숏폼 화면을 감지하기 위해 Android 접근성 서비스를 사용합니다. 이 권한은 오직 유튜브·인스타그램·페이스북의 화면 구조를 확인해 쇼츠/릴스 여부를 판별하는 데에만 사용되며, 어떤 정보도 저장하거나 전송하지 않습니다.

**English:**

> Take back the hours lost to short-form feeds.
>
> ShortsBlocker precisely detects and blocks YouTube Shorts, Instagram Reels, and Facebook Reels — and nothing else. Because it blocks only the short-form feed itself, you can keep using regular videos, DMs, and posts as usual.
>
> ■ Features
> • Per-app control: toggle YouTube / Instagram / Facebook independently
> • Two block modes: instant back-navigation, or a friendly block screen (overlay)
> • Custom block message: write your own reminder on the block screen
> • Master switch: enable/disable everything at once
>
> ■ Privacy first
> • No INTERNET permission at all — your data physically cannot leave the device
> • 100% on-device processing
> • No ads, no tracking, no analytics
> • Open source: https://github.com/Saul-Kim-Goodman/ShortsBlocker
>
> ■ Light & efficient
> • Optimized screen detection keeps battery and CPU usage minimal
> • Runs only inside YouTube, Instagram, and Facebook — never touches other apps
>
> ■ About the accessibility service
> ShortsBlocker uses the Android accessibility service to detect short-form screens. It is used solely to inspect the UI of YouTube, Instagram, and Facebook to decide whether a Shorts/Reels screen is showing. Nothing is stored or transmitted.

## 접근성 API 사용 신고 (Play Console 앱 콘텐츠 → 접근성 API 선언)

Play Console에서 AccessibilityService API 사용 목적을 물으면 아래와 같이 답변:

> ShortsBlocker is a digital wellbeing app. It uses the AccessibilityService API as its core functionality: detecting when the user enters a short-form video feed (YouTube Shorts, Instagram Reels, Facebook Reels) and helping the user avoid it, per the user's own configuration. The service is restricted via android:packageNames to exactly three apps (com.google.android.youtube, com.instagram.android, com.facebook.katana). The app reads UI node structure only to classify the current screen; it does not collect, store, or transmit any data, and it has no INTERNET permission. This use qualifies as an accessibility/digital-wellbeing tool that helps users manage compulsive content consumption.

* IsAccessibilityTool 선언: 아니요(No)로 답하되, 위 설명으로 "핵심 기능(core functionality)" 정당성을 제시
* 사용자 데이터 수집: 없음

## 데이터 보안 (Data Safety) 폼 응답

- 데이터를 수집하나요? → **아니요**
- 데이터를 공유하나요? → **아니요**
- 데이터가 전송 중 암호화되나요? → 해당 없음 (전송 자체가 없음)
- 데이터 삭제 요청 방법 제공? → 해당 없음 (수집 데이터 없음)

## 콘텐츠 등급 설문 가이드

- 카테고리: 유틸리티/생산성/커뮤니케이션 기타
- 폭력성/선정성/약물/도박/비속어: 전부 없음
- 사용자 간 상호작용/위치 공유/개인정보 공유: 없음
- 예상 등급: 전체이용가 (3+)

## 필요한 그래픽 자산 (직접 준비 필요)

| 자산 | 규격 | 비고 |
|---|---|---|
| 앱 아이콘 | 512×512 PNG (32bit, 알파 허용) | 필수 |
| 피처 그래픽 | 1024×500 PNG/JPG | 필수 |
| 휴대전화 스크린샷 | 최소 2장, 각 변 320~3840px, 비율 16:9~9:16 | 필수 — 실제 기기/에뮬레이터에서 캡처 |
| 7인치/10인치 태블릿 스크린샷 | 선택 | 태블릿 지원 표시를 원하면 |

## 출시 절차 체크리스트

1. [ ] Google Play Console 개발자 계정 생성 (https://play.google.com/console) — 등록비 $25 (1회), 신분증 본인 인증 필요
2. [ ] "앱 만들기" → 이름/언어(한국어)/앱/무료 선택
3. [ ] 앱 콘텐츠 (정책 → 앱 콘텐츠): 개인정보처리방침 URL, 광고 없음, 콘텐츠 등급 설문, 타겟층(18세 이상 권장 아님 — 전체), 데이터 보안 폼, 접근성 API 선언 작성
4. [ ] 스토어 등록정보: 위의 설명문 + 그래픽 업로드
5. [ ] 프로덕션(또는 비공개 테스트) 트랙 → app-release.aab 업로드
6. [ ] 검토 제출 (심사 보통 1~7일, 접근성 API 사용 앱은 추가 검토 가능)

**주의**: 2023년 11월 이후 생성된 개인 개발자 계정은 프로덕션 출시 전 **비공개 테스트(20명 테스터 × 14일)** 요건이 있습니다. 지인 20명을 테스터로 모으거나, 테스터 모집 커뮤니티를 활용하세요.
