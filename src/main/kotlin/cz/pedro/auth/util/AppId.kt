package cz.pedro.auth.util

import java.util.UUID

enum class AppId(id: UUID, name: String) {

    ADMIN_SERVICE(UUID.fromString("4b1764cf-c3e7-4885-baab-85d53277760c"), "admin-service"),
    INVOICE_APP(UUID.randomUUID(), "invoice-app")
}
