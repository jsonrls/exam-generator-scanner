package com.pbec.preboardexamchecker.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pbec.preboardexamchecker.data.models.TransactionLog;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TransactionLogDao_Impl implements TransactionLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TransactionLog> __insertionAdapterOfTransactionLog;

  public TransactionLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTransactionLog = new EntityInsertionAdapter<TransactionLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transaction_logs` (`id`,`action`,`subject`,`details`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionLog entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getAction());
        if (entity.getSubject() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getSubject());
        }
        statement.bindString(4, entity.getDetails());
        statement.bindLong(5, entity.getCreatedAt());
      }
    };
  }

  @Override
  public Object insertTransaction(final TransactionLog log,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTransactionLog.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TransactionLog>> getAllTransactions() {
    final String _sql = "SELECT * FROM transaction_logs ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transaction_logs"}, new Callable<List<TransactionLog>>() {
      @Override
      @NonNull
      public List<TransactionLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAction = CursorUtil.getColumnIndexOrThrow(_cursor, "action");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "details");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TransactionLog> _result = new ArrayList<TransactionLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpAction;
            _tmpAction = _cursor.getString(_cursorIndexOfAction);
            final String _tmpSubject;
            if (_cursor.isNull(_cursorIndexOfSubject)) {
              _tmpSubject = null;
            } else {
              _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            }
            final String _tmpDetails;
            _tmpDetails = _cursor.getString(_cursorIndexOfDetails);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TransactionLog(_tmpId,_tmpAction,_tmpSubject,_tmpDetails,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
