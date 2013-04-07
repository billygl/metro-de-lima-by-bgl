package me.metro.bgl.adapters;


import me.metro.bgl.R;
import me.metro.bgl.model.beans.EstacionBean;
import me.metro.bgl.model.dao.DataBaseHelper;
import me.metro.bgl.model.dao.EstacionDao;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class EstacionesCursorAdapter extends CursorAdapter implements
		SectionIndexer {
	AlphabetIndexer alphaIndexer;
	private LayoutInflater inflater;
	
	public EstacionesCursorAdapter(Context context, Cursor c,int flags) {		
		super(context,c,flags);
		alphaIndexer=new AlphabetIndexer(c, 
				DataBaseHelper.SORTED_COLUMN_INDEX, " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		inflater = LayoutInflater.from(context);
	}	
	
	@Override
	public View newView(Context context, Cursor c, ViewGroup parentView) {
		View v = inflater.inflate(R.layout.estaciones_item, parentView,false);
		bindView(v, context, c);
		return v;
	}
		
	@Override
	public void bindView(View view, Context context, Cursor c) {
		EstacionBean estacion = EstacionDao.cursorToEstacion(c);		
		view = setViewFromEstacion(view,estacion);		
	}

	private View setViewFromEstacion(View v,EstacionBean estacion){
		TextView tviEstacion = (TextView) v.findViewById(R.id.tviEstacion);
		if(tviEstacion != null){
			TextView tviHorario_a_gr = (TextView) v.findViewById(R.id.tviHorario_a_gr);
			TextView tviHorario_a_ves = (TextView) v.findViewById(R.id.tviHorario_a_ves);
			TextView tviLblHorario_a_gr = (TextView) v.findViewById(R.id.tviLblHorario_a_gr);
			TextView tviLblHorario_a_ves = (TextView) v.findViewById(R.id.tviLblHorario_a_ves);
			TextView tviSeparator = (TextView) v.findViewById(R.id.textViewS);
			
			tviEstacion.setText(estacion.getNombre());
			tviHorario_a_gr.setText(estacion.getHora_a_gr12h());
			tviHorario_a_ves.setText(estacion.getHora_a_ves12h());	
			
			tviLblHorario_a_gr.setVisibility(View.VISIBLE);
			tviLblHorario_a_ves.setVisibility(View.VISIBLE);
			tviSeparator.setVisibility(View.VISIBLE);
			
			if(estacion.getNombre().equalsIgnoreCase(DataBaseHelper.ESTACION_DESTINO_GR)){								
				tviSeparator.setVisibility(View.INVISIBLE);
				tviLblHorario_a_gr.setVisibility(View.INVISIBLE);
			}else if(estacion.getNombre().equalsIgnoreCase(DataBaseHelper.ESTACION_DESTINO_VES)){	
				tviSeparator.setVisibility(View.INVISIBLE);
				tviLblHorario_a_ves.setVisibility(View.INVISIBLE);
			}
		}
		return v;
	}
	@Override
	public Object getItem(int position) {
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		return EstacionDao.cursorToEstacion(cursor);
	}

	@Override
	public int getPositionForSection(int section) {
		Log.i("SECTION",String.valueOf(section));
		return alphaIndexer.getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		Log.i("SECTION",String.valueOf(position));
		return alphaIndexer.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		Log.i("SECTION","sections");
		return alphaIndexer.getSections();
	}

}
