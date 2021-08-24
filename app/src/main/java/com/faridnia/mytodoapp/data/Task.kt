package com.faridnia.mytodoapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "task_table")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isImportant: Boolean = false,
    val isCompleted: Boolean = false,
    val createDate: Long = System.currentTimeMillis()
) : Parcelable {
    val createDateFormatted: String
        get() = DateFormat.getDateInstance().format(createDate)
}
