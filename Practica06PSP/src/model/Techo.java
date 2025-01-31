package model;

public class Techo {
	
	private Integer altura;
	
	public Techo(Integer altura) {
		setAltura(altura);
	}
	
	public void setAltura(Integer altura) {
		this.altura = altura;
	}
	
	public Integer getAltura() {
		return altura;
	}
	
	public String toString() {return "El techo está a " + getAltura() + " metros";}
}
