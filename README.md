# RSA-cryptography
RSA algorithm for generating public and private keys, encrypt and decrypt messages, and finally, break the message using the brute force algorithm.

## Parameters for execution

### Generation of public and private keys

--gerarChaves <size> <public_key> <private_key>
  
#### Example

--gerarChaves 5 pub.txt priv.txt

### Message encryption

--criptografar <public_key> <message_file> <encrypted_message>

#### Example

--criptografar priv.txt mensagem.txt criptografado.txt

### Message decryption

--descriptografar <public_key> <encrypted_message> <decrypted_message>

#### Example

--descriptografar pub.txt criptografado.txt decifrado.txt

### Brute Force for key breaking

--bruteForce <public_key> <private_key> <encrypted_message>

#### Example
--bruteForce pub.txt priv.txt criptografado.txt
