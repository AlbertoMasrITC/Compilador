package Compilador;

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
	
	// Crea ArrayList para almacenar la posicion en donde se encuentra una expresion compuesta para aplicar el c�digo intermedio
	ArrayList <Integer> posicionExpresionCompuesta = new ArrayList<Integer>();
		
	// Constructor que invoca cada uno de los m�todos del programa
	public AnalizadorSemantico(String URL)
	{
		
		String codigo = leerArchivo(URL); // Obtiene el c�digo que se ley� del archivo
		recorrerCodigo(codigo); // Recorree el c�digo para obtener las l�neas
		asignarCodigo(codigo); // Asigna el c�digo a una pila de datos
		TipoDatosVariablesValoresPosicionesAlcances(); // M�todo para llenar los arraylist y con ello la tabla de s�mbolos, tambi�n determina el analisis sem�ntico
		validarSentencias(); // M�todo para determinar la correcta funci�n e los operando-operaciones en las sentenicas
		
		System.out.println();
		
		// Imprime l�nea por l�nea con su respectivo rengl�n
		int c = 0;
		for(int i = 0; i < linea.size(); i++)
		{
			
			c++;
			System.out.println(c +".- "+ linea.get(i));
			
		}
		
		System.out.println();
		
		imprimirTabla(); // Imprime la tabla de s�mbolos
		
		System.out.println();
		
		// Imprime los errores sem�nticos encontrados
		for(String s: listaErroresSemanticos)
			System.err.println(s);
		
		System.out.println();
		
		codigoIntermedio(); // M�todo para determinar el c�digo intermedio y mostrarlo
		
	}
	
	// M�todo para leer un archivo desde la URL
	public static String leerArchivo(String URL)
	{
		
		String parrafo = ""; // Parrafos que pasar�n a formar parte del texto
		String texto = ""; // Texto que regresar� el m�todo como URL
		
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
		
		String parrafo = ""; // Obtiene una l�nea de parrafo que pasar� a ser parte del arraylist l�nea, toma como bandera "{", "}" y ";"
		
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
									
						int j = i + 3; // Apunta al valor (Por si es un valor compuesto)
						String c = ""; // Obtiene la cadena de un valor compuesto
									
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
							
							int contador = 0;
										
							for(int j = 0; j < variable.size(); j++)
							{
								
								if(siguiente.equals(variable.get(j)))
										contador = j;
										
								
							}
											
										
							int j = i + 3; // Apunta al valor (Por si es un valor compuesto)
							String c = ""; // Obtiene la cadena de un valor compuesto
										
							do
							{
											
								siguiente3 = datos.get(j);
											
								if(!siguiente3.equals(";"))
								{
												
									c += siguiente3;
									j++;
												
								}
											
							}while(!siguiente3.equals(";"));
										
							int p = posicionSegunda(siguiente); // Invoca al m�todo posici�n para obtener la posici�n  de la variable
							posicion.add(p);
																
							String tipo = validarAsignacion(siguiente, c, p); // Invoca al m�todo validar para saber si el dato es el correcto HU02
							String tipo2 = "";
																		
							int indice = validarIndice(siguiente, c); // Invoca al m�todo validar para saber en donde se encuentra el indice de la asignacion incorrecta HU02
							
							String al = alcance(siguiente);	// Invoca al m�todo alcance para saber el alcance de la variable		
							alcance.add(al);
										
							if(tipo.equals("true")) // Valida si el tipo es booleano
							{
											
								valor.set(contador, c);
											
							}
							else
								if(tipo.equals("false")) // Valida si el tipo es booleano
								{
												
									valor.set(contador, c);
												
								}
								else
									if(!tipo.equals("null") && !tipo.contains("HU02. ") && !tipo.contains("HU03. ")) // Valida si el tipo es entero
									{
													
										valor.set(contador, c);
												
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
							else
								{
								
									int p = posicion(siguiente);
									listaErroresSemanticos.add("HU03. La variable "+ siguiente +" se est� queriendo utilizar en la posici�n "+ p + " y no est� definida.");
									
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
	
	// M�todo para encontrar la primera posici�n de la expresi�n
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
				
			}
			
		}
		
		return posEntero;
		
	}
	
	// M�todo para encontrar la segunda posici�n de la expresi�n
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
	
	// M�todo para saber la posici�n de la expresi�n
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
	
	// M�todo para saber el alcance de la variable
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
	
	// M�todo para saber si una variable existe
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
	
	// M�todo para validar si la asignaci�n es correcta o incorrecta, tambi�n eval�a las asignaciones compuestas
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
			
			boolean esCompuesto = false;
			
			for(int i = 0; i < valor.length(); i++)
			{
				
				if(valor.charAt(i) == '/' || valor.charAt(i) == '*' || valor.charAt(i) == '-' || valor.charAt(i) == '+')
					esCompuesto = true;
				
			}

			if(esCompuesto ==  true)
			{
				
				String parrafo = "";
				boolean esValor = false;
				boolean inicioBooleano = true;
				int inicioEntero = 0;
				boolean anterior = false;
				boolean comparar = false;
				
				for(int i = 0; i < valor.length(); i++)
				{
										
					if((valor.charAt(i) == '/' || valor.charAt(i) == '*' || valor.charAt(i) == '-' || valor.charAt(i) == '+') || i == valor.length() - 1)
					{
																		
						if(i == valor.length() - 1)
						{

								
								if(anterior == true)
								{
									
									parrafo = Character.toString(valor.charAt(i));
									
								}
								else
								{
									
									parrafo = valor.substring(inicioEntero, i + 1);
									
								}								
														
						}
						else
						{
							
							if(anterior == false)
							{
								
								parrafo = valor.substring(inicioEntero, i);
																								
							}
							
						}
						
						esValor = false;
						inicioBooleano = true;
						comparar = true;	
						anterior = true;
						
					}
					else
						{
							
							esValor = true;
							
						}
						
						if(esValor == true)
						{
							
							if(inicioBooleano == true)
							{
								
								inicioEntero = i;
								
							}
							
							inicioBooleano = false;
							anterior = false;
							
						}
						
						if(comparar == true)
						{
							
							comparar = false;
														
							if(ValidarSiEsNumerico(parrafo))
							{
								
								
							}
							else
								if(existe(parrafo))
								{
																		
									int c2 = 0;
									
									for(int j = 0; j < variable.size(); j++)
									{
										
										String s2 = variable.get(j);
										
										if(s2.equals(parrafo))
											c2 = j;
																				
									}
									
									String tipo2 = tipoDato.get(c2);
									
									if(!tipo2.equals("int"))
										return "HU02. No se le puede asignar a la variable "+ cadena + " el tipo de dato "+ tipo2 + " debido a que es "+  tipo +" en la posici�n "+ p +".";
									
								}
								else
									if(parrafo.equals("true") || parrafo.equals("false"))
									{
										
										return "HU02. No se le puede asignar a la variable "+ cadena + " el tipo de dato boolean debido a que es "+  tipo +" en la posici�n "+ p +".";
																				
									}
									else
									{
										
										return "HU03. La variable "+ parrafo +" se est� queriendo utilizar en la posici�n "+ p + " y no est� definida.";
										
									}
							
						}
					
				}
				
				System.out.println("Hay una expresi�n compuesta en la linea "+ p);
				posicionExpresionCompuesta.add(p); // Agrega la posicion de la linea en donde hay una expresi�n compuesta
				return valor;
				
			}
			else
			{
				
				if(ValidarSiEsNumerico(valor))
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
						if(valor.equals("true") || valor.equals("false"))
						{
								
							return "HU02. No se le puede asignar a la variable "+ cadena + " el tipo de dato boolean debido a que es "+  tipo +" en la posici�n "+ p +".";
																		
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
	
	// M�todo para validar el indice de la asignaci�n incorrecta
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
	
	// M�todo para validar la sentencia
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
				
				if(!operadorValido) // Si hay asignacion despu�s de la variable y tipo de dato es entero o booleano
				{
					
					int p = posicionExpresion(dato, siguiente, siguiente2, siguiente3);
					listaErroresSemanticos.add("HU05. No se puede realizar la expresi�n en la l�nea "+ p +", el tipo de dato "+ operando1 +" no corresponde al tipo de dato "+ operando2 +".");
					
					System.out.println("Entr� al if del m�todo");
					
				}
				
			}
						
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
					
			if(ValidarSiEsNumerico(cadena))
			{
						
				return "int";
					
			}
			else
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
	
	public boolean ValidarSiEsNumerico(String cadena)
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
	
	// M�todo para llevar acabo el c�digo intermedio
	public void codigoIntermedio()
	{
		
		for(int i = 0; i < posicionExpresionCompuesta.size(); i++)
		{
			
			String expresionCompuesta = linea.get(posicionExpresionCompuesta.get(i) - 1);
			
			String parrafo = ""; // Pedazos del parrafo que se asignan al ArrayList datos
	        boolean esVariable = false; // Identifica si se trata de una variable durante la iteraci�n
	        int inicioEntero = 0; // Inicio de la subcadena
	        boolean inicioBooleano = true; // Inicia para saber si se trata de una variable
	        boolean anterior = false; // 
	        
	        // Crea un arreglo que almacenar� temporalmente las partes de la expresi�n compuesta
	        ArrayList<String> arregloExpresionCompuesta = new ArrayList<String>();
	       
	        // Arreglo que recorre el rengl�n en el que se encontr� la expresi�n compuesta para poder meter el c�digo en una pila y su posterior uso
			for (int j = 0; j < expresionCompuesta.length(); j++) 
			{
				
				if (expresionCompuesta.charAt(j) == '{' || expresionCompuesta.charAt(j) == '}' || expresionCompuesta.charAt(j) == ';' || expresionCompuesta.charAt(j) == ' ' || expresionCompuesta.charAt(j) == '(' || expresionCompuesta.charAt(j) == ')')
				{
					
						
							if(anterior == false)
							{
								
								parrafo = expresionCompuesta.substring(inicioEntero, j);
								arregloExpresionCompuesta.add(parrafo);
								parrafo = "";
								
								esVariable = false;	
								inicioBooleano = true;
								
							}
							
							if(expresionCompuesta.charAt(j) == ';')
							{
								
								parrafo = Character.toString(expresionCompuesta.charAt(j));
								arregloExpresionCompuesta.add(parrafo);
								
							}
							
							if(expresionCompuesta.charAt(j) == '}')
							{
								
								parrafo = Character.toString(expresionCompuesta.charAt(j));
								arregloExpresionCompuesta.add(parrafo);
								
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
						
						inicioEntero = j;
						
					}
					
					inicioBooleano = false;
					anterior = false;
					
				}
				
			}
			
			// String que almacena la variable para su posterior uso
			String variable = "";
			
			// Crea ArrayList de los operandos y operador
			ArrayList<String> operando1 = new ArrayList<String>();
			ArrayList<String> operador = new ArrayList<String>();
			ArrayList<Integer> indiceOperador = new ArrayList<Integer>(); // ArrayList para almacenar el indice de los operadores
			ArrayList<String> operando2 = new ArrayList<String>();
			
			// Contador para determinar el indice de los operadores
			int contadorIndiceOperador = 0;
			
			// Arreglo que recorre la pila de la expresi�n compuesta para almacenar los operandos y operador.
			for(int j = 0; j < arregloExpresionCompuesta.size(); j++)
			{
				
				System.out.println(arregloExpresionCompuesta.get(j));
				
				String anterior2 = ""; // Apunta atr�s de la variable/operador
				String dato = arregloExpresionCompuesta.get(j); // Apunta a la variable/operador
				String siguiente = ""; // Apunta adelante da la variable/operador
				
				if(j != 0)
					anterior2 = arregloExpresionCompuesta.get(j - 1);
						
				if(j < arregloExpresionCompuesta.size() - 1)
					siguiente = arregloExpresionCompuesta.get(j + 1);
				
				// Si detr�s de la variable hay un tipo de dato o despu�s hay una igualdad almacena el nombre de la variable
				if(anterior2.equals("int") || siguiente.equals("="))
				{
					
					variable = dato;
					
				}
				
				// Si el operador es alguno de estos simbolos matem�ticos almacena el operando 1 y operando 2 en sus arreglos correspondientes
				if(dato.equals("/") || dato.equals("*") || dato.equals("-") || dato.equals("+"))
				{
					
					contadorIndiceOperador++;
					
					operando1.add(anterior2);
					operador.add(dato);
					indiceOperador.add(contadorIndiceOperador);
					operando2.add(siguiente);
										
				}
				
			}
			
			for(int j = 0; j < operador.size(); j++)
			{
				
				System.out.println(variable +" "+ operando1.get(j) +" "+ "("+ indiceOperador.get(j) +") "+ operador.get(j) +" "+ operando2.get(j));
				
			}
			
			// Arreglo que define la jerarqu�a de los operadores
			for(int j = 0; j < operador.size(); j++)
			{
				
				// Arreglo auxiliar para acomodar la precedencia de los operadores
				ArrayList<String> arregloAuxiliarOperando1 = new ArrayList<String>();
				
				// Arreglo auxiliar para acomodar la precedencia de los operadores
				ArrayList<String> arregloAuxiliarOperador = new ArrayList<String>();
				
				// Arreglo para dar indice a los operadores
				ArrayList<Integer> arreloAuxiliarIndiceOperador = new ArrayList<Integer>();
				
				// Arreglo auxiliar para acomodar la precedencia de los operadores
				ArrayList<String> arregloAuxiliarOperando2 = new ArrayList<String>();
				
				String datoOperando1 = operando1.get(j); // Apuntado al operador
				String siguienteOperando1 = ""; // Apunta adelante dal operador
				
				if(j < operando1.size() - 1)
					siguienteOperando1 = operando1.get(j + 1);
				
				String datoOperador = operador.get(j); // Apuntado al operador
				String siguienteOperador = ""; // Apunta adelante dal operador
				
				if(j < operador.size() - 1)
					siguienteOperador = operador.get(j + 1);
				
				int datoIndiceOperador = indiceOperador.get(j); // Apuntado al indice del operador
				int siguienteIndiceOperador = 0; // Apunta adelante dal indice del operador operador
				
				if(j < operador.size() - 1)
					siguienteIndiceOperador = indiceOperador.get(j + 1);
				
				String datoOperando2 = operando2.get(j); // Apuntado al operador
				String siguienteOperando2 = ""; // Apunta adelante dal operador
				
				if(j < operando2.size() - 1)
					siguienteOperando2 = operando2.get(j + 1);
				
				// Si el operador es igual a "-" � "+"
				if(datoOperador.equals("-") || datoOperador.equals("+"))
				{
					
					// Si la siguiente posici�n al operador es "/" � "*"
					if(siguienteOperador.equals("/") || siguienteOperador.equals("*"))
					{
						
						// Intercambia el operando1 con mayor prioridad con el de menor
						arregloAuxiliarOperando1.add(siguienteOperando1);
						String auxOperando1 = arregloAuxiliarOperando1.get(0);
						operando1.set(j + 1, datoOperando1);
						operando1.set(j, auxOperando1);
						
						// Intercambia el operador con mayor prioridad con el de menor
						arregloAuxiliarOperador.add(siguienteOperador);
						String auxOperador = arregloAuxiliarOperador.get(0);
						operador.set(j + 1, datoOperador);
						operador.set(j, auxOperador);
						
						// Intercambia el indice del operador dependiendo de los cambios del operador con mayor prioridad con el de menor
						arreloAuxiliarIndiceOperador.add(siguienteIndiceOperador);
						int auxIndiceOperador = arreloAuxiliarIndiceOperador.get(0);
						indiceOperador.set(j + 1, datoIndiceOperador);
						indiceOperador.set(j, auxIndiceOperador);
						
						// Intercambia el operando1 con mayor prioridad con el de menor
						arregloAuxiliarOperando2.add(siguienteOperando2);
						String auxOperando2 = arregloAuxiliarOperando2.get(0);
						operando2.set(j + 1, datoOperando2);
						operando2.set(j, auxOperando2);
						
						j = -1;
						
					}
					
				}
				
			}
			
			System.out.println();
			
			for(int j = 0; j < operador.size(); j++)
			{
				
				System.out.println(variable +" "+ operando1.get(j) +" "+ "("+ indiceOperador.get(j) +") "+ operador.get(j) +" "+ operando2.get(j));
				
			}
									
			System.out.println("------------------------------------------------------------------------------");
			System.out.println("                             C�digo intermedio #"+ (i+1) +"                                  ");
			System.out.println("------------------------------------------------------------------------------");
			System.out.println(expresionCompuesta);
			System.out.println("------------------------------------------------------------------------------");
			System.out.println("   Resultado    |      Operando 1     |    Operador   |       Operando 2      ");
			System.out.println("------------------------------------------------------------------------------");
			
		}
		
	}
	
}