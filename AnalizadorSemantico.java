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
	ArrayList<String> posicion = new ArrayList<String>();
	ArrayList<String> valor = new ArrayList<String>();
	ArrayList<String> alcance = new ArrayList<String>();
	
	// Crea ArrayList para almacenar los errores
	ArrayList <String> listaErroresSemanticos = new ArrayList<String>();
	
	String codigoGlobal = "";
	
	// Constructor que invoca cada uno de los métodos del programa
	public AnalizadorSemantico(String URL)
	{
		
		String codigo = leerArchivo(URL);
		codigoGlobal = codigo;
		recorrerCodigo(codigo);		
		asignarCodigo(codigo);
		
		/*for(int i = 0; i < codigo.length(); i++)
			System.out.println(codigo.charAt(i));*/
		
		for(int i = 0; i < datos.size(); i++)
			System.out.println(datos.get(i));
		
		llenadoTipoDatos();
		
	}
	
	// Método para leer un archivo desde la URL
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
	
	// Método para recorrer el código, tomando como bandera algunos carácteres
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
	
	// Método para asignar el código sin las banderas anteriores a una pila
	public void asignarCodigo(String codigo)
	{
		
        String parrafo = ""; // Pedazos del parrafo que se asignan al ArrayList datos
        boolean esVariable = false; // Identifica si se trata de una variable durante la iteración
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
	
	// Método para el llenado de la tabla de simbolos llenando los distintos ArrayList
	public void crearTablaSimbolos()
	{		
		
		
		
	}
	
	public void llenadoTipoDatos()
	{
		
		// Cíclo para el llenado de Tipo de datos
				for(int i = 0; i < datos.size(); i++)
				{
					
					String anterior = ""; // Apunta atrás del tipo de dato
					String dato = datos.get(i); // Apunta al tipo de dato
					String siguiente = ""; // Apunta a la variable
					String siguiente2 = ""; // Apunta a la asignación
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
						
						if(siguiente2.equals("=") && (dato.equals("int") || dato.equals("boolean"))) // Si hay asignacion después de la variable y tipo de dato es entero o booleano
						{
							
							if(!existe(siguiente))
							{
								
								tipoDato.add(dato);
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
								
								valor.add(c);
								
								String al = alcance(siguiente);
								
								alcance.add(al);
								
							}
							else
							{
								
								listaErroresSemanticos.add("HU03. La variable "+ siguiente + "está tratando de ser usada y no existe");
								
							}
							
							
							
						}
						else
							if(siguiente2.equals("=") && (!dato.equals("int") || !dato.equals("boolean"))) // Si hay asignacion después de la variable y tipo de dato no es entero ni booleano 
							{
								
								if(!existe(siguiente))
								{
									
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
									
									valor.add(c);
									
									String al = alcance(siguiente);
									
									alcance.add(al);
									
								}
								else
								{
									
									listaErroresSemanticos.add("HU03. La variable "+ siguiente + "está tratando de ser usada y no existe");
									
								}
								
								
							}
							else
								if(!siguiente2.equals("=") && (dato.equals("int") || dato.equals("boolean"))) // Si no hay asignacion después de la variable y tipo de dato es entero o booleano
								
									if(!existe(siguiente))
									{
										
										tipoDato.add(null);
										variable.add(siguiente);
										valor.add(null);
										
										String al = alcance(siguiente);
										
										alcance.add(al);
										
									}
									else
									{
										
										listaErroresSemanticos.add("HU03. La variable "+ siguiente + "está tratando de ser usada y no existe");
										
									}
								
						}
					
						//System.out.print("Anterior: "+ anterior +"\n"+ "Actual: "+ dato+"\n"+ "Siguiente: "+ siguiente +"\n"+ "Siguiente2: "+ siguiente2 +"\n");
						
					}
				
				for(String s: tipoDato)
					System.out.println(s);
				
				for(String s: variable)
					System.out.println(s);
				
				for(String s: valor)
					System.out.println(s);
				
				for(String s: alcance)
					System.out.println(s);
	}
	
	/*public void llenadoPosiciones()
	{
		
		// Cíclo para el llenado de la posición
		
		
	}*/
	
	public String alcance(String cadena)
	{
		
		// Ciclo para el llenado del alcance
		
		String al = "";
		boolean local = false;
		boolean global = false;
		
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
	
	/*public String posicion(String codigo)
	{
		
		String parrafo = "";
        boolean esVariable = false;
        int inicioEntero = 0;
        boolean inicioBooleano = true;
        boolean anterior = false;
       
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
		
	}*/
	
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
	
	public boolean inicializada(String cadena)
	{
		
		boolean inicializada = false;
		
		for(int i = 0; i < datos.size(); i++)
		{
			
			String siguiente = "";
			
			if(i != datos.size())
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
		
	}
	
}

/*public void llenadoVariables()
{
	
	//Cíclo para llenar las variables		
			for(int i = 0; i < datos.size(); i++)
			{
				
				String anterior = "";
				String anterior2 = "";
				
				if(i != 0)
					anterior = datos.get(i - 1);
				
				if(i != 0 && i != 1)
					anterior2 = datos.get(i - 2);
				
				String dato = datos.get(i);
				
				if(!anterior2.equals("public"))
				{
						
						if(anterior.equals("int") || anterior.equals("boolean") )
						{
								
							if(!existe(dato))
							{
								
								variable.add(dato);
								System.out.print("No existe");
								//llenadoValores(dato);
								//llenadoPosicion(dato);
								//llenadoAlcance(dato);
								
							}
							
						}
					
				}
			
				//System.out.print("Anterior2: " + anterior2 + "\n" + "Anterior: "+ anterior + "\n" +"Actual: "+ dato +"\n");
				
			}
			
		for(String s: variable)
			System.out.println(s);
	
}*/

/*public void llenadoValores(String cadena)
{
	
	// Ciclo para el llenado del valor	
	
	boolean esValor = false;
	boolean primeraVez = true;
	String valors = "";
	
	for(int i = 0; i < datos.size(); i++)
	{
	
		String dato = datos.get(i);
		
		String anterior = "";
		String siguiente = "";
		
		if(i != 0)
			anterior = datos.get(i - 1);
		
		if(i < datos.size() - 1)
			siguiente = datos.get(i + 1);
		
		if(anterior.equals(cadena) || esValor == true)
		{
			
			if(dato.equals("=") || esValor == true)
			{
				
				esValor = true;
				
				if(primeraVez == true)
				{
					
					primeraVez = false;
					
				}
				else										
					if(siguiente.equals("/") || siguiente.equals("*") || siguiente.equals("-") || siguiente.equals("+"))
					{
						
						valors += dato;

						
					}
					else
						if(anterior.equals("/") || anterior.equals("*") || anterior.equals("-") || anterior.equals("+") || anterior.equals("="))
						{
							
							valors += dato;
							valor.add(valors);
							valors = "";
							esValor = false;
							primeraVez = true;
							
						}
				
			}
			
		}
		
		//System.out.print("Anterior: "+ anterior +"\n"+ "Actual: "+ dato +"\n"+ "Siguiente: "+ siguiente +"\n");
	
	}
	
	for(String s: valor)
		System.out.println(s);
				
}*/