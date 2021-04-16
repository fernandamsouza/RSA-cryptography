package source;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class RSA {
	private BigInteger chavePrivada;
	private BigInteger chavePublica;
	private BigInteger n;
	
	private GeradorChaves gerador;
	
	public static final BigInteger zero = BigInteger.ZERO;
	public static final BigInteger one  = BigInteger.ONE;
	public static final BigInteger two  = new BigInteger("2");

	
	public RSA () {
		this.gerador = new GeradorChaves();		
	}
	
	public RSA (int numberOfBytes) {
		this.gerador = new GeradorChaves();
		this.gerarChaves(numberOfBytes);
	}

	public BigInteger gcdExtendido(BigInteger totiente, BigInteger chavePublica) {
		if(chavePublica.subtract(BigInteger.ONE).signum() < 0)
			return totiente;
		else
			return gcdExtendido(chavePublica, totiente.mod(chavePublica));
	}

	
	// Inverso Modular para geração da chave privada
	// Usando o algoritmo de Euclides Estendido
	// D = private key, que é o inverso multiplicativo de E mod totiente 
	// D * E = 1 mod totiente
	// a = chave publica
	// b = totiente
	public BigInteger inversoModular(BigInteger chavePublica, BigInteger totiente) {	
		
		BigInteger b0 = totiente, t, q;
		BigInteger x0 = zero, chavePrivada = one;
		
		// Se totiente = 1, retorna 1;
		if (totiente.equals(one)) { 
			return one;
		}
		
		// Enquanto a chave publica - 1 for maior que 0
		while (chavePublica.subtract(one).signum() > 0) {
			q 				= chavePublica.divide(totiente);
			t 				= totiente; 
			totiente 		= chavePublica.mod(totiente); 
			chavePublica 	= t;
			t 				= x0; 
			x0 			    = chavePrivada.subtract(q.multiply(x0)); 
			chavePrivada 	= t;
		}
		
		// Se a chave privada, for menor que 0, é somado a chavePrivada o valor de totiente (b0)
		if (chavePrivada.signum() < 0) {
			chavePrivada = chavePrivada.add(b0);
		}
		
		// Se retorna a chave privada final
		return chavePrivada;
	}
	
	// CRIPTOGRAFANDO
	// a = message
	// b = this.privateKey
	// c = n
	
	// DESCRIPTOGRAFANDO
	// a = message
	// b = this.publicKey
	// c = n
	public BigInteger potenciaModular (BigInteger mensagem, BigInteger key, BigInteger n) {
		// Encriptando:    C = M^E mod N
		// Desencriptando: M = C^D mod N
		
		if(key.compareTo(zero) == -1) {
			return this.potenciaModular(this.inversoModular(mensagem, n), key.negate(), n);
		}
		
		if(key.equals(one)) {
			return this.inversoModular(mensagem, n);
		}
		
		BigInteger result   			  = one;
		BigInteger mensagemTemporaria     = mensagem;
		BigInteger chaveTemporaria 	      = key;
		
		// Enquanto a chave privada/publica for diferente de 0
		while(!chaveTemporaria.equals(zero)) {
			
			// Se o resto da divisão da chave privada/publica por 2 for diferente de 0 
			if (!chaveTemporaria.remainder(two).equals(zero)) {
				// result % n * (M % n) % n
				result = (result.remainder(n).multiply(mensagemTemporaria.remainder(n)).remainder(n)); 
			}	
			// m % n * (m % n) % n
			mensagemTemporaria = mensagemTemporaria.remainder(n).multiply(mensagemTemporaria.remainder(n)).remainder(n);
			
			// A cada iteracao a chave publica/privada é dividida por 2;
			chaveTemporaria = chaveTemporaria.divide(two);
		}
		
		// Resultado final é o arquivo criptografado/descriptografado
		return result;
		// Uma vez que o texto for criptografado com a chave pública, apenas a chave privada conseguirá decriptografar!
	}
    
    private static BigInteger polyMod(BigInteger x, BigInteger n) {
        return x.pow(2).add(BigInteger.ONE).mod(n);
    }
	
	public void gerarChaves(int numeroDigitos) {
		
		// p e q gerados aleatoriamente por meio do teste de primalidade de miller-rabin
        BigInteger p = this.gerador.getPrimoAleatorio(numeroDigitos, 5);
        BigInteger q = this.gerador.getPrimoAleatorio(numeroDigitos, 5);
		
        // totiente = (p-1) * (q-1)
		BigInteger totiente = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		
		// this.n = p * q
		this.n = p.multiply(q);	
		
		// Chave pública primeiramente é um numero primo
		this.chavePublica = this.gerador.getPrimoAleatorio(numeroDigitos, 5);
		
		// Caso for igual ou maior que o result[0]
		// E = Encontrar um numero E qualquer, tal que 1 < E < totiente e E é co-primo de totiente
		
		while (gcdExtendido(totiente, this.chavePublica).compareTo(BigInteger.ONE) > 0 && this.chavePublica.compareTo(totiente) < 0) {
            this.chavePublica.add(BigInteger.ONE);
		}
		
		// D = private key, que é o inverso multiplicativo de E mod totiente 
		// D * E = 1 mod totiente
		// Inverso modular de gcdExtendido
		this.chavePrivada = inversoModular(this.chavePublica, totiente);

		// Conjunto Chave Publica = E, N;
		// Conjunto Chave Privada = D, N;
	}	
	

	public BigInteger criptografar(BigInteger message) {
		return potenciaModular(message, this.chavePrivada, this.n);
	}

	public BigInteger descriptografar(BigInteger message){
		return potenciaModular(message, this.chavePublica, this.n);
	}
	
	// Complexidade de O(2^b), sendo b o número de bits de n. 
    public void bruteForce(BigInteger n, BigInteger e, BigInteger privatek, BigInteger text) {
		System.out.println("Algoritmo de força bruta iniciado...");
		System.out.println("Iniciando ataque..." + "\n");
		
        BigInteger x = new BigInteger("2");
        BigInteger y = new BigInteger("2");
        BigInteger d = BigInteger.ONE;
        int steps = 0;

        do {
            while (d.equals(BigInteger.ONE)) {
                ++steps;
                x = polyMod(x, n);
                y = polyMod(polyMod(y, n), n);
                d = x.subtract(y).abs().gcd(n);
            }
            x = x.add(BigInteger.ONE);
        } while (d.equals(n));

        BigInteger q   = n.divide(d);
        BigInteger totiente = d.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger privateKey = inversoModular(e, totiente);
        int nSteps     = steps;
        
        System.out.println("Chave privada descoberta: " + privateKey);
        System.out.println("Passos para descoberta: " + nSteps + "\n");
    }
    
	public void setPublicKey(BigInteger chavePublica){
		this.chavePublica = chavePublica;
	}
	public BigInteger getPublicKey(){
		return this.chavePublica;
	}
	
	public void setPrivateKey(BigInteger chavePrivada){
		this.chavePrivada = chavePrivada;
	}
	public BigInteger getPrivateKey(){
		return this.chavePrivada;
	}
	public void setN(BigInteger modulus){
		this.n = modulus;
	}
	public BigInteger getN(){
		return this.n;
	}

}
