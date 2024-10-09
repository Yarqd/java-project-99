package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;

public final class TaskStatusUpdateDto {

//    @NotBlank(message = "Name must not be blank")
    private String name;

//    @NotBlank(message = "Slug must not be blank")
    private String slug;

    /**
     * Возвращает имя статуса задачи.
     * @return имя статуса задачи
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя статуса задачи.
     * @param name имя статуса задачи
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает slug статуса задачи.
     * @return slug статуса задачи
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Устанавливает slug статуса задачи.
     * @param slug slug статуса задачи
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * Проверяет, содержит ли DTO данные для обновления.
     * @return true, если есть хотя бы одно поле для обновления, иначе false
     */
    public boolean hasUpdates() {
        return name != null || slug != null;
    }
}
