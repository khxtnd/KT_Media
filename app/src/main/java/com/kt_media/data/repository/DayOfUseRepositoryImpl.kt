package com.kt_media.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.domain.constant.CHILD_DAY_OF_USE
import com.kt_media.domain.constant.CHILD_USER_ID
import com.kt_media.domain.entities.DayOfUse
import com.kt_media.domain.repository.DayOfUseRepository
import com.kt_media.domain.usecase.GetUserIdUseCase
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DayOfUseRepositoryImpl(private val getUserIdUseCase: GetUserIdUseCase) : DayOfUseRepository {
    override suspend fun getDayOfUseList(startDate: String, endDate: String): List<DayOfUse> {
        val userId = getUserIdUseCase.execute()
        return suspendCoroutine { continuation ->
            val dbRefDayOfUse = FirebaseDatabase.getInstance().getReference(CHILD_DAY_OF_USE)
            val query = dbRefDayOfUse.orderByChild(CHILD_USER_ID).equalTo(userId)
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val startMillis = dateFormat.parse(startDate)?.time ?: 0
            val endMillis = dateFormat.parse(endDate)?.time ?: Long.MAX_VALUE

            query.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val dayOfUseList = mutableListOf<DayOfUse>()
                    for (data: DataSnapshot in snapshot.children) {
                        val dayOfUse = data.getValue(DayOfUse::class.java)
                        val date = dayOfUse?.date
                        val dateToMillis = date?.let { dateFormat.parse(it)?.time } ?: 0
                        if (dateToMillis in startMillis..endMillis) {
                            dayOfUse?.let { dayOfUseList.add(it) }
                        }
                    }
                    if (dayOfUseList.isNotEmpty()) {
                        dayOfUseList.sortBy { dateFormat.parse(it.date)?.time }
                    }
                    continuation.resume(dayOfUseList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

}