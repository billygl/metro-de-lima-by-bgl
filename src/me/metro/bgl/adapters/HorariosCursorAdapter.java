package me.metro.bgl.adapters;

import me.metro.bgl.R;
import me.metro.bgl.model.beans.EstacionBean;
import me.metro.bgl.model.beans.HorarioBean;
import me.metro.bgl.model.dao.EstacionDao;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class HorariosCursorAdapter extends CursorAdapter{
	private LayoutInflater inflater;
	private int horario_direccion;
	
	public HorariosCursorAdapter(Context context, Cursor c,int flags) {		
		super(context,c,flags);
		inflater =  LayoutInflater.from(context);
	}
	
	public View getView(int position, View convertView, ViewGroup parentView) {
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.horarios_item, null);
			
			holder.tviHorario = (TextView) convertView.findViewById(R.id.tviHorario);
			
			convertView.setTag(holder);
		}else 
			holder = (ViewHolder)convertView.getTag();
		
		
		HorarioBean horario = new HorarioBean();//horarios.get(position);
		String str_horario = "";
		switch(horario_direccion){
		case 0:
			str_horario = horario.getHora_a_gr12();
			break;
		case 1:
			str_horario = horario.getHora_a_ves12();
			break;
		}
		holder.tviHorario.setText(str_horario);
		
		return convertView;
	}
	static class ViewHolder{
		TextView tviHorario;
	}
	@Override
	public View newView(Context context, Cursor c, ViewGroup parentView) {
		View v = inflater.inflate(R.layout.horarios_item, parentView,false);
		bindView(v, context, c);
		return v;
	}
		
	@Override
	public void bindView(View view, Context context, Cursor c) {
		EstacionBean estacion = EstacionDao.cursorToEstacion(c);		
		TextView tviEstacion = (TextView) view.findViewById(R.id.tviHorario);
		if(tviEstacion != null){
			TextView tviHorario_a_gr = (TextView) view.findViewById(R.id.tviHorario_a_gr);
			TextView tviHorario_a_ves = (TextView) view.findViewById(R.id.tviHorario_a_ves);
			
			tviEstacion.setText(estacion.getNombre());
			tviHorario_a_gr.setText(estacion.getHora_a_gr12h());
			tviHorario_a_ves.setText(estacion.getHora_a_ves12h());			
		}
		//return view;
	}
}