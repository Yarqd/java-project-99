package hexlet.code.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO для частичного обновления статуса задачи.
 */
public final class TaskStatusUpdateDto {

    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * Проверяет, есть ли данные для обновления.
     *
     * @return true, если хотя бы одно поле обновлено, иначе false
     */
    public boolean hasUpdates() {
        return name != null || slug != null;
    }

    @Override
    public String toString() {
        return "TaskStatusUpdateDto{"
                + "name='" + name + '\''
                + ", slug='" + slug + '\''
                + '}';
    }
}
