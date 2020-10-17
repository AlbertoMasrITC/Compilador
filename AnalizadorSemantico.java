package Prueba;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class AnalizadorSemantico 
{	
	
	ArrayList<String> linea = new ArrayList<String>();
	ArrayList <String> listaErroresSemanticos = new ArrayList<String>();
	HashMap<String, TablaSimbolos> tablaSimbolos = new HashMap<String, TablaSimbolos>();
	ArrayList<String> variables = new ArrayList<>();
	
	ArrayList<String> pilaDatos = new ArrayList<>();
	
	public AnalizadorSemantico(String URL)
	{
		
		String codigo = leerArchivo(URL);
		recorrerCodigo(codigo);		
		asignarCodigo(codigo);
		
		int c = 0;
		
		for(String s: pilaDatos)
		{
			
			c++;
			
			System.out.println(c + ".- "+ s);
			
		}
		
	}
	
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
	
	public void asignarCodigo(String codigo)
	{
		
        String parrafo = "";
        boolean esVariable = false;
        int inicioEntero = 0;
        boolean inicioBooleano = true;
        boolean anterior = false;
        
       /*for(int i = 0; i < codigo.length(); i++) 
        {
        	    	   
        	if (codigo.charAt(i) == '{' || codigo.charAt(i) == '}' || codigo.charAt(i) == ';' || codigo.charAt(i) == '(' || codigo.charAt(i) == ')' || codigo.charAt(i) == ' ' )
			{
	
        		
        			
        	
			}
        	else
        	{
        		
        		
        		
        	}
        	
        	System.out.println(codigo.charAt(i));
        	
        }*/
       
		for (int i = 0; i < codigo.length(); i++) 
		{
			
			if (codigo.charAt(i) == '{' || codigo.charAt(i) == '}' || codigo.charAt(i) == ';' || codigo.charAt(i) == ' ' || codigo.charAt(i) == '(' || codigo.charAt(i) == ')')
			{
				
					if(anterior == false)
					{
						
						parrafo = codigo.substring(inicioEntero, i);
						pilaDatos.add(parrafo);
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
		
	}
	
}