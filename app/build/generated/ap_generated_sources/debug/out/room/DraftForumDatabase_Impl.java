package room;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unchecked")
public final class DraftForumDatabase_Impl extends DraftForumDatabase {
  private volatile DraftForumDao _draftForumDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `DraftForum` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `header` TEXT, `aboutList` TEXT, `contentList` TEXT, `timeStamp` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"263cf4825ad74dc4343deee64c3156a0\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `DraftForum`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsDraftForum = new HashMap<String, TableInfo.Column>(5);
        _columnsDraftForum.put("id", new TableInfo.Column("id", "INTEGER", true, 1));
        _columnsDraftForum.put("header", new TableInfo.Column("header", "TEXT", false, 0));
        _columnsDraftForum.put("aboutList", new TableInfo.Column("aboutList", "TEXT", false, 0));
        _columnsDraftForum.put("contentList", new TableInfo.Column("contentList", "TEXT", false, 0));
        _columnsDraftForum.put("timeStamp", new TableInfo.Column("timeStamp", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDraftForum = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDraftForum = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDraftForum = new TableInfo("DraftForum", _columnsDraftForum, _foreignKeysDraftForum, _indicesDraftForum);
        final TableInfo _existingDraftForum = TableInfo.read(_db, "DraftForum");
        if (! _infoDraftForum.equals(_existingDraftForum)) {
          throw new IllegalStateException("Migration didn't properly handle DraftForum(models.DraftForum).\n"
                  + " Expected:\n" + _infoDraftForum + "\n"
                  + " Found:\n" + _existingDraftForum);
        }
      }
    }, "263cf4825ad74dc4343deee64c3156a0", "7ce8499f28e3aae19908047d8f23d264");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "DraftForum");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `DraftForum`");
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
  public DraftForumDao forumDao() {
    if (_draftForumDao != null) {
      return _draftForumDao;
    } else {
      synchronized(this) {
        if(_draftForumDao == null) {
          _draftForumDao = new DraftForumDao_Impl(this);
        }
        return _draftForumDao;
      }
    }
  }
}
