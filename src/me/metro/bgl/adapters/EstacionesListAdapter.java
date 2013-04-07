package me.metro.bgl.adapters;

import java.util.ArrayList;

import me.metro.bgl.R;
import me.metro.bgl.model.beans.EstacionBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class EstacionesListAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private ArrayList<EstacionBean> estaciones;
	
	public EstacionesListAdapter(Context context,ArrayList<EstacionBean> arr){
		inflater =  LayoutInflater.from(context);
		estaciones = arr;
	}

	public int getCount() {
		return estaciones.size();
	}

	public Object getItem(int index) {
		return estaciones.get(index);
	}

	public long getItemId(int index) {
		return estaciones.get(index).getId();
	}

	public View getView(int position, View convertView, ViewGroup parentView) {
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.estaciones_item, null);
			
			holder.tviEstacion = (TextView) convertView.findViewById(R.id.tviEstacion);
			holder.tviHorario_a_gr = (TextView) convertView.findViewById(R.id.tviHorario_a_gr);
			holder.tviHorario_a_ves = (TextView) convertView.findViewById(R.id.tviHorario_a_ves);
			
			convertView.setTag(holder);
		}else 
			holder = (ViewHolder)convertView.getTag();
		
		EstacionBean estacion = estaciones.get(position);
		
		holder.tviEstacion.setText(estacion.getNombre());
		holder.tviHorario_a_gr.setText(estacion.getHora_a_gr12h());
		holder.tviHorario_a_ves.setText(estacion.getHora_a_ves12h());
		
		return convertView;
	}
	static class ViewHolder{
		TextView tviEstacion;
		TextView tviHorario_a_gr;
		TextView tviHorario_a_ves;
	}
}