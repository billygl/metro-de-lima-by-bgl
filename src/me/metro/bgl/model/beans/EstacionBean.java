package me.metro.bgl.model.beans;

import java.util.ArrayList;

import me.metro.bgl.utils.Util;

public class EstacionBean {
	private int id;
	private String codigo;
	private String nombre;
	private String distrito;
	private String direccion;
	private String hora_a_gr;
	private String hora_a_ves;
	private int pos_a_gr;
	private int pos_a_ves;
	private ArrayList<HorarioBean> horarios;
	private ArrayList<HorarioBean> horariosGR;
	private ArrayList<HorarioBean> horariosVES;

	public int getId() {
		return id;
	}

	public void setId(int _id) {
		this.id = _id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDistrito() {
		return distrito;
	}

	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getHora_a_gr() {
		return hora_a_gr;
	}

	public String getHora_a_gr12h() {
		return Util.get12Hora(hora_a_gr);
	}

	public void setHora_a_gr(String hora_a_gr) {
		this.hora_a_gr = hora_a_gr;
	}

	public String getHora_a_ves() {
		return hora_a_ves;
	}
	public String getHora_a_ves12h() {
		return Util.get12Hora(hora_a_ves);
	}
	public void setHora_a_ves(String hora_a_ves) {
		this.hora_a_ves = hora_a_ves;
	}

	public ArrayList<HorarioBean> getHorarios() {
		return horarios;
	}

	public void setHorarios(ArrayList<HorarioBean> horarios) {
		this.horarios = horarios;
	}

	public ArrayList<HorarioBean> getHorariosGR() {
		return horariosGR;
	}

	public void setHorariosGR(ArrayList<HorarioBean> horariosGR) {
		this.horariosGR = horariosGR;
	}

	public ArrayList<HorarioBean> getHorariosVES() {
		return horariosVES;
	}

	public void setHorariosVES(ArrayList<HorarioBean> horariosVES) {
		this.horariosVES = horariosVES;
	}
	public int getPos_a_gr() {
		return pos_a_gr;
	}

	public void setPos_a_gr(int pos_a_gr) {
		this.pos_a_gr = pos_a_gr;
	}

	public int getPos_a_ves() {
		return pos_a_ves;
	}

	public void setPos_a_ves(int pos_a_ves) {
		this.pos_a_ves = pos_a_ves;
	}
}
