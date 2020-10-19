import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class AnalizadorSemantico 
{	
	
	// Crea ArrayList para leer el archivo y guardar los datos
	ArrayList<String> linea = new ArrayList<String>();
	ArrayList<String> datos = new ArrayList<String>();
	
	// Crea ArrayList de los diferentes datos a llenar en la tabla de simbolos
	ArrayList<String> tipoDato = new ArrayList<String>();
	ArrayList<String> variable = new ArrayList<String>();
	ArrayList<String> valor = new ArrayList<String>();
	ArrayList<Integer> posicion = new ArrayList<Integer>();
	ArrayList<String> alcance = new ArrayList<String>();
	
	// Crea ArrayList para almacenar los errores
	ArrayList <String> listaErroresSemanticos = new ArrayList<String>();
	
	// Variable con copia de c�digo para encontrar posiciones
	String codigoGlobal = "";
	
	// Constructor que invoca cada uno de los m�todos del programa
	public AnalizadorSemantico(String URL)
	{
		
		String codigo = leerArchivo(URL);
		codigoGlobal = codigo;
		recorrerCodigo(codigo);		
		asignarCodigo(codigo);
		TipoDatosVariablesValoresPosicionesAlcances();
		validarSentencias();
		imprimirTabla();
		
		System.out.println();
		
		for(String s: listaErroresSemanticos)
			System.err.println(s);
		
		System.out.println();
		System.out.println();
		
		int c = 0;
		
		for(int i = 0; i < linea.size(); i++)
		{
			
			c++;
			System.out.println(c +".- "+ linea.get(i));
			
		}
		
		/*for(String s: datos)
			System.out.println(s);*/
		
	}
	
	// M�todo para leer un archivo desde la URL
	public static String leerArchivo(String URL)
	{
		
		String parrafo = "";
		String texto = "";
		
		try 
		{
			
			FileReader archivo = new FileReader(URL);
			BufferedReader leeLinea = new BufferedReader(archivo);
			parrafo = leeLinea.readLine();
			
			while (parrafo != null) 
			{
				
				texto += parrafo;
				parrafo = leeLinea.readLine();
				
			}
			
		} catch (FileNotFoundException e) 
		{
			
			System.out.println(e);
			
		} 
		catch (Exception e)
		{
			
			System.out.println(e);
		
		}
		
		return texto;
		
	}
	
	// M�todo para recorrer el c�digo, tomando como bandera algunos car�cteres y almacenar las l�neas de c�digo
	public void recorrerCodigo(String codigo)
	{
		
		String parrafo = "";
		
		for (int i = 0; i < codigo.length(); i++) 
		{
			
			if (codigo.charAt(i) == '{' || codigo.charAt(i) == '}' || codigo.charAt(i) == ';')
			{
				
				parrafo += Character.toString(codigo.charAt(i));
				linea.add(parrafo);
				parrafo = "";
				
			} 
			else
				parrafo += Character.toString(codigo.charAt(i));
		}
		
	}
	
	// M�todo para asignar las variables/datos con las banderas anteriores y otras m�s a una pila
	public void asignarCodigo(String codigo)
	{
		
        String parrafo = ""; // Pedazos del parrafo que se asignan al ArrayList datos
        boolean esVariable = false; // Identifica si se trata de una variable durante la iteraci�n
        int inicioEntero = 0; // Inicio de la subcadena
        boolean inicioBooleano = true; // Inicia para saber si se trata de una variable
        boolean anterior = false; // 
       
		for (int i = 0; i < codigo.length(); i++) 
		{
			
			if (codigo.charAt(i) == '{' || codigo.charAt(i) == '}' || codigo.charAt(i) == ';' || codigo.charAt(i) == ' ' || codigo.charAt(i) == '(' || codigo.charAt(i) == ')')
			{
				
					
						if(anterior == false)
						{
							
							parrafo = codigo.substring(inicioEntero, i);
							datos.add(parrafo);
							parrafo = "";
							
							esVariable = false;	
							inicioBooleano = true;
							
						}
						
						if(codigo.charAt(i) == ';')
						{
							
							parrafo = Character.toString(codigo.charAt(i));
							datos.add(parrafo);
							
						}
						
						if(codigo.charAt(i) == '}')
						{
							
							parrafo = Character.toString(codigo.charAt(i));
							datos.add(parrafo);
							
						}

					anterior = true;
				
			} 
			else
			{
				
				esVariable = true;
				
			}
			
			if(esVariable == true)
			{
				
				if(inicioBooleano == true)
				{
					
					inicioEntero = i;
					
				}
				
				inicioBooleano = false;
				anterior = false;
				
			}
			
		}
		
	}
	
	// M�todo para llenar los ArrayList de tipo de dato, variable, valor, posici�n y alcance
	public void TipoDatosVariablesValoresPosicionesAlcances()
	{
		
		// C�clo para el llenado de Tipo de datos, variables, valores, posiciones y alcances
		for(int i = 0; i < datos.size(); i++)
		{
					
			String anterior = ""; // Apunta atr�s del tipo de dato
			String dato = datos.get(i); // Apunta al tipo de dato
			String siguiente = ""; // Apunta a la variable
			String siguiente2 = ""; // Apunta a la asignaci�n
			String siguiente3 = ""; // Apunta al valor (En caso de que exista)
					
			if(i != 0)
				anterior = datos.get(i - 1);
					
			if(i < datos.size() - 1)
				siguiente = datos.get(i + 1);
					
			if(i < datos.size() - 2)
				siguiente2 = datos.get(i + 2);
					
			if(i < datos.size() - 3)
				siguiente3 = datos.get(i + 3);
					
			if(!anterior.equals("public")) // Si antes del tipo de dato hay diferencia a publico
			{
						
				if(siguiente2.equals("=") && (dato.equals("int") || dato.equals("boolean"))) // Si hay asignacion despu�s de la variable y tipo de dato es entero o booleano
				{

					if(!existe(siguiente)) // Si la variable no existe
					{
									
						int j = i + 3;
						String c = "";
									
						do
						{
										
							siguiente3 = datos.get(j);
										
							if(!siguiente3.equals(";"))
							{
											
								c += siguiente3;
								j++;
											
							}
										
						}while(!siguiente3.equals(";"));
						
						tipoDato.add(dato);
						variable.add(siguiente);

						int p = posicion(siguiente); // Invoca al m�todo posici�n para obtener la posici�n  de la variable
						posicion.add(p);
															
						String tipo = validarAsignacion(siguiente, c, p); // Invoca al m�todo validar para saber si el dato es el correcto HU02
						String tipo2 = "";
						
						System.out.println(tipo);
						
						int indice = validarIndice(siguiente, c); // Invoca al m�todo validar para saber en donde se encuentra el indice de la asignacion incorrecta HU02
						
						String al = alcance(siguiente);	// Invoca al m�todo alcance para saber el alcance de la variable		
						alcance.add(al);
									
						if(tipo.equals("true")) // Valida si el tipo es booleano
						{
										
							valor.add(c);
										
						}
						else
							if(tipo.equals("false")) // Valida si el tipo es booleano
							{
											
								valor.add(c);
											
							}
							else
								if(!tipo.equals("null") && !tipo.contains("HU02. ") && !tipo.contains("HU03. ")) // Valida si el tipo es entero
								{
												
									valor.add(c);
											
								} 
								else
								{
												
									if(dato.equals("int")) // Si el dato es entero se le asigna booleano a tipo2 para llenar la HU02
										tipo2 = "boolean";
									else
										tipo2 = "int"; // Si el dato es booleano se le asigna entero a tipo2 para llenar la HU02
									
									valor.add("null"); // Se agrega nulo a valor debido a que no es del tipo de dato la asignaci�n que se quiso hacer
									
									if(indice >= 0)
									{
										
										tipoDato.remove(indice);
										variable.remove(indice);
										valor.remove(indice);
										posicion.remove(indice);
										alcance.remove(indice);
									}
										
									if(tipo.contains("null"))
									{
										
										listaErroresSemanticos.add("HU02. No se le puede asignar a la variable "+ siguiente + " el tipo de dato "+ tipo2 + " debido a que es "+  dato +" en la posici�n "+ p +".");
										
									}
									else
										if(tipo.contains("HU02. "))
										{
											
											listaErroresSemanticos.add(tipo);
											
										}
										else
											if(tipo.contains("HU03. "))
											{
												
												listaErroresSemanticos.add(tipo);
												
											}
									
								}
									
					}
					else // Si la variable existe
					{
									
						int p = posicion(siguiente); // Obtiene la posici�n de la variable que se est� tratando de usar por segunda vez
						listaErroresSemanticos.add("HU04. La variable "+ siguiente + " est� tratando de ser usada en la l�nea "+ posicionSegunda(siguiente) +" y ya fue declarada en "+ p +".");
									
					}
								
				}
				else
					if(siguiente2.equals("=") && (!dato.equals("int") || !dato.equals("boolean"))) // Si hay asignacion despu�s de la variable y tipo de dato no es entero ni booleano 
					{
								
						if(existe(siguiente))
						{
										
							String tipo = tipoDato.get(i);
										
						//	if(tipo.equals("null"))
											
								tipoDato.add(null);
								variable.add(siguiente);
										
								int j = i + 3;
								String c = "";
										
								do
								{
											
									siguiente3 = datos.get(j);
										
									if(!siguiente3.equals(";"))
									{
												
										c += siguiente3;
										j++;
												
									}
											
								}while(!siguiente3.equals(";"));
										
								int p = posicion(siguiente);			
								posicion.add(p);
								
								String t = validarAsignacion(siguiente, c, p);
								String tipo2 = "";
								
								System.out.println(t);
										
								if(t.equals("true"))
								{
											
									valor.add(c);
											
								}
								else
									if(t.equals("false"))
									{
												
										valor.add(c);
											
									}
									else
										if(!t.equals("null"))
										{
													
											valor.add(c);
													
										}
										else
											{
													
												if(dato.equals("int"))
													tipo2 = "boolean";
												else
													tipo2 = "int";
													
												valor.add("null");
												listaErroresSemanticos.add("HU02. No se le puede asignar a la variable "+ siguiente + " el tipo de dato "+ tipo2 + " debido a que es "+  dato +" en la posici�n "+ p +".");
													
											}
										
									String al = alcance(siguiente);
									alcance.add(al);
										
							}
							else
								{
										
									int p = posicion(siguiente);
									listaErroresSemanticos.add("HU04. La variable "+ siguiente + " est� tratando de ser usada en la l�nea "+ posicionSegunda(siguiente) +" y ya fue declarada en "+ p +".");
									
								}
								
					}
					else
						if(!siguiente2.equals("=") && (dato.equals("int") || dato.equals("boolean"))) // Si no hay asignacion despu�s de la variable y tipo de dato es entero o booleano
						{
					
							if(!existe(siguiente)) // Si variable no existe
							{
										
								tipoDato.add(dato);
								variable.add(siguiente);
								valor.add(null);
								
								
								int p = posicion(siguiente); // Invoca al m�todo posicion para obtener la posici�n			
								posicion.add(p);
										
								String al = alcance(siguiente); // Invoca al m�todo alcance para obtener el alcance		
								alcance.add(al);
										
							}
							else
								{
										
									int p = posicion(siguiente); // Invoca al m�todo posicion para obtener la posici�n de la variable donde quiere ser usada por segunda vez
									listaErroresSemanticos.add("HU04. La variable "+ siguiente + " est� tratando de ser usada en la l�nea "+ posicionSegunda(siguiente) +" y ya fue declarada en "+ p +".");
										
								}
								
								
						 }
						
			}
											
		}
				
	}
	
	public int posicion(String codigo)
	{
		
		// C�clo para el llenado de la posici�n
		String posCadena = "";
		int posEntero = 0;
		boolean yaExiste = false;
		
		for (int i = 0; i < linea.size(); i++) 
		{
			
			posCadena = linea.get(i);
			
			if(posCadena.contains(" "+ codigo+ " ") || posCadena.contains(" "+ codigo +";"))
			{
								
				if(yaExiste == false)
				{
					
					posEntero = i + 1;
					yaExiste = true;
					
				}

				/*if(yaExiste == false)
					yaExiste = true;
				
				if(yaExiste == true)
					posOtras = i + 1;*/
				
			}
			
		}
		
		return posEntero;
		
	}
	
	public int posicionSegunda(String codigo)
	{
		
		// C�clo para el llenado de la posici�n
		String posCadena = "";
		int posPrimera = 0;
		boolean yaExiste = false;
		boolean yaExiste2 = false;
		int veces = 0;
				
		for (int i = 0; i < linea.size(); i++) 
		{
					
			posCadena = linea.get(i);
					
			if(posCadena.contains(" "+ codigo + " "))
			{
										
				if(yaExiste == false)
				{
							
					yaExiste = true;
					veces++;
							
				}

				if(yaExiste == true && veces > 0)
				{
						
					posPrimera = i + 1;
					yaExiste2 = true;
						
				}						
						
			}
				
	}
		
	if(yaExiste2 == true)
	{
			
		return posPrimera;
			
	}
	else
	{
			
		return 0;
			
	}
		
	}
	
	public int posicionExpresion(String sentencia, String operando1, String operador, String operando2)
	{
			
		// C�clo para el llenado de la posici�n de la sentencia
		String posCadena = "";
		int posEntero = 0;
		boolean yaExiste = false;
				
		for (int i = 0; i < linea.size(); i++) 
		{
					
			posCadena = linea.get(i);
					
			if(posCadena.contains(sentencia))
			{
										
				if(yaExiste == false)
				{
							
					posEntero = i + 1;
					yaExiste = true;
							
				}
						
			}
					
		}
				
		return posEntero;
			
	}
	
	public String alcance(String cadena)
	{
		
		// Ciclo para el llenado del alcance
		String al = "";
		boolean local = false;
		
		for(int i = 0; i < datos.size(); i++)
		{
			
			String c = datos.get(i);
			
			if(c.equals("public"))
				local = true;
				
			if(c.equals(cadena) && local == false)
			{
				
				al = "global";
				
				return al;
				
			}
			
		}
		
		if(local == true)
			al = "local";
				
		return al;
		
	}
	
	public boolean existe(String cadena)
	{
		
		boolean existe = false;
		
		for(int i = 0; i < variable.size(); i++)
		{
			
			String dato = variable.get(i);
			
			if(dato.equals(cadena))
				existe = true;
						
		}
		
		if(existe == true)
			return true;
		else
			return false;
		
	}
	
	/*public boolean inicializada(String cadena)
	{
		
		boolean inicializada = false;
		
		for(int i = 0; i < datos.size(); i++)
		{
			
			String siguiente = "";
			
			if(i != datos.size() - 1)
				siguiente = datos.get(i + 1);
			
			if(existe(cadena) == true)
			{
				
				if(siguiente == "=")
					inicializada = true;
				
			}
						
		}
		
		if(inicializada == true)
			return true;
		else
			return false;
		
	}*/
	
	public String validarAsignacion(String cadena, String valor, int p)
	{
		
		int contador = 0;
		
		for(int i = 0; i < variable.size(); i++)
		{
			
			String s = variable.get(i);
			
			if(s.equals(cadena))
				contador = i;
			
		}
		
		String tipo = tipoDato.get(contador);
		
		switch(tipo)
		{
			
		case "boolean":
			if(valor.equals("true"))
			{
				
				return "true";
				
			}
			else
				if(valor.equals("false"))
				{
					
					return "false";
					
				}
				else
				{
					
					if(!existe(valor))
						return "HU03. La variable "+ valor +" se est� queriendo utilizar en la posici�n "+ p + " y no est� definida.";
					else
						return "null";
					
				}
		case "int":

			if(valor.length() != 1)
			{
				
				int cont = 0;
				
				for(int i = 0; i < valor.length(); i++)
				{
					
					String s = Character.toString(valor.charAt(i));
					
					if(valor.charAt(i) == '/' || valor.charAt(i) == '*' || valor.charAt(i) == '-' || valor.charAt(i) == '+')
					{
						
						cont++;
						
					}
					else
						if(esNumerico(s))
						{
							
							cont++;
							
						}
						else
							if(existe(s))
							{
								
								int c2 = 0;
								
								for(int j = 0; j < variable.size(); j++)
								{
									
									String s2 = variable.get(j);
									
									if(s2.equals(cadena))
										c2 = j;
									
								}
								
								String tipo2 = tipoDato.get(contador);
								
								if(tipo2.equals("int"))
									cont++;
								else
									return "HU02. No se le puede asignar a la variable "+ cadena + " el tipo de dato "+ tipo2 + " debido a que es "+  tipo +" en la posici�n "+ p +".";

								
							}
							else
								if(valor.equals("true") || valor.equals("false"))
								{
									
									return "HU02. No se le puede asignar a la variable "+ cadena + " el tipo de dato boolean debido a que es "+  tipo +" en la posici�n "+ p +".";
									
								}
								else
								{
									
									return "HU03. La variable "+ s +" se est� queriendo utilizar en la posici�n "+ p + " y no est� definida.";
									
								}
							
					
				}
								
				if(cont == valor.length())
					return valor;
				else
					return "null";
				
			}
			else
			{
				
				if(esNumerico(valor))
				{
					
					return valor;
					
				}
				else
					if(existe(valor))
					{
						
						int c3 = 0;
						
						for(int j = 0; j < variable.size(); j++)
						{
							
							String s3 = variable.get(j);
							
							if(s3.equals(cadena))
								c3 = j;
							
						}
						
						String tipo3 = tipoDato.get(c3);
						
						if(tipo3.equals("int"))
							return valor;
						else
							return "HU02. No se le puede asignar a la variable "+ cadena + " el tipo de dato "+ tipo3 + " debido a que es "+  tipo +" en la posici�n "+ p +".";
						
					}
					else
					{
						
						return "HU03. La variable "+ valor +" se est� queriendo utilizar en la posici�n "+ p + " y no est� definida.";
						
					}
				
			}
		default:
			return "null";
			
		}
		
	}
	
	public int validarIndice(String cadena, String valor)
	{
		
		int contador = 0;
		
		for(int i = 0; i < variable.size(); i++)
		{
			
			String s = variable.get(i);
			
			if(s.equals(cadena))
				contador = i;
			
		}
		
		String tipo = tipoDato.get(contador);
		
		switch(tipo)
		{
			
		case "boolean":
			if(valor.equals("true"))
			{
				
				return -1;
				
			}
			else
				if(valor.equals("false"))
				{
					
					return -1;
					
				}
				else
				{
					
					return contador;
					
				}
		case "int":
			try 
			{
				
				Integer.parseInt(valor);
				return -1;
				
			} 
			catch (Exception e)
			{
				
				return contador;
			
			}
		default:
			return contador;
			
		}
		
	}
	
	public void validarSentencias()
	{
		
		// C�clo para el llenado de Tipo de datos, variables y valores
		for(int i = 0; i < datos.size(); i++)
		{
			
			String dato = datos.get(i); // Apunta a la sentencia
			String siguiente = ""; // Apunta a la variable
			String siguiente2 = ""; // Apunta al tipo de operador
			String siguiente3 = ""; // Apunta a la variable 2
			
			if(i < datos.size() - 1)
				siguiente = datos.get(i + 1);
			
			if(i < datos.size() - 2)
				siguiente2 = datos.get(i + 2);
			
			if(i < datos.size() - 3)
				siguiente3 = datos.get(i + 3);
			
			if(dato.equals("if") || dato.equals("while")) // Si es un if o while
			{
			
				String operando1 = validarSiEsEnteroBooleano(siguiente); // Invoca al m�todo para validar si es operador u operando
				String operando2 = validarSiEsEnteroBooleano(siguiente3); // Invoca al m�todo para validar si es operador u operando
				boolean operadorValido = validarSiEsAritmeticoLogico(operando1, siguiente2, operando2); // Invoca al m�todo para validar si es aritm�tico o l�gico, regresa true si es v�lido para los dos operandos y false si es inv�lido

				System.out.println(operando1);
				System.out.println(operando2);
				System.out.println(siguiente2);
				
				if(!operadorValido) // Si hay asignacion despu�s de la variable y tipo de dato es entero o booleano
				{
					
					int p = posicionExpresion(dato, siguiente, siguiente2, siguiente3);
					listaErroresSemanticos.add("HU05. No se puede realizar la expresi�n en la l�nea "+ p +", el tipo de dato "+ operando1 +" no corresponde al tipo de dato "+ operando2 +".");
					
					System.out.println("Entr� al if del m�todo");
					
				}
				
			}
			
			//System.out.println("Anetrior: "+ anterior +"\nActual: "+ dato +"\nSiguiente: "+ siguiente +"\nSiguiente2: "+ siguiente2 +"\nSiguiente3: "+ siguiente3 );
			
		}	
				
	}
	
	// M�todo para saber si la variable es entera o booleana
	public String validarSiEsEnteroBooleano(String cadena)
	{			
				
				if(existe(cadena))
				{
					
					int contador = 0;
					
					for(int i = 0; i < variable.size(); i++)
					{
						
						String s = variable.get(i);
						
						if(s.equals(cadena))
							contador = i;
						
					}
					
					return tipoDato.get(contador);
					
				}
				else
				{
					
					if(esNumerico(cadena))
					{
						
						return "int";
						
					}
					if(cadena.equals("true") || cadena.equals("false"))
					{
						
						return "boolean";
						
					}
					else
					{
							
						return "null";
							
					}
					
				}
				
	}
	
	public boolean esNumerico(String cadena)
	{
		
		try
		{
			
			Integer.parseInt(cadena);
			return true;
		}
		
		catch (NumberFormatException nfe)
		{
			
			return false;
		
		}
		
	}
	
	// M�todo para saber si el operador es aritm�tico o l�gico
	public boolean validarSiEsAritmeticoLogico(String operando1, String operador, String operando2)
	{
		
		if(operando1.equals("int") && operando2.equals("int"))
		{
			
			if(operador.equals("<") ||  operador.equals(">") || operador.equals("!=") || operador.equals("==") || operador.equals("<=") || operador.equals("=>"))
			{
				
				return true;
				
			}
			else
			{
				
				return false;
				
			}
			
		}
		else
			if(operando1.equals("boolean") && operando2.equals("boolean"))
			{
				
				if(operador.equals("&&") || operador.equals("!=") || operador.equals("=="))
				{
					
					return true;
					
				}
				else
				{
					
					return false;
					
				}
				
			}
			else
			{
				
				return false;
				
			}
		
	}
	
	// M�todo para el llenado de la tabla de simbolos llenando los distintos ArrayList
	public void imprimirTabla()
	{		
			
			// Objeto TablaSimbolos
			System.out.println("------------------------------------------------------------------------------");
			System.out.println("                             Tabla de simbolos                                    ");
			System.out.println("------------------------------------------------------------------------------");
			System.out.println("  Tipo  |     Variable    |     Valor     |   Posici�n   |      Alcance      ");
			System.out.println("------------------------------------------------------------------------------");
			for(int i = 0; i < variable.size(); i++)
			{
				
				System.out.format("%s\t\t %s\t\t %s \t\t %-10d \t %s", tipoDato.get(i), variable.get(i), valor.get(i), posicion.get(i), alcance.get(i));
				System.out.println();
				
			}
				
	}
	
}