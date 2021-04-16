package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;

import source.RSA;


public class Main {
	
	public static void main (String[] args) {
		
		// --gerarChaves 5 pub.txt priv.txt
		if (args.length == 4 && args[0].equals("--gerarChaves")) {
			gerarChaves(args);			
		// --criptografar priv.txt mensagem.txt criptografado.txt
		} else if (args.length == 4 && args[0].equals("--criptografar")) {
			criptografarMensagem(args);			
		// --descriptografar pub.txt criptografado.txt decifrado.txt
		} else if (args.length == 4 && args[0].equals("--descriptografar")) {
			descriptografarMensagem(args);
		// --bruteForce pub.txt priv.txt criptografado.txt
		} else if (args.length == 4 && args[0].equals("--bruteForce")) {
			atacarBrute(args);
		}
	}	
	
	// Função para geração das chaves publica e privada
	public static void gerarChaves(String[] args){
		System.out.println("Gerando a chave Publica e Privada..."  + "\n");

		RSA rsa = new RSA();
		
		long startTime = System.currentTimeMillis();

		rsa.gerarChaves(Integer.parseInt(args[1]));

		long endTime = System.currentTimeMillis();

		System.out.println("Executou em " + (endTime - startTime) + " milisegundos");
		
		String chavePublica = new String(rsa.getPublicKey().toString());
		String chavePrivada = new String(rsa.getPrivateKey().toString());
		String n = new String(rsa.getN().toString());
		
		String conteudoArquivoPrivada = chavePrivada + "//" + n;
		
		// Escrevendo a chave privada em priv.txt
		String arquivoPrivada = args[2];
		escrevendoArquivo(arquivoPrivada,conteudoArquivoPrivada);
		
		// Escrevendo a chave publica em pub.txt
		String conteudoArquivoPublica = chavePublica + "//" + n;
		String arquivoPublica = args[3];
		escrevendoArquivo(arquivoPublica, conteudoArquivoPublica);
		
		System.out.println("-- CHAVES GERADAS --");
		System.out.println("Chave publica: " + chavePublica);
		System.out.println("Chave privada: " + chavePrivada);

	}
	
	public static void criptografarMensagem(String[] args) {
		System.out.println("Criptografando a mensagem..."  + "\n");
		
		RSA rsa = new RSA();
		
		// priv.txt
		String arquivoChavePrivada = args[1];
		// mensagem.txt
		String arquivoMensagem = args[2];
		
		String chavePrivada = lendoArquivo(arquivoChavePrivada);
	
		// Arquivo da chave privada contém o E e N
		String[] valores = chavePrivada.split("//");
		
		// Setando o N 
		rsa.setN(new BigInteger(valores[1]));

		// Lendo a mensagem a ser enviada
		String mensagem = lendoArquivo(arquivoMensagem);

		// Setando a chave privada
		rsa.setPrivateKey(new BigInteger(valores[0]));
		long startTime = System.currentTimeMillis();

		// Obtendo a mensagem criptografado e escrevendo a mesma em criptografado.txt
		String mensagemCriptografada = new String(rsa.criptografar(new BigInteger(mensagem.getBytes())).toString());
		
		long endTime = System.currentTimeMillis();

		System.out.println("Executou em " + (endTime - startTime) + " milisegundos");
		escrevendoArquivo(args[3], mensagemCriptografada);
		
		System.out.println("-- Mensagem criptografada com sucesso! --");
		System.out.println("Escrita em: " + args[3]);
	}
	
	public static void descriptografarMensagem(String[] args) {
		System.out.println("Descriptografando a mensagem..."  + "\n");
		RSA rsa = new RSA();
		
		// pub.txt
		String arquivoChavePublica = args[1];
		
		// criptografado.txt
		String arquivoMensagemCriptografada = args[2];
		
		String chavePublica = lendoArquivo(arquivoChavePublica);
	
		// Arquivo da chave privada contém o E e N
		String[] valores = chavePublica.split("//");
		
		// Setando o N 
		rsa.setN(new BigInteger(valores[1]));

		// Lendo a mensagem criptografada
		String mensagemCriptografada = lendoArquivo(arquivoMensagemCriptografada);
		
		// Setando o valor de N
		rsa.setPublicKey(new BigInteger(valores[0]));
		long startTime = System.currentTimeMillis();

		// Obtendo a mensagem descriptografada e a escrevendo em decifrado.txt
		String mensagemDescriptografada = new String(rsa.descriptografar(new BigInteger(mensagemCriptografada)).toByteArray());
		
		long endTime = System.currentTimeMillis();

		System.out.println("Executou em " + (endTime - startTime) + " milisegundos");
		escrevendoArquivo(args[3], mensagemDescriptografada);
		
		System.out.println("-- Mensagem descriptografada com sucesso! --");
		System.out.println("Escrita em: " + args[3]);
	}
	
	public static void atacarBrute(String[] args) {
		System.out.println("Atacando a chave privada :) ..."  + "\n");
		RSA rsa = new RSA();
		
		// pub.txt
		String arquivoChavePublica = args[1];
		// priv.txt
		String arquivoChavePrivada = args[2];
		// criptografado.txt
		String arquivoMensagemCriptografada = args[3];
		
		// Lendo o arquivo de ambas as chaves (chave privada será usada apenas para comparação com o resultado final obtido pela quebra da chave publica)
		String chavePublica = lendoArquivo(arquivoChavePublica);
		String chavePrivada = lendoArquivo(arquivoChavePrivada);
		
		String[] valoresChavePublica = chavePublica.split("//");
		String[] valoresChavePrivada = chavePrivada.split("//");
		
		// Setando o N
		rsa.setN(new BigInteger(valoresChavePublica[1]));
		
		// Lendo a mensagem criptografada
		String mensagemCriptografada = lendoArquivo(arquivoMensagemCriptografada);
		
		System.out.println("N: " + valoresChavePublica[1]);
		System.out.println("Chave publica: " + valoresChavePublica[0]);
		System.out.println("Chave privada: " + valoresChavePrivada[0]);
		System.out.println("Mensagem criptografada: " + mensagemCriptografada + "\n");
		
		// Realizando o Brute Force
		long startTime = System.currentTimeMillis();
		rsa.bruteForce(new BigInteger(valoresChavePublica[1]), new BigInteger(valoresChavePublica[0]), new BigInteger(valoresChavePrivada[0]), new BigInteger(mensagemCriptografada));
		long endTime = System.currentTimeMillis();

		System.out.println("Executou em " + (endTime - startTime) + " milisegundos");
		
		System.out.println("-- RSA quebrado com sucesso! --");
	}
		
	public static String lendoArquivo(String nomeArquivo) {
		String conteudo = "";
		try {
			File file 		   	 = new File(nomeArquivo);
			BufferedReader entrada = new BufferedReader(new FileReader(file));
			conteudo 			     = entrada.readLine();
			entrada.close();
		} catch(Exception e) {
			System.out.println("Erro lendo o arquivo: " + nomeArquivo);
		}
		
        return conteudo;		
	}
	
	public static void escrevendoArquivo(String nomeArquivo,String conteudo) {
		try {			
			File arquivo 			  = new File(nomeArquivo);
			BufferedWriter saida 	  = new BufferedWriter(new FileWriter(arquivo));
			
			saida.write(conteudo);
			saida.close();
		} catch(Exception e){
			System.out.println("Erro escrevendo no arquivo");
		}		
	}
	
}
