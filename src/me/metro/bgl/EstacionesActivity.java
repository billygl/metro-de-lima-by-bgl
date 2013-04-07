package me.metro.bgl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import me.metro.bgl.adapters.EstacionesCursorAdapter;
import me.metro.bgl.model.beans.EstacionBean;
import me.metro.bgl.model.dao.DataBaseHelper;
import me.metro.bgl.model.dao.EstacionDao;
import me.metro.bgl.utils.Util;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class EstacionesActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.estaciones_list);
	}

	public static class CursorLoaderListFragment extends ListFragment implements
			LoaderManager.LoaderCallbacks<Cursor>, OnItemLongClickListener {
		private static final int LIST_LOADER = 0x01;
		private static final int ACTIVITY_HORARIOS = 0;
		private static final long MS_UPDATE = 1 * 15 * 1000;
		private static final long MS_UPDATE_FAVORITAS = 1 * 1000;

		private EstacionesCursorAdapter adapter;
		private String favoritas;
		private boolean siFavoritas = false;
		private long estacion_favorita;
		

		final private Runnable actualizarHorarioProximo = new Runnable() {
			public void run() {
				getLoaderManager().restartLoader(LIST_LOADER, null,
						CursorLoaderListFragment.this);
			}

		};

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			favoritas = EstacionDao.getFavoritas(getActivity());
			Bundle args = getArguments();
			if (args != null)
				siFavoritas = args.getBoolean(DataBaseHelper.FAVORITAS);

			if(siFavoritas)setEmptyText(getText(R.string.bienvenida).toString());
			setRetainInstance(true);

			getListView().setOnItemLongClickListener(this);

			if (adapter == null) {
				adapter = new EstacionesCursorAdapter(getActivity(), null,
						CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			}
			setListAdapter(adapter);
			setListShown(false);

			LoaderManager lm = getLoaderManager();
			lm.initLoader(LIST_LOADER, null, this);

			crearHilo();
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			EstacionBean estacion = (EstacionBean) adapter.getItem(position);

			Intent i = new Intent(getActivity(), HorariosActivity.class);
			i.putExtra(DataBaseHelper.COLUMN_ID, id);
			i.putExtra(DataBaseHelper.COLUMN_NOMBRE, estacion.getNombre());
			i.putExtra(DataBaseHelper.COLUMN_POS_A_GR, estacion.getPos_a_gr());
			i.putExtra(DataBaseHelper.COLUMN_POS_A_VES, estacion.getPos_a_ves());
			startActivityForResult(i, ACTIVITY_HORARIOS);
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> adapter, View v,
				int position, long id) {
			MainActivity main = (MainActivity)getActivity();
			if(!main.isEditable())return false;
			estacion_favorita = id;
			favoritas = EstacionDao.getFavoritas(getActivity());
			if (!siFavoritas) {
				if (favoritas == null || favoritas.equalsIgnoreCase("")) {
					
					favoritas = String.valueOf(id);
					EstacionDao.setFavoritas(getActivity(), favoritas);
					Toast.makeText(getActivity(), "Favorita agregada",
							Toast.LENGTH_SHORT).show();
					return true;
				}
			}
			String[] arr = favoritas.split(",");
			Set<String> mySet = new HashSet<String>();
			Collections.addAll(mySet, arr);
			if (siFavoritas) {// quitar
				mySet.remove(String.valueOf(estacion_favorita));
				Toast.makeText(getActivity(), "Favorita eliminada",
					Toast.LENGTH_SHORT).show();
			} else {// agregar
				Toast.makeText(getActivity(), "Favorita agregada",
						Toast.LENGTH_SHORT).show();
				mySet.add(String.valueOf(estacion_favorita));
			}
			favoritas = Util.setToString(mySet, ",");
			EstacionDao.setFavoritas(getActivity(), favoritas);
			return true;
		}
		
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Uri baseUri = EstacionDao.CONTENT_URI;
			if (siFavoritas) {
				baseUri = Uri.withAppendedPath(EstacionDao.CONTENT_URI,
						Uri.encode(EstacionDao.FAVORITAS));
			} else {
				baseUri = EstacionDao.CONTENT_URI;
			}
			favoritas = EstacionDao.getFavoritas(getActivity());
			CursorLoader cursorLoader = new CursorLoader(getActivity(),
					baseUri, null, favoritas, null, null);
			// cursorLoader.setUpdateThrottle(ms_update);
			return cursorLoader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			adapter.swapCursor(cursor);			
			if (isResumed()) {
				setListShown(true);
			} else {
				setListShownNoAnimation(true);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			adapter.swapCursor(null);
		}

		private void crearHilo() {
			Thread hilo = new Thread() {
				public void run() {
					while (true) {
						try {
							if(siFavoritas)Thread.sleep(MS_UPDATE_FAVORITAS);
							else{
								Thread.sleep(MS_UPDATE);
							}
						} catch (InterruptedException e) {

						}
						if (isResumed())
							getActivity().runOnUiThread(
									actualizarHorarioProximo);
					}
				}
			};
			hilo.start();
		}

	}
}