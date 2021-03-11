package test.hierarchical.catalogmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import test.hierarchical.catalogmanagement.exception.CannotDeleteCatalogException;
import test.hierarchical.catalogmanagement.exception.CannotMoveCatalogException;
import test.hierarchical.catalogmanagement.exception.CatalogNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = CatalogNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(CatalogNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CannotDeleteCatalogException.class)
    public ResponseEntity<Object> handleCannotDelete(CannotDeleteCatalogException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = CannotMoveCatalogException.class)
    public ResponseEntity<Object> handleCannotMove(CannotMoveCatalogException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }
}
