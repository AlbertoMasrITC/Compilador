package Prueba;

public class TablaSimbolos {
	private String tipoDato;
	private String variable;
	private Object valor;
	private int posicion;
	private String alcance;

	public TablaSimbolos(String tipoDato, String variable, Object valor, int posicion, String alcance) {
		this.tipoDato = tipoDato;
		this.variable = variable;
		this.valor = valor;
		this.posicion = posicion;
		this.alcance = alcance;
	}
	public String getTipoDato() {
		return tipoDato;
	}
	public void setTipoDato(String tipoDato) {
		this.tipoDato = tipoDato;
	}
	public String getVariable() {
		return variable;
	}
	public void setVariable(String variable) {
		this.variable = variable;
	}
	public Object getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public int getPosicion() {
		return posicion;
	}
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	public String getAlcance() {
		return alcance;
	}
	public void setAlcance(String alcance) {
		this.alcance = alcance;
	}
	
	@Override
	public String toString()
	{
		
		return String.format("%10s \t %10s \t %10s \t %-10d \t %10s", tipoDato, variable, valor, posicion, alcance);
		
	}
	
}
