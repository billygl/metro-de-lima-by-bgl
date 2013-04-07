package me.metro.bgl;

import java.util.ArrayList;

import me.metro.bgl.adapters.HorariosListAdapter;
import me.metro.bgl.model.beans.EstacionBean;
import me.metro.bgl.model.beans.HorarioBean;
import me.metro.bgl.model.dao.DataBaseHelper;
import me.metro.bgl.model.dao.EstacionDao;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HorariosActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, OnClickListener {
	private static final int LIST_GR_LOADER = 0x01;
	private static final int LIST_VES_LOADER = 0x02;
	public static final int DIRECCION_GR = 0;
	public static final int DIRECCION_VES = 1;
	public static final long ID_ESTACION_GR = 16;
	public static final long ID_ESTACION_VES = 1;
	private Long idestacion;
	private TextView tviEstacion;
	private DigitalClock dclReloj;
	private ListView lviGR;
	private ListView lviVES;
	private LinearLayout linlHeader;
	private ImageView iviBtnBack;

	private EstacionDao estacionDao;
	private int posGR=0,posVES=0;
	private HorariosListAdapter adapter_gr, adapter_ves;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.horarios_list);

		estacionDao = new EstacionDao();
		estacionDao.open(this);

		idestacion = null;
		Bundle extras = getIntent().getExtras();//if null - imposible

		idestacion = extras.getLong(DataBaseHelper.COLUMN_ID);
		tviEstacion = (TextView) findViewById(R.id.tviEstacion);
		dclReloj = (DigitalClock) findViewById(R.id.dclReloj);		
		lviGR = (ListView) findViewById(R.id.lviGR);
		lviVES = (ListView) findViewById(R.id.lviVES);
		linlHeader = (LinearLayout) findViewById(R.id.linlHeader);
		iviBtnBack = (ImageView) findViewById(R.id.iviBtnBack);
		
		linlHeader.setOnClickListener(this);
		iviBtnBack.setVisibility(View.VISIBLE);
		dclReloj.setOnClickListener(this);
		tviEstacion.setText(extras.getString(DataBaseHelper.COLUMN_NOMBRE));

		posGR = (extras.getInt(DataBaseHelper.COLUMN_POS_A_GR)-1) % 62;
		posVES = (extras.getInt(DataBaseHelper.COLUMN_POS_A_VES)-1) % 62;
		
		EstacionBean estacion = estacionDao.getCursorEstacionConHorarios(idestacion);
		ArrayList<HorarioBean> horarios = estacion.getHorarios();

		if(idestacion == ID_ESTACION_GR){
			adapter_gr = new HorariosListAdapter(this, new ArrayList<HorarioBean>(), DIRECCION_GR);
		}else{
			adapter_gr = new HorariosListAdapter(this, horarios, DIRECCION_GR);
		}
		if(idestacion == ID_ESTACION_VES){
			adapter_ves = new HorariosListAdapter(this, new ArrayList<HorarioBean>(), DIRECCION_VES);
		}else{
			adapter_ves = new HorariosListAdapter(this, horarios, DIRECCION_VES);
		}
		
		lviGR.setAdapter(adapter_gr);
		lviVES.setAdapter(adapter_ves);

		posicionarAhora();		
	}

	private void posicionarAhora(){
		lviGR.setSelection(posGR);
		adapter_gr.setSelected_position(posGR);
		lviVES.setSelection(posVES);
		adapter_ves.setSelected_position(posVES);
	}
	@Override
	public void onClick(View v) {
		if (v.getId() == linlHeader.getId()) {
			Intent mIntent = new Intent();
			setResult(RESULT_OK, mIntent);
			finish();
		}else if(v.getId() == dclReloj.getId()){
			posicionarAhora();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri;
		switch (id) {
		case LIST_GR_LOADER:
			uri = Uri.parse(EstacionDao.CONTENT_URI + "/" + id);
			return new CursorLoader(this, uri, null, null, null, null);
		case LIST_VES_LOADER:
			uri = Uri.parse(EstacionDao.CONTENT_URI + "/" + id);
			return new CursorLoader(this, uri, null, null, null, null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}