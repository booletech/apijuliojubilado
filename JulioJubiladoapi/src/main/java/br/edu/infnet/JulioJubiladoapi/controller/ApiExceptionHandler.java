package br.edu.infnet.JulioJubiladoapi.controller;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import feign.FeignException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.ClienteInvalidoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.ClienteNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioInvalidoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.LocalidadeNaoEncontradaException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TarefaInvalidaException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TarefaNaoEncontradaException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TicketInvalidoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TicketNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ApiExceptionHandler {

	private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	@ExceptionHandler({
		ClienteNaoEncontradoException.class,
		FuncionarioNaoEncontradoException.class,
		LocalidadeNaoEncontradaException.class,
		TarefaNaoEncontradaException.class,
		TicketNaoEncontradoException.class
	})
	public ResponseEntity<ApiError> handleNotFound(RuntimeException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
	}

	@ExceptionHandler({
		ClienteInvalidoException.class,
		FuncionarioInvalidoException.class,
		TarefaInvalidaException.class,
		TicketInvalidoException.class,
		IllegalArgumentException.class
	})
	public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(
						FieldError::getField,
						FieldError::getDefaultMessage,
						ApiExceptionHandler::keepFirst,
						LinkedHashMap::new
				));
		return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest request) {
		Map<String, String> errors = ex.getConstraintViolations().stream()
				.collect(Collectors.toMap(
						ApiExceptionHandler::violationPath,
						ConstraintViolation::getMessage,
						ApiExceptionHandler::keepFirst,
						LinkedHashMap::new
				));
		return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleMalformedJson(HttpMessageNotReadableException ex,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request body", request, null);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.CONFLICT, "Data integrity violation", request, null);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpServletRequest request) {
		Map<String, String> errors = new LinkedHashMap<>();
		errors.put(ex.getName(), "Invalid value: " + ex.getValue());
		return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request parameter", request, errors);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex,
			HttpServletRequest request) {
		Map<String, String> errors = new LinkedHashMap<>();
		errors.put(ex.getParameterName(), "Parameter is required");
		return buildResponse(HttpStatus.BAD_REQUEST, "Missing request parameter", request, errors);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request) {
		Map<String, String> errors = new LinkedHashMap<>();
		errors.put("method", ex.getMethod());
		return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", request, errors);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiError> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported content type", request, null);
	}

	@ExceptionHandler(HttpMessageNotWritableException.class)
	public ResponseEntity<ApiError> handleNotWritable(HttpMessageNotWritableException ex,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Response serialization error", request, null);
	}

	@ExceptionHandler(FeignException.class)
	public ResponseEntity<ApiError> handleFeign(FeignException ex, HttpServletRequest request) {
		HttpStatus status = ex.status() == -1 ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.BAD_GATEWAY;
		return buildResponse(status, "External service error", request, null);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", request, null);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.FORBIDDEN, "Access denied", request, null);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request, null);
	}

	private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, HttpServletRequest request,
			Map<String, String> fieldErrors) {
		ApiError body = new ApiError(
			OffsetDateTime.now().format(TIMESTAMP_FORMAT),
			status.value(),
			status.getReasonPhrase(),
			message,
			request.getRequestURI(),
			fieldErrors
		);
		return ResponseEntity.status(status).body(body);
	}

	private static String keepFirst(String first, String second) {
		return first;
	}

	private static String violationPath(ConstraintViolation<?> violation) {
		return violation.getPropertyPath().toString();
	}

	public record ApiError(
		String timestamp,
		int status,
		String error,
		String message,
		String path,
		Map<String, String> fieldErrors
	) {}
}
