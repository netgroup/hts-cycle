package it.uniroma2.musicsharep2p;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseHelper extends SQLiteOpenHelper{

	//Classe delegata al controllo del database
	private static String DB_PATH = "/data/data/it.uniroma2.musicsharep2p/databases/";

	private static String DB_NAME = "Db.sqlite";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	/**
	 * Costruttore
	 * Prende e registra il contesto dato per accedere agli assets e alle risorse
	 * @param context
	 */
	public DataBaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}	


	/**
	 * Crea un database vuoto e lo riempie con il database prelevato dagli assets
	 * */
	public void createDataBase() throws IOException{

		boolean dbExist = checkDataBase();

		if(dbExist){
			//Il database già esiste, non fa niente
		}else{

			//Crea il database
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Controlla l'esistenza del Db
	 * @return true se esiste, altrimenti false
	 */
	private boolean checkDataBase(){

		SQLiteDatabase checkDB = null;

		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		}catch(SQLiteException e){

			//database does't exist yet.

		}

		if(checkDB != null){

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copia il database da assets alla cartella di sistema
	 * */
	private void copyDataBase() throws IOException{

		//Apre il database dagli assets come inputstream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Prende il path al database che creato
		String outFileName = DB_PATH + DB_NAME;

		//Lo apre come outputstream
		OutputStream myOutput = new FileOutputStream(outFileName);

		//scrittura di bytes da inputstream a output stream
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//Chiude gli stream
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException{

		//Apre il Db
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
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}


}
