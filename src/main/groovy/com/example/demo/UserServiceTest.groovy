package com.example.demo

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class UserServiceTest {

    @Test
    void "deve retornar UNAUTHORIZED quando senha é nula ou vazia"() {
        def service = new UserService()

        assertEquals("UNAUTHORIZED", service.login("carlos", null))
        assertEquals("UNAUTHORIZED", service.login("carlos", ""))
        assertEquals("UNAUTHORIZED", service.login("carlos", "   "))
    }

    @Test
    void "deve retornar INVALID_USER quando user é nulo ou vazio"() {
        def service = new UserService()

        assertEquals("INVALID_USER", service.login(null, "qualquer"))
        assertEquals("INVALID_USER", service.login("", "qualquer"))
        assertEquals("INVALID_USER", service.login("   ", "qualquer"))
    }

    @Test
    void "deve retornar user em lowercase quando credenciais comuns são válidas"() {
        def service = new UserService()

        assertEquals("carlos", service.login("Carlos", "qualquer"))
        assertEquals("joao", service.login("JOAO", "qualquer"))
    }
}
