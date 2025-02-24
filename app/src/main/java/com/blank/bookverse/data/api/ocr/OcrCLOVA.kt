package com.blank.bookverse.data.api.ocr

import com.google.gson.annotations.SerializedName

data class OcrCLOVA(
    @SerializedName("version")
    var version: String,
    @SerializedName("requestId")
    var requestId: String,
    @SerializedName("timestamp")
    var timestamp: Long,
    @SerializedName("images")
    var images: List<ImageItem>
)

data class ImageItem(
    // 이미지 UID  --  API 유효성 검사 및 요청 추적 시 사용
    @SerializedName("uid")
    var uid: String,
    // 이미지 이름  --  이미지 식별 및 응답 결과 확인 시 사용
    @SerializedName("name")
    var name: String,
    // 이미지 인식 결과 SUCCESS | FAILURE | ERROR
    @SerializedName("inferResult")
    var inferResult: String, //SUCCESS: 인식 성공 //FAILURE: 인식 실패 //ERROR: 인식 처리 예외
    // 유효성 검사 결과 정보
    @SerializedName("validationResult")
    var validationResult: ValidationResultObject,
    // 변환 이미지 정보 -- format이 pdf 또는 tiff일 때
    // 좌표 값은 호출 이미지 파일을 기준으로 설정
    @SerializedName("convertedImageInfo")
    var convertedImageInfo: ConvertedImageInfoObject,
    @SerializedName("fields")
    var field: List<FieldObject>,
    // 이미지 인식 결과 결합 정보
    @SerializedName("combineResult")
    var combineResult: CombineResultObject,
)

data class ValidationResultObject(
    //유효성 검사 결과 코드
    //NO_REQUESTED | UNCHECKED | ERROR | VALID | INVALID
    @SerializedName("message")
    var result: String, // NO_REQUESTED: 검증 작업 미요청
    // UNCHECKED: 동작 응답 미확인 및 응답 미수락
    // ERROR: 검증 시 오류 발생
    // VALID: 검증 결과 유효
    // INVALID: 검증 결과 유효하지 않음

//    // 유효성 검사 결과 세부 메시지  --  항상 응답되는 값은 아님
//    @SerializedName("message")
//    var message: String,
)

data class ConvertedImageInfoObject(
    // 변환 이미지 가로 길이
    @SerializedName("width")
    var width: Int,
    // 변환 이미지 세로 길이
    @SerializedName("height")
    var height: Int,
    // 변환 이미지 페이지 인덱스
    @SerializedName("pageIndex")
    var pageIndex: Int,
    // 변환 이미지 길이 Long 여부 true | false
    @SerializedName("longImage")
    var longImage: Boolean, // true: 긴(Long) 이미지
    // false: 긴(Long) 이미지가 아님

)

data class FieldObject(
    @SerializedName("valueType")
    var valueType: String,
    @SerializedName("inferText")
    var inferText: String,
    @SerializedName("inferConfidence")
    var inferConfidence: Float,
    @SerializedName("type")
    var type: String,
    @SerializedName("lineBreak")
    var lineBreak: Boolean,
    @SerializedName("boundingPoly")
    var boundingPoly: BoundingPoly
)

/*
* 이미지 인식 결과 결합 정보
* */
data class CombineResultObject(
    // 이미지 결합 필드 이름
    @SerializedName("name")
    var name: String,
    // 각 이미지 필드별 출력값 및 고정 텍스트
    @SerializedName("text")
    var text: String,
)

data class BoundingPoly(
    @SerializedName("vertices")
    var vertices: List<Location>,
)

data class Location(
    @SerializedName("x")
    var x: String,
    @SerializedName("y")
    var y: String,
)