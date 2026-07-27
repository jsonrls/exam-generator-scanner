package com.pbec.preboardexamchecker.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pbec.preboardexamchecker.data.models.ScanResult;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ScanResultDao_Impl implements ScanResultDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ScanResult> __insertionAdapterOfScanResult;

  private final EntityInsertionAdapter<ScanResult> __insertionAdapterOfScanResult_1;

  private final SharedSQLiteStatement __preparedStmtOfMarkSynced;

  private final SharedSQLiteStatement __preparedStmtOfUpdateRemoteId;

  private final SharedSQLiteStatement __preparedStmtOfSoftDeleteByExamId;

  private final SharedSQLiteStatement __preparedStmtOfSoftDeleteBySubject;

  private final SharedSQLiteStatement __preparedStmtOfSoftDeleteCascadeByExamId;

  private final SharedSQLiteStatement __preparedStmtOfRestoreCascadeByExamId;

  public ScanResultDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScanResult = new EntityInsertionAdapter<ScanResult>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `scan_results` (`id`,`studentId`,`studentName`,`studentBlock`,`studentYearLevel`,`studentProgram`,`subject`,`examId`,`examName`,`clusterId`,`clusterName`,`testSet`,`rawAnswers`,`score`,`total`,`passed`,`scannedAt`,`syncedAt`,`remoteId`,`deletedAt`,`trashedByExamId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScanResult entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getStudentId());
        statement.bindString(3, entity.getStudentName());
        statement.bindString(4, entity.getStudentBlock());
        statement.bindString(5, entity.getStudentYearLevel());
        statement.bindString(6, entity.getStudentProgram());
        statement.bindString(7, entity.getSubject());
        statement.bindLong(8, entity.getExamId());
        statement.bindString(9, entity.getExamName());
        if (entity.getClusterId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getClusterId());
        }
        if (entity.getClusterName() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getClusterName());
        }
        statement.bindString(12, entity.getTestSet());
        statement.bindString(13, entity.getRawAnswers());
        statement.bindLong(14, entity.getScore());
        statement.bindLong(15, entity.getTotal());
        final int _tmp = entity.getPassed() ? 1 : 0;
        statement.bindLong(16, _tmp);
        statement.bindLong(17, entity.getScannedAt());
        if (entity.getSyncedAt() == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, entity.getSyncedAt());
        }
        if (entity.getRemoteId() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getRemoteId());
        }
        if (entity.getDeletedAt() == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, entity.getDeletedAt());
        }
        if (entity.getTrashedByExamId() == null) {
          statement.bindNull(21);
        } else {
          statement.bindLong(21, entity.getTrashedByExamId());
        }
      }
    };
    this.__insertionAdapterOfScanResult_1 = new EntityInsertionAdapter<ScanResult>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `scan_results` (`id`,`studentId`,`studentName`,`studentBlock`,`studentYearLevel`,`studentProgram`,`subject`,`examId`,`examName`,`clusterId`,`clusterName`,`testSet`,`rawAnswers`,`score`,`total`,`passed`,`scannedAt`,`syncedAt`,`remoteId`,`deletedAt`,`trashedByExamId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScanResult entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getStudentId());
        statement.bindString(3, entity.getStudentName());
        statement.bindString(4, entity.getStudentBlock());
        statement.bindString(5, entity.getStudentYearLevel());
        statement.bindString(6, entity.getStudentProgram());
        statement.bindString(7, entity.getSubject());
        statement.bindLong(8, entity.getExamId());
        statement.bindString(9, entity.getExamName());
        if (entity.getClusterId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getClusterId());
        }
        if (entity.getClusterName() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getClusterName());
        }
        statement.bindString(12, entity.getTestSet());
        statement.bindString(13, entity.getRawAnswers());
        statement.bindLong(14, entity.getScore());
        statement.bindLong(15, entity.getTotal());
        final int _tmp = entity.getPassed() ? 1 : 0;
        statement.bindLong(16, _tmp);
        statement.bindLong(17, entity.getScannedAt());
        if (entity.getSyncedAt() == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, entity.getSyncedAt());
        }
        if (entity.getRemoteId() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getRemoteId());
        }
        if (entity.getDeletedAt() == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, entity.getDeletedAt());
        }
        if (entity.getTrashedByExamId() == null) {
          statement.bindNull(21);
        } else {
          statement.bindLong(21, entity.getTrashedByExamId());
        }
      }
    };
    this.__preparedStmtOfMarkSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE scan_results SET syncedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateRemoteId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE scan_results SET remoteId = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSoftDeleteByExamId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE scan_results SET deletedAt = ?, syncedAt = NULL WHERE examId = ? AND deletedAt IS NULL";
        return _query;
      }
    };
    this.__preparedStmtOfSoftDeleteBySubject = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE scan_results SET deletedAt = ?, syncedAt = NULL WHERE subject = ? AND deletedAt IS NULL";
        return _query;
      }
    };
    this.__preparedStmtOfSoftDeleteCascadeByExamId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE scan_results SET deletedAt = ?, trashedByExamId = ?, syncedAt = NULL WHERE examId = ? AND deletedAt IS NULL";
        return _query;
      }
    };
    this.__preparedStmtOfRestoreCascadeByExamId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE scan_results SET deletedAt = NULL, trashedByExamId = NULL, syncedAt = NULL WHERE trashedByExamId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ScanResult result, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfScanResult.insertAndReturnId(result);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertOrIgnore(final List<ScanResult> results,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfScanResult_1.insert(results);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markSynced(final long id, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkSynced.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkSynced.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRemoteId(final long id, final String remoteId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateRemoteId.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, remoteId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateRemoteId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object softDeleteByExamId(final long examId, final long deletedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDeleteByExamId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deletedAt);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, examId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSoftDeleteByExamId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object softDeleteBySubject(final String subject, final long deletedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDeleteBySubject.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deletedAt);
        _argIndex = 2;
        _stmt.bindString(_argIndex, subject);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSoftDeleteBySubject.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object softDeleteCascadeByExamId(final long examId, final long deletedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDeleteCascadeByExamId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deletedAt);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, examId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, examId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSoftDeleteCascadeByExamId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreCascadeByExamId(final long examId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRestoreCascadeByExamId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, examId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfRestoreCascadeByExamId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScanResult>> getAll() {
    final String _sql = "SELECT * FROM scan_results WHERE deletedAt IS NULL ORDER BY scannedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scan_results"}, new Callable<List<ScanResult>>() {
      @Override
      @NonNull
      public List<ScanResult> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "studentName");
          final int _cursorIndexOfStudentBlock = CursorUtil.getColumnIndexOrThrow(_cursor, "studentBlock");
          final int _cursorIndexOfStudentYearLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "studentYearLevel");
          final int _cursorIndexOfStudentProgram = CursorUtil.getColumnIndexOrThrow(_cursor, "studentProgram");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfClusterName = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterName");
          final int _cursorIndexOfTestSet = CursorUtil.getColumnIndexOrThrow(_cursor, "testSet");
          final int _cursorIndexOfRawAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "rawAnswers");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfScannedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scannedAt");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfTrashedByExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "trashedByExamId");
          final List<ScanResult> _result = new ArrayList<ScanResult>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScanResult _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentBlock;
            _tmpStudentBlock = _cursor.getString(_cursorIndexOfStudentBlock);
            final String _tmpStudentYearLevel;
            _tmpStudentYearLevel = _cursor.getString(_cursorIndexOfStudentYearLevel);
            final String _tmpStudentProgram;
            _tmpStudentProgram = _cursor.getString(_cursorIndexOfStudentProgram);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final long _tmpExamId;
            _tmpExamId = _cursor.getLong(_cursorIndexOfExamId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final Long _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getLong(_cursorIndexOfClusterId);
            }
            final String _tmpClusterName;
            if (_cursor.isNull(_cursorIndexOfClusterName)) {
              _tmpClusterName = null;
            } else {
              _tmpClusterName = _cursor.getString(_cursorIndexOfClusterName);
            }
            final String _tmpTestSet;
            _tmpTestSet = _cursor.getString(_cursorIndexOfTestSet);
            final String _tmpRawAnswers;
            _tmpRawAnswers = _cursor.getString(_cursorIndexOfRawAnswers);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final long _tmpScannedAt;
            _tmpScannedAt = _cursor.getLong(_cursorIndexOfScannedAt);
            final Long _tmpSyncedAt;
            if (_cursor.isNull(_cursorIndexOfSyncedAt)) {
              _tmpSyncedAt = null;
            } else {
              _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            }
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final Long _tmpTrashedByExamId;
            if (_cursor.isNull(_cursorIndexOfTrashedByExamId)) {
              _tmpTrashedByExamId = null;
            } else {
              _tmpTrashedByExamId = _cursor.getLong(_cursorIndexOfTrashedByExamId);
            }
            _item = new ScanResult(_tmpId,_tmpStudentId,_tmpStudentName,_tmpStudentBlock,_tmpStudentYearLevel,_tmpStudentProgram,_tmpSubject,_tmpExamId,_tmpExamName,_tmpClusterId,_tmpClusterName,_tmpTestSet,_tmpRawAnswers,_tmpScore,_tmpTotal,_tmpPassed,_tmpScannedAt,_tmpSyncedAt,_tmpRemoteId,_tmpDeletedAt,_tmpTrashedByExamId);
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
  public Flow<List<ScanResult>> getTrashed() {
    final String _sql = "SELECT * FROM scan_results WHERE deletedAt IS NOT NULL AND trashedByExamId IS NULL ORDER BY deletedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scan_results"}, new Callable<List<ScanResult>>() {
      @Override
      @NonNull
      public List<ScanResult> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "studentName");
          final int _cursorIndexOfStudentBlock = CursorUtil.getColumnIndexOrThrow(_cursor, "studentBlock");
          final int _cursorIndexOfStudentYearLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "studentYearLevel");
          final int _cursorIndexOfStudentProgram = CursorUtil.getColumnIndexOrThrow(_cursor, "studentProgram");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfClusterName = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterName");
          final int _cursorIndexOfTestSet = CursorUtil.getColumnIndexOrThrow(_cursor, "testSet");
          final int _cursorIndexOfRawAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "rawAnswers");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfScannedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scannedAt");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfTrashedByExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "trashedByExamId");
          final List<ScanResult> _result = new ArrayList<ScanResult>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScanResult _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentBlock;
            _tmpStudentBlock = _cursor.getString(_cursorIndexOfStudentBlock);
            final String _tmpStudentYearLevel;
            _tmpStudentYearLevel = _cursor.getString(_cursorIndexOfStudentYearLevel);
            final String _tmpStudentProgram;
            _tmpStudentProgram = _cursor.getString(_cursorIndexOfStudentProgram);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final long _tmpExamId;
            _tmpExamId = _cursor.getLong(_cursorIndexOfExamId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final Long _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getLong(_cursorIndexOfClusterId);
            }
            final String _tmpClusterName;
            if (_cursor.isNull(_cursorIndexOfClusterName)) {
              _tmpClusterName = null;
            } else {
              _tmpClusterName = _cursor.getString(_cursorIndexOfClusterName);
            }
            final String _tmpTestSet;
            _tmpTestSet = _cursor.getString(_cursorIndexOfTestSet);
            final String _tmpRawAnswers;
            _tmpRawAnswers = _cursor.getString(_cursorIndexOfRawAnswers);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final long _tmpScannedAt;
            _tmpScannedAt = _cursor.getLong(_cursorIndexOfScannedAt);
            final Long _tmpSyncedAt;
            if (_cursor.isNull(_cursorIndexOfSyncedAt)) {
              _tmpSyncedAt = null;
            } else {
              _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            }
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final Long _tmpTrashedByExamId;
            if (_cursor.isNull(_cursorIndexOfTrashedByExamId)) {
              _tmpTrashedByExamId = null;
            } else {
              _tmpTrashedByExamId = _cursor.getLong(_cursorIndexOfTrashedByExamId);
            }
            _item = new ScanResult(_tmpId,_tmpStudentId,_tmpStudentName,_tmpStudentBlock,_tmpStudentYearLevel,_tmpStudentProgram,_tmpSubject,_tmpExamId,_tmpExamName,_tmpClusterId,_tmpClusterName,_tmpTestSet,_tmpRawAnswers,_tmpScore,_tmpTotal,_tmpPassed,_tmpScannedAt,_tmpSyncedAt,_tmpRemoteId,_tmpDeletedAt,_tmpTrashedByExamId);
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
  public Flow<List<ScanResult>> getBySubject(final String subject) {
    final String _sql = "SELECT * FROM scan_results WHERE subject = ? AND deletedAt IS NULL ORDER BY scannedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, subject);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scan_results"}, new Callable<List<ScanResult>>() {
      @Override
      @NonNull
      public List<ScanResult> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "studentName");
          final int _cursorIndexOfStudentBlock = CursorUtil.getColumnIndexOrThrow(_cursor, "studentBlock");
          final int _cursorIndexOfStudentYearLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "studentYearLevel");
          final int _cursorIndexOfStudentProgram = CursorUtil.getColumnIndexOrThrow(_cursor, "studentProgram");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfClusterName = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterName");
          final int _cursorIndexOfTestSet = CursorUtil.getColumnIndexOrThrow(_cursor, "testSet");
          final int _cursorIndexOfRawAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "rawAnswers");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfScannedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scannedAt");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfTrashedByExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "trashedByExamId");
          final List<ScanResult> _result = new ArrayList<ScanResult>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScanResult _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentBlock;
            _tmpStudentBlock = _cursor.getString(_cursorIndexOfStudentBlock);
            final String _tmpStudentYearLevel;
            _tmpStudentYearLevel = _cursor.getString(_cursorIndexOfStudentYearLevel);
            final String _tmpStudentProgram;
            _tmpStudentProgram = _cursor.getString(_cursorIndexOfStudentProgram);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final long _tmpExamId;
            _tmpExamId = _cursor.getLong(_cursorIndexOfExamId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final Long _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getLong(_cursorIndexOfClusterId);
            }
            final String _tmpClusterName;
            if (_cursor.isNull(_cursorIndexOfClusterName)) {
              _tmpClusterName = null;
            } else {
              _tmpClusterName = _cursor.getString(_cursorIndexOfClusterName);
            }
            final String _tmpTestSet;
            _tmpTestSet = _cursor.getString(_cursorIndexOfTestSet);
            final String _tmpRawAnswers;
            _tmpRawAnswers = _cursor.getString(_cursorIndexOfRawAnswers);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final long _tmpScannedAt;
            _tmpScannedAt = _cursor.getLong(_cursorIndexOfScannedAt);
            final Long _tmpSyncedAt;
            if (_cursor.isNull(_cursorIndexOfSyncedAt)) {
              _tmpSyncedAt = null;
            } else {
              _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            }
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final Long _tmpTrashedByExamId;
            if (_cursor.isNull(_cursorIndexOfTrashedByExamId)) {
              _tmpTrashedByExamId = null;
            } else {
              _tmpTrashedByExamId = _cursor.getLong(_cursorIndexOfTrashedByExamId);
            }
            _item = new ScanResult(_tmpId,_tmpStudentId,_tmpStudentName,_tmpStudentBlock,_tmpStudentYearLevel,_tmpStudentProgram,_tmpSubject,_tmpExamId,_tmpExamName,_tmpClusterId,_tmpClusterName,_tmpTestSet,_tmpRawAnswers,_tmpScore,_tmpTotal,_tmpPassed,_tmpScannedAt,_tmpSyncedAt,_tmpRemoteId,_tmpDeletedAt,_tmpTrashedByExamId);
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
  public Object getResultsForStudent(final String studentId,
      final Continuation<? super List<ScanResult>> $completion) {
    final String _sql = "SELECT * FROM scan_results WHERE studentId = ? ORDER BY scannedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, studentId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ScanResult>>() {
      @Override
      @NonNull
      public List<ScanResult> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "studentName");
          final int _cursorIndexOfStudentBlock = CursorUtil.getColumnIndexOrThrow(_cursor, "studentBlock");
          final int _cursorIndexOfStudentYearLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "studentYearLevel");
          final int _cursorIndexOfStudentProgram = CursorUtil.getColumnIndexOrThrow(_cursor, "studentProgram");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfClusterName = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterName");
          final int _cursorIndexOfTestSet = CursorUtil.getColumnIndexOrThrow(_cursor, "testSet");
          final int _cursorIndexOfRawAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "rawAnswers");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfScannedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scannedAt");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfTrashedByExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "trashedByExamId");
          final List<ScanResult> _result = new ArrayList<ScanResult>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScanResult _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentBlock;
            _tmpStudentBlock = _cursor.getString(_cursorIndexOfStudentBlock);
            final String _tmpStudentYearLevel;
            _tmpStudentYearLevel = _cursor.getString(_cursorIndexOfStudentYearLevel);
            final String _tmpStudentProgram;
            _tmpStudentProgram = _cursor.getString(_cursorIndexOfStudentProgram);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final long _tmpExamId;
            _tmpExamId = _cursor.getLong(_cursorIndexOfExamId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final Long _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getLong(_cursorIndexOfClusterId);
            }
            final String _tmpClusterName;
            if (_cursor.isNull(_cursorIndexOfClusterName)) {
              _tmpClusterName = null;
            } else {
              _tmpClusterName = _cursor.getString(_cursorIndexOfClusterName);
            }
            final String _tmpTestSet;
            _tmpTestSet = _cursor.getString(_cursorIndexOfTestSet);
            final String _tmpRawAnswers;
            _tmpRawAnswers = _cursor.getString(_cursorIndexOfRawAnswers);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final long _tmpScannedAt;
            _tmpScannedAt = _cursor.getLong(_cursorIndexOfScannedAt);
            final Long _tmpSyncedAt;
            if (_cursor.isNull(_cursorIndexOfSyncedAt)) {
              _tmpSyncedAt = null;
            } else {
              _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            }
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final Long _tmpTrashedByExamId;
            if (_cursor.isNull(_cursorIndexOfTrashedByExamId)) {
              _tmpTrashedByExamId = null;
            } else {
              _tmpTrashedByExamId = _cursor.getLong(_cursorIndexOfTrashedByExamId);
            }
            _item = new ScanResult(_tmpId,_tmpStudentId,_tmpStudentName,_tmpStudentBlock,_tmpStudentYearLevel,_tmpStudentProgram,_tmpSubject,_tmpExamId,_tmpExamName,_tmpClusterId,_tmpClusterName,_tmpTestSet,_tmpRawAnswers,_tmpScore,_tmpTotal,_tmpPassed,_tmpScannedAt,_tmpSyncedAt,_tmpRemoteId,_tmpDeletedAt,_tmpTrashedByExamId);
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
  public Object getActiveByStudentAndExam(final String studentId, final long examId,
      final Continuation<? super List<ScanResult>> $completion) {
    final String _sql = "SELECT * FROM scan_results WHERE studentId = ? AND examId = ? AND deletedAt IS NULL ORDER BY scannedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, studentId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, examId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ScanResult>>() {
      @Override
      @NonNull
      public List<ScanResult> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "studentName");
          final int _cursorIndexOfStudentBlock = CursorUtil.getColumnIndexOrThrow(_cursor, "studentBlock");
          final int _cursorIndexOfStudentYearLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "studentYearLevel");
          final int _cursorIndexOfStudentProgram = CursorUtil.getColumnIndexOrThrow(_cursor, "studentProgram");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfClusterName = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterName");
          final int _cursorIndexOfTestSet = CursorUtil.getColumnIndexOrThrow(_cursor, "testSet");
          final int _cursorIndexOfRawAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "rawAnswers");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfScannedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scannedAt");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfTrashedByExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "trashedByExamId");
          final List<ScanResult> _result = new ArrayList<ScanResult>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScanResult _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentBlock;
            _tmpStudentBlock = _cursor.getString(_cursorIndexOfStudentBlock);
            final String _tmpStudentYearLevel;
            _tmpStudentYearLevel = _cursor.getString(_cursorIndexOfStudentYearLevel);
            final String _tmpStudentProgram;
            _tmpStudentProgram = _cursor.getString(_cursorIndexOfStudentProgram);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final long _tmpExamId;
            _tmpExamId = _cursor.getLong(_cursorIndexOfExamId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final Long _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getLong(_cursorIndexOfClusterId);
            }
            final String _tmpClusterName;
            if (_cursor.isNull(_cursorIndexOfClusterName)) {
              _tmpClusterName = null;
            } else {
              _tmpClusterName = _cursor.getString(_cursorIndexOfClusterName);
            }
            final String _tmpTestSet;
            _tmpTestSet = _cursor.getString(_cursorIndexOfTestSet);
            final String _tmpRawAnswers;
            _tmpRawAnswers = _cursor.getString(_cursorIndexOfRawAnswers);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final long _tmpScannedAt;
            _tmpScannedAt = _cursor.getLong(_cursorIndexOfScannedAt);
            final Long _tmpSyncedAt;
            if (_cursor.isNull(_cursorIndexOfSyncedAt)) {
              _tmpSyncedAt = null;
            } else {
              _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            }
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final Long _tmpTrashedByExamId;
            if (_cursor.isNull(_cursorIndexOfTrashedByExamId)) {
              _tmpTrashedByExamId = null;
            } else {
              _tmpTrashedByExamId = _cursor.getLong(_cursorIndexOfTrashedByExamId);
            }
            _item = new ScanResult(_tmpId,_tmpStudentId,_tmpStudentName,_tmpStudentBlock,_tmpStudentYearLevel,_tmpStudentProgram,_tmpSubject,_tmpExamId,_tmpExamName,_tmpClusterId,_tmpClusterName,_tmpTestSet,_tmpRawAnswers,_tmpScore,_tmpTotal,_tmpPassed,_tmpScannedAt,_tmpSyncedAt,_tmpRemoteId,_tmpDeletedAt,_tmpTrashedByExamId);
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
  public Object getUnsynced(final Continuation<? super List<ScanResult>> $completion) {
    final String _sql = "SELECT * FROM scan_results WHERE syncedAt IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ScanResult>>() {
      @Override
      @NonNull
      public List<ScanResult> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "studentName");
          final int _cursorIndexOfStudentBlock = CursorUtil.getColumnIndexOrThrow(_cursor, "studentBlock");
          final int _cursorIndexOfStudentYearLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "studentYearLevel");
          final int _cursorIndexOfStudentProgram = CursorUtil.getColumnIndexOrThrow(_cursor, "studentProgram");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfClusterName = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterName");
          final int _cursorIndexOfTestSet = CursorUtil.getColumnIndexOrThrow(_cursor, "testSet");
          final int _cursorIndexOfRawAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "rawAnswers");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfScannedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scannedAt");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfTrashedByExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "trashedByExamId");
          final List<ScanResult> _result = new ArrayList<ScanResult>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScanResult _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentBlock;
            _tmpStudentBlock = _cursor.getString(_cursorIndexOfStudentBlock);
            final String _tmpStudentYearLevel;
            _tmpStudentYearLevel = _cursor.getString(_cursorIndexOfStudentYearLevel);
            final String _tmpStudentProgram;
            _tmpStudentProgram = _cursor.getString(_cursorIndexOfStudentProgram);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final long _tmpExamId;
            _tmpExamId = _cursor.getLong(_cursorIndexOfExamId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final Long _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getLong(_cursorIndexOfClusterId);
            }
            final String _tmpClusterName;
            if (_cursor.isNull(_cursorIndexOfClusterName)) {
              _tmpClusterName = null;
            } else {
              _tmpClusterName = _cursor.getString(_cursorIndexOfClusterName);
            }
            final String _tmpTestSet;
            _tmpTestSet = _cursor.getString(_cursorIndexOfTestSet);
            final String _tmpRawAnswers;
            _tmpRawAnswers = _cursor.getString(_cursorIndexOfRawAnswers);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final long _tmpScannedAt;
            _tmpScannedAt = _cursor.getLong(_cursorIndexOfScannedAt);
            final Long _tmpSyncedAt;
            if (_cursor.isNull(_cursorIndexOfSyncedAt)) {
              _tmpSyncedAt = null;
            } else {
              _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            }
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final Long _tmpTrashedByExamId;
            if (_cursor.isNull(_cursorIndexOfTrashedByExamId)) {
              _tmpTrashedByExamId = null;
            } else {
              _tmpTrashedByExamId = _cursor.getLong(_cursorIndexOfTrashedByExamId);
            }
            _item = new ScanResult(_tmpId,_tmpStudentId,_tmpStudentName,_tmpStudentBlock,_tmpStudentYearLevel,_tmpStudentProgram,_tmpSubject,_tmpExamId,_tmpExamName,_tmpClusterId,_tmpClusterName,_tmpTestSet,_tmpRawAnswers,_tmpScore,_tmpTotal,_tmpPassed,_tmpScannedAt,_tmpSyncedAt,_tmpRemoteId,_tmpDeletedAt,_tmpTrashedByExamId);
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
  public Object getByTrashedExamId(final long examId,
      final Continuation<? super List<ScanResult>> $completion) {
    final String _sql = "SELECT * FROM scan_results WHERE trashedByExamId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, examId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ScanResult>>() {
      @Override
      @NonNull
      public List<ScanResult> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "studentName");
          final int _cursorIndexOfStudentBlock = CursorUtil.getColumnIndexOrThrow(_cursor, "studentBlock");
          final int _cursorIndexOfStudentYearLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "studentYearLevel");
          final int _cursorIndexOfStudentProgram = CursorUtil.getColumnIndexOrThrow(_cursor, "studentProgram");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfClusterName = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterName");
          final int _cursorIndexOfTestSet = CursorUtil.getColumnIndexOrThrow(_cursor, "testSet");
          final int _cursorIndexOfRawAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "rawAnswers");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfScannedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scannedAt");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfTrashedByExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "trashedByExamId");
          final List<ScanResult> _result = new ArrayList<ScanResult>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScanResult _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentBlock;
            _tmpStudentBlock = _cursor.getString(_cursorIndexOfStudentBlock);
            final String _tmpStudentYearLevel;
            _tmpStudentYearLevel = _cursor.getString(_cursorIndexOfStudentYearLevel);
            final String _tmpStudentProgram;
            _tmpStudentProgram = _cursor.getString(_cursorIndexOfStudentProgram);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final long _tmpExamId;
            _tmpExamId = _cursor.getLong(_cursorIndexOfExamId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final Long _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getLong(_cursorIndexOfClusterId);
            }
            final String _tmpClusterName;
            if (_cursor.isNull(_cursorIndexOfClusterName)) {
              _tmpClusterName = null;
            } else {
              _tmpClusterName = _cursor.getString(_cursorIndexOfClusterName);
            }
            final String _tmpTestSet;
            _tmpTestSet = _cursor.getString(_cursorIndexOfTestSet);
            final String _tmpRawAnswers;
            _tmpRawAnswers = _cursor.getString(_cursorIndexOfRawAnswers);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final long _tmpScannedAt;
            _tmpScannedAt = _cursor.getLong(_cursorIndexOfScannedAt);
            final Long _tmpSyncedAt;
            if (_cursor.isNull(_cursorIndexOfSyncedAt)) {
              _tmpSyncedAt = null;
            } else {
              _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            }
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final Long _tmpTrashedByExamId;
            if (_cursor.isNull(_cursorIndexOfTrashedByExamId)) {
              _tmpTrashedByExamId = null;
            } else {
              _tmpTrashedByExamId = _cursor.getLong(_cursorIndexOfTrashedByExamId);
            }
            _item = new ScanResult(_tmpId,_tmpStudentId,_tmpStudentName,_tmpStudentBlock,_tmpStudentYearLevel,_tmpStudentProgram,_tmpSubject,_tmpExamId,_tmpExamName,_tmpClusterId,_tmpClusterName,_tmpTestSet,_tmpRawAnswers,_tmpScore,_tmpTotal,_tmpPassed,_tmpScannedAt,_tmpSyncedAt,_tmpRemoteId,_tmpDeletedAt,_tmpTrashedByExamId);
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
  public Object getExpired(final long threshold,
      final Continuation<? super List<ScanResult>> $completion) {
    final String _sql = "SELECT * FROM scan_results WHERE deletedAt IS NOT NULL AND deletedAt < ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, threshold);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ScanResult>>() {
      @Override
      @NonNull
      public List<ScanResult> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "studentName");
          final int _cursorIndexOfStudentBlock = CursorUtil.getColumnIndexOrThrow(_cursor, "studentBlock");
          final int _cursorIndexOfStudentYearLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "studentYearLevel");
          final int _cursorIndexOfStudentProgram = CursorUtil.getColumnIndexOrThrow(_cursor, "studentProgram");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfClusterName = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterName");
          final int _cursorIndexOfTestSet = CursorUtil.getColumnIndexOrThrow(_cursor, "testSet");
          final int _cursorIndexOfRawAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "rawAnswers");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfScannedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scannedAt");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfTrashedByExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "trashedByExamId");
          final List<ScanResult> _result = new ArrayList<ScanResult>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScanResult _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentBlock;
            _tmpStudentBlock = _cursor.getString(_cursorIndexOfStudentBlock);
            final String _tmpStudentYearLevel;
            _tmpStudentYearLevel = _cursor.getString(_cursorIndexOfStudentYearLevel);
            final String _tmpStudentProgram;
            _tmpStudentProgram = _cursor.getString(_cursorIndexOfStudentProgram);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final long _tmpExamId;
            _tmpExamId = _cursor.getLong(_cursorIndexOfExamId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final Long _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getLong(_cursorIndexOfClusterId);
            }
            final String _tmpClusterName;
            if (_cursor.isNull(_cursorIndexOfClusterName)) {
              _tmpClusterName = null;
            } else {
              _tmpClusterName = _cursor.getString(_cursorIndexOfClusterName);
            }
            final String _tmpTestSet;
            _tmpTestSet = _cursor.getString(_cursorIndexOfTestSet);
            final String _tmpRawAnswers;
            _tmpRawAnswers = _cursor.getString(_cursorIndexOfRawAnswers);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final long _tmpScannedAt;
            _tmpScannedAt = _cursor.getLong(_cursorIndexOfScannedAt);
            final Long _tmpSyncedAt;
            if (_cursor.isNull(_cursorIndexOfSyncedAt)) {
              _tmpSyncedAt = null;
            } else {
              _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            }
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final Long _tmpTrashedByExamId;
            if (_cursor.isNull(_cursorIndexOfTrashedByExamId)) {
              _tmpTrashedByExamId = null;
            } else {
              _tmpTrashedByExamId = _cursor.getLong(_cursorIndexOfTrashedByExamId);
            }
            _item = new ScanResult(_tmpId,_tmpStudentId,_tmpStudentName,_tmpStudentBlock,_tmpStudentYearLevel,_tmpStudentProgram,_tmpSubject,_tmpExamId,_tmpExamName,_tmpClusterId,_tmpClusterName,_tmpTestSet,_tmpRawAnswers,_tmpScore,_tmpTotal,_tmpPassed,_tmpScannedAt,_tmpSyncedAt,_tmpRemoteId,_tmpDeletedAt,_tmpTrashedByExamId);
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
  public Object getByIds(final List<Long> ids,
      final Continuation<? super List<ScanResult>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM scan_results WHERE id IN (");
    final int _inputSize = ids.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (long _item : ids) {
      _statement.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ScanResult>>() {
      @Override
      @NonNull
      public List<ScanResult> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "studentId");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "studentName");
          final int _cursorIndexOfStudentBlock = CursorUtil.getColumnIndexOrThrow(_cursor, "studentBlock");
          final int _cursorIndexOfStudentYearLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "studentYearLevel");
          final int _cursorIndexOfStudentProgram = CursorUtil.getColumnIndexOrThrow(_cursor, "studentProgram");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfExamName = CursorUtil.getColumnIndexOrThrow(_cursor, "examName");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfClusterName = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterName");
          final int _cursorIndexOfTestSet = CursorUtil.getColumnIndexOrThrow(_cursor, "testSet");
          final int _cursorIndexOfRawAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "rawAnswers");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfScannedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scannedAt");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfTrashedByExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "trashedByExamId");
          final List<ScanResult> _result = new ArrayList<ScanResult>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScanResult _item_1;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentBlock;
            _tmpStudentBlock = _cursor.getString(_cursorIndexOfStudentBlock);
            final String _tmpStudentYearLevel;
            _tmpStudentYearLevel = _cursor.getString(_cursorIndexOfStudentYearLevel);
            final String _tmpStudentProgram;
            _tmpStudentProgram = _cursor.getString(_cursorIndexOfStudentProgram);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final long _tmpExamId;
            _tmpExamId = _cursor.getLong(_cursorIndexOfExamId);
            final String _tmpExamName;
            _tmpExamName = _cursor.getString(_cursorIndexOfExamName);
            final Long _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getLong(_cursorIndexOfClusterId);
            }
            final String _tmpClusterName;
            if (_cursor.isNull(_cursorIndexOfClusterName)) {
              _tmpClusterName = null;
            } else {
              _tmpClusterName = _cursor.getString(_cursorIndexOfClusterName);
            }
            final String _tmpTestSet;
            _tmpTestSet = _cursor.getString(_cursorIndexOfTestSet);
            final String _tmpRawAnswers;
            _tmpRawAnswers = _cursor.getString(_cursorIndexOfRawAnswers);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final long _tmpScannedAt;
            _tmpScannedAt = _cursor.getLong(_cursorIndexOfScannedAt);
            final Long _tmpSyncedAt;
            if (_cursor.isNull(_cursorIndexOfSyncedAt)) {
              _tmpSyncedAt = null;
            } else {
              _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            }
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final Long _tmpTrashedByExamId;
            if (_cursor.isNull(_cursorIndexOfTrashedByExamId)) {
              _tmpTrashedByExamId = null;
            } else {
              _tmpTrashedByExamId = _cursor.getLong(_cursorIndexOfTrashedByExamId);
            }
            _item_1 = new ScanResult(_tmpId,_tmpStudentId,_tmpStudentName,_tmpStudentBlock,_tmpStudentYearLevel,_tmpStudentProgram,_tmpSubject,_tmpExamId,_tmpExamName,_tmpClusterId,_tmpClusterName,_tmpTestSet,_tmpRawAnswers,_tmpScore,_tmpTotal,_tmpPassed,_tmpScannedAt,_tmpSyncedAt,_tmpRemoteId,_tmpDeletedAt,_tmpTrashedByExamId);
            _result.add(_item_1);
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
  public Object countByExamId(final long examId, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM scan_results WHERE examId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, examId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object softDeleteByIds(final List<Long> ids, final long deletedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE scan_results SET deletedAt = ");
        _stringBuilder.append("?");
        _stringBuilder.append(", syncedAt = NULL WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(") AND deletedAt IS NULL");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deletedAt);
        _argIndex = 2;
        for (long _item : ids) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreByIds(final List<Long> ids, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE scan_results SET deletedAt = NULL, syncedAt = NULL WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (long _item : ids) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object hardDeleteByIds(final List<Long> ids,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM scan_results WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (long _item : ids) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
