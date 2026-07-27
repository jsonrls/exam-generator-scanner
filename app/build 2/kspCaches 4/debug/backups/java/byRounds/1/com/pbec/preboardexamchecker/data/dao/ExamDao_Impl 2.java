package com.pbec.preboardexamchecker.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pbec.preboardexamchecker.data.models.Exam;
import com.pbec.preboardexamchecker.data.models.ListLongConverter;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ExamDao_Impl implements ExamDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Exam> __insertionAdapterOfExam;

  private ListLongConverter __listLongConverter;

  private final EntityDeletionOrUpdateAdapter<Exam> __deletionAdapterOfExam;

  private final EntityDeletionOrUpdateAdapter<Exam> __updateAdapterOfExam;

  public ExamDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExam = new EntityInsertionAdapter<Exam>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `exams` (`id`,`examName`,`subject`,`setAQuestionIds`,`setBQuestionIds`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Exam entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getExamName());
        statement.bindString(3, entity.getSubject());
        final String _tmp = __listLongConverter().fromListLong(entity.getSetAQuestionIds());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final String _tmp_1 = __listLongConverter().fromListLong(entity.getSetBQuestionIds());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
        statement.bindLong(6, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfExam = new EntityDeletionOrUpdateAdapter<Exam>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `exams` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Exam entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfExam = new EntityDeletionOrUpdateAdapter<Exam>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `exams` SET `id` = ?,`examName` = ?,`subject` = ?,`setAQuestionIds` = ?,`setBQuestionIds` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Exam entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getExamName());
        statement.bindString(3, entity.getSubject());
        final String _tmp = __listLongConverter().fromListLong(entity.getSetAQuestionIds());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final String _tmp_1 = __listLongConverter().fromListLong(entity.getSetBQuestionIds());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insertExam(final Exam exam, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfExam.insertAndReturnId(exam);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExam(final Exam exam, final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        int _total = 0;
        __db.beginTransaction();
        try {
          _total += __deletionAdapterOfExam.handle(exam);
          __db.setTransactionSuccessful();
          return _total;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExams(final List<Exam> exams,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        int _total = 0;
        __db.beginTransaction();
        try {
          _total += __deletionAdapterOfExam.handleMultiple(exams);
          __db.setTransactionSuccessful();
          return _total;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateExam(final Exam exam, final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        int _total = 0;
        __db.beginTransaction();
        try {
          _total += __updateAdapterOfExam.handle(exam);
          __db.setTransactionSuccessful();
          return _total;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Exam>> getAllExams() {
    final String _sql = "SELECT * FROM exams ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exams"}, new Callable<List<Exam>>() {
      @Override
      @NonNull
      public List<Exam> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfSetAQuestionIds = CursorUtil.getColumnIndexOrThrow(_cursor, "setAQuestionIds");
          final int _cursorIndexOfSetBQuestionIds = CursorUtil.getColumnIndexOrThrow(_cursor, "setBQuestionIds");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Exam> _result = new ArrayList<Exam>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Exam _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final List<Long> _tmpSetAQuestionIds;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfSetAQuestionIds)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfSetAQuestionIds);
            }
            final List<Long> _tmp_1 = __listLongConverter().toListLong(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<java.lang.Long>', but it was NULL.");
            } else {
              _tmpSetAQuestionIds = _tmp_1;
            }
            final List<Long> _tmpSetBQuestionIds;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfSetBQuestionIds)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfSetBQuestionIds);
            }
            final List<Long> _tmp_3 = __listLongConverter().toListLong(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<java.lang.Long>', but it was NULL.");
            } else {
              _tmpSetBQuestionIds = _tmp_3;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Exam(_tmpId,_tmpExamName,_tmpSubject,_tmpSetAQuestionIds,_tmpSetBQuestionIds,_tmpCreatedAt);
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

  @Override
  public Flow<List<Exam>> getExamsBySubject(final String subject) {
    final String _sql = "SELECT * FROM exams WHERE subject = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, subject);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exams"}, new Callable<List<Exam>>() {
      @Override
      @NonNull
      public List<Exam> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfSetAQuestionIds = CursorUtil.getColumnIndexOrThrow(_cursor, "setAQuestionIds");
          final int _cursorIndexOfSetBQuestionIds = CursorUtil.getColumnIndexOrThrow(_cursor, "setBQuestionIds");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Exam> _result = new ArrayList<Exam>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Exam _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final List<Long> _tmpSetAQuestionIds;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfSetAQuestionIds)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfSetAQuestionIds);
            }
            final List<Long> _tmp_1 = __listLongConverter().toListLong(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<java.lang.Long>', but it was NULL.");
            } else {
              _tmpSetAQuestionIds = _tmp_1;
            }
            final List<Long> _tmpSetBQuestionIds;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfSetBQuestionIds)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfSetBQuestionIds);
            }
            final List<Long> _tmp_3 = __listLongConverter().toListLong(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<java.lang.Long>', but it was NULL.");
            } else {
              _tmpSetBQuestionIds = _tmp_3;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Exam(_tmpId,_tmpExamName,_tmpSubject,_tmpSetAQuestionIds,_tmpSetBQuestionIds,_tmpCreatedAt);
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

  @Override
  public Object getExamsBySubjectOnce(final String subject,
      final Continuation<? super List<Exam>> $completion) {
    final String _sql = "SELECT * FROM exams WHERE subject = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, subject);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Exam>>() {
      @Override
      @NonNull
      public List<Exam> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfSetAQuestionIds = CursorUtil.getColumnIndexOrThrow(_cursor, "setAQuestionIds");
          final int _cursorIndexOfSetBQuestionIds = CursorUtil.getColumnIndexOrThrow(_cursor, "setBQuestionIds");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Exam> _result = new ArrayList<Exam>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Exam _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final List<Long> _tmpSetAQuestionIds;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfSetAQuestionIds)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfSetAQuestionIds);
            }
            final List<Long> _tmp_1 = __listLongConverter().toListLong(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<java.lang.Long>', but it was NULL.");
            } else {
              _tmpSetAQuestionIds = _tmp_1;
            }
            final List<Long> _tmpSetBQuestionIds;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfSetBQuestionIds)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfSetBQuestionIds);
            }
            final List<Long> _tmp_3 = __listLongConverter().toListLong(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<java.lang.Long>', but it was NULL.");
            } else {
              _tmpSetBQuestionIds = _tmp_3;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Exam(_tmpId,_tmpExamName,_tmpSubject,_tmpSetAQuestionIds,_tmpSetBQuestionIds,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getExamById(final long examId, final Continuation<? super Exam> $completion) {
    final String _sql = "SELECT * FROM exams WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, examId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Exam>() {
      @Override
      @Nullable
      public Exam call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfSetAQuestionIds = CursorUtil.getColumnIndexOrThrow(_cursor, "setAQuestionIds");
          final int _cursorIndexOfSetBQuestionIds = CursorUtil.getColumnIndexOrThrow(_cursor, "setBQuestionIds");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final Exam _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final List<Long> _tmpSetAQuestionIds;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfSetAQuestionIds)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfSetAQuestionIds);
            }
            final List<Long> _tmp_1 = __listLongConverter().toListLong(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<java.lang.Long>', but it was NULL.");
            } else {
              _tmpSetAQuestionIds = _tmp_1;
            }
            final List<Long> _tmpSetBQuestionIds;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfSetBQuestionIds)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfSetBQuestionIds);
            }
            final List<Long> _tmp_3 = __listLongConverter().toListLong(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<java.lang.Long>', but it was NULL.");
            } else {
              _tmpSetBQuestionIds = _tmp_3;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new Exam(_tmpId,_tmpExamName,_tmpSubject,_tmpSetAQuestionIds,_tmpSetBQuestionIds,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Arrays.asList(ListLongConverter.class);
  }

  private synchronized ListLongConverter __listLongConverter() {
    if (__listLongConverter == null) {
      __listLongConverter = __db.getTypeConverter(ListLongConverter.class);
    }
    return __listLongConverter;
  }
}
