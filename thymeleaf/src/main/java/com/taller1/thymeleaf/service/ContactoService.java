package com.taller1.thymeleaf.service;

import org.springframework.stereotype.Service;

import com.taller1.thymeleaf.model.Contacto;
import com.taller1.thymeleaf.repository.ContactoRepository;

@Service
public class ContactoService {

    private final ContactoRepository contactoRepository;

    public ContactoService(ContactoRepository contactoRepository) {
        this.contactoRepository = contactoRepository;
    }

    public Contacto guardar(Contacto contacto) {
        return contactoRepository.save(contacto);
    }
}
