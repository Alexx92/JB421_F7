package com.test.object;

import java.io.Serializable;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
@CsvRecord(separator=",", crlf="UNIX")
public class Order implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DataField(pos=1)
	private String no;
	@DataField(pos=2)
	private String tipo;
	@DataField(pos=3)
	private int numero;
	@DataField(pos=4)
	private String description;
	
	public Order() {
		
	}
	
	public Order(String no, String tipo, int numero, String descripcion) {
		this.no = no;
		this.tipo = tipo;
		this.numero = numero;
		this.description = descripcion;
	}
	
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
