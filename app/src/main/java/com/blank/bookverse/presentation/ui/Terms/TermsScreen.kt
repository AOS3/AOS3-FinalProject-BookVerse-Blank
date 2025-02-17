package com.blank.bookverse.presentation.ui.Terms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            BookVerseToolbar(title = "이용약관",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = "뒤로 가기",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                })
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            val terms = listOf(
                "제 1조 (목적)" to "이 약관은 북버스(BookVerse)와 이용자 간에 본 어플의 사용과 관련하여 권리, 의무 및 책임사항을 규정하는 것을 목적으로 합니다.",
                "제 2조 (용어의 정의)" to """
                    1. 이용자란 본 어플을 이용하는 자를 말합니다.
                    2. 서비스란 회사가 본 어플을 통해 제공하는 책 글귀 저장 및 관련 기능을 말합니다.
                    3. 저장된 글귀란 이용자가 본 어플을 통해 저장한 책에서 발췌한 글귀를 의미합니다.
                    4. 회원이란 본 어플에 회원가입을 하고 로그인하여 서비스를 이용하는 이용자를 의미합니다.
                """.trimIndent(),
                "제 3조 (이용계약의 체결)" to """
                    1. 이용자가 본 약관에 동의하고 본 어플을 설치 및 이용함으로써 이용계약이 체결됩니다.
                    2. 이용자가 본 어플을 다운로드하거나 이용을 시작할 때 본 약관에 동의한 것으로 간주됩니다.
                """.trimIndent(),
                "제 4조 (서비스의 제공 및 변경)" to """
                    1. 회사는 본 어플을 통해 책 글귀 저장, 검색, 공유 등 다양한 기능을 제공합니다.
                    2. 회사는 필요에 따라 서비스 내용을 추가하거나 변경할 수 있으며, 변경 사항은 사전에 공지합니다.
                    3. 회사는 천재지변, 시스템 오류 등 불가항력적인 사유로 서비스 제공이 일시 중지될 수 있습니다.
                """.trimIndent(),
                "제 5조 (이용자의 의무)" to """
                    1. 이용자는 본 어플을 이용함에 있어 타인의 권리를 침해하거나 불법적인 행위를 해서는 안 됩니다.
                    2. 이용자는 본 어플에 저장된 글귀가 저작권법 등 법률에 위반되지 않도록 해야 하며, 타인의 저작물을 무단으로 업로드하거나 배포해서는 안 됩니다.
                    3. 이용자는 자신의 계정 정보를 타인에게 제공하거나 양도할 수 없습니다.
                """.trimIndent(),
                "제 6조 (저작권 및 지적 재산권)" to """
                    1. 본 애플리케이션에 포함된 모든 콘텐츠(텍스트, 이미지 등)의 저작권 및 지적 재산권은 회사에 있습니다.
                    2. 이용자는 허용된 경우를 제외한 해당 저작 및 출판사의 사전 승인 없이 콘텐츠를 복제 또는 재배포할 수 없습니다.
                    3. 이용자는 해당 콘텐츠를 상업적 용도로 사용하거나 타인에게 제공할 수 없습니다.
                """.trimIndent(),
                "제 7조 (개인정보 보호)" to """
                    1. 회사는 이용자의 개인정보를 보호하기 위해 관련 법률을 준수합니다.
                    2. 이용자는 애플리케이션을 사용하는 동안 개인정보 제공에 대한 동의를 철회할 수 있습니다.
                """.trimIndent(),
                "제 8조 (서비스 이용의 제한 및 정지)" to """
                    1. 회사는 이용자가 본 약관을 위반한 경우, 사전 경고 없이 서비스 이용을 제한하거나 정지할 수 있습니다.
                """.trimIndent(),
                "제 9조 (면책 조항)" to """
                    1. 회사는 이용자의 부주의로 인한 서비스 이용 장애, 데이터 손실 등에 대해 책임지지 않습니다.
                    2. 회사는 천재지변, 시스템 장애 등 불가항력적인 사유로 인해 발생한 손해에 대해 책임을 지지 않습니다.
                    3. 이용자가 저장한 글귀나 콘텐츠의 저작권 관련 문제에 대해 회사는 책임지지 않으며, 관련 법적 문제는 이용자가 직접 해결해야 합니다.
                """.trimIndent(),
                "제 10조 (약관의 변경)" to """
                    1. 회사는 본 약관을 변경할 수 있으며, 변경 사항은 사전에 공지합니다.
                    2. 변경된 약관은 공지일로부터 7일 이후 효력이 발생합니다.
                    3. 이용자가 변경된 약관에 동의하지 않으면 서비스 이용을 중단할 수 있습니다.
                """.trimIndent(),
                "제 11조 (관할 법원)" to """
                    본 약관에 관한 분쟁이 발생할 경우, 회사의 본사가 위치한 법원을 제1심 법원으로 합니다.
                """.trimIndent(),
                "부칙" to "이 약관은 2025.03.06부터 시행됩니다."
            )

            terms.forEach { (title, content) ->
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )

                // 제목과 본문 사이에 줄 바꿈 역할
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )

                // 조항 간 간격을 추가하여 가독성 향상
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
