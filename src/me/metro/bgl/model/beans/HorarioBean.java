package me.metro.bgl.model.beans;

import me.metro.bgl.utils.Util;

public class HorarioBean {	
	private String hora_a_gr;
	private String hora_a_ves;
	public String getHora_a_gr() {
		return hora_a_gr;
	}
	public String getHora_a_gr12() {
		return Util.get12Hora(hora_a_gr);
	}
	public void setHora_a_gr(String hora_a_gr) {
		this.hora_a_gr = hora_a_gr;
	}
	public String getHora_a_ves() {
		return hora_a_ves;
	}
	public String getHora_a_ves12() {
		return Util.get12Hora(hora_a_ves);
	}
	public void setHora_a_ves(String hora_a_ves) {
		this.hora_a_ves = hora_a_ves;
	}
}
