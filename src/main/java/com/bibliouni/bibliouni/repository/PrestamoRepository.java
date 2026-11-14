
package com.bibliouni.bibliouni.repository;

import com.bibliouni.bibliouni.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository
        extends JpaRepository<Prestamo, Long> {
}
