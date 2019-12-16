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
import models.Tag;

@SuppressWarnings("unchecked")
public final class CategoryDao_Impl implements CategoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfTag;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfTag;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public CategoryDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTag = new EntityInsertionAdapter<Tag>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `Tag`(`about`,`count`) VALUES (?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Tag value) {
        if (value.getAbout() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getAbout());
        }
        stmt.bindLong(2, value.getCount());
      }
    };
    this.__deletionAdapterOfTag = new EntityDeletionOrUpdateAdapter<Tag>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `Tag` WHERE `about` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Tag value) {
        if (value.getAbout() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getAbout());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM Tag";
        return _query;
      }
    };
  }

  @Override
  public void addAll(Tag... categories) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfTag.insert(categories);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(Tag tag) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfTag.handle(tag);
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
  public List<Tag> getAll() {
    final String _sql = "SELECT * FROM Tag";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfAbout = _cursor.getColumnIndexOrThrow("about");
      final int _cursorIndexOfCount = _cursor.getColumnIndexOrThrow("count");
      final List<Tag> _result = new ArrayList<Tag>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Tag _item;
        _item = new Tag();
        final String _tmpAbout;
        _tmpAbout = _cursor.getString(_cursorIndexOfAbout);
        _item.setAbout(_tmpAbout);
        final long _tmpCount;
        _tmpCount = _cursor.getLong(_cursorIndexOfCount);
        _item.setCount(_tmpCount);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Tag getCategory(String string) {
    final String _sql = "SELECT * FROM Tag WHERE about = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (string == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, string);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfAbout = _cursor.getColumnIndexOrThrow("about");
      final int _cursorIndexOfCount = _cursor.getColumnIndexOrThrow("count");
      final Tag _result;
      if(_cursor.moveToFirst()) {
        _result = new Tag();
        final String _tmpAbout;
        _tmpAbout = _cursor.getString(_cursorIndexOfAbout);
        _result.setAbout(_tmpAbout);
        final long _tmpCount;
        _tmpCount = _cursor.getLong(_cursorIndexOfCount);
        _result.setCount(_tmpCount);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Tag> getCategories(String about) {
    final String _sql = "SELECT * FROM Tag WHERE about LIKE ? LIMIT 5";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (about == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, about);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfAbout = _cursor.getColumnIndexOrThrow("about");
      final int _cursorIndexOfCount = _cursor.getColumnIndexOrThrow("count");
      final List<Tag> _result = new ArrayList<Tag>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Tag _item;
        _item = new Tag();
        final String _tmpAbout;
        _tmpAbout = _cursor.getString(_cursorIndexOfAbout);
        _item.setAbout(_tmpAbout);
        final long _tmpCount;
        _tmpCount = _cursor.getLong(_cursorIndexOfCount);
        _item.setCount(_tmpCount);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
