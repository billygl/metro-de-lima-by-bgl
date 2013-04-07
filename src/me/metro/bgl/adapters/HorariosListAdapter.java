package me.metro.bgl.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import me.metro.bgl.R;
import me.metro.bgl.model.beans.HorarioBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HorariosListAdapter extends BaseAdapter /*implements SectionIndexer*/ {
	private LayoutInflater inflater;
	private ArrayList<HorarioBean> horarios;
	private int horario_direccion;
	HashMap<String, Integer> alphaIndexer;
	String[] sections;
	private int selected_position = -1;

	public HorariosListAdapter(Context context, ArrayList<HorarioBean> arr,
			int horario_direccion) {
		inflater = LayoutInflater.from(context);
		horarios = arr;
		this.horario_direccion = horario_direccion;
	}

	public int getSelected_position() {
		return selected_position;
	}

	public void setSelected_position(int selected_position) {
		this.selected_position = selected_position;
	}

	public int getCount() {
		return horarios.size();
	}

	public Object getItem(int index) {
		return horarios.get(index);
	}

	public long getItemId(int index) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parentView) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.horarios_item, null);

			holder.tviHorario = (TextView) convertView.findViewById(R.id.tviHorario);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		if(selected_position==position){
			convertView.setBackgroundResource(R.drawable.list_selector_pressed);
		}else{
			convertView.setBackgroundResource(R.drawable.list_selector);
		}
		
		HorarioBean horario = horarios.get(position);
		switch (horario_direccion) {
		case 0:
			holder.tviHorario.setText(horario.getHora_a_gr12());
			break;
		case 1:
			holder.tviHorario.setText(horario.getHora_a_ves12());
			break;
		}
		

		return convertView;
	}

	static class ViewHolder {
		TextView tviHorario;
	}
}