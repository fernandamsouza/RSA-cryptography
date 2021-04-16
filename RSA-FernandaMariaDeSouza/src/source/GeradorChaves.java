package source;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;



public class GeradorChaves {
	
	public static final BigInteger zero = BigInteger.ZERO;
	public static final BigInteger one = BigInteger.ONE;
	public static final BigInteger two = new BigInteger("2");
	public static final BigInteger three = new BigInteger("3");
	public static final BigInteger four = new BigInteger("4");

	private final int[] vetorPrimos = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};
	
	
	public BigInteger getPrimo(int seed_length) {
        BigInteger primo;
        
        // Geração de um primo aleatorio
        while (true) {
            primo = new BigInteger(this.getSeed(seed_length));
            if (primo.mod(four).equals(three)) {
                break;
            }
        }

        return primo;
    }
	
	public BigInteger getN(int seed_length, BigInteger p, BigInteger q) {
		// p e q devem ser diferentes
        while (p.equals(q)) 
            q = this.getPrimo(seed_length);
        
        return p.multiply(q);
    }
	
	// Geração de um numero aleatorio do tamanho solicitado
	public BigInteger getNumeroAleatorio(int seed_length) {
		
        BigInteger numeroAleatorioTestado;
        BigInteger numeroAleatorio = new BigInteger(this.getSeed(seed_length));
        BigInteger p = this.getPrimo(seed_length);
        BigInteger q = this.getPrimo(seed_length);

        // Enquanto o numero aleatorio for par, são gerados outros valores para p e q
        while (numeroAleatorio.mod(p).equals(zero)) {
            p = this.getPrimo(seed_length);
            q = this.getPrimo(seed_length);
        }
        
        BigInteger N = this.getN(seed_length, p, q); 

        numeroAleatorioTestado = (numeroAleatorio.multiply(numeroAleatorio)).mod(N);
        
        return numeroAleatorioTestado;
    }
	
	// É gerado um n aleatório primo de acordo com o tamanho passado como parametro
	public BigInteger getPrimoAleatorio(int numeroDigitos, int certeza) {
        BigInteger primoAleatorio;
      
        // Testando primos aleatorios para verificar se passam no teste de Miller Rabin
        while (true) {
            primoAleatorio = this.getNumeroAleatorio(numeroDigitos);
            
            // Se o primoAleatorio não passar pelo teste, o mesmo com certeza é um número composto. 
            // Se o número passar no teste, ele é considerado primo.
            if (this.primalidadeMillerRabin(primoAleatorio, certeza)) {
                break;
            }
        }
        
        return primoAleatorio;
    }
	
	public boolean verificacaoFormula(BigInteger primoAleatorio, BigInteger primoVetor, int aux, BigInteger primoSub) {
        for (int i = 0; i < aux; i++) {
        	// resultExp = 2^aux
            BigInteger resultExp = two.pow(i);
            
            // resultExp = 2^aux * primoSub
            resultExp = resultExp.multiply(primoSub);
            
            // result = primoVetor ^ ( 2^aux * primoSub) mod primoAleatorio
            BigInteger result = primoVetor.modPow(resultExp, primoAleatorio);
            
            // Caso o result seja 1 ou existir um inteiro (result) tal que primoVetor^( 2^aux * primoSub ) mod primoAleatorio = primoAleatorio-1, o numero é aprovado como primo
            if (result.equals(primoAleatorio.subtract(one)) || result.equals(one)) {
                return true;
            }
        }
        
    	// Nao foi aprovado como primo.
        return false;
	}
	
	// Teste de primalidade de Miller-Rabin
	// Dado "primoAleatorio" um numero inteiro primo e A um inteiro qualquer, tal que: 1 < A < N
	// Encontra-se: 2^S | (n-1) e 
	// Complexidade de O(k log² n), sendo k o numero de iteracoes.
	public boolean primalidadeMillerRabin(BigInteger primoAleatorio, int certeza) { 
		
		// Retorna um BigInteger cujo valor é (primoAleatorio - 1)
        BigInteger primoSub = primoAleatorio.subtract(one);                          
        
        int aux = 0;
        
        // Enquanto o resto da divisão do número for igual a 0.
        // A cada iteracao, esse numero é dividido por 2.
        // primoSub = d = (n-1)/2^S
        // Objetivo: primoSub = d = ser ímpar.
        while (primoSub.mod(two).equals(zero)) {
            aux++;
            primoSub = primoSub.divide(two);
        }
        
    	// Por ser um algoritmo probabilistico, a certeza é testada 5 vezes.
        for (int i = 0; i < certeza; i++) {      
            BigInteger primoVetor = BigInteger.valueOf(vetorPrimos[i]);  
            
            boolean aprovado = this.verificacaoFormula(primoAleatorio, primoVetor, aux, primoSub);
            
            if (!aprovado) {
                return false;
            }
        }
        
        return true;
    }
	
	// Gerador de numero verdadeiramente randomico, usando /dev/urandom
	public String getSeed(int seed_length) {
		try {
	        
	        File file = new File("/dev/urandom");
	        InputStream is = new FileInputStream(file);
	
	        byte[] bytes = new byte[seed_length];
	
	        is.read(bytes);
	        is.close();
	
	        String s = new String();
	
	        for (int i = 0; i < bytes.length; i++) {
	            s += ((int) bytes[i] & 0xFF);
	        }
	
	        return s;
	
	    } catch (Exception e) {
	        System.out.println("Error getting the seed!");
	        return "ERROR";
	    }
	}
}
