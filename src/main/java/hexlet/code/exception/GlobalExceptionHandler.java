package hexlet.code.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений.
 * Обеспечивает централизованную обработку ошибок, возникающих в приложении.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обрабатывает исключения валидации аргументов метода.
     * Например, ошибки в переданных данных, нарушающие правила валидации.
     *
     * @param ex исключение, вызванное недопустимыми аргументами метода
     * @return объект ResponseEntity с сообщением об ошибке и статусом 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOGGER.error("Validation error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("Validation error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключения времени выполнения (RuntimeException).
     * Используется для обработки ошибок, возникающих во время работы приложения.
     *
     * @param ex исключение времени выполнения
     * @return объект ResponseEntity с сообщением об ошибке и статусом 400 BAD REQUEST
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        LOGGER.error("Runtime exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключения отказа в доступе (AccessDeniedException).
     * Например, когда пользователь пытается выполнить действие, на которое у него нет прав.
     *
     * @param ex исключение отказа в доступе
     * @return объект ResponseEntity с сообщением об ошибке и статусом 403 FORBIDDEN
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        LOGGER.error("Access denied: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("Access denied: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Обрабатывает все остальные исключения, которые не были обработаны ранее.
     * Это защита от непредвиденных ошибок в приложении.
     *
     * @param ex общее исключение
     * @return объект ResponseEntity с сообщением об ошибке и статусом 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        LOGGER.error("Unexpected error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("Unexpected error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
