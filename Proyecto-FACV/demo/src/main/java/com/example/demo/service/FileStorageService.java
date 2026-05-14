package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileStorageService{
    private final Path rootLocation = Path.of("uploadDir");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
            log.info("FileStorageService - Directorio de almacenamiento inicializado: {}", rootLocation.toAbsolutePath());
        } catch (IOException ioe) {
            log.error("FileStorageService - Error al inicializar almacenamiento: {}", ioe.getMessage(), ioe);
            throw new RuntimeException("Could not initialize storage", ioe);
        }
    }
    /**
     * Almacena un archivo enviado por formulario en el directorio configurado.
     * Genera un nombre único basado en timestamp para evitar colisiones.
     * @param file archivo recibido como MultipartFile
     * @return el nombre con el que se almacenó el archivo
     * @throws RuntimeException en caso de error o archivo vacío
     */
    public String store(MultipartFile file) throws RuntimeException{//cogemos un objeto de tipo MultiPartFile
        if(file.isEmpty()) {
            log.warn("FileStorageService - Intento de guardar archivo vacío");
            throw new RuntimeException ("Empty file");//si no hay file
        }
        String fileOriginal = file.getOriginalFilename();
        String filename = StringUtils.cleanPath(fileOriginal != null ? fileOriginal : "file");//creamos un nombre simple a través del nombre del archivo que crea Spring por su cuenta.

        if(filename.contains("..")) {
            log.warn("FileStorageService - Nombre de archivo inválido: {}", filename);
            throw new RuntimeException("Invalid file");//comprobamos que el nombre es simple
        }
        String extension = StringUtils.getFilenameExtension(filename);//coge la extensión del archivo
        String storedFilename = System.currentTimeMillis() + (extension != null ? "." + extension : ""); //crea el nombre definitivo con el tiempo actual + un punto + la extensión

        try(InputStream inputStream = file.getInputStream()){
            Files.copy(inputStream, this.rootLocation.resolve(storedFilename), StandardCopyOption.REPLACE_EXISTING);//guarda en el directorio el file
            log.info("FileStorageService - Archivo almacenado: {} (original: {})", storedFilename, filename);
            return storedFilename;
        }catch(IOException ioe){
            log.error("FileStorageService - Error al guardar archivo: {}", ioe.getMessage(), ioe);
            throw new RuntimeException("Write error");
        }
    }

    /**
     * Elimina un archivo existente en el almacenamiento.
     * @param filename nombre del archivo a borrar
     * @throws RuntimeException si el archivo no existe o ocurre un error I/O
     */
    public void delete(String filename) throws RuntimeException{
        try{
            Path file = rootLocation.resolve(filename);
            if(!Files.exists(file)) {
                log.warn("FileStorageService - Intento de eliminar archivo inexistente: {}", filename);
                throw new RuntimeException ("File does not exist");
            }
            Files.delete(file);
            log.info("FileStorageService - Archivo eliminado: {}", filename);
        }catch(IOException ioe){
            log.error("FileStorageService - Error al eliminar archivo: {}", ioe.getMessage(), ioe);
            throw new RuntimeException("Delete error");
        }
    }

    /**
     * Carga un archivo del almacenamiento como recurso Spring `Resource`.
     * @param filename nombre del archivo a cargar
     * @return Resource que representa el archivo
     * @throws RuntimeException en caso de error de E/S o si no es legible
     */
    public Resource loadAsResource(String filename) throws RuntimeException{
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                log.info("FileStorageService - Cargando recurso: {}", filename);
                return resource;
            }
            else {
                log.warn("FileStorageService - Recurso no existe o no es legible: {}", filename);
                throw new RuntimeException("IO Error");
            }
        }catch(Exception e){
            log.error("FileStorageService - Error al cargar recurso: {}", e.getMessage(), e);
            throw new RuntimeException("IO Error");
        }
    }
}
