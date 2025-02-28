package com.blank.bookverse.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blank.bookverse.data.local.dao.MemberRepoDao
import com.blank.bookverse.data.local.entity.MemberRepoEntity

@Database(entities = [MemberRepoEntity::class], version = 1, exportSchema = false)
abstract class MemberDatabase : RoomDatabase() {
    abstract fun dao(): MemberRepoDao
    companion object{
        var memberDatabase:MemberDatabase? = null
        @Synchronized
        fun getInstance(context: Context) : MemberDatabase?{
            synchronized(MemberDatabase::class){
                memberDatabase = Room.databaseBuilder(
                    context.applicationContext, MemberDatabase::class.java,
                    "Member.db"
                ).build()
            }
            return memberDatabase
        }
    }

}