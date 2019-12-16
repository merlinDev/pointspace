package room;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import models.DraftForum;

@SuppressWarnings("unchecked")
public final class DraftForumDao_Impl implements DraftForumDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfDraftForum;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfDraftForum;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public DraftForumDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDraftForum = new EntityInsertionAdapter<DraftForum>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `DraftForum`(`id`,`header`,`aboutList`,`contentList`,`timeStamp`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DraftForum value) {
        stmt.bindLong(1, value.getId());
        if (value.getHeader() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getHeader());
        }
        if (value.getAboutList() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getAboutList());
        }
        if (value.getContentList() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getContentList());
        }
        stmt.bindLong(5, value.getTimeStamp());
      }
    };
    this.__deletionAdapterOfDraftForum = new EntityDeletionOrUpdateAdapter<DraftForum>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `DraftForum` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DraftForum value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM draftforum";
        return _query;
      }
    };
  }

  @Override
  public void saveDraftForum(DraftForum draftForum) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfDraftForum.insert(draftForum);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(DraftForum draftForum) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfDraftForum.handle(draftForum);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public List<DraftForum> getAll() {
    final String _sql = "SELECT * FROM draftforum";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfHeader = _cursor.getColumnIndexOrThrow("header");
      final int _cursorIndexOfAboutList = _cursor.getColumnIndexOrThrow("aboutList");
      final int _cursorIndexOfContentList = _cursor.getColumnIndexOrThrow("contentList");
      final int _cursorIndexOfTimeStamp = _cursor.getColumnIndexOrThrow("timeStamp");
      final List<DraftForum> _result = new ArrayList<DraftForum>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DraftForum _item;
        _item = new DraftForum();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpHeader;
        _tmpHeader = _cursor.getString(_cursorIndexOfHeader);
        _item.setHeader(_tmpHeader);
        final String _tmpAboutList;
        _tmpAboutList = _cursor.getString(_cursorIndexOfAboutList);
        _item.setAboutList(_tmpAboutList);
        final String _tmpContentList;
        _tmpContentList = _cursor.getString(_cursorIndexOfContentList);
        _item.setContentList(_tmpContentList);
        final long _tmpTimeStamp;
        _tmpTimeStamp = _cursor.getLong(_cursorIndexOfTimeStamp);
        _item.setTimeStamp(_tmpTimeStamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public DraftForum getDraftForum(int id) {
    final String _sql = "SELECT * FROM DraftForum WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfHeader = _cursor.getColumnIndexOrThrow("header");
      final int _cursorIndexOfAboutList = _cursor.getColumnIndexOrThrow("aboutList");
      final int _cursorIndexOfContentList = _cursor.getColumnIndexOrThrow("contentList");
      final int _cursorIndexOfTimeStamp = _cursor.getColumnIndexOrThrow("timeStamp");
      final DraftForum _result;
      if(_cursor.moveToFirst()) {
        _result = new DraftForum();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final String _tmpHeader;
        _tmpHeader = _cursor.getString(_cursorIndexOfHeader);
        _result.setHeader(_tmpHeader);
        final String _tmpAboutList;
        _tmpAboutList = _cursor.getString(_cursorIndexOfAboutList);
        _result.setAboutList(_tmpAboutList);
        final String _tmpContentList;
        _tmpContentList = _cursor.getString(_cursorIndexOfContentList);
        _result.setContentList(_tmpContentList);
        final long _tmpTimeStamp;
        _tmpTimeStamp = _cursor.getLong(_cursorIndexOfTimeStamp);
        _result.setTimeStamp(_tmpTimeStamp);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
