# Project Spec: Todo API (Contoh)

## Tujuan
Menyediakan REST API untuk mengelola Todo:
- Membuat, menampilkan, memperbarui status, dan menghapus todo.
- Menyediakan pencarian dengan filter status dan pagination.

## Arsitektur
- Spring Boot 3.x, Java 21, Maven
- Layering: controller -> service -> repository
- Persistence: Spring Data JPA + H2 (dev)
- Validasi: Bean Validation (Jakarta Validation)
- Dokumentasi: Springdoc OpenAPI (opsional)

## Endpoint
- POST /api/todos
  - Request:
    {
      "title": "Belajar Spring",
      "description": "Membaca dokumentasi Spring",
      "dueDate": "2025-10-01"
    }
  - Response 201:
    {
      "id": "UUID",
      "title": "...",
      "description": "...",
      "status": "OPEN",
      "dueDate": "2025-10-01T00:00:00Z",
      "createdAt": "2025-09-25T16:50:16Z",
      "updatedAt": "2025-09-25T16:50:16Z"
    }
  - Validasi: title wajib, min 3 char; dueDate opsional.

- GET /api/todos?status=OPEN&page=0&size=10
  - Response 200: Page DTO berisi daftar todo + metadata paging.

- GET /api/todos/{id}
  - 200 jika ada, 404 jika tidak.

- PATCH /api/todos/{id}/status
  - Request:
    { "status": "DONE" }
  - Response 200: todo dengan status diperbarui.
  - Validasi: hanya nilai OPEN, IN_PROGRESS, DONE.

- DELETE /api/todos/{id}
  - 204 jika berhasil, 404 jika tidak.

## Model
- Todo:
  - id: UUID
  - title: string (required, 3..200)
  - description: string (0..2000)
  - status: enum [OPEN, IN_PROGRESS, DONE] (default OPEN)
  - dueDate: Instant (opsional)
  - createdAt: Instant (auto)
  - updatedAt: Instant (auto)

## Error Handling
- Gunakan RFC 7807 (application/problem+json)
- 400 untuk validasi, 404 untuk not found.

## Acceptance Criteria
- Semua endpoint di atas tersedia dan ter-cover minimal test unit/service.
- Validasi sesuai kebutuhan.
- Swagger UI tersedia di /swagger-ui (jika springdoc diaktifkan).
- H2 console aktif hanya di profile dev.