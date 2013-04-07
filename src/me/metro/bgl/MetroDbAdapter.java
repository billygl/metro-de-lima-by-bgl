package me.metro.bgl;

import java.io.IOException;

import me.metro.bgl.model.dao.DataBaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MetroDbAdapter {

    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_CODIGO = "codigo";
    public static final String KEY_HORARIOS = "horarios";
    public static final String KEY_ID = "_id";
    public static final String DATABASE_TABLE = "estacion";
    
    
    private DataBaseHelper mDbHelper;
    private SQLiteDatabase mDb;    

    private final Context mCtx;

    public MetroDbAdapter(Context ctx) {   	
        this.mCtx = ctx;    
		
    }

    public MetroDbAdapter open() throws SQLException {
    	mDbHelper = new DataBaseHelper(mCtx);
    	try {
    		mDbHelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}
		try {
			mDbHelper.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}        
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    public Cursor fetchAllEstaciones() {    	
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_CODIGO,
                KEY_NOMBRE}, null, null, null, null, null);
    }


	public Cursor fetchHorarios(long estacion,String top) {
		String sEstacion = String.valueOf(estacion);
		String sql = "select _id,'    ' " +
				" || hora_a_gr || '          -          '" +
				" || hora_a_ves horarios " +
				"from horario " +
				"where idestacion = '"+sEstacion + "'";
		String filtro =	" and " +
				"(time(hora_a_gr) > time('now','localtime') or " +
				"time(hora_a_ves) > time('now','localtime') ) " +
				"limit "+top;
		if(!top.equals("todos"))sql+=filtro;
		return mDb.rawQuery(sql, null);
	}
}
