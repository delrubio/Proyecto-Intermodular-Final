package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Spring Boot FACV.
 * <p>
 * {@code @SpringBootApplication} activa el escaneo de componentes, la autoconfiguración
 * de Spring Boot y la configuración de beans declarados en la misma clase o en su paquete.
 * </p>
 */
@SpringBootApplication
public class DemoApplication {

	/**
	 * Arranca el contexto de la aplicación Spring Boot.
	 *
	 * @param args argumentos de línea de comandos (no utilizados)
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
