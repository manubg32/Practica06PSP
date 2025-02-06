package model;

public class Techo {

	//Atributo altura
	private Integer altura;

	//Constructor
	public Techo(Integer altura) {
		setAltura(altura);
	}

	//G&S
	public void setAltura(Integer altura) {
		this.altura = altura;
	}
	public Integer getAltura() {
		return altura;
	}

	//Tostring
	public String toString() {return "El techo está a " + getAltura() + " metros";}
}
