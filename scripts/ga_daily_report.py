import os
import datetime
import requests
from google.oauth2.credentials import Credentials
from google.analytics.data_v1beta import BetaAnalyticsDataClient
from google.analytics.data_v1beta.types import (
    RunReportRequest,
    DateRange,
    Dimension,
    Metric,
    FilterExpression,
    Filter,
)

PROPERTY_ID = "524989384"
DISCORD_WEBHOOK_URL = os.environ["DISCORD_WEBHOOK_URL"]


def get_client():
    credentials = Credentials(
        token=None,
        refresh_token=os.environ["GOOGLE_REFRESH_TOKEN"],
        client_id=os.environ["GOOGLE_CLIENT_ID"],
        client_secret=os.environ["GOOGLE_CLIENT_SECRET"],
        token_uri="https://oauth2.googleapis.com/token",
    )
    return BetaAnalyticsDataClient(credentials=credentials)


def run_report(client, date, dimensions, metrics, dimension_filter=None):
    request = RunReportRequest(
        property=f"properties/{PROPERTY_ID}",
        date_ranges=[DateRange(start_date=date, end_date=date)],
        dimensions=[Dimension(name=d) for d in dimensions],
        metrics=[Metric(name=m) for m in metrics],
    )
    if dimension_filter:
        request.dimension_filter = dimension_filter
    return client.run_report(request)


def event_filter(event_name):
    return FilterExpression(
        filter=Filter(
            field_name="eventName",
            string_filter=Filter.StringFilter(
                match_type=Filter.StringFilter.MatchType.EXACT,
                value=event_name,
            ),
        )
    )


def main():
    yesterday = (datetime.date.today() - datetime.timedelta(days=1)).isoformat()
    client = get_client()

    # 전체 이벤트
    all_resp = run_report(client, yesterday, ["eventName"], ["eventCount", "totalUsers"])
    ev = {}
    us = {}
    for row in all_resp.rows:
        name = row.dimension_values[0].value
        ev[name] = int(row.metric_values[0].value)
        us[name] = int(row.metric_values[1].value)

    dau = us.get("session_start", 0)
    new_users = ev.get("first_open", 0)

    # 브랜드별 필터 토글
    brand_resp = run_report(
        client, yesterday,
        ["customEvent:brand_name"], ["eventCount"],
        dimension_filter=event_filter("map_brand_filter_toggle"),
    )
    brand_lines = []
    for row in sorted(brand_resp.rows, key=lambda r: int(r.metric_values[0].value), reverse=True):
        name = row.dimension_values[0].value
        count = row.metric_values[0].value
        if name != "(not set)":
            brand_lines.append(f"  └ {name} {count}회")

    # 업로드 method별
    upload_resp = run_report(
        client, yesterday,
        ["customEvent:method"], ["eventCount"],
        dimension_filter=event_filter("photo_upload"),
    )
    upload = {}
    for row in upload_resp.rows:
        method = row.dimension_values[0].value
        upload[method] = int(row.metric_values[0].value)

    gallery = upload.get("gallery", 0)
    qr = upload.get("qr", 0)

    def field(name, lines):
        return {"name": name, "value": "\n".join(lines) or "없음", "inline": False}

    map_lines = [
        f"진입 {ev.get('map_view', 0)}회 ({us.get('map_view', 0)}명)",
        f"재검색 {ev.get('map_re_search', 0)}회",
        f"브랜드 필터 {ev.get('map_brand_filter_toggle', 0)}회",
    ] + brand_lines + [
        f"부스 선택 {ev.get('booth_select', 0)}회 ({us.get('booth_select', 0)}명)",
        f"길찾기 {ev.get('map_route_click', 0)}회",
    ]

    pose_lines = [
        f"진입 {ev.get('pose_view', 0)}회 ({us.get('pose_view', 0)}명)",
        f"필터 토글 {ev.get('pose_filter_toggle', 0)}회",
        f"랜덤 시작 {ev.get('pose_random_start', 0)}회",
        f"북마크 {ev.get('pose_bookmark', 0)}회",
    ]

    archive_lines = [
        f"진입 {ev.get('archiving_view', 0)}회 ({us.get('archiving_view', 0)}명)",
        f"사진 상세 {ev.get('photo_detail_view', 0)}회",
        f"메모 작성 {ev.get('photo_memo_create', 0)}회",
        f"앨범 생성 {ev.get('album_create', 0)}회",
    ]

    upload_lines = [
        f"갤러리 {gallery}회  |  QR {qr}회",
    ]

    embed = {
        "title": f"📊 GA4 일간 리포트 · {yesterday}",
        "color": 0x5865F2,
        "fields": [
            {"name": "👥 DAU", "value": f"**{dau}명** (신규 {new_users}명)", "inline": False},
            field("🗺 지도", map_lines),
            field("🧘 포즈", pose_lines),
            field("📦 아카이브", archive_lines),
            field("📤 사진 업로드", upload_lines),
        ],
        "footer": {"text": "neki · GA4 자동 리포트"},
    }

    payload = {
        "username": "네키 GA 봇",
        "avatar_url": "https://i.ifh.cc/PbdkGM.jpg",
        "embeds": [embed],
    }

    resp = requests.post(DISCORD_WEBHOOK_URL, json=payload)
    resp.raise_for_status()
    print(f"전송 완료: {resp.status_code} / {yesterday}")


if __name__ == "__main__":
    main()
