package me.metro.bgl.model.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.metro.bgl.model.beans.EstacionBean;
import me.metro.bgl.model.beans.HorarioBean;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class EstacionDao extends ContentProvider {
	private DataBaseHelper dbHelper;
	private SQLiteDatabase database;

	private static final String PROVIDER_NAME = "me.metro.bgl.model.dao.EstacionDao";// PROVIDER_NAME
	public static final int ESTACIONES_CON_HORARIO = 100;
	public static final int ESTACIONES_FRECUENTES_CON_HORARIO = 110;
	public static final int ESTACION_CON_HORARIOS = 120;

	private static final String ESTACIONES_BASE_PATH = "estaciones";
	public static final String FAVORITAS = "favoritas";
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ PROVIDER_NAME + "/" + ESTACIONES_BASE_PATH);

	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/vnd.bgl.metro-estacion";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/vnd.bgl.metro-estacion";

	private static final UriMatcher sURIMatcher;
	static {
		sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sURIMatcher.addURI(PROVIDER_NAME, ESTACIONES_BASE_PATH,
				ESTACIONES_CON_HORARIO);
		sURIMatcher.addURI(PROVIDER_NAME, ESTACIONES_BASE_PATH + "/"
				+ FAVORITAS, ESTACIONES_FRECUENTES_CON_HORARIO);
		sURIMatcher.addURI(PROVIDER_NAME, ESTACIONES_BASE_PATH + "/#",
				ESTACION_CON_HORARIOS);
	}

	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String SELECTION = "selection";
	public static final String EDITABLE = "editable";

	@Override
	public boolean onCreate() {
		dbHelper = new DataBaseHelper(getContext());
		try {
			dbHelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}
		try {
			dbHelper.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}
		database = dbHelper.getWritableDatabase();
		return true;
	}
	public void open(Context context) throws SQLException {
		dbHelper = new DataBaseHelper(context);
		try {
			dbHelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}
		try {
			dbHelper.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	@Override
	public String getType(Uri url) {
		int match = sURIMatcher.match(url);
		switch (match) {
		case ESTACIONES_CON_HORARIO:
			return CONTENT_TYPE;
		case ESTACION_CON_HORARIOS:
			return CONTENT_ITEM_TYPE;
		}
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String query;

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case ESTACIONES_CON_HORARIO:
			query = DataBaseHelper.QUERY_ESTACIONES_HORARIO_BEGIN
					+ DataBaseHelper.QUERY_ESTACIONES_HORARIO_END;
			break;
		case ESTACIONES_FRECUENTES_CON_HORARIO:
			if (selection == null)
				selection = "-1";
			query = DataBaseHelper.QUERY_ESTACIONES_HORARIO_BEGIN
					+ "WHERE e._id in (" + selection + ") "
					+ DataBaseHelper.QUERY_ESTACIONES_HORARIO_END;
			break;
		case ESTACION_CON_HORARIOS:
			query = DataBaseHelper.QUERY_HORARIOS;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		Cursor cursor = null;
		cursor = database.rawQuery(query, null);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	public List<EstacionBean> getAllEstaciones(String orderBy) {
		List<EstacionBean> estaciones = new ArrayList<EstacionBean>();

		Cursor cursor = database.query(DataBaseHelper.TABLE_ESTACION,
				new String[] { DataBaseHelper.COLUMN_ID,
						DataBaseHelper.COLUMN_CODIGO,
						DataBaseHelper.COLUMN_NOMBRE,
						DataBaseHelper.COLUMN_DIRECCION,
						DataBaseHelper.COLUMN_DISTRITO }, null, null, null,
				null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			EstacionBean estacion = cursorToEstacion(cursor);
			estaciones.add(estacion);
			cursor.moveToNext();
		}
		cursor.close();
		return estaciones;
	}

	public EstacionBean getCursorEstacionConHorarios(long id) {
		EstacionBean estacion = new EstacionBean();
		String[] arg = { String.valueOf(id) };
		Cursor cursor = database.rawQuery(DataBaseHelper.QUERY_HORARIOS, arg);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			estacion.setId(cursor.getInt(0));
			estacion.setNombre(cursor.getString(1));
			ArrayList<HorarioBean> horarios = new ArrayList<HorarioBean>();

			while (!cursor.isAfterLast()) {
				HorarioBean horario = new HorarioBean();
				if (!cursor.getString(2).equalsIgnoreCase("")) {
					horario.setHora_a_gr(cursor.getString(2));
				}
				if (!cursor.getString(3).equalsIgnoreCase("")) {
					horario.setHora_a_ves(cursor.getString(3));
				}
				horarios.add(horario);
				cursor.moveToNext();
			}
			estacion.setHorarios(horarios);
		}
		cursor.close();
		return estacion;
	}

	public EstacionBean getEstacion(long id) {
		EstacionBean estacion = new EstacionBean();
		String[] arg = { String.valueOf(id) };
		Cursor cursor = database.rawQuery(DataBaseHelper.QUERY_HORARIOS, arg);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			estacion.setId(cursor.getInt(0));
			estacion.setNombre(cursor.getString(1));
			ArrayList<HorarioBean> horariosGR = new ArrayList<HorarioBean>();
			ArrayList<HorarioBean> horariosVES = new ArrayList<HorarioBean>();

			while (!cursor.isAfterLast()) {
				if (!cursor.getString(2).equalsIgnoreCase("")) {
					HorarioBean horario = new HorarioBean();
					horario.setHora_a_gr(cursor.getString(2));
					horariosGR.add(horario);
				}
				if (!cursor.getString(3).equalsIgnoreCase("")) {
					HorarioBean horario = new HorarioBean();
					horario.setHora_a_ves(cursor.getString(3));
					horariosVES.add(horario);
				}
				cursor.moveToNext();
			}
			estacion.setHorariosGR(horariosGR);
			estacion.setHorariosVES(horariosVES);
		}
		cursor.close();
		return estacion;
	}

	static public EstacionBean cursorToEstacion(Cursor cursor) {
		EstacionBean estacion = new EstacionBean();
		estacion.setId(cursor.getInt(0));
		estacion.setCodigo(cursor.getString(1));
		estacion.setNombre(cursor.getString(2));
		estacion.setDireccion(cursor.getString(3));
		estacion.setDistrito(cursor.getString(4));

		if (cursor.getColumnCount() > 5) {
			estacion.setHora_a_gr(cursor.getString(5));
			estacion.setHora_a_ves(cursor.getString(6));
			estacion.setPos_a_gr(cursor.getInt(7));
			estacion.setPos_a_ves(cursor.getInt(8));
		}

		return estacion;
	}

	static public EstacionBean cursorToEstacionConHorarios(Cursor cursor) {
		if (!cursor.getString(2).equalsIgnoreCase("")) {
			HorarioBean horario = new HorarioBean();
			horario.setHora_a_gr(cursor.getString(2));
		}
		if (!cursor.getString(3).equalsIgnoreCase("")) {
			HorarioBean horario = new HorarioBean();
			horario.setHora_a_ves(cursor.getString(3));
		}
		EstacionBean estacion = new EstacionBean();
		estacion.setId(cursor.getInt(0));
		estacion.setCodigo(cursor.getString(1));
		estacion.setNombre(cursor.getString(2));
		estacion.setDireccion(cursor.getString(3));
		estacion.setDistrito(cursor.getString(4));

		if (cursor.getColumnCount() > 5) {
			estacion.setHora_a_gr(cursor.getString(5));
			estacion.setHora_a_ves(cursor.getString(6));
		}

		return estacion;
	}

	public static String getFavoritas(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME,
				0);
		return settings.getString(SELECTION, null);
	}

	public static void setFavoritas(Activity activity, String favoritas) {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME,0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(SELECTION, favoritas);
		editor.commit();		
	}
	public static boolean isEditable(Activity activity) {
		// Restore preferences
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME,0);
		return settings.getBoolean(EDITABLE, true);
	}
	
	public static void setEditable(Activity activity, boolean editable) {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME,0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(EDITABLE, editable);
		editor.commit();		
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		return 0;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		return 0;
	}
}
