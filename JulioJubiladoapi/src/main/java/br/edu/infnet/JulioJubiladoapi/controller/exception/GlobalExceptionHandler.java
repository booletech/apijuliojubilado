package br.edu.infnet.JulioJubiladoapi.controller.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioInvalidoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioNaoEncontradoException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	//tratamento para exceção de validação
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}  
	
	
	
	
	
	
        
      //FUNCIONARIO INVALIDO;
         
        @ExceptionHandler(FuncionarioInvalidoException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(FuncionarioInvalidoException ex) {
            Map<String, String> errors = new HashMap<>();
            
            //datahora do erro
            errors.put("Data/Hora", LocalDateTime.now().toString());
            //status (tipo do erro)
            errors.put("Status", HttpStatus.BAD_REQUEST.toString());
            //mensagem de erro
            errors.put("mensagem:", ex.getMessage());
            
            
            
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        
        }
      
        
        
        //FUNCIONARIO NÃO ENCONTRADO
        
        @ExceptionHandler(FuncionarioNaoEncontradoException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(FuncionarioNaoEncontradoException ex) {
            Map<String, String> errors = new HashMap<>();
            
            //datahora do erro
            errors.put("Data/Hora", LocalDateTime.now().toString());
            //status (tipo do erro)
            errors.put("Status", HttpStatus.NOT_FOUND.toString());
            //mensagem de erro
            errors.put("mensagem:", ex.getMessage());
            
            
            
            return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
        }
  
        //tratamento para exceções de argumentos inválidos;
		//IllegalArgumentException
        
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(IllegalArgumentException ex) {
            Map<String, String> errors = new HashMap<>();
            
            //datahora do erro
            errors.put("Data/Hora", LocalDateTime.now().toString());
            //status (tipo do erro)
            errors.put("Status", HttpStatus.BAD_REQUEST.toString());
            //mensagem de erro
            errors.put("mensagem:", ex.getMessage());
            
            
            
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        
        }
        
        
        
        //tratamento para exceções genéricas 
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(Exception ex) {
            Map<String, String> errors = new HashMap<>();
            
            //datahora do erro
            errors.put("Data/Hora", LocalDateTime.now().toString());
            //status (tipo do erro)
            errors.put("Status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            //mensagem de erro
            errors.put("mensagem:", ex.getMessage());
            
            
            
            return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
        
        }
        
}
	
	


