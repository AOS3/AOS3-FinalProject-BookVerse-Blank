package com.blank.bookverse.presentation.ui.findAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseToolbar

@Composable
fun FindAccountScreen(
    navController: NavHostController,
    findAccountViewModel: FindAccountViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            BookVerseToolbar(title = "아이디 / 비밀번호 찾기",
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                })
        },
        modifier = Modifier.background(Color.White)
    ) {

        val selectedTabIndex = findAccountViewModel.selectedTabIndex.collectAsState(initial = 0)

        Column(
            modifier = Modifier
                .padding(it)
                .background(Color.White)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex.value])
                            .height(2.dp)
                            .background(Color.Black)
                    )
                },
                modifier = Modifier.background(Color.White)
            ) {
                findAccountViewModel.tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        onClick = { findAccountViewModel.setSelectedTabIndex(index) },
                        text = {
                            Text(
                                text = title,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex.value) {
                0 -> FindIdScreen(navController)
                1 -> FindPwScreen(navController)
            }
        }
    }
}