package com.blank.bookverse.presentation.common

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blank.bookverse.R


@Composable
fun BookVerseTextField(
    // 입력값에 대한 상태관리 변수
    textFieldValue: MutableState<String> = mutableStateOf(""),
    // 포커스가 주여지고 입력된 내용이 없을 경우 보여줄 안내 문구
    placeHolder:String = "",
    // 입력 제한을 주기위한 정규식
    inputCondition:String? = null,
    // 입력 요소 앞의 아이콘
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier,
    // 우측 끝의 아이콘
    trailingIconMode: LikeLionOutlinedTextFieldEndIconMode = LikeLionOutlinedTextFieldEndIconMode.NONE,
    // 한줄 입력 여부
    singleLine:Boolean = false,
    // 상단 여백
    paddingTop:Dp = 0.dp,
    // 입력 모드
    inputType: LikeLionOutlinedTextFieldInputType = LikeLionOutlinedTextFieldInputType.TEXT,
    // 입력 가능 여부
    readOnly: Boolean = false,
    // 포커싱 관리
    focusRequest: MutableState<FocusRequester>? = null,
    // 입력 요소 하단에 나오는 메세지
    supportText:MutableState<String>? = null,
    // 에러 표시
    isError:MutableState<Boolean>? = null,
    //IntRangeError
    textRange: TextRange? = null,
    //textRegex 텍스트 검사
    regexList: List<Pair<String, Regex>>? = null,
    gridRow: Int = 3,
    // 키보드 옵션 및 동작 추가 (기본값 제공)
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    // 입력값이 변경될 때 호출되는 콜백 함수
    onTrailingIconClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    onValueChange: (String) -> Unit = {},
) {
    val gridRow by remember { mutableStateOf(gridRow) }
    // 비밀번호가 보이는지...
    var isShowingPasswordFlag by rememberSaveable {
        mutableStateOf(false)
    }

    // Modify 생성
    var defaultModifier = modifier.padding(top = paddingTop)
    if(focusRequest != null){
        Modifier.focusRequester(focusRequest.value)
    }
    Column {
        OutlinedTextField(
            modifier = defaultModifier,
            value = textFieldValue.value,
            label = null,
            placeholder = {
                Text(
                    text = placeHolder,
                    color = MaterialTheme
                        .colorScheme
                        .onSurface
                        .copy(alpha = 0.5f)) // 플레이스홀더 텍스트
            },
            onValueChange = {
                var filteredValue = if (textRange == null) {
                    // 조건이 없으면 원래 값 그대로 사용
                    it
                } else {
                    if (it.length>textRange.end-1 && textRange.end-1 != Int.MAX_VALUE){
                        it.substring(0,textRange.end-1)
                    }else it
                }

                val regexText = if (inputCondition == null){
                    filteredValue
                }else{
                    var string = ""
                    // 정규식으로 필터링
                    string = filteredValue.replace(inputCondition.toRegex(), "")

                    string
                }
                // 필터링된 값을 상태에 반영
                textFieldValue.value = regexText


                // 필터링된 값을 콜백으로 전달
                onValueChange(regexText)
            },
            leadingIcon = if(leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null
                    )
                }
            } else {
                null
            },
            trailingIcon = when(trailingIconMode){
                LikeLionOutlinedTextFieldEndIconMode.NONE -> null
                LikeLionOutlinedTextFieldEndIconMode.TEXT -> {
                    {
                        if(textFieldValue.value.isNotEmpty()){
                            IconButton(
                                onClick = {
                                    textFieldValue.value = ""
                                    onTrailingIconClick?.invoke()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = null
                                )
                            }
                        } else {
                            null
                        }
                    }
                }
                LikeLionOutlinedTextFieldEndIconMode.PASSWORD -> {
                    {
                        IconButton(
                            onClick = {
                                isShowingPasswordFlag = !isShowingPasswordFlag
                            }
                        ) {
                            if(isShowingPasswordFlag){
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_pw_eye_on),
                                    contentDescription = null,
                                    modifier = Modifier.size(25.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_pw_eye_off),
                                    contentDescription = null,
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                    }
                }
            },
            singleLine = singleLine,
            visualTransformation = if (isShowingPasswordFlag == false && inputType == LikeLionOutlinedTextFieldInputType.PASSWORD){
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            readOnly = readOnly,
            supportingText = if(supportText != null){
                {
                    Text(text = supportText.value)
                }
            } else {
                null
            },
            //isError = isError.value,
            // keyboardOptions = keyboardOptions, // 키보드 옵션 추가
            keyboardActions = keyboardActions,  // 키보드 동작 추가
            keyboardOptions = if (inputType == LikeLionOutlinedTextFieldInputType.NUMBER) {
                KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            } else {
                KeyboardOptions.Default
            },
            colors =  OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.DarkGray

            ),
            interactionSource = interactionSource,


        )

        if (isError != null && regexList != null && regexList.isNotEmpty())
            ErrorText(gridRow = gridRow,textFieldValue,isError, textRange,regexList)
    }

}

enum class LikeLionOutlinedTextFieldEndIconMode{
    NONE,
    TEXT,
    PASSWORD,
}

enum class LikeLionOutlinedTextFieldInputType{
    TEXT,
    PASSWORD,
    NUMBER,
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ErrorText(
    gridRow:Int,
    textValue: MutableState<String>,
    error: MutableState<Boolean>,
    textRange: TextRange?,
    regexList: List<Pair<String, Regex>>
){
    val inEach = (regexList.size + if (textRange != null)1 else 0)
    var textLengthValue by rememberSaveable { mutableStateOf(false) }
    // 이후에 넣어야 값이 반영
    textLengthValue = if (textRange != null) textValue.value.length in textRange else true

    var regex: Boolean by rememberSaveable { mutableStateOf(
        regexList.fold(false){ result,it ->
            if(!textValue.value.contains(it.second))
                true
            else {
                return@fold false
            }
        }
    ) }
    Log.d("st","${textValue.value.length} $regex")

    error.value = textLengthValue && regex
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        maxItemsInEachRow = gridRow
    ) {
        var repeat = 0
        repeat(inEach){ it ->

            if(textRange != null && it == 0)
                TextLength(textValue,textRange)
            else{

                ErrorComposable(textValue,regexList[repeat].first,regexList[repeat].second)
                repeat++
            }

        }



    }
}

@Composable
fun TextLength(
    textValue: MutableState<String>,
    textRange: TextRange
){
    var text by rememberSaveable { mutableStateOf("") }
    var lengthValue by rememberSaveable { mutableStateOf(false) }
    lengthValue = textValue.value.length in textRange
    text = if (textRange.end == Int.MAX_VALUE) "${textRange.start}자 이상"
    else if(textRange.start == Int.MIN_VALUE) "${textRange.end-1}자 이하"
    else "${textRange.start}~${textRange.end-1}자"
    Row(
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            tint = when {
                textValue.value.isBlank() -> Color.LightGray
                lengthValue -> {
                    Color(0xFF0DB00C)

                } // 조건 충족
                else -> Color(0xFFB00E0E)
            },
            contentDescription = "Check",
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = text,
            color = when {
                textValue.value.isBlank() -> Color.LightGray
                lengthValue -> Color(0xFF0DB00C)
                else -> Color(0xFFB00E0E)
            },
            fontSize = 14.sp
        )
    }
}

@Composable
fun ErrorComposable(
    textValue: MutableState<String>,
    regexText: String,
    it: Regex
){
    Row(
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            tint = when {
                textValue.value.isBlank() -> Color.LightGray
                !textValue.value.contains(it) -> Color(0xFF0DB00C)
                else -> Color(0xFFB00E0E)
            },
            contentDescription = "Check",
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = regexText,
            color = when {
                textValue.value.isBlank() -> Color.LightGray
                !textValue.value.contains(it) -> Color(0xFF0DB00C)
                else -> Color(0xFFB00E0E)
            },
            fontSize = 14.sp
        )
    }
}