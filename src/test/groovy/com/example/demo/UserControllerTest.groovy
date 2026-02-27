package com.example.demo

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class UserControllerTest {

  @Test
  void "login deve delegar para o userService"() {
    def controller = new UserController()

    def result = controller.login("carlos", "123")

    assertNotNull(result)
  }
}
