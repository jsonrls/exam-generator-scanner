package com.pbec.preboardexamchecker.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pbec.preboardexamchecker.data.models.Question;
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
public final class QuestionDao_Impl implements QuestionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Question> __insertionAdapterOfQuestion;

  private final EntityDeletionOrUpdateAdapter<Question> __deletionAdapterOfQuestion;

  private final SharedSQLiteStatement __preparedStmtOfDeleteQuestionsByImportSessionId;

  private final SharedSQLiteStatement __preparedStmtOfUpdateCustomSessionName;

  public QuestionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQuestion = new EntityInsertionAdapter<Question>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `questions` (`id`,`subject`,`fileName`,`category`,`topic`,`questionNumber`,`questionText`,`optionA`,`optionB`,`optionC`,`optionD`,`correctAnswer`,`questionBankId`,`importSessionId`,`customSessionName`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getSubject());
        statement.bindString(3, entity.getFileName());
        if (entity.getCategory() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCategory());
        }
        if (entity.getTopic() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getTopic());
        }
        statement.bindLong(6, entity.getQuestionNumber());
        statement.bindString(7, entity.getQuestionText());
        statement.bindString(8, entity.getOptionA());
        statement.bindString(9, entity.getOptionB());
        statement.bindString(10, entity.getOptionC());
        statement.bindString(11, entity.getOptionD());
        if (entity.getCorrectAnswer() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getCorrectAnswer());
        }
        statement.bindString(13, entity.getQuestionBankId());
        statement.bindLong(14, entity.getImportSessionId());
        if (entity.getCustomSessionName() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getCustomSessionName());
        }
      }
    };
    this.__deletionAdapterOfQuestion = new EntityDeletionOrUpdateAdapter<Question>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `questions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteQuestionsByImportSessionId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM questions WHERE importSessionId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateCustomSessionName = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE questions SET customSessionName = ? WHERE importSessionId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertQuestions(final List<Question> questions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQuestion.insert(questions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteQuestion(final Question question,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        int _total = 0;
        __db.beginTransaction();
        try {
          _total += __deletionAdapterOfQuestion.handle(question);
          __db.setTransactionSuccessful();
          return _total;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteQuestionsByImportSessionId(final long importSessionId,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteQuestionsByImportSessionId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, importSessionId);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteQuestionsByImportSessionId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCustomSessionName(final long importSessionId, final String newName,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateCustomSessionName.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, newName);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, importSessionId);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateCustomSessionName.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Question>> getQuestionsBySubject(final String subject) {
    final String _sql = "SELECT * FROM questions WHERE subject = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, subject);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"questions"}, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfQuestionNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "questionNumber");
          final int _cursorIndexOfQuestionText = CursorUtil.getColumnIndexOrThrow(_cursor, "questionText");
          final int _cursorIndexOfOptionA = CursorUtil.getColumnIndexOrThrow(_cursor, "optionA");
          final int _cursorIndexOfOptionB = CursorUtil.getColumnIndexOrThrow(_cursor, "optionB");
          final int _cursorIndexOfOptionC = CursorUtil.getColumnIndexOrThrow(_cursor, "optionC");
          final int _cursorIndexOfOptionD = CursorUtil.getColumnIndexOrThrow(_cursor, "optionD");
          final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
          final int _cursorIndexOfQuestionBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "questionBankId");
          final int _cursorIndexOfImportSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "importSessionId");
          final int _cursorIndexOfCustomSessionName = CursorUtil.getColumnIndexOrThrow(_cursor, "customSessionName");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpTopic;
            if (_cursor.isNull(_cursorIndexOfTopic)) {
              _tmpTopic = null;
            } else {
              _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
            }
            final int _tmpQuestionNumber;
            _tmpQuestionNumber = _cursor.getInt(_cursorIndexOfQuestionNumber);
            final String _tmpQuestionText;
            _tmpQuestionText = _cursor.getString(_cursorIndexOfQuestionText);
            final String _tmpOptionA;
            _tmpOptionA = _cursor.getString(_cursorIndexOfOptionA);
            final String _tmpOptionB;
            _tmpOptionB = _cursor.getString(_cursorIndexOfOptionB);
            final String _tmpOptionC;
            _tmpOptionC = _cursor.getString(_cursorIndexOfOptionC);
            final String _tmpOptionD;
            _tmpOptionD = _cursor.getString(_cursorIndexOfOptionD);
            final String _tmpCorrectAnswer;
            if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
              _tmpCorrectAnswer = null;
            } else {
              _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
            }
            final String _tmpQuestionBankId;
            _tmpQuestionBankId = _cursor.getString(_cursorIndexOfQuestionBankId);
            final long _tmpImportSessionId;
            _tmpImportSessionId = _cursor.getLong(_cursorIndexOfImportSessionId);
            final String _tmpCustomSessionName;
            if (_cursor.isNull(_cursorIndexOfCustomSessionName)) {
              _tmpCustomSessionName = null;
            } else {
              _tmpCustomSessionName = _cursor.getString(_cursorIndexOfCustomSessionName);
            }
            _item = new Question(_tmpId,_tmpSubject,_tmpFileName,_tmpCategory,_tmpTopic,_tmpQuestionNumber,_tmpQuestionText,_tmpOptionA,_tmpOptionB,_tmpOptionC,_tmpOptionD,_tmpCorrectAnswer,_tmpQuestionBankId,_tmpImportSessionId,_tmpCustomSessionName);
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
  public Flow<List<Question>> getAllQuestionsFlow() {
    final String _sql = "SELECT * FROM questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"questions"}, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfQuestionNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "questionNumber");
          final int _cursorIndexOfQuestionText = CursorUtil.getColumnIndexOrThrow(_cursor, "questionText");
          final int _cursorIndexOfOptionA = CursorUtil.getColumnIndexOrThrow(_cursor, "optionA");
          final int _cursorIndexOfOptionB = CursorUtil.getColumnIndexOrThrow(_cursor, "optionB");
          final int _cursorIndexOfOptionC = CursorUtil.getColumnIndexOrThrow(_cursor, "optionC");
          final int _cursorIndexOfOptionD = CursorUtil.getColumnIndexOrThrow(_cursor, "optionD");
          final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
          final int _cursorIndexOfQuestionBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "questionBankId");
          final int _cursorIndexOfImportSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "importSessionId");
          final int _cursorIndexOfCustomSessionName = CursorUtil.getColumnIndexOrThrow(_cursor, "customSessionName");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpTopic;
            if (_cursor.isNull(_cursorIndexOfTopic)) {
              _tmpTopic = null;
            } else {
              _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
            }
            final int _tmpQuestionNumber;
            _tmpQuestionNumber = _cursor.getInt(_cursorIndexOfQuestionNumber);
            final String _tmpQuestionText;
            _tmpQuestionText = _cursor.getString(_cursorIndexOfQuestionText);
            final String _tmpOptionA;
            _tmpOptionA = _cursor.getString(_cursorIndexOfOptionA);
            final String _tmpOptionB;
            _tmpOptionB = _cursor.getString(_cursorIndexOfOptionB);
            final String _tmpOptionC;
            _tmpOptionC = _cursor.getString(_cursorIndexOfOptionC);
            final String _tmpOptionD;
            _tmpOptionD = _cursor.getString(_cursorIndexOfOptionD);
            final String _tmpCorrectAnswer;
            if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
              _tmpCorrectAnswer = null;
            } else {
              _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
            }
            final String _tmpQuestionBankId;
            _tmpQuestionBankId = _cursor.getString(_cursorIndexOfQuestionBankId);
            final long _tmpImportSessionId;
            _tmpImportSessionId = _cursor.getLong(_cursorIndexOfImportSessionId);
            final String _tmpCustomSessionName;
            if (_cursor.isNull(_cursorIndexOfCustomSessionName)) {
              _tmpCustomSessionName = null;
            } else {
              _tmpCustomSessionName = _cursor.getString(_cursorIndexOfCustomSessionName);
            }
            _item = new Question(_tmpId,_tmpSubject,_tmpFileName,_tmpCategory,_tmpTopic,_tmpQuestionNumber,_tmpQuestionText,_tmpOptionA,_tmpOptionB,_tmpOptionC,_tmpOptionD,_tmpCorrectAnswer,_tmpQuestionBankId,_tmpImportSessionId,_tmpCustomSessionName);
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
  public Object getAllQuestionsForSubject(final String subject,
      final Continuation<? super List<Question>> $completion) {
    final String _sql = "SELECT * FROM questions WHERE subject = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, subject);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfQuestionNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "questionNumber");
          final int _cursorIndexOfQuestionText = CursorUtil.getColumnIndexOrThrow(_cursor, "questionText");
          final int _cursorIndexOfOptionA = CursorUtil.getColumnIndexOrThrow(_cursor, "optionA");
          final int _cursorIndexOfOptionB = CursorUtil.getColumnIndexOrThrow(_cursor, "optionB");
          final int _cursorIndexOfOptionC = CursorUtil.getColumnIndexOrThrow(_cursor, "optionC");
          final int _cursorIndexOfOptionD = CursorUtil.getColumnIndexOrThrow(_cursor, "optionD");
          final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
          final int _cursorIndexOfQuestionBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "questionBankId");
          final int _cursorIndexOfImportSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "importSessionId");
          final int _cursorIndexOfCustomSessionName = CursorUtil.getColumnIndexOrThrow(_cursor, "customSessionName");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpTopic;
            if (_cursor.isNull(_cursorIndexOfTopic)) {
              _tmpTopic = null;
            } else {
              _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
            }
            final int _tmpQuestionNumber;
            _tmpQuestionNumber = _cursor.getInt(_cursorIndexOfQuestionNumber);
            final String _tmpQuestionText;
            _tmpQuestionText = _cursor.getString(_cursorIndexOfQuestionText);
            final String _tmpOptionA;
            _tmpOptionA = _cursor.getString(_cursorIndexOfOptionA);
            final String _tmpOptionB;
            _tmpOptionB = _cursor.getString(_cursorIndexOfOptionB);
            final String _tmpOptionC;
            _tmpOptionC = _cursor.getString(_cursorIndexOfOptionC);
            final String _tmpOptionD;
            _tmpOptionD = _cursor.getString(_cursorIndexOfOptionD);
            final String _tmpCorrectAnswer;
            if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
              _tmpCorrectAnswer = null;
            } else {
              _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
            }
            final String _tmpQuestionBankId;
            _tmpQuestionBankId = _cursor.getString(_cursorIndexOfQuestionBankId);
            final long _tmpImportSessionId;
            _tmpImportSessionId = _cursor.getLong(_cursorIndexOfImportSessionId);
            final String _tmpCustomSessionName;
            if (_cursor.isNull(_cursorIndexOfCustomSessionName)) {
              _tmpCustomSessionName = null;
            } else {
              _tmpCustomSessionName = _cursor.getString(_cursorIndexOfCustomSessionName);
            }
            _item = new Question(_tmpId,_tmpSubject,_tmpFileName,_tmpCategory,_tmpTopic,_tmpQuestionNumber,_tmpQuestionText,_tmpOptionA,_tmpOptionB,_tmpOptionC,_tmpOptionD,_tmpCorrectAnswer,_tmpQuestionBankId,_tmpImportSessionId,_tmpCustomSessionName);
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
  public Object getQuestionsByImportSessionIds(final String subject,
      final List<Long> importSessionIds, final Continuation<? super List<Question>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM questions WHERE subject = ");
    _stringBuilder.append("?");
    _stringBuilder.append(" AND importSessionId IN (");
    final int _inputSize = importSessionIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 1 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    _statement.bindString(_argIndex, subject);
    _argIndex = 2;
    for (long _item : importSessionIds) {
      _statement.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfQuestionNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "questionNumber");
          final int _cursorIndexOfQuestionText = CursorUtil.getColumnIndexOrThrow(_cursor, "questionText");
          final int _cursorIndexOfOptionA = CursorUtil.getColumnIndexOrThrow(_cursor, "optionA");
          final int _cursorIndexOfOptionB = CursorUtil.getColumnIndexOrThrow(_cursor, "optionB");
          final int _cursorIndexOfOptionC = CursorUtil.getColumnIndexOrThrow(_cursor, "optionC");
          final int _cursorIndexOfOptionD = CursorUtil.getColumnIndexOrThrow(_cursor, "optionD");
          final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
          final int _cursorIndexOfQuestionBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "questionBankId");
          final int _cursorIndexOfImportSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "importSessionId");
          final int _cursorIndexOfCustomSessionName = CursorUtil.getColumnIndexOrThrow(_cursor, "customSessionName");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item_1;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpTopic;
            if (_cursor.isNull(_cursorIndexOfTopic)) {
              _tmpTopic = null;
            } else {
              _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
            }
            final int _tmpQuestionNumber;
            _tmpQuestionNumber = _cursor.getInt(_cursorIndexOfQuestionNumber);
            final String _tmpQuestionText;
            _tmpQuestionText = _cursor.getString(_cursorIndexOfQuestionText);
            final String _tmpOptionA;
            _tmpOptionA = _cursor.getString(_cursorIndexOfOptionA);
            final String _tmpOptionB;
            _tmpOptionB = _cursor.getString(_cursorIndexOfOptionB);
            final String _tmpOptionC;
            _tmpOptionC = _cursor.getString(_cursorIndexOfOptionC);
            final String _tmpOptionD;
            _tmpOptionD = _cursor.getString(_cursorIndexOfOptionD);
            final String _tmpCorrectAnswer;
            if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
              _tmpCorrectAnswer = null;
            } else {
              _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
            }
            final String _tmpQuestionBankId;
            _tmpQuestionBankId = _cursor.getString(_cursorIndexOfQuestionBankId);
            final long _tmpImportSessionId;
            _tmpImportSessionId = _cursor.getLong(_cursorIndexOfImportSessionId);
            final String _tmpCustomSessionName;
            if (_cursor.isNull(_cursorIndexOfCustomSessionName)) {
              _tmpCustomSessionName = null;
            } else {
              _tmpCustomSessionName = _cursor.getString(_cursorIndexOfCustomSessionName);
            }
            _item_1 = new Question(_tmpId,_tmpSubject,_tmpFileName,_tmpCategory,_tmpTopic,_tmpQuestionNumber,_tmpQuestionText,_tmpOptionA,_tmpOptionB,_tmpOptionC,_tmpOptionD,_tmpCorrectAnswer,_tmpQuestionBankId,_tmpImportSessionId,_tmpCustomSessionName);
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
  public Object getQuestionsByImportSessionIdsOnly(final List<Long> importSessionIds,
      final Continuation<? super List<Question>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM questions WHERE importSessionId IN (");
    final int _inputSize = importSessionIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (long _item : importSessionIds) {
      _statement.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfQuestionNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "questionNumber");
          final int _cursorIndexOfQuestionText = CursorUtil.getColumnIndexOrThrow(_cursor, "questionText");
          final int _cursorIndexOfOptionA = CursorUtil.getColumnIndexOrThrow(_cursor, "optionA");
          final int _cursorIndexOfOptionB = CursorUtil.getColumnIndexOrThrow(_cursor, "optionB");
          final int _cursorIndexOfOptionC = CursorUtil.getColumnIndexOrThrow(_cursor, "optionC");
          final int _cursorIndexOfOptionD = CursorUtil.getColumnIndexOrThrow(_cursor, "optionD");
          final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
          final int _cursorIndexOfQuestionBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "questionBankId");
          final int _cursorIndexOfImportSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "importSessionId");
          final int _cursorIndexOfCustomSessionName = CursorUtil.getColumnIndexOrThrow(_cursor, "customSessionName");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item_1;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpTopic;
            if (_cursor.isNull(_cursorIndexOfTopic)) {
              _tmpTopic = null;
            } else {
              _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
            }
            final int _tmpQuestionNumber;
            _tmpQuestionNumber = _cursor.getInt(_cursorIndexOfQuestionNumber);
            final String _tmpQuestionText;
            _tmpQuestionText = _cursor.getString(_cursorIndexOfQuestionText);
            final String _tmpOptionA;
            _tmpOptionA = _cursor.getString(_cursorIndexOfOptionA);
            final String _tmpOptionB;
            _tmpOptionB = _cursor.getString(_cursorIndexOfOptionB);
            final String _tmpOptionC;
            _tmpOptionC = _cursor.getString(_cursorIndexOfOptionC);
            final String _tmpOptionD;
            _tmpOptionD = _cursor.getString(_cursorIndexOfOptionD);
            final String _tmpCorrectAnswer;
            if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
              _tmpCorrectAnswer = null;
            } else {
              _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
            }
            final String _tmpQuestionBankId;
            _tmpQuestionBankId = _cursor.getString(_cursorIndexOfQuestionBankId);
            final long _tmpImportSessionId;
            _tmpImportSessionId = _cursor.getLong(_cursorIndexOfImportSessionId);
            final String _tmpCustomSessionName;
            if (_cursor.isNull(_cursorIndexOfCustomSessionName)) {
              _tmpCustomSessionName = null;
            } else {
              _tmpCustomSessionName = _cursor.getString(_cursorIndexOfCustomSessionName);
            }
            _item_1 = new Question(_tmpId,_tmpSubject,_tmpFileName,_tmpCategory,_tmpTopic,_tmpQuestionNumber,_tmpQuestionText,_tmpOptionA,_tmpOptionB,_tmpOptionC,_tmpOptionD,_tmpCorrectAnswer,_tmpQuestionBankId,_tmpImportSessionId,_tmpCustomSessionName);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
