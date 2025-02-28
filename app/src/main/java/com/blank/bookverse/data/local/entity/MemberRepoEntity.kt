package com.blank.bookverse.data.local.entity

import androidx.activity.result.contract.ActivityResultContracts
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github_repos")
data class MemberRepoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val documentId: String,
    val reSearchList: String,
    val visibility: String,
)