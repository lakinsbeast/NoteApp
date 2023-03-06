package code.with.me.testroomandnavigationdrawertest.data.localDataSource

import android.database.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

sealed class DatabaseResult<T: Any> {
    class Success<T: Any>(val data: T): DatabaseResult<T>()
    class Exception<T: Any>(val e: SQLiteException?): DatabaseResult<T>()
}

