# 교대근무 루틴 앱 (CLAUDE.md)

## 앱의 목적 (정체성)
교대근무자가 스케줄을 편하게 짜고, 잘 지킬 수 있게 하는 스케줄러.
루틴화는 목적이 아니라 '잘 지키기'를 위한 도구다.
- 이 앱은 '앞으로'만 본다: 오늘과 다음만 다룬다.
- 회고·성찰·통계 대시보드는 만들지 않는다(그건 노트 앱의 몫).
  성취율 숫자 정도의 '슬쩍 보는 신호'까지만 허용.

## 앱의 경계 기준 (기능 추가 판단)
"빠르게 확인·체크·처리 = 이 앱 / 앉아서 오래 정리·기록 = 다른 앱(옵시디언 등)"
판단법: 이 기능을 쓰려면 앱을 오래 붙잡아야 하나?
붙잡아야 하면 넣지 않는다. 슬쩍 보고 닫을 수 있으면 넣는다.

## 핵심 시스템 (확정 — 모든 기능·화면은 이 흐름을 따른다)
교대근무자는 '넣기'(할 일 떠올림)와 '놓기'(언제 할지)가 분리된다.
근무표를 봐야 놓을 수 있기 때문. 이 분리를 잇는 것이 이 앱의 심장.

1) 넣기: 어디서든 빈칸에 글 + 반복 O/X 버튼만으로 즉시 추가.
   - 특정 날짜 화면(오늘 탭 / 날짜 상세)에서 추가하면 그 날짜에 바로
     배치(넣기=놓기 동시) — 하단에 항상 떠 있는 전역 빠른 추가 바(quick-bar)
     하나로 처리(submitQuickAdd가 오늘 탭이면 오늘 날짜, 날짜 상세가 열려있으면
     그 날짜(detailDateStr)로 바로 배치, 둘 다 아니면 미배치로 보냄). **2026-07-14
     정리**: 예전엔 날짜 상세 화면 안에 이것과 별개인 자체 입력창(dd-event-input)이
     따로 있었는데, 지저분해서 없애고 이 전역 바 하나로 통일함 — 날짜 상세
     화면에 다시 별도 입력창을 만들지 말 것.
   - 날짜를 모르면 '미배치(인박스)'로 들어간다.
2) 미배치(인박스): 화면 어디서든 항상 떠 있는 버튼(미처리 개수 배지).
   - 누르면 미배치 리스트 → 항목을 골라 배치.
   - 항목 상태는 딱 둘: 미배치 / 배치됨. (아래 "월 태그"는 새로운 세 번째
     상태가 아니라 미배치 상태 안에서의 부가 정보일 뿐 — 미배치 배지 개수에
     계속 포함됨. 다시 이 규칙을 어기고 진짜 세 번째 상태를 만들려 하면 안 됨.)
   - 대략 배치(월 태그): 몇 날짜에 할지는 아직 모르지만 "대충 몇 월에 하면
     되겠다"는 감만 있을 때를 위한 기능. 연간 탭에서 미배치 항목을 꾹 눌러
     드래그하면(길게 누르는 순간 미배치 창이 사라지고 뒤의 연간 화면이 드러남)
     원하는 달 박스에 놓아서 그 달로 태그할 수 있음(inbox item에
     targetMonth:"YYYY-MM" 저장). 월 박스 자체에는 아무 표시도 안 함(지저분해
     지지 않도록). 태그된 항목은 평소 미배치 목록에는 안 보이고, 그 달의
     달력(전체보기) 화면을 보면서 미배치를 열었을 때만 위쪽에 따로 나타남 —
     거기서 다시 꾹 눌러 드래그해서 원하는 날짜 칸에 놓으면 그 날짜로 확정
     배치(targetMonth 없어지고 완전한 배치됨 상태).
   - **탭으로 배치(2026-07-16, 배치용 캘린더 팝업 폐기)**: 예전엔 항목을 짧게
     탭하면 작은 배치용 캘린더 팝업(#place-overlay)이 따로 떴었는데, 사용자
     요청으로 없애고 지금 보고 있던 화면을 그대로 재사용하는 방식으로 바꿈
     (startInboxPlacement). 항목을 탭한 순간 미배치 창이 닫히면서: **연간
     탭을 보고 있었으면** 그 연간 화면이 그대로 드러나고, 그 상태에서 월
     박스를 하나 탭하면 그 달로 태그(위 "대략 배치"와 결과 동일, 드래그 대신
     탭 두 번으로 함). **그 밖의 탭(달력 탭 포함)을 보고 있었으면** 달력 탭
     으로 전환되고, 거기서 날짜 칸을 탭하면 그 날짜로 확정 배치. 두 경우 다
     화면 위쪽에 얇은 안내 띠(#placement-hint-bar, "넣을 달/날짜를
     선택하세요" + 취소 버튼)만 뜸 — **처음엔 놓을 수 있는 칸마다 옅게
     테두리(.placement-active)도 같이 둘렀었는데, 사용자가 "테두리는 안
     보이게" 요청해서 뺌(2026-07-16)** — 다시 칸 테두리 강조를 넣지 말 것,
     안내 띠 문구만으로 충분하다고 판단함. 취소 버튼을 누르거나 미배치
     버튼(btn-inbox-open)을 다시 눌러 미배치 창을 열면 배치가 취소됨
     (cancelPlacementMode, openInbox 안에서 항상 먼저 호출). 미배치 목록이
     펼쳐진 채로 다른 탭으로 넘어가면(하단 탭 직접 누름 또는 위젯을 눌러
     들어옴 둘 다) switchTab()이 항상 미배치 목록도 같이 닫음(closeInbox,
     날짜 상세를 항상 같이 닫던 것과 동일한 원칙 — 2026-07-16 추가, 위젯을
     눌러 달력 탭으로 들어왔는데 미배치 목록이 펼쳐진 채로 같이 나타나던
     문제 수정). 안내 띠 자체(배치가 진행 중이라는 상태)는 안 건드림 —
     목록 오버레이만 닫히고 배치는 계속 진행 중일 수 있음.
     연간 탭에서는 배치 도중 연도를 넘기거나(yearViewYear) 달력 탭에서는
     보고 있는 달을 넘겨도(calYear/calMonth) 배치 모드가 유지되므로, 원하는
     연도/달로 옮겨가서 탭해도 됨. 위 드래그 방식(연간 탭 한정)은 그대로
     남겨둠 — 둘 다 같은 결과(targetMonth 태그 / 확정 배치)로 이어지는
     서로 다른 손짓일 뿐, 하나가 다른 하나를 대체하지 않음.
   - **배치 해제(2026-07-14 추가)**: 반대로 이미 날짜가 정해진(배치된) 한 번짜리
     할 일도, 그 줄의 손잡이(≡)를 꾹 눌러 드래그해서 하단 미배치 버튼
     (btn-inbox-open) 위에 놓으면 날짜 지정을 없애고 미배치로 돌려보낼 수
     있음(unplaceEventToInbox — 기존 이벤트를 지우고 텍스트만 그대로 새 미배치
     항목으로 만듦, id는 새로 부여됨 — placeInboxItem이 반대 방향으로 할 때도
     항상 새 id를 주는 것과 대칭). 오늘 탭(setupTimelineDrag, 타임라인 순서
     바꾸는 드래그에 얹음)과 날짜 상세(setupUnplaceDrag, 이쪽은 다른 곳에 놓을
     수 없고 미배치 버튼에 놓는 것 하나뿐이라 더 단순한 별도 함수) 둘 다에서
     됨. **반복 할일은 대상 아님**(애초에 특정 날짜에 배치된 게 아니라 매번
     규칙으로 계산되는 것이라 "날짜 지정 해제"라는 개념 자체가 성립 안 함) —
     오늘 탭에서는 반복 할일 손잡이를 미배치 버튼 위에 올려도 아무 반응 없고,
     날짜 상세에서는 반복 할일 줄에 애초에 이 손잡이 자체가 없음. 드래그 중
     미배치 버튼 위에 있으면 파란색으로 살짝 커지는 강조(drop-hover 클래스)가
     붙어서 여기 놓으면 된다는 걸 알려줌.
   - **내용 수정(2026-07-16 추가)**: 미배치 목록에서 항목을 꾹 눌렀다가(움직이지
     않고) 떼면 그 자리에서 바로 글자를 고칠 수 있는 입력칸으로 바뀜
     (startInlineTextEdit 재사용 — 반복 할일 텍스트 수정과 같은 함수). 같은
     "꾹 누르기" 제스처 안에서 셋으로 갈림: 짧게 탭(500ms 안에 뗌) → 배치
     모드 진입(위 "탭으로 배치"), 꾹 눌렀다가 움직이지 않고 뗌 → 내용 수정,
     꾹 누른 채로 움직임 → 월/날짜로 드래그. 서로 안 부딪히게
     setupInboxItemDrag 안에서 하나의 상태 기계로 처리(움직임이 있어야만
     드래그로 확정되고, 그 전까지는 미배치 창을 안 닫은 채로 기다림 — 예전엔
     꾹 누르는 순간 바로 창이 닫혔는데, 그러면 수정할 항목이 화면에서
     사라져버려서 움직임이 있을 때만 창을 닫도록 바꿈).
     **(2026-07-14 레이아웃 수정)** 처음 만들었을 땐 날짜 상세 화면(#day-detail-overlay)이
     화면 전체(inset:0)를 덮어서 하단 전역 바(#bottom-fixed, 빠른 추가 입력칸 +
     미배치 버튼)가 그 뒤에 완전히 가려져 안 보이고 안 눌렸음 — 위에서 설명한
     "날짜 상세를 보면서 그 자리에 바로 입력"도 "미배치 버튼으로 드래그"도 둘 다
     실제로는 바가 안 보여서 할 수 없는 상태였음. 그래서 #day-detail-overlay의
     바닥을 화면 맨 아래(0)가 아니라 156px 위(.tab-content 바닥 여백과 같은 값,
     즉 하단 전역 바가 차지하는 자리만큼)로 올려서, 그 오버레이(어둡게 깔리는
     배경 + 날짜 상세 패널)가 하단 바 자리는 아예 안 덮게 함 — 하단 바는
     계속 그 자리에 그대로 보이고 눌리고, 날짜 상세 패널은 그 위에 떠 있는
     모양이 됨. 다시 화면 전체를 덮는 방식(inset:0)으로 되돌리지 말 것 —
     그러면 이 두 기능이 다시 못 쓰게 됨.
     **(2026-07-16, 키보드 뜰 때 틈새 버그 수정)** 오늘이 아닌 날짜 상세를
     열어놓고 하단 빠른 추가 입력칸에 글자를 치면(키보드가 올라오면),
     입력칸과 날짜 상세 패널 사이에 틈이 생겨서 그 틈으로 뒤에 있는 달력
     탭이 보이고 눌리기까지 하는 문제가 있었음 — #day-detail-overlay의
     "bottom:156px"는 하단 바가 항상 정확히 156px를 차지한다는 가정으로
     고정해둔 값인데, 안드로이드 웹뷰가 키보드를 띄울 때 화면을 실제로
     줄여주지 않으면(=MainActivity에 별도 설정이 없어서 기본값으로
     동작하던 상태) 두 fixed 요소(오버레이·하단 바)가 서로 다른 기준으로
     밀려나면서 이 가정이 깨졌던 것으로 보임. MainActivity에
     `android:windowSoftInputMode="adjustResize"`를 추가해서, 키보드가 뜰 때
     웹뷰 자체의 실제 화면 크기가 줄어들게 함 — 그러면 두 fixed 요소가 항상
     같은(줄어든) 화면 기준으로 계산되어 서로 어긋나지 않음.
     **(2026-07-16, 같은 날 재발 — 근본적으로 다시 고침)** adjustResize만으로는
     안 잡히는 기기가 있었음(사용자가 스크린샷으로 재확인 — 날짜 상세 목록과
     키보드 사이에 여전히 달력 탭 한 줄이 그대로 보임). 원인으로 보이는 것:
     안드로이드가 웹뷰 자체 크기는 줄여줘도, 웹뷰 **안**의 CSS(100vh,
     position:fixed의 bottom 계산)가 그 축소를 실시간으로 못 따라가는
     기기·웹뷰 버전이 있음(잘 알려진 모바일 웹 공통 문제). 그래서 CSS/네이티브
     설정에만 의존하지 않고, JS로 키보드가 실제로 가리는 높이를 직접 재서
     보정하는 방식으로 바꿈: `window.visualViewport`의 resize/scroll
     이벤트마다 `innerHeight - visualViewport.height - visualViewport.offsetTop`
     (=키보드가 가린 픽셀 수)를 계산해서 CSS 변수 `--keyboard-inset`에
     저장(:root 기본값 0). `#bottom-fixed`의 `bottom`과
     `#day-detail-overlay`의 `bottom`(156px + 이 변수) 둘 다 이 변수를
     같이 써서, 키보드가 얼마나 떠 있든 항상 정확히 같은 기준으로 맞물리게
     함 — 하나는 CSS vh, 하나는 고정 dp처럼 서로 다른 기준을 쓰던 게 근본
     원인이었으므로, 이제 둘 다 "실측한 키보드 높이" 하나의 기준만 씀.
     이후에도 "키보드 뜰 때 하단 고정 요소가 어긋나는" 증상이 보이면, 먼저
     `--keyboard-inset`이 실제로 갱신되고 있는지(예: 개발자도구로
     `document.documentElement.style.getPropertyValue('--keyboard-inset')`
     확인) 볼 것 — visualViewport API 자체가 없는 아주 오래된 웹뷰라면
     `if (window.visualViewport)`에서 조용히 아무 것도 안 하고 넘어가므로
     그 경우엔 다른 방법을 추가로 찾아야 함.
     **(2026-07-16, 또 재발 — 진짜 원인 찾음)** 위 키보드 보정을 적용하고도
     사용자가 다시 스크린샷을 보내서 확인해보니 여전히 틈이 그대로였음 —
     즉 키보드는 애초에 원인이 아니었던 것(스크린샷이 우연히 계속 키보드가
     뜬 채로 찍혔을 뿐). **진짜 원인**: `#day-detail-overlay`의
     `bottom:156px`, `.tab-content`의 아래쪽 패딩 156px, `#day-detail-panel`의
     `max-height` 계산에 쓰인 `100vh - 156px` 전부 "하단 고정 바
     (#bottom-fixed)의 실제 높이가 156px일 것"이라는 **숫자 하나를 코드
     세 군데에 똑같이 못박아둔 것**이었는데, 실제로는 기기의 하단 안전영역
     (제스처 내비게이션 여백 등)에 따라 그 바의 진짜 높이가 156px보다 커질
     수 있어서, 그 차이만큼 항상(키보드 유무와 무관하게) 틈이 남아 있었음.
     그래서 156px라는 숫자를 세 곳 다 지우고, `#bottom-fixed`의 실제
     렌더링된 높이를 `ResizeObserver`로 직접 재서 CSS 변수
     `--bottom-fixed-height`에 담아두고(:root 기본값만 156px, JS가 못
     채웠을 최초 한 순간 대비) 세 곳 다 이 변수를 쓰게 바꿈. **다시 156px
     같은 숫자를 하드코딩하지 말 것** — 하단 바 높이가 바뀔 만한 수정(빠른
     입력칸 크기, 하단 탭 높이 등)을 하더라도 이 변수 덕분에 자동으로
     따라감. 이후에도 비슷한 "하단 바와 다른 요소 사이 틈" 증상이 보이면,
     `--bottom-fixed-height`가 실제 #bottom-fixed 높이와 같은지부터 확인할 것
     (개발자도구로 `getComputedStyle(document.documentElement).getPropertyValue('--bottom-fixed-height')`
     와 `document.getElementById('bottom-fixed').offsetHeight` 비교).
   - **닫기 방식(2026-07-14)**: 날짜 상세 패널 안의 "닫기" 버튼(btn-dd-close)은
     없앰 — 대신 ① 패널 바깥의 어둡게 깔린 배경(오버레이 자기 자신, e.target===this
     로 판정)을 탭하면 닫히고 ② 하단 탭(오늘/달력/연간/반복 할일/설정) 중
     아무거나 눌러 다른 페이지로 넘어가도 자동으로 같이 닫힘(switchTab() 맨
     앞에서 closeDayDetail()을 항상 먼저 호출). 날짜 상세 패널 안쪽을 탭하는
     건 안 닫힘(e.target이 패널의 자손이라 오버레이 자신이 아님). 닫기 버튼을
     다시 만들지 말 것.
3) 배치: 항목당 남은 질문은 하나만(넣기의 OX가 첫 갈림길이므로).
   - 반복 X → "언제?" : 근무가 보이는 달력에서 날짜 하나 지정.
   - 반복 O → "어떤 날마다?" : 버튼 2개만 제공("기간" / "근무·요일") — 탭이
     지저분해지지 않도록 예전에 따로 있던 "요일로 고르기"는 "근무·요일" 안으로,
     "음력으로 고르기"는 "기간" 안으로 흡수함.
     a. 근무·요일로 고르기: 근무×요일 표(세로: 근무 순서대로, 가로: 월~일)를
        보여주고 칸을 누르면 바로 켜지고(색칠) 다시 누르면 꺼짐 — 그게 다임.
        "조건 추가" 같은 별도 단계나 무슨 요일마다인지 알려주는 문구 없음
        (색칠된 칸 자체가 곧 반복 조건이라 따로 설명 안 함 — 확정된 사용자
        의견). 한 근무의 요일 7칸을 전부 켜면 "그 근무 날마다"(요일 무관),
        한 요일의 근무를 전부 켜면 "그 요일마다"(근무 무관)와 동일 — 전체
        칸을 다 켜는 것만으로 예전 "요일만"/"근무만" 조건이 자연스럽게 표현됨.
     b. 기간으로 고르기: 기본은 "3개월마다" 등 년/개월/일 간격(근무 주기와
        무관, 실제 달력 날짜로 계산. 전환 시작일 = 그 할 일의 원래 날짜, 새로
        만들 때는 오늘). 누르면 숫자 세 칸 모두 0으로 시작함(하위 탭 없이,
        구석에 작은 "음력" 토글 버튼 하나만 있음 — 누르면 화면이 음력 월/일
        입력으로 바뀌고, 원래 날짜의 음력이 자동으로 채워져 있어 그대로
        확인만 눌러도 "매년 음력 O월 O일마다"로 추가됨(부모님 생신 등 극소수
        용도). 한 번 더 누르면 다시 양력 기간 입력으로 돌아오고 숫자는 다시
        0으로 리셋됨).
4) 실행: 오늘 탭에서 오늘 근무 + 오늘 할 것들을 한눈에 보고 체크.

## 반복의 3층 구조 (내부 엔진 개념)
- 층1 달력 반복: 요일·날짜 기준 (매주 화, 매월 15일)
- 층2 주기 반복: 근무 주기 기준 (D번호 엔진). 이 앱만의 차별점.
  ※ 사용자에게 D번호를 직접 노출하지 않는다. "비번날마다" 같은
    사용자 언어로 번역해 보여준다. D는 내부 계산용(코드 변수명 dayIndex로는
    계속 씀 — 화면에 글자로 찍히지만 않으면 됨).
  **실제 누락 발견·수정(2026-07-15)**: 반복 할일 규칙 화면은 처음부터 이
    원칙을 지켰지만, 날짜 상세 화면의 "근무 변경"(하루 근무를 다른 근무로
    바꾸는 기능, dayOverrides)에서는 예외적으로 "D3 · 주간"처럼 화면에 그대로
    찍히고 있었음 — 사용자가 발견해서 요청. 세 군데를 고침: ① 날짜 상세
    상단 정보 줄(원래 "D3 · 주간")은 D번호를 빼고 근무 이름만("주간") 남김
    ② "근무 변경" 버튼을 누르면 나오는 두 선택지 중 "앞으로 D3마다"는 "앞으로
    이 자리마다"로 바꿈(패턴상 같은 위치가 돌아올 때마다 바뀐다는 뜻은 그대로
    전달하되 숫자는 뺌) ③ 그렇게 저장된 뒤 붙는 "D마다" 표시 배지도 "이
    자리마다"로 맞춤. 앞으로도 D번호(dayIndex)를 화면 글자로 직접 찍는 코드는
    추가하지 말 것 — 항상 이런 식으로 사용자 언어로 번역해서 보여줄 것.
- 층3 간격 반복: "3개월마다, 6주마다" 같은 실제 달력 날짜 간격 (머리, 세차 등).
  구현 완료 — 반복 방식 중 "기간으로 고르기"(rule.type:'interval')로 존재.
  근무 주기(cycleDays)와 무관하게 anchorDate + years/months/days로 계산.
  (예전에 따로 있던 "리마인더" 기능은 이걸로 대체돼서 제거함.)
- 음력 매년 반복: rule.type:'lunar', {lunarMonth, lunarDay}. 부모님 생신처럼
  아주 가끔 쓰는 용도라 가볍게 구현 — 최상단 반복 방식 버튼을 따로 두지 않고
  "기간으로 고르기" 화면 안의 하위 선택("기간"/"음력")으로 넣어둠(promoteMode는
  'interval' 그대로, promoteIntervalSubMode만 'lunar'로 바뀜 — 저장되는 rule.type은
  'lunar'). 매년 그 음력 월/일의 "평달" 발생에만 맞춤(윤달 여부는
  무시 — 그래야 매년 정확히 한 번씩만 걸림). 그 달이 그 해엔 29일까지밖에 없어서
  30일이 존재하지 않으면 그 해는 자동으로 건너뜀. 한국천문연구원(KASI) 기준
  음력 변환 데이터(1000~2050년)를 이용 — 날짜 상세 화면의 음력 표시 기능과
  같은 변환 엔진(solarToLunar/lunarToSolar) 공유.

## 보류/제외 (지금 안 만듦)
- 스킵(못 한 것 표시) 기능: 영구 제외. 다시 만들지 말 것.
- 회고·통계 대시보드·월간 리포트: 영구 제외(앱 정체성 밖).

## 근무유형별 알람 (구현 완료, 네이티브 앱 전용)
- 예전엔 "PWA는 앱/브라우저를 닫으면 알람이 안 울려서 자체 구현 금지"였는데,
  Capacitor로 네이티브 안드로이드 앱이 생기면서 그 제약이 없어짐 — 지금은
  진짜 자체 알람(로컬 알림, 앱이 꺼져 있어도 울림)으로 구현돼 있음.
  **웹(브라우저/GitHub Pages) 버전에서는 여전히 안 울림** — 설치된 안드로이드
  앱에서만 동작(isNativeApp() 체크로 웹에서는 안내 문구만 보여주고 스킵).
- 설정 탭 "근무유형별 알람"(sd-alarms, renderShiftAlarmSettings)에서 근무유형별로
  켜기/끄기 + 시각 설정. state.shiftAlarms = { [근무이름]: {enabled,
  time:"HH:MM"} }. 저장은 카테고리·별표처럼 바뀔 때마다 즉시(확인 버튼 없음).
  **시각 입력 방식(2026-07-14 변경)**: 처음엔 드래그로 숫자를 돌리는 다이얼
  (bindNumberSpinner, 기간 편집 화면의 스피너와 같은 방식)이었는데, 사용자가
  "다이얼 말고 타이핑으로" 요청해서 `<input type="number">` 두 칸(시/분,
  .shift-alarm-time-input)으로 바꿈 — change/blur 시 0~23·0~59 범위로
  clamp하고 "HH:MM" 문자열로 합쳐 저장. 다른 화면(기간으로 고르기 등)의
  드래그 다이얼(.promote-interval-field)은 그대로 두고 안 건드림 — 다시
  섞어서 알람 시각도 드래그 방식으로 되돌리지 말 것.
- scheduleShiftAlarms()가 앱을 켤 때(초기화 시점)와 알람 설정을 바꿀 때마다
  호출됨 — 오늘부터 14일치 근무를 계산해서(getEffectiveShiftName 재사용,
  날짜 예외·D번호 예외 다 반영됨), 알람 켜진 근무유형인 날마다 그 시각에
  Capacitor Local Notifications로 예약. id는 그 날짜(YYYYMMDD)를 그대로 씀 —
  하루에 알람 하나뿐이라 날짜 자체가 고유 id. 다시 예약할 때 state.
  _scheduledAlarmIds(직전에 우리가 예약한 id 목록, state에 저장해둠)를 먼저
  취소하고 새로 깜 — 플러그인이 돌려주는 예약 목록에 의존하지 않고 우리가
  직접 추적하는 방식이라 더 확실함.
  권한 요청(ensureAlarmPermission)은 예약 직전에 매번 확인 — 이미 허용돼
  있으면 다시 안 물어봄.
- **알림 채널("shift-alarm") 생성 위치(2026-07-14 최종 확정)**: index.html의
  JS가 아니라 MainActivity.ensureAlarmChannel()에서 안드로이드 API로 직접
  만듦(@capacitor/local-notifications의 createChannel()은 더 이상 안 씀) —
  이유는 아래 "항상 소리로 울리게" 항목 참고. 앱을 켤 때(onCreate)마다 기존
  채널을 지우고 새로 만듦(안드로이드는 채널을 한 번 만들면 소리/중요도 같은
  속성을 나중에 못 바꾸므로, 예전 버전이 만들어둔 잘못된 설정의 채널이 남아
  있을 수 있어 매번 지우고 새로 만드는 것 — 가벼운 작업이라 매번 해도 문제없음).
  JS 쪽에서 다시 LN.createChannel()을 부르지 말 것(같은 id라 무시되긴 하지만
  어느 쪽이 진짜 설정인지 헷갈리게 됨) — index.html 초기화 코드는 이제
  scheduleShiftAlarms()만 바로 부름(채널은 이미 웹뷰가 뜨기 전에 네이티브가
  만들어둔 상태).
- **"알람이 제대로 안 울림" 버그 수정(2026-07-14)**: 사용자 신고로 찾은 원인들,
  @capacitor/local-notifications 플러그인의 안드로이드 네이티브 코드
  (node_modules 안의 실제 구현)를 직접 열어서 확인함:
  1) 알림 채널 importance를 5로 줬었는데, 안드로이드 NotificationManager의
     정식 중요도 값은 0~4뿐(4=IMPORTANCE_HIGH가 최댓값, 5는 정식으로 없는 값).
  2) createChannel 호출에 vibrate를 안 줬었는데, 플러그인 기본값이 false라
     진동이 꺼진 채로 채널이 만들어지고 있었음.
  3) 각 알람의 schedule에 allowWhileIdle을 안 줬었는데, 플러그인 기본값이
     false라 기기가 Doze(절전) 상태일 때 그냥 미뤄지는 기본 예약 방식
     (AlarmManager.set/setExact)으로만 잡히고 있었음 — 특히 정확한 알람
     권한이 없는 안드로이드 12+ 기기에서는 이게 가장 약한 예약 방식이라 많이
     늦거나 사실상 안 울리는 것처럼 느껴질 수 있었음. schedule에
     allowWhileIdle:true를 추가해서 Doze 중에도 깨우는 방식
     (setExactAndAllowWhileIdle/setAndAllowWhileIdle)을 쓰게 함 — 지금도
     scheduleShiftAlarms()에 이 설정이 남아있음.
- **무음/진동 모드여도 항상 소리로 울리게(2026-07-14, 같은 날 후속 요청)**:
  위 수정(1·2번)만으로는 여전히 부족했음 — @capacitor/local-notifications의
  createChannel()이 채널 소리를 항상 AudioAttributes.USAGE_NOTIFICATION으로
  고정해서 만들도록 네이티브 코드에 하드코딩돼 있어서(JS에서 바꿀 방법이
  없음), 안드로이드가 무음/진동 모드일 때 이 알림을 그대로 존중해서 소리가
  안 나거나 진동만 됨. 진짜 알람시계 앱들이 쓰는 방식(AudioAttributes.
  USAGE_ALARM — 안드로이드가 무음/진동 모드와 별개로 취급하는 "알람" 전용
  소리 스트림)으로 바꾸려면 플러그인을 거치지 않고 채널을 직접 만들어야 해서,
  MainActivity.ensureAlarmChannel()로 채널 생성 자체를 옮김(위 항목 참고).
  소리는 RingtoneManager로 시스템의 "기본 알람음"을 가져와 씀(전화벨/알림음이
  아니라 알람음 — 사용자가 시스템 설정에서 알람음을 바꾸면 이 알람도 그
  소리를 따라감). **한계**: 기기 자체의 "알람" 볼륨이 0으로 돼 있으면(무음/
  진동 모드와는 별개의, 시스템 설정 안의 독립된 볼륨 슬라이더) 그때는 진짜
  알람시계 앱도 마찬가지로 소리가 안 남 — 이건 사용자가 직접 그 볼륨을
  올려야 하는, 앱이 어떻게 할 수 없는 부분.
  **남은 한계(1차 버전)**: SCHEDULE_EXACT_ALARM 권한(안드로이드 12+ 기기에서
  "정확한 알람" 특별 권한)까지 요청하는 건 아직 안 함 — allowWhileIdle만으로도
  대부분 상황에서 눈에 띄게 나아지지만, 정말 초 단위로 정확한 시각이 필요하면
  나중에 이 권한 요청 흐름을 추가로 검토할 것. 화면을 깨우는 풀스크린 알람
  스타일(진짜 알람시계처럼 잠금화면 위로 뜨는 것)도 아직 없음.

## 배포
- GitHub Pages. 저장소 routine-app.
- 배포 = git add → commit → push. ("배포해줘"로 통칭)
- 접속: https://gmdwn916-cmd.github.io/routine-app/
- 데이터는 기기 localStorage에만. 코드 배포는 사용자 데이터에 영향 없음.

## 파일 구조
- index.html 단일 파일(HTML/CSS/JS 일체)에 앱의 실제 로직이 전부 있음 — 아래
  android/www는 이 파일을 감싸는 껍데기일 뿐, 로직은 없음. 번들러(webpack 등)는
  안 씀 — 예전엔 "외부 라이브러리 없음"이었는데, 알람 기능 때문에 Capacitor
  다리 스크립트 2개(capacitor.js, local-notifications.js)만 예외로 추가됨(둘 다
  로직 없는 순수 연결 코드, node_modules에서 그대로 복사해온 것). index.html이
  `<script src="capacitor.js">`/`<script src="local-notifications.js">`로 이
  둘을 불러오므로, **이 두 파일은 index.html과 같은 위치(루트)에도, www/ 안에도
  똑같이 있어야 함** — 루트 것은 GitHub Pages 배포용, www/ 것은 안드로이드 앱용.
  새 Capacitor 플러그인을 추가하면 그 플러그인의 node_modules/@capacitor/
  <이름>/dist/plugin.js도 같은 방식으로 루트+www에 복사하고 index.html에 스크립트
  태그를 추가해야 함(빌드 도구가 없어서 수동으로 하는 것 — 공식 "번들러 없이
  쓰기" 방식). 웹(브라우저)에서는 이 스크립트들이 로드는 되지만 Capacitor.
  isNativePlatform()이 false라서 실제로는 아무 것도 안 함.
- Capacitor로 안드로이드 네이티브 앱 껍데기 추가함(위젯·자체 알람 등 PWA로는
  안 되는 기능을 넣기 위한 전환, 2026-07-13):
  - package.json, node_modules/ — Capacitor 패키지 설치용. node_modules는
    .gitignore에 포함(용량 커서 저장소에 안 올림).
  - capacitor.config.json — 앱 이름 "루틴", 패키지ID `com.hyeongju.routineapp`,
    webDir: `www`.
  - www/index.html — 루트의 index.html 복사본. **여기를 직접 고치면 안 됨** —
    항상 루트 index.html을 고친 뒤 `cp index.html www/index.html`로 복사하고
    **반드시 `npx cap sync android`까지 실행해야** android/app/src/main/assets/
    public/index.html(= APK에 실제로 들어가는 파일)에 반영됨.
    **실제로 겪은 실수(2026-07-14)**: `cp`만 하고 `npx cap sync`를 몇 차례
    빼먹은 채로 `./gradlew assembleDebug`만 반복 실행 — Gradle은 www/index.html이
    바뀌어도 assets/public/index.html을 알아서 다시 복사해주지 않아서, 계속 옛날
    코드가 든 APK가 만들어짐. 겉으로 보이는 증상이 "안드로이드 웹뷰 캐시가 새
    코드를 안 받아준다"처럼 보여서 캐시 문제로 오진단하고, 사용자에게 앱 캐시
    삭제→완전 삭제 후 재설치까지 여러 번 헛되이 시켰음(둘 다 소용없었음 — 당연히
    문제는 기기가 아니라 빌드 쪽에 있었으니까). **재발 방지**: 빌드 스크립트를
    `cp index.html www/index.html && npx cap sync android`로 항상 같이 실행하고,
    의심되면 `diff www/index.html android/app/src/main/assets/public/index.html`
    (또는 `unzip -p app-debug.apk assets/public/index.html | grep <최근 고친 코드>`로
    APK 안의 실제 내용)로 반영 여부를 먼저 확인한 뒤에 "캐시 문제"를 의심할 것.
  - android/ — Capacitor가 생성한 안드로이드 네이티브 프로젝트(Android Studio로 엶).
    android/gradle.properties에 `android.overridePathCheck=true` 추가돼 있음
    — 프로젝트 폴더 이름(루틴어플)에 한글이 섞여 있어서 나오는 경고를 끈 것,
    네이티브(C++/NDK) 코드가 없어서 실제로는 문제 없음. 지우면 빌드 실패함.
  - 빌드: `cd android && ./gradlew assembleDebug` (JAVA_HOME을 Android Studio
    내장 JDK로 지정해야 함, 보통 `C:\Program Files\Android\Android Studio\jbr`).
    결과물은 android/app/build/outputs/apk/debug/app-debug.apk.
- 데이터 백업(내보내기/가져오기) 기능은 이 네이티브 전환 때문에 생김: 웹앱(브라우저)과
  네이티브 앱(Capacitor WebView)은 같은 코드를 실행해도 localStorage가 서로 다른
  저장공간이라 자동으로 안 이어짐 — 설정 탭의 "데이터 백업" 화면에서 JSON 파일로
  내보내고 다른 실행 환경(웹/네이티브)에서 가져오기 하면 이어붙일 수 있음. 아래
  데이터 모델 항목 참고.
  - **내보내기는 웹과 네이티브가 서로 다른 방식을 씀**(btn-data-export 클릭 핸들러
    안에서 isNativeApp()으로 분기): 웹은 blob 링크 다운로드(기존 방식, 문제없음).
    네이티브 앱의 웹뷰는 blob 다운로드를 자체 처리하는 기능이 아예 없어서(일반
    브라우저와 다른 부분) 눌러도 조용히 아무 일도 안 일어남 — 그래서 네이티브
    에서는 WidgetBridgePlugin.saveBackupFile()로 안드로이드가 다운로드 폴더에
    직접 파일을 쓰게 함(MediaStore API, 안드로이드 10+; 9 이하는 예전 방식+
    WRITE_EXTERNAL_STORAGE 권한). 이 구분을 다시 하나로 합치려 하지 말 것 —
    네이티브 웹뷰에서 blob 다운로드가 안 되는 건 플랫폼 자체 한계임.
  - MainActivity에서 웹뷰 캐시를 아예 안 쓰게(WebSettings.setCacheMode(LOAD_NO_CACHE))
    설정해둠(방어적 조치, 유지는 하되 없앨 필요 없음). 다만 **"위젯 갱신 실패"가
    계속되던 진짜 원인은 웹뷰 캐시가 아니라 빌드 실수였음** — 위 www/index.html
    항목의 2026-07-14 메모 참고. 뭔가 "고쳤는데도 안 바뀐다"는 증상이 보이면
    캐시부터 의심하지 말고 먼저 APK 안의 실제 코드를 확인할 것.
- 홈 화면 "빠른 할일 추가" 위젯 (네이티브 전용, 2026-07-13): 위젯을 탭하면 작은
  입력창(QuickAddActivity, 다이얼로그 테마 — 화면 전체 안 가리고 살짝 뜸)이
  뜨고, 글자를 넣으면 앱을 열지 않고도 그 자리에서 저장됨.
  - 네이티브 코드(QuickAddWidgetProvider/QuickAddActivity)는 android/app/src/main/
    java/com/hyeongju/routineapp/ 안에 있음. 위젯은 이 웹뷰의 localStorage를
    직접 못 읽고 못 쓰므로(따로인 저장공간이고, 안드로이드 쪽에서 웹뷰 localStorage를
    직접 건드리는 건 공식 지원 방법이 아님), 입력한 글자는 일단 안드로이드
    SharedPreferences("widget_bridge", 키 "pending_inbox_items", JSON 문자열 배열
    — "임시 우편함")에 쌓임.
  - 이 우편함을 읽고 비우는 다리 역할은 직접 작성한 전용 Capacitor 플러그인
    WidgetBridgePlugin.java가 함(local-notifications처럼 npm으로 받아온 게 아니라
    이 프로젝트 전용으로 새로 쓴 것 — getPendingItems/clearPendingItems 두 메서드뿐).
    MainActivity.java의 onCreate에서 registerPlugin(WidgetBridgePlugin.class)로 등록.
  - JS쪽(index.html)의 getWidgetBridge()/syncWidgetInboxItems()가 앱을 켤 때(초기화
    시점)마다 이 플러그인으로 우편함 내용을 가져와 state.inbox에 옮기고 우편함을
    비움. 위젯은 "글자 목록"만 넘길 뿐 inbox 데이터 형식(id/createdAt 등)을 전혀
    모름 — 실제 inbox 항목으로 바꾸는 건 JS쪽에서만 함.
    앱을 새로 켤 때뿐 아니라, document의 visibilitychange 이벤트(백그라운드에서
    포그라운드로 돌아올 때도 웹뷰가 발생시킴)에도 다시 동기화하도록 돼 있어서,
    "앱을 아예 껐다 켜야만 반영되는" 문제 없음 — 새 네이티브 플러그인 없이 표준
    웹 API만으로 해결됨(App 플러그인 안 씀).
  - 위젯 크기는 처음엔 1x1(가장 작음)에 아이콘(+ 모양, ic_widget_add.xml)만 있고
    글씨가 없었는데, 위젯 선택 화면에서 뭔지 알아보기 힘들다는 이유로(아래
    "위젯 선택 화면" 항목 참고) 2026-07-14에 상단에 "할 일 추가" 글씨를 추가함.
    처음엔 글씨+아이콘을 다 넣으려고 2x1(minWidth 110dp)로 키웠었는데, 사용자가
    "1x1로 유지하고 그 작은 칸 안에 글씨·그림을 잘 정리해서 넣어달라"고 다시
    요청해서 1x1(40dp)로 되돌림 — 다시 위젯 크기를 키우는 방향으로 바꾸지 말 것
    (사용자가 명시적으로 1x1 유지를 원함). 그 안에서 글씨·아이콘 배치는 위쪽
    절반(weight=1) 안에 글씨를, 아래쪽 절반(weight=1)에 아이콘을 두는 정확히
    반반 구조로 확정함(처음엔 8sp+아이콘이 weight=1로 남는 공간 전부를
    차지하던 구조였는데, "+ 공간은 아래 절반만" 요청으로 지금 구조로 바뀜).
    글씨 위치는 한 번은 gravity="bottom"(그 절반의 아래쪽에 붙임, "글씨를
    조금 내려달라"는 요청 반영)이었다가, gravity="center"(그 절반의 정중앙)로
    바꿨는데도 "여전히 이상하다"는 피드백이 또 나옴 — 이번엔 직접 실제 크기
    비율로 목업을 그려서 검토해보니, **진짜 원인은 위치가 아니라 글씨 크기
    였음**: 위젯이 보장하는 최소 크기(40dp)에서 "할 일 추가"(12sp)가 그 폭을
    넘어서 옆이 잘리고 있었음. 그래서 textSize를 8sp로 줄여서 40dp 폭에서도
    확실히 다 들어가게 고침. 아이콘도 ImageButton(버튼 기본 스타일이 은근한
    여백을 넣어서 정확한 중앙 정렬을 방해할 수 있음)에서 순수 ImageView로
    바꾸고 FrameLayout(weight=1) + layout_gravity="center" + 고정 크기(18dp)로
    감싸서 그 절반의 정중앙에 흔들림 없이 오게 함. **이후 위젯 크기·비율을
    다시 만지게 되면, 실제 배포 전에 이번처럼 Node(sharp)로 실제 dp 비율
    그대로 목업 이미지를 그려서 먼저 검토하고 사용자에게 보여준 뒤 진행할 것**
    — 실기기 없이 눈대중으로 코드만 보고 판단하다가 이번처럼 두 번 헛빌드함.
    레이아웃도 아이콘 하나만 있던
    FrameLayout에서 세로 LinearLayout(글씨 TextView 위 + 아이콘 아래)으로 바뀜 —
    클릭 반응 영역도 아이콘(widget_add_button)에서 루트 전체
    (widget_quick_add_root)로 넓혀서 글씨를 눌러도 입력창이 뜸.
  - **세로 배치 재설계(2026-07-16, 여러 차례 끝에 단순한 2칸 구조로 정착)**:
    긴 시행착오 끝에 지금은 **글씨 칸(weight 15, textSize 8sp 고정) + 십자가
    칸(weight 45, 남는 공간 전부)** 두 칸짜리 단순한 구조로 정착함. 다시
    여백을 여러 칸으로 잘게 쪼개는(위여백/글자/간격/십자가/아래여백 5칸
    구조 등) 방향으로 돌아가지 말 것 — 아래 이유 때문에 몇 번이나 실패한
    뒤 "단순한 게 낫다"로 결론남.
    - **실패 1 — 절대 위치**: (top,text,gap,cross,bottom) 비율을 40dp 기준
      dp 값(marginTop 고정값)으로 환산해 FrameLayout에 배치했더니, 사용자가
      실제 홈 화면에 놓아본 스크린샷에서 내용물이 위쪽에 쏠리고 아래에 큰
      빈 공간이 남음 — **원인: 위젯 칸이 항상 정사각형(40×40dp)인 게
      아님**(기기·런처의 홈 화면 격자가 정사각형이 아닐 수 있음,
      minWidth/minHeight="40dp"는 최소 요구치일 뿐 실제 배정 크기를 보장
      안 함). dp 고정값은 실제 칸이 더 커도 그대로라 나머지가 안 채워짐.
    - **실패 2 — 잘게 쪼갠 비율(weight) 5칸**: 절대 위치 대신 위/글자/간격/
      십자가/아래여백 5칸에 비율(weight)을 줘서 칸 크기와 무관하게 만듦.
      "십자가" 비율이 아이콘 파일의 투명 여백을 뺀 실제 보이는 그림
      크기를 뜻하도록(아이콘(ic_widget_add.xml)이 자기 칸의 20/24만 채우고
      나머지는 투명이라 이 비율 보정까지 정확히 계산해서) 수치까지 다
      맞춰 넣었는데도, 실제 기기(정사각형 아닌 칸)에서 보니 여백을
      너무 잘게 쪼개서 십자가가 중앙으로 안 보이고 전체적으로 안 예뻐
      보인다는 피드백 — 계산은 맞았지만 결과물이 별로였음.
    - **지금(3번째, 단순화)**: 5칸을 다 없애고 글씨(15)+십자가(45) 2칸만
      남김. 십자가 칸의 ImageView는 `layout_width/height="match_parent"` +
      **`android:scaleType="centerInside"`**(필수 — 이게 없으면 칸이
      정사각형이 아닐 때 십자가 그림이 옆으로 늘어나거나 눌림)로 그 칸
      안에서 원래 비율(정사각형) 그대로 줄어들어 항상 정중앙에 옴 —
      비율 계산이나 미리 여백을 나눠둘 필요가 아예 없어짐(칸이 정사각형이든
      세로로 길든 알아서 중앙에 맞게 그려짐). 글씨는 textSize="8sp" 고정
      (2026-07-14에 이미 40dp 폭에서 안 잘리는 걸로 확인된 안전한 크기) —
      세로 비율(weight)을 바꿔도 이 글씨 크기는 안 바꿀 것(잘림은 가로
      문제라 세로 비율과 무관).
    **빈 칸 버그(같은 날 발견·수정, 지금은 빈 칸 자체가 없어져서 해당 없지만
    기록)**: 여백 칸을 plain `<View>`로 만들었더니 위젯이 "추가할 수
    없습니다" 오류로 아예 죽었음 — 안드로이드 위젯(RemoteViews)은 그릴 수
    있는 뷰 종류가 정해져 있고 `android.view.View` 자체는 그 목록에 없어서
    생긴 문제. 나중에 다시 빈 칸(스페이서)이 필요해지면 `<View>`가 아니라
    내용 없는 `<TextView>` 같은 지원되는 뷰를 쓸 것.
    **아이콘 파일도 같이 손봄**: ic_widget_add.xml이 원래 24×24 캔버스 중
    가운데 14×14에만 그려져 있었는데(위아래 각 5씩 투명 여백), 계산을
    복잡하게 만드는 원인이라 여백을 2×2로 줄여 20×20을 채우도록 다시
    그림(pathData 변경) — 지금은 scaleType="centerInside" 덕분에 이 비율을
    직접 계산할 일이 없어졌지만, 아이콘 자체가 여백 없이 큼직하게 보이는
    데는 여전히 도움이 됨.
  - QuickAddActivity는 android:taskAffinity=""로 MainActivity와 완전히 다른
    작업(task)으로 뜸(+ 위젯 쪽 Intent에 FLAG_ACTIVITY_NEW_TASK|MULTIPLE_TASK) —
    안 하면 앱이 이미 켜져 있을 때 위젯을 눌렀을 때 입력창과 함께 앱 화면까지
    같이 튀어나오는 문제가 있었음. windowSoftInputMode="stateAlwaysVisible" +
    코드에서 명시적 showSoftInput 호출로 입력창 뜨자마자 키보드도 자동으로 뜸.
  - **"미배치에 추가" 버튼 제거(2026-07-16)**: 입력창 아래 있던 확인 버튼을
    없앰 — 키보드의 완료(엔터) 키를 누르면 저장되는 동작
    (setOnEditorActionListener)이 이미 있었어서, 버튼은 같은 동작을 중복
    제공하던 것뿐이라 지워도 기능은 그대로임. 다시 버튼을 넣지 말 것 —
    입력 후 엔터만으로 끝나는 게 의도된 동작.

## 이번 달 달력 위젯 (네이티브 전용, 읽기 전용, 2026-07-14)
- 홈 화면에서 달을 날짜별 근무색으로 보여주는 위젯(4x4 크기, 리사이즈 가능).
  수정 기능 없음 — 탭하면 앱만 실행됨(단, 하단 좌우 ‹/› 는 위젯 안에서 달만
  넘김, 앱은 안 열림). 목적이 "그날 근무가 뭔지 한눈에 파악"이라 글씨가 큼
  (여백은 그대로 두고 글씨만 키움), 할 일은 안 보여줌.
  **글씨 크기·정렬(2026-07-16)**: 날짜 숫자(cell_date_N, 20sp→16sp)와 근무
  이름(cell_shift_N, 17sp→13.6sp)을 각각 20% 줄임(요일 헤더는 그대로 17sp —
  건드리지 않음). 그리고 각 날짜 칸(cell_container_N)의 세로 정렬을
  gravity="center"(칸 안에서 위아래로 정중앙)에서 "top|center_horizontal"
  (칸 위쪽에 붙임)로 바꿔서, 날짜 숫자가 칸 위쪽 여백에 가깝게 옴 — 오늘 칸만
  다르게 하지 않고 42칸 전부 똑같이 적용함(오늘 칸은 배경·테두리만 다를 뿐
  정렬 방식 자체는 원래도 다른 칸과 같은 코드를 씀).
- **근무 계산은 전부 JS(index.html)가 하고, 네이티브는 그 결과를 그대로
  그리기만 함** — 위젯 1(빠른 할일 추가)과 반대 방향의 데이터 흐름(이번엔 앱→위젯).
  이렇게 나눈 이유: 근무 계산 규칙(로컬 자정 기준, cycleDays/baseDate, 날짜예외>
  D번호예외>기본패턴 우선순위, 요일 시작 설정)이 네이티브에도 따로 있으면 나중에
  둘 중 하나만 고쳐서 어긋날 위험이 있음 — 절대 안드로이드 쪽에 근무 계산 로직을
  새로 만들지 말 것.
  - JS: buildOneMonthGrid(year, month, weekStart)가 renderGrid()와 완전히 같은
    규칙(getEffectiveShiftName/getShiftColor 그대로 재사용)으로 한 달치 42칸
    그리드를 만들고, buildMonthCalendarPayload()가 **[지난달, 이번달, 다음달]
    3개월치를 항상 이 순서·이 개수로** 계산해서 pushMonthCalendarToWidget()이
    WidgetBridge 플러그인의 setMonthCalendarData()로 한 번에 넘김. payload 형태:
    { headers(7개, 요일 시작 반영), satCol/sunCol(토/일이 몇 번째 칸인지 —
    이것도 네이티브가 안 헷갈리게 JS가 알려줌), months: [ {monthLabel, days(42개,
    그 달에 안 속하면 null, 속하면 {date, dayNum, shiftName, color})} × 3 ] }.
  - 네이티브(WidgetBridgePlugin.setMonthCalendarData)는 그 JSON을
    SharedPreferences("widget_bridge", 키 "month_calendar_data")에 그대로 저장하고,
    보여줄 달 위치(키 "month_calendar_display_index")를 항상 1(=배열의 "이번달")로
    되돌린 뒤 MonthCalendarWidgetProvider.refreshAll()로 위젯을 즉시 다시 그림 —
    위젯에서 지난달/다음달로 넘겨봤어도 앱을 열면 오늘 기준으로 리셋됨.
    "오늘" 강조와 토/일 헤더 색칠만 유일하게 네이티브에서 직접 처리(오늘 강조는
    날짜 문자열 비교, 토/일은 JS가 알려준 열 번호를 그대로 씀 — 둘 다 근무
    로직이 아니라 중복 구현 아님). 토=#007AFF(파랑), 일=#FF3B30(빨강), 앱의
    .grid-header-cell.sat/.sun과 같은 색. **오늘 칸 테두리(2026-07-15 추가)**:
    처음엔 오늘 날짜 숫자 글자색만 파랗게 바꿨었는데, 스케줄 위젯(N주)이 오늘
    칸 전체(sch_cell_N)에 사각 테두리(widget_today_cell_border.xml)를 두르는
    것과 달리 이 위젯만 글자색만 바뀌어서 사용자가 "달력위젯에서도 스케줄
    위젯처럼 오늘 날짜를 표시해"로 요청해서 통일함 — 날짜 숫자 글자색은
    그대로 두고, 그 날짜 칸의 컨테이너(cell_container_N)에도 스케줄 위젯과
    똑같은 drawable로 테두리를 추가로 두름.
  - **이전/다음 달 넘기기(2026-07-14, 스케줄 위젯과 같은 방식으로 재변경)**:
    처음엔 하단 좌우에 눈에 보이는 ‹(nav_prev)/›(nav_next) 텍스트뷰가 있었는데,
    스케줄 위젯(N주) 쪽에서 먼저 "화살표를 없애고 월/일요일 칸 전체를 탭
    영역으로" 바꾼 뒤 사용자가 "달력 위젯도 같은 방식으로"를 요청해서 동일하게
    바꿈 — nav_prev/nav_next 뷰 자체를 레이아웃에서 제거하고, 대신 월요일 쪽
    열(헤더 칸 + 6줄 그리드 전체, 즉 그 열의 header_N + cell_container_N×6개)
    전부에 이전 달 PendingIntent를, 일요일 쪽 열 전부에 다음 달 PendingIntent를
    걸어서 그 세로 줄 전체가 하나의 탭 영역처럼 동작하게 함 — 다시 작은 화살표
    버튼을 만들지 말 것. 어느 열이 월/일요일인지는 스케줄 위젯과 똑같이
    "일요일 바로 다음 칸이 항상 월요일"(mondayCol = (sunCol+1)%7, JS가 넘겨준
    sunCol만 보고 계산)로 구함. 그 열을 탭하면 항상 페이지 이동만 하고 앱을
    열지 않음(의도된 트레이드오프, 스케줄 위젯과 동일).
    앱을 열지 않고 위젯 안에서만 달을 넘김 — MonthCalendarWidgetProvider가 자기
    자신에게 보내는 커스텀 브로드캐스트(ACTION_PREV/ACTION_NEXT, PendingIntent.
    getBroadcast, onReceive에서 가로챔)로 표시 인덱스(0/1/2)만 바꾸고 다시 그림.
    딱 3개월치만 미리 계산해 넘겨받은 상태라 그 범위(지난달~다음달)를 못
    벗어남 — 더 이전/이후 달을 보려면 앱을 열어야 함(이 범위 밖은 근무 계산
    자체가 없어서 위젯 혼자서는 절대 못 만듦).
  - 위젯 레이아웃(widget_month_calendar.xml)은 헤더 1줄 + 6줄×7칸(칸 컨테이너
    id: cell_container_N, 안에 [날짜 숫자(cell_date_N) / 근무이름 작은 배지
    (cell_shift_N)] 세로 2줄, 앱의 달력 탭 칸(cell-date + cell-shift-badge)과
    같은 모양 — 칸 배경은 중립, 배지만 근무색으로 옅게 칠하고 글자도 근무색).
    id는 리소스 이름으로 동적 조회(Resources.getIdentifier), RemoteViewsService
    같은 복잡한 컬렉션 위젯 아님.
  - 위젯 배경/글자색은 res/values/colors.xml + res/values-night/colors.xml로
    시스템 라이트/다크 모드에 맞춰 자동 전환(widget_bg/widget_text_primary/
    widget_text_secondary). "오늘"/토/일 강조색만 항상 고정색.
  - **갱신 시점**: 앱 시작/포그라운드 복귀 시(위젯 1과 동일한 지점) +
    교대 주기 저장, 주 시작 요일 변경, 근무유형 색 변경, 날짜별/D번호 근무 수정,
    여러 날짜 한번에 근무 수정(bulk) — 이 값들이 바뀌는 모든 저장 시점에서
    pushAllWidgets() 호출(달력 위젯 2 + 스케줄 위젯 3을 한 번에 갱신 — 아래 참고).
    **한계(1차 버전)**: 이 시점들 외에는 안 밀어줌 — 예를 들어 달이 넘어가는
    순간(예: 이번 달 말일 근처) 앱을 며칠간 안 열면 위젯이 지난달 그리드를
    계속 보여줄 수 있음. 앱을 한 번이라도 열면 바로 갱신됨.

## N주 스케줄 위젯 (네이티브 전용, 읽기 전용, 2026-07-14, 2026-07-15 그리드로 재설계)
- **처음엔 세로 목록(날짜 나열)으로 만들었다가, 사용자가 다른 앱 위젯 캡처를
  보여주며 "이거랑 비슷하게" 요청해서 요일 그리드 형태로 다시 만듦** — 목록형
  구조를 다시 만들지 말 것. 지금 구조: 요일 헤더(월~일 또는 일~월, weekStart
  따름) + 2주(2줄×7칸) 그리드, 위젯 2(달력)와 같은 칸 구조를 재사용
  (cell = 날짜 숫자 + 근무 배지 + 할 일 최대 3개를 "• "로 시작하는 줄로).
  단, 배지 색·톤은 참고 앱을 그대로 베끼지 않고 **이 앱 고유의 옅은 톤(15%
  불투명도) 스타일 유지**(사용자가 "앱 UI 통일성 유지"를 명시적으로 요청함) —
  진하고 꽉 찬 배지로 바꾸려 하지 말 것.
- 근무 계산·할 일 판단은 위젯 2와 완전히 같은 원칙으로 전부 JS가 끝냄
  (getEffectiveShiftName/getShiftColor + getRepeatTodosForDate/getEventsForDate를
  그대로 재사용 — 새 규칙 없음). buildSchedulePayload()가 계산하고
  pushScheduleToWidget()이 같은 WidgetBridge 플러그인의 setScheduleData()로 넘김
  (새 플러그인 안 만듦). "오늘" 여부(isToday)도 JS가 미리 계산해서 넘김 —
  네이티브는 그 값으로 오늘 칸 전체(sch_cell_N 컨테이너, 날짜 숫자만이 아님)에
  사각 테두리(widget_today_cell_border.xml)를 입힘 — 날짜만 감싸는 원형이었다가
  "그 날짜 전체에 테두리"로 바뀐 것. 근무 배지(sch_shift_N)도 match_parent
  너비로 칸 좌우 꽉 채움(달력 위젯의 "이어진 띠"와 같은 원리).
- payload 형태: { headers(7개), satCol/sunCol(토/일 열 번호, 달력 위젯과 동일한
  이유로 JS가 알려줌), pages: [ {days(14개, 2주치)} × 3 ] } — 각 day는
  {date, dayNum, isToday, shiftName, color, todos(최대 3개, 이미 "• " 접두 붙은
  문자열 배열)}.
- **페이지 넘기기**: 위젯 크기로 1주/2주를 정하던 예전 방식(리사이즈 기반)은
  없앰 — 대신 자기 자신에게 보내는 커스텀 브로드캐스트로 onReceive에서 페이지
  인덱스만 바꾸는 방식으로 2주씩 묶은 페이지를 넘김. **[지난 2주, 이번 2주,
  다음 2주] 3페이지**(SCHEDULE_PAGE_OFFSET_DAYS = [-14, 0, 14], 달력 위젯의
  [지난달,이번달,다음달]과 똑같은 관례 — 배열 인덱스 1이 항상 "이번 2주")를
  미리 계산해 넘겨받음 — 처음엔 앞으로(다음 2주)만 있었는데, "오늘 이전
  날짜도 보고 싶다"는 요청으로 지난 2주 페이지를 추가함. 그 범위 밖은 앱을
  열어야 갱신됨(달력 위젯과 같은 한계). 참고로 보여준 다른 앱은 스와이프로
  넘기는 방식이었지만, RemoteViews에서 스와이프 페이지는 훨씬 복잡해서
  (AdapterViewFlipper+RemoteViewsService 필요) 안 씀. 앱이 새 자료를 보낼
  때마다 페이지 위치는 1(=이번 2주)로 리셋됨.
  - **탭 영역(2026-07-14 최초 확정, 2026-07-16 네 차례 조정 끝에 최종 확정)**:
    처음엔 하단에 눈에 보이는 ‹/› 버튼을 뒀는데(따로 줄을 차지 → 헤더 줄에
    접어넣음 → 그래도 너무 작아서 못 누름) 화살표를 없애고 세로줄 전체 탭
    방식으로 바꿨고, 그 뒤 "날짜 칸을 누르면 팝업" 기능(아래 항목)이 생기면서
    넘기기 영역을 몇 차례 더 좁혀오다("월요일/일요일 세로줄 전체" →
    "헤더+첫째 줄 칸 전체" → "헤더+첫째 줄 날짜 숫자만"), **마지막으로
    사용자가 요일 이름이 아니라 위치 기준으로 다시 정리해달라고 요청해서
    지금 모양으로 확정됨** — 이제 요일(월/일요일)과 무관하게 **왼쪽 끝 열(0)·
    오른쪽 끝 열(6)** 고정으로 판단(더 이상 sunCol을 안 읽음). 넘기기 영역은
    헤더 칸(sch_header_0/6)과, 왼쪽·오른쪽 끝 열의 **모든 줄**(위/아래 둘 다)의
    "날짜 숫자를 뺀 칸의 나머지 부분"(근무 배지·할일이 나오는 부분) —
    자세한 동작은 아래 팝업 기능 항목 참고. 다시 요일 이름(월/일요일) 기준으로
    되돌리지 말 것 — 위치(왼쪽 끝/오른쪽 끝) 기준이 최종 결정.
- 위젯 1·2·3이 늘어나면서, 여러 저장 시점마다 위젯마다 따로 push 호출을 추가하는
  게 번거로워져서 pushAllWidgets() 함수 하나로 묶음(pushMonthCalendarToWidget()
  + pushScheduleToWidget() + pushTodayWidgetToWidget() + pushInboxWidgetToWidget()
  순서로 호출) — 앞으로 위젯이 더 늘어도 이 함수 안에만 추가하면 됨, 각 저장
  시점 코드는 안 건드려도 됨.
- **날짜 칸을 누르면 작은 팝업으로 그 날 근무·할일 보기 + 바로 추가(2026-07-16,
  이 위젯에서 유일하게 "쓰기"가 생긴 부분, 같은 날 다섯 차례 조정 끝에 최종
  확정됨)**: 여러 번 범위를 좁혔다 넓혔다 하며 정리한 끝에, **지금 최종
  규칙은 위/아래 두 줄 모두 완전히 똑같은 원칙 하나로 통일됨** — 매 칸마다
  "날짜 숫자(sch_date_N)"와 "근무 배지(sch_shift_N)"는 항상 팝업(어느 열이든
  예외 없음, 사용자가 "숫자랑 밑에 근무까지는 팝업 영역으로" 요청해서 근무
  배지도 넘기기에서 팝업으로 넘어옴). 그 둘을 뺀 칸의 나머지(=할일 줄,
  sch_todo_N_0~2)는 **왼쪽 끝 열(0)에서는 왼쪽 넘기기, 오른쪽 끝 열(6)에서는
  오른쪽 넘기기, 가운데 열(1~5)에서는 그냥 팝업**. sch_date_N·sch_shift_N이
  sch_cell_N(칸 전체) 안에 중첩된 자식 뷰라서, 안드로이드 표준 터치 처리
  (자식이 있는 자리는 자식이, 없는 자리는 부모가 받음)로 그 둘 위를 직접
  누르면 자식(팝업)이 받고, 자식이 없는 나머지 자리(할일 줄 + 여백)는
  부모(sch_cell_N, 열 위치에 따라 팝업 또는 넘기기)가 받음 — 위젯 안 목록
  (RemoteViewsFactory)과 달리 이건 보통 레이아웃이라 fillInIntent 같은 별도
  처리 없이 이 분리가 그대로 됨(ScheduleWidgetProvider.updateOne()의 날짜
  렌더링 루프, dateId·shiftId는 항상 dayPending, `col == 0` → cellId에
  prevPending, `col == 6` → cellId에 nextPending, 그 외 → cellId에도
  dayPending). 새 팝업 대상 뷰(예: 할일 줄 일부)가 추가로 필요해지면 이
  패턴(그 자식 뷰에 dayPending을 직접 걸기)을 그대로 따를 것 — 다시 "칸
  전체가 통째로 넘기기" 방식으로 되돌리지 말 것. 팝업을 누르면
  QuickAddActivity와 같은 작은 다이얼로그 화면(DayQuickViewActivity)이 뜸 —
  그 날짜의 근무·할 일은 위젯이 이미 그리고 있던 정보(payload의 해당 day
  객체)를 그대로 인텐트 extras로 넘겨서 보여주기만 함(체크·수정 불가, 보기
  전용) — 따로 다시 계산 안 함(근무 계산은 항상 JS만 한다는 원칙 유지).
  처음엔 좌우 넘기기를 제스처(스와이프)로 바꾸는 방안도 검토했으나,
  RemoteViews 위젯은 스와이프를 감지할 방법이 없어(AdapterViewFlipper로
  전면 재작성해야 하는데 이미 예전에 복잡하다고 기각된 방식) 위 방식(날짜
  숫자·근무 배지=팝업, 할일 줄=열 위치에 따라 팝업 또는 넘기기)으로 최종
  결정함.
  - 팝업 화면(activity_day_quick_view.xml)은 QuickAddDialogTheme +
    quick_add_bg.xml(둥근 흰/검 카드)을 그대로 재사용 — 날짜 제목, 근무 배지
    (근무가 있을 때만 보임, 위젯 배지와 같은 15% 옅은 배경+진한 글자 색 공식),
    할 일 목록(줄바꿈으로 나열, 없으면 "이 날 할 일이 없어요"), 마지막에
    입력칸(EditText, 엔터=추가) 순서. 위젯과 달리 진짜 액티비티라 배지 모서리를
    RemoteViews 우회법 없이 GradientDrawable로 바로 둥글게 줄 수 있었음.
  - 입력칸에 쓰고 엔터를 치면 그 항목은 미배치가 아니라 **그 날짜에 바로 배치된
    한 번짜리 할 일**로 들어감 — 위젯 1(빠른 할일 추가)의 "임시 우편함" 패턴을
    그대로 따르되, 이번엔 글자만이 아니라 날짜도 같이 담음
    (SharedPreferences("widget_bridge") 안의 "pending_dated_items" 키에
    {text, date} 객체 배열로 쌓임 → WidgetBridgePlugin.getPendingDatedItems()/
    clearPendingDatedItems() → JS의 syncWidgetDatedItems()가 위젯 1과 같은
    시점(앱 시작 + 포그라운드 복귀)에 읽어서 placeInboxItem()과 같은 모양의
    진짜 이벤트(state.events, date/endDate 둘 다 그 날짜)로 만듦).
  - 각 날짜 칸의 PendingIntent는 **날짜별로 다른 action 문자열
    ("com.hyeongju.routineapp.OPEN_DAY_" + 날짜)을 반드시 같이 줌** — requestCode만
    으로는(칸 위치 i마다 200+i) 페이지를 넘겨서 같은 칸 위치가 다른 날짜를
    가리키게 됐을 때 예전 인텐트와 충돌할 수 있음(위젯이 전부 미배치로만
    열리던 예전 버그와 같은 원인 패턴) — 그래서 이 프로젝트에서 위젯 인텐트를
    새로 만들 때마다 항상 지키는 규칙(requestCode + action 둘 다로 구분)을
    여기서도 그대로 적용함.

## 오늘 전체 관리 위젯 (네이티브 전용, 2026-07-14, 유일하게 "쓰기" 있음)
- 오늘 날짜·요일·근무를 위젯 2·3처럼 크게 보여주고, 그 아래 오늘의 반복 할일 +
  한 번짜리 할 일을 스크롤 가능한 목록으로 보여줌. 목록의 각 줄을 탭하면 위젯
  안에서 바로 완료 체크가 토글됨 — 만들 당시엔 위젯 1~3이 전부 "읽기 전용"이라
  이 위젯만 유일하게 "쓰기"가 있어서 기술적으로 가장 복잡했음(그 뒤 2026-07-16에
  위젯 3에도 날짜 칸 팝업을 통한 "쓰기"가 추가됨 — 위 "N주 스케줄 위젯" 항목
  참고. 다만 그건 위젯 자체 안에서 바로 상태가 바뀌는 이 위젯의 체크 토글과
  달리, 작은 별도 화면을 띄워 입력받는 방식이라 여전히 기술적 복잡도는 이
  위젯이 제일 높음).
  - **맨 밑 두 줄 자리에 큼지막한 + 버튼(2026-07-16 추가, 같은 날 화면
    구성을 한 번 바꿈)**: 미배치 위젯에 먼저 만든 것과 똑같은 자리 배치
    (목록에서 맨 아래 56dp만큼을 빼서 큰 "+" 글자로 채움, today_add_button)
    이지만, **여기서 입력한 글자는 미배치가 아니라 오늘 날짜에 바로 배치된
    할 일로 들어감**. **처음엔** 스케줄 위젯의 날짜 칸 팝업과 같은
    DayQuickViewActivity를 오늘 날짜로 채워서 재사용했는데(날짜·근무·할일
    목록까지 같이 보여주는 화면), 사용자가 "다른거 보여주지 말고 할일추가
    위젯처럼만 띄워줘"로 요청해서 **입력칸 하나뿐인 새 화면
    TodayQuickAddActivity**(레이아웃도 QuickAddActivity와 완전히 같은
    activity_quick_add.xml 재사용, 다른 정보 표시 전혀 없음)로 바꿈 — 다시
    날짜·근무·할일 목록을 보여주는 방식으로 되돌리지 말 것. 저장 경로는
    스케줄 위젯과 똑같이 pending_dated_items → syncWidgetDatedItems()를
    그대로 씀(DayQuickViewActivity와 같은 SharedPreferences 키를 공유,
    TodayQuickAddActivity.addPendingDatedItem()이 DayQuickViewActivity.
    PREFS_NAME/KEY_PENDING_DATED_ITEMS 상수를 그대로 참조 — 새 임시 저장소
    안 만듦). 위젯이 아직 한 번도 데이터를 못 받았을 때(today_widget_data
    없음)를 대비해, 그 순간만 기기의 오늘 날짜를 `SimpleDateFormat("yyyy-MM-dd")`로
    직접 계산해 대신 씀(TodayWidgetProvider와 TodayQuickAddActivity 둘 다
    같은 fallback을 각자 가짐) — 이건 근무 계산이 아니라 단순 "오늘이
    며칠인지"라서 네이티브에 둬도 되는 예외(다른 위젯들의
    currentDisplayedMonth()와 같은 성격). 이 버튼의 PendingIntent는
    requestCode 2 + action에 "_FROM_TODAY_WIDGET" 접미사를 붙여서, 스케줄
    위젯이 같은 날짜로 만드는 DayQuickViewActivity 인텐트(접미사 없는
    "OPEN_DAY_"+날짜)와 절대 안 겹치게 함(다른 컴포넌트라 원래도 안 겹치지만
    이 프로젝트의 습관대로 이중 안전장치). 위젯 1과 같은 이유로
    NEW_TASK|MULTIPLE_TASK 플래그도 줌(TodayQuickAddActivity의
    taskAffinity=""와 짝 — 앱이 이미 켜져 있어도 입력창만 별도 작업으로
    뜨고 앱 화면까지 같이 안 끌려나오게 함).
  - **근무 표시칸 모서리 둥글게(2026-07-16)**: today_shift(근무 이름)의
    옅은 배경색은 RemoteViews의 setBackgroundColor로 입혔었는데, 이 메서드는
    부르는 순간 배경을 통째로 각진 ColorDrawable로 바꿔버려서 모서리를 둥글게
    할 방법이 없었음(XML에서 둥근 배경을 줘도 setBackgroundColor가 덮어써서
    무의미). 그래서 today_shift를 FrameLayout으로 감싸고, 그 안에 둥근 사각형
    밑그림 ImageView(today_shift_bg, widget_badge_base_light/dark.xml)를 글자
    뒤에 깔아둔 뒤 setColorFilter로 그 위에 근무색만 입히는 방식으로 바꿈 —
    ImageView.setColorFilter(int)는 RemoteViews가 지원하는 단순 int 메서드라
    minSdk 24부터 다 됨. 라이트/다크 모드마다 밑그림 색(흰색/#1C1C1E)이 달라야
    "옅게 15%" 느낌이 유지되므로, 위젯 배경 고를 때 쓰는 것과 같은 isDark
    판단으로 밑그림 리소스도 매번 같이 고름. 근무가 없을 때는 today_shift_bg를
    GONE으로 감춤(예전엔 투명색으로 지웠음). 다른 위젯들도 근무색을 옅게
    칠하는 배지가 필요해지면 이 패턴(둥근 밑그림 ImageView + setColorFilter)을
    그대로 쓸 것 — setBackgroundColor로 직접 칠하면 항상 각지게 나옴.
    **버그(2026-07-16, 같은 날 재수정)**: 처음엔 밑그림 ImageView를 담는
    그릇으로 FrameLayout(wrap_content)을 쓰고 ImageView는 그 안에서
    match_parent로 채웠는데, 실기기에서 근무 표시칸(글자 포함) 전체가 아예
    안 보이는 문제가 있었음 — wrap_content인 부모 안에 match_parent 자식만
    있으면(크기를 정해줄 다른 기준이 없어서) RemoteViews 렌더링에서 크기가
    0으로 무너지는 것으로 보임. FrameLayout을 RelativeLayout으로 바꾸고,
    ImageView가 "내 형제(today_shift)의 테두리에 맞춰라"는 명확한 기준
    (layout_alignLeft/Top/Right/Bottom="@id/today_shift")으로 크기를 잡게
    고침 — RelativeLayout은 이런 형제 참조를 신뢰성 있게 지원함(참조 대상이
    XML에서 나중에 나와도 됨). **다시 FrameLayout+match_parent 조합으로
    되돌리지 말 것** — 둥근 밑그림이 필요한 배지를 또 만들 일이 있으면
    이 RelativeLayout 방식을 그대로 따라 할 것.
  - **목록 안 줄마다 따로 탭 반응**: 위젯 2·3처럼 칸을 미리 정해진 개수만큼
    그려두는 방식으로는 "개수가 매번 다르고 스크롤도 되는 목록"을 못 만들어서,
    안드로이드의 "컬렉션 위젯" 방식(RemoteViewsService + RemoteViewsFactory,
    ListView)을 이 위젯에서 처음 씀. TodayWidgetService/내부의
    TodayRemoteViewsFactory가 목록 줄을 실제로 채우고, TodayWidgetProvider는
    헤더(날짜·근무)만 그림. 목록 전체에 "눌리면 이런 신호를 보내라"는 공통
    틀(PendingIntentTemplate, ACTION_TOGGLE)을 걸어두고, 각 줄마다
    TodayRemoteViewsFactory가 그 줄의 항목 id·종류·눌렀을 때 될 상태를
    fillInIntent로 붙여서, 탭 시 그 둘이 합쳐져 TodayWidgetProvider.
    handleToggle()이 어떤 항목을 어떻게 바꿀지 알게 됨. 이 템플릿용
    PendingIntent는 다른 위젯들과 달리 FLAG_MUTABLE로 만들어야 함(fillInIntent가
    안에 끼워져야 하므로) — FLAG_IMMUTABLE로 하면 정보가 안 합쳐짐.
    **탭 영역(2026-07-15 변경)**: 처음엔 줄 전체(row_root)에 fillInIntent를
    걸어서 텍스트를 눌러도 체크가 토글됐는데, 사용자가 "텍스트 말고 체크칸을
    눌러야 체크되게" 요청해서 체크 아이콘(item_check)에만 걸도록 좁힘 —
    TodayRemoteViewsFactory.getViewAt()의 setOnClickFillInIntent 대상만 바뀜
    (row_root → item_check), PendingIntentTemplate·나머지 흐름은 그대로.
    이제 텍스트(item_text)를 눌러도 아무 반응 없음.
    **(2026-07-16 후속)** 그렇게 비어버린 체크칸 바깥(row_root, 글자 포함)에
    사용자가 새 동작을 요청 — "체크칸을 제외한 모든 부분을 누르면 오늘
    탭이 열리도록". item_check는 그대로 자기만의 fillInIntent(체크 토글)를
    갖고, row_root에도 별도 fillInIntent를 하나 더 걸어서(같은 신호 틀 —
    PendingIntentTemplate는 컬렉션당 하나뿐이라 목적지가 다른 두 번째
    클릭을 못 만듦, 그래서 똑같이 ACTION_TOGGLE 방송을 타되 새 표시
    EXTRA_OPEN_APP을 얹어 보냄) TodayWidgetProvider.onReceive()가 이 표시를
    보면 handleToggle() 대신 openAppToToday()를 불러 앱을 오늘 탭으로 엶.
    **(2026-07-16 재수정)** row_root에만 걸어두면 실제로는 item_check가
    차지한 자리를 뺀 "여백(패딩)"만 반응하고, 그 안의 item_text(글자와
    그 옆 빈 공백을 포함하는 넓은 칸)는 안 눌린다는 걸 사용자가 실기기에서
    확인 — 컬렉션 위젯은 자식 뷰가 부모의 fillInIntent를 자동으로 물려받지
    않고, 각 뷰가 자기 몫의 fillInIntent를 직접 갖고 있어야만 반응함(위
    "안드로이드가 알아서 나눠 처리"라던 설명은 틀렸던 것 — 정정). 그래서
    item_text에도 row_root와 완전히 같은 openFillInIntent를 하나 더
    걸어서, 글자 부분과 글자 옆 빈 공백까지 전부 눌리게 고침. 이후 컬렉션
    위젯에 클릭 영역을 나눠 걸 때는 "부모에 걸면 자식도 자동으로 된다"고
    가정하지 말고, 반응하길 원하는 뷰마다 각각 걸어줄 것.
    **(2026-07-16 세 번째, 빈 줄 채우기)** 위 두 수정은 전부 "실제로 항목이
    있는 줄" 안에서의 클릭 영역 얘기였고, 별개로 "할 일이 몇 개 안 돼서
    목록이 위젯 칸을 다 못 채울 때, 그 아래 남는 빈자리를 눌러도 반응이
    없다"는 신고가 따로 있었음 — 이건 애초에 그 자리가 '줄(항목)' 자체가
    아니라 ListView의 빈 여백이라 fillInIntent를 걸 대상이 없어서 생기는
    문제라, 위 두 수정과는 다른 접근이 필요했음. TodayRemoteViewsFactory가
    실제 항목 뒤에 "보이지 않는 빈 줄"을 12개 더 만들어 붙이는 방식으로
    해결(getCount()가 실제 개수+12를 돌려줌, getViewAt()에서 실제 개수를
    넘는 자리는 글자 없고 체크칸도 안 보이는 빈 줄을 만들어 row_root·
    item_text에 똑같이 "오늘 탭 열기" fillInIntent를 걺) — 그러면 위젯 칸
    끝까지 항상 '눌리는 줄'로 채워짐(화면에 안 보이는 빈 줄은 안 그려져서
    비용 거의 없음). **주의**: 할 일이 정말 0개일 때는 이 빈 줄을 붙이면 안
    됨 — TodayWidgetProvider가 setEmptyView로 걸어둔 "오늘 할 일이
    없어요"(today_empty) 문구가 목록 개수가 정확히 0일 때만 뜨는 방식이라,
    getCount()에서 items.isEmpty()면 그대로 0을 돌려주고 있음(items가 1개
    이상일 때만 +12). 빈 줄 개수(FILLER_COUNT)를 바꿀 일이 있으면 이 0개
    예외 처리를 건드리지 말 것.
  - **위젯→앱 동기화(이 위젯에서 처음 생긴 흐름)**: 줄을 탭하면 ①
    handleToggle()이 저장해둔 오늘 데이터 안의 그 항목 done 값을 그 자리에서
    바로 바꾸고 notifyAppWidgetViewDataChanged로 위젯을 다시 그려서 즉시 체크된
    것처럼 보이게 함(낙관적 갱신) ② 동시에 "이 항목을 이 상태로 만들어라"
    (id/date/type/done)를 안드로이드 SharedPreferences의 임시 보관함
    (today_widget_pending_toggles)에 쌓음 — 위젯 1의 "임시 우편함"과 같은
    다리 역할. **토글이 아니라 최종 상태를 보내는 게 핵심** — 같은 요청이
    실수로 중복 반영돼도 결과가 달라지지 않아 안전함(반대로 "뒤집어라"였으면
    중복 반영 시 원래대로 되돌아가는 사고가 날 수 있음). JS쪽
    syncWidgetTodayToggles()가 앱 시작/포그라운드 복귀 시(위젯 1·2·3과 같은
    지점, syncWidgetInboxItems 바로 옆에서 같이 호출) 이 보관함을 읽어서
    실제 done_log(반복 할일)·ev.done(한 번짜리)에 반영하고 보관함을 비운 뒤,
    pushAllWidgets()로 최신 상태를 위젯들에 다시 보냄. 오늘 탭이 화면에 떠
    있는 상태로 복귀하면 renderToday()도 같이 불러서 화면도 바로 갱신됨.
  - buildTodayWidgetPayload()가 오늘의 반복 할일(getRepeatTodosForDate)과
    한 번짜리 할 일(getEventsForDate)을 합쳐서, 오늘 탭(renderTodayTimeline)과
    완전히 같은 순서(시간대 미정→오전→오후→밤, 그 안에서 timelineOrder)로
    정렬해 넘김 — 위젯 목록 순서가 앱 오늘 탭과 어긋나지 않게 함. 각 항목은
    {id, text, done, icon(카테고리 이모지), type:'repeat'|'once'}.
  - **(2026-07-14 변경)** 완료된 항목은 위젯 목록에서 아예 사라짐 — 처음엔
    취소선+흐린 색으로 표시만 하고 계속 보여줬는데("체크 자체가 목적이라
    바로 사라지면 확인이 안 됨"이라는 이유였음), 사용자가 "체크하면 안 보이게
    해달라"고 명시적으로 요청해서 스케줄 위젯과 같은 방식(완료된 항목은 안
    보이게)으로 바뀜 — 다시 "계속 보여주기"로 되돌리지 말 것. 구현: JS의
    buildTodayWidgetPayload()가 애초에 미완료 항목만 payload에 담고,
    TodayWidgetProvider.handleToggle()도 체크되는 순간 그 항목을 저장된
    JSON의 items 배열에서 통째로 제거함(done 값만 바꾸던 이전 방식에서 바뀜)
    — 그래야 위젯 안에서 눌렀을 때 새 데이터를 안 기다리고도 바로 그 줄이
    사라짐. TodayRemoteViewsFactory도 혹시 남아있는 옛 데이터를 대비해 done
    항목을 한 번 더 걸러내는 안전장치를 둠.
  - 위젯에서 새 항목 추가는 없음(그건 위젯 1의 역할) — 이 위젯은 "오늘 보고
    체크"만.
  - **크기(2026-07-14 자유 리사이즈로 변경)**: 처음엔 minWidth=250dp,
    minHeight=180dp(4x3 고정에 가까움)였는데, 사용자가 "2x2, 2x4, 4x2, 4x4처럼
    원하는 크기로 자유롭게 쓰고 싶다"고 요청해서 minWidth/minHeight를
    110dp(2칸 크기)까지 낮춤 — resizeMode="horizontal|vertical"은 이미 있었어서
    최소 크기만 낮추면 2x2까지 줄일 수 있음(위쪽 한계는 안 정해둬서 런처 grid가
    허용하는 한 계속 키울 수 있음). 기본 크기(처음 추가했을 때, targetCellWidth/
    Height)는 한 번은 4x4였다가, "위젯 넣는 화면에서 기본이 4x4로 돼있다"는
    피드백으로 같은 날 다시 2x2로 바꿈(targetCellWidth/Height=2) — 최소
    크기와 기본 크기를 똑같이 2x2로 맞춘 것. 다시 최소 크기를 250dp 근처로
    올리거나 기본 크기를 4x4 등으로 키우지 말 것(사용자가 명시적으로 2x2
    기본을 원함, 크게 쓰고 싶으면 직접 리사이즈하면 됨). 좁은 너비에서도 안
    깨지게 today_date에 maxLines="1"/ellipsize="end" 추가함(이전엔 없었음).

## 위젯 2·3·4 다크/라이트 모드 배경-글자색 어긋남 버그(2026-07-14 수정)
- 증상: 시스템이 라이트 모드인데 위젯 안 날짜 글자가 흰색으로 나와서 흰
  배경 위에 흰 글자가 겹쳐 안 보임(사용자 신고).
- 원인: 위젯 배경(레이아웃 XML의 `android:background="@drawable/widget_background"`,
  안에서 `@color/widget_bg` 참조)은 이 위젯을 실제로 그리는 홈 화면 런처가
  "그 순간 자신의" 다크/라이트 상태로 화면에 그릴 때마다 실시간으로 다시
  해석해서 그림. 반면 글자색(`views.setTextColor(...)`)은 우리 앱이 마지막으로
  push했던 그 순간의 다크/라이트 상태를 이미 확정된 색(RemoteViews에 심어지는
  순간의 리터럴 값)으로 심어서 보내는 것이라, 그 이후 push 없이 시스템
  다크/라이트만 바뀌면 배경은 즉시 새 상태로 바뀌는데 글자색은 예전 상태
  그대로 남아 서로 어긋남(다크 모드일 때 push해서 흰 글자로 심어둔 상태에서,
  앱을 다시 열지 않은 채 시스템만 라이트로 바뀌면 흰 배경 위에 흰 글자만
  남는 사고).
- 고침: 배경도 글자색과 완전히 같은 순간·같은 판단으로 우리가 직접 골라
  심음 — `widget_background_light.xml`/`widget_background_dark.xml`(색이 고정된
  채로 각각 따로 있는 모양, `@color` 참조 없음)을 새로 만들고,
  `context.getResources().getConfiguration().uiMode`로 그 순간의 다크/라이트를
  판단해 `views.setInt(rootId, "setBackgroundResource", ...)`로 배경도 매번
  push할 때 명시적으로 골라 심음(MonthCalendarWidgetProvider/
  ScheduleWidgetProvider/TodayWidgetProvider 셋 다 동일하게 적용). 이러면
  배경과 글자색이 항상 "같은 순간의 판단"으로 맞아 있음이 보장됨 — 다만
  시스템 테마가 바뀐 뒤 앱을 아직 안 열었으면 여전히 예전 테마 그대로
  보일 수 있음(둘 다 예전 상태로 일치하니 최소한 안 보이는 사고는 없음).
  더 완벽하게 실시간으로 맞추려면 시스템 테마 변경 브로드캐스트를 들어야
  하는데, 위젯 수신자(BroadcastReceiver)는 매니페스트만으로는 그 브로드캐스트를
  안정적으로 못 받아서(최신 안드로이드의 암시적 브로드캐스트 제한) 이번엔
  그 범위까지는 안 하고, "앱을 열 때마다 항상 다시 맞춰짐" 정도로 충분하다고
  판단함. **(2026-07-14 갱신)** 위젯 1(빠른 할일 추가)은 처음엔 날짜/글자색이
  없고 고정 파란색 아이콘뿐이라 이 버그 대상이 아니었는데, 같은 날 상단에
  "할 일 추가" 글씨(라벨)가 추가되면서 이 위젯도 똑같은 어긋남 버그 대상이 됨 —
  그래서 QuickAddWidgetProvider에도 나머지 셋과 동일한 방식(isDark 판단 +
  widget_background_light/dark 명시적 선택)을 똑같이 적용해둠.

## 위젯 선택 화면(홈 화면에서 위젯 추가할 때 뜨는 목록) 표시 (2026-07-14)
- 예전엔 4개 위젯 전부 안드로이드 기본값(앱 이름 "루틴"만 반복 표시, 미리보기
  없음)이라 어느 게 어느 위젯인지 위젯 선택 화면에서 구분이 안 됐음 — 사용자가
  "각 위젯의 미리보기 모습을 보여주고, 제목을 붙여줘"로 요청해서 고침.
  - **제목**: 각 위젯을 등록하는 AndroidManifest.xml의 `<receiver>` 태그에
    `android:label`을 달아줌(안드로이드가 위젯 선택 화면에 보여주는 이름은 앱
    자체 라벨이 아니라 이 리시버의 label을 씀) — QuickAddWidgetProvider="할 일
    추가", MonthCalendarWidgetProvider="달력", ScheduleWidgetProvider="스케줄",
    TodayWidgetProvider="오늘 할일", InboxWidgetProvider="미배치 목록"(2026-07-14
    추가, 처음엔 "미배치"였다가 같은 날 "미배치 목록"으로 다시 바뀜 — 위젯
    안 제목(inbox_title)도 같이 맞춰서 "미배치 목록"으로 바꿔뒀음, 픽커
    라벨과 위젯 안 제목이 다르면 헷갈리니 항상 같이 바꿀 것).
    **ScheduleWidgetProvider는 사용자가 말한 "일정" 대신 "스케줄"로
    붙임** — 이 프로젝트는 "일정"이라는 표현 자체를 안 쓰기로 정해뒀어서(용어
    통일 규칙 참고) 위젯 이름에도 그 규칙을 그대로 지킴. "일정"이 아니라 다른
    표현이 필요하면 그때 다시 정할 것.
  - **미리보기 — 2026-07-14 최초 시도 실패 후 재작업**: 처음엔 `android:previewLayout`
    만 추가해서 위젯이 실제로 쓰는 살아있는 레이아웃(@layout/widget_*)을 그대로
    재사용하려 했는데, 사용자 기기에서 위젯 선택 화면에 미리보기가 하나도 안
    뜨는 문제가 있었음. 원인 후보 둘: ① `previewLayout`은 안드로이드 12(API 31)
    이상 + 그 속성을 지원하는 런처에서만 동작 — 그 조건이 안 맞으면 아예 무시됨.
    ② 설사 지원돼도, 실제 위젯 레이아웃 안의 글자들(날짜·근무·할일 텍스트)은
    전부 빈 문자열 상태로 시작해서(Java 코드가 실행돼야 채워짐, 미리보기
    렌더링에서는 Java가 안 돌아감) 배경만 있는 빈 상자로 보였을 수 있음.
    **최종 해결책(둘 다 적용)**:
    1) `previewLayout`은 유지하되, 실제 위젯이 쓰는 레이아웃이 아니라 예시
       데이터(가짜 날짜·가짜 근무·가짜 할 일)를 하드코딩으로 미리 박아넣은
       **미리보기 전용 레이아웃**(widget_month_calendar_preview.xml/
       widget_schedule_preview.xml/widget_today_preview.xml/
       widget_inbox_preview.xml — 파일 이름에 "_preview" 붙음, Java 코드가
       절대 참조하지 않음, 순전히 이 미리보기 속성에서만 쓰임)을 새로 만들어
       연결함. 빠른 할일 추가 위젯은 원래부터 텍스트가 XML에 하드코딩돼
       있어서(글씨·아이콘 다 고정) 별도 preview 레이아웃 없이 실제 레이아웃을
       그대로 재사용해도 문제없음.
    2) 그래도 API 31 미만이거나 `previewLayout`을 안 받아주는 런처를 위해
       `android:previewImage`(모든 안드로이드 버전에서 동작하는 예전 방식)도
       같이 추가함 — 진짜 스크린샷 대신, Node.js의 `sharp` 라이브러리로 각
       위젯과 똑같은 모양의 SVG(예시 데이터 포함)를 그려서 PNG로 저장한 것
       (res/drawable-nodpi/widget_preview_*.png, 화면 밀도 무관하게 같은 이미지
       사용). 실기기/에뮬레이터가 없어서 진짜 스크린샷을 못 찍었기 때문에
       택한 방법 — 사용자가 "실시간 데이터 아니고 예시 사진이어도 된다"고
       명시적으로 허락함. 나중에 위젯 디자인이 크게 바뀌면 이 PNG들도 그때
       다시 만들어야 함(자동으로 안 바뀜 — previewLayout과 다른 점).
    3) **(2026-07-16, 세 개 다시 그림)** 할일추가·미배치·오늘할일 세 위젯의
       디자인이 그 뒤로 많이 바뀌었는데(할일추가는 글씨·십자가 배치를 여러 번
       재조정, 미배치·오늘할일은 맨 밑에 큰 + 버튼이 새로 생김) PNG는 그대로라
       실제 모습과 달라져 있었음 — "정리"를 요청받은 김에 다시 그림. 이번엔
       sharp 대신(설치가 다시 필요해서) 이미 갖고 있던 headless Chrome
       스크린샷 방식(위젯 디자인 검토용으로 이 세션에서 쓰던 방법)으로
       각 레이아웃과 똑같은 비율의 HTML을 만들어 찍음 — 이모지도 SVG로
       흉내내지 않고 Chrome이 그리는 진짜 이모지를 그대로 씀. 방법이 sharp든
       headless Chrome이든 상관없음 — 다음에 또 디자인이 바뀌면 그때 편한
       쪽으로 다시 그리면 됨, 중요한 건 실제 위젯 모습과 맞춰두는 것.

## 미배치 위젯 (네이티브 전용, 2026-07-14, 5번째 위젯 — 목록 자체는 읽기 전용, 2026-07-16부터 맨 밑 + 버튼만 예외)
- 미배치(인박스) 목록을 그대로 보여줌 — 위젯 2·3·4와 달리 근무·날짜 계산이
  아예 없어서 다섯 개 위젯 중 가장 단순함. buildInboxWidgetPayload()가
  state.inbox에서 targetMonth로 태그된 항목만 뺀 목록(count, items)을 만들어
  넘김 — 이 필터링은 앱의 기본 미배치 목록 화면(연간 탭에서 그 달을 안 보고
  있을 때의 renderInboxList, generalItems)과 똑같은 기준(!it.targetMonth)임.
  위젯엔 "지금 연간 탭에서 몇 월을 보고 있는지" 같은 맥락이 없어서 항상 이
  기본 화면과 같은 걸 보여줌 — targetMonth 태그된 항목까지 보여주려 하지 말 것
  (지저분해지지 않게 하려는 원래 설계 의도와 어긋남).
- 체크 같은 조작은 없음(목록 자체는 순수 조회용) — 항목을 탭하면 그냥 앱이
  열림, 어떤 항목을 눌렀는지는 구분 안 함(위젯 4처럼 개별 항목마다 다른 동작이
  필요 없어서). 그래도 목록(ListView)의 각 줄이 탭에 반응하려면 fillInIntent가
  있어야 하는 안드로이드 제약 때문에, 내용이 비어있는 fillInIntent를 각
  줄에 붙이고 PendingIntentTemplate 자체를 "앱 열기" 액티비티 인텐트로 씀
  (위젯 4의 체크 토글용 브로드캐스트 템플릿과 다른 부분 — 여기선 브로드캐스트가
  아니라 액티비티 실행이 템플릿). 이 템플릿용 PendingIntent만 FLAG_MUTABLE
  (다른 위젯들의 단순 클릭은 FLAG_IMMUTABLE 그대로) — fillInIntent 병합 자체가
  MUTABLE을 요구하는 안드로이드 제약이라, 병합할 내용이 비어있어도 예외 없음.
- **맨 밑 두 줄 자리에 큼지막한 + 버튼(2026-07-16 추가, 이 위젯의 유일한
  "쓰기")**: 목록(inbox_list)이 차지하던 자리 중 맨 아래 두 줄 높이만큼
  (56dp)을 목록에서 빼서 큰 "+" 글자(inbox_add_button, 30sp, 파란색 #007AFF —
  할일추가 위젯 아이콘과 같은 색으로 통일)로 채움. 목록·"+"버튼 둘 다 같은
  부모 LinearLayout의 형제라, 이 고정 높이 뷰를 추가하면 weight=1인 목록·빈
  문구 칸이 자동으로 그만큼만 줄어듦(따로 계산 안 해도 됨). 이 "+"를 누르면
  새 화면을 만들지 않고 **위젯 1(할일추가)의 QuickAddActivity를 그대로
  재사용** — 거기서 입력한 글자는 위젯 1과 완전히 같은 "임시 우편함"
  (widget_bridge의 pending_inbox_items)에 쌓였다가 앱을 열 때
  syncWidgetInboxItems()가 미배치로 옮김(별도의 새 임시 저장소나 동기화
  함수를 또 만들지 않음 — 이미 있는 위젯 1 경로를 그대로 씀). 이 PendingIntent는
  requestCode 2를 씀(이 Provider 안의 다른 두 개는 0·1) + action도 따로
  붙여서(다른 위젯들의 PendingIntent 충돌 버그를 겪고 배운 습관) 위젯 1의
  QuickAddActivity 인텐트(requestCode 0)와도 안 겹치게 함. 위젯 1과 똑같이
  FLAG_ACTIVITY_NEW_TASK|MULTIPLE_TASK를 줘서(QuickAddActivity의
  taskAffinity=""와 짝) 앱이 이미 켜져 있어도 입력창만 별도 작업으로 뜨고
  앱 화면까지 같이 안 끌려나오게 함.
- 위젯 4(TodayWidgetProvider/TodayWidgetService)와 완전히 같은 구조
  (RemoteViewsService + RemoteViewsFactory로 목록을 채움, Provider는 헤더만) —
  다만 체크·임시 보관함(pending toggles) 관련 코드가 전혀 없어서 두 파일
  (InboxWidgetProvider/InboxWidgetService) 다 훨씬 짧음.
- 배경도 다른 위젯들과 동일하게 isDark 판단 + widget_background_light/dark
  명시적 선택(런처의 실시간 배경 재해석과 우리가 push 시점에 확정하는 글자색이
  어긋나는 사고 방지, 위젯 2·3·4·1과 같은 이유).
- **크기(2026-07-14, 위젯 4와 함께 자유 리사이즈로 변경)**: minWidth/minHeight를
  110dp(2칸 크기)로 둬서 2x2까지 줄일 수 있고, 위쪽 한계는 안 정해서 런처
  grid가 허용하는 한 계속 키울 수 있음(resizeMode="horizontal|vertical").
  기본 크기(처음 추가할 때, targetCellWidth/Height)는 2x2 — 최소 크기와 같게
  맞춤(위젯 4와 같은 이유로 4x4였다가 2x2로 바뀜, 자세한 사정은 위젯 4 항목
  참고).

## 위젯 탭하면 그 화면으로 바로 이동 (2026-07-14)
- 예전엔 위젯(2·3·4·5)을 눌러도 그냥 앱만 열리고(마지막으로 보던 화면 그대로),
  그 위젯과 관련된 화면으로 알아서 넘어가주지 않았음 — 사용자가 "각 위젯을
  누르면 그 페이지로 바로 가도록" 요청해서 고침. 달력 위젯·스케줄 위젯은 그
  위젯이 보여주고 있던 달의 달력 탭으로, 오늘 위젯은 오늘 탭으로, 미배치
  목록 위젯은 미배치 오버레이가 열린 채로 들어감.
- **흐름**: 각 위젯의 "앱 열기" 인텐트에 목적지 정보(widget_nav="month"|"today"
  |"inbox", 달력/스케줄 위젯은 widget_nav_month="YYYY-MM"도 같이)를 실어
  보냄 → MainActivity.onCreate/onNewIntent(런치모드가 singleTask라 앱이 이미
  떠 있으면 onCreate가 아니라 onNewIntent로 옴 — 둘 다에서 처리해야 함)가 그
  값을 바로 JS에 넘기지 않고 SharedPreferences에 저장해둠(웹뷰가 아직 로딩
  중일 수도 있는 시점에 바로 JS를 실행하는 건 타이밍이 불안정해서 피함) →
  JS의 syncWidgetNavTarget()이 위젯 1의 "임시 우편함"과 똑같은 방식(앱 시작·
  포그라운드 복귀 시 WidgetBridgePlugin.getPendingNavTarget()으로 읽어서
  처리하고 clearPendingNavTarget()으로 비움)으로 그 화면으로 이동시킴
  (switchTab('month'/'today') 또는 openInbox()). 이 패턴(네이티브가 바로 JS를
  부르지 않고 SharedPreferences에 남겨두면 JS가 다음 동기화 시점에 가져가는
  방식)을 계속 유지할 것 — 인텐트 시점에 바로 JS 실행을 시도하지 말 것.
- 달력/스케줄 위젯이 "그 달"을 알아내는 방법: 이미 JS가 계산해서 저장해둔
  월별 데이터(달력 위젯의 KEY_MONTH_DATA/KEY_DISPLAY_INDEX, 스케줄 위젯의
  KEY_SCHEDULE_DATA/KEY_PAGE_INDEX)에서 지금 보여주고 있는 달/페이지의 날짜
  하나(date 필드)를 꺼내 "YYYY-MM"만 잘라 씀(currentDisplayedMonth() 함수,
  두 Provider에 각각 있음) — 근무 계산이 아니라 이미 계산된 문자열을 자르는
  것뿐이라 "네이티브에 근무 로직을 두지 않는다"는 원칙에 어긋나지 않음.
- **버그 수정(2026-07-15)**: 위 기능을 만든 뒤, 실제로는 어느 위젯을 눌러도
  전부 미배치 화면으로 들어가는 문제가 있었음. 원인은 안드로이드가
  "앱을 여는 이 버튼은 저 버튼과 같은 버튼인가"를 판단할 때 버튼 안에 담긴
  구체적인 목적지 값(위 widget_nav="month"|"today"|"inbox")은 안 보고, 어떤
  화면을 여는지(MainActivity)와 내부 식별 번호만 보는데, 네 위젯이 전부 이
  두 가지가 완전히 같았음(식별 번호를 전부 0으로 통일해서 씀) — 그래서 넷이
  전부 "같은 버튼"으로 취급되고, 그 버튼에 마지막으로 심어진 목적지 값
  (위젯을 새로고침하는 순서상 항상 미배치 위젯이 제일 마지막이라 그 값으로
  덮어써짐)만 실제로 눌렸을 때 쓰였음. 고침: 네 위젯의 "앱 열기" 버튼에
  서로 겹치지 않는 이름표(action)를 하나씩 달아서 안드로이드가 넷을 확실히
  다른 버튼으로 구분하게 함(MonthCalendarWidgetProvider/ScheduleWidgetProvider/
  TodayWidgetProvider/InboxWidgetProvider 각 updateOne()의 openIntent). 이후
  위젯을 새로 추가할 때도 "앱 열기" 버튼을 만들 때는 항상 이렇게 그 위젯만의
  고유한 이름표를 붙일 것 — 식별 번호(requestCode)만 다르게 주거나 아예 안
  건드리고 넘어가면 이 버그가 똑같이 재발함.

## 용어 (통일 — 혼동 금지)
- 할 일 = state.events[] 전체. 반복이 꺼져 있으면 한 번짜리(특정 날짜),
  반복이 켜져 있으면 반복 할일(근무/요일/직접 규칙으로 매번 계산해서 나타남).
  둘 다 같은 객체(할 일 + repeat 여부)일 뿐, 서로 다른 종류의 물건이 아니다.
- 반복 할일 = 할 일 중 repeat 규칙이 있는 것 (예전 "루틴". 하단 탭 이름도 "반복 할일").
  더 이상 날짜별로 복사돼 있지 않고, 하나의 객체 + 규칙으로 존재하며
  화면에 뿌릴 때만 그 날짜에 해당하는지 계산한다.
- 미배치 = 아직 안 놓은 것 (인박스)
- "일정"이라는 표현은 쓰지 않는다.
- "리마인더"라는 이름의 별도 기능(설정 탭 안, 간격/날짜형)은 폐기됨. 반복 할일의
  "기간으로 고르기"(층3)로 대체됐으니 다시 만들지 말 것.
- "트리거"(항목 묶어 보여주던 태그) 기능은 폐기됨. 다시 만들지 말 것.

## 작업 방식 (반드시 지킬 것)
1. 코드 수정 전 항상 계획 먼저 제시, 사용자 확인 후에만 수정.
   - 계획 설명은 프로그래밍 용어를 빼고 쉬운 말로 할 것.
   - 계획을 승인받으면 그 뒤로는 세부 진행마다 다시 묻지 말고
     배포까지 끝까지 처리할 것(권한 확인 재질문 금지).
2. 수정 파일은 명시된 것만.
3. 데이터 구조 변경 시 기존 localStorage 보존, 마이그레이션 필수.
4. 수정 후 셀프체크하고 한국어로 요약 보고.
5. 모든 응답 한국어.
6. 새 기능·화면은 '핵심 시스템' 흐름과 '경계 기준'에 맞는지 먼저 검토.
   어긋나면 만들기 전에 사용자에게 알릴 것.
7. 사용자에게 하는 모든 말은 전문용어 없이 요점만 간단히, 일반인에게 설명하듯
   쉽게 할 것(계획 설명뿐 아니라 진행 상황·완료 보고 등 모든 대화에 적용 —
   2026-07-15, "www/index.html 복사 → npx cap sync android →
   gradlew assembleDebug", "pushQuickAddWidgetRefresh" 같은 파일명·함수명·
   빌드 명령어를 그대로 나열한 것에 대해 사용자가 알아듣기 어렵다고 요청함).
   파일명·함수명·명령어 같은 코드 용어는 꼭 필요할 때가 아니면 언급하지 말고,
   "무엇을 왜 했는지"만 짧게 전달할 것.
8. 안드로이드 빌드는 코드 수정할 때마다 바로 하지 않고 모아둠(작업 목록에
   기록만 해두는 방식) — 사용자가 "빌드해줘"라고 명시적으로 말하기 전까지는
   빌드 여부를 먼저 묻지도 말 것(2026-07-15, 매번 물어보지 말라고 명시적으로
   요청함).

## 데이터 모델 (localStorage key: shiftRoutine)
- 설정 탭 "데이터 백업"(sd-data)에서 이 localStorage 전체를 JSON 파일로 내보내기/
  가져오기 가능(btn-data-export/btn-data-import). 가져오기는 확인창(confirm) 이후
  localStorage를 통째로 덮어쓰고 location.reload() — 마이그레이션은 그 뒤 초기화
  과정(migrateState)에서 평소처럼 자동으로 돎.
- cycleDays, baseDate("YYYY-MM-DD"), days[]{dayIndex, shiftType:{name}, items[]}
  ※ days[].items는 더 이상 쓰지 않음(항상 빈 배열). 과거 루틴 데이터 마이그레이션
    이후의 흔적일 뿐이니 이 필드 기준으로 새 기능을 만들지 말 것.
- events[]: 할 일 전체(한 번짜리 + 반복 할일 모두 여기 하나에 있음)
  - 공통: { id, text, category }
  - 한 번짜리(repeat 없음): { date, endDate, done }
  - 반복 할일(repeat 있음): { repeat: rule } — date/endDate/done은 안 씀,
    대신 done_log를 (date, id) 기준으로 찾아서 그날 완료 여부 판단.
  - rule 종류: {type:'shift', groups:[{shiftNames:[...], offset, weekdays?}, ...]}
               ("근무·요일로 고르기". groups는 (근무+요일) 조건 묶음 여러 개를
                OR로 합친 것 — 저장 형식 자체는 그대로지만 편집 화면은 표
                형태(promote-shift-matrix)로 완전히 바뀜: 근무×요일 칸을
                직접 켜고 끄고, "조건 추가" 같은 단계도 화면에 규칙을 설명하는
                문구도 없음. 저장할 때 collapseGridSelectionToGroups()가
                근무(행) 하나당 묶음 하나로 압축 — 그 근무의 요일 7칸이 전부
                켜져 있으면 weekdays 없이("이 근무 날마다"), 아니면 켜진
                요일만 담음. 편집하러 열 때는 expandGroupsToGridSelection()이
                반대로 묶음들을 표의 켜진 칸으로 풀어줌(shiftNames/weekdays가
                비어 있던 예전 묶음은 그 축 전체를 켜서 표현). 예전 통짜 형식
                {shiftNames, offset, weekdays}(묶음 1개짜리와 동일)도 계속
                읽힘 — 별도 마이그레이션 없이 computeIndicesForRule/
                describeRepeatRule이 groups 없으면 그 자리에서 묶음 1개로
                감싸서 처리.
                offset("이 근무 다음날"): 화면에서 고르는 UI가 없어서 새로
                만드는 묶음은 항상 offset:0("이 날"). 예전에 offset:1(다음날)로
                저장된 묶음은 계속 그 값 그대로 정상 계산·표시되지만(하위 호환),
                표에서 다시 편집해서 저장하면 offset:0으로 바뀜 — 의도된
                동작, 다시 만들지 말 것)
              {type:'weekday', weekdays:[...]}  (예전 "요일로 고르기" 전용 모드였음 —
               버튼은 없어졌지만 이 형식으로 저장된 예전 데이터를 계속 읽기
               위해 computeIndicesForRule/describeRepeatRule에 남겨둠. 그런
               항목을 열어서 수정하면 그 자리에서 shift 묶음(근무 없이 요일만)
               형식으로 자동 전환됨)
              {type:'interval', years, months, days, anchorDate}  (기간으로 고르기.
               근무 주기와 무관하게 실제 달력 날짜로 계산)
              {type:'lunar', lunarMonth, lunarDay}  (기간 안의 "음력" 하위 선택.
               매년 그 음력 월/일의 평달 발생에만 맞춤 — 윤달 무시)
  - shift/weekday는 computeIndicesForRule(rule)로 그 규칙에 맞는 D인덱스 목록을 계산.
    interval은 D인덱스 개념이 없고 isIntervalRuleMatch(rule, dateStr)로 anchorDate부터의
    간격을 직접 날짜 연산해서 판단(cycleDays/baseDate 미설정이어도 동작).
    lunar도 D인덱스 없이 isLunarRuleMatch(rule, dateStr)로 solarToLunar 변환 결과를
    직접 비교해서 판단.
    getRepeatTodosForDate(dateStr)가 이 셋을 분기해서 그 날짜의 반복 할일 목록을 조회.
  - 반복 규칙 고르는 화면(기간[구석의 "음력" 토글로 양력/음력 전환]/근무·요일)은
    openRepeatEditor() 하나로 공용
    (새로 만들기·기존 할일을 반복으로 전환·기존 반복 할일 편집 모두 이 함수).
    삭제/확인/취소 버튼이 없음(의도적으로 없앤 것 — 다시 만들지 말 것):
    - 저장: 화면에서 뭔가 바뀔 때마다(텍스트 입력, 카테고리 선택, 근무·요일
      칸 클릭, 기간/음력 스피너를 놓을 때, 모드 전환) syncPromoteEditorToState()가
      바로 저장. 새로 만들기 중엔 텍스트가 비어 있거나 규칙을 아직 하나도
      못 만들 상태(예: 모드 자체를 안 골랐거나 칸을 하나도 안 켬)면 저장을
      보류하고 조용히 기다림 — 텍스트+유효한 규칙이 갖춰지는 순간 그때
      state.events에 항목이 생김. 닫기는 배경(바깥) 탭으로만.
    - 삭제: 이 화면 안에 버튼 없음. "반복 할일" 탭 목록(renderRoutine)에서
      한 번짜리/오늘 탭 할 일과 똑같이 왼쪽으로 스와이프해서 지움
      (wrapWithSwipeToDelete 재사용). 여기서 스와이프는 그 반복 항목을
      통째로 지우는 것 — 오늘 탭/날짜 상세에서 스와이프하면 그 날짜만
      제외(excludedDates)하는 것과는 다르니 혼동 금지(아래 excludedDates
      설명 참고, 예전에 실제로 헷갈려서 버그 신고된 적 있음).
  - "반복 할일" 탭 목록(renderRoutine)은 카테고리가 아니라 반복 방식으로 섹션을
    나눠 보여줌: 근무·요일(shift/weekday) → 기간(interval) 순. 음력(lunar)은
    화면(반복 편집 화면)에서도 "기간" 안의 하위 선택이므로 이 탭 정리에서도
    따로 안 빼고 "기간" 섹션에 같이 묶임. 이 탭은 "반복이 어떻게 걸리는지"
    확인·수정하는 용도라 방식별 묶음이 맞음(카테고리는 각 줄 아이콘/색으로
    이미 표시되니 그걸로 또 묶지 않음). repeatGroupKey(rule)이 분류를 담당.
  - excludedDates?: [dateStr,...] — 오늘 탭/날짜 상세에서 반복 할일을 왼쪽으로
    쓸어 삭제하면 그 날짜만 여기 추가됨(반복 할일 자체는 안 지워짐).
    반복 할일 전체 삭제는 반드시 "반복 할일" 탭에서만(openRepeatEditor의
    삭제 버튼). 이 구분 헷갈리지 말 것 — 실제로 헷갈려서 버그 신고된 적 있음.
  - 화면에 그릴 때는 반드시 buildTodoRow()/renderTodoList() 공용 함수로 그릴 것.
    반복 할일과 한 번짜리 할 일은 겉모습이 완전히 같아야 함 — 별도 마크업/
    스타일을 새로 만들지 말 것(예전엔 today-event-item/dd-event-item으로
    따로 그렸다가 통합함). 오늘 탭과 날짜 상세도 겉모습·조작 방식이 완전히
    같음(2026-07-14 재정리로 더 확실해짐, 아래 항목 참고) — 화면마다 다르게
    만들지 말 것.
  - **할 일 줄 조작 방식(2026-07-14 재정리)**: 줄에 상시로 붙어있던 반복 전환
    버튼·중요(★) 버튼·드래그 손잡이(≡)를 전부 없애고, 텍스트를 누르면 뜨는
    작은 메뉴(반복/중요/수정 3개, 카테고리 고르기 팝업과 같은 cat-picker-menu
    스타일 재사용, 취소 버튼 없이 바깥 탭하면 닫힘 — openTodoItemMenu 함수,
    HTML의 #todo-item-menu)로 옮김. 텍스트를 눌러 바로 고치던 것도 이제
    메뉴의 "수정"을 거침(startInlineTextEdit — 예전 makeEventTextEditable을
    "클릭 시 실행되던 것"에서 "메뉴가 호출하는 함수"로 바꾼 것, 기간(멀티데이)
    할일은 수정을 누르면 그대로 openPeriodEdit). 다시 버튼들을 줄에 상시로
    붙이거나 텍스트 탭으로 바로 수정되게 되돌리지 말 것.
  - **드래그(2026-07-14 카드 전체로 확장)**: 손잡이 없이 카드(줄) 자체를 꾹
    눌러서 드래그함. wrapWithSwipeToDelete(rowEl, bgVar, onDelete, dragConfig)
    가 스와이프 삭제와 드래그를 하나의 상태 기계로 합쳐서 처리(둘 다 같은
    wrap 위에서 손가락을 보고 있어서 — 빠르게 움직이면 스와이프, 가만히
    500ms 누르고 있으면 드래그로 갈림). dragConfig(선택, 없으면 스와이프만
    — "반복 할일" 탭(renderRoutine)의 카드는 드래그 필요 없어서 안 줌):
    { eventId, refreshFn, enableInboxDrop, rootEl(선택) } — enableInboxDrop이면
    하단 미배치 버튼 위에 놓았을 때 unplaceEventToInbox 실행, rootEl이 있으면
    (오늘 탭만) 그 안의 .timeline-section-list로 옮겨서 시간대/순서 변경까지
    같이 됨(날짜 상세는 rootEl 자체가 없음 — 그 화면엔 시간대 구역이 없어서).
    예전에 따로 있던 setupTimelineDrag/setupUnplaceDrag 함수는 이 안으로
    흡수돼서 없어짐 — 다시 별도 함수로 쪼개지 말 것.
  - timeSlot?: 'morning'|'afternoon'|'night' — 오늘 탭 타임라인 구역. 없으면
    "미정"으로 취급(값이 'unset'으로 저장되지는 않음, 그냥 필드 자체가 없는
    상태 = 미정). 카테고리·important처럼 그 할 일 객체 자체에 저장되는 정보라
    반복 할일은 매번 같은 구역에 나타남.
  - timelineOrder?: number — 같은 timeSlot(미정 포함) 안에서의 순서. 없으면 0
    취급. 오늘 탭에서 손잡이(.item-drag-handle, "≡")를 꾹 눌러 드래그하면
    setupTimelineDrag()가 놓인 구역의 timeSlot과, 그 구역 안 최종 DOM 순서
    기준으로 형제 항목들의 timelineOrder를 전부 10 간격으로 재계산해서 저장.
    이 드래그는 스와이프 삭제와 같은 줄(li)을 쓰지만 손잡이에서 시작한 터치만
    반응하도록 e.stopPropagation()으로 완전히 분리해뒀음 — 손잡이를 없애거나
    다른 요소와 합치면 두 제스처가 서로 간섭하니 주의.
- inbox[]: 미배치 { id, text, createdAt, targetMonth? }
  - targetMonth?: "YYYY-MM" — 연간 탭에서 월 박스로 드래그해서 "대략 이 달에"로
    태그해 둔 것. 있어도 여전히 미배치 상태(세 번째 상태 아님, 위 핵심 시스템
    참고). setupInboxItemDrag()가 롱프레스 드래그를, renderInboxList()가
    현재 보고 있는 달(calYear/calMonth, tab-month 활성 시)에 맞는 targetMonth
    항목만 상단에 별도 표시하는 로직을 담당.
  (반복 배치 옵션은 아직 없음, 범위 밖으로 보류 중)
- shiftOverrides: 날짜 예외{date,shiftName} / D번호 예외{dayIndex,shiftName}
- shiftColors { 근무이름: 색 }, weekStart
- done_log[]: { date, id } — 반복 할일의 그 날짜 완료 기록.
  (예전엔 text로 저장해서 이름 바꾸면 기록이 끊겼는데, id로 바꿔서 해결됨)
  스킵 기능은 제거됨(state.skips는 더 이상 안 씀).
- teams[] { name, offsetDays }
- routinesMigratedV1: true — 예전 루틴(day.items)을 반복 할일로 1회 변환했다는 표시.
  이 값이 있으면 마이그레이션을 다시 돌리지 않는다(중복 생성 방지).

## 핵심 로직 (건드릴 때 주의)
- 날짜는 반드시 로컬 자정 기준. parseLocalDate() 사용.
  new Date("YYYY-MM-DD") 직접 파싱 금지(UTC 밀림).
  diff = Math.round((todayMid - baseMid)/86400000)
  dayIndex = ((diff % cycleDays) + cycleDays) % cycleDays + 1
- 근무 표시 우선순위: 날짜 예외 > D번호 예외 > 기본 패턴.
  설정탭 기본 근무 패턴은 어떤 수정으로도 바뀌지 않는다.
- 테마: data-theme 명시(light/dark) > @media 시스템 감지.
  @media 규칙은 :root:not([data-theme])로 한정.
- 카테고리는 설정 탭에서 사용자가 직접 추가·수정(이름/아이콘)·삭제 가능
  (state.categories — CATEGORIES_DEFAULT는 최초 1회 씨앗 데이터일 뿐, 이후엔
  안 씀). 이름·아이콘은 입력칸 하나에 같이 입력(예: "🥋 운동") —
  splitCategoryInput()이 맨 앞 이모지 1개를 아이콘으로, 나머지를 이름으로
  자동 분리. 따로 나뉜 입력칸을 두지 않음.
  - CATEGORIES_DEFAULT 8개 색은 근무유형 기본색(SHIFT_COLORS: 파랑·보라·초록·
    주황)과 안 겹치게 고른 팔레트(브라운·시안·틸·노랑·민트·보라(마젠타 계열)·
    핑크·회색). 새 카테고리 색을 추가/변경할 때도 이 4개 근무색과 안 겹치게
    고를 것 — 달력에 근무 배지와 카테고리 점이 같이 보여서 겹치면 헷갈림.
    "기타"의 회색만 휴무 색과 겹치는데 이건 의도된 것(둘 다 "특별히 없음"
    중립색이라 안 헷갈림). 색상 선택 프리셋도 근무유형용(SHIFT_COLOR_PRESETS)과
    카테고리용(CATEGORY_COLOR_PRESETS)을 따로 둠 — 카테고리 프리셋은
    CATEGORIES_DEFAULT와 정확히 같은 8색이라 설정 화면에서 항상 하나가
    "선택됨"으로 표시됨.
  - "기타"(id:'etc')는 항상 맨 끝에 회색(#8e8e93)으로 고정 — 수정·삭제 불가
    (설정 화면에 이름만 "고정" 표시로 보여주고 색상/삭제 UI 자체가 없음).
    카테고리가 없거나 잘못된 항목은 늘 이 "기타"로 폴백됨(getCat).
    migrateState()가 매번 "기타"가 마지막에 정확히 이 모양인지 검사해서
    아니면 되돌림(순서가 밀렸거나 예전 버전에서 색이 바뀌었어도 자동 복구) —
    그래서 일반 카테고리는 몇 개를 지우든 항상 최소 1개("기타")는 남음.
  - getCategories()/getCat(id)로 조회 — getCat은 없는 id면 CAT_FALLBACK으로
    폴백(카테고리를 삭제해도 그 카테고리를 쓰던 할 일이 깨지지 않게).
  근무 색·디자인 토큰은 여전히 코드 상단 상수로 관리(사용자 수정 불가).
- 아이콘 체계: 앱 고정 아이콘(하단 탭바, 설정 메뉴 6개, "다른 팀 근무 확인"/
  "미배치"/"달력보기" 버튼)은 전부 선 아이콘 SVG로 통일(viewBox 0 0 24 24,
  stroke:currentColor, fill:none, stroke-width:1.8, round cap/join — 하단
  탭바 스타일 그대로). 설정 메뉴용은 SETTINGS_ICONS 상수에 모아둠. 반대로
  **카테고리 아이콘은 이모지 그대로 유지** — 사용자가 직접 아무 이모지나
  골라 쓰는 기능이라(예: "🥋 운동") 고정 아이콘 세트로 바꿀 수 없음. 새 고정
  버튼을 추가할 때 이모지를 쓰지 말고 이 SVG 스타일을 따를 것.
- 달력 칸 렌더링은 화면 간 공유(달력 탭/팀 근무/복수선택 통일 유지).
- **날짜 칸 줄 높이 통일 + 할 일 3줄(2026-07-16)**: 근무 이름을 보여주는 줄이
  두 가지 모양(하루짜리는 .cell-shift-badge, 여러 날 이어지는 근무는
  .cell-shift-band)으로 나뉘어 있는데, 예전엔 badge 쪽에 높이가 고정돼
  있지 않아서 band(16px 고정)와 실제 줄 높이가 서로 달라 날짜 칸마다
  들쭉날쭉해 보였음 — badge도 16px로 맞춤. 오늘 칸 강조도 `border`
  대신 `outline`(+ 음수 offset)으로 바꿈 — border는 칸 높이를 그만큼
  더 키워서 오늘이 든 주(week)만 다른 주보다 미묘하게 커 보이는 원인이었고,
  outline은 레이아웃 크기에 영향을 안 줘서 이 문제가 없음. 하루짜리 할
  일은 각 칸에 2줄까지만 보이던 것을 3줄까지 보이게 늘림
  (appendEventIndicators, .slice(0,3)) — 늘어난 만큼 칸 안 다른 여백
  (cell-date 아래 여백, cell-event-list 위 여백·줄 간격, 칸 자체 위아래
  패딩)을 조금씩 좁혀서 전체적으로 너무 안 커지게 함.

## 화면 구성 (하단 탭 5개)
- 연간 / 달력(구 전체보기) / 반복 할일(구 루틴) / 오늘 / 설정 (+ 미배치 버튼은 전 화면 공통)
- 연간 탭: 1~3/4~6/7~9/10~12월을 4줄×3칸으로, 각 달 상자를 누르면 그 달의
  달력 탭으로 이동. 달 상자엔 그 달에 "★ 중요 표시"된 할 일(반복이든
  한 번짜리든 상관없음, getImportantEventsForMonth)을 그 달에서 처음
  나타나는 날짜 순으로 최대 3개까지 보여줌. 생일처럼 매년 반복되는 것도
  중요 표시만 해두면 매년 뜸 — 반복이라고 걸러내지 않고 사용자가 별표로
  알아서 정하게 함(예전엔 반복 할일을 통째로 제외했었는데 이 원칙에 안 맞아
  없앰).
- ★ 중요 표시는 buildTodoRow()에서 반복 할일·한 번짜리 할 일 모두에 똑같이
  보임(item.important). 반복으로 전환할 때도 그대로 이어받는다.
- 날짜 상세는 "반복 할일 + 한 번짜리 할 일"을 한 목록으로 합쳐서 보여줌
  (따로 섹션 나누지 않음, renderTodoList).
- 오늘 탭은 renderTodoList 대신 renderTodayTimeline으로 그림 — 미정(맨 위)·
  오전·오후(~18시)·밤 4구역으로 나눠서 보여줌(아래 timeSlot 설명 참고).
  이 구분은 오늘 탭에만 적용, 날짜 상세는 안 바뀜.

## 안 만들 것 (포지션 유지)
- 범용 습관 트래커·만능 투두·일반 근무자용 확장 금지.
  '교대근무자'라는 틈에 집중. 넓히려는 유혹을 참는다.
- 웹(PWA) 버전은 자체 알람 구현 안 함(브라우저 한계로 불가능). 네이티브 앱
  버전은 위 "근무유형별 알람" 섹션 참고 — 이미 구현돼 있음.
