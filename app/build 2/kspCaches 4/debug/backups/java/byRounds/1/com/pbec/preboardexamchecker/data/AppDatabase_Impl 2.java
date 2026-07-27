package com.pbec.preboardexamchecker.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.pbec.preboardexamchecker.data.dao.ExamDao;
import com.pbec.preboardexamchecker.data.dao.ExamDao_Impl;
import com.pbec.preboardexamchecker.data.dao.QuestionDao;
import com.pbec.preboardexamchecker.data.dao.QuestionDao_Impl;
import com.pbec.preboardexamchecker.data.dao.ScanResultDao;
import com.pbec.preboardexamchecker.data.dao.ScanResultDao_Impl;
import com.pbec.preboardexamchecker.data.dao.TransactionLogDao;
import com.pbec.preboardexamchecker.data.dao.TransactionLogDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile QuestionDao _questionDao;

  private volatile ExamDao _examDao;

  private volatile TransactionLogDao _transactionLogDao;

  private volatile ScanResultDao _scanResultDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(11) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `questions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `subject` TEXT NOT NULL, `fileName` TEXT NOT NULL, `category` TEXT, `topic` TEXT, `questionNumber` INTEGER NOT NULL, `questionText` TEXT NOT NULL, `optionA` TEXT NOT NULL, `optionB` TEXT NOT NULL, `optionC` TEXT NOT NULL, `optionD` TEXT NOT NULL, `correctAnswer` TEXT, `questionBankId` TEXT NOT NULL, `importSessionId` INTEGER NOT NULL, `customSessionName` TEXT)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_questions_subject_fileName` ON `questions` (`subject`, `fileName`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_questions_importSessionId` ON `questions` (`importSessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `exams` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `examName` TEXT NOT NULL, `subject` TEXT NOT NULL, `setAQuestionIds` TEXT NOT NULL, `setBQuestionIds` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `transaction_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `action` TEXT NOT NULL, `subject` TEXT, `details` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `scan_results` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `studentId` TEXT NOT NULL, `studentName` TEXT NOT NULL, `studentBlock` TEXT NOT NULL, `studentYearLevel` TEXT NOT NULL, `studentProgram` TEXT NOT NULL, `subject` TEXT NOT NULL, `examId` INTEGER NOT NULL, `examName` TEXT NOT NULL, `clusterId` INTEGER, `clusterName` TEXT, `testSet` TEXT NOT NULL, `rawAnswers` TEXT NOT NULL, `score` INTEGER NOT NULL, `total` INTEGER NOT NULL, `passed` INTEGER NOT NULL, `scannedAt` INTEGER NOT NULL, `syncedAt` INTEGER, `remoteId` TEXT, `deletedAt` INTEGER, `trashedByExamId` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8baeb42344e20c80c1c1dca1eb4dbeb4')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `questions`");
        db.execSQL("DROP TABLE IF EXISTS `exams`");
        db.execSQL("DROP TABLE IF EXISTS `transaction_logs`");
        db.execSQL("DROP TABLE IF EXISTS `scan_results`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsQuestions = new HashMap<String, TableInfo.Column>(15);
        _columnsQuestions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("subject", new TableInfo.Column("subject", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("fileName", new TableInfo.Column("fileName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("topic", new TableInfo.Column("topic", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("questionNumber", new TableInfo.Column("questionNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("questionText", new TableInfo.Column("questionText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("optionA", new TableInfo.Column("optionA", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("optionB", new TableInfo.Column("optionB", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("optionC", new TableInfo.Column("optionC", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("optionD", new TableInfo.Column("optionD", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("correctAnswer", new TableInfo.Column("correctAnswer", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("questionBankId", new TableInfo.Column("questionBankId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("importSessionId", new TableInfo.Column("importSessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("customSessionName", new TableInfo.Column("customSessionName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuestions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesQuestions = new HashSet<TableInfo.Index>(2);
        _indicesQuestions.add(new TableInfo.Index("index_questions_subject_fileName", false, Arrays.asList("subject", "fileName"), Arrays.asList("ASC", "ASC")));
        _indicesQuestions.add(new TableInfo.Index("index_questions_importSessionId", false, Arrays.asList("importSessionId"), Arrays.asList("ASC")));
        final TableInfo _infoQuestions = new TableInfo("questions", _columnsQuestions, _foreignKeysQuestions, _indicesQuestions);
        final TableInfo _existingQuestions = TableInfo.read(db, "questions");
        if (!_infoQuestions.equals(_existingQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "questions(com.pbec.preboardexamchecker.data.models.Question).\n"
                  + " Expected:\n" + _infoQuestions + "\n"
                  + " Found:\n" + _existingQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsExams = new HashMap<String, TableInfo.Column>(6);
        _columnsExams.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExams.put("examName", new TableInfo.Column("examName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExams.put("subject", new TableInfo.Column("subject", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExams.put("setAQuestionIds", new TableInfo.Column("setAQuestionIds", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExams.put("setBQuestionIds", new TableInfo.Column("setBQuestionIds", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExams.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExams = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExams = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExams = new TableInfo("exams", _columnsExams, _foreignKeysExams, _indicesExams);
        final TableInfo _existingExams = TableInfo.read(db, "exams");
        if (!_infoExams.equals(_existingExams)) {
          return new RoomOpenHelper.ValidationResult(false, "exams(com.pbec.preboardexamchecker.data.models.Exam).\n"
                  + " Expected:\n" + _infoExams + "\n"
                  + " Found:\n" + _existingExams);
        }
        final HashMap<String, TableInfo.Column> _columnsTransactionLogs = new HashMap<String, TableInfo.Column>(5);
        _columnsTransactionLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactionLogs.put("action", new TableInfo.Column("action", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactionLogs.put("subject", new TableInfo.Column("subject", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactionLogs.put("details", new TableInfo.Column("details", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactionLogs.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactionLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTransactionLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTransactionLogs = new TableInfo("transaction_logs", _columnsTransactionLogs, _foreignKeysTransactionLogs, _indicesTransactionLogs);
        final TableInfo _existingTransactionLogs = TableInfo.read(db, "transaction_logs");
        if (!_infoTransactionLogs.equals(_existingTransactionLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "transaction_logs(com.pbec.preboardexamchecker.data.models.TransactionLog).\n"
                  + " Expected:\n" + _infoTransactionLogs + "\n"
                  + " Found:\n" + _existingTransactionLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsScanResults = new HashMap<String, TableInfo.Column>(21);
        _columnsScanResults.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("studentId", new TableInfo.Column("studentId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("studentName", new TableInfo.Column("studentName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("studentBlock", new TableInfo.Column("studentBlock", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("studentYearLevel", new TableInfo.Column("studentYearLevel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("studentProgram", new TableInfo.Column("studentProgram", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("subject", new TableInfo.Column("subject", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("examId", new TableInfo.Column("examId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("examName", new TableInfo.Column("examName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("clusterId", new TableInfo.Column("clusterId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("clusterName", new TableInfo.Column("clusterName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("testSet", new TableInfo.Column("testSet", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("rawAnswers", new TableInfo.Column("rawAnswers", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("score", new TableInfo.Column("score", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("total", new TableInfo.Column("total", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("passed", new TableInfo.Column("passed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("scannedAt", new TableInfo.Column("scannedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("syncedAt", new TableInfo.Column("syncedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("remoteId", new TableInfo.Column("remoteId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("deletedAt", new TableInfo.Column("deletedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("trashedByExamId", new TableInfo.Column("trashedByExamId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScanResults = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesScanResults = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScanResults = new TableInfo("scan_results", _columnsScanResults, _foreignKeysScanResults, _indicesScanResults);
        final TableInfo _existingScanResults = TableInfo.read(db, "scan_results");
        if (!_infoScanResults.equals(_existingScanResults)) {
          return new RoomOpenHelper.ValidationResult(false, "scan_results(com.pbec.preboardexamchecker.data.models.ScanResult).\n"
                  + " Expected:\n" + _infoScanResults + "\n"
                  + " Found:\n" + _existingScanResults);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "8baeb42344e20c80c1c1dca1eb4dbeb4", "7986e4e0ff869cdb3e243cae9ba84159");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "questions","exams","transaction_logs","scan_results");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `questions`");
      _db.execSQL("DELETE FROM `exams`");
      _db.execSQL("DELETE FROM `transaction_logs`");
      _db.execSQL("DELETE FROM `scan_results`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(QuestionDao.class, QuestionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExamDao.class, ExamDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TransactionLogDao.class, TransactionLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ScanResultDao.class, ScanResultDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public QuestionDao questionDao() {
    if (_questionDao != null) {
      return _questionDao;
    } else {
      synchronized(this) {
        if(_questionDao == null) {
          _questionDao = new QuestionDao_Impl(this);
        }
        return _questionDao;
      }
    }
  }

  @Override
  public ExamDao examDao() {
    if (_examDao != null) {
      return _examDao;
    } else {
      synchronized(this) {
        if(_examDao == null) {
          _examDao = new ExamDao_Impl(this);
        }
        return _examDao;
      }
    }
  }

  @Override
  public TransactionLogDao transactionLogDao() {
    if (_transactionLogDao != null) {
      return _transactionLogDao;
    } else {
      synchronized(this) {
        if(_transactionLogDao == null) {
          _transactionLogDao = new TransactionLogDao_Impl(this);
        }
        return _transactionLogDao;
      }
    }
  }

  @Override
  public ScanResultDao scanResultDao() {
    if (_scanResultDao != null) {
      return _scanResultDao;
    } else {
      synchronized(this) {
        if(_scanResultDao == null) {
          _scanResultDao = new ScanResultDao_Impl(this);
        }
        return _scanResultDao;
      }
    }
  }
}
