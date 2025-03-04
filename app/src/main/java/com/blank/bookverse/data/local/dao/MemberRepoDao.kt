package com.blank.bookverse.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.blank.bookverse.data.local.entity.MemberRepoEntity
import com.google.firebase.firestore.DocumentId
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberRepoDao {
//    @Query("SELECT * FROM github_repos ORDER BY stars DESC")
//    fun getPagingSource(): PagingSource<Int, MemberRepoEntity>

    @Query("SELECT * FROM github_repos")
    fun getAllReposFlow(): Flow<List<MemberRepoEntity>> // ✅ 최신 데이터 Flow로 제공

    @Query("SELECT * FROM github_repos WHERE documentId = :documentId")
    fun getReposFlow(documentId: String): Flow<MemberRepoEntity?> // ✅ 최신 데이터 제공

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<MemberRepoEntity>)

    @Query("DELETE FROM github_repos")
    suspend fun clearRepos()

    @Update
    suspend fun updateMemberData(repoEntity: MemberRepoEntity)
}
