package me.metro.bgl.model.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{
 
    private static String DB_PATH = "/data/data/me.metro.bgl/databases/";
    private static String DB_NAME = "metro_database";
    private static final String TAG = "MetroDbAdapter";
    private static final int DATABASE_VERSION = 3;//2
    
    public static final int SORTED_COLUMN_INDEX = 2;
    
    public static final String FAVORITAS = "FAVORITAS";
    public static final String COLUMN_CODIGO = "codigo";
    public static final String COLUMN_NOMBRE = "nombre";    
    public static final String COLUMN_DISTRITO = "distrito";
    public static final String COLUMN_DIRECCION = "direccion";
    public static final String COLUMN_HORA_A_GR = "hora_a_gr";
    public static final String COLUMN_HORA_A_VES = "hora_a_ves";
    public static final String COLUMN_POS_A_GR = "pos_a_gr";
    public static final String COLUMN_POS_A_VES = "pos_a_ves";
    public static final String COLUMN_ID = "_id";    
    public static final String COLUMN_HORARIO_POSITION = "position";    
    public static final String TABLE_ESTACION = "estacion";
    public static final String TABLE_HORARIO = "horario";
    public static final String ESTACION_DESTINO_GR = "Grau ";//con un espacio final :(
    public static final String ESTACION_DESTINO_VES = "Villa El Salvador ";
    
    public static final String QUERY_ESTACIONES_HORARIO2 =
    	    "SELECT DISTINCT estacion._id AS _id,estacion.codigo,estacion.nombre,estacion.distrito,estacion.direccion,"+
    	    "    (SELECT min(h1.hora_a_gr) FROM horario h1 "+
    	    "     WHERE h1.idestacion = horario.idestacion and time(h1.hora_a_gr) > time('now','localtime')"+
    	    "    ) hora_a_gr,"+
    	    "    (SELECT min(h2.hora_a_ves) FROM horario h2"+ 
    	    "     WHERE h2.idestacion = horario.idestacion and time(h2.hora_a_ves) > time('now','localtime')"+
    	    "    ) hora_a_ves "+
    	    "    FROM estacion "+
    	    "INNER JOIN horario ON estacion._id = horario.idestacion "+
    	    "ORDER BY nombre";
    
    //en la Android 2.1 es necesario usar AS.
    public static final String QUERY_ESTACIONES_HORARIO_BEGIN = 
    		"SELECT e._id AS _id,e.codigo,e.nombre,e.distrito,e.direccion," +
    		"MIN(horario1.hora_a_gr) AS hora_a_gr,MIN(horario2.hora_a_ves) AS hora_a_ves," +
    		"MIN(horario1._id) AS pos_a_gr,MIN(horario2._id) AS pos_a_ves "+
    		"FROM estacion e "+
		    "LEFT JOIN horario as horario1 ON e._id = horario1.idestacion and " +
		    "		time(horario1.hora_a_gr) > time('now','localtime') and " +
		    "		time(horario1.hora_a_gr) < time('now','localtime','+30 minutes') "+
		    "LEFT JOIN horario as horario2 ON e._id = horario2.idestacion and " +
		    "		time(horario2.hora_a_ves) > time('now','localtime') and " +
		    "		time(horario2.hora_a_ves) < time('now','localtime','+30 minutes') ";
    
    public static final String QUERY_ESTACIONES_HORARIO_END = 		    
			"GROUP BY e._id,e.nombre,e.nombre,e.distrito,e.direccion " +
			"ORDER BY e.nombre";
    
    public static final String QUERY_HORARIOS = 
    		"SELECT e._id,e.nombre,hora_a_gr,hora_a_ves, " +
    		"case when time(horario.hora_a_gr) > time('now','localtime') then horario._id else 0 end posicion_gr," +
    		"case when time(horario.hora_a_ves) > time('now','localtime') then horario._id else 0 end posicion_ves " +
    		"FROM estacion e "+
    		"INNER JOIN horario ON e._id = horario.idestacion " +
    		"WHERE horario.idestacion = ?";
    
    public static final String[] QUERY_ESTACIONES_HORARIO_ORDER_BY_FIELDS ={"e.nombre","e._id"};
    
    private SQLiteDatabase myDataBase;   
    private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, DATABASE_VERSION);    	
        this.myContext = context;
    }	

  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{ 
    	
    	boolean dbExist = checkDataBase(); 
    	SQLiteDatabase db_Read = null;
    	if(dbExist){
    		//do nothing - database already exist
    	}else{ 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.        	
    		db_Read = this.getReadableDatabase(); 
    		db_Read.close();
        	try { 
    			copyDataBase(); 
    		} catch (IOException e) { 
    			//e.printStackTrace();
        		throw new Error("Error copying database"); 
        	}
    	} 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
    	SQLiteDatabase checkDB = null; 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);     		
    	}catch(SQLiteException e){	
    		//throw new Error("SQLException - can't open database");
    	}catch(Exception e){
    		//throw new Error("Exception - can't open database");
    	}
    	if(checkDB != null){ 
    		if(checkDB.getVersion()<DATABASE_VERSION){
    			checkDB.close();
    			deleteDatabase();
    			return false;    			
    		}
    		checkDB.close(); 
    	} 
    	return checkDB != null ? true : false;
    }
    private boolean deleteDatabase(){
    	String outFileName = DB_PATH + DB_NAME; 
		File file = new File(outFileName);
		boolean deleted = file.delete();
		return deleted; 
    }
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME); 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME; 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName); 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	} 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{ 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY); 
    }
 
    @Override
	public synchronized void close() { 
    	    if(myDataBase != null)
    		    myDataBase.close(); 
    	    super.close();
	} 
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        //db.execSQL("DROP TABLE IF EXISTS horario");		
        //onCreate(db);
	} 
	@Override
	public void onOpen(SQLiteDatabase db) {
		
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}
}